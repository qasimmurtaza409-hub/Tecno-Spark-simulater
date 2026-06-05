package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SparkSpecs
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BenchmarkRunner(memFusionEnabled: Boolean) {
    var isRunning by remember { mutableStateOf(false) }
    var currentProgress by remember { mutableFloatStateOf(0f) }
    var currentLog by remember { mutableStateOf("Helio G85 Core Diagnostics Idle") }
    var thermalTemp by remember { mutableIntStateOf(34) }
    var finalAnTuTuScore by remember { mutableStateOf<Int?>(null) }
    
    // Waveform simulation path parameters for dynamic core loads
    val infiniteTransition = rememberInfiniteTransition(label = "BenchmarkCycles")
    val cycleOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Cycle"
    )

    val scope = rememberCoroutineScope()

    val runBenchmark: () -> Unit = {
        scope.launch {
            isRunning = true
            finalAnTuTuScore = null
            currentProgress = 0f
            thermalTemp = 34
            
            val logs = listOf(
                "Initializing MediaTek Helio G85 core array...",
                "Booting dual high-power ARM Cortex-A75 cores at 2.0 GHz...",
                "Activating six high-efficiency ARM Cortex-A55 cores at 1.8 GHz...",
                if (memFusionEnabled) "Detecting 8GB + 8GB MemFusion dynamic virtual RAM. Initializing dual-channel heap paging..." 
                else "Detecting standard 8GB Physical RAM array...",
                "Scanning Mali-G52 MC2 dual shaders. Allocating full vertex cache...",
                "Running floating-point parallel thread operations...",
                "Warming CPU core registers. Evaluating thermal threshold metrics...",
                "HyperEngine 2.0 Lite: Adjusting load-balance network routing...",
                "Rendering heavy dynamic 3D graphic pipelines at 90Hz...",
                "Benchmark complete! Collating diagnostic results..."
            )

            for (i in logs.indices) {
                currentProgress = (i + 1).toFloat() / logs.size.toFloat()
                currentLog = logs[i]
                
                // Thermal heating simulation
                thermalTemp = 34 + (i * 2) + (if (memFusionEnabled) 1 else 0)
                delay(750)
            }
            
            // Final collated AnTuTu Score
            finalAnTuTuScore = if (memFusionEnabled) {
                284550 // dynamic memory boost
            } else {
                271840
            }
            isRunning = false
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .testTag("benchmark_runner_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = "Benchmark Icon",
                    tint = ThermalRed,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Helio G85 System Benchmark",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Run diagnostic stress tests on the MediaTek octa-core processing array",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Trigger button
            Button(
                onClick = { if (!isRunning) runBenchmark() },
                enabled = !isRunning,
                colors = ButtonDefaults.buttonColors(containerColor = ThermalRed, contentColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("run_benchmark_button")
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Run Icon")
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isRunning) "Stress Test in Progress..." else "Execute Helio G85 Diagnostics")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Live Diagnostics Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Left: CPU Core Load graph Canvas
                Box(
                    modifier = Modifier
                        .weight(1.5f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF111215))
                        .padding(6.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val centerY = h / 2f
                        
                        // Drawn static outline grids
                        drawLine(Color.White.copy(alpha = 0.05f), Offset(0f, h * 0.25f), Offset(w, h * 0.25f), strokeWidth = 1f)
                        drawLine(Color.White.copy(alpha = 0.05f), Offset(0f, centerY), Offset(w, centerY), strokeWidth = 1f)
                        drawLine(Color.White.copy(alpha = 0.05f), Offset(0f, h * 0.75f), Offset(w, h * 0.75f), strokeWidth = 1f)

                        if (isRunning) {
                            val path = Path()
                            path.moveTo(0f, centerY)
                            
                            val step = 15
                            for (i in 0..step) {
                                val x = (w / step) * i
                                val waveFactor = if (i % 2 == 0) 1.2f else -0.8f
                                val progressWeight = currentProgress * 1.5f
                                val y = centerY + (waveFactor * Math.sin(((i * 0.4f) + (cycleOffset * 6.28f)).toDouble()).toFloat() * h * 0.3f * progressWeight)
                                
                                if (i == 0) {
                                    path.moveTo(x, y)
                                } else {
                                    path.lineTo(x, y)
                                }
                            }
                            
                            drawPath(
                                path = path,
                                brush = Brush.horizontalGradient(listOf(ThermalRed, HologramTeal)),
                                style = Stroke(width = 2.dp.toPx())
                            )
                        } else {
                            // Flat resting line
                            drawLine(
                                color = Color.White.copy(alpha = 0.12f),
                                start = Offset(0f, centerY),
                                end = Offset(w, centerY),
                                strokeWidth = 2.dp.toPx()
                            )
                        }
                    }
                    
                    Text(
                        text = "CORTEX-A75 GRAPH",
                        fontSize = 7.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(2.dp)
                    )
                }

                // Right: Thermal temperature gauge + logs
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF21232A)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "CORE TEMP",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.LightGray
                        )
                        
                        Text(
                            text = "$thermalTemp°C",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = if (thermalTemp > 45) ThermalRed else if (thermalTemp > 38) SparkAmber else HologramTeal
                        )

                        // Thermometer progress capsule bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color.Black.copy(alpha = 0.3f))
                        ) {
                            val tempRatio = ((thermalTemp - 30).toFloat() / 25f).coerceIn(0f, 1f)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(tempRatio)
                                    .fillMaxHeight()
                                    .background(
                                        Brush.horizontalGradient(listOf(HologramTeal, SparkAmber, ThermalRed))
                                    )
                            )
                        }
                        
                        Text(
                            text = if (thermalTemp > 45) "THROTTLE GUARD: ACTIVE" else "STABLE HEAT DENSITIES",
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (thermalTemp > 45) ThermalRed else Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Dynamic Progress Indicator Strip & Rolling Log Outputs
            AnimatedVisibility(visible = isRunning || currentProgress > 0f) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.Black.copy(alpha = 0.2f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(currentProgress)
                                .fillMaxHeight()
                                .background(ThermalRed)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentLog,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1
                    )
                }
            }

            // High Tech AnTuTu Collated Badge
            AnimatedVisibility(
                visible = finalAnTuTuScore != null,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                finalAnTuTuScore?.let { score ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                            .border(1.dp, ThermalRed.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0x22FF1744)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "AnTuTu benchmark V10 Score",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.LightGray
                                )
                                Text(
                                    text = if (memFusionEnabled) "Enhanced with 8GB MemFusion speed!" else "Base storage speed calibration",
                                    fontSize = 8.sp,
                                    color = Color.Gray
                                )
                            }
                            
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = String.format("%,d", score),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = ThermalRed,
                                    textAlign = TextAlign.End
                                )
                                Text(
                                    text = "HELIO G85 OCTA",
                                    fontSize = 7.sp,
                                    color = ThermalRed,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
