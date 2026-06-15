package com.halombg.mobile.fragment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.halombg.mobile.data.AuthRepository
import com.halombg.mobile.data.MockData
import com.halombg.mobile.model.DistributionStatus
import com.halombg.mobile.ui.StatusBadge
import com.halombg.mobile.ui.theme.*

@Composable
fun SppgDistributionScreen() {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository(context) }
    val sppgId = remember { 
        // In a real app, this would be from the SPPG user's profile
        // For mock, we'll assume the first SPPG if not specified
        1L 
    }
    
    var distributionList by remember { mutableStateOf(MockData.getDistributionStatusesForSppg(sppgId)) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface2)
            .padding(16.dp)
    ) {
        Text(
            text = "MANAJEMEN DISTRIBUSI",
            color = PrimaryNavy,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Kelola status pengiriman paket makanan ke sekolah-sekolah mitra.",
            color = TextSecondary,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(distributionList) { status ->
                DistributionCard(
                    status = status,
                    onUpdateStatus = { newStatus ->
                        MockData.updateDistributionStatus(sppgId, status.schoolId, newStatus)
                        distributionList = MockData.getDistributionStatusesForSppg(sppgId)
                    }
                )
            }
        }
    }
}

@Composable
fun DistributionCard(
    status: DistributionStatus,
    onUpdateStatus: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface1),
        border = BorderStroke(1.dp, BorderDefault)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = status.schoolName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryNavy
                    )
                    Text(
                        text = "Update terakhir: ${status.statusUpdatedAt}",
                        fontSize = 11.sp,
                        color = TextTertiary
                    )
                }
                StatusBadge(status = status.status)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Ubah Status:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusButton(
                    label = "Masak",
                    icon = Icons.Outlined.Refresh,
                    active = status.status == "belum_diantar",
                    color = StatusWarning,
                    onClick = { onUpdateStatus("belum_diantar") },
                    modifier = Modifier.weight(1f)
                )
                StatusButton(
                    label = "Kirim",
                    icon = Icons.AutoMirrored.Outlined.Send,
                    active = status.status == "siap_diantar",
                    color = StatusInfo,
                    onClick = { onUpdateStatus("siap_diantar") },
                    modifier = Modifier.weight(1f)
                )
                StatusButton(
                    label = "Tiba",
                    icon = Icons.Outlined.Check,
                    active = status.status == "sudah_diantar",
                    color = StatusSuccess,
                    onClick = { onUpdateStatus("sudah_diantar") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StatusButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    active: Boolean,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (active) color.copy(alpha = 0.1f) else Color.Transparent,
            contentColor = if (active) color else TextSecondary
        ),
        border = BorderStroke(
            width = if (active) 2.dp else 1.dp,
            color = if (active) color else BorderDefault
        ),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Text(text = label, fontSize = 10.sp, fontWeight = if (active) FontWeight.Bold else FontWeight.Normal)
        }
    }
}
