package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkSteel
import com.example.ui.theme.HologramTeal
import com.example.ui.theme.SparkAmber
import kotlinx.coroutines.delay

@Composable
fun DisplaySmoothnessTester() {
    var isRunning by remember { mutableStateOf(false) }
    var selectedFreq by remember { mutableStateOf(90) }

    // Dynamic animation parameters
    val infiniteTransition = rememberInfiniteTransition(label = "RefreshRate")
    val positionOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ballPosition"
    )

    // Trigger local simulation rates
    var frame60Position by remember { mutableStateOf(0f) }
    var frame90Position by remember { mutableStateOf(0f) }

    // Create a custom ticking clock that simulates different frame timings
    LaunchedEffect(isRunning) {
        if (isRunning) {
            var counter60 = 0f
            var direction60 = 1f
            var counter90 = 0f
            var direction90 = 1f

            while (isRunning) {
                // 60Hz updates roughly every 16ms
                delay(16)
                counter60 += (0.015f * direction60)
                if (counter60 >= 1f) {
                    counter60 = 1f
                    direction60 = -1f
                } else if (counter60 <= 0f) {
                    counter60 = 0f
                    direction60 = 1f
                }
                // Simulate slightly stuttered updates for 60Hz
                frame60Position = counter60

                // 90Hz updates roughly every 11ms (gives much smoother distribution)
                counter90 += (0.015f * direction90)
                if (counter90 >= 1f) {
                    counter90 = 1f
                    direction90 = -1f
                } else if (counter90 <= 0f) {
                    counter90 = 0f
                    direction90 = 1f
                }
                frame90Position = counter90
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .testTag("display_smoothness_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Refresh Rate Icon",
                        tint = HologramTeal,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "90Hz Refresh Smoothness Test",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                FilledTonalButton(
                    onClick = { isRunning = !isRunning },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (isRunning) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                        contentColor = if (isRunning) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.testTag("toggle_smoothness_test")
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Stop" else "Start"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isRunning) "Stop Demo" else "Run Demo")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "The Spark 20 utilizes a 90Hz ultra-fluid high refresh rate display, presenting double the speed and unmatched visual responsiveness compared to generic 60Hz screens. Drag, scroll, and view live physics side by side below.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Canvas Comparison Trackers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 60Hz Panel (Standard Phone)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSteel)
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Legacy 60Hz Display",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val r = 24.dp.toPx()

                            // Simulated position calculation with steps (60 fps spacing)
                            val position = if (isRunning) frame60Position else positionOffset
                            // Artificially truncate to give a slightly staggered "stuttering" feel compared to solid 90Hz
                            val stepQuantized = if (isRunning) {
                                // Round to 20 distinct discrete intervals
                                (position * 24).toInt() / 24f
                            } else {
                                (positionOffset * 22).toInt() / 22f
                            }

                            val yVal = r + (h - r * 2) * stepQuantized

                            // Background linear speed line guide
                            drawLine(
                                color = Color.White.copy(alpha = 0.05f),
                                start = Offset(w / 2, 0f),
                                end = Offset(w / 2, h),
                                strokeWidth = 2.dp.toPx()
                            )

                            // Ghost frames to show flicker/trail visually
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color.Red.copy(alpha = 0.15f), Color.Transparent),
                                    center = Offset(w / 2, yVal - (30.dp.toPx() * (if (position > 0.5f) 1 else -1)))
                                ),
                                radius = r * 0.9f
                            )

                            // Drawing Moving Neon Ball
                            drawCircle(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFFFF5252), Color(0xFFFF1744))
                                ),
                                center = Offset(w / 2, yVal),
                                radius = r
                            )
                        }
                    }
                }

                // 90Hz Panel (Tecno Spark 20)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F2C2E))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Spark 20 90Hz Display",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = HologramTeal
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val r = 24.dp.toPx()

                            // 90Hz position is completely analog and fluid without step truncation (continuous)
                            val position = if (isRunning) frame90Position else positionOffset
                            val yVal = r + (h - r * 2) * position

                            // Background linear speed line guide
                            drawLine(
                                color = HologramTeal.copy(alpha = 0.1f),
                                start = Offset(w / 2, 0f),
                                end = Offset(w / 2, h),
                                strokeWidth = 3.dp.toPx()
                            )

                            // Continuous smooth fluid trails
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(HologramTeal.copy(alpha = 0.25f), Color.Transparent),
                                    center = Offset(w / 2, yVal - (12.dp.toPx() * (if (position > 0.5f) 1 else -1)))
                                ),
                                radius = r * 1.1f
                            )
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(HologramTeal.copy(alpha = 0.15f), Color.Transparent),
                                    center = Offset(w / 2, yVal - (24.dp.toPx() * (if (position > 0.5f) 1 else -1)))
                                ),
                                radius = r * 1.2f
                            )

                            // Drawing Moving Neon Ball
                            drawCircle(
                                brush = Brush.verticalGradient(
                                    colors = listOf(HologramTeal, Color(0xFF00B0FF))
                                ),
                                center = Offset(w / 2, yVal),
                                radius = r
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Spec highlight indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "90Hz",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = HologramTeal
                    )
                    Text(
                        text = "Refresh Rate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
                Divider(
                    modifier = Modifier
                        .height(35.dp)
                        .width(1.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "180Hz",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SparkAmber
                    )
                    Text(
                        text = "Touch Sampling",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
                Divider(
                    modifier = Modifier
                        .height(35.dp)
                        .width(1.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "720x1612",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "HD+ Resolution",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
