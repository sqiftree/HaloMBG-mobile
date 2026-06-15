package com.halombg.mobile.fragment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.halombg.mobile.data.AuthRepository
import com.halombg.mobile.data.MockData
import com.halombg.mobile.model.Review
import com.halombg.mobile.ui.theme.*

@Composable
fun GuruModerationScreen() {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository(context) }
    val schoolId = remember { authRepository.getSchoolId() }
    
    var reviews by remember { mutableStateOf(MockData.getReviewsForSchool(schoolId)) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface2)
            .padding(16.dp)
    ) {
        Text(
            text = "MODERASI ULASAN SISWA",
            color = PrimaryNavy,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Pantau dan moderasi ulasan yang diberikan oleh siswa di sekolah Anda.",
            color = TextSecondary,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (reviews.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Belum ada ulasan untuk dimoderasi.", color = TextTertiary)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reviews) { review ->
                    ModerationReviewCard(
                        review = review,
                        onFlag = {
                            review.flagStatus = if (review.flagStatus == "flagged") "none" else "flagged"
                            reviews = MockData.getReviewsForSchool(schoolId)
                        },
                        onDelete = {
                            review.flagStatus = "deleted"
                            reviews = MockData.getReviewsForSchool(schoolId)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ModerationReviewCard(
    review: Review,
    onFlag: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface1),
        border = BorderStroke(1.dp, if (review.flagStatus == "flagged") StatusError.copy(alpha = 0.5f) else BorderDefault)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = review.userName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNavy
                    )
                    Text(
                        text = review.reviewDate,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
                
                if (review.flagStatus == "flagged") {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFFFEBEE)
                    ) {
                        Text(
                            text = "DITANDAI",
                            color = StatusError,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = review.content,
                fontSize = 13.sp,
                color = TextPrimary
            )
            
            if (review.photo != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "✓ Lampiran foto: ${review.photo}",
                    fontSize = 11.sp,
                    color = StatusSuccess,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onFlag,
                    colors = ButtonDefaults.textButtonColors(contentColor = if (review.flagStatus == "flagged") StatusInfo else StatusWarning)
                ) {
                    Icon(
                        imageVector = if (review.flagStatus == "flagged") Icons.Outlined.CheckCircle else Icons.Outlined.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (review.flagStatus == "flagged") "Batal Tandai" else "Tandai", fontSize = 12.sp)
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = StatusError),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(imageVector = Icons.Outlined.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hapus", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
