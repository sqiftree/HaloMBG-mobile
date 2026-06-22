package com.halombg.mobile.fragment

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun SppgFollowUpScreen(
    modifier: Modifier = Modifier,
    viewModel: SppgFollowUpViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadCriticalReviews()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Surface2)
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
                    .padding(16.dp)
            ) {
                Text(
                    text = "PENANGANAN ULASAN KRITIS",
                    color = PrimaryNavy,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Pantau dan tindak lanjuti ulasan kritis siswa atau ulasan yang telah dilaporkan oleh guru.",
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
                            shape = RoundedCornerShape(4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
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
                            shape = RoundedCornerShape(4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
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

                if (viewModel.criticalReviewsList.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Surface1),
                        border = BorderStroke(1.dp, BorderDefault)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Tidak ada laporan ulasan kritis saat ini. Kerja bagus!",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(viewModel.criticalReviewsList) { review ->
                            SppgCriticalReviewRowItem(
                                review = review,
                                onStatusChange = { newStatus ->
                                    viewModel.updateStatus(review.id, newStatus)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SppgCriticalReviewRowItem(
    review: Review,
    onStatusChange: (String) -> Unit
) {
    val statusLabel = when (review.followUpStatus) {
        "belum_diproses" -> "Belum Diproses"
        "dalam_proses" -> "Dalam Proses"
        "selesai" -> "Selesai"
        else -> "Belum Diproses"
    }

    val (statusBg, statusText) = when (review.followUpStatus) {
        "belum_diproses" -> Color(0xFFFFEBEE) to Color(0xFFC62828) // Red
        "dalam_proses" -> Color(0xFFFFF9C4) to Color(0xFFF57F17)  // Yellow
        "selesai" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)      // Green
        else -> Color(0xFFFFEBEE) to Color(0xFFC62828)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, BorderDefault),
        colors = CardDefaults.cardColors(containerColor = Surface1)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${review.userName} · ${review.schoolName}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNavy
                    )
                    Text(
                        text = review.reviewDate,
                        fontSize = 11.sp,
                        color = TextTertiary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = statusBg
                ) {
                    Text(
                        text = statusLabel.uppercase(),
                        color = statusText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = review.content,
                fontSize = 13.sp,
                color = TextPrimary,
                lineHeight = 18.sp
            )

            if (review.photo != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "✓ Lampiran foto: ${review.photo}",
                    fontSize = 11.sp,
                    color = StatusSuccess,
                    fontWeight = FontWeight.Medium
                )
            }

            // Flagged by Teacher Indicator
            if (review.flagStatus == "flagged") {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFFFEBEE)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Warning,
                            contentDescription = null,
                            tint = StatusError,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Dilaporkan oleh Guru (FLAGGED)",
                            color = StatusError,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action: Change status row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ubah Status Tindak Lanjut:",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val statusOptions = listOf(
                        "belum_diproses" to "Belum",
                        "dalam_proses" to "Proses",
                        "selesai" to "Selesai"
                    )

                    statusOptions.forEach { (optionKey, label) ->
                        val isSelected = review.followUpStatus == optionKey
                        OutlinedButton(
                            onClick = { onStatusChange(optionKey) },
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) PrimaryNavy else Color.Transparent,
                                contentColor = if (isSelected) Color.White else TextSecondary
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (isSelected) PrimaryNavy else BorderDefault
                            )
                        ) {
                            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
