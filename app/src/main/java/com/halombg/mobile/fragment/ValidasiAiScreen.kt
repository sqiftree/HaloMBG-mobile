package com.halombg.mobile.fragment

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.halombg.mobile.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class PresetMenu(
    val name: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val tags: String,
    val summary: String
)

data class HistoryRecord(
    val time: String,
    val menu: PresetMenu
)

val presets = listOf(
    PresetMenu(
        name = "Nasi Putih, Fillet Ayam Panggang, Cah Wortel & Buncis",
        calories = 640,
        protein = 25,
        carbs = 82,
        fat = 14,
        tags = "Nasi Putih · Fillet Ayam Panggang · Cah Wortel & Buncis · Susu Kotak UHT · Melon",
        summary = "Menu memenuhi standar kecukupan nutrisi program MBG dengan gizi makro lengkap dan serat seimbang."
    ),
    PresetMenu(
        name = "Nasi Merah, Ikan Kembung Goreng, Sayur Sop Bening",
        calories = 590,
        protein = 22,
        carbs = 75,
        fat = 12,
        tags = "Nasi Merah · Ikan Kembung Goreng · Sayur Sop Bening · Susu Kotak UHT · Buah Pisang",
        summary = "Menu makanan kaya akan protein hewani, omega-3, dan karbohidrat kompleks dari nasi merah."
    ),
    PresetMenu(
        name = "Nasi Putih, Tumis Daging Sapi Lada Hitam, Sayur Bayam",
        calories = 670,
        protein = 28,
        carbs = 85,
        fat = 15,
        tags = "Nasi Putih · Tumis Daging Sapi Lada Hitam · Sayur Bayam · Susu Kotak UHT · Irisan Pepaya",
        summary = "Menu gizi lengkap dengan kandungan zat besi tinggi dari daging sapi segar serta zat antioksidan dari bayam."
    )
)

@Composable
fun ValidasiAiScreen() {
    var isCameraActive by remember { mutableStateOf(false) }
    var isScanning by remember { mutableStateOf(false) }
    var activePreset by remember { mutableStateOf<PresetMenu?>(null) }
    val historyRecords = remember { mutableStateListOf<HistoryRecord>(
        HistoryRecord("Hari ini, 12:15", presets[0]),
        HistoryRecord("Kemarin, 11:58", presets[1])
    ) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface2)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = "VALIDASI AI KELAYAKAN GIZI",
            color = PrimaryNavy,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Pindai porsi makanan dengan kamera untuk memvalidasi standar gizi makro harian program MBG.",
            color = TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        // Camera / Workspace Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            shape = RoundedCornerShape(6.dp),
            colors = CardDefaults.cardColors(containerColor = Surface1),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Viewport Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isCameraActive && !isScanning) PrimaryNavy else Surface2),
                    contentAlignment = Alignment.Center
                ) {
                    if (!isCameraActive && !isScanning) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Add, // Using Add as placeholder for camera
                                contentDescription = null,
                                tint = TextTertiary,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                "Kamera belum aktif",
                                color = TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                            Text(
                                "Gunakan kamera simulasi di bawah untuk menguji.",
                                color = TextTertiary,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    } else if (isCameraActive && !isScanning) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = SecondaryGreen,
                                modifier = Modifier.size(80.dp)
                            )
                            Text(
                                "SIMULASI KAMERA AKTIF \u2726",
                                color = SecondaryGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Text(
                                "Mendeteksi porsi: Nasi, Ayam Panggang, Sayur Buncis",
                                color = Surface3,
                                fontSize = 10.sp
                            )
                        }
                        
                        // Scan line animation
                        val infiniteTransition = rememberInfiniteTransition(label = "scan")
                        val scanOffsetY by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 200f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(2000, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "scanLine"
                        )
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .offset(y = (scanOffsetY - 100).dp)
                                .background(SecondaryGreen)
                        )
                    }

                    if (isScanning) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(PrimaryNavy.copy(alpha = 0.85f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = SecondaryGreen)
                                Text(
                                    "Menganalisis Makanan Menggunakan AI...",
                                    color = Surface1,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(top = 12.dp)
                                )
                            }
                        }
                    }
                }

                // Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!isCameraActive && !isScanning) {
                        Button(
                            onClick = { isCameraActive = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
                        ) {
                            Text("Kamera Simulasi", color = Surface1)
                        }
                    } else if (isCameraActive && !isScanning) {
                        Button(
                            onClick = {
                                isScanning = true
                                scope.launch {
                                    delay(2500)
                                    isScanning = false
                                    isCameraActive = false
                                    activePreset = presets.random()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryGreen)
                        ) {
                            Text("Ambil & Validasi", color = PrimaryNavy)
                        }
                    }
                }
            }
        }

        // Result Card
        if (activePreset != null) {
            val preset = activePreset!!
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(6.dp),
                colors = CardDefaults.cardColors(containerColor = Surface1),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFE8F5E9)
                        ) {
                            Text(
                                "TERVALIDASI",
                                color = StatusSuccess,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Akurasi AI: 96.4%",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Text(
                        text = preset.summary,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 12.dp)
                    )

                    // Macros Breakdown
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        MacroItem(preset.calories.toString(), "Kalori (kkal)")
                        MacroItem("${preset.protein}g", "Protein")
                        MacroItem("${preset.carbs}g", "Karbo")
                        MacroItem("${preset.fat}g", "Lemak")
                    }

                    Text(
                        "Komposisi Bahan Terdeteksi:",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Text(
                        preset.tags,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                historyRecords.add(0, HistoryRecord("Hari ini, " + SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()), preset))
                                activePreset = null
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
                        ) {
                            Text("Simpan Log Validasi", color = Surface1)
                        }
                        TextButton(
                            onClick = { activePreset = null }
                        ) {
                            Text("Hapus Hasil", color = StatusError)
                        }
                    }
                }
            }
        }

        // History Section
        Text(
            text = "RIWAYAT VALIDASI TERBARU",
            color = PrimaryNavy,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 24.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            shape = RoundedCornerShape(6.dp),
            colors = CardDefaults.cardColors(containerColor = Surface1),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDefault)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                historyRecords.forEach { record ->
                    HistoryItem(record)
                    if (record != historyRecords.last()) {
                        HorizontalDivider(color = BorderDefault, modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ValidasiAiScreenPreview() {
    HaloMBGTheme {
        ValidasiAiScreen()
    }
}


@Composable
fun MacroItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryNavy)
        Text(label, fontSize = 9.sp, color = TextTertiary)
    }
}

@Composable
fun HistoryItem(record: HistoryRecord) {
    Column(modifier = Modifier.padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color(0xFFE8F5E9)
            ) {
                Text(
                    "✓ Tervalidasi",
                    color = StatusSuccess,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(record.time + " · Akurasi 96%", fontSize = 11.sp, color = TextSecondary)
        }
        
        Text(record.menu.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.padding(top = 4.dp))
        Text(
            "Gizi: Kalori ${record.menu.calories} kkal, Protein ${record.menu.protein}g, Karbo ${record.menu.carbs}g, Lemak ${record.menu.fat}g.\n\nBahan: ${record.menu.tags}",
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
