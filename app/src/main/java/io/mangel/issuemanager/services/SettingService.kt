package io.mangel.issuemanager.services

import android.content.SharedPreferences
import io.mangel.issuemanager.store.AuthenticationToken
import io.mangel.issuemanager.store.User

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

    private fun <T> read(key: SettingKey, classOfT: Class<T>): T? {
        val json = preferences.getString(key.value, null) ?: return null
        return serializationService.deserialize(json, classOfT)
    }

    private enum class SettingKey(val value: String) {
        AuthenticationToken("SettingService.AuthenticationToken"),
        User("SettingService.User")
    }
}