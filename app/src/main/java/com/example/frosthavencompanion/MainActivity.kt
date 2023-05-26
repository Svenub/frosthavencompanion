package com.example.frosthavencompanion


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.frosthavencompanion.data.character.GameCharacter
import com.example.frosthavencompanion.data.character.Hero
import com.example.frosthavencompanion.data.character.Monster
import com.example.frosthavencompanion.ui.theme.FrosthavenCompanionTheme
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.PaletteContentScale
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.google.accompanist.flowlayout.FlowRow

class MainActivity : ComponentActivity() {


    private val characterViewModel: CharacterViewModelPreview by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen(viewModelPreview = characterViewModel)
        }
    }
}


@Composable
fun MainScreen(viewModelPreview: CharacterViewModelPreview) {
    val showAddCharacterDialog = remember { mutableStateOf(false) }
    val showAddAliasDialog = remember { mutableStateOf(false) }
    val showInitiativeDialog = remember { mutableStateOf(false) }

    val currentSelectedGameCharacter = remember { mutableStateOf<GameCharacter?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddCharacterDialog.value = true }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { _ ->
        CharacterList(
            characterList = viewModelPreview.characterList.collectAsState().value,
            onAliasAdded = { gameCharacter ->
                currentSelectedGameCharacter.value = gameCharacter
                showAddAliasDialog.value = true
            },
            onAliasRemoved = { gameCharacter, aliasRemoved ->
                viewModelPreview.removeNameAlias(gameCharacter.name, aliasRemoved)
            },
            onChangeName = { characterName, newName ->
                viewModelPreview.changeName(characterName, newName)
            },
            onChangeInitiative = { gameCharacter ->
                currentSelectedGameCharacter.value = gameCharacter
                showInitiativeDialog.value = true
            },
            onEditCharacter = {}
        )


        if (showInitiativeDialog.value && currentSelectedGameCharacter.value != null) {
            InitiativeDialog(
                currentSelectedGameCharacter.value!!,
                { initiative ->
                    viewModelPreview.changeFirstInitiative(
                        currentSelectedGameCharacter.value!!.name,
                        initiative
                    )
                },
                { initiative ->
                    viewModelPreview.changeSecondInitiative(
                        currentSelectedGameCharacter.value!!.name,
                        initiative
                    )
                },
                { showDialog -> showInitiativeDialog.value = showDialog }
            )
        }

/*
        if (showAddAliasDialog.value && currentSelectedGameCharacter.value != null) {
            AddAliasDialog(
                { newAlias ->
                    viewModelPreview.addNameAlias(
                        currentSelectedGameCharacter.value!!.name,
                        newAlias
                    )
                },
                { showDialog -> showAddAliasDialog.value = showDialog }
            )

        }



 */

        if (showAddCharacterDialog.value) {
            AddCharacterDialog(
                { showDialog -> showAddCharacterDialog.value = showDialog },
                viewModelPreview.allTypes
            ) { character ->
                viewModelPreview.addGameCharacter(character)
            }
        }


    }

}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val viewModel = CharacterViewModelPreview()
    FrosthavenCompanionTheme {
        MainScreen(viewModelPreview = viewModel)
    }
}


@Composable
fun CharacterItem(
    character: GameCharacter,
    onCharacterClick: (GameCharacter) -> Unit,
    onIconClick: (GameCharacter) -> Unit,
    onNameChange: (GameCharacter, String) -> Unit,
    onNameAliasAdded: (GameCharacter) -> Unit,
    onNameAliasRemoved: (GameCharacter, String) -> Unit
) {

    val expanded = remember { mutableStateOf(false) }
    val showAddAliasDialog = remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(character.name) }


    // TODO have two separate alias, example removed alias and added alias
    var nameAlias by remember { mutableStateOf(character.nameAlias.toMutableList()) }
    val removedAlias = remember { mutableStateOf(character.nameAlias.toMutableList()) }


    Card(
        modifier = Modifier
            .clickable { onCharacterClick(character) }
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth(),
        elevation = 2.dp,
        backgroundColor = character.color,
        shape = RoundedCornerShape(corner = CornerSize(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = if (!expanded.value) Alignment.CenterVertically else Alignment.Top
            ) {
                if (!expanded.value) {
                    Text(
                        text = character.name,
                        style = typography.h6
                    )
                } else {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { updatedName ->
                            newName = updatedName
                        },
                        label = { Text("Character name") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!expanded.value) {
                        Text(
                            text = character.firstInitiative.toString(),
                            style = typography.h4,
                            modifier = Modifier.padding(15.dp)
                        )
                        if (character is Hero && character.secondInitiative!! > 0) {
                            Text(
                                text = character.secondInitiative.toString(),
                                style = typography.h4
                            )
                        }
                        IconButton(onClick = {
                            expanded.value = !expanded.value
                            onIconClick(character)
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    } else {
                        IconButton(onClick = {
                            onNameChange(character, newName)
                            expanded.value = false
                        }) {
                            Icon(
                                painterResource(id = R.drawable.baseline_save_24),
                                contentDescription = "Save",
                                modifier = Modifier
                                    .width(36.dp)
                                    .height(36.dp)
                            )
                        }
                    }
                }
            }
            if (expanded.value) {
                FlowRow {
                    for (alias in nameAlias) {
                        Tag(alias = alias, onRemoveClick = {
                            onNameAliasRemoved(character, alias)
                            nameAlias = character.nameAlias.toMutableList()
                        })
                    }
                    AddTag(addTag = { showAddAliasDialog.value = true })
                }
            }

            if (showAddAliasDialog.value) {
                AddAliasDialog(onAddNameAlias = { aliasRemoved ->
                    onNameAliasAdded(character)
                    nameAlias = character.nameAlias.toMutableList()

                }
                , showDialog = {show -> showAddAliasDialog.value = show}
                )

            }


        }
    }

}


@Composable
fun CharacterList(
    characterList: List<GameCharacter>,
    onAliasAdded: (GameCharacter) -> Unit,
    onAliasRemoved: (GameCharacter, String) -> Unit,
    onChangeName: (String, String) -> Unit,
    onChangeInitiative: (GameCharacter) -> Unit,
    onEditCharacter: (GameCharacter) -> Unit
) {
    LazyColumn {
        itemsIndexed(
            items = characterList
        )
        { index, character ->
            CharacterItem(
                character = character,
                onCharacterClick = { onChangeInitiative(it) },
                onIconClick = { onEditCharacter(it) },
                onNameAliasAdded = { gameCharacter -> onAliasAdded(gameCharacter) },
                onNameAliasRemoved = { gameCharacter, removeAlias ->
                    onAliasRemoved(gameCharacter, removeAlias)
                },
                onNameChange = { thisCharacter, newName ->
                    onChangeName(
                        thisCharacter.name,
                        newName
                    )
                }
            )
        }
    }
}


@Composable
fun AddCharacterDialog(
    showDialog: (Boolean) -> Unit,
    characterTypes: List<String>,
    onCharacterAdd: (GameCharacter) -> Unit
) {
    var characterName by remember { mutableStateOf("") }
    var characterType by remember { mutableStateOf(characterTypes[0]) } // default value
    var aliasTags by remember { mutableStateOf("") }
    var color by remember { mutableStateOf(Color.Black) }
    val showCharacterDialog = remember { mutableStateOf(true) }
    val showColorPicker = remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val controller = rememberColorPickerController()

    // Dialog for adding a new character
    if (showCharacterDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog(false) },
            title = {
                Column {
                    Text(
                        text = "Add new character",
                        modifier = Modifier.padding(bottom = 16.dp),
                        style = TextStyle(fontSize = 20.sp)
                    )
                }
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = characterName,
                        onValueChange = { characterName = it },
                        label = { Text("Character name") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    Text(
                        "Select character type",
                        modifier = Modifier.padding(bottom = 4.dp),
                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    )
                    Box {
                        Text(
                            text = characterType,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = true }
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }) {
                            characterTypes.forEach { type ->
                                DropdownMenuItem(onClick = {
                                    characterType = type
                                    expanded = false
                                }) {
                                    Text(type)
                                }
                            }
                        }
                    }
                    OutlinedTextField(
                        value = aliasTags,
                        onValueChange = { aliasTags = it },
                        label = { Text("Alias Tags") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp)
                    )
                    TextButton(onClick = {
                        showColorPicker.value = true
                        showCharacterDialog.value = false

                    }) {
                        Text("Pick Color")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val gameCharacter = when (characterType) {
                        "Hero" -> Hero(
                            characterName,
                            aliasTags.split(",").toMutableList(),
                            color = color
                        )
                        else -> Monster(characterName, aliasTags.split(",").toMutableList(), color)
                    }
                    onCharacterAdd(gameCharacter)
                    showDialog(false)
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog(false) }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dialog for picking color
    if (showColorPicker.value) {
        AlertDialog(onDismissRequest = { showColorPicker.value = false },
            text = {
                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(450.dp)
                        .padding(10.dp),
                    controller = controller,
                    onColorChanged = { colorEnvelope: ColorEnvelope ->
                        color = colorEnvelope.color
                        showColorPicker.value = false
                        showCharacterDialog.value = true
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                }) {
                    Text("Pick Color")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog(false) }) {
                    Text("Cancel")
                }
            })


    }
}


@Composable
fun Tag(alias: String, onRemoveClick: (String) -> Unit) {
    Surface(
        color = MaterialTheme.colors.primary,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = alias,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
            IconButton(onClick = { onRemoveClick(alias) }) {
                Icon(Icons.Default.Close, contentDescription = "Remove")
            }
        }
    }
}

@Composable
fun AddTag(addTag: () -> Unit) {
    Surface(
        color = MaterialTheme.colors.onPrimary,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(4.dp)
    ) {
        IconButton(onClick = addTag) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }

    }
}

@Composable
fun AddAliasDialog(
    onAddNameAlias: (String) -> Unit,
    showDialog: (Boolean) -> Unit,
) {

    var newNameAlias by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { showDialog(false) },
        title = { Text(text = "Add name alias") },
        text = {
            Column {
                OutlinedTextField(
                    value = newNameAlias,
                    onValueChange = {
                        newNameAlias = it
                    },
                    label = { Text("Alias name") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (newNameAlias.any { it.isDigit() } || newNameAlias == "") {
                    error = true
                } else {
                    onAddNameAlias(newNameAlias.trim())
                    showDialog(false)
                }
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = { showDialog(false) }) {
                Text("Cancel")
            }
        }
    )

}

@Composable
fun InitiativeDialog(
    character: GameCharacter,
    onFirstInitiativeChanged: (Int) -> Unit,
    onSecondInitiativeChanged: (Int) -> Unit,
    showDialog: (Boolean) -> Unit
) {
    var newFirstInitiative by remember { mutableStateOf(character.firstInitiative.toString()) }
    var newSecondInitiative by remember { mutableStateOf(if (character is Hero) character.secondInitiative.toString() else "") }

    AlertDialog(
        onDismissRequest = { showDialog(false) },
        title = { Text(text = "Change Initiative") },
        text = {
            Column {
                OutlinedTextField(
                    value = newFirstInitiative,
                    onValueChange = {
                        newFirstInitiative = it
                        if (it.length == 2) {
                            val initiative = it.toIntOrNull()
                            if (initiative != null) {
                                onFirstInitiativeChanged(initiative)
                            }
                        }
                    },
                    label = { Text("First initiative") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                newFirstInitiative = "" // Clear TextField when it gets focus
                            }
                        }
                )
                if (character is Hero) {
                    OutlinedTextField(
                        value = newSecondInitiative,
                        onValueChange = {
                            newSecondInitiative = it
                            if (it.length == 2) {
                                val initiative = it.toIntOrNull()
                                if (initiative != null) {
                                    onSecondInitiativeChanged(initiative)
                                    showDialog(false)
                                }
                            }
                        },
                        label = { Text("Second initiative") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    newSecondInitiative =
                                        "" // Clear TextField when it gets focus
                                }
                            }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val firstInitiative = newFirstInitiative.toIntOrNull()
                val secondInitiative = newSecondInitiative.toIntOrNull()
                if (firstInitiative != null) {
                    onFirstInitiativeChanged(firstInitiative)
                }
                if (secondInitiative != null) {
                    onSecondInitiativeChanged(secondInitiative)
                }
                showDialog(false)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = { showDialog(false) }) {
                Text("Cancel")
            }
        }
    )
}



