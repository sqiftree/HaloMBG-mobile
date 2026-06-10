package com.halombg.mobile.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*

data class LoginRequest(
    val email: String,
    val password: String
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

interface ApiService {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
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
}
