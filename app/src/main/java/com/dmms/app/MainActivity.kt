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
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.input.pointer.pointerInput
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
    
    // We hold the project in a mutable state so we can update the notes
    var currentProject by remember { mutableStateOf<LmmsProject?>(null) }
    var currentBpm by remember { mutableStateOf(140) }

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
            
            if (selectedTab == "Project") {
                Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                    
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

                    Row(modifier = Modifier.fillMaxWidth().weight(0.6f)) {
                        
                        // PIANO ROLL PANEL
                        Box(
                            modifier = Modifier
                                .weight(0.65f)
                                .fillMaxHeight()
                                .background(PanelBg)
                                .border(1.dp, BorderColor)
                        ) {
                            PianoRoll(
                                project = currentProject,
                                onNoteAdded = { newNote ->
                                    // Update the first instrument track with the new note
                                    if (currentProject != null) {
                                        val updatedTracks = currentProject!!.tracks.map { track ->
                                            if (track.type == "instrument") {
                                                track.copy(notes = track.notes + newNote)
                                            } else track
                                        }
                                        currentProject = currentProject!!.copy(tracks = updatedTracks)
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // MIXER PANEL
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
fun PianoRoll(project: LmmsProject?, onNoteAdded: (LmmsNote) -> Unit) {
    val gridSize = 20f // Size of the grid squares

    Row(modifier = Modifier.fillMaxSize()) {
        // LEFT: Piano Keys
        Canvas(modifier = Modifier.width(40.dp).fillMaxHeight()) {
            for (i in 0..40) {
                drawRect(color = Color.White, topLeft = Offset(0f, i * gridSize), size = Size(size.width, gridSize))
                if (i % 2 == 1) {
                    drawRect(color = Color.Black, topLeft = Offset(0f, i * gridSize), size = Size(size.width * 0.6f, gridSize))
                }
                drawLine(color = Color.Gray, start = Offset(0f, i * gridSize), end = Offset(size.width, i * gridSize), strokeWidth = 1f)
            }
        }

        // RIGHT: Interactive Grid
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        // 1. Calculate which grid square was tapped
                        val tappedCol = (offset.x / gridSize).toInt()
                        val tappedRow = (offset.y / gridSize).toInt()

                        // 2. Convert to LMMS data format
                        // We map 1 grid square = 24 ticks (1/8th note) for demo purposes
                        val newPos = tappedCol * 24 
                        val newKey = 72 - tappedRow // Invert Y axis for MIDI pitch

                        // 3. Create the note and send it back to the main state
                        if (newKey in 0..127) { // Ensure it's a valid MIDI note
                            onNoteAdded(LmmsNote(pos = newPos, len = 24, key = newKey, vel = 100))
                        }
                    }
                }
        ) {
            // Draw Grid Lines
            for (i in 0..(size.width / gridSize).toInt()) {
                drawLine(color = Color.DarkGray, start = Offset(i * gridSize, 0f), end = Offset(i * gridSize, size.height), strokeWidth = 1f)
            }
            for (i in 0..(size.height / gridSize).toInt()) {
                drawLine(color = Color.DarkGray, start = Offset(0f, i * gridSize), end = Offset(size.width, i * gridSize), strokeWidth = 1f)
            }

            // Draw Notes
            project?.tracks?.filter { it.type == "instrument" }?.forEach { track ->
                track.notes.forEach { note ->
                    val xPos = (note.pos / 24f) * gridSize 
                    val yPos = (72 - note.key) * gridSize 
                    drawRect(
                        color = AccentGreen,
                        topLeft = Offset(xPos, yPos),
                        size = Size(gridSize - 1f, gridSize - 1f)
                    )
                }
            }
        }
    }
}

@Composable
fun TopBar(bpm: Int, onLoadClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(PanelBg).padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🎵 DMMS", color = AccentGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(end = 16.dp))
            
            Button(onClick = { }, colors = ButtonDefaults.buttonColors(containerColor = AccentGreen), shape = RoundedCornerShape(8.dp), modifier = Modifier.size(40.dp), contentPadding = PaddingValues(0.dp)) {
                Icon(Icons.Filled.PlayArrow, contentDescription = "Play", tint = Color.Black, modifier = Modifier.size(28.dp))
            }
            IconButton(onClick = { }) { Text("⏸", color = Color.White, fontSize = 20.sp) }
            IconButton(onClick = { }) { Text("⏹", color = Color.White, fontSize = 20.sp) }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { }) { Icon(Icons.Filled.Refresh, contentDescription = "Loop", tint = TextGray, modifier = Modifier.size(20.dp)) }
        }

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