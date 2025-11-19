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

    @Query("SELECT * FROM scores WHERE playerId = :playerId LIMIT 1")
    suspend fun getScoreByPlayerId(playerId: Int): ScoreEntity?

    @Query("UPDATE scores SET score = :newScore WHERE id = :scoreId")
    suspend fun updateScore(scoreId: Int, newScore: Int)

    @Query("SELECT * FROM scores ORDER BY score DESC LIMIT 5")
    suspend fun getTopScores(): List<ScoreEntity>


}

