package com.example.rideflow.cache

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.appCacheDataStore by preferencesDataStore(name = "app_cache")

object AppCache {
    private const val SCHEMA_VERSION = 1

    fun put(context: Context, key: String, json: String, ttlMs: Long) = runBlocking {
        val expireAt = System.currentTimeMillis() + ttlMs
        context.appCacheDataStore.edit { prefs ->
            prefs[stringPreferencesKey("${key}_data")] = json
            prefs[longPreferencesKey("${key}_expire_at")] = expireAt
            prefs[intPreferencesKey("${key}_schema")] = SCHEMA_VERSION
        }
    }

    fun get(context: Context, key: String): String? = runBlocking {
        val prefs = context.appCacheDataStore.data.first()
        val schema = prefs[intPreferencesKey("${key}_schema")] ?: 0
        val expireAt = prefs[longPreferencesKey("${key}_expire_at")] ?: 0L
        val data = prefs[stringPreferencesKey("${key}_data")]
        if (schema != SCHEMA_VERSION) null else if (expireAt > System.currentTimeMillis()) data else null
    }

    fun clear(context: Context, key: String) = runBlocking {
        context.appCacheDataStore.edit { prefs ->
            prefs.remove(stringPreferencesKey("${key}_data"))
            prefs.remove(longPreferencesKey("${key}_expire_at"))
            prefs.remove(intPreferencesKey("${key}_schema"))
        }
    }
}

