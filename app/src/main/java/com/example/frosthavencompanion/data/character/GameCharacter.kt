package com.example.frosthavencompanion.data.character

import androidx.compose.ui.graphics.Color

sealed class GameCharacter(
    open val name: String,
    open val nameAlias: List<String> = emptyList(),
    open val firstInitiative: Int? = 0,
    open val done: Boolean = true,
    open val color: Color
)



