package com.example.rideflow.auth.session

class SessionManager(private val store: SessionStore) {
    fun sessionFlow() = store.sessionFlow
    suspend fun loginSuccess(userId: String, ttlDays: Int = 30) {
        val now = System.currentTimeMillis()
        val expireAt = now + ttlDays * 24L * 60 * 60 * 1000
        store.save(SessionData(userId, now, expireAt))
    }
    suspend fun logout() { store.clear() }
}

