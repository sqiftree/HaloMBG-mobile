package com.halombg.mobile

import com.halombg.mobile.data.MockData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class MockDataTest {

    @Test
    fun testSearchSchools() {
        val results = MockData.searchSchools("SD Negeri 1")
        assertEquals(1, results.size)
        assertEquals("SD Negeri 1 Jaya", results[0].name)
    }

    @Test
    fun testGetSppgProfile() {
        val profile = MockData.getSppgProfile(1L)
        assertNotNull(profile)
        assertEquals("Dapur Sehat Kartasura", profile?.kitchenName)
    }
}
