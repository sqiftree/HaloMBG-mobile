package com.halombg.mobile.fragment

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Email
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.halombg.mobile.ui.theme.*

@Composable
fun SppgProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: SppgProfileViewModel = viewModel()
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Surface2) // Flat Surface2 background (#F8F7F5)
    ) {
        if (viewModel.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryNavy)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                // Section Title (snapped to multiples of 8)
                Text(
                    text = "PROFIL DAPUR SPPG",
                    color = PrimaryNavy,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Informasi detail operasional dapur Satuan Pelayanan Pemenuhan Gizi (SPPG).",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Error Message Box
                AnimatedVisibility(visible = viewModel.errorMessage != null) {
                    viewModel.errorMessage?.let { error ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(4.dp), // 4dp status badge corner
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)) // Light Red
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = null,
                                    tint = StatusError,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = error,
                                    color = StatusError,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Success Message Box
                AnimatedVisibility(visible = viewModel.successMessage != null) {
                    viewModel.successMessage?.let { success ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(4.dp), // 4dp status badge corner
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)) // Light Green
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    tint = StatusSuccess,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = success,
                                    color = StatusSuccess,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Profile Header Card (Read-only Overview)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp), // 8dp card corners
                    colors = CardDefaults.cardColors(containerColor = Surface1),
                    border = BorderStroke(1.dp, BorderDefault)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = viewModel.kitchenName.ifBlank { "Dapur SPPG" },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryNavy
                            )

                            // Status badge
                            Surface(
                                shape = RoundedCornerShape(4.dp), // 4dp corner
                                color = if (viewModel.isActive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                            ) {
                                Text(
                                    text = if (viewModel.isActive) "AKTIF" else "NONAKTIF",
                                    color = if (viewModel.isActive) StatusSuccess else StatusError,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${viewModel.district}, ${viewModel.province}",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Capacity info
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(24.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Column {
                                Text("Kapasitas Produksi", fontSize = 10.sp, color = TextTertiary, fontWeight = FontWeight.Medium)
                                Text(
                                    text = viewModel.productionCapacity?.let { "$it porsi / hari" } ?: "Belum diatur",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Column {
                                Text("Mode Operasional", fontSize = 10.sp, color = TextTertiary, fontWeight = FontWeight.Medium)
                                Text(
                                    text = if (viewModel.isSimulationMode) "Offline (Simulasi)" else "Online (API Connected)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (viewModel.isSimulationMode) HighlightGold else StatusInfo
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Detail Information Card (Read-only)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface1),
                    border = BorderStroke(1.dp, BorderDefault)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Detail Informasi Dapur",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        InfoRow(label = "Alamat Dapur", value = viewModel.address, icon = Icons.Outlined.LocationOn)
                        HorizontalDivider(color = BorderDefault, modifier = Modifier.padding(vertical = 4.dp))
                        InfoRow(label = "Nama Penanggung Jawab (Contact Person)", value = viewModel.contactPersonName, icon = Icons.Outlined.Person)
                        HorizontalDivider(color = BorderDefault, modifier = Modifier.padding(vertical = 4.dp))
                        InfoRow(label = "No. WhatsApp / Telepon", value = viewModel.contactPhone, icon = Icons.Outlined.Phone)
                        HorizontalDivider(color = BorderDefault, modifier = Modifier.padding(vertical = 4.dp))
                        InfoRow(label = "Email Kontak", value = viewModel.contactEmail, icon = Icons.Outlined.Email)
                        HorizontalDivider(color = BorderDefault, modifier = Modifier.padding(vertical = 4.dp))
                        InfoRow(label = "Deskripsi Operasional Dapur", value = viewModel.description, icon = Icons.Outlined.Info)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryNavy,
            modifier = Modifier
                .size(20.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextTertiary,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value.ifBlank { "-" },
                fontSize = 14.sp,
                color = TextPrimary,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
