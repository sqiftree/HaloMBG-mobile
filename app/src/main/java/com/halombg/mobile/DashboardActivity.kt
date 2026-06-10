package com.halombg.mobile

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.halombg.mobile.databinding.ActivityDashboardBinding
import com.halombg.mobile.fragment.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private var currentRole: String = "Siswa"
    private var userEmail: String = "siswa@halombg.go.id"

    companion object {
        const val ID_TAB_1 = 101
        const val ID_TAB_2 = 102
        const val ID_TAB_3 = 103
    }

    override fun onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentRole = intent.getStringExtra("ROLE") ?: "Siswa"
        userEmail = intent.getStringExtra("USER_EMAIL") ?: "user@halombg.go.id"
        val goToAi = intent.getBooleanExtra("GO_TO_AI", false)

        setupHeader()
        setupBottomNav()

        binding.ivLogout.setOnClickListener {
            finish()
        }

        // Initialize with default or AI tab
        if (goToAi) {
            binding.bottomNavigationView.selectedItemId = ID_TAB_2
        } else {
            binding.bottomNavigationView.selectedItemId = ID_TAB_1
        }
    }

    private fun setupHeader() {
        binding.tvDashboardTitle.text = "Dashboard $currentRole"
        binding.tvDashboardRoleBadge.text = currentRole.uppercase()
    }

    private fun setupBottomNav() {
        val menu = binding.bottomNavigationView.menu
        menu.clear()

        when (currentRole) {
            "Siswa" -> {
                menu.add(Menu.NONE, ID_TAB_1, Menu.NONE, "Ulasan").setIcon(android.R.drawable.ic_menu_edit)
                menu.add(Menu.NONE, ID_TAB_2, Menu.NONE, "Validasi AI").setIcon(android.R.drawable.ic_menu_compass)
            }
            "SPPG (Dapur)" -> {
                menu.add(Menu.NONE, ID_TAB_1, Menu.NONE, "Menu").setIcon(android.R.drawable.ic_menu_agenda)
                menu.add(Menu.NONE, ID_TAB_2, Menu.NONE, "Distribusi").setIcon(android.R.drawable.ic_menu_send)
                menu.add(Menu.NONE, ID_TAB_3, Menu.NONE, "Validasi AI").setIcon(android.R.drawable.ic_menu_compass)
            }
            "Guru" -> {
                menu.add(Menu.NONE, ID_TAB_1, Menu.NONE, "Moderasi").setIcon(android.R.drawable.ic_menu_manage)
                menu.add(Menu.NONE, ID_TAB_2, Menu.NONE, "Validasi AI").setIcon(android.R.drawable.ic_menu_compass)
            }
            "Admin" -> {
                menu.add(Menu.NONE, ID_TAB_1, Menu.NONE, "Ringkasan").setIcon(android.R.drawable.ic_menu_info_details)
                menu.add(Menu.NONE, ID_TAB_2, Menu.NONE, "Validasi AI").setIcon(android.R.drawable.ic_menu_compass)
            }
        }

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                ID_TAB_1 -> getTab1Fragment()
                ID_TAB_2 -> getTab2Fragment()
                ID_TAB_3 -> getTab3Fragment()
                else -> null
            }
            if (fragment != null) {
                loadFragment(fragment)
                true
            } else {
                false
            }
        }
    }

    private fun getTab1Fragment(): Fragment {
        return when (currentRole) {
            "Siswa" -> SiswaFragment()
            "SPPG (Dapur)" -> SppgFragment.newInstance("menu")
            "Guru" -> GuruFragment()
            "Admin" -> AdminFragment()
            else -> SiswaFragment()
        }
    }

    private fun getTab2Fragment(): Fragment {
        return when (currentRole) {
            "SPPG (Dapur)" -> SppgFragment.newInstance("distribusi")
            else -> ValidasiAiFragment()
        }
    }

    private fun getTab3Fragment(): Fragment {
        return ValidasiAiFragment()
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
