package com.halombg.mobile.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class AuthRepository(context: Context) {

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    
    private val sharedPreferences: SharedPreferences = try {
        EncryptedSharedPreferences.create(
            "secure_auth_prefs",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        // Fallback to standard SharedPreferences if encryption fails (e.g. key store issues)
        context.getSharedPreferences("auth_prefs_fallback", Context.MODE_PRIVATE)
    }

    fun saveToken(token: String) {
        sharedPreferences.edit().putString("auth_token", token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }

    fun clearToken() {
        sharedPreferences.edit().remove("auth_token").apply()
    }

    fun saveUserRole(role: String) {
        sharedPreferences.edit().putString("user_role", role).apply()
    }

    fun getUserRole(): String? {
        return sharedPreferences.getString("user_role", null)
    }

    fun clearUserRole() {
        sharedPreferences.edit().remove("user_role").apply()
    }

    fun saveUserEmail(email: String) {
        sharedPreferences.edit().putString("user_email", email).apply()
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString("user_email", null)
    }

    fun clearUserEmail() {
        sharedPreferences.edit().remove("user_email").apply()
    }

    fun saveUserName(name: String) {
        sharedPreferences.edit().putString("user_name", name).apply()
    }

    fun getUserName(): String? {
        return sharedPreferences.getString("user_name", null)
    }

    fun clearUserName() {
        sharedPreferences.edit().remove("user_name").apply()
    }

    fun saveSchoolId(id: Long) {
        sharedPreferences.edit().putLong("school_id", id).apply()
    }

    fun getSchoolId(): Long {
        return sharedPreferences.getLong("school_id", -1L)
    }

    fun clearSchoolId() {
        sharedPreferences.edit().remove("school_id").apply()
    }

    fun saveSimulationMode(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("simulation_mode", enabled).apply()
    }

    fun isSimulationMode(): Boolean {
        return sharedPreferences.getBoolean("simulation_mode", false)
    }

    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null || (isSimulationMode() && getUserRole() != null)
    }
}
