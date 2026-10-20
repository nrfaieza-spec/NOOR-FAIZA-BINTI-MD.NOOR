package com.example.game.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.game.audio.GameAudioEngine
import com.example.game.model.EducationalChallenge

@Composable
fun ChallengeDialog(
    challenge: EducationalChallenge,
    audioEngine: GameAudioEngine,
    onSuccess: () -> Unit
) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var isCorrect by remember { mutableStateOf(false) }
    var showWrongFeedback by remember { mutableStateOf(false) }

    LaunchedEffect(challenge.id) {
        audioEngine.speak(challenge.question)
    }

    Dialog(
        onDismissRequest = { /* Must answer to resume */ },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(12.dp)
                .testTag("challenge_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FBE7)),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Banner
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF2E7D32), Color(0xFF43A047))
                            )
                        )
                        .padding(horizontal = 18.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🌱",
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = challenge.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = { audioEngine.speak(challenge.question) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Dengar soalan",
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Question Box
                Text(
                    text = challenge.question,
                    color = Color(0xFF1B2E1C),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    lineHeight = 23.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Options List
                challenge.options.forEachIndexed { index, option ->
                    val isThisSelected = selectedIndex == index
                    val isThisCorrect = isThisSelected && index == challenge.correctIndex
                    val isThisWrong = isThisSelected && index != challenge.correctIndex

                    val borderColor = when {
                        isThisCorrect -> Color(0xFF2E7D32)
                        isThisWrong -> Color(0xFFD32F2F)
                        else -> Color(0xFFC8E6C9)
                    }

                    val bgColor = when {
                        isThisCorrect -> Color(0xFFE8F5E9)
                        isThisWrong -> Color(0xFFFFEBEE)
                        else -> Color.White
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
                            .clickable(enabled = !isCorrect) {
                                selectedIndex = index
                                if (index == challenge.correctIndex) {
                                    isCorrect = true
                                    showWrongFeedback = false
                                    audioEngine.speak(challenge.explanation)
                                } else {
                                    isCorrect = false
                                    showWrongFeedback = true
                                    audioEngine.speak("Cuba lagi, kawan! Kamu boleh melakukannya.")
                                }
                            }
                            .testTag("challenge_option_$index"),
                        color = bgColor,
                        tonalElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Badge Icon
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF1F8E9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = option.badge, fontSize = 20.sp)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = option.text,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF1B2E1C)
                                )
                                if (option.subtext.isNotEmpty()) {
                                    Text(
                                        text = option.subtext,
                                        fontSize = 12.sp,
                                        color = Color(0xFF556B57)
                                    )
                                }
                            }

                            if (isThisCorrect) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Betul",
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(24.dp)
                                )
                            } else if (isThisWrong) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Salah",
                                    tint = Color(0xFFD32F2F),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Feedback Banner
                AnimatedVisibility(
                    visible = showWrongFeedback,
                    enter = fadeIn() + slideInVertically()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFF3E0))
                            .border(1.dp, Color(0xFFFFB74D), RoundedCornerShape(12.dp))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "💡 Cuba lagi! ${challenge.hint}",
                            color = Color(0xFFE65100),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                AnimatedVisibility(
                    visible = isCorrect,
                    enter = fadeIn() + slideInVertically()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE8F5E9))
                                .border(1.dp, Color(0xFF81C784), RoundedCornerShape(12.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "🎉 " + challenge.explanation,
                                color = Color(0xFF1B5E20),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = onSuccess,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("challenge_continue_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "Teruskan Pengembaraan! 🚀",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
