package io.mangel.issuemanager.services

import android.content.SharedPreferences
import io.mangel.issuemanager.services.data.store.AuthenticationToken
import io.mangel.issuemanager.services.data.store.User

class SettingService(
    private val preferences: SharedPreferences,
    private val serializationService: SerializationService
) {
    fun saveUser(user: User) {
        save(SettingKey.User, user)
    }

    fun readUser(): User? {
        return read(SettingKey.User, User::class.java)
    }

    fun clearAuthenticationToken() {
        clear(SettingKey.AuthenticationToken)
    }

    fun saveAuthenticationToken(authenticationToken: AuthenticationToken) {
        save(SettingKey.AuthenticationToken, authenticationToken)
    }

    fun readAuthenticationToken(): AuthenticationToken? {
        return read(SettingKey.AuthenticationToken, AuthenticationToken::class.java)
    }

    private fun save(key: SettingKey, element: Any) {
        val json = serializationService.serialize(element)
        preferences.edit().putString(key.value, json).apply()
    }

    private fun clear(key: SettingKey) {
        preferences.edit().remove(key.value).apply()
    }

    private fun <T> read(key: SettingKey, classOfT: Class<T>): T? {
        val json = preferences.getString(key.value, null) ?: return null
        return serializationService.deserialize(json, classOfT)
    }

    private enum class SettingKey(val value: String) {
        AuthenticationToken("SettingService.AuthenticationToken"),
        User("SettingService.User")
    }
}