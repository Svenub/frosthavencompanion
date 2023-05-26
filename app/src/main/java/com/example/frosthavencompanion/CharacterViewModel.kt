package com.example.frosthavencompanion

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.frosthavencompanion.data.character.GameCharacter
import com.example.frosthavencompanion.data.character.Hero
import com.example.frosthavencompanion.data.character.Monster
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

open class CharacterViewModel : ViewModel() {

    private val _characterList = MutableStateFlow<List<GameCharacter>>(emptyList())
    val characterList: StateFlow<List<GameCharacter>> = _characterList
    private val _internalCharacterList: MutableList<GameCharacter> = mutableListOf()

    val allTypes = listOf("Hero", "Monster")


    fun addGameCharacter(gameCharacter: GameCharacter) {
        when (gameCharacter) {
            is Hero -> addHero(gameCharacter.name, gameCharacter.nameAlias, gameCharacter.color)
            is Monster -> addMonster(
                gameCharacter.name,
                gameCharacter.nameAlias,
                gameCharacter.color
            )
        }


    }

    fun removeGameCharacter(gameCharacter: GameCharacter) {
        _internalCharacterList.removeIf { it == gameCharacter }
        _characterList.value = _internalCharacterList.toList()
    }

    private fun addHero(name: String, alias: MutableList<String> = mutableListOf(), color: Color) {
        val newHero = Hero(name, alias, color = color)
        _internalCharacterList.add(newHero)
        _characterList.value = _internalCharacterList.toList()  // emit a copy of the updated list
    }


    private fun addMonster(name: String, alias: MutableList<String> = mutableListOf(), color: Color
    ) {

        _internalCharacterList.add(Monster(name, alias, color = color))
        _characterList.value = _internalCharacterList.toList()
    }

    fun changeFirstInitiative(name: String, initiative: Int) {
        _internalCharacterList.find { it.name == name }?.firstInitiative = initiative
        _characterList.value = _internalCharacterList.toList()
    }

    fun changeSecondInitiative(name: String, initiative: Int) {
        val char = _internalCharacterList.find { it.name == name }
        if (char is Hero) {
            char.secondInitiative = initiative
        }
        _characterList.value = _internalCharacterList.toList()
    }

    fun changeName(searchName: String, newName: String) {
        _internalCharacterList.find { it.name == searchName }?.name = newName
        _characterList.value = _internalCharacterList.toList()
    }

    fun addNameAlias(searchName: String, newAlias: String) {
        Log.e("addNameAlias", "searchName: $searchName, newAlias: $newAlias")
        _internalCharacterList.find { it.name == searchName }?.nameAlias?.add(newAlias)
        _characterList.value = _internalCharacterList.toList()
    }

    fun removeNameAlias(searchName: String, removeAlias: String) {
        Log.e("removeNameAlias", "searchName: $searchName, removeAlias: $removeAlias")

        _internalCharacterList.find { it.name == searchName }?.nameAlias?.removeIf { it == removeAlias}
        _characterList.value = _internalCharacterList.toList()
    }


}