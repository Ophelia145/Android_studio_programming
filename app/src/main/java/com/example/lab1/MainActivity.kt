package com.example.lab1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.lab1.data.Player
import com.example.lab1.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainTabs()
                }
            }
        }
    }
}

@Composable
fun MainTabs() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Игра", "Регистрация", "Рекорды", "Правила", "Авторы", "Настройки")

    var registrationState by remember { mutableStateOf(RegistrationState()) }
    var settingsState by remember { mutableStateOf(SettingsState()) }
    var selectedPlayer by remember { mutableStateOf<Player?>(null) }

    val backgroundColor = if (selectedTab == 0) Color.White else Color(0xFFf3d7fa)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Box(  modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)) {
                when (selectedTab) {
                    0 -> {
                        if (selectedPlayer == null) {
                            PlayerSelection(onPlayerSelected = { selectedPlayer = it })
                        } else {
                            GameTab(settingsState = settingsState, player = selectedPlayer!!)
                        }
                    }
                    1 -> RegistrationTab(
                        state = registrationState,
                        onStateChange = { registrationState = it }
                    )
                    2 -> RecordsTab()
                    3 -> RulesTab()
                    4 -> AuthorsTab()
                    5 -> SettingsTab(
                        state = settingsState,
                        onStateChange = { settingsState = it }
                    )
                }
            }
        }
    }
}
