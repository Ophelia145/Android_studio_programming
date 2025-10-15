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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp


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
            GameTabs()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameTabs() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Profile", "Rules", "Author", "Settings")

    Column {
        TabRow(selectedTabIndex = selectedTab) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTab) {
            0 -> PlayerForm()
            1 -> RulesTab()
            2 -> AuthorsTab()
            3 -> SettingsTab()
        }
    }
}



@Composable
fun RulesTab() {
    val rulesText = stringResource(id = R.string.rules)
    Box(modifier = Modifier.padding(16.dp)) {
        Text(text = rulesText, fontSize = 16.sp)

    }
}

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

@Composable
fun SettingsTab() {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("GameSettings", Context.MODE_PRIVATE)

    val savedSpeed = prefs.getString("speed", "5") ?: "5"
    val savedMaxBugs = prefs.getString("max_bugs", "10") ?: "10"
    val savedBonusInterval = prefs.getString("bonus_interval", "3") ?: "3"
    val savedRoundDuration = prefs.getString("round_duration", "60") ?: "60"

    var speed by remember { mutableStateOf(savedSpeed) }
    var maxBugs by remember { mutableStateOf(savedMaxBugs) }
    var bonusInterval by remember { mutableStateOf(savedBonusInterval) }
    var roundDuration by remember { mutableStateOf(savedRoundDuration) }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFacbfba))
        .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Game Settings", style = MaterialTheme.typography.titleMedium)

        NumberTextField("Game Speed", speed) { speed = it }
        NumberTextField("Max Bugs on Screen", maxBugs) { maxBugs = it }
        NumberTextField("Bonus Interval (seconds)", bonusInterval) { bonusInterval = it }
        NumberTextField("Round Duration (seconds)", roundDuration) { roundDuration = it }

        Button(
            onClick = {
                prefs.edit {
                    putString("speed", speed)
                    putString("max_bugs", maxBugs)
                    putString("bonus_interval", bonusInterval)
                    putString("round_duration", roundDuration)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("SAVE SETTINGS")
        }

        Text(
            text = "Current Settings:\nSpeed: $speed\nMax Bugs: $maxBugs\nBonus Interval: $bonusInterval\nRound Duration: $roundDuration",
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}


@Composable
fun NumberTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = { newText ->
            val filtered = newText.filter { it.isDigit() }
            onValueChange(filtered)
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
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

        Text(text = "Difficulty: ${difficulty.toInt()}")
        Slider(value = difficulty, onValueChange = { difficulty = it }, valueRange = 1f..10f, steps = 8)

        Spacer(modifier = Modifier.height(16.dp))

        val calendar = Calendar.getInstance()
        val datePickerDialog = android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                birthDate = "$dayOfMonth/${month + 1}/$year"
                zodiac = calculateZodiac(dayOfMonth, month + 1)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        val maxCalendar = Calendar.getInstance().apply {
            set(2020, Calendar.DECEMBER, 31)
        }
        datePickerDialog.datePicker.maxDate = maxCalendar.timeInMillis
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