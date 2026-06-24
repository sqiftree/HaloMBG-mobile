package com.halombg.mobile.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*

data class LoginRequest(
    val email: String,
    val password: String
)

data class GoogleLoginRequest(
    @SerializedName("id_token") val idToken: String
)

data class LoginResponse(
    val status: String,
    val message: String,
    val token: String?,
    val user: UserDto?
)

data class UserDto(
    val id: Long,
    val name: String,
    val email: String,
    val role: String,
    @SerializedName("nisn") val nisn: String?,
    @SerializedName("nuptk") val nuptk: String?,
    @SerializedName("school_id") val schoolId: Long?,
    val school: SchoolDto?
)

data class SchoolDto(
    val id: Long,
    val name: String,
    val address: String?,
    val district: String,
    val province: String,
    @SerializedName("sppg_profiles") val sppgProfiles: List<ProfileDto>?
)

data class ProfileDto(
    val id: Long? = null,
    @SerializedName("kitchen_name") val kitchenName: String? = null,
    val address: String? = null,
    val district: String? = null,
    val province: String? = null,
    @SerializedName("contact_person_name") val contactPersonName: String? = null,
    @SerializedName("contact_phone") val contactPhone: String? = null,
    @SerializedName("contact_email") val contactEmail: String? = null,
    val description: String? = null,
    @SerializedName("production_capacity") val productionCapacity: Int? = null,
    
    // Custom properties from /siswa/sppg-info
    val served: Boolean? = null,
    @SerializedName("contact_person") val contactPerson: String? = null
)

data class UpdateSppgProfileRequest(
    val address: String,
    @SerializedName("contact_person_name") val contactPersonName: String,
    @SerializedName("contact_phone") val contactPhone: String,
    @SerializedName("contact_email") val contactEmail: String?,
    val description: String?
)

data class ReviewRequest(
    val content: String,
    @SerializedName("review_date") val reviewDate: String,
    val photo: String? = null // Base64 image
)

data class ReviewResponse(
    val status: String,
    val message: String,
    val review: ReviewDto?
)

data class ReviewDto(
    val id: Long,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("school_id") val schoolId: Long,
    val content: String,
    @SerializedName("review_date") val reviewDate: String,
    val photo: String?
)

data class DistributionStatusDto(
    val id: Long,
    @SerializedName("sppg_id") val sppgId: Long,
    @SerializedName("school_id") val schoolId: Long,
    @SerializedName("school_name") val schoolName: String,
    @SerializedName("distributed_at") val distributedAt: String,
    val status: String,
    @SerializedName("status_updated_at") val statusUpdatedAt: String,
    val photo: String?
)

data class UpdateDistributionRequest(
    val status: String,
    val photo: String? = null // Base64 image
)

data class FlagReviewResponse(
    val status: String,
    val message: String,
    val review: ReviewDto?
)

data class FollowUpRequest(
    val status: String // belum_diproses, dalam_proses, selesai
)

data class FollowUpResponse(
    val status: String,
    val message: String,
    val review: ReviewDto?
)

interface ApiService {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("auth/google")
    suspend fun googleLogin(
        @Body request: GoogleLoginRequest
    ): Response<LoginResponse>

    @GET("user")
    suspend fun getUserProfile(): Response<LoginResponse>

    @GET("sppg/profile")
    suspend fun getSppgProfile(): Response<ProfileDto>

    @PUT("sppg/profile")
    suspend fun updateSppgProfile(
        @Body request: UpdateSppgProfileRequest
    ): Response<ProfileDto>

    @GET("siswa/sppg-info")
    suspend fun getSiswaSppgInfo(): Response<ProfileDto>

    @GET("siswa/reviews")
    suspend fun getSiswaReviews(): Response<List<ReviewDto>>

    @POST("siswa/reviews")
    suspend fun postSiswaReview(
        @Body request: ReviewRequest
    ): Response<ReviewResponse>

    @GET("sppg/distribution")
    suspend fun getSppgDistributions(
        @Query("date") date: String
    ): Response<List<DistributionStatusDto>>

    @PUT("sppg/distribution/{id}")
    suspend fun updateDistributionStatus(
        @Path("id") id: Long,
        @Body request: UpdateDistributionRequest
    ): Response<DistributionStatusDto>

    @GET("guru/reviews")
    suspend fun getGuruReviews(): Response<List<ReviewDto>>

    @POST("guru/reviews/{id}/flag")
    suspend fun flagReview(
        @Path("id") id: Long
    ): Response<FlagReviewResponse>

    @POST("sppg/reviews/{id}/follow-up")
    suspend fun updateFollowUpStatus(
        @Path("id") id: Long,
        @Body request: FollowUpRequest
    ): Response<FollowUpResponse>
}
