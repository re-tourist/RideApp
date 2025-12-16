package com.example.rideflow.auth.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore by preferencesDataStore(name = "session_store")

class SessionStore(private val context: Context) {
    private object Keys {
        val USER_ID = stringPreferencesKey("user_id")
        val LOGIN_AT = longPreferencesKey("login_at")
        val EXPIRE_AT = longPreferencesKey("expire_at")
        val SCHEMA_VERSION = intPreferencesKey("schema_version")
    }

    val sessionFlow: Flow<SessionData?> = context.sessionDataStore.data.map { pref ->
        val userId = pref[Keys.USER_ID] ?: ""
        val loginAt = pref[Keys.LOGIN_AT] ?: 0L
        val expireAt = pref[Keys.EXPIRE_AT] ?: 0L
        val schema = pref[Keys.SCHEMA_VERSION] ?: 0
        if (userId.isEmpty() || expireAt <= 0L || schema != 1) null else SessionData(userId, loginAt, expireAt, schema)
    }

    suspend fun save(session: SessionData) {
        context.sessionDataStore.edit { pref ->
            pref[Keys.USER_ID] = session.userId
            pref[Keys.LOGIN_AT] = session.loginAt
            pref[Keys.EXPIRE_AT] = session.expireAt
            pref[Keys.SCHEMA_VERSION] = session.schemaVersion
        }
    }

    suspend fun clear() {
        context.sessionDataStore.edit { it.clear() }
    }
}

