package com.example.game.model

enum class AnimationState {
    IDLE,
    RUNNING,
    JUMPING,
    FALLING,
    COLLECTING,
    CELEBRATING
}

enum class CollectibleType {
    STAR,
    WATER_DROPLET,
    SUNSHINE_TOKEN,
    SEED_BAG
}

enum class PlatformType {
    GRASS_SOIL,
    FLOATING_WOOD,
    LILY_PAD,
    MOSS_ROOT,
    CLOUD_STONE,
    CRYSTAL_PETAL
}

enum class HazardType {
    WATER_GAP,
    THORN_BUSH
}

data class ChallengeOption(
    val text: String,
    val subtext: String = "",
    val badge: String = ""
)

data class EducationalChallenge(
    val id: String,
    val title: String,
    val question: String,
    val hint: String,
    val options: List<ChallengeOption>,
    val correctIndex: Int,
    val explanation: String
)

data class Platform(
    val id: String,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val type: PlatformType = PlatformType.GRASS_SOIL
) {
    val left: Float get() = x
    val right: Float get() = x + width
    val top: Float get() = y
    val bottom: Float get() = y + height
}

data class Collectible(
    val id: String,
    val x: Float,
    val y: Float,
    val type: CollectibleType,
    var collected: Boolean = false,
    val points: Int = 100
)

data class Checkpoint(
    val id: String,
    val x: Float,
    val y: Float,
    var activated: Boolean = false,
    val challenge: EducationalChallenge? = null
)

data class FinishPortal(
    val x: Float,
    val y: Float,
    val width: Float = 70f,
    val height: Float = 110f,
    var reached: Boolean = false
) {
    val centerX: Float get() = x + width / 2f
    val centerY: Float get() = y + height / 2f
}

data class Hazard(
    val id: String,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val type: HazardType = HazardType.WATER_GAP
) {
    val left: Float get() = x
    val right: Float get() = x + width
    val top: Float get() = y
    val bottom: Float get() = y + height
}

data class SparkleParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var life: Float,
    val maxLife: Float,
    val color: Long,
    val size: Float
)

data class WorldDefinition(
    val worldNumber: Int,
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val backgroundRes: Int,
    val levelWidth: Float = 3200f,
    val levelHeight: Float = 900f,
    val primaryColor: Long,
    val secondaryColor: Long,
    val mainCollectibleType: CollectibleType,
    val totalStars: Int = 5,
    val startX: Float = 100f,
    val startY: Float = 550f
)
