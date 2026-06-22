package com.halombg.mobile.fragment

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.halombg.mobile.data.AuthRepository
import com.halombg.mobile.data.MockData
import com.halombg.mobile.data.api.NetworkModule
import com.halombg.mobile.data.api.DistributionStatusDto
import com.halombg.mobile.data.api.UpdateDistributionRequest
import com.halombg.mobile.model.DistributionStatus
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class SppgDistribusiViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(application)
    private val apiService = NetworkModule.getApiService(application)

    val distributionList = mutableStateListOf<DistributionStatus>()

    var isLoading by mutableStateOf(false)
        private set

    var isUpdating by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
    var successMessage by mutableStateOf<String?>(null)

    // Camera Capture Uri
    var capturedImageUri by mutableStateOf<Uri?>(null)

    val isSimulationMode = authRepository.isSimulationMode()
    private val sppgId = 1L // Default logged in SPPG Kitchen ID for mock

    fun loadDistributions() {
        errorMessage = null
        isLoading = true

        viewModelScope.launch {
            if (isSimulationMode) {
                // Fetch from MockData
                distributionList.clear()
                distributionList.addAll(MockData.getDistributionStatusesForSppg(sppgId))
                isLoading = false
            } else {
                try {
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val response = apiService.getSppgDistributions(today)
                    if (response.isSuccessful && response.body() != null) {
                        distributionList.clear()
                        val list = response.body()!!.map { dto ->
                            DistributionStatus(
                                id = dto.id,
                                sppgId = dto.sppgId,
                                schoolId = dto.schoolId,
                                schoolName = dto.schoolName,
                                distributedAt = dto.distributedAt,
                                status = dto.status,
                                statusUpdatedAt = dto.statusUpdatedAt,
                                photo = dto.photo
                            )
                        }
                        distributionList.addAll(list)
                    } else {
                        errorMessage = "Gagal memuat jadwal distribusi: ${response.message()}"
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

    private fun convertUriToBase64(uri: Uri): String? {
        return try {
            val contentResolver = getApplication<Application>().contentResolver
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap != null) {
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                val bytes = outputStream.toByteArray()
                Base64.encodeToString(bytes, Base64.NO_WRAP)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun updateStatus(id: Long, schoolId: Long, newStatus: String) {
        errorMessage = null
        successMessage = null
        isUpdating = true

        viewModelScope.launch {
            val base64Image = capturedImageUri?.let { convertUriToBase64(it) }

            if (isSimulationMode) {
                // Save locally
                MockData.updateDistributionStatus(sppgId, schoolId, newStatus, base64Image ?: capturedImageUri?.lastPathSegment)
                
                // Refresh list
                distributionList.clear()
                distributionList.addAll(MockData.getDistributionStatusesForSppg(sppgId))

                capturedImageUri = null
                successMessage = "Status berhasil diperbarui!"
                isUpdating = false
            } else {
                try {
                    val request = UpdateDistributionRequest(
                        status = newStatus,
                        photo = base64Image
                    )
                    val response = apiService.updateDistributionStatus(id, request)
                    if (response.isSuccessful && response.body() != null) {
                        val dto = response.body()!!
                        
                        // Update item in local list
                        val index = distributionList.indexOfFirst { it.id == id }
                        if (index != -1) {
                            distributionList[index] = distributionList[index].copy(
                                status = dto.status,
                                statusUpdatedAt = dto.statusUpdatedAt,
                                photo = dto.photo ?: base64Image ?: capturedImageUri?.lastPathSegment
                            )
                        }

                        // Also sync to MockData local storage for visual consistency
                        MockData.updateDistributionStatus(sppgId, schoolId, newStatus, dto.photo ?: base64Image ?: capturedImageUri?.lastPathSegment)

                        capturedImageUri = null
                        successMessage = "Status berhasil diperbarui ke server!"
                    } else {
                        errorMessage = "Gagal memperbarui status: ${response.message()}"
                    }
                } catch (e: IOException) {
                    errorMessage = "Kesalahan jaringan: Gagal terhubung ke server."
                } catch (e: Exception) {
                    errorMessage = "Terjadi kesalahan: ${e.localizedMessage ?: "Unknown Error"}"
                } finally {
                    isUpdating = false
                }
            }
        }
    }

    fun clearPhoto() {
        capturedImageUri = null
    }
}
