package com.halombg.mobile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.halombg.mobile.ui.theme.HaloMBGTheme

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val preselectedRole = intent.getStringExtra("PRESELECT_ROLE")

        setContent {
            HaloMBGTheme {
                LoginScreen(
                    preselectedRole = preselectedRole,
                    onLoginSuccess = { role, email ->
                        Toast.makeText(this, "Berhasil masuk sebagai $role", Toast.LENGTH_SHORT).show()
                        
                        val dashboardIntent = Intent(this, DashboardActivity::class.java).apply {
                            putExtra("ROLE", role)
                            putExtra("USER_EMAIL", email)
                        }
                        startActivity(dashboardIntent)
                        finish()
                    },
                    onCancel = {
                        finish()
                    }
                )
            }
        }
    }
}
