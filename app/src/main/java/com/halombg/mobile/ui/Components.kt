package com.halombg.mobile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.halombg.mobile.ui.theme.*

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource

@Composable
fun Logo(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp
) {
    Image(
        painter = painterResource(id = com.halombg.mobile.R.drawable.ic_logo_halombg),
        contentDescription = "HaloMBG Logo",
        modifier = modifier.size(size)
    )
}

@Composable
fun StatusBadge(status: String) {
    val (text, color, bgColor) = when (status) {
        "sudah_diantar" -> Triple("TELAH TIBA", StatusSuccess, Color(0xFFE8F5E9))
        "siap_diantar" -> Triple("SIAP DIKIRIM", StatusInfo, Color(0xFFE3F2FD))
        "belum_diantar" -> Triple("PROSES MASAK", StatusWarning, Color(0xFFFFFDE7))
        else -> Triple("TIDAK DIKETAHUI", TextTertiary, Surface3)
    }

    Surface(
        shape = RoundedCornerShape(4.dp),
        color = bgColor
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
