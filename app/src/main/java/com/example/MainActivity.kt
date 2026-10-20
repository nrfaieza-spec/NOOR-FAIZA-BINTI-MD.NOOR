package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.game.audio.GameAudioEngine
import com.example.game.data.GamePreferences
import com.example.game.data.GameWorldData
import com.example.game.engine.PlatformGameEngine
import com.example.game.ui.GameScreen
import com.example.game.ui.WorldMapScreen
import com.example.ui.theme.MyApplicationTheme

enum class Screen {
    WORLD_MAP,
    GAME_PLAY
}

class MainActivity : ComponentActivity() {

    private lateinit var audioEngine: GameAudioEngine
    private lateinit var preferences: GamePreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        audioEngine = GameAudioEngine(this)
        preferences = GamePreferences(this)
        audioEngine.isSfxEnabled = preferences.isSfxEnabled
        audioEngine.isVoiceEnabled = preferences.isVoiceEnabled

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFE8F5E9)
                ) {
                    AdventureGameApp(
                        audioEngine = audioEngine,
                        preferences = preferences
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioEngine.release()
    }
}

@Composable
fun AdventureGameApp(
    audioEngine: GameAudioEngine,
    preferences: GamePreferences
) {
    var currentScreen by remember { mutableStateOf(Screen.WORLD_MAP) }
    var activeWorldNumber by remember { mutableIntStateOf(1) }

    when (currentScreen) {
        Screen.WORLD_MAP -> {
            WorldMapScreen(
                preferences = preferences,
                audioEngine = audioEngine,
                onSelectWorld = { worldNumber ->
                    activeWorldNumber = worldNumber
                    currentScreen = Screen.GAME_PLAY
                }
            )
        }

        Screen.GAME_PLAY -> {
            val levelData = remember(activeWorldNumber) {
                GameWorldData.getLevelData(activeWorldNumber)
            }
            val engine = remember(levelData) {
                PlatformGameEngine(levelData, audioEngine, preferences)
            }

            BackHandler {
                currentScreen = Screen.WORLD_MAP
            }

            GameScreen(
                engine = engine,
                audioEngine = audioEngine,
                onReturnToMap = {
                    currentScreen = Screen.WORLD_MAP
                },
                onNextLevel = { nextWorld ->
                    activeWorldNumber = nextWorld
                }
            )
        }
    }
}
