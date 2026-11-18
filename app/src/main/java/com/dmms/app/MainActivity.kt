package com.dmms.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DmmsApp()
        }
    }
}

val DarkBg = Color(0xFF1E1E1E)
val PanelBg = Color(0xFF2A2A2A)
val AccentGreen = Color(0xFF00E676)
val TextGray = Color(0xFFB0B0B0)

@Composable
fun DmmsApp() {
    var showSplash by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(2500) // I bumped this to 2.5 seconds so you can read the text!
        showSplash = false
    }

    if (showSplash) {
        SplashScreen()
    } else {
        MainStudio()
    }
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // The Logo Image
        Image(
            painter = painterResource(id = R.drawable.dmms_logo),
            contentDescription = "DMMS Logo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop 
        )

        // The Tribute Text at the bottom
        Text(
            text = "Inspired by Linux MultiMedia Studio (LMMS)",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun MainStudio() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        TopBar()
        
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Sidebar()
            
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
        Row(verticalAlignment = Alignment.CenterVertically) {
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
            
            IconButton(onClick = { }) {
                Text("⏸", color = Color.White, fontSize = 24.sp)
            }
            
            IconButton(onClick = { }) {
                Text("⏹", color = Color.White, fontSize = 24.sp)
            }
        }

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