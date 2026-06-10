package com.halombg.mobile

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.halombg.mobile.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val roles = listOf("Siswa", "SPPG (Dapur)", "Guru", "Admin")

    override fun onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRole.adapter = adapter

        // Handle pre-selected role
        val preselected = intent.getStringExtra("PRESELECT_ROLE")
        if (preselected != null && roles.contains(preselected)) {
            binding.spinnerRole.setSelection(roles.indexOf(preselected))
        }

        binding.btnLogin.setOnClickListener {
            val selectedRole = binding.spinnerRole.selectedItem.toString()
            val email = binding.etEmail.text.toString().trim()
            
            if (email.isEmpty()) {
                binding.etEmail.error = "Email tidak boleh kosong"
                return@setOnClickListener
            }

            // Simulate authentication success
            Toast.makeText(this, "Berhasil masuk sebagai $selectedRole", Toast.LENGTH_SHORT).show()
            
            val dashboardIntent = Intent(this, DashboardActivity::class.java).apply {
                putExtra("ROLE", selectedRole)
                putExtra("USER_EMAIL", email)
            }
            startActivity(dashboardIntent)
            finish()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }
}
