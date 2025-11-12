package com.example.lab1.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface ScoreDao {
    @Insert
    suspend fun insertScore(score: ScoreEntity)

    @Query("SELECT * FROM scores ORDER BY score DESC LIMIT 50")
    suspend fun getAllScores(): List<ScoreEntity>
}