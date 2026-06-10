package com.halombg.mobile

import android.app.Application
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.halombg.mobile.data.AuthRepository
import com.halombg.mobile.data.api.LoginRequest
import com.halombg.mobile.data.api.NetworkModule
import kotlinx.coroutines.launch
import java.io.IOException

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(application)
    private val apiService = NetworkModule.getApiService(application)

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var selectedRole by mutableStateOf("Siswa") // Siswa, SPPG (Dapur), Guru
    var isSimulationMode by mutableStateOf(false)

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)

    var loginSuccess by mutableStateOf(false)
        private set

    var userRoleForNavigation by mutableStateOf<String?>(null)
        private set

    init {
        // Load stored simulation mode preference if any
        isSimulationMode = authRepository.isSimulationMode()
    }

    fun onRoleSelected(role: String) {
        selectedRole = role
    }

    fun login() {
        errorMessage = null

        // 1. Validation
        val emailTrimmed = email.trim()
        val passwordTrimmed = password.trim()

        if (emailTrimmed.isEmpty()) {
            errorMessage = "Email tidak boleh kosong"
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailTrimmed).matches()) {
            errorMessage = "Format email tidak valid"
            return
        }

        if (passwordTrimmed.isEmpty()) {
            errorMessage = "Kata sandi tidak boleh kosong"
            return
        }

        if (passwordTrimmed.length < 6) {
            errorMessage = "Kata sandi minimal 6 karakter"
            return
        }

        isLoading = true

        viewModelScope.launch {
            if (isSimulationMode) {
                val inferredRole = when {
                    emailTrimmed.contains("siswa", ignoreCase = true) -> "Siswa"
                    emailTrimmed.contains("guru", ignoreCase = true) -> "Guru"
                    emailTrimmed.contains("sppg", ignoreCase = true) || emailTrimmed.contains("dapur", ignoreCase = true) -> "SPPG (Dapur)"
                    else -> "Siswa"
                }
                performSimulationLogin(emailTrimmed, inferredRole)
            } else {
                try {
                    val response = apiService.login(LoginRequest(emailTrimmed, passwordTrimmed))
                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()!!
                        val user = data.user
                        
                        if (user == null) {
                            errorMessage = "Data pengguna tidak ditemukan"
                            isLoading = false
                            return@launch
                        }

                        if (user.role.lowercase() == "admin") {
                            errorMessage = "Admin tidak memiliki akses ke aplikasi mobile"
                            isLoading = false
                            return@launch
                        }

                        // Save details
                        authRepository.saveToken(data.token ?: "")
                        authRepository.saveUserRole(mapRoleFromApi(user.role))
                        authRepository.saveUserEmail(user.email)
                        authRepository.saveUserName(user.name)
                        authRepository.saveSimulationMode(false)

                        userRoleForNavigation = authRepository.getUserRole()
                        loginSuccess = true
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: "Email atau sandi salah"
                        errorMessage = errorMsg
                    }
                } catch (e: IOException) {
                    // Network connection failure, fallback to simulation mode automatically
                    errorMessage = "Gagal terhubung ke server. Mengalihkan ke mode simulasi..."
                    isSimulationMode = true
                    authRepository.saveSimulationMode(true)
                    
                    // Wait a moment so user can read the auto-fallback warning, then login via simulation
                    kotlinx.coroutines.delay(1500)
                    val inferredRole = when {
                        emailTrimmed.contains("siswa", ignoreCase = true) -> "Siswa"
                        emailTrimmed.contains("guru", ignoreCase = true) -> "Guru"
                        emailTrimmed.contains("sppg", ignoreCase = true) || emailTrimmed.contains("dapur", ignoreCase = true) -> "SPPG (Dapur)"
                        else -> "Siswa"
                    }
                    performSimulationLogin(emailTrimmed, inferredRole)
                } catch (e: Exception) {
                    errorMessage = "Terjadi kesalahan: ${e.localizedMessage ?: "Unknown Error"}"
                } finally {
                    isLoading = false
                }
            }
        }
    }

    private fun performSimulationLogin(email: String, role: String) {
        // Setup mock user details based on role choice
        val name = when (role) {
            "Siswa" -> "Ahmad Dani"
            "SPPG (Dapur)" -> "Budi Santoso"
            "Guru" -> "Ibu Retno"
            else -> "User"
        }

        authRepository.clearToken() // No server token in simulation
        authRepository.saveUserRole(role)
        authRepository.saveUserEmail(email)
        authRepository.saveUserName(name)
        authRepository.saveSimulationMode(true)

        userRoleForNavigation = role
        loginSuccess = true
        isLoading = false
    }

    private fun mapRoleFromApi(apiRole: String): String {
        return when (apiRole.lowercase()) {
            "siswa" -> "Siswa"
            "sppg" -> "SPPG (Dapur)"
            "guru" -> "Guru"
            else -> "Siswa"
        }
    }

    fun clearState() {
        loginSuccess = false
        userRoleForNavigation = null
        isLoading = false
        errorMessage = null
    }
}
