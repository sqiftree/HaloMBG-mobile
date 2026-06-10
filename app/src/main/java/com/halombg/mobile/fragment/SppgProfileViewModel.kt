package com.halombg.mobile.fragment

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.halombg.mobile.data.AuthRepository
import com.halombg.mobile.data.MockData
import com.halombg.mobile.data.api.NetworkModule
import com.halombg.mobile.data.api.ProfileDto
import com.halombg.mobile.data.api.UpdateSppgProfileRequest
import kotlinx.coroutines.launch
import java.io.IOException

class SppgProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(application)
    private val apiService = NetworkModule.getApiService(application)

    var kitchenName by mutableStateOf("")
    var address by mutableStateOf("")
    var district by mutableStateOf("")
    var province by mutableStateOf("")
    var contactPersonName by mutableStateOf("")
    var contactPhone by mutableStateOf("")
    var contactEmail by mutableStateOf("")
    var description by mutableStateOf("")
    var productionCapacity by mutableStateOf<Int?>(null)
    var isActive by mutableStateOf(true)

    var isLoading by mutableStateOf(false)
        private set

    var isSaving by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
    var successMessage by mutableStateOf<String?>(null)

    val isSimulationMode = authRepository.isSimulationMode()

    fun loadProfile() {
        errorMessage = null
        successMessage = null
        isLoading = true

        viewModelScope.launch {
            if (isSimulationMode) {
                // Load from mock data
                val mockProfile = MockData.getSppgProfile(1L) // Dapur Sehat Kartasura
                if (mockProfile != null) {
                    kitchenName = mockProfile.kitchenName
                    address = mockProfile.address
                    district = mockProfile.district
                    province = mockProfile.province
                    contactPersonName = mockProfile.contactPersonName
                    contactPhone = mockProfile.contactPhone
                    contactEmail = mockProfile.contactEmail ?: ""
                    description = mockProfile.description ?: ""
                    productionCapacity = mockProfile.productionCapacity
                    isActive = mockProfile.isActive
                } else {
                    errorMessage = "Profil dapur simulasi tidak ditemukan"
                }
                isLoading = false
            } else {
                try {
                    val response = apiService.getSppgProfile()
                    if (response.isSuccessful && response.body() != null) {
                        populateFields(response.body()!!)
                    } else {
                        errorMessage = "Gagal memuat profil: ${response.message()}"
                    }
                } catch (e: IOException) {
                    errorMessage = "Kesalahan jaringan: Gagal terhubung ke server."
                } catch (e: Exception) {
                    errorMessage = "Terjadi kesalahan: ${e.localizedMessage ?: "Unknown Error"}"
                } finally {
                    isLoading = false
                }
            }
        }
    }

    fun saveProfile() {
        errorMessage = null
        successMessage = null

        if (address.trim().isEmpty()) {
            errorMessage = "Alamat tidak boleh kosong"
            return
        }
        if (contactPersonName.trim().isEmpty()) {
            errorMessage = "Nama kontak person tidak boleh kosong"
            return
        }
        if (contactPhone.trim().isEmpty()) {
            errorMessage = "Nomor telepon/WA tidak boleh kosong"
            return
        }

        isSaving = true

        viewModelScope.launch {
            if (isSimulationMode) {
                // Save to MockData
                val index = MockData.sppgProfiles.indexOfFirst { it.id == 1L }
                if (index != -1) {
                    val current = MockData.sppgProfiles[index]
                    MockData.sppgProfiles[index] = current.copy(
                        address = address,
                        contactPersonName = contactPersonName,
                        contactPhone = contactPhone,
                        contactEmail = contactEmail.ifBlank { null },
                        description = description.ifBlank { null }
                    )
                    successMessage = "Profil simulasi berhasil diperbarui!"
                } else {
                    errorMessage = "Gagal memperbarui profil simulasi"
                }
                isSaving = false
            } else {
                try {
                    val request = UpdateSppgProfileRequest(
                        address = address,
                        contactPersonName = contactPersonName,
                        contactPhone = contactPhone,
                        contactEmail = contactEmail.ifBlank { null },
                        description = description.ifBlank { null }
                    )
                    val response = apiService.updateSppgProfile(request)
                    if (response.isSuccessful && response.body() != null) {
                        populateFields(response.body()!!)
                        successMessage = "Profil dapur berhasil diperbarui!"
                    } else {
                        errorMessage = "Gagal memperbarui profil: ${response.message()}"
                    }
                } catch (e: IOException) {
                    errorMessage = "Kesalahan jaringan: Gagal terhubung ke server."
                } catch (e: Exception) {
                    errorMessage = "Terjadi kesalahan: ${e.localizedMessage ?: "Unknown Error"}"
                } finally {
                    isSaving = false
                }
            }
        }
    }

    private fun populateFields(dto: ProfileDto) {
        kitchenName = dto.kitchenName ?: ""
        address = dto.address ?: ""
        district = dto.district ?: ""
        province = dto.province ?: ""
        contactPersonName = dto.contactPersonName ?: ""
        contactPhone = dto.contactPhone ?: ""
        contactEmail = dto.contactEmail ?: ""
        description = dto.description ?: ""
        productionCapacity = dto.productionCapacity
        isActive = true
    }
}
