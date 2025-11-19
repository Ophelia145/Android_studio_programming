package com.example.lab1

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.lab1.data.AppDatabase
import com.example.lab1.data.Player

@Composable
fun PlayerSelection(onPlayerSelected: (Player) -> Unit) {
    val db = AppDatabase.getDatabase(LocalContext.current)
    val players by db.playerDao().getAllPlayers().collectAsState(initial = emptyList())

    LazyColumn {
        items(players) { player ->
            Text(
                text = player.fio,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPlayerSelected(player) }
                    .padding(16.dp)
            )
        }
    }
}