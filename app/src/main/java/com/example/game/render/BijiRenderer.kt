package com.example.game.render

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import com.example.game.model.AnimationState
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Procedural Vector Canvas renderer for Biji the seed character.
 * Renders smooth 60fps animations: Idle, Run, Jump, Fall, Collect, and Celebrate.
 */
object BijiRenderer {

    // Color definitions
    private val BodyColorBase = Color(0xFF8D5B4C)
    private val BodyColorHighlight = Color(0xFFB5846B)
    private val BodyColorShadow = Color(0xFF5D3A2C)
    private val SproutGreen = Color(0xFF43A047)
    private val SproutGreenLight = Color(0xFF7CB342)
    private val SproutGreenDark = Color(0xFF2E7D32)
    private val EyeWhite = Color(0xFFFFFFFF)
    private val EyePupil = Color(0xFF211510)
    private val CheekBlush = Color(0x66FF8A80)
    private val MouthDark = Color(0xFF3E2723)
    private val TonguePink = Color(0xFFFF5252)
    private val LimbColor = Color(0xFF794B3D)

    fun drawBiji(
        drawScope: DrawScope,
        centerX: Float,
        centerY: Float,
        state: AnimationState,
        animTime: Float,
        facingRight: Boolean,
        invulnerable: Boolean = false,
        size: Float = 58f
    ) {
        // Flicker if invulnerable
        if (invulnerable && ((animTime * 15).toInt() % 2 == 0)) {
            return
        }

        drawScope.withTransform({
            // Scale horizontal direction
            if (!facingRight) {
                scale(scaleX = -1f, scaleY = 1f, pivot = Offset(centerX, centerY))
            }
        }) {
            val radius = size / 2f

            // Animation transforms
            var bodyOffsetY = 0f
            var bodyScaleX = 1f
            var bodyScaleY = 1f
            var bodyRotation = 0f
            var leftLegAngle = 0f
            var rightLegAngle = 0f
            var leftArmAngle = 0f
            var rightArmAngle = 0f
            var sproutSway = 0f
            var eyeBlink = false
            var mouthState = "smile" // "smile", "open", "surprised"

            when (state) {
                AnimationState.IDLE -> {
                    // Gentle breathing and bobbing
                    bodyOffsetY = sin(animTime * 3.5f) * 2.5f
                    bodyScaleY = 1f + sin(animTime * 3.5f) * 0.03f
                    bodyScaleX = 1f - sin(animTime * 3.5f) * 0.02f
                    sproutSway = sin(animTime * 2.5f) * 8f
                    leftArmAngle = -10f + sin(animTime * 3.5f) * 5f
                    rightArmAngle = 10f - sin(animTime * 3.5f) * 5f
                    // Blink every 3.2 seconds
                    val blinkPhase = (animTime % 3.2f)
                    eyeBlink = blinkPhase < 0.16f
                }

                AnimationState.RUNNING -> {
                    // Running trot with legs and arms cycling
                    val runFreq = 16f
                    val legSwing = sin(animTime * runFreq) * 36f
                    leftLegAngle = legSwing
                    rightLegAngle = -legSwing
                    leftArmAngle = -legSwing * 0.8f
                    rightArmAngle = legSwing * 0.8f

                    // Bouncy body bob
                    bodyOffsetY = -abs(sin(animTime * runFreq)) * 5f
                    bodyRotation = 4f
                    bodyScaleX = 0.98f
                    sproutSway = -14f + sin(animTime * runFreq) * 10f
                    mouthState = "open"
                }

                AnimationState.JUMPING -> {
                    // Stretch vertically, arms raised up high
                    bodyScaleX = 0.92f
                    bodyScaleY = 1.10f
                    bodyOffsetY = -4f
                    leftArmAngle = -70f
                    rightArmAngle = -65f
                    leftLegAngle = -20f
                    rightLegAngle = 15f
                    sproutSway = 18f
                    mouthState = "open"
                }

                AnimationState.FALLING -> {
                    // Squash slightly, arms flailing upward
                    bodyScaleX = 1.08f
                    bodyScaleY = 0.94f
                    bodyOffsetY = 2f
                    leftArmAngle = -50f + sin(animTime * 20f) * 15f
                    rightArmAngle = -50f - sin(animTime * 20f) * 15f
                    leftLegAngle = 10f
                    rightLegAngle = -10f
                    sproutSway = -15f
                    mouthState = "surprised"
                }

                AnimationState.COLLECTING -> {
                    // Happy hop with big joyful eyes
                    bodyOffsetY = -8f * abs(sin(animTime * 12f))
                    bodyScaleY = 1.06f
                    leftArmAngle = -80f
                    rightArmAngle = -80f
                    sproutSway = sin(animTime * 18f) * 20f
                    mouthState = "open"
                }

                AnimationState.CELEBRATING -> {
                    // Victory dance!
                    val danceFreq = 10f
                    bodyOffsetY = -abs(sin(animTime * danceFreq)) * 12f
                    bodyRotation = sin(animTime * danceFreq) * 12f
                    leftArmAngle = -75f + sin(animTime * danceFreq) * 30f
                    rightArmAngle = -75f - sin(animTime * danceFreq) * 30f
                    leftLegAngle = sin(animTime * danceFreq) * 25f
                    rightLegAngle = -sin(animTime * danceFreq) * 25f
                    sproutSway = sin(animTime * 16f) * 30f
                    mouthState = "open"
                }
            }

            val bX = centerX
            val bY = centerY + bodyOffsetY

            // 1. Draw Legs
            drawLeg(this, bX - radius * 0.35f, bY + radius * 0.82f, leftLegAngle, radius * 0.38f)
            drawLeg(this, bX + radius * 0.35f, bY + radius * 0.82f, rightLegAngle, radius * 0.38f)

            // 2. Draw Back Arm
            drawArm(this, bX - radius * 0.85f, bY + radius * 0.15f, leftArmAngle, radius * 0.42f)

            // 3. Draw Seed Body (Round with 3D gradient highlight)
            withTransform({
                translate(left = bX, top = bY)
                rotate(degrees = bodyRotation)
                scale(scaleX = bodyScaleX, scaleY = bodyScaleY)
            }) {
                // Radial gradient for 3D sphere look
                val bodyBrush = Brush.radialGradient(
                    colors = listOf(BodyColorHighlight, BodyColorBase, BodyColorShadow),
                    center = Offset(-radius * 0.25f, -radius * 0.25f),
                    radius = radius * 1.3f
                )
                drawCircle(
                    brush = bodyBrush,
                    radius = radius,
                    center = Offset.Zero
                )

                // Body outline for crisp cartoon look
                drawCircle(
                    color = BodyColorShadow,
                    radius = radius,
                    center = Offset.Zero,
                    style = Stroke(width = 2.2f)
                )

                // Rosy Cheek Blushes
                drawCircle(
                    color = CheekBlush,
                    radius = radius * 0.22f,
                    center = Offset(-radius * 0.52f, radius * 0.18f)
                )
                drawCircle(
                    color = CheekBlush,
                    radius = radius * 0.22f,
                    center = Offset(radius * 0.52f, radius * 0.18f)
                )

                // Expressive Cartoon Eyes
                if (eyeBlink || state == AnimationState.COLLECTING) {
                    // Closed happy eye arcs (^ ^)
                    drawHappyEyeArc(this, -radius * 0.30f, -radius * 0.08f, radius * 0.18f)
                    drawHappyEyeArc(this, radius * 0.30f, -radius * 0.08f, radius * 0.18f)
                } else {
                    // Open big shiny cartoon eyes
                    drawExpressiveEye(this, -radius * 0.28f, -radius * 0.08f, radius * 0.23f)
                    drawExpressiveEye(this, radius * 0.28f, -radius * 0.08f, radius * 0.23f)
                }

                // Mouth
                drawMouth(this, 0f, radius * 0.28f, radius * 0.25f, mouthState)
            }

            // 4. Draw Front Arm
            drawArm(this, bX + radius * 0.85f, bY + radius * 0.15f, rightArmAngle, radius * 0.42f)

            // 5. Draw Sprout on Head (Two leaves with stem)
            drawSprout(this, bX, bY - radius * 0.95f, sproutSway, radius * 0.75f)
        }
    }

    private fun drawLeg(scope: DrawScope, x: Float, y: Float, angle: Float, length: Float) {
        scope.withTransform({
            translate(left = x, top = y)
            rotate(degrees = angle, pivot = Offset.Zero)
        }) {
            // Little rounded foot
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(0f, length)
                cubicTo(length * 0.5f, length + 4f, length * 0.8f, length + 2f, length * 0.7f, length - 2f)
                lineTo(length * 0.2f, 0f)
                close()
            }
            drawPath(path, color = LimbColor, style = Fill)
            drawPath(path, color = BodyColorShadow, style = Stroke(width = 1.8f))
        }
    }

    private fun drawArm(scope: DrawScope, x: Float, y: Float, angle: Float, length: Float) {
        scope.withTransform({
            translate(left = x, top = y)
            rotate(degrees = angle, pivot = Offset.Zero)
        }) {
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(length * 0.8f, length * 0.2f)
                // cute mitten hand
                cubicTo(length, length * 0.4f, length + 3f, length * 0.1f, length * 0.7f, -2f)
                close()
            }
            drawPath(path, color = LimbColor, style = Fill)
            drawPath(path, color = BodyColorShadow, style = Stroke(width = 1.8f))
        }
    }

    private fun drawExpressiveEye(scope: DrawScope, x: Float, y: Float, radius: Float) {
        // Eye Sclera (White)
        scope.drawCircle(
            color = EyeWhite,
            radius = radius,
            center = Offset(x, y)
        )
        scope.drawCircle(
            color = BodyColorShadow,
            radius = radius,
            center = Offset(x, y),
            style = Stroke(width = 1.8f)
        )

        // Pupil (Dark Brown)
        val pupilRadius = radius * 0.65f
        scope.drawCircle(
            color = EyePupil,
            radius = pupilRadius,
            center = Offset(x + radius * 0.12f, y + radius * 0.05f)
        )

        // Glossy Specular Twinkles
        scope.drawCircle(
            color = Color.White,
            radius = pupilRadius * 0.42f,
            center = Offset(x - pupilRadius * 0.2f, y - pupilRadius * 0.2f)
        )
        scope.drawCircle(
            color = Color.White,
            radius = pupilRadius * 0.20f,
            center = Offset(x + pupilRadius * 0.35f, y + pupilRadius * 0.25f)
        )
    }

    private fun drawHappyEyeArc(scope: DrawScope, x: Float, y: Float, size: Float) {
        val path = Path().apply {
            moveTo(x - size, y + size * 0.2f)
            quadraticTo(x, y - size * 0.8f, x + size, y + size * 0.2f)
        }
        scope.drawPath(path, color = EyePupil, style = Stroke(width = 3.2f))
    }

    private fun drawMouth(scope: DrawScope, x: Float, y: Float, width: Float, mouthState: String) {
        when (mouthState) {
            "open" -> {
                val path = Path().apply {
                    moveTo(x - width * 0.7f, y)
                    quadraticTo(x, y + width * 0.9f, x + width * 0.7f, y)
                    close()
                }
                scope.drawPath(path, color = MouthDark, style = Fill)
                // Cute Tongue
                val tongue = Path().apply {
                    moveTo(x - width * 0.4f, y + width * 0.4f)
                    quadraticTo(x, y + width * 0.9f, x + width * 0.4f, y + width * 0.4f)
                    close()
                }
                scope.drawPath(tongue, color = TonguePink, style = Fill)
                scope.drawPath(path, color = BodyColorShadow, style = Stroke(width = 1.8f))
            }

            "surprised" -> {
                scope.drawCircle(
                    color = MouthDark,
                    radius = width * 0.4f,
                    center = Offset(x, y + width * 0.3f)
                )
                scope.drawCircle(
                    color = BodyColorShadow,
                    radius = width * 0.4f,
                    center = Offset(x, y + width * 0.3f),
                    style = Stroke(width = 1.8f)
                )
            }

            else -> {
                // Cheerful Smile arc
                val smile = Path().apply {
                    moveTo(x - width * 0.6f, y)
                    quadraticTo(x, y + width * 0.45f, x + width * 0.6f, y)
                }
                scope.drawPath(smile, color = EyePupil, style = Stroke(width = 2.4f))
            }
        }
    }

    private fun drawSprout(scope: DrawScope, x: Float, y: Float, swayAngle: Float, size: Float) {
        scope.withTransform({
            translate(left = x, top = y)
            rotate(degrees = swayAngle, pivot = Offset.Zero)
        }) {
            // Little green stem
            val stemPath = Path().apply {
                moveTo(-size * 0.12f, 0f)
                cubicTo(-size * 0.15f, -size * 0.4f, -size * 0.08f, -size * 0.6f, 0f, -size * 0.65f)
                cubicTo(size * 0.08f, -size * 0.6f, size * 0.15f, -size * 0.4f, size * 0.12f, 0f)
                close()
            }
            drawPath(stemPath, color = SproutGreenDark, style = Fill)

            // Left Leaf
            withTransform({
                translate(left = 0f, top = -size * 0.65f)
                rotate(degrees = -38f + swayAngle * 0.4f, pivot = Offset.Zero)
            }) {
                val leafPath = Path().apply {
                    moveTo(0f, 0f)
                    cubicTo(-size * 0.4f, -size * 0.1f, -size * 0.6f, -size * 0.5f, -size * 0.85f, -size * 0.65f)
                    cubicTo(-size * 0.5f, -size * 0.95f, -size * 0.1f, -size * 0.6f, 0f, 0f)
                    close()
                }
                drawPath(leafPath, color = SproutGreen, style = Fill)
                drawPath(leafPath, color = SproutGreenDark, style = Stroke(width = 1.6f))
                // Leaf Vein
                val vein = Path().apply {
                    moveTo(0f, 0f)
                    quadraticTo(-size * 0.35f, -size * 0.4f, -size * 0.7f, -size * 0.58f)
                }
                drawPath(vein, color = SproutGreenLight, style = Stroke(width = 1.4f))
            }

            // Right Leaf
            withTransform({
                translate(left = 0f, top = -size * 0.65f)
                rotate(degrees = 38f + swayAngle * 0.4f, pivot = Offset.Zero)
            }) {
                val leafPath = Path().apply {
                    moveTo(0f, 0f)
                    cubicTo(size * 0.4f, -size * 0.1f, size * 0.6f, -size * 0.5f, size * 0.85f, -size * 0.65f)
                    cubicTo(size * 0.5f, -size * 0.95f, size * 0.1f, -size * 0.6f, 0f, 0f)
                    close()
                }
                drawPath(leafPath, color = SproutGreenLight, style = Fill)
                drawPath(leafPath, color = SproutGreenDark, style = Stroke(width = 1.6f))
                // Leaf Vein
                val vein = Path().apply {
                    moveTo(0f, 0f)
                    quadraticTo(size * 0.35f, -size * 0.4f, size * 0.7f, -size * 0.58f)
                }
                drawPath(vein, color = SproutGreen, style = Stroke(width = 1.4f))
            }
        }
    }
}
