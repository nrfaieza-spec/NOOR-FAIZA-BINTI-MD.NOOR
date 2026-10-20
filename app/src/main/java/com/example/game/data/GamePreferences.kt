package com.example.game.data

import android.content.Context
import android.content.SharedPreferences

class GamePreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("biji_adventure_prefs", Context.MODE_PRIVATE)

    fun getUnlockedWorldMax(): Int {
        return prefs.getInt("unlocked_world_max", 1)
    }

    fun unlockWorld(worldNumber: Int) {
        val current = getUnlockedWorldMax()
        if (worldNumber > current) {
            prefs.edit().putInt("unlocked_world_max", worldNumber.coerceAtMost(5)).apply()
        }
    }

    fun getStarsForWorld(worldNumber: Int): Int {
        return prefs.getInt("stars_world_$worldNumber", 0)
    }

    fun saveStarsForWorld(worldNumber: Int, stars: Int) {
        val prev = getStarsForWorld(worldNumber)
        if (stars > prev) {
            prefs.edit().putInt("stars_world_$worldNumber", stars).apply()
        }
    }

    fun getHighScoreForWorld(worldNumber: Int): Int {
        return prefs.getInt("highscore_world_$worldNumber", 0)
    }

    fun saveHighScoreForWorld(worldNumber: Int, score: Int) {
        val prev = getHighScoreForWorld(worldNumber)
        if (score > prev) {
            prefs.edit().putInt("highscore_world_$worldNumber", score).apply()
        }
    }

    fun getTotalStars(): Int {
        var total = 0
        for (i in 1..5) {
            total += getStarsForWorld(i)
        }
        return total
    }

    var isSfxEnabled: Boolean
        get() = prefs.getBoolean("sfx_enabled", true)
        set(value) = prefs.edit().putBoolean("sfx_enabled", value).apply()

    var isVoiceEnabled: Boolean
        get() = prefs.getBoolean("voice_enabled", true)
        set(value) = prefs.edit().putBoolean("voice_enabled", value).apply()
}
