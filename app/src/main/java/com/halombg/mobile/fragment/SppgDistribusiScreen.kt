package com.halombg.mobile.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.halombg.mobile.model.DistributionStatus
import com.halombg.mobile.ui.theme.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SppgDistribusiScreen(
    modifier: Modifier = Modifier,
    viewModel: SppgDistribusiViewModel = viewModel()
) {
    val context = LocalContext.current
    var selectedItem by remember { mutableStateOf<DistributionStatus?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadDistributions()
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
                    text = "JADWAL DISTRIBUSI MAKANAN",
                    color = PrimaryNavy,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Pilih sekolah di bawah untuk memperbarui status pengiriman makanan hari ini.",
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

                if (viewModel.distributionList.isEmpty()) {
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
                                text = "Tidak ada jadwal distribusi hari ini.",
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
                        items(viewModel.distributionList) { item ->
                            DistributionRowItem(
                                item = item,
                                onClick = {
                                    selectedItem = item
                                    viewModel.capturedImageUri = null // Reset
                                    showBottomSheet = true
                                }
                            )
                        }
                    }
                }
            }
        }

        // Bottom Sheet for status update
        if (showBottomSheet && selectedItem != null) {
            val item = selectedItem!!
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = Surface1
            ) {
                DistributionStatusUpdateBottomSheet(
                    item = item,
                    viewModel = viewModel,
                    onDismiss = { showBottomSheet = false }
                )
            }
        }
    }
}

@Composable
fun DistributionRowItem(item: DistributionStatus, onClick: () -> Unit) {
    val statusLabel = when (item.status) {
        "belum_diantar" -> "Belum Diantar"
        "siap_diantar" -> "Siap Diantar"
        "sudah_diantar" -> "Sudah Diantar"
        "batal" -> "Batal"
        else -> item.status.uppercase()
    }

    val (badgeBg, badgeText) = when (item.status) {
        "belum_diantar" -> Color(0xFFECEFF1) to Color(0xFF546E7A) // Gray
        "siap_diantar" -> Color(0xFFFFF9C4) to Color(0xFFF57F17)  // Yellow
        "sudah_diantar" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32) // Green
        "batal" -> Color(0xFFFFEBEE) to Color(0xFFC62828)        // Red
        else -> Color.LightGray to Color.DarkGray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                Text(
                    text = item.schoolName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNavy
                )
                
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = badgeBg
                ) {
                    Text(
                        text = statusLabel.uppercase(),
                        color = badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Pengiriman: ${item.distributedAt}",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Update: ${item.statusUpdatedAt}",
                    fontSize = 11.sp,
                    color = TextTertiary
                )
            }

            // Image indicator if photo exists
            if (item.photo != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "✓ Bukti foto terlampir",
                    fontSize = 11.sp,
                    color = StatusSuccess,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun DistributionStatusUpdateBottomSheet(
    item: DistributionStatus,
    viewModel: SppgDistribusiViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedStatus by remember { mutableStateOf(item.status) }
    var tempPhotoFile by remember { mutableStateOf<File?>(null) }
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // TakePicture launcher
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempPhotoUri?.let { uri ->
                viewModel.capturedImageUri = uri
            }
        }
    }

    // Permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                val file = createTempImageFile(context)
                tempPhotoFile = file
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                tempPhotoUri = uri
                takePictureLauncher.launch(uri)
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal membuka kamera: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Izin kamera ditolak.", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(bottom = 24.dp) // Avoid system navigation overlapping
    ) {
        Text(
            text = "PERBARUI STATUS DISTRIBUSI",
            color = PrimaryNavy,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = item.schoolName,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
        )

        Text(
            text = "Status Pengiriman:",
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Selectable status cards row
        val statuses = listOf(
            Triple("belum_diantar", "Belum", Color(0xFFECEFF1) to Color(0xFF546E7A)),
            Triple("siap_diantar", "Siap", Color(0xFFFFF9C4) to Color(0xFFF57F17)),
            Triple("sudah_diantar", "Sudah", Color(0xFFE8F5E9) to Color(0xFF2E7D32)),
            Triple("batal", "Batal", Color(0xFFFFEBEE) to Color(0xFFC62828))
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            statuses.forEach { (statusKey, label, colors) ->
                val isSelected = selectedStatus == statusKey
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedStatus = statusKey },
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) PrimaryNavy else BorderDefault
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) colors.first else Surface1
                    )
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) colors.second else TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Bukti Serah Terima Makanan (Kamera):",
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Photo thumbnail / selector
        if (viewModel.capturedImageUri != null) {
            val bitmap = rememberBitmapFromUri(viewModel.capturedImageUri!!)
            if (bitmap != null) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, BorderDefault, RoundedCornerShape(8.dp))
                ) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Foto Bukti Pengiriman",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { viewModel.clearPhoto() },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(28.dp)
                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(bottomStart = 8.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Hapus Foto",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        } else {
            Button(
                onClick = {
                    val permission = Manifest.permission.CAMERA
                    val hasPermission = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
                    if (hasPermission) {
                        try {
                            val file = createTempImageFile(context)
                            tempPhotoFile = file
                            val uri = FileProvider.getUriForFile(
                                context,
                                "${context.packageName}.fileprovider",
                                file
                            )
                            tempPhotoUri = uri
                            takePictureLauncher.launch(uri)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Gagal membuka kamera: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        cameraPermissionLauncher.launch(permission)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentPastelBlue, contentColor = PrimaryNavy),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Send,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ambil Bukti Foto", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                border = BorderStroke(1.dp, BorderDefault)
            ) {
                Text("Batal", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    viewModel.updateStatus(item.id, item.schoolId, selectedStatus)
                    onDismiss()
                },
                enabled = !viewModel.isUpdating,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryNavy)
            ) {
                if (viewModel.isUpdating) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Simpan Bukti", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun createTempImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "DELIVERY_${timeStamp}_",
        ".jpg",
        storageDir
    )
}
