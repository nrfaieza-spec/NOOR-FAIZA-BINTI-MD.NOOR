package com.example.game.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Locale
import kotlin.math.PI
import kotlin.math.sin

/**
 * Procedural Audio Synthesizer and Native Malay Text-To-Speech engine.
 * Generates instant, zero-latency retro/platformer sound effects without external audio files.
 */
class GameAudioEngine(private val context: Context) : TextToSpeech.OnInitListener {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    var isSfxEnabled = true
    var isVoiceEnabled = true

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e("GameAudioEngine", "Failed to init TTS", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val ttsEngine = tts ?: return
            val malayLocale = Locale("ms", "MY")
            val result = ttsEngine.setLanguage(malayLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to standard Malay or default
                val altMalay = Locale("ms")
                if (ttsEngine.setLanguage(altMalay) == TextToSpeech.LANG_NOT_SUPPORTED) {
                    ttsEngine.language = Locale.getDefault()
                }
            }
            ttsEngine.setPitch(1.15f) // Cheerful, child-friendly pitch
            ttsEngine.setSpeechRate(0.95f) // Clear speech rate for kids
            isTtsReady = true
        }
    }

    fun speak(text: String) {
        if (!isVoiceEnabled || !isTtsReady) return
        try {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "BijiVoice_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.w("GameAudioEngine", "TTS error: ${e.message}")
        }
    }

    fun playJump() {
        if (!isSfxEnabled) return
        vibrate(20)
        scope.launch {
            // Frequency sweep 320Hz to 680Hz
            synthesizeSweep(startFreq = 320f, endFreq = 680f, durationSec = 0.12f, volume = 0.6f)
        }
    }

    fun playCollectStar() {
        if (!isSfxEnabled) return
        vibrate(30)
        scope.launch {
            // Bright two-tone chime (987Hz -> 1318Hz)
            synthesizeTone(freq = 987f, durationSec = 0.08f, volume = 0.5f)
            synthesizeTone(freq = 1318f, durationSec = 0.16f, volume = 0.7f)
        }
    }

    fun playCollectDroplet() {
        if (!isSfxEnabled) return
        vibrate(25)
        scope.launch {
            // Bubble tone with gentle downward pitch
            synthesizeSweep(startFreq = 580f, endFreq = 380f, durationSec = 0.10f, volume = 0.6f)
        }
    }

    fun playCollectSun() {
        if (!isSfxEnabled) return
        vibrate(30)
        scope.launch {
            // Radiant triad arpeggio
            synthesizeTone(freq = 523f, durationSec = 0.06f, volume = 0.5f)
            synthesizeTone(freq = 659f, durationSec = 0.06f, volume = 0.6f)
            synthesizeTone(freq = 784f, durationSec = 0.14f, volume = 0.7f)
        }
    }

    fun playCheckpoint() {
        if (!isSfxEnabled) return
        vibrate(40)
        scope.launch {
            val freqs = floatArrayOf(440f, 554f, 659f, 880f)
            for (f in freqs) {
                synthesizeTone(freq = f, durationSec = 0.07f, volume = 0.6f)
            }
        }
    }

    fun playVictory() {
        if (!isSfxEnabled) return
        vibrate(80)
        scope.launch {
            val notes = floatArrayOf(523f, 659f, 784f, 1046f, 784f, 1046f)
            val durations = floatArrayOf(0.12f, 0.12f, 0.12f, 0.22f, 0.12f, 0.35f)
            for (i in notes.indices) {
                synthesizeTone(freq = notes[i], durationSec = durations[i], volume = 0.75f)
            }
        }
    }

    fun playHurt() {
        if (!isSfxEnabled) return
        vibrate(60)
        scope.launch {
            synthesizeSweep(startFreq = 380f, endFreq = 160f, durationSec = 0.25f, volume = 0.65f)
        }
    }

    fun vibrate(millis: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(millis)
                }
            }
        } catch (_: Exception) {
            // Graceful fallback if device has no vibrator
        }
    }

    private fun synthesizeTone(freq: Float, durationSec: Float, volume: Float) {
        val sampleRate = 22050
        val numSamples = (durationSec * sampleRate).toInt().coerceAtLeast(1)
        val shortBuffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toFloat() / sampleRate
            // Sine wave with soft attack and exponential decay envelope
            val raw = sin(2.0 * PI * freq * t).toFloat()
            val envelope = (1f - (i.toFloat() / numSamples)).coerceIn(0f, 1f)
            val sample = (raw * envelope * volume * Short.MAX_VALUE).toInt()
            shortBuffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        playRawPcm(shortBuffer, sampleRate)
    }

    private fun synthesizeSweep(startFreq: Float, endFreq: Float, durationSec: Float, volume: Float) {
        val sampleRate = 22050
        val numSamples = (durationSec * sampleRate).toInt().coerceAtLeast(1)
        val shortBuffer = ShortArray(numSamples)
        var phase = 0.0

        for (i in 0 until numSamples) {
            val progress = i.toFloat() / numSamples
            val currentFreq = startFreq + (endFreq - startFreq) * progress
            phase += 2.0 * PI * currentFreq / sampleRate
            val raw = sin(phase).toFloat()
            val envelope = (1f - progress).coerceIn(0f, 1f)
            val sample = (raw * envelope * volume * Short.MAX_VALUE).toInt()
            shortBuffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

        playRawPcm(shortBuffer, sampleRate)
    }

    private fun playRawPcm(samples: ShortArray, sampleRate: Int) {
        var track: AudioTrack? = null
        try {
            val bufferSize = samples.size * 2
            track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(samples, 0, samples.size)
            track.play()
            Thread.sleep((samples.size.toFloat() / sampleRate * 1000).toLong() + 30)
        } catch (_: Exception) {
        } finally {
            try {
                track?.stop()
                track?.release()
            } catch (_: Exception) {}
        }
    }

    fun release() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {}
    }
}
