package com.halombg.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.halombg.mobile.ui.theme.HaloMBGTheme

class DashboardActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val role = intent.getStringExtra("ROLE") ?: "Siswa"
        val goToAi = intent.getBooleanExtra("GO_TO_AI", false)

        setContent {
            HaloMBGTheme {
                DashboardScreen(
                    role = role,
                    initialGoToAi = goToAi,
                    onLogout = { finish() }
                )
            }
        }
    }
}
