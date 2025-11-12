package com.example.lab1.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val gender: String,
    val course: String,
    val difficulty: Int,
    val birthDate: String,
    val zodiac: String
)
