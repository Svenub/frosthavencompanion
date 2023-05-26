package com.example.frosthavencompanion.data.character

import androidx.compose.ui.graphics.Color


data class Hero(
    override var name: String,
    override var nameAlias: MutableList<String> = mutableListOf(),
    var secondInitiative: Int? = 0,
    override var color: Color
) : GameCharacter(name, nameAlias, 0, false, color)