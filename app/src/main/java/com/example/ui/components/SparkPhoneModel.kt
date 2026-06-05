package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Colorway
import com.example.data.SparkSpecs
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@Composable
fun SparkPhoneModel(
    selectedColor: Colorway,
    memFusionEnabled: Boolean,
    onMemFusionToggled: (Boolean) -> Unit,
    chargerConnected: Boolean,
    onChargerToggled: (Boolean) -> Unit,
    activeMusicTitle: String,
    isMusicPlaying: Boolean
) {
    var isPhoneOn by remember { mutableStateOf(true) }
    var isBooting by remember { mutableStateOf(false) }
    var showFaceUnlockCheck by remember { mutableStateOf(false) }
    var batteryPercentage by remember { mutableIntStateOf(52) }
    var currentVolume by remember { mutableStateOf(10) } // scales from 0 to 15
    var showVolumeSlider by remember { mutableStateOf(false) }
    var viewFace by remember { mutableStateOf("front") } // "front", "back"
    var showIncomingCall by remember { mutableStateOf(false) }
    var isCallActive by remember { mutableStateOf(false) }

    // Store Simulated App States
    var openPlayStoreApp by remember { mutableStateOf(false) }
    var activeSimulatedAppId by remember { mutableStateOf<String?>(null) } // "WhatsApp", "PUBG MOBILE", etc.
    val installedApps = remember {
        mutableStateListOf(
            "Settings",
            "Benchmark",
            "Phone",
            "DTS DTS",
            "Camera",
            "Charge",
            "Play Store"
        )
    }
    var installingAppId by remember { mutableStateOf<String?>(null) }
    var installProgress by remember { mutableStateOf(0.0f) }

    // WhatsApp Message Thread States
    val whatsappMessages = remember {
        mutableStateListOf(
            "Hello Qasim Murtaza! Outstanding work on designing the Tecno Spark 20 Signature Edition! 🚀",
            "The screen size is now maximized to look identical to an premium tablet. Touch latency is 1ms."
        )
    }
    var messageText by remember { mutableStateOf("") }

    // PUBG MOBILE Simulated States
    var pubgMatchActive by remember { mutableStateOf(false) }
    var pubgKills by remember { mutableStateOf(0) }

    // Subway Surfers Simulated States
    var subwayCoins by remember { mutableStateOf(1420) }
    var subwayScore by remember { mutableStateOf(24500) }
    var subwayLane by remember { mutableStateOf(1) } // 0: Left, 1: Center, 2: Right
    var subwayYOffset by remember { mutableStateOf(0) }

    // Instagram States
    var instagramLiked by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Battery charge increment thread
    LaunchedEffect(chargerConnected) {
        if (chargerConnected) {
            while (chargerConnected && batteryPercentage < 100) {
                delay(3500)
                batteryPercentage = (batteryPercentage + 1).coerceAtMost(100)
            }
        }
    }

    // Timer to hide volume bar
    LaunchedEffect(currentVolume) {
        showVolumeSlider = true
        delay(2000)
        showVolumeSlider = false
    }

    // Trigger initial signature on-screen booting animation on launch
    LaunchedEffect(Unit) {
        isBooting = true
        delay(3000)
        isBooting = false
    }

    // Trigger face unlock simulation
    val triggerFaceUnlock: () -> Unit = {
        scope.launch {
            if (isPhoneOn && !isBooting) {
                showFaceUnlockCheck = true
                delay(1800)
                showFaceUnlockCheck = false
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .testTag("spark_phone_model_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Interactive control bar for physical housing
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(selectedColor.color)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Spark 20 Tablet Panel",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // View Toggle Face: Front Screen vs Dual-Camera Metallic back
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.1f))
                        .padding(2.dp)
                ) {
                    Button(
                        onClick = { viewFace = "front" },
                        modifier = Modifier
                            .height(28.dp)
                            .testTag("view_front_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (viewFace == "front") MaterialTheme.colorScheme.primary else Color.Transparent,
                            contentColor = if (viewFace == "front") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Text("10.8\" Screen", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { viewFace = "back" },
                        modifier = Modifier
                            .height(28.dp)
                            .testTag("view_back_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (viewFace == "back") MaterialTheme.colorScheme.primary else Color.Transparent,
                            contentColor = if (viewFace == "back") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Text("50MP Back", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main chassis layout row (housing with side buttons) - HEIGHT SIZED ENLARGED ON REQUEST
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(560.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Front and Back rendering frame - SIZED WIDER LIKE A TABLET ON REQUEST (340.dp)
                Box(
                    modifier = Modifier
                        .width(340.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(32.dp))
                        .background(DarkSteel)
                        .border(
                            width = 5.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.LightGray, selectedColor.color.copy(alpha = 0.5f), Color.DarkGray)
                            ),
                            shape = RoundedCornerShape(32.dp)
                        )
                        .padding(4.dp)
                ) {
                    if (viewFace == "front") {
                        // FRONT SCREEN RENDERING FRAME
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(28.dp))
                                .background(if (isPhoneOn) Color.Black else Color(0xFF030304))
                        ) {
                            if (isPhoneOn) {
                                if (isBooting) {
                                    // PREMIUM GLOWING CUSTOM BOOTUP CAROUSEL FEATURING QASIM MURTAZA LOGO!
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color(0xFF06070B)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            // Glowing custom designer signature logo container
                                            Box(
                                                modifier = Modifier
                                                    .size(100.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        Brush.radialGradient(
                                                            colors = listOf(
                                                                SparkAmber.copy(alpha = 0.25f),
                                                                Color.Transparent
                                                            )
                                                        )
                                                    )
                                                    .border(2.dp, SparkAmber, CircleShape)
                                                    .padding(6.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "QM",
                                                    fontSize = 42.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = SparkAmber,
                                                    fontFamily = FontFamily.Monospace,
                                                    letterSpacing = 1.sp
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(18.dp))

                                            Text(
                                                text = "Qasim Murtaza",
                                                fontSize = 22.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White,
                                                letterSpacing = 1.5.sp
                                            )

                                            Text(
                                                text = "AURA SIGNATURE EDITION",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SparkAmber,
                                                letterSpacing = 2.5.sp,
                                                modifier = Modifier.padding(top = 2.dp)
                                            )

                                            Spacer(modifier = Modifier.height(42.dp))

                                            CircularProgressIndicator(
                                                color = SparkAmber,
                                                strokeWidth = 2.5.dp,
                                                modifier = Modifier.size(22.dp)
                                            )

                                            Spacer(modifier = Modifier.height(64.dp))

                                            Text(
                                                text = "POWERED BY HiOS 13.0 PANEL",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White.copy(alpha = 0.4f),
                                                letterSpacing = 1.5.sp
                                            )
                                        }
                                    }
                                } else {
                                    // HiOS ACTIVE DESKTOP ENVIRONMENT
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.radialGradient(
                                                    colors = listOf(
                                                        selectedColor.color.copy(alpha = 0.45f),
                                                        Color(0xFF020412),
                                                        Color(0xFF0C0212)
                                                    )
                                                )
                                            )
                                    ) {
                                        // Real-time simulated status bar (Battery, Time, Wi-Fi, 4G)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 10.dp, start = 16.dp, end = 16.dp)
                                                .height(20.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val currentTimeString = java.text.SimpleDateFormat("hh:mm", java.util.Locale.getDefault()).format(java.util.Date())
                                            Text(
                                                text = currentTimeString,
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(Icons.Default.Wifi, contentDescription = "Wi-Fi logo", tint = Color.White, modifier = Modifier.size(11.dp))
                                                Icon(Icons.Default.NetworkCell, contentDescription = "Network 4G logo", tint = Color.White, modifier = Modifier.size(11.dp))
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text("$batteryPercentage%", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                    Spacer(modifier = Modifier.width(1.dp))
                                                    Icon(
                                                        imageVector = if (chargerConnected) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                                                        contentDescription = "Battery level logo",
                                                        tint = if (chargerConnected) SparkAmber else Color.White,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                }
                                            }
                                        }

                                        // Screen Content Selector Box
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(top = 36.dp, bottom = 32.dp, start = 10.dp, end = 10.dp)
                                        ) {
                                            if (openPlayStoreApp) {
                                                // GOOGLE PLAY STORE APP MODE CONTAINER
                                                Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0D0F13))) {
                                                    // Store header with back button return
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(6.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            IconButton(
                                                                onClick = { openPlayStoreApp = false },
                                                                modifier = Modifier.size(24.dp)
                                                            ) {
                                                                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(16.dp))
                                                            }
                                                            Spacer(modifier = Modifier.width(4.dp))
                                                            Text("Google Play Store", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                        }

                                                        // Play Store colorful logo token
                                                        Box(
                                                            modifier = Modifier
                                                                .size(20.dp)
                                                                .background(Brush.linearGradient(listOf(HologramTeal, SparkAmber)), RoundedCornerShape(4.dp)),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Icon(Icons.Default.Shop, contentDescription = "Play Store Icon", tint = Color.Black, modifier = Modifier.size(12.dp))
                                                        }
                                                    }

                                                    // Search Bar simulation
                                                    Card(
                                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF161920)),
                                                        shape = RoundedCornerShape(18.dp),
                                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text("Search apps & games...", color = Color.Gray, fontSize = 8.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                                                    }

                                                    // Categorized App Items List scroll stream
                                                    LazyColumn(
                                                        modifier = Modifier.fillMaxSize().padding(top = 8.dp),
                                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        val storeApps = listOf(
                                                            Triple("WhatsApp", "WhatsApp Messenger", Color(0xFF25D366)),
                                                            Triple("PUBG MOBILE", "PUBG Ultimate Combat", Color(0xFFF57C00)),
                                                            Triple("Subway Surfers", "Subway Endless Run", Color(0xFFFFD54F)),
                                                            Triple("Instagram", "Instagram: Feed & Reels", Color(0xFFE1306C))
                                                        )

                                                        items(storeApps) { item ->
                                                            val (appName, appTitle, accentColor) = item
                                                            val isAlreadyInstalled = installedApps.contains(appName)
                                                            val isCurrentlyInstalling = installingAppId == appName

                                                            Row(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .background(Color(0xFF14171F), RoundedCornerShape(8.dp))
                                                                    .padding(6.dp),
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                Box(
                                                                    modifier = Modifier
                                                                        .size(36.dp)
                                                                        .clip(RoundedCornerShape(8.dp))
                                                                        .background(accentColor.copy(alpha = 0.15f))
                                                                        .border(1.dp, accentColor, RoundedCornerShape(8.dp)),
                                                                    contentAlignment = Alignment.Center
                                                                ) {
                                                                    val appSymbol = when (appName) {
                                                                        "WhatsApp" -> Icons.Default.Message
                                                                        "PUBG MOBILE" -> Icons.Default.SportsEsports
                                                                        "Subway Surfers" -> Icons.Default.DirectionsRun
                                                                        "Instagram" -> Icons.Default.CameraAlt
                                                                        else -> Icons.Default.Check
                                                                    }
                                                                    Icon(appSymbol, contentDescription = appTitle, tint = accentColor, modifier = Modifier.size(20.dp))
                                                                }

                                                                Spacer(modifier = Modifier.width(8.dp))

                                                                Column(modifier = Modifier.weight(1f)) {
                                                                    Text(appTitle, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                                    Text("4.8 ★", color = Color.Gray, fontSize = 7.sp)
                                                                }

                                                                if (isAlreadyInstalled) {
                                                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                                        Button(
                                                                            onClick = { installedApps.remove(appName) },
                                                                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.2f)),
                                                                            contentPadding = PaddingValues(horizontal = 6.dp),
                                                                            modifier = Modifier.height(22.dp)
                                                                        ) {
                                                                            Text("Remove", color = Color.Red, fontSize = 7.sp)
                                                                        }
                                                                        Button(
                                                                            onClick = { activeSimulatedAppId = appName },
                                                                            colors = ButtonDefaults.buttonColors(containerColor = HiOSGreen),
                                                                            contentPadding = PaddingValues(horizontal = 8.dp),
                                                                            modifier = Modifier.height(22.dp)
                                                                        ) {
                                                                            Text("Open", color = Color.White, fontSize = 7.sp)
                                                                        }
                                                                    }
                                                                } else if (isCurrentlyInstalling) {
                                                                    Text("${(installProgress * 100).toInt()}%...", color = HologramTeal, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                                                } else {
                                                                    Button(
                                                                        onClick = {
                                                                            scope.launch {
                                                                                installingAppId = appName
                                                                                installProgress = 0.0f
                                                                                while (installProgress < 1.0f) {
                                                                                    delay(120)
                                                                                    installProgress += 0.2f
                                                                                }
                                                                                installedApps.add(appName)
                                                                                installingAppId = null
                                                                            }
                                                                        },
                                                                        colors = ButtonDefaults.buttonColors(containerColor = HologramTeal),
                                                                        contentPadding = PaddingValues(horizontal = 8.dp),
                                                                        modifier = Modifier.height(22.dp)
                                                                    ) {
                                                                        Text("Install", color = Color.Black, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            } else if (activeSimulatedAppId != null) {
                                                // ACTIVE APP IMMERSION VIEWPORTS
                                                when (activeSimulatedAppId) {
                                                    "WhatsApp" -> {
                                                        Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0C141B))) {
                                                            // Green App Header
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth().background(Color(0xFF13212C)).padding(6.dp),
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.SpaceBetween
                                                            ) {
                                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                                    Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(Color(0xFF25D366)), contentAlignment = Alignment.Center) {
                                                                        Icon(Icons.Default.Person, contentDescription = "User", tint = Color.White, modifier = Modifier.size(12.dp))
                                                                    }
                                                                    Spacer(modifier = Modifier.width(6.dp))
                                                                    Column {
                                                                        Text("Qasim Murtaza", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                                        Text("Active Sync", color = Color(0xFF25D366), fontSize = 6.sp)
                                                                    }
                                                                }
                                                                IconButton(onClick = { activeSimulatedAppId = null }, modifier = Modifier.size(20.dp)) {
                                                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray, modifier = Modifier.size(12.dp))
                                                                }
                                                            }

                                                            // Message feed stream
                                                            LazyColumn(
                                                                modifier = Modifier.weight(1f).fillMaxWidth().padding(4.dp),
                                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                                            ) {
                                                                items(whatsappMessages) { m ->
                                                                    val isUserMessage = m.startsWith("ME:")
                                                                    val msgBody = if (isUserMessage) m.substring(3) else m

                                                                    Row(
                                                                        modifier = Modifier.fillMaxWidth(),
                                                                        horizontalArrangement = if (isUserMessage) Arrangement.End else Arrangement.Start
                                                                    ) {
                                                                        Card(
                                                                            colors = CardDefaults.cardColors(containerColor = if (isUserMessage) Color(0xFF005C4B) else Color(0xFF202C33)),
                                                                            shape = RoundedCornerShape(6.dp),
                                                                            modifier = Modifier.widthIn(max = 200.dp)
                                                                        ) {
                                                                            Text(msgBody, color = Color.White, fontSize = 8.sp, modifier = Modifier.padding(6.dp))
                                                                        }
                                                                    }
                                                                }
                                                            }

                                                            // TextInput send row
                                                            Row(modifier = Modifier.fillMaxWidth().padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                                                Card(
                                                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2C34)),
                                                                    shape = RoundedCornerShape(12.dp),
                                                                    modifier = Modifier.weight(1f)
                                                                ) {
                                                                    Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                                                        androidx.compose.foundation.text.BasicTextField(
                                                                            value = messageText,
                                                                            onValueChange = { messageText = it },
                                                                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 8.sp),
                                                                            modifier = Modifier.weight(1f)
                                                                        )
                                                                    }
                                                                }
                                                                Spacer(modifier = Modifier.width(4.dp))
                                                                Box(
                                                                    modifier = Modifier
                                                                        .size(24.dp)
                                                                        .clip(CircleShape)
                                                                        .background(Color(0xFF00A884))
                                                                        .clickable {
                                                                            if (messageText.isNotBlank()) {
                                                                                whatsappMessages.add("ME:" + messageText)
                                                                                val contentInput = messageText
                                                                                messageText = ""
                                                                                scope.launch {
                                                                                    delay(1000)
                                                                                    if (contentInput.lowercase().contains("hi") || contentInput.lowercase().contains("hello")) {
                                                                                        whatsappMessages.add("Assalamu Alaikum! Qasim Murtaza edition is fully compiled. Everything works smoothly! 🚀")
                                                                                    } else {
                                                                                        whatsappMessages.add("Excellent! Checking diagnostic parameters on your Spark 20 Tablet layout!")
                                                                                    }
                                                                                }
                                                                            }
                                                                        },
                                                                    contentAlignment = Alignment.Center
                                                                ) {
                                                                    Icon(Icons.Default.Send, contentDescription = "Send Message", tint = Color.White, modifier = Modifier.size(11.dp))
                                                                }
                                                            }
                                                        }
                                                    }
                                                    "PUBG MOBILE" -> {
                                                        Column(
                                                            modifier = Modifier.fillMaxSize().background(Color(0xFF10131E)),
                                                            horizontalAlignment = Alignment.CenterHorizontally,
                                                            verticalArrangement = Arrangement.SpaceBetween
                                                        ) {
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth().padding(4.dp),
                                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                Text("PUBG MOBILE LOBBY", color = Color(0xFFF57C00), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                                                IconButton(onClick = { activeSimulatedAppId = null }, modifier = Modifier.size(16.dp)) {
                                                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(10.dp))
                                                                }
                                                            }

                                                            if (pubgMatchActive) {
                                                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(10.dp)) {
                                                                    Text("MATCH RUNNING (120 FPS)", color = HiOSGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                                    Spacer(modifier = Modifier.height(4.dp))
                                                                    Text("Opponents Eliminated: $pubgKills", color = Color.White, fontSize = 8.sp)
                                                                    Spacer(modifier = Modifier.height(12.dp))
                                                                    Box(
                                                                        modifier = Modifier
                                                                            .size(64.dp)
                                                                            .clip(CircleShape)
                                                                            .background(Color.Red.copy(alpha = 0.2f))
                                                                            .border(1.5.dp, Color.Red, CircleShape)
                                                                            .clickable {
                                                                                pubgKills = (pubgKills + 1).coerceAtMost(15)
                                                                                if (pubgKills >= 15) {
                                                                                    pubgMatchActive = false
                                                                                }
                                                                            },
                                                                        contentAlignment = Alignment.Center
                                                                    ) {
                                                                        Text("FIRE", color = Color.Red, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                                                    }
                                                                }
                                                            } else {
                                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                                    Icon(Icons.Default.SportsEsports, contentDescription = "Helmet", tint = SparkAmber, modifier = Modifier.size(36.dp))
                                                                    Spacer(modifier = Modifier.height(4.dp))
                                                                    Text(if (pubgKills >= 15) "★ CHICKEN DINNER! WINNER! ★" else "READY UP COMMANDER", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                                                    Spacer(modifier = Modifier.height(8.dp))
                                                                    Button(
                                                                        onClick = { pubgMatchActive = true; pubgKills = 0 },
                                                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57C00)),
                                                                        modifier = Modifier.height(24.dp),
                                                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                                                    ) {
                                                                        Text("START", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                                                    }
                                                                }
                                                            }
                                                            Text("Graphics: UHD • CPU Status: Super Fast", color = Color.Gray, fontSize = 6.sp, modifier = Modifier.padding(bottom = 4.dp))
                                                        }
                                                    }
                                                    "Subway Surfers" -> {
                                                        Column(
                                                            modifier = Modifier.fillMaxSize().background(Color(0xFF2E1503)),
                                                            horizontalAlignment = Alignment.CenterHorizontally,
                                                            verticalArrangement = Arrangement.SpaceBetween
                                                        ) {
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth().padding(4.dp),
                                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                Text("Subway Surfers", color = Color(0xFFFFD54F), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                                                IconButton(onClick = { activeSimulatedAppId = null }, modifier = Modifier.size(16.dp)) {
                                                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(10.dp))
                                                                }
                                                            }

                                                            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                                                Text("🪙 Coins: $subwayCoins", color = Color(0xFFFFD54F), fontSize = 8.sp)
                                                                Text("Score: $subwayScore", color = Color.White, fontSize = 8.sp)
                                                            }

                                                            Box(
                                                                modifier = Modifier.size(70.dp),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Icon(
                                                                    Icons.Default.DirectionsRun,
                                                                    contentDescription = "Jake character",
                                                                    tint = Color(0xFFFFD54F),
                                                                    modifier = Modifier.size(36.dp).offset(
                                                                        x = if (subwayLane == 0) (-15).dp else if (subwayLane == 2) 15.dp else 0.dp,
                                                                        y = subwayYOffset.dp
                                                                    )
                                                                )
                                                            }

                                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                                                IconButton(onClick = { subwayLane = (subwayLane - 1).coerceAtLeast(0); subwayScore += 100 }, modifier = Modifier.size(24.dp)) {
                                                                    Icon(Icons.Default.ArrowBack, contentDescription = "Left", tint = Color.White)
                                                                }
                                                                IconButton(onClick = {
                                                                    scope.launch { subwayYOffset = -15; subwayCoins += 10; delay(300); subwayYOffset = 0 }
                                                                }, modifier = Modifier.size(24.dp)) {
                                                                    Icon(Icons.Default.ArrowUpward, contentDescription = "Up", tint = Color.White)
                                                                }
                                                                IconButton(onClick = { subwayLane = (subwayLane + 1).coerceAtMost(2); subwayScore += 100 }, modifier = Modifier.size(24.dp)) {
                                                                    Icon(Icons.Default.ArrowForward, contentDescription = "Right", tint = Color.White)
                                                                }
                                                            }
                                                            Spacer(modifier = Modifier.height(2.dp))
                                                        }
                                                    }
                                                    "Instagram" -> {
                                                        Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth().padding(4.dp),
                                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                Text("Instagram", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                                IconButton(onClick = { activeSimulatedAppId = null }, modifier = Modifier.size(16.dp)) {
                                                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(10.dp))
                                                                }
                                                            }
                                                            Card(
                                                                modifier = Modifier.fillMaxWidth().weight(1f).padding(4.dp).clickable { instagramLiked = !instagramLiked },
                                                                colors = CardDefaults.cardColors(containerColor = Color(0xFF141414))
                                                            ) {
                                                                Column(
                                                                    modifier = Modifier.fillMaxSize().padding(6.dp),
                                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                                    verticalArrangement = Arrangement.Center
                                                                ) {
                                                                    Text("Post by Qasim Murtaza", color = SparkAmber, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                                    Spacer(modifier = Modifier.height(6.dp))
                                                                    Icon(
                                                                        if (instagramLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                                        contentDescription = "Like",
                                                                        tint = if (instagramLiked) Color.Red else Color.White,
                                                                        modifier = Modifier.size(28.dp)
                                                                    )
                                                                    Spacer(modifier = Modifier.height(4.dp))
                                                                    Text("Double Tap to Like this Masterpiece!", color = Color.Gray, fontSize = 7.sp)
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            } else {
                                                // STANDARD TABLET LAUNCHER ENVIRONMENT
                                                Column(
                                                    modifier = Modifier.fillMaxSize(),
                                                    verticalArrangement = Arrangement.SpaceBetween,
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    // High-contrast lock/clock section widget
                                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                        val dateStr = java.text.SimpleDateFormat("EEEE, dd MMM", java.util.Locale.getDefault()).format(java.util.Date())
                                                        Text(
                                                            text = dateStr,
                                                            color = Color.LightGray,
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )

                                                        Spacer(modifier = Modifier.height(2.dp))

                                                        Text(
                                                            text = if (memFusionEnabled) "Memory Boost: 16GB (8+8)" else "System RAM: 8GB",
                                                            fontSize = 8.sp,
                                                            color = HologramTeal,
                                                            fontWeight = FontWeight.Bold
                                                        )

                                                        Spacer(modifier = Modifier.height(10.dp))

                                                        // Interactive diagnostic center indicator card
                                                        Card(
                                                            colors = CardDefaults.cardColors(containerColor = GlassWhite),
                                                            shape = RoundedCornerShape(12.dp),
                                                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                                                            modifier = Modifier.clickable { triggerFaceUnlock() }
                                                        ) {
                                                            Column(
                                                                modifier = Modifier.padding(6.dp),
                                                                horizontalAlignment = Alignment.CenterHorizontally
                                                            ) {
                                                                if (showFaceUnlockCheck) {
                                                                    Icon(Icons.Default.Face, contentDescription = "Scanning face scan", tint = HologramTeal, modifier = Modifier.size(22.dp))
                                                                    Text("Scanning Face ID...", fontSize = 7.sp, color = HologramTeal, fontWeight = FontWeight.Bold)
                                                                } else {
                                                                    Text("HiOS 13.0 Active Panel", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                                                    Text("Click here to Scan Face ID", fontSize = 7.sp, color = Color.White.copy(alpha = 0.7f))
                                                                }
                                                            }
                                                        }
                                                    }

                                                    // SPACIOUS TABLET LAYOUT - DYNAMIC LAUNCHER SCREEN GRID
                                                    Column(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalAlignment = Alignment.CenterHorizontally
                                                    ) {
                                                        // Chunk items 4 per row elegantly for standard tablet look!
                                                        val chunkedAppsList = installedApps.chunked(4)
                                                        chunkedAppsList.forEach { rowApps ->
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                                                horizontalArrangement = Arrangement.SpaceEvenly
                                                            ) {
                                                                rowApps.forEach { isApp ->
                                                                    val appBackGradient = when (isApp) {
                                                                        "Settings" -> HiOSBlue
                                                                        "Benchmark" -> ThermalRed
                                                                        "Phone" -> HiOSGreen
                                                                        "DTS DTS" -> HiOSViolet
                                                                        "Camera" -> SparkAmber
                                                                        "Charge" -> HiOSGreen
                                                                        "Play Store" -> Color(0xFF109D59)
                                                                        "WhatsApp" -> Color(0xFF25D366)
                                                                        "PUBG MOBILE" -> Color(0xFFF57C00)
                                                                        "Subway Surfers" -> Color(0xFFFFD54F)
                                                                        "Instagram" -> Color(0xFFE1306C)
                                                                        else -> Color.DarkGray
                                                                    }

                                                                    val appSymbolVector = when (isApp) {
                                                                        "Settings" -> Icons.Default.Settings
                                                                        "Benchmark" -> Icons.Default.Speed
                                                                        "Phone" -> Icons.Default.Call
                                                                        "DTS DTS" -> Icons.Default.Audiotrack
                                                                        "Camera" -> Icons.Default.PhotoCamera
                                                                        "Charge" -> Icons.Default.Power
                                                                        "Play Store" -> Icons.Default.Shop
                                                                        "WhatsApp" -> Icons.Default.Message
                                                                        "PUBG MOBILE" -> Icons.Default.SportsEsports
                                                                        "Subway Surfers" -> Icons.Default.DirectionsRun
                                                                        "Instagram" -> Icons.Default.CameraAlt
                                                                        else -> Icons.Default.Android
                                                                    }

                                                                    MiniAppBadge(
                                                                        title = isApp,
                                                                        icon = appSymbolVector,
                                                                        colorBack = appBackGradient
                                                                    ) {
                                                                        if (isApp == "Play Store") {
                                                                            openPlayStoreApp = true
                                                                        } else if (isApp == "Phone") {
                                                                            showIncomingCall = true
                                                                        } else if (isApp == "Charge") {
                                                                            onChargerToggled(!chargerConnected)
                                                                        } else if (isApp == "WhatsApp" || isApp == "PUBG MOBILE" || isApp == "Subway Surfers" || isApp == "Instagram") {
                                                                            activeSimulatedAppId = isApp
                                                                        } else {
                                                                            // trigger soft alert confirmation or log
                                                                            triggerFaceUnlock()
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        // SLEEK HiOS SOFT NAVIGATION BAR AT THE BOTTOM OF THE TABLET SCREEN
                                        Row(
                                            modifier = Modifier
                                                .align(Alignment.BottomCenter)
                                                .fillMaxWidth()
                                                .height(28.dp)
                                                .background(Color.Black.copy(alpha = 0.5f))
                                                .padding(horizontal = 24.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Soft Back Navigation Button
                                            Icon(
                                                imageVector = Icons.Default.ArrowBack,
                                                contentDescription = "Soft Back Button",
                                                tint = Color.White.copy(alpha = 0.7f),
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clickable {
                                                        if (activeSimulatedAppId != null) {
                                                            activeSimulatedAppId = null
                                                        } else if (openPlayStoreApp) {
                                                            openPlayStoreApp = false
                                                        }
                                                    }
                                            )
                                            // Soft Home Navigation Button
                                            Icon(
                                                imageVector = Icons.Default.Circle,
                                                contentDescription = "Soft Home Button",
                                                tint = Color.White.copy(alpha = 0.7f),
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .clickable {
                                                        activeSimulatedAppId = null
                                                        openPlayStoreApp = false
                                                    }
                                            )
                                            // Soft Recents Diagnostic Info Button
                                            Icon(
                                                imageVector = Icons.Default.CropSquare,
                                                contentDescription = "Soft Recents Button",
                                                tint = Color.White.copy(alpha = 0.7f),
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .clickable {
                                                        triggerFaceUnlock()
                                                    }
                                            )
                                        }

                                        // **DYNAMIC PORT** - THE MASTERPIECE OF TECNO SPARK 20
                                        var isPortExpanded = chargerConnected || isMusicPlaying || showFaceUnlockCheck || showIncomingCall || isCallActive

                                        val density = androidx.compose.ui.platform.LocalDensity.current
                                        val targetWidth by animateDpAsState(
                                            targetValue = if (showIncomingCall) 210.dp 
                                                            else if (chargerConnected) 180.dp 
                                                            else if (isMusicPlaying) 190.dp 
                                                            else if (showFaceUnlockCheck) 130.dp 
                                                            else 24.dp, // standard tiny punch hole size
                                            animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessLow),
                                            label = "portWidth"
                                        )

                                        val targetHeight by animateDpAsState(
                                            targetValue = if (showIncomingCall) 45.dp else if (isPortExpanded) 25.dp else 24.dp,
                                            animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessLow),
                                            label = "portHeight"
                                        )

                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopCenter)
                                                .padding(top = 8.dp)
                                                .width(targetWidth)
                                                .height(targetHeight)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color.Black)
                                                .border(0.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                                .clickable {
                                                    if (showIncomingCall) {
                                                        showIncomingCall = false
                                                        isCallActive = true
                                                    } else if (isCallActive) {
                                                        isCallActive = false
                                                    }
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (showIncomingCall) {
                                                // Expanded Dynamic Island for Simulated Call
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Icon(Icons.Default.Call, contentDescription = "Calling icon", tint = HiOSGreen, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Aura Call: CEO", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    // Simulated buttons inside Dynamic Port!
                                                    Card(
                                                        colors = CardDefaults.cardColors(containerColor = HiOSGreen),
                                                        shape = RoundedCornerShape(4.dp),
                                                        modifier = Modifier.height(16.dp)
                                                    ) {
                                                        Text("Answer", color = Color.White, fontSize = 7.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                                                    }
                                                }
                                            } else if (chargerConnected) {
                                                // Dynamic Port Charging details
                                                Row(
                                                    modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.Center
                                                ) {
                                                    Icon(Icons.Default.Power, contentDescription = "charging", tint = SparkAmber, modifier = Modifier.size(11.dp))
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text(
                                                        text = "18W Fast Loading: $batteryPercentage%",
                                                        color = SparkAmber,
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            } else if (isMusicPlaying) {
                                                // Dynamic Port Music details
                                                Row(
                                                    modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(Icons.Default.MusicNote, contentDescription = "music", tint = HiOSViolet, modifier = Modifier.size(10.dp))
                                                        Spacer(modifier = Modifier.width(2.dp))
                                                        Text(
                                                            text = activeMusicTitle,
                                                            color = Color.White,
                                                            fontSize = 8.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            maxLines = 1
                                                        )
                                                    }
                                                    // Tiny reactive equalizer bars inside Dynamic Island
                                                    Row(horizontalArrangement = Arrangement.spacedBy(1.dp), verticalAlignment = Alignment.CenterVertically) {
                                                        Box(modifier = Modifier.size(width = 1.5.dp, height = 8.dp).background(HiOSGreen))
                                                        Box(modifier = Modifier.size(width = 1.5.dp, height = 5.dp).background(HiOSGreen))
                                                        Box(modifier = Modifier.size(width = 1.5.dp, height = 7.dp).background(HiOSGreen))
                                                    }
                                                }
                                            } else if (showFaceUnlockCheck) {
                                                // Dynamic Port face scanner text
                                                Row(
                                                    modifier = Modifier.fillMaxSize(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.Center
                                                ) {
                                                    Icon(Icons.Default.LockOpen, contentDescription = "Scanning", tint = HologramTeal, modifier = Modifier.size(11.dp))
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text("Face ID Authed", color = HologramTeal, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                                }
                                            } else {
                                                // Standard circular punch hole representation
                                                Box(
                                                    modifier = Modifier
                                                        .size(10.dp)
                                                        .clip(CircleShape)
                                                        .background(Color.Black)
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                // Locked state screen
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "SPARK",
                                            fontWeight = FontWeight.Black,
                                            color = Color.White.copy(alpha = 0.05f),
                                            fontSize = 24.sp,
                                            fontFamily = FontFamily.SansSerif
                                        )
                                        Text(
                                            text = "Tablet is off. Click Power Button to wake up",
                                            fontSize = 8.sp,
                                            color = Color.White.copy(alpha = 0.3f),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // BACK OF THE PHONE DESIGN RENDERING
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(29.dp))
                                .background(selectedColor.color)
                        ) {
                            // If Magic Skin Eco-leather is selected, render nice stitch lines using Custom Canvas background!
                            if (selectedColor.isLeather) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val w = size.width
                                    val h = size.height

                                    // Draw stitch borders
                                    val pathBorder = Path()
                                    pathBorder.addRoundRect(
                                        androidx.compose.ui.geometry.RoundRect(
                                            left = 8.dp.toPx(),
                                            top = 8.dp.toPx(),
                                            right = w - 8.dp.toPx(),
                                            bottom = h - 8.dp.toPx(),
                                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx())
                                        )
                                    )

                                    drawPath(
                                        path = pathBorder,
                                        color = Color.White.copy(alpha = 0.35f),
                                        style = Stroke(
                                            width = 1.dp.toPx(),
                                            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                                                floatArrayOf(10f, 10f),
                                                0f
                                            )
                                        )
                                    )
                                }
                            }

                            // Distinct linear back stellar elements
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // 3-ring metallic camera square layout (Looks like iPhone Pro style)
                                    Card(
                                        modifier = Modifier
                                            .size(90.dp)
                                            .border(
                                                width = 1.dp,
                                                color = Color.White.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(18.dp)
                                            )
                                            .testTag("camera_module_back"),
                                        colors = CardDefaults.cardColors(
                                            containerColor = selectedColor.color.copy(alpha = 0.85f)
                                        ),
                                        shape = RoundedCornerShape(18.dp),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                                    ) {
                                        Box(modifier = Modifier.fillMaxSize()) {
                                            // Ring 1: 50MP Main Lens (Top Left)
                                            CameraRingItem(
                                                modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
                                                colorId = selectedColor.color,
                                                megapixels = "50M"
                                            )

                                            // Ring 2: Auxiliary Smart AI Lens (Bottom Left)
                                            CameraRingItem(
                                                modifier = Modifier.align(Alignment.BottomStart).padding(8.dp),
                                                colorId = selectedColor.color,
                                                megapixels = "AI"
                                            )

                                            // Ring 3: Dual Temperature LED Ring Flash (Top Right/Center)
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.CenterEnd)
                                                    .padding(end = 10.dp)
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(Color.White.copy(alpha = 0.18f))
                                                    .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                                                    .padding(3.dp)
                                            ) {
                                                // Dual core flash diodes
                                                Column(
                                                    modifier = Modifier.fillMaxSize(),
                                                    verticalArrangement = Arrangement.SpaceAround,
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(SparkAmber))
                                                    Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(Color.White))
                                                }
                                            }

                                            // "50MP" metadata text printed in small font
                                            Text(
                                                text = "ULTRA CLEAR\nPRO SENSOR",
                                                color = Color.White.copy(alpha = 0.5f),
                                                fontSize = 5.sp,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center,
                                                lineHeight = 6.sp,
                                                modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 6.dp, end = 6.dp)
                                            )
                                        }
                                    }

                                    // Small dynamic logo for the simulation details if needed
                                }

                                // Bottom engraved brand logo: "TECN0 SPARK"
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "TECNO SPARK",
                                        color = if (selectedColor.name == "Cyber White") Color.Black.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.8f),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 2.sp
                                    )
                                    Text(
                                        text = "DESIGNED BY TECNO LABS • IP53",
                                        color = if (selectedColor.name == "Cyber White") Color.Black.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.4f),
                                        fontSize = 6.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Physical Buttons Panel housing keys
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.width(55.dp)
                ) {
                    Text(
                        "Buttons",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.padding(start = 2.dp)
                    )

                    // Volume + Key
                    Button(
                        onClick = {
                            if (isPhoneOn) {
                                currentVolume = (currentVolume + 1).coerceAtMost(15)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .testTag("vol_up_chassis_button"),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Vol Up Key", modifier = Modifier.size(16.dp))
                    }

                    // Volume - Key
                    Button(
                        onClick = {
                            if (isPhoneOn) {
                                currentVolume = (currentVolume - 1).coerceAtLeast(0)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .testTag("vol_down_chassis_button"),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.VolumeDown, contentDescription = "Vol Down Key", modifier = Modifier.size(16.dp))
                    }

                    // Power Key (Wake / Sleep)
                    Button(
                        onClick = { isPhoneOn = !isPhoneOn },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPhoneOn) HiOSRed else HiOSGreen
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("power_chassis_button"),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = "Power Key",
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Footer specification parameters controls: Memory upgrade toggle + fast charger connect toggle
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.05f), RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (memFusionEnabled) "MemFusion Config: 8GB + 8GB" else "MemFusion Config: Disabled",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Expands active RAM allocations to total 16GB dynamic speed",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                    Switch(
                        checked = memFusionEnabled,
                        onCheckedChange = onMemFusionToggled,
                        modifier = Modifier.testTag("memfusion_hub_switch")
                    )
                }

                Divider(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (chargerConnected) "🔌 18W Fast Charger Connected" else "🔌 Plug-in 18W Charger",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (chargerConnected) SparkAmber else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Triggers rapid charging animations and activates the Dynamic Port notification",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                    Switch(
                        checked = chargerConnected,
                        onCheckedChange = onChargerToggled,
                        modifier = Modifier.testTag("charger_hub_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SparkAmber,
                            checkedTrackColor = SparkAmber.copy(alpha = 0.4f)
                        )
                    )
                }
            }
        }
    }
}

// Subcomponents for back panel physical layout
@Composable
fun CameraRingItem(
    modifier: Modifier = Modifier,
    colorId: Color,
    megapixels: String
) {
    Box(
        modifier = modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(Color.Black)
            .border(1.5.dp, Color.White.copy(alpha = 0.18f), CircleShape)
            .border(0.5.dp, colorId.copy(alpha = 0.8f), CircleShape)
            .padding(1.dp),
        contentAlignment = Alignment.Center
    ) {
        // Double lens effect inside concentric path
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(Brush.radialGradient(colors = listOf(Color(0xFF0F121E), Color(0xFF040609))))
                .padding(3.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF030E14))
                    .border(0.5.dp, Color(0xFF00E5FF).copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Tiny reflection white dot
                Box(
                    modifier = Modifier
                        .size(2.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.8f))
                        .align(Alignment.TopEnd)
                )
            }
        }

        // Tiny text floating
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(
                text = megapixels,
                fontSize = 5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White.copy(alpha = 0.4f)
            )
        }
    }
}

// Clean representation of volume slider blocks inside physical phone screen
@Composable
fun VerticalSliderReplacement(current: Int, max: Int) {
    Column(
        modifier = Modifier
            .height(70.dp)
            .width(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(Color.White.copy(alpha = 0.15f)),
        verticalArrangement = Arrangement.Bottom
    ) {
        val filledPercentage = current.toFloat() / max.toFloat()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(filledPercentage)
                .background(HiOSBlue)
        )
    }
}

// Styled labels inside operating system launcher
@Composable
fun MiniAppBadge(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    colorBack: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(55.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colorBack),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "$title app",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            fontSize = 7.gsp, // custom scaled text font size for physical display
            color = Color.White.copy(alpha = 0.85f),
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

// Inline value scaling descriptor
val Int.gsp: androidx.compose.ui.unit.TextUnit
    get() = this.sp
