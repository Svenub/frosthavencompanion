package com.example.frosthavencompanion.data.character

import androidx.compose.ui.graphics.Color

data class Monster(
    override var name: String,
    override var nameAlias: MutableList<String> = mutableListOf(),
    override var color: Color
) : GameCharacter(name, nameAlias, color = color)