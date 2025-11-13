package com.example.lab1
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.lab1.room.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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


        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val playerName = sharedPrefs.getString("name", null)

        // Если профиля нет — сразу показываем вкладку Profile
        val startTabIndex = if (playerName.isNullOrEmpty()) 1 else 0

        setContent {
            GameTabs(startTab = startTabIndex)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameTabs(startTab: Int = 0) {
    var selectedTab by remember { mutableStateOf(startTab) }
    val tabTitles = listOf("Game", "Profile", "Rules", "Author", "Settings", "Records")

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
            0 -> BugsGame()
            1 -> PlayerForm()
            2 -> RulesTab()
            3 -> AuthorsTab()
            4 -> SettingsTab()
            5 -> RecordsTab()
        }
    }
}


@Composable
fun RecordsTab() {
    val context = LocalContext.current
    val db = remember { GameDatabase.getDatabase(context) }
    var scores by remember { mutableStateOf<List<ScoreEntity>>(emptyList()) }
    LaunchedEffect(Unit) {
        try {
            withContext(Dispatchers.IO) {
                scores = db.scoreDao().getAllScores()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFacbfba))
            .padding(16.dp)
    ) {
        item {
            Text(" Таблица рекордов", fontSize = 20.sp, color = Color.White)
        }

        items(scores) { score ->
            var player by remember { mutableStateOf<PlayerEntity?>(null) }

            LaunchedEffect(score.playerId) {
                try {
                    withContext(Dispatchers.IO) {
                        player = db.playerDao().getPlayerById(score.playerId)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF7f8c8d))
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("Имя: ${player?.name ?: "Неизвестно"}", color = Color.White)
                    Text("Очки: ${score.score}", color = Color.White)
                    Text("Сложность: ${score.difficulty}", color = Color.White)
                    Text(
                        "Дата: ${
                            java.text.SimpleDateFormat("dd.MM.yyyy HH:mm")
                                .format(score.timestamp)
                        }", color = Color.White
                    )
                }
            }
        }
    }
}


@Composable
fun BugsGame() {
    val context = LocalContext.current
    val db = remember { GameDatabase.getDatabase(context) }
    val prefs = context.getSharedPreferences("GameSettings", Context.MODE_PRIVATE)
    val speed = (prefs.getString("speed", "8") ?: "8").toInt()
    val maxBugs = (prefs.getString("max_bugs", "10") ?: "10").toInt()
    val roundDuration = (prefs.getString("round_duration", "60") ?: "60").toInt()
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val savedDifficulty = sharedPreferences.getInt("difficulty", 1)

    var score by remember { mutableStateOf(0) }
    var bugs by remember { mutableStateOf(listOf<Bug>()) }
    var timeLeft by remember { mutableStateOf(roundDuration) }

    val spawnInterval = (2000L / savedDifficulty)

    LaunchedEffect(Unit) {
        var spawnTime = 0L
        var timerTime = 0L
        while (timeLeft > 0) {
            delay(16L)
            bugs = bugs.map { it.move(speed / 60f) }


            spawnTime += 16L
            if (spawnTime >= spawnInterval && bugs.size < maxBugs) {
                bugs = bugs + Bug.randomBug(bugs)
                spawnTime = 0L
            }


            timerTime += 16L
            if (timerTime >= 1000L) {
                timeLeft--
                timerTime = 0L
            }
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
        .then(
            if (timeLeft > 0) {
                Modifier.pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val density = Resources.getSystem().displayMetrics.density
                        val clickedBug = bugs.find { bug ->
                            val bugSizePx = 50 * density
                            offset.x in bug.x * density..(bug.x * density + bugSizePx) &&
                                    offset.y in bug.y * density..(bug.y * density + bugSizePx)
                        }
                        if (clickedBug != null) {
                            bugs = bugs - clickedBug
                            score += 10
                        } else {
                            score -= 10
                        }
                    }
                }
            } else Modifier
        )
    ) {
        bugs.forEach { bug ->
            Image(
                painter = painterResource(id = R.drawable.buglady),
                contentDescription = "Bug",
                modifier = Modifier
                    .size(50.dp)
                    .offset(bug.x.dp, bug.y.dp)
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .background(Color(0xAA000000))
                .padding(8.dp)
        ) {
            Text("Очки: $score", color = Color.White)
            Text("Время: $timeLeft с", color = Color.White)
        }

        if (timeLeft <= 0) {
            val context = LocalContext.current
            val sharedPrefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val name = sharedPrefs.getString("name", "") ?: ""
            val difficulty = sharedPrefs.getInt("difficulty", 1)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x88000000)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Игра окончена!\nОчки: $score",
                    color = Color.White,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
            }

            // 💾 Сохраняем результат 1 раз
            LaunchedEffect(key1 = name + score) {
                if (name.isNotBlank()) {
                    try {
                        withContext(Dispatchers.IO) {
                            val allPlayers = db.playerDao().getAllPlayersOnce()
                            val player = allPlayers.find { it.name == name }

                            // если нет, создаем нового
                            val playerId = player?.id ?: db.playerDao().insertPlayer(
                                PlayerEntity(
                                    name = name,
                                    gender = sharedPrefs.getString("gender", "") ?: "",
                                    course = sharedPrefs.getString("course", "") ?: "",
                                    difficulty = difficulty,
                                    birthDate = sharedPrefs.getString("birthDate", "") ?: "",
                                    zodiac = sharedPrefs.getString("zodiac", "") ?: ""
                                )
                            ).toInt()

                            db.scoreDao().insertScore(
                                ScoreEntity(
                                    playerId = playerId,
                                    score = score,
                                    difficulty = difficulty
                                )
                            )
                        }
                        Toast.makeText(context, "Результат сохранён", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, "Ошибка при сохранении результата", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Игрок не найден!", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }
}

data class Bug(
    val x: Float,
    val y: Float,
    val dx: Float,
    val dy: Float
) {
    companion object {
        fun randomBug(existingBugs: List<Bug>): Bug {
            var newBug: Bug
            do {
                val randomX = (0..300).random().toFloat()
                val randomY = (0..500).random().toFloat()
                val dirX = listOf(-1f, 1f).random()
                val dirY = listOf(-1f, 1f).random()
                newBug = Bug(randomX, randomY, dirX, dirY)
            } while (existingBugs.any { it.isOverlapping(newBug) })
            return newBug
        }

        fun Bug.isOverlapping(other: Bug): Boolean {
            val size = 50f
            return (x < other.x + size && x + size > other.x &&
                    y < other.y + size && y + size > other.y)
        }

    }


    fun move(speed: Float): Bug {
        val newX = (x + dx * speed).coerceIn(0f, 320f)
        val newY = (y + dy * speed).coerceIn(0f, 550f)
        val newDx = if (newX == 0f || newX == 320f) -dx else dx
        val newDy = if (newY == 0f || newY == 550f) -dy else dy
        return copy(x = newX, y = newY, dx = newDx, dy = newDy)
    }



    val xPx get() = x * Resources.getSystem().displayMetrics.density
    val yPx get() = y * Resources.getSystem().displayMetrics.density
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
    val scope = rememberCoroutineScope()
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
    val db = remember { GameDatabase.getDatabase(context) }
    var players by remember { mutableStateOf<List<PlayerEntity>>(emptyList()) }
    var showPlayerList by remember { mutableStateOf(false) }

        //ettings → Apps → lab1 → Storage → Clear data
    LaunchedEffect(Unit) {
        try {
            withContext(Dispatchers.IO) {
                players = db.playerDao().getAllPlayersOnce()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Button(onClick = { showPlayerList = !showPlayerList }) {
        Text("Выбрать существующего игрока")
    }

    if (showPlayerList) {
        LazyColumn {
            items(players) { player ->
                Button(onClick = {
                    name = player.name
                    gender = player.gender
                    course = player.course
                    difficulty = player.difficulty.toFloat()
                    birthDate = player.birthDate
                    zodiac = player.zodiac
                    showPlayerList = false
                }) {
                    Text("Игрок: ${player.name} (${player.course} курс)")
                }
            }
        }
    }

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
                if (name.isBlank() || gender.isBlank() || course.isBlank() || birthDate.isBlank()) {
                    Toast.makeText(context, "Заполни все поля!", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                sharedPreferences.edit {
                    putString("name", name)
                    putString("gender", gender)
                    putString("course", course)
                    putInt("difficulty", difficulty.toInt())
                    putString("birthDate", birthDate)
                    putString("zodiac", zodiac)
                }

                scope.launch(Dispatchers.IO) {
                    try {
                        val existing = db.playerDao().getAllPlayersOnce().find { it.name == name }
                        if (existing == null) {
                            db.playerDao().insertPlayer(
                                PlayerEntity(
                                    name = name,
                                    gender = gender,
                                    course = course,
                                    difficulty = difficulty.toInt(),
                                    birthDate = birthDate,
                                    zodiac = zodiac
                                )
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Ошибка при сохранении игрока", Toast.LENGTH_SHORT).show()
                        }
                    }
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
            val zodiacDrawable = when (zodiac.trim().lowercase()) {
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
    return when {
        month == 1 && day in 1..19 -> "Capricorn"
        month == 1 && day in 20..31 -> "Aquarius"
        month == 2 && day in 1..18 -> "Aquarius"
        month == 2 && day in 19..29 -> "Pisces"
        month == 3 && day in 1..20 -> "Pisces"
        month == 3 && day in 21..31 -> "Aries"
        month == 4 && day in 1..19 -> "Aries"
        month == 4 && day in 20..30 -> "Taurus"
        month == 5 && day in 1..20 -> "Taurus"
        month == 5 && day in 21..31 -> "Gemini"
        month == 6 && day in 1..20 -> "Gemini"
        month == 6 && day in 21..30 -> "Cancer"
        month == 7 && day in 1..22 -> "Cancer"
        month == 7 && day in 23..31 -> "Leo"
        month == 8 && day in 1..22 -> "Leo"
        month == 8 && day in 23..31 -> "Virgo"
        month == 9 && day in 1..22 -> "Virgo"
        month == 9 && day in 23..30 -> "Libra"
        month == 10 && day in 1..22 -> "Libra"
        month == 10 && day in 23..31 -> "Scorpio"
        month == 11 && day in 1..21 -> "Scorpio"
        month == 11 && day in 22..30 -> "Sagittarius"
        month == 12 && day in 1..21 -> "Sagittarius"
        month == 12 && day in 22..31 -> "Capricorn"
        else -> "Unknown"
    }
}
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


