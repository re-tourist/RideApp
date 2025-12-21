package com.example.rideflow.backend

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

interface DbResultSet {
    fun next(): Boolean
    fun wasNull(): Boolean

    fun getString(columnIndex: Int): String?
    fun getInt(columnIndex: Int): Int
    fun getDouble(columnIndex: Int): Double
    fun getLong(columnIndex: Int): Long
    fun getBoolean(columnIndex: Int): Boolean
    fun getDate(columnIndex: Int): java.sql.Date?
    fun getTimestamp(columnIndex: Int): java.sql.Timestamp?

    fun getBigDecimal(columnIndex: Int): java.math.BigDecimal?

    fun getString(columnLabel: String): String?
    fun getInt(columnLabel: String): Int
    fun getDouble(columnLabel: String): Double
    fun getLong(columnLabel: String): Long
    fun getBoolean(columnLabel: String): Boolean
    fun getTimestamp(columnLabel: String): java.sql.Timestamp?
    fun getBigDecimal(columnLabel: String): java.math.BigDecimal?
    fun getObject(columnLabel: String): Any?
    fun getObject(columnIndex: Int): Any?
}

object DatabaseHelper {
    private const val TAG = "DatabaseHelper"
    private const val BASE_URL = "http://101.37.79.220:8080"

    fun executeQuery(sql: String, params: List<Any> = emptyList()): DbResultSet? {
        Log.e(TAG, "executeQuery is not supported in HTTP mode")
        return null
    }

    fun <T> processQuery(
        sql: String,
        params: List<Any> = emptyList(),
        processor: (DbResultSet) -> T
    ): T? {
        Log.d(TAG, "processQuery http sql=$sql params=$params")
        val rows = queryMultipleRows(sql, params)
        val resultSet = InMemoryResultSet(rows)
        return processor(resultSet)
    }

    fun executeUpdate(sql: String, params: List<Any> = emptyList()): Int {
        Log.d(TAG, "executeUpdate http sql=$sql params=$params")
        val body = JSONObject()
            .put("sql", sql)
            .put("params", JSONArray(params))
        val root = postJson("/api/v1/db/update", body)
        val code = root.optInt("code", -1)
        if (code != 0) {
            val msg = root.optString("message", "update failed")
            Log.e(TAG, "executeUpdate error code=$code message=$msg")
            return -1
        }
        return root.optInt("data", -1)
    }

    fun insertAndReturnId(sql: String, params: List<Any> = emptyList()): Int? {
        Log.d(TAG, "insertAndReturnId http sql=$sql params=$params")
        val body = JSONObject()
            .put("sql", sql)
            .put("params", JSONArray(params))
        val root = postJson("/api/v1/db/insert", body)
        val code = root.optInt("code", -1)
        if (code != 0) {
            val msg = root.optString("message", "insert failed")
            Log.e(TAG, "insertAndReturnId error code=$code message=$msg")
            return null
        }
        val id = root.optLong("data", -1L)
        return if (id > 0L) id.toInt() else null
    }

    fun executeBatchUpdate(sql: String, paramsList: List<List<Any>>): IntArray {
        Log.d(TAG, "executeBatchUpdate http sql=$sql size=${paramsList.size}")
        val jsonParamsList = JSONArray()
        paramsList.forEach { row ->
            jsonParamsList.put(JSONArray(row))
        }
        val body = JSONObject()
            .put("sql", sql)
            .put("paramsList", jsonParamsList)
        val root = postJson("/api/v1/db/batch-update", body)
        val code = root.optInt("code", -1)
        if (code != 0) {
            val msg = root.optString("message", "batch update failed")
            Log.e(TAG, "executeBatchUpdate error code=$code message=$msg")
            return intArrayOf()
        }
        val data = root.optJSONArray("data") ?: return intArrayOf()
        val result = IntArray(data.length())
        for (i in 0 until data.length()) {
            result[i] = data.optInt(i, 0)
        }
        return result
    }

    fun querySingleRow(sql: String, params: List<Any> = emptyList()): Map<String, Any>? {
        val list = queryMultipleRows(sql, params)
        return list.firstOrNull()
    }

    fun queryMultipleRows(sql: String, params: List<Any> = emptyList()): List<Map<String, Any>> {
        Log.d(TAG, "queryMultipleRows http sql=$sql params=$params")
        val body = JSONObject()
            .put("sql", sql)
            .put("params", JSONArray(params))
        val root = postJson("/api/v1/db/query", body)
        val code = root.optInt("code", -1)
        if (code != 0) {
            val msg = root.optString("message", "query failed")
            Log.e(TAG, "queryMultipleRows error code=$code message=$msg")
            return emptyList()
        }
        val data = root.optJSONArray("data") ?: return emptyList()
        val result = mutableListOf<Map<String, Any>>()
        for (i in 0 until data.length()) {
            val rowJson = data.optJSONObject(i) ?: continue
            val row = mutableMapOf<String, Any>()
            val names = rowJson.keys()
            while (names.hasNext()) {
                val key = names.next()
                val value = rowJson.opt(key)
                row[key] = value ?: ""
            }
            result.add(row)
        }
        return result
    }

    fun querySingleValue(sql: String, params: List<Any> = emptyList()): Any? {
        val row = querySingleRow(sql, params) ?: return null
        return row.values.firstOrNull()
    }

    fun tableExists(tableName: String): Boolean {
        val sql = "SELECT COUNT(*) AS cnt FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?"
        val value = querySingleValue(sql, listOf(tableName))
        return when (value) {
            is Number -> value.toInt() > 0
            is String -> value.toIntOrNull()?.let { it > 0 } == true
            else -> false
        }
    }

    fun getTableNames(): List<String> {
        val sql = "SELECT table_name FROM information_schema.tables WHERE table_schema = DATABASE()"
        val rows = queryMultipleRows(sql)
        return rows.mapNotNull { it["table_name"]?.toString() }
    }

    fun getTableStructure(tableName: String): List<Map<String, Any>> {
        val sql = "SELECT column_name, data_type, is_nullable, column_default, column_key, extra FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = ? ORDER BY ordinal_position"
        return queryMultipleRows(sql, listOf(tableName))
    }

    fun testConnection(): Boolean {
        return try {
            val sql = "SELECT 1 AS v"
            val v = querySingleValue(sql)
            v != null
        } catch (e: Exception) {
            Log.e(TAG, "testConnection failed: ${e.message}")
            false
        }
    }

    fun createUsersTable(): Boolean {
        val sql = """
                CREATE TABLE IF NOT EXISTS users (
                    user_id INT PRIMARY KEY AUTO_INCREMENT,
                    nickname VARCHAR(50) NOT NULL UNIQUE,
                    email VARCHAR(100) NOT NULL UNIQUE,
                    password_hash VARCHAR(255) NOT NULL,
                    avatar_url VARCHAR(255),
                    bio TEXT,
                    gender ENUM('male', 'female', 'other') DEFAULT 'other',
                    birthday DATE,
                    emergency_contact VARCHAR(100),
                    status TINYINT DEFAULT 0 COMMENT '0-正常 1-禁用 2-未激活',
                    email_verified BOOLEAN DEFAULT FALSE,
                    last_login_at TIMESTAMP NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
            """.trimIndent()
        val result = executeUpdate(sql)
        return result >= 0
    }

    fun dropUsersTable(): Boolean {
        val sql = "DROP TABLE IF EXISTS users"
        val result = executeUpdate(sql)
        return result >= 0
    }

    private fun postJson(path: String, body: JSONObject): JSONObject {
        val url = URL("$BASE_URL$path")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 15000
            readTimeout = 30000
            doInput = true
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
        }
        try {
            val bytes = body.toString().toByteArray(Charsets.UTF_8)
            Log.d(TAG, "HTTP POST $path body=$body")
            conn.outputStream.use { os ->
                os.write(bytes)
            }
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val text = stream.bufferedReader().use { it.readText() }
            Log.d(TAG, "HTTP $path responseCode=$code body=$text")
            if (code !in 200..299) {
                throw RuntimeException("http $path failed code=$code")
            }
            return JSONObject(text)
        } finally {
            conn.disconnect()
        }
    }

    private class InMemoryResultSet(private val rows: List<Map<String, Any>>) : DbResultSet {
        private val columnNames: List<String> =
            if (rows.isNotEmpty()) rows[0].keys.toList() else emptyList()

        private var index = -1
        private var lastWasNull = false

        private fun current(): Map<String, Any> {
            if (index < 0 || index >= rows.size) throw java.sql.SQLException("Invalid cursor position")
            return rows[index]
        }

        private fun columnName(columnIndex: Int): String {
            if (columnIndex < 1 || columnIndex > columnNames.size) {
                throw java.sql.SQLException("Invalid column index $columnIndex")
            }
            return columnNames[columnIndex - 1]
        }

        override fun next(): Boolean {
            if (index + 1 >= rows.size) {
                index = rows.size
                lastWasNull = false
                return false
            }
            index++
            lastWasNull = false
            return true
        }

        override fun wasNull(): Boolean = lastWasNull

        override fun getObject(columnLabel: String): Any? {
            val value = current()[columnLabel]
            lastWasNull = value == null
            return value
        }

        override fun getObject(columnIndex: Int): Any? {
            return getObject(columnName(columnIndex))
        }

        override fun getString(columnLabel: String): String? {
            val v = getObject(columnLabel)
            return v?.toString()
        }

        override fun getString(columnIndex: Int): String? {
            return getString(columnName(columnIndex))
        }

        override fun getInt(columnLabel: String): Int {
            val v = getObject(columnLabel)
            return when (v) {
                null -> 0
                is Number -> v.toInt()
                is String -> v.toIntOrNull() ?: 0
                else -> 0
            }
        }

        override fun getInt(columnIndex: Int): Int {
            return getInt(columnName(columnIndex))
        }

        override fun getDouble(columnLabel: String): Double {
            val v = getObject(columnLabel)
            return when (v) {
                null -> 0.0
                is Number -> v.toDouble()
                is String -> v.toDoubleOrNull() ?: 0.0
                else -> 0.0
            }
        }

        override fun getDouble(columnIndex: Int): Double {
            return getDouble(columnName(columnIndex))
        }

        override fun getLong(columnLabel: String): Long {
            val v = getObject(columnLabel)
            return when (v) {
                null -> 0L
                is Number -> v.toLong()
                is String -> v.toLongOrNull() ?: 0L
                else -> 0L
            }
        }

        override fun getLong(columnIndex: Int): Long {
            return getLong(columnName(columnIndex))
        }

        override fun getBoolean(columnLabel: String): Boolean {
            val v = getObject(columnLabel)
            return when (v) {
                null -> false
                is Boolean -> v
                is Number -> v.toInt() != 0
                is String -> v == "1" || v.equals("true", true)
                else -> false
            }
        }

        override fun getBoolean(columnIndex: Int): Boolean {
            return getBoolean(columnName(columnIndex))
        }

        override fun getBigDecimal(columnIndex: Int): java.math.BigDecimal? {
            return getBigDecimal(columnName(columnIndex))
        }

        override fun getBigDecimal(columnLabel: String): java.math.BigDecimal? {
            val v = getObject(columnLabel)
            return when (v) {
                null -> {
                    lastWasNull = true
                    null
                }
                is java.math.BigDecimal -> v
                is Number -> {
                    try {
                        java.math.BigDecimal(v.toString())
                    } catch (e: Exception) {
                        lastWasNull = true
                        null
                    }
                }
                is String -> {
                    if (v.isEmpty()) {
                        lastWasNull = true
                        null
                    } else {
                        try {
                            java.math.BigDecimal(v)
                        } catch (e: Exception) {
                            lastWasNull = true
                            null
                        }
                    }
                }
                else -> {
                    lastWasNull = true
                    null
                }
            }
        }

        override fun getTimestamp(columnLabel: String): java.sql.Timestamp? {
            val v = getObject(columnLabel)
            return when (v) {
                null -> {
                    lastWasNull = true
                    null
                }
                is java.sql.Timestamp -> v
                is java.util.Date -> java.sql.Timestamp(v.time)
                is Number -> java.sql.Timestamp(v.toLong())
                is String -> {
                    if (v.isEmpty()) {
                        lastWasNull = true
                        null
                    } else {
                        try {
                            val s = v.replace('T', ' ')
                            java.sql.Timestamp.valueOf(if (s.length >= 19) s.substring(0, 19) else s)
                        } catch (e: Exception) {
                            lastWasNull = true
                            null
                        }
                    }
                }
                else -> {
                    lastWasNull = true
                    null
                }
            }
        }

        override fun getTimestamp(columnIndex: Int): java.sql.Timestamp? {
            return getTimestamp(columnName(columnIndex))
        }

        override fun getDate(columnIndex: Int): java.sql.Date? {
            val v = getObject(columnIndex)
            return when (v) {
                null -> {
                    lastWasNull = true
                    null
                }
                is java.sql.Date -> v
                is java.util.Date -> java.sql.Date(v.time)
                is Number -> java.sql.Date(v.toLong())
                is String -> {
                    if (v.isEmpty()) {
                        lastWasNull = true
                        null
                    } else {
                        try {
                            java.sql.Date.valueOf(v.substring(0, 10))
                        } catch (e: Exception) {
                            lastWasNull = true
                            null
                        }
                    }
                }
                else -> {
                    lastWasNull = true
                    null
                }
            }
        }
    }
}
