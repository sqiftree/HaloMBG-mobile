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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Phone
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
import com.halombg.mobile.model.Review
import com.halombg.mobile.ui.theme.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SiswaReviewScreen(
    modifier: Modifier = Modifier,
    viewModel: SiswaReviewViewModel = viewModel()
) {
    val context = LocalContext.current
    var tempPhotoFile by remember { mutableStateOf<File?>(null) }
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher for taking picture
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempPhotoUri?.let { uri ->
                viewModel.capturedImageUri = uri
                viewModel.simulatedPhotoName = null
            }
        }
    }

    // Launcher for camera permission
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
            Toast.makeText(context, "Izin kamera ditolak. Tidak dapat mengambil foto.", Toast.LENGTH_SHORT).show()
        }
    }

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
                        text = if (viewModel.editingReviewId != null) "EDIT ULASAN ANDA" else "KIRIM ULASAN ANDA",
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
                            
                            // Visual photo preview block
                            if (viewModel.capturedImageUri != null) {
                                val bitmap = rememberBitmapFromUri(viewModel.capturedImageUri!!)
                                if (bitmap != null) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(120.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(1.dp, BorderDefault, RoundedCornerShape(8.dp))
                                    ) {
                                        Image(
                                            bitmap = bitmap.asImageBitmap(),
                                            contentDescription = "Foto Makanan",
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
                            } else if (viewModel.simulatedPhotoName != null) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Surface2, RoundedCornerShape(6.dp))
                                        .border(1.dp, BorderDefault, RoundedCornerShape(6.dp))
                                        .padding(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Info,
                                        contentDescription = null,
                                        tint = SecondaryGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Simulasi: ${viewModel.simulatedPhotoName}",
                                        fontSize = 12.sp,
                                        color = TextSecondary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { viewModel.clearPhoto() },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Hapus Foto",
                                            tint = StatusError,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
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
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                    ) {
                                        Icon(imageVector = Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (viewModel.capturedImageUri == null) "Ambil Foto" else "Foto Terlampir ✓",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    if (viewModel.isSimulationMode) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        TextButton(onClick = { viewModel.simulatePhoto() }) {
                                            Text("Simulasi", fontSize = 11.sp, color = TextTertiary)
                                        }
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (viewModel.editingReviewId != null) {
                                        TextButton(
                                            onClick = { viewModel.cancelEditing() },
                                            modifier = Modifier.padding(end = 8.dp)
                                        ) {
                                            Text("Batal", fontSize = 13.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                                        }
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
                                            Text(
                                                text = if (viewModel.editingReviewId != null) "Simpan" else "Kirim Ulasan",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
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
                        val isOwnReview = viewModel.isOwnReview(review)
                        ReviewItemRow(
                            review = review,
                            isOwnReview = isOwnReview,
                            onEdit = { viewModel.startEditing(review) },
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
fun ReviewItemRow(
    review: Review,
    isOwnReview: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
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
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Show flagged badge if review is flagged
                    if (review.flagStatus == "flagged") {
                        Surface(
                            shape = RoundedCornerShape(4.dp), // 4dp corners
                            color = Color(0xFFFFEBEE),
                            modifier = Modifier.padding(end = 8.dp)
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

                    if (isOwnReview) {
                        IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "Edit",
                                tint = PrimaryNavy,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Hapus",
                                tint = StatusError,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
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

@Composable
fun rememberBitmapFromUri(uri: Uri): Bitmap? {
    val context = LocalContext.current
    return remember(uri) {
        try {
            context.contentResolver.openInputStream(uri).use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            null
        }
    }
}

private fun createTempImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "JPEG_${timeStamp}_",
        ".jpg",
        storageDir
    )
}
