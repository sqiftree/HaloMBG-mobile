package com.halombg.mobile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.halombg.mobile.ui.theme.HaloMBGTheme
import com.halombg.mobile.ui.theme.*
import com.halombg.mobile.data.AuthRepository
import com.halombg.mobile.data.MockData
import com.halombg.mobile.model.School

// Main entry point for the application
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val authRepository = AuthRepository(this)
        if (authRepository.isLoggedIn()) {
            val role = authRepository.getUserRole() ?: "Siswa"
            val email = authRepository.getUserEmail() ?: ""
            val dashboardIntent = Intent(this, DashboardActivity::class.java).apply {
                putExtra("ROLE", role)
                putExtra("USER_EMAIL", email)
            }
            startActivity(dashboardIntent)
            finish()
            return
        }

        setContent {
            HaloMBGTheme {
                MainScreen(
                    onLoginClick = {
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    onLoginClick: () -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomActionBar(onLoginClick = onLoginClick)
        }
    ) { padding ->
        BerandaScreen(modifier = Modifier.padding(padding))
    }
}

@Composable
fun BerandaScreen(
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedSchool by remember { mutableStateOf<School?>(null) }
    var showKitchenProfileId by remember { mutableStateOf<Long?>(null) }
    var showKitchenSelectorDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val searchResults = remember(searchQuery) {
        if (searchQuery.length >= 2) MockData.searchSchools(searchQuery) else emptyList()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Hero Section
        HeroSection(
            searchQuery = searchQuery,
            onSearchChange = { searchQuery = it },
            searchResults = searchResults,
            selectedSchool = selectedSchool,
            onSchoolSelect = {
                selectedSchool = it
                searchQuery = it.name
            },
            onSppgClick = { showKitchenProfileId = it }
        )

        // Features Section
        FeaturesSection(
            onSppgSearchClick = {
                Toast.makeText(context, "Silakan gunakan kolom pencarian di atas untuk mencari sekolah.", Toast.LENGTH_SHORT).show()
            },
            onMenuClick = {
                Toast.makeText(context, "Fitur Menu Harian & Gizi dalam tahap pengembangan", Toast.LENGTH_SHORT).show()
            },
            onDistribusiClick = {
                Toast.makeText(context, "Fitur Status Distribusi dalam tahap pengembangan", Toast.LENGTH_SHORT).show()
            },
            onProfilDapurClick = {
                showKitchenSelectorDialog = true
            }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showKitchenSelectorDialog) {
        val kitchens = MockData.sppgProfiles
        AlertDialog(
            onDismissRequest = { showKitchenSelectorDialog = false },
            confirmButton = {
                TextButton(onClick = { showKitchenSelectorDialog = false }) {
                    Text("Batal", color = PrimaryNavy, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text(
                    text = "Pilih Dapur SPPG",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Silakan pilih salah satu dapur SPPG di bawah ini untuk melihat detail profil:",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    kitchens.forEach { kitchen ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showKitchenSelectorDialog = false
                                    showKitchenProfileId = kitchen.id
                                },
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault),
                            colors = CardDefaults.cardColors(containerColor = Surface1)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(kitchen.kitchenName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                                Text("${kitchen.district}, ${kitchen.province}", fontSize = 12.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            },
            containerColor = Surface1,
            shape = RoundedCornerShape(8.dp)
        )
    }

    if (showKitchenProfileId != null) {
        val kitchenId = showKitchenProfileId!!
        val profile = MockData.getSppgProfile(kitchenId)
        if (profile != null) {
            AlertDialog(
                onDismissRequest = { showKitchenProfileId = null },
                confirmButton = {
                    TextButton(onClick = { showKitchenProfileId = null }) {
                        Text("Tutup", color = PrimaryNavy, fontWeight = FontWeight.Bold)
                    }
                },
                title = {
                    Text(
                        text = "Profil Dapur SPPG",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNavy
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = profile.kitchenName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryNavy
                        )
                        Text(
                            text = "${profile.district}, ${profile.province}",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        HorizontalDivider(color = BorderDefault, modifier = Modifier.padding(bottom = 12.dp))
                        
                        DetailInfoRow(label = "Alamat Dapur", value = profile.address, icon = Icons.Outlined.LocationOn)
                        DetailInfoRow(label = "Penanggung Jawab", value = profile.contactPersonName, icon = Icons.Outlined.Person)
                        DetailInfoRow(label = "No. WhatsApp / Telepon", value = profile.contactPhone, icon = Icons.Outlined.Phone)
                        DetailInfoRow(label = "Email Kontak", value = profile.contactEmail ?: "-", icon = Icons.Outlined.Email)
                        DetailInfoRow(label = "Deskripsi", value = profile.description ?: "-", icon = Icons.Outlined.Info)
                        DetailInfoRow(label = "Kapasitas Produksi", value = "${profile.productionCapacity} porsi / hari", icon = Icons.Outlined.Star)
                    }
                },
                containerColor = Surface1,
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}

@Composable
fun DetailInfoRow(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryNavy,
            modifier = Modifier
                .size(18.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = label,
                fontSize = 10.sp,
                color = TextTertiary,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value.ifBlank { "-" },
                fontSize = 13.sp,
                color = TextPrimary,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(top = 1.dp)
            )
        }
    }
}

@Composable
fun HeroSection(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    searchResults: List<School>,
    selectedSchool: School?,
    onSchoolSelect: (School) -> Unit,
    onSppgClick: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryNavy)
            .padding(horizontal = 24.dp)
            .padding(top = 48.dp, bottom = 32.dp)
    ) {
        Text(
            text = "PLATFORM MONITORING MBG",
            color = AccentPastelBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(R.string.landing_title),
            color = Surface1,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 34.sp,
            modifier = Modifier.padding(top = 8.dp)
        )

        Text(
            text = stringResource(R.string.landing_subtitle),
            color = Surface3,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            modifier = Modifier.padding(top = 12.dp)
        )

        // Search Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            shape = RoundedCornerShape(6.dp),
            colors = CardDefaults.cardColors(containerColor = Surface1)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text(stringResource(R.string.search_placeholder), fontSize = 14.sp) },
                    modifier = Modifier.fillMaxSize(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )
            }
        }

        // Search Results
        if (searchResults.isNotEmpty() && searchQuery.length >= 2 && selectedSchool?.name != searchQuery) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Surface1)
            ) {
                Column {
                    searchResults.take(5).forEach { school ->
                        SchoolResultItem(school, onClick = { onSchoolSelect(school) })
                    }
                }
            }
        }

        // Selected School Card
        if (selectedSchool != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(6.dp),
                colors = CardDefaults.cardColors(containerColor = AccentPastelBlue)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("SEKOLAH DIPILIH", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                    Text(selectedSchool.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                    Text("${selectedSchool.district}, ${selectedSchool.province}", fontSize = 12.sp, color = TextSecondary)
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BorderDefault)
                    
                    Text("Dapur SPPG Penyedia:", fontSize = 11.sp, color = TextSecondary)
                    val sppg = selectedSchool.sppgId?.let { MockData.getSppgProfile(it) }
                    if (sppg != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(sppg.kitchenName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                            TextButton(
                                onClick = { onSppgClick(sppg.id) },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                colors = ButtonDefaults.textButtonColors(contentColor = PrimaryNavy)
                            ) {
                                Text("Lihat Profil ➜", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Text("Belum terhubung ke dapur SPPG", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }

        // Stats
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatItem(stringResource(R.string.stat_schools_num), stringResource(R.string.stat_schools_label))
            StatItem(stringResource(R.string.stat_provinces_num), stringResource(R.string.stat_provinces_label))
            StatItem(stringResource(R.string.stat_kitchens_num), stringResource(R.string.stat_kitchens_label))
        }
    }
}

@Composable
fun SchoolResultItem(school: School, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Text(school.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text("${school.district} · ${school.province}", fontSize = 12.sp, color = TextSecondary)
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = SecondaryGreen)
        Text(label, fontSize = 11.sp, color = Surface3)
    }
}

@Composable
fun FeaturesSection(
    onSppgSearchClick: () -> Unit,
    onMenuClick: () -> Unit,
    onDistribusiClick: () -> Unit,
    onProfilDapurClick: () -> Unit
) {
    Column(modifier = Modifier.padding(24.dp)) {
        Text("FITUR PLATFORM", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
        Text(stringResource(R.string.features_title), fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))

        FeatureCard(
            title = "Cari SPPG & Sekolah",
            description = "Temukan dapur MBG yang melayani sekolah tertentu. Ketik nama sekolah di atas.",
            onClick = onSppgSearchClick,
            modifier = Modifier.padding(top = 16.dp)
        )
        FeatureCard(
            title = "Menu Harian & Gizi",
            description = "Pantau menu makanan yang disajikan setiap hari beserta kandungan nutrisinya.",
            onClick = onMenuClick,
            modifier = Modifier.padding(top = 12.dp)
        )
        FeatureCard(
            title = "Status Distribusi",
            description = "Cek secara real-time apakah makanan sudah dikirim dan tiba di sekolah hari ini.",
            onClick = onDistribusiClick,
            modifier = Modifier.padding(top = 12.dp)
        )
        FeatureCard(
            title = "Profil Dapur",
            description = "Lihat informasi lengkap setiap dapur MBG — alamat, kapasitas, dan sekolah yang dilayani.",
            onClick = onProfilDapurClick,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@Composable
fun FeatureCard(title: String, description: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault),
        colors = CardDefaults.cardColors(containerColor = Surface1)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
            Text(description, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
fun BottomActionBar(onLoginClick: () -> Unit) {
    Surface(
        color = Surface1,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("HaloMBG", modifier = Modifier.weight(1f), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
            Button(
                onClick = onLoginClick,
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
            ) {
                Text(stringResource(R.string.login), color = Surface1)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    HaloMBGTheme {
        MainScreen(
            onLoginClick = {}
        )
    }
}
