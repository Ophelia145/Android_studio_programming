package com.example.lab1

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import java.util.Calendar

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
    val savedZodiac = sharedPreferences.getString("zodiac", "") ?: ""

    var name by remember { mutableStateOf(savedName) }
    var gender by remember { mutableStateOf(savedGender) }
    var course by remember { mutableStateOf(savedCourse) }
    var difficulty by remember { mutableStateOf(savedDifficulty.toFloat()) }
    var birthDate by remember { mutableStateOf(savedBirthDate) }
    var zodiac by remember { mutableStateOf(savedZodiac) }

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
            RadioButton(selected = gender == "male", onClick = { gender = "male" })
            Text("male", modifier = Modifier.padding(end = 16.dp))
            RadioButton(selected = gender == "female", onClick = { gender = "female" })
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

        // Уровень сложности
        Text(text = "Difficulty: ${difficulty.toInt()}")
        Slider(value = difficulty, onValueChange = { difficulty = it }, valueRange = 1f..10f, steps = 8)

        Spacer(modifier = Modifier.height(16.dp))

        val calendar = Calendar.getInstance()
        val datePickerDialog = android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                birthDate = "$dayOfMonth/${month + 1}/$year"
                zodiac = calculateZodiac(dayOfMonth, month + 1) // Обновляем знак зодиака
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        Button(onClick = { datePickerDialog.show() }) {
            Text(text = if (birthDate.isEmpty()) "Select Birth Date" else birthDate)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                sharedPreferences.edit {
                    putString("name", name)
                    putString("gender", gender)
                    putString("course", course)
                    putInt("difficulty", difficulty.toInt())
                    putString("birthDate", birthDate)
                    putString("zodiac", zodiac)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("SAVE")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Name: $name\nGender: $gender\nCourse: $course\nDifficulty: ${difficulty.toInt()}\nBirth Date: $birthDate\nZodiac: $zodiac"
        )

        if (zodiac.isNotEmpty()) {
            val zodiacDrawable = when (zodiac.lowercase()) {
                "aries" -> R.drawable.aries
                "taurus" -> R.drawable.taurus
                "gemini" -> R.drawable.gemini
                "cancer" -> R.drawable.cancer
                "leo" -> R.drawable.leo
                "virgo" -> R.drawable.virgo
                "libra" -> R.drawable.libra
                "scorpio" -> R.drawable.scorpio
                "sagittarius" -> R.drawable.sagittarius
                "capricorn" -> R.drawable.capricorn
                "aquarius" -> R.drawable.aquarius
                "pisces" -> R.drawable.pisces
                else -> 0
            }
            if (zodiacDrawable != 0) {
                Image(
                    painter = painterResource(id = zodiacDrawable),
                    contentDescription = zodiac,
                    modifier = Modifier.size(80.dp)
                )
            }
        }
    }
}


fun calculateZodiac(day: Int, month: Int): String {
    return when (month) {
        1 -> if (day <= 19) "Capricorn" else "Aquarius"
        2 -> if (day <= 18) "Aquarius" else "Pisces"
        3 -> if (day <= 20) "Pisces" else "Aries"
        4 -> if (day <= 19) "Aries" else "Taurus"
        5 -> if (day <= 20) "Taurus" else "Gemini"
        6 -> if (day <= 20) "Gemini" else "Cancer"
        7 -> if (day <= 22) "Cancer" else "Leo"
        8 -> if (day <= 22) "Leo" else "Virgo"
        9 -> if (day <= 22) "Virgo" else "Libra"
        10 -> if (day <= 22) "Libra" else "Scorpio"
        11 -> if (day <= 21) "Scorpio" else "Sagittarius"
        12 -> if (day <= 21) "Sagittarius" else "Capricorn"
        else -> ""
    }
}