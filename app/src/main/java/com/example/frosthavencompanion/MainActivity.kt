package com.example.frosthavencompanion


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.frosthavencompanion.MainActivity.Companion.BOTTOM_APP_BAR_HEIGHT
import com.example.frosthavencompanion.data.character.GameCharacter
import com.example.frosthavencompanion.data.character.Hero
import com.example.frosthavencompanion.data.character.Monster
import com.example.frosthavencompanion.ui.theme.FrosthavenCompanionTheme
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.google.accompanist.flowlayout.FlowRow
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        const val BOTTOM_APP_BAR_HEIGHT = 56
    }

    private val characterViewModel: CharacterViewModelPreview by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen(viewModelPreview = characterViewModel, settingsViewModel)
        }
    }
}


@Composable
fun MainScreen(viewModelPreview: CharacterViewModelPreview, settingsViewModel: SettingsViewModel) {
    val showAddCharacterDialog = remember { mutableStateOf(false) }
    val showDeleteCharacterDialog = remember { mutableStateOf(false) }
    val showInitiativeDialog = remember { mutableStateOf(false) }
    val showColorPickerDialog = remember { mutableStateOf(false) }
    val showSettingsDialog = remember { mutableStateOf(false) }

    val currentSelectedGameCharacter = remember { mutableStateOf<GameCharacter?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text("Frosthaven Companion")
            })
        },


        bottomBar = {
            BottomAppBar(
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary,
                modifier = Modifier.height(BOTTOM_APP_BAR_HEIGHT.dp)
            ) {

               OutlinedButton(onClick = { viewModelPreview.setAllNotDone()}) {
                   Text(text = "New Round")
               }
                Spacer(Modifier.weight(1f))

                IconButton(
                    onClick = { showSettingsDialog.value = true }
                ) {
                    Icon((Icons.Default.Settings), contentDescription = "Settings")
                }

                IconButton(
                    onClick = { /* Microphone action */ }
                ) {
                    Icon(
                        painterResource(id = R.drawable.baseline_mic_24),
                        contentDescription = "Microphone"
                    )
                }


                IconButton(
                    onClick = { showAddCharacterDialog.value = true }
                ) {
                    Icon(
                        painterResource(id = R.drawable.baseline_person_add_24),
                        contentDescription = "Add Character"
                    )
                }
            }
        },
    ) { _ ->
        CharacterList(
            characterList = viewModelPreview.characterList.collectAsState().value,
            onAliasAdded = { gameCharacter, newAlias ->
                currentSelectedGameCharacter.value = gameCharacter
                viewModelPreview.addNameAlias(gameCharacter.name, newAlias)
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
            onEditCharacter = {},
            onDeleteCharacter = { selectedCharacter ->
                showDeleteCharacterDialog.value = true
                currentSelectedGameCharacter.value = selectedCharacter
            },
            onChangeColor = { selectedCharacter ->
                currentSelectedGameCharacter.value = selectedCharacter
                showColorPickerDialog.value = true
            },
            itemHeight = settingsViewModel.characterListItemHeight.collectAsState().value
        )


        if (showInitiativeDialog.value && currentSelectedGameCharacter.value != null) {
            InitiativeDialog(
                currentSelectedGameCharacter.value!!,
                { initiative ->
                    viewModelPreview.changeFirstInitiative(
                        currentSelectedGameCharacter.value!!.name,
                        initiative
                    )
                    viewModelPreview.setIsDone(currentSelectedGameCharacter.value!!, true)
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

        if (showAddCharacterDialog.value) {
            AddCharacterDialog(
                { showDialog -> showAddCharacterDialog.value = showDialog },
                viewModelPreview.allTypes
            ) { character ->
                viewModelPreview.addGameCharacter(character)
            }
        }

        if (showDeleteCharacterDialog.value && currentSelectedGameCharacter.value != null) {
            DeleterCharacterDialog(
                currentSelectedGameCharacter.value!!,
                showDialog = { showDeleteCharacterDialog.value = false },
                confirm = { deleteChar -> viewModelPreview.removeGameCharacter(deleteChar) }
            )
        }

        if (showColorPickerDialog.value && currentSelectedGameCharacter.value != null) {
            ColorPickerDialog(
                character = currentSelectedGameCharacter.value!!,
                showDialog = { showDialog -> showColorPickerDialog.value = showDialog },
                confirm = { gameCharacter, color ->
                    viewModelPreview.changeColor(
                        gameCharacter.name,
                        color
                    )
                }
            )
        }

        if (showSettingsDialog.value) {
            SettingsDialog(
                onChangeItemHeight = { settingsViewModel.changeCharacterListItemHeight(it) },
                showDialog = { value -> showSettingsDialog.value = value },
                initialItemHeight = settingsViewModel.characterListItemHeight.collectAsState().value
            )
        }

    }

}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val viewModel = CharacterViewModelPreview()
    val settingsViewModel = SettingsViewModel()
    FrosthavenCompanionTheme {
        MainScreen(viewModelPreview = viewModel, settingsViewModel)
    }
}


@Composable
fun CharacterItem(
    character: GameCharacter,
    itemHeight: Int,
    onCharacterClick: (GameCharacter) -> Unit,
    onIconClick: (GameCharacter) -> Unit,
    onNameChange: (GameCharacter, String) -> Unit,
    onNameAliasAdded: (GameCharacter, String) -> Unit,
    onNameAliasRemoved: (GameCharacter, String) -> Unit,
    onDeleteCharacter: (GameCharacter) -> Unit,
    onChangeColor: (GameCharacter) -> Unit
) {

    val expanded = remember { mutableStateOf(false) }
    val showAddAliasDialog = remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(character.name) }


    // TODO have two separate alias, example removed alias and added alias
    val nameAlias by remember { derivedStateOf { character } }
    val removedAlias = remember { mutableStateOf(character.nameAlias) }
    val characterType = when(character) {
        is Hero -> "(H)"
        is Monster -> "(M)"
    }

        Card(
        modifier = Modifier
            .clickable { onCharacterClick(character) }
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth()
            .alpha(if (character.done) 1f else 0.3f)
            .height(if (!expanded.value) itemHeight.dp else Dp.Unspecified),
        elevation = 2.dp,
        backgroundColor = character.color,
        shape = RoundedCornerShape(corner = CornerSize(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = if (!expanded.value) Alignment.CenterVertically else Alignment.Top
            ) {

                if (!expanded.value) {
                    Text(
                        text = character.name + " " + characterType,
                        style = typography.h5
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

                if (!expanded.value) {
                    Text(
                        text = if(character.firstInitiative == 0) "" else character.firstInitiative.toString(),
                        style = typography.h5,
                        modifier = Modifier.padding(end = 5.dp)

                    )
                    if (character is Hero && character.secondInitiative!! > 0) {
                        Text(
                            text = character.secondInitiative.toString(),
                            style = typography.h5,
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
            if (expanded.value) {
                FlowRow {
                    for (alias in character.nameAlias) {
                        Tag(alias = alias, onRemoveClick = {
                            onNameAliasRemoved(character, alias)
                        })
                    }

                }
                Row {
                    AddTag(addTag = { showAddAliasDialog.value = true })

                    IconButton(onClick = { onChangeColor(character) }) {
                        Icon(
                            painterResource(id = R.drawable.baseline_palette_24),
                            contentDescription = "Change color",
                            modifier = Modifier
                                .width(36.dp)
                                .height(36.dp)
                        )
                    }
                    IconButton(onClick = { onDeleteCharacter(character) }) {
                        Icon(
                            Icons.Default.Delete, contentDescription = "Delete character",
                            modifier = Modifier
                                .width(36.dp)
                                .height(36.dp)
                        )
                    }
                    if (showAddAliasDialog.value) {
                        AddAliasDialog(onAddNameAlias = { aliasRemoved ->
                            onNameAliasAdded(character, aliasRemoved)
                        }, showDialog = { show -> showAddAliasDialog.value = show }
                        )

                    }
                }
            }
        }
    }

}

@Composable
fun CharacterList(
    characterList: List<GameCharacter>,
    itemHeight: Int,
    onAliasAdded: (GameCharacter, String) -> Unit,
    onAliasRemoved: (GameCharacter, String) -> Unit,
    onChangeName: (String, String) -> Unit,
    onChangeInitiative: (GameCharacter) -> Unit,
    onEditCharacter: (GameCharacter) -> Unit,
    onDeleteCharacter: (GameCharacter) -> Unit,
    onChangeColor: (GameCharacter) -> Unit
) {
    LazyColumn(Modifier.padding(bottom = BOTTOM_APP_BAR_HEIGHT.dp)) {
        items(
            items = characterList,
            key = { character -> character.name }
        ) { character ->
            CharacterItem(
                itemHeight = itemHeight,
                character = character,
                onCharacterClick = { onChangeInitiative(it) },
                onIconClick = { onEditCharacter(it) },
                onNameAliasAdded = { gameCharacter, newAlias ->
                    onAliasAdded(
                        gameCharacter,
                        newAlias
                    )
                },
                onNameAliasRemoved = { gameCharacter, removeAlias ->
                    onAliasRemoved(gameCharacter, removeAlias)
                },
                onNameChange = { thisCharacter, newName ->
                    onChangeName(
                        thisCharacter.name,
                        newName
                    )
                },
                onDeleteCharacter = { gameCharacter -> onDeleteCharacter(gameCharacter) }
            ) { gameCharacter -> onChangeColor(gameCharacter) }
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
                    Text("Pick Color", modifier = Modifier.align(Alignment.CenterHorizontally))
                    HsvColorPicker(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .padding(10.dp),
                        controller = controller,
                        onColorChanged = { colorEnvelope: ColorEnvelope ->
                            color = colorEnvelope.color
                        }
                    )
                    AlphaTile(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .align(Alignment.CenterHorizontally),
                        controller = controller,
                        tileOddColor = color
                    )
                }

            },
            confirmButton = {
                TextButton(onClick = {
                    val aliasList =
                        if (aliasTags.isBlank()) mutableListOf() else aliasTags.split(",")
                            .map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()

                    val gameCharacter = when (characterType) {
                        "Hero" -> Hero(
                            characterName,
                            aliasList,
                            color = color
                        )
                        else -> Monster(
                            characterName,
                            aliasList,
                            color
                        )
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
        title = { Text(text = "Change initiative for ${character.name}") },
        text = {
            Column {
                OutlinedTextField(
                    value = newFirstInitiative,
                    onValueChange = {
                        newFirstInitiative = it
                        if (it.length == 2) {
                            val initiative = it.toIntOrNull()
                            if (initiative != null) {
                                showDialog(false)
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

@Composable
fun DeleterCharacterDialog(
    character: GameCharacter,
    showDialog: (Boolean) -> Unit,
    confirm: (GameCharacter) -> Unit,
) {
    Dialog(onDismissRequest = { showDialog(false) }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Delete character ${character.name}?",
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontFamily = FontFamily.Default,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                confirm(character)
                                showDialog(false)
                            },
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier
                                .height(50.dp)
                                .weight(1f),
                            colors = ButtonDefaults.buttonColors(Color.Red)
                        ) {
                            Text(text = "Ok")
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        Button(
                            onClick = {
                                showDialog(false)
                            },
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier
                                .height(50.dp)
                                .weight(1f)
                        ) {
                            Text(text = "Cancel")
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ColorPickerDialog(
    character: GameCharacter,
    showDialog: (Boolean) -> Unit,
    confirm: (GameCharacter, Color) -> Unit,
) {
    val color = remember { mutableStateOf(character.color) }
    val controller = rememberColorPickerController()


    AlertDialog(
        onDismissRequest = { showDialog(false) },
        title = {
            Column {
                Text(
                    text = "Change color",
                    modifier = Modifier.padding(bottom = 1.dp),
                    style = TextStyle(fontSize = 20.sp)
                )
            }
        },
        text = {
            Column {
                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(450.dp)
                        .padding(10.dp),
                    controller = controller,
                    onColorChanged = { colorEnvelope: ColorEnvelope ->
                        color.value = colorEnvelope.color
                    }
                )
                AlphaTile(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .align(Alignment.CenterHorizontally),
                    controller = controller,
                    tileOddColor = color.value
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                showDialog(false)
                confirm(character, color.value)
            }) {
                Text("Confirm")
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
fun SettingsDialog(
    initialItemHeight: Int,
    onChangeItemHeight: (Int) -> Unit,
    showDialog: (Boolean) -> Unit
) {
    var sliderPosition by remember { mutableStateOf(initialItemHeight.toFloat()) }

    AlertDialog(
        onDismissRequest = { showDialog(false) },
        title = { Text(text = "Settings") },
        text = {
            Column {
                Text(text = "Change character height")
                Slider(
                    value = sliderPosition,
                    onValueChange = { value ->
                        sliderPosition = value
                        onChangeItemHeight(sliderPosition.toInt())
                    },
                    valueRange = 60f..200f, // Adjust this to your needs
                    steps = 100 // Adjust this to your needs
                )
                Text(text = "Current height: ${sliderPosition.toInt()}")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                showDialog(false)
            }) {
                Text("Done")
            }
        },
    )
}
