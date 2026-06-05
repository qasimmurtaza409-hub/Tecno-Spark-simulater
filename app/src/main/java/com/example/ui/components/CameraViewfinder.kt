package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class CustomPhoto(
    val id: Long,
    val mode: String,
    val timestamp: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val isFront: Boolean,
    val flashSetting: String,
    val bokehSetting: Float
)

@Composable
fun CameraViewfinder() {
    var isFrontCamera by remember { mutableStateOf(false) }
    var selectedFlashMode by remember { mutableStateOf("Off") } // "Off", "Cool", "Natural", "Warm"
    var selfieBrightness by remember { mutableFloatStateOf(1.0f) }
    var bokehDegree by remember { mutableFloatStateOf(0.0f) } // Bokeh Aperture: 0.0 (f/8) to 1.0 (f/1.6 max blur)
    var selectedPhotoMode by remember { mutableStateOf("Photo") } // "Photo", "Super Night", "50MP", "Portrait"

    var capturedPhotos = remember { mutableStateListOf<CustomPhoto>() }
    var showFlashScreen by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Interactive snapshot trigger
    val capturePhoto: () -> Unit = {
        scope.launch {
            showFlashScreen = true
            delay(150)
            showFlashScreen = false

            val currentModeStr = when (selectedPhotoMode) {
                "Super Night" -> "🌌 50MP Super Night"
                "50MP" -> "📸 50MP Ultra-Clear HD"
                "Portrait" -> "🎯 Portrait (f/${String.format("%.1f", 8.0f - (6.4f * bokehDegree))})"
                else -> if (isFrontCamera) "🤳 32MP Glowing Selfie" else "📸 50MP AI Auto"
            }

            val flashGlowDesc = if (isFrontCamera && selectedFlashMode != "Off") {
                "Front Aura: $selectedFlashMode (Level ${(selfieBrightness * 3).toInt().coerceIn(1, 3)})"
            } else if (!isFrontCamera) {
                "Rear Dual Flash"
            } else {
                "No Flash"
            }

            // Generate representative visual thumbnail colors depending on specs
            val (pCol, sCol) = when {
                selectedPhotoMode == "Super Night" -> Pair(Color(0xFF030D2E), Color(0xFF512DA8))
                selectedPhotoMode == "Portrait" -> Pair(Color(0xFFE91E63), Color(0xFFFF9800))
                isFrontCamera && selectedFlashMode == "Warm" -> Pair(Color(0xFFFFCC80), Color(0xFFFF8A80))
                isFrontCamera && selectedFlashMode == "Cool" -> Pair(Color(0xFF80DEEA), Color(0xFF80CBC4))
                isFrontCamera && selectedFlashMode == "Natural" -> Pair(Color(0xFFFFF59D), Color(0xFFB0BEC5))
                else -> Pair(Color(0xFF00C853), Color(0xFF007BFF))
            }

            val curTime = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())

            capturedPhotos.add(
                0, // Add on top of history
                CustomPhoto(
                    id = System.currentTimeMillis(),
                    mode = currentModeStr,
                    timestamp = "Captured at $curTime",
                    primaryColor = pCol,
                    secondaryColor = sCol,
                    isFront = isFrontCamera,
                    flashSetting = flashGlowDesc,
                    bokehSetting = bokehDegree
                )
            )
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .testTag("camera_viewfinder_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title Header with specs description
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = "Camera Icon",
                    tint = SparkAmber,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Spark 20 Professional Camera Rig",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "50MP Ultra-Clear F/1.6 lens & 32MP Glowing Front Selfie Setup",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.61f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Camera Mode Selector (Tabs indicator inside viewfinder box)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.1f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Photo", "Portrait", "50MP", "Super Night").forEach { mode ->
                    val isSelected = selectedPhotoMode == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { selectedPhotoMode = mode }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Active Viewfinder Screen Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black)
            ) {
                // Background Simulated Scenic vector or abstract drawing
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Dynamic bokeh blur simulation mapping
                    val blurRadialMultiplier = 1f - (0.8f * bokehDegree) // smaller multiplier means larger more spread focal blur

                    // If it's Super Night mode, draw starry dark coordinates
                    if (selectedPhotoMode == "Super Night") {
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF02071A), Color(0xFF0F172A))
                            )
                        )
                        // Star highlights
                        drawCircle(Color.White.copy(alpha = 0.8f), 3.dp.toPx(), Offset(w * 0.2f, h * 0.15f))
                        drawCircle(Color.White.copy(alpha = 0.5f), 2.dp.toPx(), Offset(w * 0.8f, h * 0.3f))
                        drawCircle(Color.White.copy(alpha = 0.9f), 4.dp.toPx(), Offset(w * 0.7f, h * 0.12f))
                        drawCircle(Color.White.copy(alpha = 0.4f), 3.dp.toPx(), Offset(w * 0.45f, h * 0.45f))

                        // Cool glowing crescent moon
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFFFFFDE7), Color.Transparent),
                                center = Offset(w * 0.85f, h * 0.25f),
                                radius = 40.dp.toPx()
                            ),
                            radius = 40.dp.toPx()
                        )
                        drawCircle(
                            color = Color(0xFFFFF9C4),
                            center = Offset(w * 0.85f, h * 0.25f),
                            radius = 18.dp.toPx()
                        )
                    } else {
                        // Standard view draws sunset/sunny hills
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF1E88E5), Color(0xFF64B5F6), Color(0xFFFFB74D))
                            )
                        )
                        // Giant Glowing Sun representing Bokeh source
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFFFFEA00).copy(alpha = 0.75f), Color.Transparent),
                                center = Offset(w * 0.3f, h * 0.35f),
                                radius = (80.dp.toPx() / blurRadialMultiplier)
                            ),
                            radius = (80.dp.toPx() / blurRadialMultiplier)
                        )
                        drawCircle(
                            color = Color(0xFFFFF176),
                            center = Offset(w * 0.3f, h * 0.35f),
                            radius = 20.dp.toPx()
                        )

                        // Rolling Hills, affected by Bokeh blur slider
                        val hillCol = Color(0xFF4CAF50)
                        val hillBlurCol = Color(0xAA81C784)
                        drawCircle(
                            color = if (bokehDegree > 0.5f) hillBlurCol else hillCol,
                            center = Offset(w * 0.1f, h * 1.1f),
                            radius = w * 0.6f
                        )
                        drawCircle(
                            color = if (bokehDegree > 0.3f) Color(0xAA66BB6A) else Color(0xFF388E3C),
                            center = Offset(w * 0.9f, h * 1.2f),
                            radius = w * 0.6f
                        )
                    }

                    // Draw a portrait silhouette model if Portrait mode is active to show depth check!
                    if (selectedPhotoMode == "Portrait" || isFrontCamera) {
                        val headCenter = Offset(w / 2, h * 0.4f)
                        val headRadius = 45.dp.toPx()

                        // Shadow outline
                        drawCircle(
                            color = Color.Black.copy(alpha = 0.3f),
                            center = headCenter + Offset(4f, 4f),
                            radius = headRadius
                        )

                        // Head of the subject
                        drawCircle(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFFFD54F), Color(0xFFF57C00))
                            ),
                            center = headCenter,
                            radius = headRadius
                        )

                        // Hair Details
                        drawCircle(
                            color = Color(0xFF3E2723),
                            center = headCenter - Offset(0f, 10.dp.toPx()),
                            radius = headRadius * 0.85f
                        )

                        // Body/Shoulders
                        drawCircle(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFFE91E63), Color(0xFF880E4F))
                            ),
                            center = Offset(w / 2, h * 0.95f),
                            radius = 90.dp.toPx()
                        )

                        // Glow face outline
                        drawCircle(
                            color = Color.White.copy(alpha = 0.12f),
                            center = headCenter,
                            radius = headRadius * 0.95f
                        )
                    }
                }

                // Grid alignment overlay lines mapping
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    // Horizontal lines
                    drawLine(Color.White.copy(alpha = 0.25f), Offset(w * 0.33f, 0f), Offset(w * 0.33f, h), strokeWidth = 1f)
                    drawLine(Color.White.copy(alpha = 0.25f), Offset(w * 0.66f, 0f), Offset(w * 0.66f, h), strokeWidth = 1f)
                    // Vertical lines
                    drawLine(Color.White.copy(alpha = 0.25f), Offset(0f, h * 0.33f), Offset(w, h * 0.33f), strokeWidth = 1f)
                    drawLine(Color.White.copy(alpha = 0.25f), Offset(0f, h * 0.66f), Offset(w, h * 0.66f), strokeWidth = 1f)
                }

                // Front Dynamic Soft-glow Ring Ambient Aura Flash Overlay (Adjusted dynamically!)
                if (isFrontCamera && selectedFlashMode != "Off") {
                    val glowColor = when (selectedFlashMode) {
                        "Cool" -> Color(0xFF00E5FF)
                        "Warm" -> Color(0xFFFF9100)
                        else -> Color(0xFFFFFFFF) // Natural
                    }
                    val currentOpacity = (selfieBrightness * 0.3f).coerceIn(0.05f, 0.45f)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color.Transparent, glowColor.copy(alpha = currentOpacity)),
                                    radius = 450f
                                )
                            )
                    )
                }

                // Pure shutter sound-flash animation flash screen
                if (showFlashScreen) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                    )
                }

                // Floating Viewfinder Details overlay text
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.61f)),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color.Red)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isFrontCamera) "FRONT: 32MP ULTRA GLOW" else "REAR: 50MP ULTRA CLEAR",
                                fontSize = 9.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    if (isFrontCamera && selectedFlashMode != "Off") {
                        Spacer(modifier = Modifier.height(4.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SparkAmber.copy(alpha = 0.85f)),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "💡 Glow Fill Light [ $selectedFlashMode Temp ] Active",
                                fontSize = 9.sp,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Bottom floating configuration parameters (Resolution, HDR check)
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                            )
                        )
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Aperture: F/${String.format("%.1f", 8.0f - (6.4f * bokehDegree))}",
                        fontSize = 11.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        style = TextStyle(shadow = Shadow(color = Color.Black, blurRadius = 3f))
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.HdrOn, contentDescription = "HDR Indicator", tint = HologramTeal, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("HDR", fontSize = 10.sp, color = HologramTeal, fontWeight = FontWeight.Bold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FlashOn, contentDescription = "Dual LED Flash", tint = SparkAmber, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("Dual Warm/Cool", fontSize = 10.sp, color = SparkAmber, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dynamic camera sliders adjusting parameters
            if (isFrontCamera) {
                // Interactive Front Camera Glow configuration
                Text(
                    text = "32MP Dual-LED Aura Softlight (Front Flash Controls)",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Off", "Cool", "Natural", "Warm").forEach { mode ->
                        val isSel = selectedFlashMode == mode
                        Button(
                            onClick = { selectedFlashMode = mode },
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSel) SparkAmber.copy(alpha = 0.25f) else Color.Transparent,
                                contentColor = if (isSel) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = SolidColor(if (isSel) SparkAmber else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(mode, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (selectedFlashMode != "Off") {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Softlight Glow Range:  ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Slider(
                            value = selfieBrightness,
                            onValueChange = { selfieBrightness = it },
                            valueRange = 0.3f..1.0f,
                            modifier = Modifier.weight(1f).testTag("glow_light_slider"),
                            colors = SliderDefaults.colors(
                                thumbColor = SparkAmber,
                                activeTrackColor = SparkAmber
                            )
                        )
                    }
                }
            } else {
                // Rear camera simulated depth / bokeh aperture slider
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "F/1.6 Pro Aperture & Portrait Bokeh Slider",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (bokehDegree < 0.2f) "Deep Field (F/8.0)" else if (bokehDegree < 0.8f) "Medium Bokeh (F/3.2)" else "Ultra Blurred (F/1.6 Pro)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = HologramTeal
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Slider(
                        value = bokehDegree,
                        onValueChange = { bokehDegree = it },
                        modifier = Modifier.fillMaxWidth().testTag("aperture_depth_slider"),
                        colors = SliderDefaults.colors(
                            thumbColor = HologramTeal,
                            activeTrackColor = HologramTeal
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Camera hardware action switcher and Main shutter triggers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Switch Front/Rear button
                IconButton(
                    onClick = { isFrontCamera = !isFrontCamera },
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                        .testTag("switch_camera_button"),
                    colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.FlipCameraAndroid,
                        contentDescription = "Toggle Front/Back Camera",
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Master Shutter Circle
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                        .clickable { capturePhoto() }
                        .padding(5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        if (showFlashScreen) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(SparkAmber)
                            )
                        }
                    }
                }

                // Information indicator badge
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isFrontCamera) "32M" else "50M",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Gallery section listing taken photos
            if (capturedPhotos.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f))
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Virtual Device Gallery Photos (${capturedPhotos.size})",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(115.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(capturedPhotos, key = { it.id }) { photo ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { },
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                // Draw stylized photo thumbnail
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawRect(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(photo.primaryColor, photo.secondaryColor)
                                        )
                                    )
                                    // Sun bubble or shadow inside thumbnail
                                    drawCircle(
                                        color = Color.White.copy(alpha = 0.2f),
                                        center = Offset(size.width / 2, size.height / 2),
                                        radius = size.width * 0.3f
                                    )
                                    drawCircle(
                                        color = Color.Black.copy(alpha = 0.15f),
                                        center = Offset(size.width / 2, size.height / 2),
                                        radius = size.width * 0.15f
                                    )
                                }

                                // Photo specifications overlay details
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .background(Color.Black.copy(alpha = 0.5f))
                                        .fillMaxWidth()
                                        .padding(4.dp)
                                ) {
                                    Text(
                                        text = photo.mode,
                                        fontSize = 8.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = photo.timestamp,
                                        fontSize = 7.sp,
                                        color = Color.LightGray,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Take virtual photos to store in the simulated HiOS Gallery",
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}
