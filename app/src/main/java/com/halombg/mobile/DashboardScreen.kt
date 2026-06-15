package com.halombg.mobile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.halombg.mobile.data.AuthRepository
import com.halombg.mobile.data.MockData
import com.halombg.mobile.fragment.GuruModerationScreen
import com.halombg.mobile.fragment.SiswaReviewScreen
import com.halombg.mobile.fragment.SppgDistributionScreen
import com.halombg.mobile.fragment.SppgProfileScreen
import com.halombg.mobile.ui.StatusBadge
import com.halombg.mobile.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    role: String,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    val tabs = when (role) {
        "Siswa" -> listOf(
            TabItem("Beranda", Icons.Outlined.Home),
            TabItem("Ulasan", Icons.Outlined.Edit)
        )
        "SPPG (Dapur)" -> listOf(
            TabItem("Beranda", Icons.Outlined.Home),
            TabItem("Distribusi", Icons.AutoMirrored.Outlined.Send),
            TabItem("Profil Dapur", Icons.Outlined.Person)
        )
        "Guru" -> listOf(
            TabItem("Beranda", Icons.Outlined.Home),
            TabItem("Moderasi", Icons.Outlined.CheckCircle)
        )
        else -> listOf(TabItem("Beranda", Icons.Outlined.Home))
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
                            imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
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
                "Beranda" -> BerandaScreen(role)
                "Profil Dapur" -> SppgProfileScreen()
                "Ulasan" -> SiswaReviewScreen()
                "Moderasi" -> GuruModerationScreen()
                "Distribusi" -> SppgDistributionScreen()
                else -> {
                    // Placeholder for other screens until they are migrated
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Layar $currentTabTitle", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                            Text("Sedang dalam tahap pengembangan", color = TextSecondary, fontSize = 14.sp)
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

data class TabItem(val title: String, val icon: ImageVector)

@Composable
fun BerandaScreen(role: String) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository(context) }
    val userName = remember { authRepository.getUserName() ?: "Pengguna" }
    val schoolId = remember { authRepository.getSchoolId() }
    
    val school = remember(schoolId) { 
        if (schoolId != -1L) MockData.schools.find { it.id == schoolId } else null 
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface2)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "Selamat Datang,",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                Text(
                    text = userName,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy
                )
                
                if (role == "SPPG (Dapur)") {
                    Text(
                        text = "Kelola dapur dan pantau pengiriman hari ini.",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        if (role == "SPPG (Dapur)") {
            item {
                val sppgId = 1L // Mock SPPG ID for the user
                val sppg = MockData.getSppgProfile(sppgId)
                if (sppg != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = PrimaryNavy)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("DAPUR ANDA", color = AccentPastelBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(sppg.kitchenName, color = Surface1, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text("${sppg.district}, ${sppg.province}", color = Surface3, fontSize = 13.sp)
                            
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp), 
                                color = Surface1.copy(alpha = 0.2f)
                            )
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("KAPASITAS", color = AccentPastelBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text("${sppg.productionCapacity} Porsi", color = Surface1, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("STATUS", color = AccentPastelBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text(if (sppg.isActive) "AKTIF" else "NON-AKTIF", color = SecondaryGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
            
            item {
                val sppgId = 1L
                val statuses = MockData.getDistributionStatusesForSppg(sppgId)
                val deliveredCount = statuses.count { it.status == "sudah_diantar" }
                val totalCount = statuses.size
                
                Text(
                    text = "RINGKASAN PENGIRIMAN", 
                    color = PrimaryNavy, 
                    fontSize = 12.sp, 
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface1),
                    border = BorderStroke(1.dp, BorderDefault)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "$deliveredCount dari $totalCount sekolah",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryNavy
                            )
                            Text(
                                text = "Paket makanan telah tiba di tujuan.",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                        CircularProgressIndicator(
                            progress = { if (totalCount > 0) deliveredCount.toFloat() / totalCount else 0f },
                            modifier = Modifier.size(48.dp),
                            color = StatusSuccess,
                            trackColor = Surface2,
                            strokeWidth = 6.dp,
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
                        )
                    }
                }
            }
        }

        if (role == "Guru" && school != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryNavy)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("SEKOLAH ANDA", color = AccentPastelBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(school.name, color = Surface1, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("${school.district}, ${school.province}", color = Surface3, fontSize = 13.sp)
                    }
                }
            }
            
            item {
                val flaggedCount = MockData.getReviewsForSchool(schoolId).count { it.flagStatus == "flagged" }
                
                Text(
                    text = "RINGKASAN MODERASI", 
                    color = PrimaryNavy, 
                    fontSize = 12.sp, 
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface1),
                    border = BorderStroke(1.dp, BorderDefault)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "$flaggedCount Ulasan Ditandai",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (flaggedCount > 0) StatusError else PrimaryNavy
                            )
                            Text(
                                text = "Perlu tindakan moderasi segera.",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                        Icon(
                            imageVector = Icons.Outlined.Warning,
                            contentDescription = null,
                            tint = if (flaggedCount > 0) StatusError else TextTertiary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }

        if (role == "Siswa" && school != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryNavy)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("SEKOLAH ANDA", color = AccentPastelBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(school.name, color = Surface1, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("${school.district}, ${school.province}", color = Surface3, fontSize = 13.sp)
                        
                        val sppg = school.sppgId?.let { MockData.getSppgProfile(it) }
                        if (sppg != null) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp), 
                                color = Surface1.copy(alpha = 0.2f)
                            )
                            Text("DAPUR PENYEDIA", color = AccentPastelBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(sppg.kitchenName, color = Surface1, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            
            item {
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val menu = school.sppgId?.let { MockData.getDailyMenuForSppg(it, today) }
                
                Text(
                    text = "MENU MAKANAN HARI INI", 
                    color = PrimaryNavy, 
                    fontSize = 12.sp, 
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                if (menu != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Surface1),
                        border = BorderStroke(1.dp, BorderDefault)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(menu.menuName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(
                                text = menu.components ?: "", 
                                fontSize = 13.sp, 
                                color = TextSecondary, 
                                maxLines = 2,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(), 
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                MacroItem(value = "${menu.calories}", label = "kkal")
                                MacroItem(value = "${menu.protein}g", label = "Protein")
                                MacroItem(value = "${menu.carbs}g", label = "Karbo")
                                MacroItem(value = "${menu.fat}g", label = "Lemak")
                            }
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Surface1),
                        border = BorderStroke(1.dp, BorderDefault)
                    ) {
                        Text(
                            text = "Belum ada menu yang diumumkan untuk hari ini.",
                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                            fontSize = 14.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            item {
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val distStatus = MockData.distributionStatuses.find { it.schoolId == schoolId && it.distributedAt == today }
                
                Text(
                    text = "STATUS PENGIRIMAN", 
                    color = PrimaryNavy, 
                    fontSize = 12.sp, 
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface1),
                    border = BorderStroke(1.dp, BorderDefault)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            if (distStatus != null) {
                                StatusBadge(distStatus.status)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = distStatus.statusUpdatedAt,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                            } else {
                                Text(
                                    text = "Belum ada informasi pengiriman.",
                                    fontSize = 14.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Send,
                            contentDescription = null,
                            tint = if (distStatus?.status == "sudah_diantar") StatusSuccess else TextTertiary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            item {
                val recentReviews = MockData.getReviewsForSchool(schoolId).take(2)
                if (recentReviews.isNotEmpty()) {
                    Text(
                        text = "ULASAN TERBARU SEKOLAH",
                        color = PrimaryNavy,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                        recentReviews.forEach { review ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Surface1),
                                border = BorderStroke(1.dp, BorderDefault)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = review.userName,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryNavy
                                        )
                                        Text(
                                            text = review.reviewDate,
                                            fontSize = 10.sp,
                                            color = TextSecondary
                                        )
                                    }
                                    Text(
                                        text = review.content,
                                        fontSize = 12.sp,
                                        color = TextPrimary,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface1),
                    border = BorderStroke(1.dp, BorderDefault)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info, 
                            contentDescription = null, 
                            tint = PrimaryNavy, 
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Modul Beranda untuk $role sedang disiapkan.", 
                            textAlign = TextAlign.Center, 
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MacroItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
        Text(label, fontSize = 10.sp, color = TextTertiary)
    }
}
