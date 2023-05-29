package com.example.frosthavencompanion

import androidx.lifecycle.ViewModel
import com.example.frosthavencompanion.data.character.GameCharacter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel : ViewModel() {

    companion object {
        const val DEFAULT_CHARACTER_LIST_ITEM_HEIGHT = 60
    }

    private val _characterListItemHeight = MutableStateFlow(DEFAULT_CHARACTER_LIST_ITEM_HEIGHT)
    val characterListItemHeight: StateFlow<Int> = _characterListItemHeight

    fun changeCharacterListItemHeight(value: Int) {
        _characterListItemHeight.value = value
    }
}