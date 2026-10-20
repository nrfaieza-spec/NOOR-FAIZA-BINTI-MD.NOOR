package com.example.game.engine

import com.example.game.audio.GameAudioEngine
import com.example.game.data.GamePreferences
import com.example.game.data.WorldLevelData
import com.example.game.model.AnimationState
import com.example.game.model.Checkpoint
import com.example.game.model.Collectible
import com.example.game.model.CollectibleType
import com.example.game.model.EducationalChallenge
import com.example.game.model.Hazard
import com.example.game.model.HazardType
import com.example.game.model.Platform
import com.example.game.model.SparkleParticle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

data class GameState(
    val playerX: Float = 100f,
    val playerY: Float = 550f,
    val playerVx: Float = 0f,
    val playerVy: Float = 0f,
    val facingRight: Boolean = true,
    val isGrounded: Boolean = false,
    val animationState: AnimationState = AnimationState.IDLE,
    val animTime: Float = 0f,
    val cameraX: Float = 0f,
    val lives: Int = 3,
    val score: Int = 0,
    val starsCollected: Int = 0,
    val totalStars: Int = 5,
    val itemsCollected: Int = 0,
    val isInvulnerable: Boolean = false,
    val isPaused: Boolean = false,
    val isGameOver: Boolean = false,
    val isLevelComplete: Boolean = false,
    val activeChallenge: EducationalChallenge? = null,
    val progressFraction: Float = 0f
)

class PlatformGameEngine(
    val levelData: WorldLevelData,
    val audioEngine: GameAudioEngine,
    val preferences: GamePreferences
) {
    private val _gameState = MutableStateFlow(
        GameState(
            playerX = levelData.definition.startX,
            playerY = levelData.definition.startY,
            totalStars = levelData.definition.totalStars
        )
    )
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private var playerX = levelData.definition.startX
    private var playerY = levelData.definition.startY
    private var playerVx = 0f
    private var playerVy = 0f
    private var facingRight = true
    private var isGrounded = false
    private var animationState = AnimationState.IDLE
    private var animTime = 0f
    private var cameraX = 0f
    private var lives = 3
    private var score = 0
    private var starsCollected = 0
    private var itemsCollected = 0
    private var invulnerableTimer = 0f
    private var isPaused = false
    private var isGameOver = false
    private var isLevelComplete = false
    private var activeChallenge: EducationalChallenge? = null

    private var respawnX = levelData.definition.startX
    private var respawnY = levelData.definition.startY

    val particles = mutableListOf<SparkleParticle>()

    // Constants
    private val gravity = 1450f
    private val maxRunSpeed = 280f
    private val runAccel = 1600f
    private val runDecel = 1800f
    private val jumpVelocity = -630f
    private val playerWidth = 44f
    private val playerHeight = 52f

    private var moveInput = 0f
    private var jumpRequested = false
    private var collectHappyTimer = 0f

    init {
        // Welcome voice prompt in Malay
        audioEngine.speak("Jom, Biji!")
    }

    fun setMoveInput(input: Float) {
        moveInput = input.coerceIn(-1f, 1f)
    }

    fun requestJump() {
        jumpRequested = true
    }

    fun setPaused(paused: Boolean) {
        isPaused = paused
        emitState()
    }

    fun restartLevel() {
        playerX = levelData.definition.startX
        playerY = levelData.definition.startY
        playerVx = 0f
        playerVy = 0f
        facingRight = true
        isGrounded = false
        lives = 3
        score = 0
        starsCollected = 0
        itemsCollected = 0
        invulnerableTimer = 0f
        isGameOver = false
        isLevelComplete = false
        isPaused = false
        activeChallenge = null
        respawnX = levelData.definition.startX
        respawnY = levelData.definition.startY

        // Reset collectibles & checkpoints
        for (c in levelData.collectibles) {
            c.collected = false
        }
        for (cp in levelData.checkpoints) {
            cp.activated = false
        }
        levelData.portal.reached = false
        particles.clear()

        audioEngine.speak("Jom, Biji!")
        emitState()
    }

    fun completeChallenge() {
        activeChallenge = null
        audioEngine.playCheckpoint()
        audioEngine.speak("Bagus! Benih mula bercambah!")
        score += 300
        spawnParticles(playerX, playerY - 30f, 20, 0xFF4CAF50)
        emitState()
    }

    fun update(dt: Float, screenWidth: Float) {
        if (isPaused || isGameOver || isLevelComplete || activeChallenge != null) {
            updateParticles(dt)
            emitState()
            return
        }

        val clampedDt = dt.coerceIn(0.001f, 0.05f)
        animTime += clampedDt

        if (invulnerableTimer > 0f) {
            invulnerableTimer = max(0f, invulnerableTimer - clampedDt)
        }

        if (collectHappyTimer > 0f) {
            collectHappyTimer = max(0f, collectHappyTimer - clampedDt)
        }

        // 1. Horizontal Physics
        if (moveInput != 0f) {
            facingRight = moveInput > 0
            val targetVx = moveInput * maxRunSpeed
            playerVx = if (playerVx < targetVx) {
                min(targetVx, playerVx + runAccel * clampedDt)
            } else {
                max(targetVx, playerVx - runAccel * clampedDt)
            }
        } else {
            // Decelerate to 0
            if (playerVx > 0f) {
                playerVx = max(0f, playerVx - runDecel * clampedDt)
            } else if (playerVx < 0f) {
                playerVx = min(0f, playerVx + runDecel * clampedDt)
            }
        }

        // 2. Jump Handling
        if (jumpRequested) {
            jumpRequested = false
            if (isGrounded) {
                playerVy = jumpVelocity
                isGrounded = false
                audioEngine.playJump()
                if (Random.nextFloat() < 0.25f) {
                    audioEngine.speak("Lompat!")
                }
                spawnParticles(playerX, playerY + playerHeight * 0.45f, 6, 0xFFE0E0E0)
            }
        }

        // 3. Gravity
        playerVy += gravity * clampedDt
        if (playerVy > 900f) playerVy = 900f // Terminal velocity

        // Integrate positions
        val prevX = playerX
        val prevY = playerY
        playerX += playerVx * clampedDt
        playerY += playerVy * clampedDt

        // Clamp inside level horizontal bounds
        playerX = playerX.coerceIn(30f, levelData.definition.levelWidth - 30f)

        // 4. Platform Collision Detection & Resolution
        isGrounded = false
        val halfW = playerWidth * 0.42f
        val footY = playerY + playerHeight * 0.48f
        val prevFootY = prevY + playerHeight * 0.48f

        for (platform in levelData.platforms) {
            // Check if player's horizontal footprint overlaps platform
            val overlapX = (playerX + halfW > platform.left) && (playerX - halfW < platform.right)
            if (overlapX) {
                // Landing on platform from above while moving down
                if (playerVy >= 0f && prevFootY <= platform.top + 8f && footY >= platform.top - 4f) {
                    playerY = platform.top - playerHeight * 0.48f
                    playerVy = 0f
                    isGrounded = true
                }
            }
        }

        // 5. Hazard & Pit check
        if (playerY > levelData.definition.levelHeight - 40f) {
            handlePlayerFallHazard()
        } else {
            for (hazard in levelData.hazards) {
                if (playerX + halfW > hazard.left && playerX - halfW < hazard.right &&
                    playerY + playerHeight * 0.4f > hazard.top && playerY - playerHeight * 0.4f < hazard.bottom
                ) {
                    handlePlayerFallHazard()
                    break
                }
            }
        }

        // 6. Collectible Pickup
        for (collectible in levelData.collectibles) {
            if (!collectible.collected) {
                val dist = hypot(playerX - collectible.x, playerY - collectible.y)
                if (dist < 42f) {
                    collectible.collected = true
                    score += collectible.points
                    itemsCollected++
                    collectHappyTimer = 0.4f

                    when (collectible.type) {
                        CollectibleType.STAR -> {
                            starsCollected++
                            audioEngine.playCollectStar()
                            spawnParticles(collectible.x, collectible.y, 16, 0xFFFFD54F)
                            if (starsCollected == 1 || starsCollected == 3 || starsCollected == 5) {
                                audioEngine.speak("Hebat! Dapat satu bintang!")
                            }
                        }

                        CollectibleType.WATER_DROPLET -> {
                            audioEngine.playCollectDroplet()
                            spawnParticles(collectible.x, collectible.y, 10, 0xFF29B6F6)
                        }

                        CollectibleType.SUNSHINE_TOKEN -> {
                            audioEngine.playCollectSun()
                            spawnParticles(collectible.x, collectible.y, 12, 0xFFFFEE58)
                        }

                        CollectibleType.SEED_BAG -> {
                            audioEngine.playCollectStar()
                            spawnParticles(collectible.x, collectible.y, 10, 0xFF81C784)
                        }
                    }
                }
            }
        }

        // 7. Checkpoints
        for (cp in levelData.checkpoints) {
            if (!cp.activated) {
                val dist = hypot(playerX - cp.x, playerY - cp.y)
                if (dist < 46f) {
                    cp.activated = true
                    respawnX = cp.x
                    respawnY = cp.y - 30f
                    if (cp.challenge != null) {
                        activeChallenge = cp.challenge
                    } else {
                        audioEngine.playCheckpoint()
                        spawnParticles(cp.x, cp.y - 60f, 15, 0xFF4CAF50)
                    }
                }
            }
        }

        // 8. Portal Finish
        val portal = levelData.portal
        if (!portal.reached) {
            val distToPortal = hypot(playerX - portal.centerX, playerY - portal.centerY)
            if (distToPortal < 55f) {
                portal.reached = true
                isLevelComplete = true
                animationState = AnimationState.CELEBRATING
                audioEngine.playVictory()
                audioEngine.speak("Syabas! Dunia seterusnya terbuka!")

                // Save stars and high score
                preferences.saveStarsForWorld(levelData.definition.worldNumber, starsCollected.coerceIn(1, 5))
                preferences.saveHighScoreForWorld(levelData.definition.worldNumber, score)
                preferences.unlockWorld(levelData.definition.worldNumber + 1)

                spawnParticles(portal.centerX, portal.centerY, 40, 0xFFFFD54F)
            }
        }

        // 9. Update Animation State
        if (isLevelComplete) {
            animationState = AnimationState.CELEBRATING
        } else if (collectHappyTimer > 0f) {
            animationState = AnimationState.COLLECTING
        } else if (!isGrounded) {
            animationState = if (playerVy < -20f) AnimationState.JUMPING else AnimationState.FALLING
        } else if (abs(playerVx) > 20f) {
            animationState = AnimationState.RUNNING
        } else {
            animationState = AnimationState.IDLE
        }

        // 10. Smooth Camera Follow
        val targetCamX = playerX - screenWidth * 0.38f
        val maxCamX = (levelData.definition.levelWidth - screenWidth).coerceAtLeast(0f)
        val clampedTarget = targetCamX.coerceIn(0f, maxCamX)
        cameraX += (clampedTarget - cameraX) * 0.12f

        updateParticles(clampedDt)
        emitState()
    }

    private fun handlePlayerFallHazard() {
        if (invulnerableTimer > 0f) return
        audioEngine.playHurt()
        lives--
        invulnerableTimer = 1.8f
        spawnParticles(playerX, playerY, 15, 0xFF0288D1)

        if (lives <= 0) {
            isGameOver = true
            audioEngine.speak("Cuba lagi, Biji!")
        } else {
            // Respawn at checkpoint
            playerX = respawnX
            playerY = respawnY
            playerVx = 0f
            playerVy = 0f
            audioEngine.speak("Jom, Biji!")
        }
    }

    private fun spawnParticles(x: Float, y: Float, count: Int, color: Long) {
        for (i in 0 until count) {
            val angle = Random.nextFloat() * 2.0 * Math.PI
            val speed = Random.nextFloat() * 220f + 50f
            particles.add(
                SparkleParticle(
                    x = x,
                    y = y,
                    vx = (cos(angle) * speed).toFloat(),
                    vy = (sin(angle) * speed).toFloat() - 40f,
                    life = Random.nextFloat() * 0.4f + 0.3f,
                    maxLife = 0.7f,
                    color = color,
                    size = Random.nextFloat() * 5f + 3f
                )
            )
        }
    }

    private fun updateParticles(dt: Float) {
        val iter = particles.iterator()
        while (iter.hasNext()) {
            val p = iter.next()
            p.life -= dt
            if (p.life <= 0f) {
                iter.remove()
            } else {
                p.x += p.vx * dt
                p.y += p.vy * dt
                p.vy += 400f * dt // gravity on sparkles
            }
        }
    }

    private fun emitState() {
        val progress = (playerX / levelData.definition.levelWidth).coerceIn(0f, 1f)
        _gameState.value = GameState(
            playerX = playerX,
            playerY = playerY,
            playerVx = playerVx,
            playerVy = playerVy,
            facingRight = facingRight,
            isGrounded = isGrounded,
            animationState = animationState,
            animTime = animTime,
            cameraX = cameraX,
            lives = lives,
            score = score,
            starsCollected = starsCollected,
            totalStars = levelData.definition.totalStars,
            itemsCollected = itemsCollected,
            isInvulnerable = invulnerableTimer > 0f,
            isPaused = isPaused,
            isGameOver = isGameOver,
            isLevelComplete = isLevelComplete,
            activeChallenge = activeChallenge,
            progressFraction = progress
        )
    }
}
