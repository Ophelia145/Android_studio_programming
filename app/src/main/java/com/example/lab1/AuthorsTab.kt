package com.example.lab1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp

@Composable
fun AuthorsTab() {
    val authors = listOf(
        Author("Алиса Хайбулаева", R.drawable.me),

        )
    LazyColumn(modifier = Modifier.padding(16.dp)
        .background(Color(0xFFacbfba))) {
        items(authors) { author ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
                Image(
                    painter = painterResource(id = author.photoRes),
                    contentDescription = author.name,
                    modifier = Modifier.size(170.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(author.name, fontSize = 18.sp, color = Color.White,)
            }
        }
    }
}

data class Author(val name: String, val photoRes: Int)
