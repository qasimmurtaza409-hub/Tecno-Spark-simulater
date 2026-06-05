package com.example.data

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

data class SpecItem(val title: String, val value: String, val iconName: String)

data class Colorway(
    val name: String,
    val color: Color,
    val hexString: String,
    val assetUrl: String, // Description or custom drawing flag
    val isLeather: Boolean = false,
    val details: String
)

object SparkSpecs {
    val basicSpecs = listOf(
        SpecItem("Processor", "MediaTek Helio G85 Octa-Core (Up to 2.0GHz)", "cpu"),
        SpecItem("Memory", "8GB RAM + 8GB MemFusion Extended RAM = 16GB Total", "ram"),
        SpecItem("Storage", "256GB ROM (Support MicroSD up to 1TB)", "storage"),
        SpecItem("Screen", "6.6-inch IPS LCD 90Hz Smart Refresh Rate", "screen"),
        SpecItem("Rear Camera", "50MP Ultra-Clear F/1.6 Dual Camera with Dual-LED Flash", "camera_rear"),
        SpecItem("Selfie Camera", "32MP Glowing Selfie with Dual-LED Adjustable Flash", "camera_front"),
        SpecItem("Battery", "5000mAh Mega-Battery with 18W Fast Charge", "battery"),
        SpecItem("Audio", "DTS Dual Stereo Speakers (400% Volume Boost)", "audio"),
        SpecItem("OS Connection", "Android 13 running HiOS 13", "os"),
        SpecItem("Water Rating", "IP53 Dust & Splash Resistance", "shield")
    )

    val colorways = listOf(
        Colorway(
            "Gravity Black", 
            Color(0xFF141518), 
            "#141518", 
            "brushed", 
            isLeather = false,
            "Classic, glossy back panel with metallic stellar particles that shimmer under direct light reflections."
        ),
        Colorway(
            "Cyber White", 
            Color(0xFFE5E9F0), 
            "#E5E9F0", 
            "holographic", 
            isLeather = false,
            "Frosted glass-like back. Shimmers with elegant pink and blue hues matching futuristic cyberpunk nodes."
        ),
        Colorway(
            "Neon Gold", 
            Color(0xFFF0E5CC), 
            "#F0E5CC", 
            "gold_spark", 
            isLeather = false,
            "Opulent champagne-gold finish that glistens with a premium metallic sand texture."
        ),
        Colorway(
            "Magic Skin 2.0 (Blue)", 
            Color(0xFF4C8CD2), 
            "#4C8CD2", 
            "leather", 
            isLeather = true,
            "Eco-leather material (Silicon-polymer). Feels highly warm, dust-repellent, and features precise needle-stitch patterns along the margins."
        )
    )

    val benchmarkTests = listOf(
        "CPU Single-Core Thermal Efficiency Scan",
        "Mali-G52 Dual-Core GPU Heavy Frame Rendering",
        "MemFusion RAM Dynamic Allocation Burst Test",
        "MediaTek HyperEngine 2.0 Lite Optimizer Calibration"
    )

    val simulatedTracks = listOf(
        SimulatedTrack("Ignite Spark", "HiOS Electronic Mix", "02:45"),
        SimulatedTrack("Gravity Pulse", "Neo Synthwave Track", "03:12"),
        SimulatedTrack("Eco Leather Breeze", "Acoustic Chill Beat", "02:18")
    )
}

data class SimulatedTrack(val title: String, val artist: String, val duration: String)
