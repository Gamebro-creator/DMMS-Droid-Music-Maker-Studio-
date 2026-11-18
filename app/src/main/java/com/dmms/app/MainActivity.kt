package com.dmms.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DmmsApp()
        }
    }
}

// Define our colors based on your Gemini image
val DarkBg = Color(0xFF1E1E1E)
val PanelBg = Color(0xFF2A2A2A)
val AccentGreen = Color(0xFF00E676)
val TextGray = Color(0xFFB0B0B0)

@Composable
fun DmmsApp() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // 1. Top Transport Bar
        TopBar()
        
        // 2. Main Content Area (Sidebar + Workspace)
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Sidebar()
            
            // Right Workspace (Where the piano roll will go)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF151515)),
                contentAlignment = Alignment.Center
            ) {
                Text("Piano Roll Coming Soon", color = TextGray)
            }
        }
    }
}

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PanelBg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Transport Buttons (Play, Pause, Stop)
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Professional Play Button
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(48.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.Black,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            
            // Pause Icon
            IconButton(onClick = { }) {
                Icon(Icons.Filled.Pause, contentDescription = "Pause", tint = Color.White)
            }
            
            // Stop Icon
            IconButton(onClick = { }) {
                Icon(Icons.Filled.Stop, contentDescription = "Stop", tint = Color.White)
            }
        }

        // BPM and Time Display
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("140", color = AccentGreen, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("BPM", color = TextGray, fontSize = 10.sp)
            }
            Spacer(modifier = Modifier.width(24.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("00:00:24", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("MIN SEC MS", color = TextGray, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun Sidebar() {
    Column(
        modifier = Modifier
            .width(90.dp)
            .fillMaxHeight()
            .background(PanelBg)
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SidebarItem(Icons.Filled.Home, "Project")
        SidebarItem(Icons.Filled.List, "Samples")
        SidebarItem(Icons.Filled.PlayArrow, "Instr.")
        SidebarItem(Icons.Filled.Build, "Effects")
        SidebarItem(Icons.Filled.Settings, "Settings") 
    }
}

@Composable
fun SidebarItem(icon: ImageVector, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = TextGray,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = TextGray, fontSize = 11.sp)
    }
}