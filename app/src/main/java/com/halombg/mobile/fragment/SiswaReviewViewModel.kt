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
import com.halombg.mobile.data.api.ProfileDto
import com.halombg.mobile.data.api.ReviewRequest
import com.halombg.mobile.model.DailyMenu
import com.halombg.mobile.model.Review
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
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
    var capturedImageUri by mutableStateOf<Uri?>(null)

    var isLoading by mutableStateOf(false)
        private set

    var isSubmitting by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
    var successMessage by mutableStateOf<String?>(null)

    val isSimulationMode = authRepository.isSimulationMode()
    private val studentSchoolId = authRepository.getSchoolId()
    val studentName = authRepository.getUserName() ?: "Ahmad Dani"
    val currentUserEmail: String
        get() = authRepository.getUserEmail()?.trim() ?: ""
    var editingReviewId by mutableStateOf<Long?>(null)

    fun loadData() {
        if (studentSchoolId == -1L) {
            errorMessage = "Data sekolah tidak ditemukan. Silakan login ulang."
            isLoading = false
            return
        }
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

    fun submitReview() {
        errorMessage = null
        successMessage = null

        val content = reviewContent.trim()
        if (content.isEmpty()) {
            errorMessage = "Ulasan tidak boleh kosong"
            return
        }
        
        if (content.length < 10) {
            errorMessage = "Ulasan terlalu pendek (minimal 10 karakter)"
            return
        }

        isSubmitting = true

        viewModelScope.launch {
            val isEdit = editingReviewId != null
            if (isSimulationMode) {
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                if (isEdit) {
                    val index = MockData.reviews.indexOfFirst { it.id == editingReviewId }
                    if (index != -1) {
                        val oldReview = MockData.reviews[index]
                        MockData.reviews[index] = oldReview.copy(
                            content = content,
                            photo = simulatedPhotoName ?: capturedImageUri?.lastPathSegment
                        )
                    }
                } else {
                    val newReview = Review(
                        id = (MockData.reviews.maxOfOrNull { it.id } ?: 0L) + 1,
                        userId = currentUserEmail,
                        userName = studentName,
                        schoolId = studentSchoolId,
                        schoolName = MockData.schools.find { it.id == studentSchoolId }?.name ?: "Sekolah",
                        reviewDate = today,
                        content = content,
                        photo = simulatedPhotoName ?: capturedImageUri?.lastPathSegment
                    )
                    MockData.addReview(newReview)
                }
                
                // Refresh list
                reviewsList.clear()
                reviewsList.addAll(MockData.getReviewsForSchool(studentSchoolId))

                // Reset
                reviewContent = ""
                simulatedPhotoName = null
                capturedImageUri = null
                editingReviewId = null
                successMessage = if (isEdit) "Ulasan berhasil diperbarui!" else "Ulasan berhasil dikirim!"
                isSubmitting = false
            } else {
                try {
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    if (isEdit) {
                        // In non-simulation mode, update it locally in MockData and reviewsList to reflect the change
                        val index = MockData.reviews.indexOfFirst { it.id == editingReviewId }
                        if (index != -1) {
                            val oldReview = MockData.reviews[index]
                            MockData.reviews[index] = oldReview.copy(
                                content = content,
                                photo = simulatedPhotoName ?: capturedImageUri?.lastPathSegment
                            )
                        }
                        reviewsList.clear()
                        reviewsList.addAll(MockData.getReviewsForSchool(studentSchoolId))

                        reviewContent = ""
                        capturedImageUri = null
                        simulatedPhotoName = null
                        editingReviewId = null
                        successMessage = "Ulasan berhasil diperbarui!"
                    } else {
                        val base64Image = capturedImageUri?.let { convertUriToBase64(it) }

                        val request = ReviewRequest(
                            content = content,
                            reviewDate = today,
                            photo = base64Image
                        )

                        val response = apiService.postSiswaReview(request)
                        if (response.isSuccessful) {
                            val reviewDto = response.body()?.review
                            val newReview = Review(
                                id = reviewDto?.id ?: ((MockData.reviews.maxOfOrNull { it.id } ?: 0L) + 1),
                                userId = currentUserEmail,
                                userName = studentName,
                                schoolId = studentSchoolId,
                                schoolName = MockData.schools.find { it.id == studentSchoolId }?.name ?: "Sekolah",
                                reviewDate = today,
                                content = content,
                                photo = reviewDto?.photo ?: capturedImageUri?.lastPathSegment
                            )
                            MockData.addReview(newReview)

                            reviewsList.clear()
                            reviewsList.addAll(MockData.getReviewsForSchool(studentSchoolId))

                            reviewContent = ""
                            capturedImageUri = null
                            simulatedPhotoName = null
                            successMessage = "Ulasan berhasil dikirim ke server!"
                        } else {
                            errorMessage = "Gagal mengirim ulasan: ${response.message()}"
                        }
                    }
                } catch (e: IOException) {
                    errorMessage = "Kesalahan jaringan: Gagal terhubung ke server."
                } catch (e: Exception) {
                    errorMessage = "Terjadi kesalahan: ${e.localizedMessage ?: "Unknown Error"}"
                } finally {
                    isSubmitting = false
                }
            }
        }
    }

    fun startEditing(review: Review) {
        editingReviewId = review.id
        reviewContent = review.content
        if (review.photo != null) {
            simulatedPhotoName = review.photo
            capturedImageUri = null
        } else {
            simulatedPhotoName = null
            capturedImageUri = null
        }
    }

    fun cancelEditing() {
        editingReviewId = null
        reviewContent = ""
        simulatedPhotoName = null
        capturedImageUri = null
    }

    fun simulatePhoto() {
        simulatedPhotoName = "makan_siswa_${System.currentTimeMillis() / 1000}.jpg"
        capturedImageUri = null
    }

    fun clearPhoto() {
        simulatedPhotoName = null
        capturedImageUri = null
    }

    fun deleteReview(review: Review) {
        if (!review.userId.equals(currentUserEmail, ignoreCase = true)) {
            errorMessage = "Anda tidak memiliki akses untuk menghapus ulasan ini"
            return
        }
        if (editingReviewId == review.id) {
            cancelEditing()
        }
        MockData.reviews.remove(review)
        reviewsList.remove(review)
        successMessage = "Ulasan berhasil dihapus"
    }
}
