package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.game.data.GameWorldData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `verify app name matches Malay adventure game title`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Pengembaraan Ajaib", appName)
    }

    @Test
    fun `verify all five worlds are defined with collectibles and finish portal`() {
        assertEquals(5, GameWorldData.worlds.size)

        for (i in 1..5) {
            val levelData = GameWorldData.getLevelData(i)
            assertNotNull(levelData)
            assertTrue("World $i must have platforms", levelData.platforms.isNotEmpty())
            assertTrue("World $i must have 5 golden stars", levelData.collectibles.count { it.type == com.example.game.model.CollectibleType.STAR } >= 5)
            assertTrue("World $i must have a finish portal", levelData.portal.x > 2500f)
            assertTrue("World $i must have an educational checkpoint", levelData.checkpoints.isNotEmpty())
            val cp = levelData.checkpoints.first()
            assertNotNull("World $i must have educational challenge", cp.challenge)
            assertTrue("World $i challenge correctIndex in range", cp.challenge!!.correctIndex in cp.challenge!!.options.indices)
        }
    }
}
