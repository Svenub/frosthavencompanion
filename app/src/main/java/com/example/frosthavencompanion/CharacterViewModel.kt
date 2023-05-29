package com.example.frosthavencompanion

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.frosthavencompanion.data.character.GameCharacter
import com.example.frosthavencompanion.data.character.Hero
import com.example.frosthavencompanion.data.character.Monster
import com.example.frosthavencompanion.data.speechRecognition.SpeechRecognitionHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
open class CharacterViewModel @Inject constructor(
    private val speechRecognitionHelper: SpeechRecognitionHelper
) : ViewModel() {


    val resultsFlow: StateFlow<List<String>?> = speechRecognitionHelper.resultsFlow
    val errorFlow: StateFlow<Int?> = speechRecognitionHelper.errorFlow

    private val _characterList = MutableStateFlow<List<GameCharacter>>(emptyList())
    val characterList: StateFlow<List<GameCharacter>> = _characterList

    val allTypes = listOf("Hero", "Monster")

    fun addGameCharacter(gameCharacter: GameCharacter) {
        val updatedList = characterList.value.toMutableList()
        updatedList.add(gameCharacter)
        _characterList.value = updatedList
    }

    fun removeGameCharacter(gameCharacter: GameCharacter) {
        val updatedList = characterList.value.filterNot { it == gameCharacter }
        _characterList.value = updatedList
    }

    fun changeFirstInitiative(name: String, initiative: Int) {
        _characterList.value = characterList.value.map { character ->
            if (character.name == name) {
                when (character) {
                    is Hero -> character.copy(firstInitiative = initiative, done = true)
                    is Monster -> character.copy(firstInitiative = initiative, done = true)
                }
            } else {
                character
            }
        }
        sortCharacterList()
    }

    fun changeSecondInitiative(name: String, initiative: Int) {
        _characterList.value = characterList.value.map { character ->
            if (character.name == name && character is Hero) {
                character.copy(secondInitiative = initiative, done = true)
            } else {
                character
            }
        }
        sortCharacterList()
    }

    fun changeName(searchName: String, newName: String) {
        _characterList.value = characterList.value.map { character ->
            if (character.name == searchName) {
                when (character) {
                    is Hero -> character.copy(name = newName)
                    is Monster -> character.copy(name = newName)
                }
            } else {
                character
            }
        }
    }

    fun addNameAlias(searchName: String, newAlias: String) {
        _characterList.value = characterList.value.map { character ->
            if (character.name == searchName) {
                when (character) {
                    is Hero -> character.copy(nameAlias = character.nameAlias + newAlias)
                    is Monster -> character.copy(nameAlias = character.nameAlias + newAlias)
                }
            } else {
                character
            }
        }
    }

    fun removeNameAlias(searchName: String, removeAlias: String) {
        _characterList.value = characterList.value.map { character ->
            if (character.name == searchName) {
                when (character) {
                    is Hero -> character.copy(nameAlias = character.nameAlias.filterNot { it == removeAlias })
                    is Monster -> character.copy(nameAlias = character.nameAlias.filterNot { it == removeAlias })

                }
            } else {
                character
            }
        }
    }

    fun changeColor(searchName: String, color: Color) {
        _characterList.value = characterList.value.map { character ->
            if (character.name == searchName) {
                when (character) {
                    is Hero -> character.copy(color = color)
                    is Monster -> character.copy(color = color)
                }
            } else {
                character
            }
        }
    }

    private fun sortCharacterList() {
        _characterList.value = characterList.value.sortedWith(
            compareBy(
                { it.firstInitiative },
                { if (it is Hero) 0 else 1 },
                { if (it is Hero) it.secondInitiative else null }
            )
        )
    }

    fun setIsDone(gameCharacter: GameCharacter, done: Boolean) {
        _characterList.value = characterList.value.map { character ->
            if (character == gameCharacter) {
                when (character) {
                    is Hero -> character.copy(done = done)
                    is Monster -> character.copy(done = done)
                }
            } else {
                character
            }
        }
    }

    fun setAllNotDone() {
        _characterList.value = characterList.value.map { character ->
            when (character) {
                is Hero -> character.copy(done = false)
                is Monster -> character.copy(done = false)
            }
        }
    }

    private fun startSpeechRecognition() {
        speechRecognitionHelper.startListening()
    }

    fun stopSpeechRecognition() {
        speechRecognitionHelper.stopListening()
    }
}
