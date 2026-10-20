package com.example.game.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.game.audio.GameAudioEngine
import com.example.game.data.GamePreferences
import com.example.game.data.GameWorldData
import com.example.game.model.WorldDefinition

@Composable
fun WorldMapScreen(
    preferences: GamePreferences,
    audioEngine: GameAudioEngine,
    onSelectWorld: (Int) -> Unit
) {
    val unlockedMax = preferences.getUnlockedWorldMax()
    val totalStars = preferences.getTotalStars()
    var showStoryDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9), Color(0xFFA5D6A7))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Top App Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🌱", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PENGEMBARAAN AJAIB",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = Color(0xFF1B5E20)
                            )
                        }
                        Text(
                            text = "Misi Selamatkan Taman Bunga",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF4CAF50)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Total Stars Badge
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFFFF8E1),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFB300),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$totalStars/25",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFFE65100)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Audio Toggle
                        IconButton(
                            onClick = {
                                audioEngine.isSfxEnabled = !audioEngine.isSfxEnabled
                                audioEngine.isVoiceEnabled = audioEngine.isSfxEnabled
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (audioEngine.isSfxEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                contentDescription = "Bunyi",
                                tint = Color(0xFF2E7D32)
                            )
                        }

                        // Info Button
                        IconButton(
                            onClick = { showStoryDialog = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Panduan & Kisah",
                                tint = Color(0xFF2E7D32)
                            )
                        }
                    }
                }
            }

            // World Map List with stepping trail
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("world_map_list"),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(GameWorldData.worlds) { index, world ->
                    val isUnlocked = world.worldNumber <= unlockedMax
                    val starsEarned = preferences.getStarsForWorld(world.worldNumber)
                    val highScore = preferences.getHighScoreForWorld(world.worldNumber)

                    WorldMapCard(
                        world = world,
                        isUnlocked = isUnlocked,
                        starsEarned = starsEarned,
                        highScore = highScore,
                        onPlay = {
                            if (isUnlocked) {
                                onSelectWorld(world.worldNumber)
                            } else {
                                audioEngine.speak("Selesaikan dunia sebelumnya dahulu!")
                            }
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }

        // Story Dialog
        if (showStoryDialog) {
            StoryGuideDialog(onDismiss = { showStoryDialog = false })
        }
    }
}

@Composable
private fun WorldMapCard(
    world: WorldDefinition,
    isUnlocked: Boolean,
    starsEarned: Int,
    highScore: Int,
    onPlay: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable(enabled = isUnlocked, onClick = onPlay)
            .testTag("world_card_${world.worldNumber}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) Color.White else Color(0xFFECEFF1)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 8.dp else 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // World Image Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Image(
                    painter = painterResource(id = world.backgroundRes),
                    contentDescription = world.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = if (isUnlocked) 1f else 0.45f
                )

                // Gradient scrim
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0xAA000000))
                            )
                        )
                )

                // Top World Number Tag
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(world.primaryColor),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "DUNIA ${world.worldNumber}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Lock Overlay if not unlocked
                if (!isUnlocked) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0x99000000)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Terkunci",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Title overlay at bottom of image
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Text(
                        text = world.title,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = world.subtitle,
                        color = Color(0xFFE8F5E9),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Bottom Details Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Text(
                    text = world.description,
                    color = Color(0xFF455A64),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Star Rating
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        for (i in 1..3) {
                            val active = i <= when {
                                starsEarned >= 5 -> 3
                                starsEarned >= 3 -> 2
                                starsEarned >= 1 -> 1
                                else -> 0
                            }
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (active) Color(0xFFFFB300) else Color(0xFFCFD8DC),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        if (highScore > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Skor: $highScore",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }

                    // Action Button
                    if (isUnlocked) {
                        Button(
                            onClick = onPlay,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(world.primaryColor)),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (starsEarned > 0) "Main Semula" else "Mulakan!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFE0E0E0)
                        ) {
                            Text(
                                text = "Terkunci",
                                color = Color(0xFF757575),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StoryGuideDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(12.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDE7))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🌸 Kisah Pengembaraan Biji",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Taman Bunga Ajaib telah kehilangan sinarannya! Biji, seekor anak benih yang ceria dengan pucuk hijau di kepalanya, memulakan misi merentasi 5 Dunia:\n\n" +
                            "1️⃣ Taman Benih Ajaib - Kenal pasti struktur biji benih.\n" +
                            "2️⃣ Sungai Titisan Air - Fahami peranan air untuk tumbuhan.\n" +
                            "3️⃣ Hutan Akar Ajaib - Ketahui urutan percambahan akar & pucuk.\n" +
                            "4️⃣ Bukit Cahaya Matahari - Kumpul tenaga fotosintesis & 4 keperluan asas.\n" +
                            "5️⃣ Istana Bunga Mekar - Lengkapkan kitaran hidup & selamatkan taman!\n\n" +
                            "Kawal Biji dengan butang kiri/kanan dan lompat. Kumpul bintang emas dan jawab soalan sains di setiap papan tanda!",
                    fontSize = 13.sp,
                    color = Color(0xFF37474F),
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Faham! Jom Main! 🚀", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
