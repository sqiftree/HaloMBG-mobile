package com.halombg.mobile.model

import java.io.Serializable

data class School(
    val id: Long,
    val name: String,
    val address: String,
    val district: String,
    val province: String,
    var sppgId: Long? = null
) : Serializable

data class SppgProfile(
    val id: Long,
    val kitchenName: String,
    val address: String,
    val district: String,
    val province: String,
    val contactPersonName: String,
    val contactPhone: String,
    val contactEmail: String?,
    val description: String?,
    val productionCapacity: Int?,
    val isActive: Boolean = true
) : Serializable

data class DailyMenu(
    val id: Long,
    val sppgId: Long,
    val servedAt: String, // format YYYY-MM-DD
    val menuName: String,
    val components: String?, // comma separated elements
    val calories: Int?,
    val protein: Int?, // grams
    val carbs: Int?,   // grams
    val fat: Int?,     // grams
    val photo: String?, // base64 or drawable name
    val isAiValidated: Boolean = false,
    val aiWarning: String? = null
) : Serializable

data class DistributionStatus(
    val id: Long,
    val sppgId: Long,
    val schoolId: Long,
    val schoolName: String,
    val distributedAt: String, // format YYYY-MM-DD
    var status: String, // belum_diantar, siap_diantar, sudah_diantar, batal
    var statusUpdatedAt: String
) : Serializable

data class Review(
    val id: Long,
    val userId: String,
    val userName: String,
    val schoolId: Long,
    val schoolName: String,
    val reviewDate: String, // format YYYY-MM-DD
    val content: String,
    var flagStatus: String = "none", // none, flagged, deleted
    var flagReason: String? = null,
    val photo: String? = null // base64 or drawable name
) : Serializable
