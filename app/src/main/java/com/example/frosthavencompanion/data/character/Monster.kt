package com.example.frosthavencompanion.data.character

import androidx.compose.ui.graphics.Color

data class Monster(
    override val name: String,
    override val nameAlias: List<String> = emptyList(),
    override val color: Color,
    override val firstInitiative: Int? = 0,
    override val done: Boolean = true
) : GameCharacter(name, nameAlias, firstInitiative, color = color)