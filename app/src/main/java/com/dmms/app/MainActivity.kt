package com.dmms.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        setContent { DmmsApp() }
    }
}

val DarkBg = Color(0xFF1E1E1E)
val PanelBg = Color(0xFF252525)
val BorderColor = Color(0xFF333333)
val AccentGreen = Color(0xFF00E676)
val TextGray = Color(0xFFB0B0B0)
val SelectedGreen = Color(0xFF004D40)

@Composable
fun DmmsApp() {
    var showSplash by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(2000) 
        showSplash = false
    }
    Crossfade(targetState = showSplash, animationSpec = tween(1000), label = "Splash") { isSplash ->
        if (isSplash) SplashScreen() else MainStudio()
    }
}

@Composable
fun SplashScreen() {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Image(
            painter = painterResource(id = R.drawable.dmms_logo),
            contentDescription = "DMMS Logo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop 
        )
        CircularProgressIndicator(color = AccentGreen, modifier = Modifier.align(Alignment.Center).padding(top = 250.dp))
        Text("Inspired by Linux MultiMedia Studio (LMMS)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp).background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 6.dp))
    }
}

@Composable
fun MainStudio() {
    var selectedTab by remember { mutableStateOf("Project") }
    var currentProject: LmmsProject? by remember { mutableStateOf(null) }
    var currentBpm: Int by remember { mutableStateOf(140) }

    Column(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        // FULLY POPULATED TOP BAR
        TopBar(
            bpm = currentBpm,
            onLoadClick = {
                currentProject = LmmsParser.loadTestProject()
                currentBpm = currentProject?.bpm ?: 140
            }
        )
        
        Row(modifier = Modifier.fillMaxSize()) {
            Sidebar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
            
            if (selectedTab == "Project") {
                Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                    
                    // TOP PANEL: Song Editor (Timeline)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.4f)
                            .background(PanelBg)
                            .border(1.dp, BorderColor)
                    ) {
                        Text("Song Editor (Timeline Coming Soon)", color = TextGray, modifier = Modifier.align(Alignment.Center))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // BOTTOM PANELS: Piano Roll & Mixer
                    Row(modifier = Modifier.fillMaxWidth().weight(0.6f)) {
                        
                        // Bottom Left: Piano Roll
                        Box(
                            modifier = Modifier
                                .weight(0.65f)
                                .fillMaxHeight()
                                .background(PanelBg)
                                .border(1.dp, BorderColor)
                        ) {
                            PianoRoll(project = currentProject)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Bottom Right: Mixer
                        Box(
                            modifier = Modifier
                                .weight(0.35f)
                                .fillMaxHeight()
                                .background(PanelBg)
                                .border(1.dp, BorderColor)
                        ) {
                            Text("Mixer", color = TextGray, modifier = Modifier.align(Alignment.Center))
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("$selectedTab Screen (Coming Soon)", color = TextGray)
                }
            }
        }
    }
}

@Composable
fun PianoRoll(project: LmmsProject?) {
    Row(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.width(40.dp).fillMaxHeight()) {
            val keyHeight = 20f
            for (i in 0..40) {
                drawRect(color = Color.White, topLeft = Offset(0f, i * keyHeight), size = Size(size.width, keyHeight))
                if (i % 2 == 1) {
                    drawRect(color = Color.Black, topLeft = Offset(0f, i * keyHeight), size = Size(size.width * 0.6f, keyHeight))
                }
                drawLine(color = Color.Gray, start = Offset(0f, i * keyHeight), end = Offset(size.width, i * keyHeight), strokeWidth = 1f)
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridSize = 20f
            for (i in 0..(size.width / gridSize).toInt()) {
                drawLine(color = Color.DarkGray, start = Offset(i * gridSize, 0f), end = Offset(i * gridSize, size.height), strokeWidth = 1f)
            }
            for (i in 0..(size.height / gridSize).toInt()) {
                drawLine(color = Color.DarkGray, start = Offset(0f, i * gridSize), end = Offset(size.width, i * gridSize), strokeWidth = 1f)
            }

            project?.tracks?.filter { it.type == "instrument" }?.forEach { track: LmmsTrack ->
                track.notes.forEach { note: LmmsNote ->
                    val xPos = note.pos * 0.5f 
                    val yPos = (72 - note.key) * gridSize 
                    drawRect(
                        color = AccentGreen,
                        topLeft = Offset(xPos, yPos),
                        size = Size(note.len * 0.5f, gridSize - 1f)
                    )
                }
            }
        }
    }
}

@Composable
fun TopBar(bpm: Int, onLoadClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PanelBg)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // LEFT: Logo & Transport
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Placeholder for Logo
            Text("🎵 DMMS", color = AccentGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(end = 16.dp))
            
            Button(
                onClick = { }, 
                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen), 
                shape = RoundedCornerShape(8.dp), 
                modifier = Modifier.size(40.dp), 
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(Icons.Filled.PlayArrow, contentDescription = "Play", tint = Color.Black, modifier = Modifier.size(28.dp))
            }
            IconButton(onClick = { }) { Text("⏸", color = Color.White, fontSize = 20.sp) }
            IconButton(onClick = { }) { Text("⏹", color = Color.White, fontSize = 20.sp) }
            
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { }) { Icon(Icons.Filled.Refresh, contentDescription = "Loop", tint = TextGray, modifier = Modifier.size(20.dp)) }
        }

        // MIDDLE: BPM, Time Signature, Time
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$bpm", color = AccentGreen, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("BPM", color = TextGray, fontSize = 9.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("4/4", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("TS", color = TextGray, fontSize = 9.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("00:00:00", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("MIN SEC MS", color = TextGray, fontSize = 9.sp)
            }
        }

        // RIGHT: Save, Load, Settings, Menu
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { }) { Icon(Icons.Filled.Save, contentDescription = "Save", tint = TextGray) }
            IconButton(onClick = onLoadClick) { Icon(Icons.Filled.Menu, contentDescription = "Load", tint = AccentGreen) }
            IconButton(onClick = { }) { Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = TextGray) }
            IconButton(onClick = { }) { Icon(Icons.Filled.MoreVert, contentDescription = "Menu", tint = TextGray) }
        }
    }
}

@Composable
fun Sidebar(selectedTab: String, onTabSelected: (String) -> Unit) {
    Column(modifier = Modifier.width(80.dp).fillMaxHeight().background(DarkBg).padding(top = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        SidebarItem(Icons.Filled.Home, "Project", selectedTab, onTabSelected)
        SidebarItem(Icons.Filled.List, "Samples", selectedTab, onTabSelected)
        SidebarItem(Icons.Filled.PlayArrow, "Instr.", selectedTab, onTabSelected)
        SidebarItem(Icons.Filled.Settings, "Effects", selectedTab, onTabSelected)
        SidebarItem(Icons.Filled.Settings, "Settings", selectedTab, onTabSelected)
    }
}

@Composable
fun SidebarItem(icon: ImageVector, label: String, selectedTab: String, onTabSelected: (String) -> Unit) {
    val isSelected = selectedTab == label
    val bgColor = if (isSelected) SelectedGreen else Color.Transparent
    val iconColor = if (isSelected) AccentGreen else TextGray
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().clickable { onTabSelected(label) }.background(bgColor).padding(vertical = 12.dp)) {
        Icon(icon, contentDescription = label, tint = iconColor, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = iconColor, fontSize = 10.sp)
    }
}