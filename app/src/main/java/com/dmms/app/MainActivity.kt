package com.dmms.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
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
val PanelBg = Color(0xFF2A2A2A)
val AccentGreen = Color(0xFF00E676)
val TextGray = Color(0xFFB0B0B0)
val SelectedGreen = Color(0xFF004D40)
val BeatBlue = Color(0xFF00B0FF)

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
    
    // FIX: Explicitly told the compiler this is LmmsProject
    var currentProject: LmmsProject? by remember { mutableStateOf(null) }
    var currentBpm: Int by remember { mutableStateOf(140) }
    
    var editorMode by remember { mutableStateOf("Piano") } 

    Column(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        TopBar(
            bpm = currentBpm,
            onLoadClick = {
                currentProject = LmmsParser.loadTestProject()
                currentBpm = currentProject?.bpm ?: 140
            }
        )
        
        Row(modifier = Modifier.fillMaxSize()) {
            Sidebar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
            
            Column(modifier = Modifier.fillMaxSize().background(Color(0xFF151515))) {
                if (selectedTab == "Project") {
                    
                    Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.Center) {
                        Button(
                            onClick = { editorMode = "Piano" },
                            colors = ButtonDefaults.buttonColors(containerColor = if (editorMode == "Piano") AccentGreen else PanelBg)
                        ) {
                            Text("Piano Roll", color = if (editorMode == "Piano") Color.Black else Color.White)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            onClick = { editorMode = "Beat" },
                            colors = ButtonDefaults.buttonColors(containerColor = if (editorMode == "Beat") BeatBlue else PanelBg)
                        ) {
                            Text("Beat/Bassline", color = if (editorMode == "Beat") Color.Black else Color.White)
                        }
                    }

                    if (editorMode == "Piano") {
                        PianoRoll(project = currentProject)
                    } else {
                        BeatBaselineView(project = currentProject)
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("$selectedTab Screen (Coming Soon)", color = TextGray)
                    }
                }
            }
        }
    }
}

@Composable
fun PianoRoll(project: LmmsProject?) {
    Row(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.width(60.dp).fillMaxHeight()) {
            val keyHeight = 40f
            for (i in 0..30) {
                drawRect(color = Color.White, topLeft = Offset(0f, i * keyHeight), size = Size(size.width, keyHeight))
                if (i % 2 == 1) {
                    drawRect(color = Color.Black, topLeft = Offset(0f, i * keyHeight), size = Size(size.width * 0.6f, keyHeight))
                }
                drawLine(color = Color.Gray, start = Offset(0f, i * keyHeight), end = Offset(size.width, i * keyHeight), strokeWidth = 2f)
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridSize = 40f
            for (i in 0..(size.width / gridSize).toInt()) {
                drawLine(color = Color.DarkGray, start = Offset(i * gridSize, 0f), end = Offset(i * gridSize, size.height), strokeWidth = 1f)
            }
            for (i in 0..(size.height / gridSize).toInt()) {
                drawLine(color = Color.DarkGray, start = Offset(0f, i * gridSize), end = Offset(size.width, i * gridSize), strokeWidth = 1f)
            }

            project?.tracks?.filter { it.type == "instrument" }?.forEach { track: LmmsTrack ->
                track.notes.forEach { note: LmmsNote ->
                    val xPos = note.pos.toFloat() 
                    val yPos = (72 - note.key) * gridSize 
                    drawRect(
                        color = AccentGreen,
                        topLeft = Offset(xPos, yPos),
                        size = Size(note.len.toFloat(), gridSize - 2f)
                    )
                }
            }
        }
    }
}

@Composable
fun BeatBaselineView(project: LmmsProject?) {
    Row(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.width(100.dp).fillMaxHeight().background(PanelBg)) {
            val beatTracks = project?.tracks?.filter { it.type == "beat" } ?: emptyList()
            beatTracks.forEach { track: LmmsTrack ->
                Box(modifier = Modifier.fillMaxWidth().height(50.dp).padding(4.dp), contentAlignment = Alignment.CenterStart) {
                    Text(track.name, color = Color.White, fontSize = 12.sp)
                }
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val rowHeight = 50f
            val stepWidth = 40f
            
            for (i in 0..(size.width / stepWidth).toInt()) {
                drawLine(color = Color.DarkGray, start = Offset(i * stepWidth, 0f), end = Offset(i * stepWidth, size.height), strokeWidth = 1f)
            }
            
            val beatTracks = project?.tracks?.filter { it.type == "beat" } ?: emptyList()
            for (i in 0..beatTracks.size) {
                drawLine(color = Color.DarkGray, start = Offset(0f, i * rowHeight), end = Offset(size.width, i * rowHeight), strokeWidth = 1f)
            }

            beatTracks.forEachIndexed { trackIndex: Int, track: LmmsTrack ->
                track.notes.forEach { note: LmmsNote ->
                    val xPos = note.pos * stepWidth
                    val yPos = trackIndex * rowHeight
                    drawRect(
                        color = BeatBlue,
                        topLeft = Offset(xPos, yPos),
                        size = Size(stepWidth - 2f, rowHeight - 2f)
                    )
                }
            }
        }
    }
}

@Composable
fun TopBar(bpm: Int, onLoadClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().background(PanelBg).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { }, colors = ButtonDefaults.buttonColors(containerColor = AccentGreen), shape = RoundedCornerShape(8.dp), modifier = Modifier.size(48.dp), contentPadding = PaddingValues(0.dp)) {
                Icon(Icons.Filled.PlayArrow, contentDescription = "Play", tint = Color.Black, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(onClick = { }) { Text("⏸", color = Color.White, fontSize = 24.sp) }
            IconButton(onClick = { }) { Text("⏹", color = Color.White, fontSize = 24.sp) }
            Spacer(modifier = Modifier.width(32.dp))
            
            // FIX: Changed Icon to Menu (valid core icon)
            IconButton(onClick = onLoadClick) {
                Icon(Icons.Filled.Menu, contentDescription = "Load", tint = AccentGreen)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$bpm", color = AccentGreen, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("BPM", color = TextGray, fontSize = 10.sp)
            }
            Spacer(modifier = Modifier.width(24.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("00:00:00", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("MIN SEC MS", color = TextGray, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun Sidebar(selectedTab: String, onTabSelected: (String) -> Unit) {
    Column(modifier = Modifier.width(90.dp).fillMaxHeight().background(PanelBg).padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
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
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().clickable { onTabSelected(label) }.background(bgColor).padding(vertical = 16.dp)) {
        Icon(icon, contentDescription = label, tint = iconColor, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = iconColor, fontSize = 11.sp)
    }
}