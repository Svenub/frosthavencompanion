package com.example.frosthavencompanion

import androidx.compose.ui.graphics.Color
import com.example.frosthavencompanion.CharacterViewModel
import com.example.frosthavencompanion.data.character.Hero
import com.example.frosthavencompanion.data.character.Monster
import com.example.frosthavencompanion.ui.theme.Purple200
import com.example.frosthavencompanion.ui.theme.Purple700
import com.example.frosthavencompanion.ui.theme.Teal200

class CharacterViewModelPreview : CharacterViewModel() {

    init {
        val hero1 = Hero("DeathWalker", color = Purple200)
        val hero2 = Hero("BoneShaper", color = Purple700)
        val monster1 = Monster("Scout", color = Teal200)
        addGameCharacter(hero1)
        addGameCharacter(hero2)
        addGameCharacter(monster1)

        addNameAlias(hero1.name, "Sven")
        addNameAlias(hero1.name, "Korven")
        addNameAlias(hero1.name, "Bajs mannen")
        addNameAlias(hero1.name, "Sven den store")
        addNameAlias(hero1.name, "Sven den mäktiga")
        addNameAlias(hero2.name, "Simon den höga")
    }

}