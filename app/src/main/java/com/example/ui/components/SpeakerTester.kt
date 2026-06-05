package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SimulatedTrack
import com.example.data.SparkSpecs
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SpeakerTester(
    onPlaybackStateChanged: (trackTitle: String, isPlaying: Boolean) -> Unit
) {
    val tracks = SparkSpecs.simulatedTracks
    var currentTrackIndex by remember { mutableStateOf(0) }
    val currentTrack = tracks[currentTrackIndex]
    var isPlaying by remember { mutableStateOf(false) }

    // DTS stage selection matches key Spark 20 marketing
    var selectedDtsMode by remember { mutableStateOf("Theater") } // "Theater", "400% Boost", "Gaming Bass", "Clear Vocals"

    // Sound waves dynamic values using local animations
    val infiniteTransition = rememberInfiniteTransition(label = "SoundWaveTransition")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhase"
    )

    // Notify the parent simulator immediately if track or playing toggled
    LaunchedEffect(currentTrack, isPlaying) {
        onPlaybackStateChanged(currentTrack.title, isPlaying)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .testTag("speaker_tester_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Speaker Icon",
                    tint = HiOSViolet,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "DTS Dual Stereo Sound Stage",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Experience 400% volume boost with calibrated DTS audio stages",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Waveform Graphic Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF111215))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                // High frequency Canvas draw
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val centerY = h / 2f
                    val pointsCount = 45

                    val maxAmpMultiplier = when (selectedDtsMode) {
                        "400% Boost" -> 2.2f
                        "Gaming Bass" -> 1.7f
                        "Theater" -> 1.3f
                        else -> 0.8f // Clear Vocals
                    }

                    val baseAmplitude = h * 0.22f * maxAmpMultiplier

                    // Draw left speaker glow source
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(HiOSViolet.copy(alpha = if (isPlaying) 0.15f else 0.02f), Color.Transparent),
                            center = Offset(0f, centerY),
                            radius = w * 0.25f
                        ),
                        radius = w * 0.25f
                    )

                    // Draw right speaker glow source
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(HiOSBlue.copy(alpha = if (isPlaying) 0.15f else 0.02f), Color.Transparent),
                            center = Offset(w, centerY),
                            radius = w * 0.25f
                        ),
                        radius = w * 0.25f
                    )

                    if (isPlaying) {
                        val pathHiOS = Path()
                        pathHiOS.moveTo(0f, centerY)

                        for (i in 0..pointsCount) {
                            val x = (w / pointsCount) * i
                            // Soundwave sine simulation combining modes
                            val sineVal = Math.sin((i * 0.25f) - wavePhase.toDouble() * 1.5).toFloat()
                            val cosVal = Math.cos((i * 0.12f) + wavePhase.toDouble()).toFloat()
                            // Fade out waves near screen edges (0 and w)
                            val edgeFade = Math.sin(((i.toFloat() / pointsCount.toFloat()) * Math.PI).toDouble()).toFloat()

                            val y = centerY + (sineVal * cosVal * baseAmplitude * edgeFade)

                            if (i == 0) {
                                pathHiOS.moveTo(x, y)
                            } else {
                                pathHiOS.lineTo(x, y)
                            }
                        }

                        // Select visual wave gradients based on mode selections
                        val waveGlow = when (selectedDtsMode) {
                            "400% Boost" -> Brush.horizontalGradient(listOf(SparkAmber, GoldenAmber, Color.Red))
                            "Gaming Bass" -> Brush.horizontalGradient(listOf(HologramTeal, HiOSBlue))
                            else -> Brush.horizontalGradient(listOf(HiOSViolet, HiOSBlue, HiOSViolet))
                        }

                        drawPath(
                            path = pathHiOS,
                            brush = waveGlow,
                            style = Stroke(width = (if (selectedDtsMode == "400% Boost") 4.5.dp else 2.5.dp).toPx())
                        )

                        // If 400% volume is active, draw visual distortion boundaries!
                        if (selectedDtsMode == "400% Boost") {
                            drawLine(
                                color = Color.Red.copy(alpha = 0.25f),
                                start = Offset(0f, h * 0.1f),
                                end = Offset(w, h * 0.1f),
                                strokeWidth = 1.dp.toPx()
                            )
                            drawLine(
                                color = Color.Red.copy(alpha = 0.25f),
                                start = Offset(0f, h * 0.9f),
                                end = Offset(w, h * 0.9f),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                    } else {
                        // Draw flat stationary line
                        drawLine(
                            color = Color.White.copy(alpha = 0.15f),
                            start = Offset(0f, centerY),
                            end = Offset(w, centerY),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                }

                // DTS Active Watermark Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedDtsMode == "400% Boost") SparkAmber else Color(0xFF232530)
                        ),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (selectedDtsMode == "400% Boost") "DTS: 400% BOOST ULTRA" else "DTS® Studio Sound",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedDtsMode == "400% Boost") Color.Black else Color.LightGray,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Playing Info bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play indicator rotating discs
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.MusicNote else Icons.Default.MusicOff,
                        contentDescription = "Song note indicator",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentTrack.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${currentTrack.artist} • ${currentTrack.duration}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive DTS sound preset stages (Real-world Spark 20 feature)
            Text(
                text = "Select Calibrated DTS Sound Stage Preset",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                val dtsStages = listOf(
                    "Theater" to Icons.Default.Movie,
                    "400% Boost" to Icons.Default.VolumeUp,
                    "Gaming Bass" to Icons.Default.Gamepad,
                    "Clear Vocals" to Icons.Default.RecordVoiceOver
                )

                dtsStages.forEach { (mode, icon) ->
                    val isSel = selectedDtsMode == mode
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedDtsMode = mode }
                            .testTag("dts_stage_$mode"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent
                        ),
                        border = if (isSel) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = "$mode Icon",
                                tint = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = mode,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Playback controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        currentTrackIndex = if (currentTrackIndex > 0) currentTrackIndex - 1 else tracks.size - 1
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous Track",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                FloatingActionButton(
                    onClick = { isPlaying = !isPlaying },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(56.dp)
                        .testTag("toggle_music_playback"),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(
                    onClick = {
                        currentTrackIndex = (currentTrackIndex + 1) % tracks.size
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Track",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}
