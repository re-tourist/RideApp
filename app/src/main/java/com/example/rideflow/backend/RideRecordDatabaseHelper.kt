package com.example.rideflow.backend

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object RideRecordDatabaseHelper {
    data class UserRideRecord(
        val startTime: String,
        val durationSec: Int,
        val avgSpeedKmh: Double,
        val distanceKm: Double,
        val calories: Int
    )
    fun generateRecordId(userId: Int): Int {
        val base = System.currentTimeMillis().toInt()
        return kotlin.math.abs(base xor userId)
    }

    fun insertUserRideRecord(
        recordId: Int,
        userId: Int,
        startTimeMillis: Long,
        durationSec: Int,
        distanceKm: Double,
        avgSpeedKmh: Double,
        calories: Int,
        climb: Int,
        maxSpeedKmh: Double
    ): Boolean {
        val startTimeStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(startTimeMillis))
        val sql = "INSERT INTO user_ride_records (record_id, user_id, start_time, duration_seconds, duration_sec, distance_km, avg_speed_kmh, calories, created_at, climb, max_speed_kmh) VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, ?)"
        val params = listOf(
            recordId,
            userId,
            startTimeStr,
            0,
            durationSec,
            String.format(Locale.US, "%.2f", distanceKm).toDouble(),
            String.format(Locale.US, "%.2f", avgSpeedKmh).toDouble(),
            calories,
            climb,
            String.format(Locale.US, "%.2f", maxSpeedKmh).toDouble()
        )
        val result = DatabaseHelper.executeUpdate(sql, params)
        return result > 0
    }

    fun getUserRideRecords(userId: Int): List<UserRideRecord> {
        val sql = "SELECT start_time, duration_sec, avg_speed_kmh, distance_km, calories FROM user_ride_records WHERE user_id = ? ORDER BY start_time DESC"
        val rows = DatabaseHelper.queryMultipleRows(sql, listOf(userId))
        return rows.map { row ->
            val start = row["start_time"]?.toString() ?: ""
            val duration = row["duration_sec"]?.toString()?.toIntOrNull() ?: 0
            val avg = row["avg_speed_kmh"]?.toString()?.toDoubleOrNull() ?: 0.0
            val dist = row["distance_km"]?.toString()?.toDoubleOrNull() ?: 0.0
            val cal = row["calories"]?.toString()?.toIntOrNull() ?: 0
            UserRideRecord(start, duration, avg, dist, cal)
        }
    }
}
