package com.example.lab1.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scores")
data class ScoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val playerId: Int,
    val score: Int,
    val difficulty: Int,
    val timestamp: Long = System.currentTimeMillis()
)
