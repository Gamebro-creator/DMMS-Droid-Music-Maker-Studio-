package com.dmms.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. FULL SCREEN MODE: Hides the status bar and navigation bar
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        setContent {
            DmmsApp()
        }
    }
}

val DarkBg = Color(0xFF1E1E1E)
val PanelBg = Color(0xFF2A2A2A)
val AccentGreen = Color(0xFF00E676)
val TextGray = Color(0xFFB0B0B0)
val SelectedGreen = Color(0xFF004D40) // A dark green for the selected menu

@Composable
fun DmmsApp() {
    var showSplash by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(2500) 
        showSplash = false
    }

    // 2. FADE-IN TRANSITION: Crossfade smoothly swaps the screens
    Crossfade(
        targetState = showSplash,
        animationSpec = tween(durationMillis = 1000), // 1 second fade
        label = "SplashToStudio"
    ) { isSplash ->
        if (isSplash) {
            SplashScreen()
        } else {
            MainStudio()
        }
    }
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            painter = painterResource(id = R.drawable.dmms_logo),
            contentDescription = "DMMS Logo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop 
        )

        // 4. LOADING SPINNER
        CircularProgressIndicator(
            color = AccentGreen,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 250.dp) // Adjust this to move it below your logo
        )

        // The Tribute Text
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
    // 3. CLICKABLE MENUS: State to track which sidebar item is selected
    var selectedTab by remember { mutableStateOf("Project") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        TopBar()
        
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Sidebar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
            
            // The workspace changes based on what you click!
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF151515)),
                contentAlignment = Alignment.Center
            ) {
                if (selectedTab == "Project") {
                    Text("Piano Roll Coming Soon", color = TextGray)
                } else {
                    Text("$selectedTab Screen (Coming Soon)", color = TextGray)
                }
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
fun Sidebar(selectedTab: String, onTabSelected: (String) -> Unit) {
    Column(
        modifier = Modifier
            .width(90.dp)
            .fillMaxHeight()
            .background(PanelBg)
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SidebarItem(Icons.Filled.Home, "Project", selectedTab, onTabSelected)
        SidebarItem(Icons.Filled.List, "Samples", selectedTab, onTabSelected)
        SidebarItem(Icons.Filled.PlayArrow, "Instr.", selectedTab, onTabSelected)
        SidebarItem(Icons.Filled.Build, "Effects", selectedTab, onTabSelected)
        SidebarItem(Icons.Filled.Settings, "Settings", selectedTab, onTabSelected)
    }
}

@Composable
fun SidebarItem(
    icon: ImageVector, 
    label: String, 
    selectedTab: String, 
    onTabSelected: (String) -> Unit
) {
    val isSelected = selectedTab == label
    val bgColor = if (isSelected) SelectedGreen else Color.Transparent
    val iconColor = if (isSelected) AccentGreen else TextGray

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTabSelected(label) } // Makes it clickable!
            .background(bgColor)
            .padding(vertical = 16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = iconColor, fontSize = 11.sp)
    }
}