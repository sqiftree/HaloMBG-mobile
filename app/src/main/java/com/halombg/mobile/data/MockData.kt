package com.halombg.mobile.data

import com.halombg.mobile.model.*
import java.text.SimpleDateFormat
import java.util.*

object MockData {
    val schools = mutableListOf<School>()
    val sppgProfiles = mutableListOf<SppgProfile>()
    val dailyMenus = mutableListOf<DailyMenu>()
    val distributionStatuses = mutableListOf<DistributionStatus>()
    val reviews = mutableListOf<Review>()

    init {
        val today = getTodayDateString()
        val yesterday = getYesterdayDateString()

        // 1. MOCK SPPG
        sppgProfiles.add(
            SppgProfile(
                id = 1L,
                kitchenName = "Dapur Sehat Kartasura",
                address = "Jl. Ahmad Yani No. 50, Kartasura",
                district = "Sukoharjo",
                province = "Jawa Tengah",
                contactPersonName = "Budi Santoso",
                contactPhone = "08123456789",
                contactEmail = "dapursehat@halombg.go.id",
                description = "Melayani sekolah-sekolah di wilayah Surakarta barat.",
                productionCapacity = 1500
            )
        )
        sppgProfiles.add(
            SppgProfile(
                id = 2L,
                kitchenName = "Dapur Gizi Solo Pusat",
                address = "Jl. Adi Sucipto No. 120, Solo",
                district = "Surakarta",
                province = "Jawa Tengah",
                contactPersonName = "Siti Rahma",
                contactPhone = "08987654321",
                contactEmail = "gizisolopusat@halombg.go.id",
                description = "Penyedia makanan utama wilayah Solo Tengah.",
                productionCapacity = 3000
            )
        )

        // 2. MOCK SCHOOLS
        schools.add(School(1L, "SD Negeri 1 Jaya", "Jl. Merdeka No. 12", "Surakarta", "Jawa Tengah", 1L))
        schools.add(School(2L, "SMP Negeri 2", "Jl. Pemuda No. 45", "Surakarta", "Jawa Tengah", 1L))
        schools.add(School(3L, "SMA Negeri 3", "Jl. Slamet Riyadi No. 99", "Surakarta", "Jawa Tengah", 2L))
        schools.add(School(4L, "SD Negeri 4 Sukamaju", "Jl. Pahlawan No. 7", "Karanganyar", "Jawa Tengah", null))

        // 3. MOCK DAILY MENUS
        dailyMenus.add(
            DailyMenu(
                id = 1L,
                sppgId = 1L,
                servedAt = today,
                menuName = "Nasi & Ayam Panggang Madu",
                components = "Nasi Putih, Fillet Ayam Panggang, Cah Wortel & Buncis, Susu Kotak UHT, Potongan Melon",
                calories = 640,
                protein = 25,
                carbs = 82,
                fat = 14,
                photo = null,
                isAiValidated = true
            )
        )
        dailyMenus.add(
            DailyMenu(
                id = 2L,
                sppgId = 1L,
                servedAt = yesterday,
                menuName = "Nasi Merah & Ikan Kembung",
                components = "Nasi Merah, Ikan Kembung Goreng, Sayur Sop Bening, Susu Kotak UHT, Buah Pisang",
                calories = 590,
                protein = 22,
                carbs = 75,
                fat = 12,
                photo = null,
                isAiValidated = true
            )
        )
        dailyMenus.add(
            DailyMenu(
                id = 3L,
                sppgId = 2L,
                servedAt = today,
                menuName = "Nasi & Tumis Daging Sapi",
                components = "Nasi Putih, Tumis Daging Sapi Lada Hitam, Sayur Bayam, Susu Kotak UHT, Irisan Pepaya",
                calories = 670,
                protein = 28,
                carbs = 85,
                fat = 15,
                photo = null,
                isAiValidated = true
            )
        )

        // 4. MOCK DISTRIBUTION STATUS
        distributionStatuses.add(
            DistributionStatus(1L, 1L, 1L, "SD Negeri 1 Jaya", today, "sudah_diantar", "Hari ini, 10:30")
        )
        distributionStatuses.add(
            DistributionStatus(2L, 1L, 2L, "SMP Negeri 2", today, "siap_diantar", "Hari ini, 09:15")
        )
        distributionStatuses.add(
            DistributionStatus(3L, 2L, 3L, "SMA Negeri 3", today, "belum_diantar", "Hari ini, 07:00")
        )

        // 5. MOCK REVIEWS
        reviews.add(
            Review(
                id = 1L,
                userId = "ahmad.dani@halombg.go.id",
                userName = "Ahmad Dani",
                schoolId = 1L,
                schoolName = "SD Negeri 1 Jaya",
                reviewDate = today,
                content = "Makanannya enak sekali! Ayam panggangnya empuk, sayur buncis manis, dan susunya dingin. Teman-teman sekelas juga suka.",
                flagStatus = "none"
            )
        )
        reviews.add(
            Review(
                id = 2L,
                userId = "siswa_2",
                userName = "Siti Amelia",
                schoolId = 1L,
                schoolName = "SD Negeri 1 Jaya",
                reviewDate = yesterday,
                content = "Sop beningnya kurang asin sedikit, tapi ikannya gurih dan pisangnya manis banget. Terima kasih dapur sehat!",
                flagStatus = "none"
            )
        )
    }

    private fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun getYesterdayDateString(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
    }

    fun searchSchools(query: String): List<School> {
        if (query.length < 2) return emptyList()
        return schools.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.district.contains(query, ignoreCase = true) ||
            it.province.contains(query, ignoreCase = true)
        }
    }

    fun getSppgProfile(id: Long): SppgProfile? {
        return sppgProfiles.find { it.id == id }
    }

    fun getDailyMenuForSppg(sppgId: Long, date: String): DailyMenu? {
        return dailyMenus.find { it.sppgId == sppgId && it.servedAt == date }
    }

    fun getReviewsForSppg(sppgId: Long): List<Review> {
        val mappedSchoolIds = schools.filter { it.sppgId == sppgId }.map { it.id }
        return reviews.filter { it.schoolId in mappedSchoolIds && it.flagStatus != "deleted" }
    }

    fun getReviewsForSchool(schoolId: Long): List<Review> {
        return reviews.filter { it.schoolId == schoolId && it.flagStatus != "deleted" }
    }

    fun addReview(review: Review) {
        reviews.add(0, review) // insert at top
    }

    fun updateReviewFlagStatus(reviewId: Long, status: String) {
        val index = reviews.indexOfFirst { it.id == reviewId }
        if (index != -1) {
            reviews[index] = reviews[index].copy(flagStatus = status)
        }
    }

    fun addDailyMenu(menu: DailyMenu) {
        // Remove existing menu for the same sppg and date if exists
        dailyMenus.removeAll { it.sppgId == menu.sppgId && it.servedAt == menu.servedAt }
        dailyMenus.add(0, menu)
    }

    fun getDistributionStatusesForSppg(sppgId: Long): List<DistributionStatus> {
        return distributionStatuses.filter { it.sppgId == sppgId }
    }

    fun updateDistributionStatus(sppgId: Long, schoolId: Long, status: String) {
        val index = distributionStatuses.indexOfFirst { it.sppgId == sppgId && it.schoolId == schoolId }
        val timeNow = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        if (index != -1) {
            val dist = distributionStatuses[index]
            distributionStatuses[index] = dist.copy(
                status = status,
                statusUpdatedAt = "Hari ini, $timeNow"
            )
        } else {
            val school = schools.find { it.id == schoolId }
            val newId = (distributionStatuses.maxOfOrNull { it.id } ?: 0L) + 1
            distributionStatuses.add(
                DistributionStatus(
                    id = newId,
                    sppgId = sppgId,
                    schoolId = schoolId,
                    schoolName = school?.name ?: "Sekolah",
                    distributedAt = getTodayDateString(),
                    status = status,
                    statusUpdatedAt = "Hari ini, $timeNow"
                )
            )
        }
    }
}
