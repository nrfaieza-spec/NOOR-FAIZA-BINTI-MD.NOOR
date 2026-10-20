package com.example.game.data

import com.example.R
import com.example.game.model.ChallengeOption
import com.example.game.model.Checkpoint
import com.example.game.model.Collectible
import com.example.game.model.CollectibleType
import com.example.game.model.EducationalChallenge
import com.example.game.model.FinishPortal
import com.example.game.model.Hazard
import com.example.game.model.HazardType
import com.example.game.model.Platform
import com.example.game.model.PlatformType
import com.example.game.model.WorldDefinition

data class WorldLevelData(
    val definition: WorldDefinition,
    val platforms: List<Platform>,
    val collectibles: List<Collectible>,
    val hazards: List<Hazard>,
    val checkpoints: List<Checkpoint>,
    val portal: FinishPortal
)

object GameWorldData {

    val worlds = listOf(
        WorldDefinition(
            worldNumber = 1,
            id = "world_1",
            title = "Taman Benih Ajaib",
            subtitle = "Dunia 1: Pengenalan Biji & Bunga",
            description = "Biji memulakan pengembaraan di taman yang ceria! Kumpul 5 bintang emas dan pelajari struktur biji benih.",
            backgroundRes = R.drawable.bg_world_garden,
            levelWidth = 3200f,
            levelHeight = 900f,
            primaryColor = 0xFF2E7D32,
            secondaryColor = 0xFF81C784,
            mainCollectibleType = CollectibleType.STAR,
            totalStars = 5,
            startX = 120f,
            startY = 580f
        ),
        WorldDefinition(
            worldNumber = 2,
            id = "world_2",
            title = "Sungai Titisan Air",
            subtitle = "Dunia 2: Kepentingan Air Untuk Pokok",
            description = "Lompat merentasi daun teratai di atas sungai jernih! Kumpul titisan air dan pelajari rahsia hidrasi tumbuhan.",
            backgroundRes = R.drawable.bg_world_river,
            levelWidth = 3400f,
            levelHeight = 900f,
            primaryColor = 0xFF0277BD,
            secondaryColor = 0xFF4FC3F7,
            mainCollectibleType = CollectibleType.WATER_DROPLET,
            totalStars = 5,
            startX = 120f,
            startY = 580f
        ),
        WorldDefinition(
            worldNumber = 3,
            id = "world_3",
            title = "Hutan Akar Ajaib",
            subtitle = "Dunia 3: Rahsia Percambahan Di Bawah Tanah",
            description = "Terokai terowong akar bergemerlapan dan cendawan bercahaya! Susun urutan bagaimana biji benih bercambah.",
            backgroundRes = R.drawable.bg_world_roots,
            levelWidth = 3300f,
            levelHeight = 900f,
            primaryColor = 0xFF6D4C41,
            secondaryColor = 0xFFFFB74D,
            mainCollectibleType = CollectibleType.SEED_BAG,
            totalStars = 5,
            startX = 120f,
            startY = 580f
        ),
        WorldDefinition(
            worldNumber = 4,
            id = "world_4",
            title = "Bukit Cahaya Matahari",
            subtitle = "Dunia 4: Tenaga Matahari & Fotosintesis",
            description = "Mendaki bukit keemasan di bawah sinaran matahari! Kumpul token cahaya dan pelajari keperluan asas pokok.",
            backgroundRes = R.drawable.bg_world_sunshine,
            levelWidth = 3500f,
            levelHeight = 900f,
            primaryColor = 0xFFF57F17,
            secondaryColor = 0xFFFFF176,
            mainCollectibleType = CollectibleType.SUNSHINE_TOKEN,
            totalStars = 5,
            startX = 120f,
            startY = 580f
        ),
        WorldDefinition(
            worldNumber = 5,
            id = "world_5",
            title = "Istana Bunga Mekar",
            subtitle = "Dunia 5: Bunga Mekar & Selamatkan Taman",
            description = "Kemuncak misi di Istana Bunga Ajaib! Lengkapkan kitaran hidup tumbuhan untuk membuka bunga gergasi!",
            backgroundRes = R.drawable.bg_world_palace,
            levelWidth = 3600f,
            levelHeight = 900f,
            primaryColor = 0xFFC2185B,
            secondaryColor = 0xFFF48FB1,
            mainCollectibleType = CollectibleType.STAR,
            totalStars = 5,
            startX = 120f,
            startY = 580f
        )
    )

    fun getLevelData(worldNumber: Int): WorldLevelData {
        val def = worlds.firstOrNull { it.worldNumber == worldNumber } ?: worlds[0]
        return when (worldNumber) {
            1 -> createWorld1(def)
            2 -> createWorld2(def)
            3 -> createWorld3(def)
            4 -> createWorld4(def)
            5 -> createWorld5(def)
            else -> createWorld1(def)
        }
    }

    private fun createWorld1(def: WorldDefinition): WorldLevelData {
        val platforms = mutableListOf<Platform>()
        val collectibles = mutableListOf<Collectible>()
        val hazards = mutableListOf<Hazard>()
        val checkpoints = mutableListOf<Checkpoint>()

        // Main Ground Segments with safe gaps
        platforms.add(Platform("p1", 0f, 650f, 600f, 250f, PlatformType.GRASS_SOIL))
        platforms.add(Platform("p2", 700f, 650f, 650f, 250f, PlatformType.GRASS_SOIL))
        platforms.add(Platform("p3", 1450f, 650f, 750f, 250f, PlatformType.GRASS_SOIL))
        platforms.add(Platform("p4", 2300f, 650f, 900f, 250f, PlatformType.GRASS_SOIL))

        // Floating stepped platforms
        platforms.add(Platform("f1", 250f, 520f, 150f, 36f, PlatformType.GRASS_SOIL))
        platforms.add(Platform("f2", 450f, 430f, 140f, 36f, PlatformType.GRASS_SOIL))

        // Bridge over gap 1
        platforms.add(Platform("b1", 600f, 590f, 100f, 28f, PlatformType.FLOATING_WOOD))

        // Mid section elevation
        platforms.add(Platform("f3", 850f, 520f, 160f, 36f, PlatformType.GRASS_SOIL))
        platforms.add(Platform("f4", 1080f, 440f, 180f, 36f, PlatformType.GRASS_SOIL))
        platforms.add(Platform("f5", 1320f, 530f, 130f, 36f, PlatformType.FLOATING_WOOD))

        // Floating stepping stones near checkpoint 2
        platforms.add(Platform("f6", 1620f, 510f, 150f, 36f, PlatformType.GRASS_SOIL))
        platforms.add(Platform("f7", 1850f, 420f, 170f, 36f, PlatformType.GRASS_SOIL))
        platforms.add(Platform("f8", 2100f, 490f, 150f, 36f, PlatformType.GRASS_SOIL))

        // Final castle approach
        platforms.add(Platform("f9", 2550f, 520f, 180f, 36f, PlatformType.GRASS_SOIL))
        platforms.add(Platform("f10", 2800f, 440f, 180f, 36f, PlatformType.GRASS_SOIL))

        // Hazards: Water pits between ground segments
        hazards.add(Hazard("h1", 600f, 800f, 100f, 100f, HazardType.WATER_GAP))
        hazards.add(Hazard("h2", 1350f, 800f, 100f, 100f, HazardType.WATER_GAP))
        hazards.add(Hazard("h3", 2200f, 800f, 100f, 100f, HazardType.WATER_GAP))

        // 5 Golden Stars
        collectibles.add(Collectible("s1", 480f, 370f, CollectibleType.STAR, points = 200))
        collectibles.add(Collectible("s2", 1120f, 380f, CollectibleType.STAR, points = 200))
        collectibles.add(Collectible("s3", 1880f, 360f, CollectibleType.STAR, points = 200))
        collectibles.add(Collectible("s4", 2130f, 430f, CollectibleType.STAR, points = 200))
        collectibles.add(Collectible("s5", 2840f, 380f, CollectibleType.STAR, points = 200))

        // Extra seeds along the way
        collectibles.add(Collectible("sd1", 280f, 470f, CollectibleType.SEED_BAG, points = 50))
        collectibles.add(Collectible("sd2", 890f, 470f, CollectibleType.SEED_BAG, points = 50))
        collectibles.add(Collectible("sd3", 1660f, 460f, CollectibleType.SEED_BAG, points = 50))
        collectibles.add(Collectible("sd4", 2590f, 470f, CollectibleType.SEED_BAG, points = 50))

        // Mid-level Checkpoint with Educational Challenge
        val challenge1 = EducationalChallenge(
            id = "c1",
            title = "Kenal Pasti Bahagian Biji Benih",
            question = "Apakah bahagian biji benih yang bertindak menyimpan makanan untuk anak benih membesar?",
            hint = "Bahagian ini mengandungi nutrien yang banyak untuk benih baru!",
            options = listOf(
                ChallengeOption("Kulit Biji (Testa)", "Lapisan luar yang melindungi biji", "🛡️"),
                ChallengeOption("Kotiledon", "Menyimpan makanan untuk benih bertunas", "🥪"),
                ChallengeOption("Akar Sahaja", "Menyerap air dari tanah", "🌱")
            ),
            correctIndex = 1,
            explanation = "Bagus sekali! Kotiledon membekalkan nutrien dan makanan penting sehinggalah daun hijau mula berkembang!"
        )
        checkpoints.add(Checkpoint("cp1", 1500f, 570f, false, challenge1))

        val portal = FinishPortal(x = 3000f, y = 540f)

        return WorldLevelData(def, platforms, collectibles, hazards, checkpoints, portal)
    }

    private fun createWorld2(def: WorldDefinition): WorldLevelData {
        val platforms = mutableListOf<Platform>()
        val collectibles = mutableListOf<Collectible>()
        val hazards = mutableListOf<Hazard>()
        val checkpoints = mutableListOf<Checkpoint>()

        // River World: Lots of lily pads, wooden docks, water river below
        platforms.add(Platform("p1", 0f, 650f, 450f, 250f, PlatformType.GRASS_SOIL))

        // Lily pads across river
        platforms.add(Platform("lp1", 520f, 640f, 120f, 30f, PlatformType.LILY_PAD))
        platforms.add(Platform("lp2", 700f, 610f, 130f, 30f, PlatformType.LILY_PAD))
        platforms.add(Platform("lp3", 900f, 580f, 130f, 30f, PlatformType.LILY_PAD))

        // River Island
        platforms.add(Platform("p2", 1100f, 620f, 500f, 280f, PlatformType.GRASS_SOIL))
        platforms.add(Platform("f1", 1250f, 490f, 150f, 32f, PlatformType.FLOATING_WOOD))
        platforms.add(Platform("f2", 1450f, 400f, 140f, 32f, PlatformType.FLOATING_WOOD))

        // Floating bridge
        platforms.add(Platform("lp4", 1680f, 620f, 130f, 30f, PlatformType.LILY_PAD))
        platforms.add(Platform("lp5", 1880f, 590f, 140f, 30f, PlatformType.LILY_PAD))
        platforms.add(Platform("lp6", 2090f, 570f, 130f, 30f, PlatformType.LILY_PAD))

        // Waterfall bank
        platforms.add(Platform("p3", 2280f, 610f, 500f, 290f, PlatformType.GRASS_SOIL))
        platforms.add(Platform("f3", 2420f, 480f, 160f, 32f, PlatformType.FLOATING_WOOD))
        platforms.add(Platform("f4", 2650f, 420f, 170f, 32f, PlatformType.FLOATING_WOOD))

        // Destination shore
        platforms.add(Platform("lp7", 2880f, 610f, 140f, 30f, PlatformType.LILY_PAD))
        platforms.add(Platform("p4", 3080f, 620f, 350f, 280f, PlatformType.GRASS_SOIL))

        // River hazard along the entire bottom gaps
        hazards.add(Hazard("h1", 450f, 750f, 650f, 150f, HazardType.WATER_GAP))
        hazards.add(Hazard("h2", 1600f, 750f, 680f, 150f, HazardType.WATER_GAP))
        hazards.add(Hazard("h3", 2780f, 750f, 300f, 150f, HazardType.WATER_GAP))

        // 5 Stars
        collectibles.add(Collectible("s1", 720f, 540f, CollectibleType.STAR, points = 200))
        collectibles.add(Collectible("s2", 1480f, 340f, CollectibleType.STAR, points = 200))
        collectibles.add(Collectible("s3", 1910f, 520f, CollectibleType.STAR, points = 200))
        collectibles.add(Collectible("s4", 2680f, 360f, CollectibleType.STAR, points = 200))
        collectibles.add(Collectible("s5", 2910f, 540f, CollectibleType.STAR, points = 200))

        // Sparkling water droplets
        collectibles.add(Collectible("w1", 540f, 570f, CollectibleType.WATER_DROPLET, points = 75))
        collectibles.add(Collectible("w2", 920f, 510f, CollectibleType.WATER_DROPLET, points = 75))
        collectibles.add(Collectible("w3", 1280f, 420f, CollectibleType.WATER_DROPLET, points = 75))
        collectibles.add(Collectible("w4", 1700f, 550f, CollectibleType.WATER_DROPLET, points = 75))
        collectibles.add(Collectible("w5", 2110f, 500f, CollectibleType.WATER_DROPLET, points = 75))
        collectibles.add(Collectible("w6", 2450f, 410f, CollectibleType.WATER_DROPLET, points = 75))

        val challenge2 = EducationalChallenge(
            id = "c2",
            title = "Mengapa Tumbuhan Perlukan Air?",
            question = "Bagaimanakah air membantu tumbuhan kekal subur dan tegak?",
            hint = "Air membawa zat penting dari dalam tanah ke daun!",
            options = listOf(
                ChallengeOption("Membasuh Kotoran Sahaja", "Untuk kelihatan berkilat", "🧼"),
                ChallengeOption("Menyerap Nutrien & Kekal Segar", "Membawa zat mineral dan mengekalkan kesegaran sel", "💧"),
                ChallengeOption("Supaya Boleh Berenang", "Tumbuhan tidak berenang", "🏊")
            ),
            correctIndex = 1,
            explanation = "Hebat! Air menyerap nutrien daripada tanah dan membolehkan tumbuhan melakukan proses fotosintesis dengan baik!"
        )
        checkpoints.add(Checkpoint("cp2", 1200f, 540f, false, challenge2))

        val portal = FinishPortal(x = 3200f, y = 510f)

        return WorldLevelData(def, platforms, collectibles, hazards, checkpoints, portal)
    }

    private fun createWorld3(def: WorldDefinition): WorldLevelData {
        val platforms = mutableListOf<Platform>()
        val collectibles = mutableListOf<Collectible>()
        val hazards = mutableListOf<Hazard>()
        val checkpoints = mutableListOf<Checkpoint>()

        // Root World: Winding tree roots, giant mossy branches, soil caves
        platforms.add(Platform("p1", 0f, 650f, 500f, 250f, PlatformType.MOSS_ROOT))

        // Root climbs
        platforms.add(Platform("r1", 380f, 530f, 150f, 32f, PlatformType.MOSS_ROOT))
        platforms.add(Platform("r2", 580f, 440f, 170f, 32f, PlatformType.MOSS_ROOT))
        platforms.add(Platform("r3", 800f, 360f, 180f, 32f, PlatformType.MOSS_ROOT))

        // Ground cavern floor
        platforms.add(Platform("p2", 750f, 660f, 600f, 240f, PlatformType.GRASS_SOIL))
        platforms.add(Platform("r4", 1100f, 520f, 160f, 32f, PlatformType.MOSS_ROOT))

        // Massive underground root bridge
        platforms.add(Platform("r5", 1400f, 600f, 220f, 36f, PlatformType.MOSS_ROOT))
        platforms.add(Platform("r6", 1680f, 510f, 180f, 34f, PlatformType.MOSS_ROOT))
        platforms.add(Platform("r7", 1920f, 410f, 170f, 34f, PlatformType.MOSS_ROOT))

        // Sanctuary hill
        platforms.add(Platform("p3", 1850f, 650f, 550f, 250f, PlatformType.GRASS_SOIL))
        platforms.add(Platform("r8", 2250f, 520f, 160f, 34f, PlatformType.MOSS_ROOT))
        platforms.add(Platform("r9", 2480f, 430f, 180f, 34f, PlatformType.MOSS_ROOT))

        // Exit gateway
        platforms.add(Platform("p4", 2700f, 640f, 600f, 260f, PlatformType.MOSS_ROOT))

        // Hazards: underground spike thorns
        hazards.add(Hazard("h1", 500f, 780f, 250f, 120f, HazardType.THORN_BUSH))
        hazards.add(Hazard("h2", 1350f, 780f, 500f, 120f, HazardType.THORN_BUSH))
        hazards.add(Hazard("h3", 2400f, 780f, 300f, 120f, HazardType.THORN_BUSH))

        // 5 Stars
        collectibles.add(Collectible("s1", 840f, 300f, CollectibleType.STAR, points = 200))
        collectibles.add(Collectible("s2", 1140f, 450f, CollectibleType.STAR, points = 200))
        collectibles.add(Collectible("s3", 1950f, 350f, CollectibleType.STAR, points = 200))
        collectibles.add(Collectible("s4", 2510f, 360f, CollectibleType.STAR, points = 200))
        collectibles.add(Collectible("s5", 2850f, 550f, CollectibleType.STAR, points = 200))

        // Sprouting Seed bags
        collectibles.add(Collectible("sd1", 410f, 470f, CollectibleType.SEED_BAG, points = 60))
        collectibles.add(Collectible("sd2", 610f, 380f, CollectibleType.SEED_BAG, points = 60))
        collectibles.add(Collectible("sd3", 1440f, 540f, CollectibleType.SEED_BAG, points = 60))
        collectibles.add(Collectible("sd4", 1720f, 450f, CollectibleType.SEED_BAG, points = 60))
        collectibles.add(Collectible("sd5", 2280f, 460f, CollectibleType.SEED_BAG, points = 60))

        val challenge3 = EducationalChallenge(
            id = "c3",
            title = "Urutan Percambahan Biji Benih",
            question = "Manakah urutan yang BETUL semasa biji benih mula bercambah?",
            hint = "Akar tumbuh ke bawah dahulu untuk mencari air, sebelum pucuk naik ke atas!",
            options = listOf(
                ChallengeOption("Bunga Mekar -> Daun Gugur -> Biji", "Ini bukan urutan bertunas", "❌"),
                ChallengeOption("Akar Keluar -> Pucuk Tumbuh -> Daun Berkembang", "Akar keluar dahulu menyerap air, diikuti pucuk hijau", "🌱"),
                ChallengeOption("Daun Keluar -> Biji Hilang -> Akar Tumbuh", "Urutan terbalik", "🔄")
            ),
            correctIndex = 1,
            explanation = "Tepat sekali! Semasa percambahan, akar akan keluar dahulu untuk mencengkam tanah, kemudian barulah pucuk menolak tanah ke atas!"
        )
        checkpoints.add(Checkpoint("cp3", 950f, 580f, false, challenge3))

        val portal = FinishPortal(x = 3100f, y = 530f)

        return WorldLevelData(def, platforms, collectibles, hazards, checkpoints, portal)
    }

    private fun createWorld4(def: WorldDefinition): WorldLevelData {
        val platforms = mutableListOf<Platform>()
        val collectibles = mutableListOf<Collectible>()
        val hazards = mutableListOf<Hazard>()
        val checkpoints = mutableListOf<Checkpoint>()

        // Sunshine Hilltops: high platforms, cloud stepping stones, sunny tokens
        platforms.add(Platform("p1", 0f, 650f, 500f, 250f, PlatformType.GRASS_SOIL))

        // Stepping sun stones
        platforms.add(Platform("c1", 420f, 520f, 150f, 30f, PlatformType.CLOUD_STONE))
        platforms.add(Platform("c2", 640f, 430f, 160f, 30f, PlatformType.CLOUD_STONE))
        platforms.add(Platform("c3", 880f, 340f, 170f, 30f, PlatformType.CLOUD_STONE))

        // Hill 1
        platforms.add(Platform("p2", 1100f, 580f, 550f, 320f, PlatformType.GRASS_SOIL))
        platforms.add(Platform("c4", 1300f, 450f, 150f, 30f, PlatformType.CLOUD_STONE))
        platforms.add(Platform("c5", 1520f, 360f, 160f, 30f, PlatformType.CLOUD_STONE))

        // Sun bridge
        platforms.add(Platform("c6", 1750f, 440f, 150f, 30f, PlatformType.CLOUD_STONE))
        platforms.add(Platform("c7", 1970f, 510f, 160f, 30f, PlatformType.CLOUD_STONE))

        // Hill 2
        platforms.add(Platform("p3", 2180f, 620f, 550f, 280f, PlatformType.GRASS_SOIL))
        platforms.add(Platform("c8", 2400f, 480f, 160f, 30f, PlatformType.CLOUD_STONE))
        platforms.add(Platform("c9", 2630f, 390f, 160f, 30f, PlatformType.CLOUD_STONE))
        platforms.add(Platform("c10", 2860f, 470f, 150f, 30f, PlatformType.CLOUD_STONE))

        // Golden Plateau
        platforms.add(Platform("p4", 3050f, 600f, 500f, 300f, PlatformType.GRASS_SOIL))

        // Hazards: Deep valley drops
        hazards.add(Hazard("h1", 500f, 800f, 600f, 100f, HazardType.WATER_GAP))
        hazards.add(Hazard("h2", 1650f, 800f, 530f, 100f, HazardType.WATER_GAP))
        hazards.add(Hazard("h3", 2730f, 800f, 320f, 100f, HazardType.WATER_GAP))

        // 5 Stars
        collectibles.add(Collectible("s1", 920f, 280f, CollectibleType.STAR, points = 200))
        collectibles.add(Collectible("s2", 1550f, 300f, CollectibleType.STAR, points = 200))
        collectibles.add(Collectible("s3", 1990f, 450f, CollectibleType.STAR, points = 200))
        collectibles.add(Collectible("s4", 2660f, 320f, CollectibleType.STAR, points = 200))
        collectibles.add(Collectible("s5", 3250f, 520f, CollectibleType.STAR, points = 200))

        // Sunshine Tokens
        collectibles.add(Collectible("sn1", 450f, 460f, CollectibleType.SUNSHINE_TOKEN, points = 80))
        collectibles.add(Collectible("sn2", 670f, 370f, CollectibleType.SUNSHINE_TOKEN, points = 80))
        collectibles.add(Collectible("sn3", 1330f, 390f, CollectibleType.SUNSHINE_TOKEN, points = 80))
        collectibles.add(Collectible("sn4", 1780f, 380f, CollectibleType.SUNSHINE_TOKEN, points = 80))
        collectibles.add(Collectible("sn5", 2430f, 420f, CollectibleType.SUNSHINE_TOKEN, points = 80))
        collectibles.add(Collectible("sn6", 2890f, 410f, CollectibleType.SUNSHINE_TOKEN, points = 80))

        val challenge4 = EducationalChallenge(
            id = "c4",
            title = "Keperluan Asas Tumbuhan",
            question = "Apakah 4 keperluan asas penting untuk biji benih membesar menjadi pokok yang sihat?",
            hint = "Ingat: Pokok perlukan cahaya untuk fotosintesis dan air untuk hidup!",
            options = listOf(
                ChallengeOption("Coklat, Jus, Mainan & Angin", "Bukan makanan tumbuhan!", "🍭"),
                ChallengeOption("Air, Udara, Cahaya Matahari & Nutrien", "Empat sumber kehidupan utama tumbuhan", "☀️"),
                ChallengeOption("Hujan Batu & Minyak Sahaja", "Boleh merosakkan tanaman", "⚠️")
            ),
            correctIndex = 1,
            explanation = "Bijak sekali! Tanpa cahaya matahari, air, udara, dan nutrien dari tanah, tumbuhan tidak dapat membina makanannya sendiri!"
        )
        checkpoints.add(Checkpoint("cp4", 1200f, 500f, false, challenge4))

        val portal = FinishPortal(x = 3350f, y = 490f)

        return WorldLevelData(def, platforms, collectibles, hazards, checkpoints, portal)
    }

    private fun createWorld5(def: WorldDefinition): WorldLevelData {
        val platforms = mutableListOf<Platform>()
        val collectibles = mutableListOf<Collectible>()
        val hazards = mutableListOf<Hazard>()
        val checkpoints = mutableListOf<Checkpoint>()

        // Floral Palace: Crystal petal bridges, rainbow path, giant blossoms
        platforms.add(Platform("p1", 0f, 650f, 500f, 250f, PlatformType.GRASS_SOIL))

        // Crystal flower petals
        platforms.add(Platform("cp1", 450f, 530f, 150f, 32f, PlatformType.CRYSTAL_PETAL))
        platforms.add(Platform("cp2", 680f, 440f, 160f, 32f, PlatformType.CRYSTAL_PETAL))
        platforms.add(Platform("cp3", 920f, 350f, 170f, 32f, PlatformType.CRYSTAL_PETAL))

        // Royal Garden terrace
        platforms.add(Platform("p2", 1120f, 600f, 600f, 300f, PlatformType.GRASS_SOIL))
        platforms.add(Platform("cp4", 1320f, 470f, 150f, 32f, PlatformType.CRYSTAL_PETAL))
        platforms.add(Platform("cp5", 1540f, 380f, 160f, 32f, PlatformType.CRYSTAL_PETAL))

        // Rainbow bridge across waterfall chasm
        platforms.add(Platform("cp6", 1780f, 450f, 150f, 32f, PlatformType.CRYSTAL_PETAL))
        platforms.add(Platform("cp7", 2000f, 520f, 160f, 32f, PlatformType.CRYSTAL_PETAL))

        // Palace courtyard
        platforms.add(Platform("p3", 2200f, 620f, 550f, 280f, PlatformType.GRASS_SOIL))
        platforms.add(Platform("cp8", 2420f, 480f, 160f, 32f, PlatformType.CRYSTAL_PETAL))
        platforms.add(Platform("cp9", 2650f, 390f, 170f, 32f, PlatformType.CRYSTAL_PETAL))

        // Grand throne of the Blooming Flower
        platforms.add(Platform("p4", 2900f, 600f, 700f, 300f, PlatformType.GRASS_SOIL))

        // Hazards: Sparkling magical chasms
        hazards.add(Hazard("h1", 500f, 800f, 620f, 100f, HazardType.WATER_GAP))
        hazards.add(Hazard("h2", 1720f, 800f, 480f, 100f, HazardType.WATER_GAP))
        hazards.add(Hazard("h3", 2750f, 800f, 150f, 100f, HazardType.WATER_GAP))

        // 5 Stars
        collectibles.add(Collectible("s1", 950f, 280f, CollectibleType.STAR, points = 250))
        collectibles.add(Collectible("s2", 1570f, 310f, CollectibleType.STAR, points = 250))
        collectibles.add(Collectible("s3", 2030f, 450f, CollectibleType.STAR, points = 250))
        collectibles.add(Collectible("s4", 2680f, 320f, CollectibleType.STAR, points = 250))
        collectibles.add(Collectible("s5", 3300f, 510f, CollectibleType.STAR, points = 250))

        // Assorted magical gifts
        collectibles.add(Collectible("sd1", 480f, 470f, CollectibleType.SEED_BAG, points = 80))
        collectibles.add(Collectible("w1", 710f, 380f, CollectibleType.WATER_DROPLET, points = 80))
        collectibles.add(Collectible("sn1", 1350f, 410f, CollectibleType.SUNSHINE_TOKEN, points = 80))
        collectibles.add(Collectible("w2", 1810f, 390f, CollectibleType.WATER_DROPLET, points = 80))
        collectibles.add(Collectible("sn2", 2450f, 420f, CollectibleType.SUNSHINE_TOKEN, points = 80))

        val challenge5 = EducationalChallenge(
            id = "c5",
            title = "Kitaran Lengkap Pokok Berbunga",
            question = "Apakah fungsi bunga pada tumbuhan yang telah matang?",
            hint = "Bunga yang disenyawakan akan menghasilkan buah dan biji baru!",
            options = listOf(
                ChallengeOption("Untuk Membekalkan Elektrik", "Tumbuhan tidak menghasilkan elektrik", "⚡"),
                ChallengeOption("Menghasilkan Buah & Biji Benih Baru", "Untuk membiak dan meneruskan generasi tumbuhan", "🌸"),
                ChallengeOption("Menakutkan Rama-Rama", "Bunga sebenarnya menarik serangga pendebunga", "🦋")
            ),
            correctIndex = 1,
            explanation = "Syabas dan Tahniah! Bunga menghasilkan biji benih baru untuk meneruskan kitaran hidup taman kita! Misi menyelamatkan taman bunga BERJAYA!"
        )
        checkpoints.add(Checkpoint("cp5", 1250f, 520f, false, challenge5))

        val portal = FinishPortal(x = 3400f, y = 490f)

        return WorldLevelData(def, platforms, collectibles, hazards, checkpoints, portal)
    }
}
