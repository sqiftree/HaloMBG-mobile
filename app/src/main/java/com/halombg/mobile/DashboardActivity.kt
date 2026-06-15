package com.halombg.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.content.Intent
import com.halombg.mobile.data.AuthRepository
import com.halombg.mobile.ui.theme.HaloMBGTheme

class DashboardActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val role = intent.getStringExtra("ROLE") ?: "Siswa"

        setContent {
            HaloMBGTheme {
                DashboardScreen(
                    role = role,
                    onLogout = {
                        AuthRepository(this).clearAll()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}
