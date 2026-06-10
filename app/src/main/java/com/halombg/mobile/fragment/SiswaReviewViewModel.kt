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
import com.halombg.mobile.data.api.ProfileDto
import com.halombg.mobile.model.DailyMenu
import com.halombg.mobile.model.Review
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class SiswaReviewViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(application)
    private val apiService = NetworkModule.getApiService(application)

    // SPPG Dapur details connected to student
    var sppgProfile by mutableStateOf<ProfileDto?>(null)
        private set

    // Today's Menu details
    var todayMenu by mutableStateOf<DailyMenu?>(null)
        private set

    // Reviews list
    val reviewsList = mutableStateListOf<Review>()

    // Inputs
    var reviewContent by mutableStateOf("")
    var simulatedPhotoName by mutableStateOf<String?>(null)

    var isLoading by mutableStateOf(false)
        private set

    var isSubmitting by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
    var successMessage by mutableStateOf<String?>(null)

    val isSimulationMode = authRepository.isSimulationMode()
    private val studentSchoolId = 1L // Mock SD Negeri 1 Jaya
    private val studentName = authRepository.getUserName() ?: "Ahmad Dani"

    fun loadData() {
        errorMessage = null
        isLoading = true

        viewModelScope.launch {
            if (isSimulationMode) {
                // Load SPPG Profile
                val school = MockData.schools.find { it.id == studentSchoolId }
                val sppgId = school?.sppgId
                if (sppgId != null) {
                    val mockSppg = MockData.getSppgProfile(sppgId)
                    if (mockSppg != null) {
                        sppgProfile = ProfileDto(
                            id = mockSppg.id,
                            kitchenName = mockSppg.kitchenName,
                            address = mockSppg.address,
                            district = mockSppg.district,
                            province = mockSppg.province,
                            contactPersonName = mockSppg.contactPersonName,
                            contactPhone = mockSppg.contactPhone,
                            contactEmail = mockSppg.contactEmail,
                            description = mockSppg.description,
                            productionCapacity = mockSppg.productionCapacity
                        )
                    }
                }

                // Load Today's Menu
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                if (sppgId != null) {
                    todayMenu = MockData.getDailyMenuForSppg(sppgId, today)
                }

                // Load Reviews
                reviewsList.clear()
                reviewsList.addAll(MockData.getReviewsForSchool(studentSchoolId))

                isLoading = false
            } else {
                try {
                    // Fetch SPPG info from backend API
                    val sppgResponse = apiService.getSiswaSppgInfo()
                    if (sppgResponse.isSuccessful) {
                        sppgProfile = sppgResponse.body()
                    } else {
                        errorMessage = "Gagal memuat info dapur SPPG"
                    }

                    // Fallback local menu and reviews lookup
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val school = MockData.schools.find { it.id == studentSchoolId }
                    val sppgId = school?.sppgId
                    if (sppgId != null) {
                        todayMenu = MockData.getDailyMenuForSppg(sppgId, today)
                    }
                    reviewsList.clear()
                    reviewsList.addAll(MockData.getReviewsForSchool(studentSchoolId))

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

    fun submitReview() {
        errorMessage = null
        successMessage = null

        val content = reviewContent.trim()
        if (content.length < 10) {
            errorMessage = "Ulasan minimal 10 karakter"
            return
        }

        isSubmitting = true

        viewModelScope.launch {
            if (isSimulationMode) {
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val newReview = Review(
                    id = (MockData.reviews.maxOfOrNull { it.id } ?: 0L) + 1,
                    userId = "siswa_1",
                    userName = studentName,
                    schoolId = studentSchoolId,
                    schoolName = MockData.schools.find { it.id == studentSchoolId }?.name ?: "Sekolah",
                    reviewDate = today,
                    content = content,
                    photo = simulatedPhotoName
                )
                MockData.addReview(newReview)
                
                // Refresh list
                reviewsList.clear()
                reviewsList.addAll(MockData.getReviewsForSchool(studentSchoolId))

                // Reset
                reviewContent = ""
                simulatedPhotoName = null
                successMessage = "Ulasan berhasil dikirim!"
                isSubmitting = false
            } else {
                // For this phase, if connected to API, we post locally or simulate posting
                // since MOB-02 is partially focused on profile and autologin for now.
                // We'll post it locally to the MockData database but flag it as success.
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val newReview = Review(
                    id = (MockData.reviews.maxOfOrNull { it.id } ?: 0L) + 1,
                    userId = "siswa_1",
                    userName = studentName,
                    schoolId = studentSchoolId,
                    schoolName = MockData.schools.find { it.id == studentSchoolId }?.name ?: "Sekolah",
                    reviewDate = today,
                    content = content,
                    photo = simulatedPhotoName
                )
                MockData.addReview(newReview)

                reviewsList.clear()
                reviewsList.addAll(MockData.getReviewsForSchool(studentSchoolId))

                reviewContent = ""
                simulatedPhotoName = null
                successMessage = "Ulasan berhasil disimpan secara lokal!"
                isSubmitting = false
            }
        }
    }

    fun simulatePhoto() {
        simulatedPhotoName = "makan_siswa_${System.currentTimeMillis() / 1000}.jpg"
    }

    fun deleteReview(review: Review) {
        MockData.reviews.remove(review)
        reviewsList.remove(review)
        successMessage = "Ulasan berhasil dihapus"
    }
}
