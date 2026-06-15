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
import com.halombg.mobile.data.api.FollowUpRequest
import com.halombg.mobile.model.Review
import kotlinx.coroutines.launch
import java.io.IOException

class SppgFollowUpViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(application)
    private val apiService = NetworkModule.getApiService(application)

    val criticalReviewsList = mutableStateListOf<Review>()

    var isLoading by mutableStateOf(false)
        private set

    var isUpdating by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
    var successMessage by mutableStateOf<String?>(null)

    val isSimulationMode = authRepository.isSimulationMode()
    private val sppgId = 1L // Default logged in SPPG Kitchen ID for mock

    fun loadCriticalReviews() {
        errorMessage = null
        isLoading = true

        viewModelScope.launch {
            if (isSimulationMode) {
                criticalReviewsList.clear()
                
                // Get all reviews for this SPPG kitchen
                val allReviews = MockData.getReviewsForSppg(sppgId)
                
                // Filter critical reviews: contains critical keywords, flagged by teacher, or followUpStatus is not "none"
                val criticalWords = listOf("basi", "bau", "busuk", "kotor")
                val filtered = allReviews.filter { review ->
                    review.flagStatus == "flagged" || 
                    review.followUpStatus != "none" ||
                    criticalWords.any { word -> review.content.contains(word, ignoreCase = true) }
                }

                // If they are critical but followUpStatus is "none", initialize it to "belum_diproses"
                filtered.forEach { review ->
                    if (review.followUpStatus == "none") {
                        review.followUpStatus = "belum_diproses"
                    }
                }
                
                criticalReviewsList.addAll(filtered)
                isLoading = false
            } else {
                try {
                    // Fetch from API (since it is simulation-ready, we fetch all reviews and filter or use the endpoint)
                    val response = apiService.getGuruReviews() // Fallback to reviews fetching
                    if (response.isSuccessful && response.body() != null) {
                        criticalReviewsList.clear()
                        val criticalWords = listOf("basi", "bau", "busuk", "kotor")
                        val list = response.body()!!.map { dto ->
                            val r = Review(
                                id = dto.id,
                                userId = dto.userId.toString(),
                                userName = "Siswa ${dto.userId}",
                                schoolId = dto.schoolId,
                                schoolName = MockData.schools.find { it.id == dto.schoolId }?.name ?: "Sekolah",
                                reviewDate = dto.reviewDate,
                                content = dto.content,
                                photo = dto.photo
                            )
                            // In real system backend sets followUpStatus, but we check if we should default it
                            val isCritical = criticalWords.any { word -> dto.content.contains(word, ignoreCase = true) }
                            if (isCritical) {
                                r.followUpStatus = "belum_diproses"
                            }
                            r
                        }.filter { 
                            it.followUpStatus != "none" || it.flagStatus == "flagged"
                        }
                        criticalReviewsList.addAll(list)
                    } else {
                        errorMessage = "Gagal memuat ulasan kritis: ${response.message()}"
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

    fun updateStatus(reviewId: Long, newStatus: String) {
        errorMessage = null
        successMessage = null
        isUpdating = true

        viewModelScope.launch {
            if (isSimulationMode) {
                // Find in MockData
                val index = MockData.reviews.indexOfFirst { it.id == reviewId }
                if (index != -1) {
                    MockData.reviews[index].followUpStatus = newStatus
                    
                    // Refresh in local list
                    val listIndex = criticalReviewsList.indexOfFirst { it.id == reviewId }
                    if (listIndex != -1) {
                        criticalReviewsList[listIndex] = criticalReviewsList[listIndex].copy(followUpStatus = newStatus)
                    }
                    successMessage = "Status penanganan berhasil diperbarui!"
                } else {
                    errorMessage = "Ulasan tidak ditemukan"
                }
                isUpdating = false
            } else {
                try {
                    val request = FollowUpRequest(status = newStatus)
                    val response = apiService.updateFollowUpStatus(reviewId, request)
                    if (response.isSuccessful) {
                        // Sync list
                        val listIndex = criticalReviewsList.indexOfFirst { it.id == reviewId }
                        if (listIndex != -1) {
                            criticalReviewsList[listIndex] = criticalReviewsList[listIndex].copy(followUpStatus = newStatus)
                        }
                        // Also sync to MockData
                        val mockIndex = MockData.reviews.indexOfFirst { it.id == reviewId }
                        if (mockIndex != -1) {
                            MockData.reviews[mockIndex].followUpStatus = newStatus
                        }
                        successMessage = "Status penanganan berhasil dikirim ke server!"
                    } else {
                        errorMessage = "Gagal memperbarui status penanganan: ${response.message()}"
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
}
