package com.example.game.render

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import com.example.game.model.Checkpoint
import com.example.game.model.Collectible
import com.example.game.model.CollectibleType
import com.example.game.model.FinishPortal
import com.example.game.model.Hazard
import com.example.game.model.HazardType
import com.example.game.model.Platform
import com.example.game.model.PlatformType
import com.example.game.model.SparkleParticle
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object GameWorldRenderer {

    // Platform Colors
    private val GrassTop = Color(0xFF4CAF50)
    private val GrassLight = Color(0xFF81C784)
    private val GrassDark = Color(0xFF2E7D32)
    private val SoilBrown = Color(0xFF6D4C41)
    private val SoilBrownDark = Color(0xFF4E342E)
    private val WoodPlank = Color(0xFF8D6E63)
    private val WoodPlankDark = Color(0xFF5D4037)
    private val LilyGreen = Color(0xFF43A047)
    private val LilyGreenLight = Color(0xFF66BB6A)
    private val WaterColor = Color(0xCC0288D1)
    private val WaterFoam = Color(0xEEB3E5FC)

    // Collectible Colors
    private val StarGold = Color(0xFFFFD54F)
    private val StarGoldDark = Color(0xFFFFA000)
    private val DropletBlue = Color(0xFF29B6F6)
    private val DropletBlueDark = Color(0xFF0288D1)
    private val SunYellow = Color(0xFFFFEE58)
    private val SunAmber = Color(0xFFFF9800)

    fun drawPlatforms(drawScope: DrawScope, platforms: List<Platform>, cameraX: Float, screenWidth: Float) {
        val viewLeft = cameraX - 100f
        val viewRight = cameraX + screenWidth + 100f

        for (p in platforms) {
            // Frustum culling
            if (p.right < viewLeft || p.left > viewRight) continue

            when (p.type) {
                PlatformType.GRASS_SOIL -> drawGrassSoilPlatform(drawScope, p)
                PlatformType.FLOATING_WOOD -> drawWoodPlatform(drawScope, p)
                PlatformType.LILY_PAD -> drawLilyPadPlatform(drawScope, p)
                PlatformType.MOSS_ROOT -> drawMossRootPlatform(drawScope, p)
                PlatformType.CLOUD_STONE -> drawCloudPlatform(drawScope, p)
                PlatformType.CRYSTAL_PETAL -> drawCrystalPlatform(drawScope, p)
            }
        }
    }

    private fun drawGrassSoilPlatform(scope: DrawScope, p: Platform) {
        // Soil Body
        val soilBrush = Brush.verticalGradient(
            colors = listOf(SoilBrown, SoilBrownDark),
            startY = p.top,
            endY = p.bottom
        )
        scope.drawRoundRect(
            brush = soilBrush,
            topLeft = Offset(p.x, p.y + 14f),
            size = Size(p.width, (p.height - 14f).coerceAtLeast(10f)),
            cornerRadius = CornerRadius(10f, 10f)
        )

        // Grass Top Layer
        val grassBrush = Brush.verticalGradient(
            colors = listOf(GrassLight, GrassTop, GrassDark),
            startY = p.top,
            endY = p.top + 20f
        )
        scope.drawRoundRect(
            brush = grassBrush,
            topLeft = Offset(p.x, p.y),
            size = Size(p.width, 18f),
            cornerRadius = CornerRadius(8f, 8f)
        )

        // Little grass tufts along top edge
        var tuftX = p.x + 12f
        while (tuftX < p.right - 12f) {
            val tuftPath = Path().apply {
                moveTo(tuftX, p.top + 2f)
                lineTo(tuftX + 4f, p.top - 6f)
                lineTo(tuftX + 8f, p.top + 2f)
                close()
            }
            scope.drawPath(tuftPath, color = GrassLight)
            tuftX += 28f
        }
    }

    private fun drawWoodPlatform(scope: DrawScope, p: Platform) {
        val woodBrush = Brush.verticalGradient(
            colors = listOf(WoodPlank, WoodPlankDark),
            startY = p.top,
            endY = p.bottom
        )
        scope.drawRoundRect(
            brush = woodBrush,
            topLeft = Offset(p.x, p.y),
            size = Size(p.width, p.height),
            cornerRadius = CornerRadius(6f, 6f)
        )
        // Plank separators
        var plankX = p.x + 35f
        while (plankX < p.right - 10f) {
            scope.drawLine(
                color = WoodPlankDark,
                start = Offset(plankX, p.top + 2f),
                end = Offset(plankX, p.bottom - 2f),
                strokeWidth = 2.5f
            )
            plankX += 35f
        }
    }

    private fun drawLilyPadPlatform(scope: DrawScope, p: Platform) {
        val lilyBrush = Brush.radialGradient(
            colors = listOf(LilyGreenLight, LilyGreen),
            center = Offset(p.x + p.width / 2f, p.y + p.height / 2f),
            radius = p.width / 2f
        )
        scope.drawOval(
            brush = lilyBrush,
            topLeft = Offset(p.x, p.y),
            size = Size(p.width, p.height)
        )
        // Pad cutout line
        scope.drawLine(
            color = Color(0xFF2E7D32),
            start = Offset(p.x + p.width / 2f, p.y + p.height / 2f),
            end = Offset(p.x + p.width * 0.95f, p.y + p.height * 0.2f),
            strokeWidth = 2f
        )
    }

    private fun drawMossRootPlatform(scope: DrawScope, p: Platform) {
        val rootBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFF5D4037), Color(0xFF3E2723)),
            startY = p.top,
            endY = p.bottom
        )
        scope.drawRoundRect(
            brush = rootBrush,
            topLeft = Offset(p.x, p.y),
            size = Size(p.width, p.height),
            cornerRadius = CornerRadius(14f, 14f)
        )
        // Glowing moss on top
        scope.drawRoundRect(
            color = Color(0xFF00E676),
            topLeft = Offset(p.x + 8f, p.y),
            size = Size(p.width - 16f, 8f),
            cornerRadius = CornerRadius(6f, 6f)
        )
    }

    private fun drawCloudPlatform(scope: DrawScope, p: Platform) {
        val cloudBrush = Brush.verticalGradient(
            colors = listOf(Color(0xFFFFF9C4), Color(0xFFFFE082)),
            startY = p.top,
            endY = p.bottom
        )
        scope.drawRoundRect(
            brush = cloudBrush,
            topLeft = Offset(p.x, p.y),
            size = Size(p.width, p.height),
            cornerRadius = CornerRadius(16f, 16f)
        )
        scope.drawRoundRect(
            color = Color(0xFFFFF59D),
            topLeft = Offset(p.x + 4f, p.y + 2f),
            size = Size(p.width - 8f, 6f),
            cornerRadius = CornerRadius(8f, 8f)
        )
    }

    private fun drawCrystalPlatform(scope: DrawScope, p: Platform) {
        val petalBrush = Brush.linearGradient(
            colors = listOf(Color(0xFFF8BBD0), Color(0xFFEC407A)),
            start = Offset(p.x, p.y),
            end = Offset(p.right, p.bottom)
        )
        scope.drawRoundRect(
            brush = petalBrush,
            topLeft = Offset(p.x, p.y),
            size = Size(p.width, p.height),
            cornerRadius = CornerRadius(12f, 12f)
        )
        // Crystal sparkle border
        scope.drawRoundRect(
            color = Color.White,
            topLeft = Offset(p.x, p.y),
            size = Size(p.width, p.height),
            cornerRadius = CornerRadius(12f, 12f),
            style = Stroke(width = 2.2f)
        )
    }

    fun drawHazards(drawScope: DrawScope, hazards: List<Hazard>, animTime: Float, cameraX: Float, screenWidth: Float) {
        for (h in hazards) {
            if (h.right < cameraX - 50f || h.left > cameraX + screenWidth + 50f) continue
            when (h.type) {
                HazardType.WATER_GAP -> {
                    // Sparkling water with animated waves
                    drawScope.drawRect(
                        color = WaterColor,
                        topLeft = Offset(h.x, h.y),
                        size = Size(h.width, h.height)
                    )
                    // Animated wave tops
                    var waveX = h.x
                    val waveWidth = 24f
                    val waveOffset = (animTime * 40f) % waveWidth
                    while (waveX < h.right) {
                        val waveY = h.y + sin((waveX + animTime * 60f) * 0.1f) * 4f
                        drawScope.drawCircle(
                            color = WaterFoam,
                            radius = 6f,
                            center = Offset(waveX + waveOffset, waveY)
                        )
                        waveX += waveWidth
                    }
                }

                HazardType.THORN_BUSH -> {
                    // Thorny purple brambles
                    var thornX = h.x + 10f
                    while (thornX < h.right - 10f) {
                        val path = Path().apply {
                            moveTo(thornX, h.bottom)
                            lineTo(thornX + 8f, h.top)
                            lineTo(thornX + 16f, h.bottom)
                            close()
                        }
                        drawScope.drawPath(path, color = Color(0xFF4A148C))
                        thornX += 20f
                    }
                }
            }
        }
    }

    fun drawCollectibles(
        drawScope: DrawScope,
        collectibles: List<Collectible>,
        animTime: Float,
        cameraX: Float,
        screenWidth: Float
    ) {
        for (c in collectibles) {
            if (c.collected) continue
            if (c.x < cameraX - 60f || c.x > cameraX + screenWidth + 60f) continue

            // Gentle floating bob
            val bob = sin(animTime * 4f + c.x * 0.05f) * 6f
            val cY = c.y + bob

            when (c.type) {
                CollectibleType.STAR -> drawStar(drawScope, c.x, cY, animTime)
                CollectibleType.WATER_DROPLET -> drawWaterDroplet(drawScope, c.x, cY, animTime)
                CollectibleType.SUNSHINE_TOKEN -> drawSunshineToken(drawScope, c.x, cY, animTime)
                CollectibleType.SEED_BAG -> drawSeedBag(drawScope, c.x, cY, animTime)
            }
        }
    }

    private fun drawStar(scope: DrawScope, x: Float, y: Float, animTime: Float) {
        val radius = 18f
        // Sparkling rotating aura
        scope.drawCircle(
            color = Color(0x44FFD54F),
            radius = radius * 1.5f + sin(animTime * 6f) * 3f,
            center = Offset(x, y)
        )

        val starPath = Path().apply {
            val numPoints = 5
            val innerRadius = radius * 0.45f
            for (i in 0 until numPoints * 2) {
                val r = if (i % 2 == 0) radius else innerRadius
                val angle = (i * PI / numPoints - PI / 2).toFloat()
                val px = x + cos(angle) * r
                val py = y + sin(angle) * r
                if (i == 0) moveTo(px, py) else lineTo(px, py)
            }
            close()
        }
        scope.drawPath(starPath, color = StarGold, style = Fill)
        scope.drawPath(starPath, color = StarGoldDark, style = Stroke(width = 2f))

        // Cute smiling star face!
        scope.drawCircle(color = Color(0xFF3E2723), radius = 2f, center = Offset(x - 4f, y - 1f))
        scope.drawCircle(color = Color(0xFF3E2723), radius = 2f, center = Offset(x + 4f, y - 1f))
        val smile = Path().apply {
            moveTo(x - 3f, y + 3f)
            quadraticTo(x, y + 5.5f, x + 3f, y + 3f)
        }
        scope.drawPath(smile, color = Color(0xFF3E2723), style = Stroke(width = 1.6f))
    }

    private fun drawWaterDroplet(scope: DrawScope, x: Float, y: Float, animTime: Float) {
        val size = 18f
        scope.drawCircle(
            color = Color(0x3329B6F6),
            radius = size * 1.4f + sin(animTime * 5f) * 2f,
            center = Offset(x, y)
        )
        val dropletPath = Path().apply {
            moveTo(x, y - size)
            cubicTo(x + size * 0.8f, y, x + size * 0.8f, y + size * 0.8f, x, y + size)
            cubicTo(x - size * 0.8f, y + size * 0.8f, x - size * 0.8f, y, x, y - size)
            close()
        }
        scope.drawPath(dropletPath, color = DropletBlue, style = Fill)
        scope.drawPath(dropletPath, color = DropletBlueDark, style = Stroke(width = 1.8f))
        // Twinkle highlight
        scope.drawCircle(color = Color.White, radius = 3f, center = Offset(x - 4f, y - 2f))
    }

    private fun drawSunshineToken(scope: DrawScope, x: Float, y: Float, animTime: Float) {
        val radius = 16f
        // Rotating rays
        scope.withTransform({
            rotate(degrees = animTime * 45f, pivot = Offset(x, y))
        }) {
            for (i in 0 until 8) {
                val angle = (i * PI / 4.0).toFloat()
                val start = Offset(x + cos(angle) * (radius + 2f), y + sin(angle) * (radius + 2f))
                val end = Offset(x + cos(angle) * (radius + 9f), y + sin(angle) * (radius + 9f))
                drawLine(color = SunAmber, start = start, end = end, strokeWidth = 3f)
            }
        }
        scope.drawCircle(color = SunYellow, radius = radius, center = Offset(x, y))
        scope.drawCircle(color = SunAmber, radius = radius, center = Offset(x, y), style = Stroke(width = 1.8f))
        // Smiling face
        scope.drawCircle(color = Color(0xFF5D4037), radius = 2f, center = Offset(x - 4f, y - 1f))
        scope.drawCircle(color = Color(0xFF5D4037), radius = 2f, center = Offset(x + 4f, y - 1f))
        val smile = Path().apply {
            moveTo(x - 4f, y + 3f)
            quadraticTo(x, y + 6f, x + 4f, y + 3f)
        }
        scope.drawPath(smile, color = Color(0xFF5D4037), style = Stroke(width = 1.6f))
    }

    private fun drawSeedBag(scope: DrawScope, x: Float, y: Float, animTime: Float) {
        val size = 16f
        // Little seed pouch
        val pouchPath = Path().apply {
            moveTo(x - size * 0.8f, y + size)
            lineTo(x + size * 0.8f, y + size)
            quadraticTo(x + size, y - size * 0.2f, x + size * 0.5f, y - size * 0.7f)
            lineTo(x - size * 0.5f, y - size * 0.7f)
            quadraticTo(x - size, y - size * 0.2f, x - size * 0.8f, y + size)
            close()
        }
        scope.drawPath(pouchPath, color = Color(0xFF8D6E63), style = Fill)
        scope.drawPath(pouchPath, color = Color(0xFF5D4037), style = Stroke(width = 1.8f))

        // Tiny green sprout peeking out of bag
        val sproutPath = Path().apply {
            moveTo(x, y - size * 0.7f)
            lineTo(x, y - size * 1.3f)
        }
        scope.drawLine(color = Color(0xFF2E7D32), start = Offset(x, y - size * 0.7f), end = Offset(x, y - size * 1.3f), strokeWidth = 2.5f)
        scope.drawOval(color = Color(0xFF4CAF50), topLeft = Offset(x - 6f, y - size * 1.5f), size = Size(7f, 5f))
        scope.drawOval(color = Color(0xFF81C784), topLeft = Offset(x, y - size * 1.5f), size = Size(7f, 5f))
    }

    fun drawCheckpoints(drawScope: DrawScope, checkpoints: List<Checkpoint>, animTime: Float, cameraX: Float, screenWidth: Float) {
        for (cp in checkpoints) {
            if (cp.x < cameraX - 60f || cp.x > cameraX + screenWidth + 60f) continue

            // Wooden post
            drawScope.drawRect(
                color = Color(0xFF795548),
                topLeft = Offset(cp.x - 4f, cp.y - 65f),
                size = Size(8f, 65f)
            )

            if (cp.activated) {
                // Blooming flower flag and magical aura
                drawScope.drawCircle(
                    color = Color(0x554CAF50),
                    radius = 28f + sin(animTime * 6f) * 4f,
                    center = Offset(cp.x, cp.y - 75f)
                )
                // Blooming 4-petal flower
                val petalRadius = 9f
                val flowerCenter = Offset(cp.x, cp.y - 75f)
                val flowerColors = listOf(Color(0xFFE91E63), Color(0xFFFF4081))
                drawScope.drawCircle(color = flowerColors[0], radius = petalRadius, center = flowerCenter + Offset(0f, -petalRadius))
                drawScope.drawCircle(color = flowerColors[0], radius = petalRadius, center = flowerCenter + Offset(0f, petalRadius))
                drawScope.drawCircle(color = flowerColors[1], radius = petalRadius, center = flowerCenter + Offset(-petalRadius, 0f))
                drawScope.drawCircle(color = flowerColors[1], radius = petalRadius, center = flowerCenter + Offset(petalRadius, 0f))
                drawScope.drawCircle(color = Color(0xFFFFD54F), radius = petalRadius * 0.75f, center = flowerCenter)
            } else {
                // Wooden sign with question mark / seed badge
                drawScope.drawRoundRect(
                    color = Color(0xFFFFA726),
                    topLeft = Offset(cp.x - 22f, cp.y - 80f),
                    size = Size(44f, 30f),
                    cornerRadius = CornerRadius(6f, 6f)
                )
                drawScope.drawRoundRect(
                    color = Color(0xFFE65100),
                    topLeft = Offset(cp.x - 22f, cp.y - 80f),
                    size = Size(44f, 30f),
                    cornerRadius = CornerRadius(6f, 6f),
                    style = Stroke(width = 2f)
                )
                // Sprout icon on sign
                drawScope.drawCircle(color = Color(0xFF4CAF50), radius = 5f, center = Offset(cp.x - 4f, cp.y - 65f))
                drawScope.drawCircle(color = Color(0xFF81C784), radius = 5f, center = Offset(cp.x + 4f, cp.y - 65f))
            }
        }
    }

    fun drawFinishPortal(drawScope: DrawScope, portal: FinishPortal, animTime: Float, cameraX: Float, screenWidth: Float) {
        if (portal.centerX < cameraX - 100f || portal.centerX > cameraX + screenWidth + 100f) return

        val cx = portal.centerX
        val cy = portal.centerY

        // Outer Magical Vortex Glow
        val pulse = sin(animTime * 5f) * 6f
        val portalBrush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFE1BEE7),
                Color(0xFFBA68C8),
                Color(0xFF8E24AA),
                Color(0x008E24AA)
            ),
            center = Offset(cx, cy),
            radius = 65f + pulse
        )
        drawScope.drawCircle(brush = portalBrush, radius = 65f + pulse, center = Offset(cx, cy))

        // Rotating Rainbow Ring
        drawScope.withTransform({
            rotate(degrees = animTime * 60f, pivot = Offset(cx, cy))
        }) {
            drawCircle(
                color = Color(0xFFFFD54F),
                radius = 42f,
                center = Offset(cx, cy),
                style = Stroke(width = 5f)
            )
            // Orbiting star crystals
            for (i in 0 until 6) {
                val angle = (i * PI / 3.0).toFloat()
                val sx = cx + cos(angle) * 42f
                val sy = cy + sin(angle) * 42f
                drawCircle(color = Color.White, radius = 4f, center = Offset(sx, sy))
            }
        }

        // Inner glowing core
        drawScope.drawCircle(
            color = Color(0xFFEDE7F6),
            radius = 24f + sin(animTime * 8f) * 3f,
            center = Offset(cx, cy)
        )

        // Portal flower arch base
        val archPath = Path().apply {
            moveTo(cx - 38f, cy + 45f)
            quadraticTo(cx, cy - 55f, cx + 38f, cy + 45f)
        }
        drawScope.drawPath(archPath, color = Color(0xFF6A1B9A), style = Stroke(width = 6f))
    }

    fun drawSparkles(drawScope: DrawScope, particles: List<SparkleParticle>) {
        for (p in particles) {
            val alpha = (p.life / p.maxLife).coerceIn(0f, 1f)
            val color = Color(p.color).copy(alpha = alpha)
            drawScope.drawCircle(
                color = color,
                radius = p.size * alpha,
                center = Offset(p.x, p.y)
            )
        }
    }
}
