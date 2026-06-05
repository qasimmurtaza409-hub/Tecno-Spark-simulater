package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Colorway
import com.example.data.SparkSpecs
import com.example.ui.components.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    SparkWorkbenchScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun SparkWorkbenchScreen(modifier: Modifier = Modifier) {
    // Shared Device Simulator Sync States
    val colors = SparkSpecs.colorways
    var selectedColorway by remember { mutableStateOf(colors[0]) } // Gravity Black default
    var memFusionEnabled by remember { mutableStateOf(true) } // RAM Expand toggle
    var chargerConnected by remember { mutableStateOf(false) } // fast charger plug status
    var activeMusicTitle by remember { mutableStateOf("Spark Beats Remix") }
    var isMusicPlaying by remember { mutableStateOf(false) }

    // Navigation Tab Selection (Mobile Only toggle)
    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: Device view, 1: Performance, 2: Camera, 3: Audio

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0C0D11))
    ) {
        val isWideScreen = maxWidth >= 680.dp

        Column(modifier = Modifier.fillMaxSize()) {
            // Master Premium Sleek Header
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF14161C)),
                shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp),
                border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.08f)),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                BaseMetallicChip("SPARK 20 SERIES", SparkAmber)
                                Spacer(modifier = Modifier.width(6.dp))
                                BaseMetallicChip("HiOS 13.0", HologramTeal)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tecno Spark 20 Phone Console",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.DeveloperMode,
                            contentDescription = "Diagnostics Center",
                            tint = SparkAmber,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "System Sync: Helio G85 • ${if (memFusionEnabled) "16GB Speed Array (8+8)" else "8GB Physical RAM"} • Model Color: ${selectedColorway.name}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.LightGray.copy(alpha = 0.8f),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            if (isWideScreen) {
                // TABLET / EXPANDED SPLIT VIEWPORT DESIGN
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Left Side: Ever Present Simulated Device View!
                    Column(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        SparkPhoneModel(
                            selectedColor = selectedColorway,
                            memFusionEnabled = memFusionEnabled,
                            onMemFusionToggled = { memFusionEnabled = it },
                            chargerConnected = chargerConnected,
                            onChargerToggled = { chargerConnected = it },
                            activeMusicTitle = activeMusicTitle,
                            isMusicPlaying = isMusicPlaying
                        )

                        // Colorway Swapper Palette Card directly matching device rotation
                        ColorwaySelectorCard(
                            selectedColorway = selectedColorway,
                            colors = colors,
                            onSelect = { selectedColorway = it }
                        )
                    }

                    // Right Side: Control Panels & Diagnosticians Tabs
                    Column(
                        modifier = Modifier
                            .weight(1.4f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Specs tab controller bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF14161C))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(
                                "Specifications" to Icons.Default.Info,
                                "Performance" to Icons.Default.Speed,
                                "Camera Rig" to Icons.Default.PhotoCamera,
                                "DTS Speakers" to Icons.Default.VolumeUp
                            ).forEachIndexed { index, (label, icon) ->
                                val isSel = selectedTabIndex == index
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent)
                                        .clickable { selectedTabIndex = index }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = label,
                                            tint = if (isSel) Color.White else Color.Gray,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = label,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSel) Color.White else Color.Gray
                                        )
                                    }
                                }
                            }
                        }

                        // Display selected tab components
                        when (selectedTabIndex) {
                            0 -> TechSpecsExplorerCard()
                            1 -> {
                                BenchmarkRunner(memFusionEnabled = memFusionEnabled)
                                DisplaySmoothnessTester()
                            }
                            2 -> CameraViewfinder()
                            3 -> SpeakerTester { title, playing ->
                                activeMusicTitle = title
                                isMusicPlaying = playing
                            }
                        }
                    }
                }
            } else {
                // MOBILE / COMPACT SCREEN VIEWPORT DESIGN
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(10.dp)
                ) {
                    // Quick Horizontal Icon Tabs selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF14161C))
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val mobileTabs = listOf(
                            "Device" to Icons.Default.PhoneAndroid,
                            "Performance" to Icons.Default.Speed,
                            "Camera" to Icons.Default.PhotoCamera,
                            "Audio" to Icons.Default.VolumeUp
                        )

                        mobileTabs.forEachIndexed { index, (label, icon) ->
                            val isSel = selectedTabIndex == index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable { selectedTabIndex = index }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        tint = if (isSel) Color.White else Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = label,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else Color.Gray
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Display singular viewport screen depending on active tab selected
                    when (selectedTabIndex) {
                        0 -> {
                            SparkPhoneModel(
                                selectedColor = selectedColorway,
                                memFusionEnabled = memFusionEnabled,
                                onMemFusionToggled = { memFusionEnabled = it },
                                chargerConnected = chargerConnected,
                                onChargerToggled = { chargerConnected = it },
                                activeMusicTitle = activeMusicTitle,
                                isMusicPlaying = isMusicPlaying
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            ColorwaySelectorCard(
                                selectedColorway = selectedColorway,
                                colors = colors,
                                onSelect = { selectedColorway = it }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TechSpecsExplorerCard()
                        }
                        1 -> {
                            BenchmarkRunner(memFusionEnabled = memFusionEnabled)
                            Spacer(modifier = Modifier.height(8.dp))
                            DisplaySmoothnessTester()
                        }
                        2 -> CameraViewfinder()
                        3 -> SpeakerTester { title, playing ->
                            activeMusicTitle = title
                            isMusicPlaying = playing
                        }
                    }
                }
            }
        }
    }
}

// Compact helper tags
@Composable
fun BaseMetallicChip(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.15f))
            .border(0.5.dp, color.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            fontSize = 8.sp,
            fontWeight = FontWeight.Black,
            color = color,
            letterSpacing = 0.5.sp
        )
    }
}

// Color Swapper Selector controls widget
@Composable
fun ColorwaySelectorCard(
    selectedColorway: Colorway,
    colors: List<Colorway>,
    onSelect: (Colorway) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("colorway_selector_card"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14161C)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Select Hardware Color Variant",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                colors.forEach { cw ->
                    val isSel = selectedColorway.name == cw.name
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(cw.color)
                            .clickable { onSelect(cw) }
                            .border(
                                width = if (isSel) 3.dp else 1.dp,
                                color = if (isSel) SparkAmber else Color.White.copy(alpha = 0.3f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (cw.isLeather) {
                            // Needle stitch logo inside leather skin colorway
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Leather stitch logo icon",
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Color description strings
            Text(
                text = selectedColorway.name,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = SparkAmber
            )
            Text(
                text = selectedColorway.details,
                fontSize = 9.sp,
                color = Color.LightGray.copy(alpha = 0.8f),
                lineHeight = 11.sp
            )
        }
    }
}

// Beautiful list of specifications index card
@Composable
fun TechSpecsExplorerCard() {
    val specList = SparkSpecs.basicSpecs
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tech_specs_card"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14161C)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Specs Book",
                    tint = HologramTeal,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Hardware Specifications Sheet",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                specList.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(6.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(HologramTeal.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            val logoSymbol = when (item.iconName) {
                                "cpu" -> Icons.Default.DeveloperBoard
                                "ram" -> Icons.Default.Memory
                                "storage" -> Icons.Default.SdCard
                                "screen" -> Icons.Default.PhoneAndroid
                                "camera_rear" -> Icons.Default.PhotoCamera
                                "camera_front" -> Icons.Default.CameraFront
                                "battery" -> Icons.Default.BatteryChargingFull
                                "audio" -> Icons.Default.VolumeUp
                                "os" -> Icons.Default.Android
                                "shield" -> Icons.Default.VerifiedUser
                                else -> Icons.Default.Check
                            }
                            Icon(
                                imageVector = logoSymbol,
                                contentDescription = item.title,
                                tint = HologramTeal,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Column {
                            Text(
                                text = item.title,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                text = item.value,
                                fontSize = 11.sp,
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
