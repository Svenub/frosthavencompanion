package com.example.frosthavencompanion.data.character

import androidx.compose.ui.graphics.Color

sealed class GameCharacter(
    open var name: String,
    open var nameAlias: MutableList<String> = mutableListOf(),
    open var firstInitiative: Int? = 0,
    var done: Boolean = false,
    open var color: Color
)



