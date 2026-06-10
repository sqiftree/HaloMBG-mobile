package com.halombg.mobile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.halombg.mobile.fragment.ValidasiAiScreen
import com.halombg.mobile.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    role: String,
    initialGoToAi: Boolean = false,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(if (initialGoToAi) 1 else 0) }
    
    val tabs = when (role) {
        "Siswa" -> listOf(
            TabItem("Ulasan", Icons.Default.Edit),
            TabItem("Validasi AI", Icons.Default.Search)
        )
        "SPPG (Dapur)" -> listOf(
            TabItem("Menu", Icons.Default.List),
            TabItem("Distribusi", Icons.Default.Send),
            TabItem("Validasi AI", Icons.Default.Search)
        )
        "Guru" -> listOf(
            TabItem("Moderasi", Icons.Default.CheckCircle),
            TabItem("Validasi AI", Icons.Default.Search)
        )
        "Admin" -> listOf(
            TabItem("Ringkasan", Icons.Default.Info),
            TabItem("Validasi AI", Icons.Default.Search)
        )
        else -> listOf(TabItem("Beranda", Icons.Default.Home))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Dashboard $role",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Surface1
                        )
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = SecondaryGreen
                    ) {
                        Text(
                            text = role.uppercase(),
                            color = PrimaryNavy,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Keluar",
                            tint = Surface1
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryNavy)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Surface1,
                contentColor = PrimaryNavy
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        label = { Text(tab.title) },
                        icon = { Icon(tab.icon, contentDescription = null) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryNavy,
                            selectedTextColor = PrimaryNavy,
                            unselectedIconColor = TextTertiary,
                            unselectedTextColor = TextTertiary,
                            indicatorColor = AccentPastelBlue
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            val currentTabTitle = tabs.getOrNull(selectedTab)?.title ?: ""
            
            when (currentTabTitle) {
                "Validasi AI" -> ValidasiAiScreen()
                else -> {
                    // Placeholder for other screens until they are migrated
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Layar $currentTabTitle", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text("Sedang dalam tahap migrasi ke Compose", color = TextSecondary, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardSiswaPreview() {
    HaloMBGTheme {
        DashboardScreen(role = "Siswa", onLogout = {})
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardAdminPreview() {
    HaloMBGTheme {
        DashboardScreen(role = "Admin", onLogout = {})
    }
}

data class TabItem(val title: String, val icon: ImageVector)

