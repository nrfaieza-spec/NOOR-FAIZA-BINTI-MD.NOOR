package com.example.game.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.audio.GameAudioEngine
import com.example.game.engine.PlatformGameEngine
import com.example.game.model.CollectibleType
import com.example.game.render.BijiRenderer
import com.example.game.render.GameWorldRenderer

@Composable
fun GameScreen(
    engine: PlatformGameEngine,
    audioEngine: GameAudioEngine,
    onReturnToMap: () -> Unit,
    onNextLevel: (Int) -> Unit
) {
    val state by engine.gameState.collectAsState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    // Frame game loop using withFrameNanos for smooth delta-time updates
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenWidth = constraints.maxWidth.toFloat()

        LaunchedEffect(engine) {
            var lastFrameTime = 0L
            while (true) {
                withFrameNanos { frameTimeNanos ->
                    if (lastFrameTime != 0L) {
                        val dt = (frameTimeNanos - lastFrameTime) / 1_000_000_000f
                        engine.update(dt, screenWidth)
                    }
                    lastFrameTime = frameTimeNanos
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester)
                .focusable()
                .onKeyEvent { keyEvent ->
                    if (keyEvent.type == KeyEventType.KeyDown) {
                        when (keyEvent.key) {
                            Key.DirectionLeft, Key.A -> {
                                engine.setMoveInput(-1f)
                                true
                            }
                            Key.DirectionRight, Key.D -> {
                                engine.setMoveInput(1f)
                                true
                            }
                            Key.DirectionUp, Key.Spacebar, Key.W -> {
                                engine.requestJump()
                                true
                            }
                            Key.P, Key.Escape -> {
                                engine.setPaused(!state.isPaused)
                                true
                            }
                            else -> false
                        }
                    } else if (keyEvent.type == KeyEventType.KeyUp) {
                        when (keyEvent.key) {
                            Key.DirectionLeft, Key.A, Key.DirectionRight, Key.D -> {
                                engine.setMoveInput(0f)
                                true
                            }
                            else -> false
                        }
                    } else {
                        false
                    }
                }
        ) {
            // 1. Scenery Background with Parallax Scrolling
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = engine.levelData.definition.backgroundRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Subtle gradient overlay for readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0x22000000), Color.Transparent, Color(0x33000000))
                            )
                        )
                )
            }

            // 2. Main Game Platform World Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val camX = state.cameraX

                withTransform({
                    translate(left = -camX, top = 0f)
                }) {
                    // Hazards (water, thorns)
                    GameWorldRenderer.drawHazards(this, engine.levelData.hazards, state.animTime, camX, screenWidth)

                    // Platforms
                    GameWorldRenderer.drawPlatforms(this, engine.levelData.platforms, camX, screenWidth)

                    // Checkpoints
                    GameWorldRenderer.drawCheckpoints(this, engine.levelData.checkpoints, state.animTime, camX, screenWidth)

                    // Finish Portal
                    GameWorldRenderer.drawFinishPortal(this, engine.levelData.portal, state.animTime, camX, screenWidth)

                    // Collectibles (Stars, Droplets, Sun tokens, Seeds)
                    GameWorldRenderer.drawCollectibles(this, engine.levelData.collectibles, state.animTime, camX, screenWidth)

                    // Biji Animated Playable Character
                    BijiRenderer.drawBiji(
                        drawScope = this,
                        centerX = state.playerX,
                        centerY = state.playerY,
                        state = state.animationState,
                        animTime = state.animTime,
                        facingRight = state.facingRight,
                        invulnerable = state.isInvulnerable,
                        size = 56f
                    )

                    // Sparkle Particles
                    GameWorldRenderer.drawSparkles(this, engine.particles)
                }
            }

            // 3. Top HUD: Health, Score, Collectibles, Progress & Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Lives Badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xCCFFFFFF),
                        tonalElevation = 4.dp,
                        modifier = Modifier.testTag("hud_lives")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🌱", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            for (i in 1..3) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Nyawa $i",
                                    tint = if (i <= state.lives) Color(0xFFE91E63) else Color(0xFFBDBDBD),
                                    modifier = Modifier.size(18.dp).padding(horizontal = 1.dp)
                                )
                            }
                        }
                    }

                    // Level Title Badge
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xDD2E7D32),
                        tonalElevation = 4.dp
                    ) {
                        Text(
                            text = engine.levelData.definition.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    // Stars & Score Badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xCCFFFFFF),
                        tonalElevation = 4.dp,
                        modifier = Modifier.testTag("hud_score")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Bintang",
                                tint = Color(0xFFFFB300),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${state.starsCollected}/${state.totalStars}",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1B2E1C),
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${state.score}",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32),
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Sound Toggle & Pause Button
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xCCFFFFFF),
                            modifier = Modifier.size(38.dp)
                        ) {
                            IconButton(onClick = {
                                audioEngine.isSfxEnabled = !audioEngine.isSfxEnabled
                                audioEngine.isVoiceEnabled = audioEngine.isSfxEnabled
                            }) {
                                Icon(
                                    imageVector = if (audioEngine.isSfxEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                    contentDescription = "Bunyi",
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            shape = CircleShape,
                            color = Color(0xCCFFFFFF),
                            modifier = Modifier.size(38.dp).testTag("hud_pause_button")
                        ) {
                            IconButton(onClick = { engine.setPaused(true) }) {
                                Icon(
                                    imageVector = Icons.Default.Pause,
                                    contentDescription = "Jeda",
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Progress Bar towards Finish Portal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Mula", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    LinearProgressIndicator(
                        progress = { state.progressFraction },
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFFFFD54F),
                        trackColor = Color(0x66FFFFFF)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Portal 🌸", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            // 4. On-Screen Touch Controls (Mobile / Tablet)
            GameControlsOverlay(
                onMove = { dir -> engine.setMoveInput(dir) },
                onJump = { engine.requestJump() }
            )

            // 5. Modals & Overlays
            state.activeChallenge?.let { challenge ->
                ChallengeDialog(
                    challenge = challenge,
                    audioEngine = audioEngine,
                    onSuccess = { engine.completeChallenge() }
                )
            }

            if (state.isLevelComplete) {
                VictoryDialog(
                    levelData = engine.levelData,
                    starsCollected = state.starsCollected,
                    score = state.score,
                    onNextWorld = {
                        val next = engine.levelData.definition.worldNumber + 1
                        if (next <= 5) {
                            onNextLevel(next)
                        } else {
                            onReturnToMap()
                        }
                    },
                    onRestart = { engine.restartLevel() },
                    onReturnToMap = onReturnToMap
                )
            }

            if (state.isGameOver) {
                GameOverDialog(
                    onRetry = { engine.restartLevel() },
                    onReturnToMap = onReturnToMap
                )
            }

            if (state.isPaused) {
                PauseMenuDialog(
                    audioEngine = audioEngine,
                    onResume = { engine.setPaused(false) },
                    onRestart = { engine.restartLevel() },
                    onReturnToMap = onReturnToMap
                )
            }
        }
    }
}
