package com.halombg.mobile.fragment

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Phone
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
import com.halombg.mobile.model.Review
import com.halombg.mobile.ui.theme.*

@Composable
fun SiswaReviewScreen(
    modifier: Modifier = Modifier,
    viewModel: SiswaReviewViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Surface2) // Flat Surface2 background
    ) {
        if (viewModel.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryNavy)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // SPPG Kitchen Profile Section
                item {
                    Text(
                        text = "MITRA DAPUR PENYEDIA SPPG",
                        color = PrimaryNavy,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    val sppg = viewModel.sppgProfile
                    if (sppg != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp), // 8dp card corners
                            colors = CardDefaults.cardColors(containerColor = Surface1),
                            border = BorderStroke(1.dp, BorderDefault)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = sppg.kitchenName ?: "Dapur SPPG",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryNavy
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                
                                sppg.description?.let { desc ->
                                    Text(
                                        text = desc,
                                        fontSize = 12.sp,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.LocationOn,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${sppg.address ?: ""}, ${sppg.district ?: ""}",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.Phone,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Hubungi: ${sppg.contactPersonName ?: ""} (${sppg.contactPhone ?: ""})",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Surface1),
                            border = BorderStroke(1.dp, BorderDefault)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = null,
                                    tint = TextTertiary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Sekolah Anda belum terhubung ke dapur SPPG manapun.",
                                    fontSize = 13.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Today's Menu Section
                item {
                    Text(
                        text = "MENU MAKANAN HARI INI",
                        color = PrimaryNavy,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    val menu = viewModel.todayMenu
                    if (menu != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
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
                                        text = menu.menuName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    if (menu.isAiValidated) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp), // 4dp status badge
                                            color = Color(0xFFE8F5E9)
                                        ) {
                                            Text(
                                                text = "✓ TERVALIDASI AI",
                                                color = StatusSuccess,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                menu.components?.let { components ->
                                    Text(
                                        text = "Komponen: $components",
                                        fontSize = 12.sp,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Macros breakdown
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    MacroMetricItem(value = menu.calories?.toString() ?: "—", label = "Kalori (kkal)")
                                    MacroMetricItem(value = "${menu.protein ?: "—"}g", label = "Protein")
                                    MacroMetricItem(value = "${menu.carbs ?: "—"}g", label = "Karbo")
                                    MacroMetricItem(value = "${menu.fat ?: "—"}g", label = "Lemak")
                                }
                            }
                        }
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Surface1),
                            border = BorderStroke(1.dp, BorderDefault)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = null,
                                    tint = TextTertiary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Belum ada menu yang diunggah hari ini.",
                                    fontSize = 13.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Input Section
                item {
                    Text(
                        text = "KIRIM ULASAN ANDA",
                        color = PrimaryNavy,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Surface1),
                        border = BorderStroke(1.dp, BorderDefault)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            OutlinedTextField(
                                value = viewModel.reviewContent,
                                onValueChange = { viewModel.reviewContent = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Ceritakan pengalaman makan siang Anda...", fontSize = 13.sp) },
                                minLines = 3,
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryNavy,
                                    unfocusedBorderColor = BorderDefault
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Simulation for photo upload
                                TextButton(onClick = { viewModel.simulatePhoto() }) {
                                    Icon(imageVector = Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (viewModel.simulatedPhotoName == null) "Lampirkan Foto" else "Foto Terlampir ✓",
                                        fontSize = 12.sp
                                    )
                                }

                                Button(
                                    onClick = { viewModel.submitReview() },
                                    enabled = !viewModel.isSubmitting,
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                                ) {
                                    if (viewModel.isSubmitting) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                    } else {
                                        Text("Kirim Ulasan", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Error/Success Messages
                            AnimatedVisibility (visible = viewModel.errorMessage != null) {
                                Text(
                                    text = viewModel.errorMessage ?: "",
                                    color = StatusError,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                            AnimatedVisibility (visible = viewModel.successMessage != null) {
                                Text(
                                    text = viewModel.successMessage ?: "",
                                    color = StatusSuccess,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }

                // Reviews List Section
                item {
                    Text(
                        text = "RIWAYAT ULASAN SEKOLAH",
                        color = PrimaryNavy,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                if (viewModel.reviewsList.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Surface1),
                            border = BorderStroke(1.dp, BorderDefault)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Belum ada ulasan dari sekolah Anda.",
                                    fontSize = 13.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                } else {
                    items(viewModel.reviewsList) { review ->
                        ReviewItemRow(
                            review = review,
                            onDelete = { viewModel.deleteReview(review) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun ReviewItemRow(review: Review, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp), // 8dp card corners
        colors = CardDefaults.cardColors(containerColor = Surface1),
        border = BorderStroke(1.dp, BorderDefault)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${review.userName} · ${review.reviewDate}",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                
                // Show flagged badge if review is flagged
                if (review.flagStatus == "flagged") {
                    Surface(
                        shape = RoundedCornerShape(4.dp), // 4dp corners
                        color = Color(0xFFFFEBEE)
                    ) {
                        Text(
                            text = "FLAGGED",
                            color = StatusError,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Hapus",
                        tint = StatusError,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = review.content,
                fontSize = 13.sp,
                color = TextPrimary
            )
            review.photo?.let { filename ->
                Text(
                    text = "✓ Lampiran foto: $filename",
                    fontSize = 11.sp,
                    color = StatusSuccess,
                    modifier = Modifier.padding(top = 4.dp),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun MacroMetricItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryNavy
        )
        Text(
            text = label,
            fontSize = 9.sp,
            color = TextTertiary
        )
    }
}
