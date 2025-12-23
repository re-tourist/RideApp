package com.example.rideflow.backend

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar

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
        if (result > 0) {
            updateUserAchievementsForRide(userId)
        }
        return result > 0
    }

    fun updateRideRecordTrackImageUrl(
        recordId: Int,
        trackImageUrl: String
    ): Boolean {
        val sql = "UPDATE user_ride_records SET track_image_url = ? WHERE record_id = ?"
        val params = listOf(trackImageUrl, recordId)
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

    private fun updateUserAchievementsForRide(userId: Int) {
        if (userId <= 0) return
        var totalRides = 0
        var maxDistance = 0.0
        DatabaseHelper.processQuery(
            "SELECT COUNT(*), MAX(distance_km) FROM user_ride_records WHERE user_id = ?",
            listOf(userId)
        ) { rs ->
            if (rs.next()) {
                totalRides = rs.getInt(1)
                maxDistance = rs.getDouble(2)
            }
            Unit
        }
        var nightRides = 0
        DatabaseHelper.processQuery(
            "SELECT COUNT(*) FROM user_ride_records WHERE user_id = ? AND TIME(start_time) >= '22:00:00'",
            listOf(userId)
        ) { rs ->
            if (rs.next()) {
                nightRides = rs.getInt(1)
            }
            Unit
        }
        val rideDates = mutableListOf<Long>()
        DatabaseHelper.processQuery(
            "SELECT DISTINCT DATE(start_time) AS ride_date FROM user_ride_records WHERE user_id = ? ORDER BY ride_date DESC",
            listOf(userId)
        ) { rs ->
            while (rs.next()) {
                val d = rs.getDate(1)
                if (d != null) {
                    rideDates.add(d.time)
                }
            }
            Unit
        }
        val currentStreakDays = calculateCurrentStreakDays(rideDates)
        var monthlyRideCount = 0
        run {
            val cal = Calendar.getInstance()
            cal.set(Calendar.DAY_OF_MONTH, 1)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val startStr = sdf.format(cal.time)
            cal.add(Calendar.MONTH, 1)
            val endStr = sdf.format(cal.time)
            DatabaseHelper.processQuery(
                "SELECT COUNT(*) FROM user_ride_records WHERE user_id = ? AND start_time >= ? AND start_time < ?",
                listOf(userId, startStr, endStr)
            ) { rs ->
                if (rs.next()) {
                    monthlyRideCount = rs.getInt(1)
                }
                Unit
            }
        }
        val badges = mutableListOf<BadgeRule>()
        DatabaseHelper.processQuery(
            "SELECT badge_id, rule_type, target_count, target_distance_km, target_days FROM achievement_badges WHERE active = 1",
            emptyList()
        ) { rs ->
            while (rs.next()) {
                val id = rs.getInt(1)
                val ruleType = rs.getString(2) ?: ""
                val targetCount = rs.getInt(3)
                val targetDistance = rs.getDouble(4)
                val targetDays = rs.getInt(5)
                badges.add(
                    BadgeRule(
                        id = id,
                        ruleType = ruleType,
                        targetCount = targetCount,
                        targetDistanceKm = targetDistance,
                        targetDays = targetDays
                    )
                )
            }
            Unit
        }
        badges.forEach { rule ->
            var currentCount = 0
            var currentDistanceKm = 0.0
            var streakDaysValue = 0
            var progressPercent = 0.0
            var unlocked = false
            when (rule.ruleType) {
                "first_ride" -> {
                    val target = if (rule.targetCount > 0) rule.targetCount else 1
                    currentCount = totalRides
                    unlocked = totalRides >= target && target > 0
                    progressPercent = if (target > 0) totalRides * 100.0 / target else 0.0
                }
                "total_rides" -> {
                    val target = if (rule.targetCount > 0) rule.targetCount else 1
                    currentCount = totalRides
                    unlocked = totalRides >= target && target > 0
                    progressPercent = if (target > 0) totalRides * 100.0 / target else 0.0
                }
                "single_distance" -> {
                    val target = if (rule.targetDistanceKm > 0.0) rule.targetDistanceKm else 1.0
                    currentDistanceKm = maxDistance
                    unlocked = maxDistance >= target && target > 0.0
                    progressPercent = if (target > 0.0) maxDistance * 100.0 / target else 0.0
                }
                "night_rides" -> {
                    val target = if (rule.targetCount > 0) rule.targetCount else 1
                    currentCount = nightRides
                    unlocked = nightRides >= target && target > 0
                    progressPercent = if (target > 0) nightRides * 100.0 / target else 0.0
                }
                "streak_days" -> {
                    val target = if (rule.targetDays > 0) rule.targetDays else 1
                    streakDaysValue = currentStreakDays
                    unlocked = currentStreakDays >= target && target > 0
                    progressPercent = if (target > 0) currentStreakDays * 100.0 / target else 0.0
                }
                "monthly_rides" -> {
                    val target = if (rule.targetCount > 0) rule.targetCount else 1
                    currentCount = monthlyRideCount
                    unlocked = monthlyRideCount >= target && target > 0
                    progressPercent = if (target > 0) monthlyRideCount * 100.0 / target else 0.0
                }
            }
            if (progressPercent.isNaN() || progressPercent.isInfinite()) {
                progressPercent = 0.0
            }
            progressPercent = progressPercent.coerceIn(0.0, 100.0)
            DatabaseHelper.executeUpdate(
                "INSERT INTO user_achievement_progress (user_id, badge_id, current_count, current_distance_km, current_streak_days, progress_percent, is_unlocked) VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE current_count = VALUES(current_count), current_distance_km = VALUES(current_distance_km), current_streak_days = VALUES(current_streak_days), progress_percent = VALUES(progress_percent), is_unlocked = VALUES(is_unlocked)",
                listOf(
                    userId,
                    rule.id,
                    currentCount,
                    currentDistanceKm,
                    streakDaysValue,
                    progressPercent,
                    if (unlocked) 1 else 0
                )
            )
        }
    }

    private fun calculateCurrentStreakDays(dates: List<Long>): Int {
        if (dates.isEmpty()) return 0
        val oneDayMillis = 24L * 60L * 60L * 1000L
        var streak = 1
        var previous = dates[0]
        for (i in 1 until dates.size) {
            val current = dates[i]
            val diff = previous - current
            if (diff == 0L) {
                continue
            }
            if (diff != oneDayMillis) {
                break
            }
            streak++
            previous = current
        }
        return streak
    }

    private data class BadgeRule(
        val id: Int,
        val ruleType: String,
        val targetCount: Int,
        val targetDistanceKm: Double,
        val targetDays: Int
    )
}
