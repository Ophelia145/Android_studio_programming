package com.example.lab1

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lab1.data.AppDatabase
import com.example.lab1.data.Score
@Composable
fun RecordsTab() {
    val db = AppDatabase.getDatabase(LocalContext.current)
    val allScores by db.playerDao().getTopTenScores().collectAsState(initial = emptyList())

    val uniqueScores = remember(allScores) {
        allScores
            .groupBy { it.playerId }
            .map { (_, scores) -> scores.maxByOrNull { it.score } }
            .filterNotNull()
            .sortedByDescending { it.score }
            .take(10)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Таблица рекордов",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(uniqueScores) { score ->
                RecordItem(score = score)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordItem(score: Score) {
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val player by produceState<String?>(initialValue = null, score.playerId) {
        value = db.playerDao().getPlayerById(score.playerId)?.fio
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        ListItem(
            headlineContent = { Text(text = player ?: "Загрузка...") },
            supportingContent = { Text("Сложность: ${score.difficulty}") },
            trailingContent = {
                Text(
                    text = "${score.score}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        )
    }
}
