package com.halombg.mobile.fragment

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.halombg.mobile.data.AuthRepository
import com.halombg.mobile.data.MockData
import com.halombg.mobile.data.api.NetworkModule
import com.halombg.mobile.model.Review
import kotlinx.coroutines.launch
import java.io.IOException

class GuruModerasiViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(application)
    private val apiService = NetworkModule.getApiService(application)

    val reviewsList = mutableStateListOf<Review>()

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
    var successMessage by mutableStateOf<String?>(null)

    val isSimulationMode = authRepository.isSimulationMode()
    private val teacherSchoolId = 1L // SD Negeri 1 Jaya (Mock)

    fun loadReviews() {
        errorMessage = null
        isLoading = true

        viewModelScope.launch {
            if (isSimulationMode) {
                reviewsList.clear()
                reviewsList.addAll(MockData.getReviewsForSchool(teacherSchoolId))
                isLoading = false
            } else {
                try {
                    val response = apiService.getGuruReviews()
                    if (response.isSuccessful && response.body() != null) {
                        reviewsList.clear()
                        val list = response.body()!!.map { dto ->
                            Review(
                                id = dto.id,
                                userId = dto.userId.toString(),
                                userName = "Siswa ${dto.userId}",
                                schoolId = dto.schoolId,
                                schoolName = MockData.schools.find { it.id == dto.schoolId }?.name ?: "Sekolah",
                                reviewDate = dto.reviewDate,
                                content = dto.content,
                                photo = dto.photo
                            )
                        }
                        reviewsList.addAll(list)
                    } else {
                        errorMessage = "Gagal memuat ulasan: ${response.message()}"
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

    fun toggleFlag(review: Review) {
        errorMessage = null
        successMessage = null

        viewModelScope.launch {
            if (isSimulationMode) {
                // Find in MockData
                val index = MockData.reviews.indexOfFirst { it.id == review.id }
                if (index != -1) {
                    val current = MockData.reviews[index]
                    val newFlag = if (current.flagStatus == "flagged") "none" else "flagged"
                    MockData.reviews[index] = current.copy(flagStatus = newFlag)
                    
                    // Update locally
                    val listIndex = reviewsList.indexOfFirst { it.id == review.id }
                    if (listIndex != -1) {
                        reviewsList[listIndex] = reviewsList[listIndex].copy(flagStatus = newFlag)
                    }
                    successMessage = if (newFlag == "flagged") "Ulasan berhasil dilaporkan!" else "Laporan ulasan dibatalkan."
                } else {
                    errorMessage = "Ulasan tidak ditemukan"
                }
            } else {
                try {
                    val response = apiService.flagReview(review.id)
                    if (response.isSuccessful && response.body() != null) {
                        val reviewDto = response.body()!!.review
                        val newFlag = reviewDto?.photo ?: if (review.flagStatus == "flagged") "none" else "flagged" // Wait, actually the API returns the review
                        
                        // Toggle local list
                        val listIndex = reviewsList.indexOfFirst { it.id == review.id }
                        if (listIndex != -1) {
                            val nextFlag = if (reviewsList[listIndex].flagStatus == "flagged") "none" else "flagged"
                            reviewsList[listIndex] = reviewsList[listIndex].copy(flagStatus = nextFlag)
                            
                            // Sync to MockData for consistency
                            val mockIndex = MockData.reviews.indexOfFirst { it.id == review.id }
                            if (mockIndex != -1) {
                                MockData.reviews[mockIndex] = MockData.reviews[mockIndex].copy(flagStatus = nextFlag)
                            }
                        }
                        successMessage = "Aksi flag berhasil diperbarui!"
                    } else {
                        errorMessage = "Gagal mengubah status flag: ${response.message()}"
                    }
                } catch (e: IOException) {
                    errorMessage = "Kesalahan jaringan: Gagal terhubung ke server."
                } catch (e: Exception) {
                    errorMessage = "Terjadi kesalahan: ${e.localizedMessage ?: "Unknown Error"}"
                }
            }
        }
    }
}
