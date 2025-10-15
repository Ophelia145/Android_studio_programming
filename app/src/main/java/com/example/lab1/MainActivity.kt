package com.example.lab1

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.compose.material3.*

data class Player(
    val name: String,
    val gender: String,
    val course: String,
    val difficulty: Int,
    val birthDate: String,
    val zodiac: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlayerForm()
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerForm() {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    val savedName = sharedPreferences.getString("name", "") ?: ""
    val savedGender = sharedPreferences.getString("gender", "") ?: ""
    val savedCourse = sharedPreferences.getString("course", "") ?: ""
    val savedDifficulty = sharedPreferences.getInt("difficulty", 1)
    val savedBirthDate = sharedPreferences.getString("birthDate", "") ?: ""

    var name by remember { mutableStateOf(savedName) }
    var gender by remember { mutableStateOf(savedGender) }
    var course by remember { mutableStateOf(savedCourse) }
    var difficulty by remember { mutableStateOf(savedDifficulty.toFloat()) } // для Slider
    var birthDate by remember { mutableStateOf(savedBirthDate) }

    val courseOptions = listOf("1", "2", "3", "4", "5", "6")
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFacbfba))
            .padding(16.dp)
    ) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Hey, who r u?") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Gender:")
        Row {
            RadioButton(
                selected = gender == "male",
                onClick = { gender = "male" }
            )
            Text("male", modifier = Modifier.padding(end = 16.dp))

            RadioButton(
                selected = gender == "female",
                onClick = { gender = "female" }
            )
            Text("female")
        }

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = course,
                onValueChange = {},
                readOnly = true,
                label = { Text("Курс") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                courseOptions.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            course = item
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Уровень сложности (Slider)
        Text(text = "Difficulty: ${difficulty.toInt()}")
        Slider(
            value = difficulty,
            onValueChange = { difficulty = it },
            valueRange = 1f..10f,
            steps = 8
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Дата рождения
        val calendar = java.util.Calendar.getInstance()
        val datePickerDialog = android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                birthDate = "$dayOfMonth/${month + 1}/$year"
            },
            calendar.get(java.util.Calendar.YEAR),
            calendar.get(java.util.Calendar.MONTH),
            calendar.get(java.util.Calendar.DAY_OF_MONTH)
        )

        Button(onClick = { datePickerDialog.show() }) {
            Text(text = if (birthDate.isEmpty()) "Select Birth Date" else birthDate)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Сохранение всех данных
        Button(
            onClick = {
                sharedPreferences.edit {
                    putString("name", name)
                    putString("gender", gender)
                    putString("course", course)
                    putInt("difficulty", difficulty.toInt())
                    putString("birthDate", birthDate)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("SAVE")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Name: $name\nGender: $gender\nCourse: $course\nDifficulty: ${difficulty.toInt()}\nBirth Date: $birthDate"
        )
    }
}
