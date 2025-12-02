package com.example.rideflow.backend

import android.util.Log
import java.sql.*

object DatabaseHelper {
    private const val TAG = "DatabaseHelper"

    // 数据库配置 - 针对云服务器MySQL配置优化
    private const val DB_URL = "jdbc:mysql://101.37.79.220:3306/rideapp?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&characterEncoding=utf8&connectTimeout=15000&socketTimeout=45000&useUnicode=true&autoReconnect=true"
    private const val DB_USER = "root"
    private const val DB_PASSWORD = "Aa@123456"

    /**
     * 获取数据库连接
     */
    private fun getConnection(): Connection? {
        return try {
            // 使用兼容性更好的旧版本MySQL驱动类
            Class.forName("com.mysql.jdbc.Driver")
            val connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)
            Log.d(TAG, "✅ 数据库连接创建成功")
            connection
        } catch (e: Exception) {
            Log.e(TAG, "❌ 获取数据库连接失败: ${e.message}")
            Log.e(TAG, "连接URL: $DB_URL")
            Log.e(TAG, "用户名: $DB_USER")
            Log.e(TAG, "异常类型: ${e.javaClass.simpleName}")
            e.printStackTrace()
            null
        }
    }

    /**
     * 关闭数据库资源
     */
    private fun closeResources(connection: Connection?, statement: Statement?, resultSet: ResultSet?) {
        try {
            resultSet?.close()
            statement?.close()
            connection?.close()
        } catch (e: SQLException) {
            Log.e(TAG, "关闭数据库资源失败: ${e.message}")
        }
    }

    /**
     * 执行查询SQL，返回结果集
     * @param sql 查询SQL语句
     * @param params 参数列表
     * @return ResultSet? 结果集，需要手动关闭或使用processQuery方法
     */
    fun executeQuery(sql: String, params: List<Any> = emptyList()): ResultSet? {
        var connection: Connection? = null
        var statement: PreparedStatement? = null

        return try {
            connection = getConnection()
            statement = connection?.prepareStatement(sql)

            // 设置参数
            params.forEachIndexed { index, param ->
                when {
                    param == null -> statement?.setNull(index + 1, java.sql.Types.NULL)
                    param is Int -> statement?.setInt(index + 1, param)
                    param is String -> statement?.setString(index + 1, param)
                    param is Double -> statement?.setDouble(index + 1, param)
                    param is Float -> statement?.setFloat(index + 1, param)
                    param is Long -> statement?.setLong(index + 1, param)
                    param is Boolean -> statement?.setBoolean(index + 1, param)
                    else -> statement?.setObject(index + 1, param)
                }
            }

            statement?.executeQuery()
        } catch (e: Exception) {
            Log.e(TAG, "执行查询失败: ${e.message}, SQL: $sql")
            closeResources(connection, statement, null)
            null
        }
        // 注意：调用者需要手动关闭ResultSet和连接，或者使用processQuery方法
    }

    /**
     * 处理查询结果，自动关闭资源
     * @param sql 查询SQL语句
     * @param params 参数列表
     * @param processor 结果处理函数
     */
    fun <T> processQuery(
        sql: String,
        params: List<Any> = emptyList(),
        processor: (ResultSet) -> T
    ): T? {
        var connection: Connection? = null
        var statement: PreparedStatement? = null
        var resultSet: ResultSet? = null

        return try {
            Log.d(TAG, "🔍 开始执行查询操作: $sql")
            Log.d(TAG, "参数: $params")
            
            connection = getConnection()
            if (connection == null) {
                Log.e(TAG, "❌ 无法获取数据库连接，查询操作失败")
                return null
            }
            
            statement = connection.prepareStatement(sql)

            // 设置参数
            params.forEachIndexed { index, param ->
                when {
                    param == null -> statement.setNull(index + 1, java.sql.Types.NULL)
                    param is Int -> statement.setInt(index + 1, param)
                    param is String -> statement.setString(index + 1, param)
                    param is Double -> statement.setDouble(index + 1, param)
                    param is Float -> statement.setFloat(index + 1, param)
                    param is Long -> statement.setLong(index + 1, param)
                    param is Boolean -> statement.setBoolean(index + 1, param)
                    else -> statement.setObject(index + 1, param)
                }
            }

            resultSet = statement.executeQuery()
            val result = resultSet?.let { processor(it) }
            Log.d(TAG, "✅ 查询操作成功，返回结果")
            result
        } catch (e: Exception) {
            Log.e(TAG, "❌ 处理查询失败: ${e.message}")
            Log.e(TAG, "SQL: $sql")
            Log.e(TAG, "参数: $params")
            Log.e(TAG, "异常类型: ${e.javaClass.simpleName}")
            e.printStackTrace()
            null
        } finally {
            closeResources(connection, statement, resultSet)
        }
    }

    /**
     * 执行更新操作（INSERT, UPDATE, DELETE）
     * @param sql SQL语句
     * @param params 参数列表
     * @return 影响的行数，-1表示执行失败
     */
    fun executeUpdate(sql: String, params: List<Any> = emptyList()): Int {
        var connection: Connection? = null
        var statement: PreparedStatement? = null

        return try {
            Log.d(TAG, "🔧 开始执行更新操作: $sql")
            Log.d(TAG, "参数: $params")
            
            connection = getConnection()
            if (connection == null) {
                Log.e(TAG, "❌ 无法获取数据库连接，更新操作失败")
                return -1
            }
            
            statement = connection.prepareStatement(sql)

            // 设置参数
            params.forEachIndexed { index, param ->
                when {
                    param == null -> statement.setNull(index + 1, java.sql.Types.NULL)
                    param is Int -> statement.setInt(index + 1, param)
                    param is String -> statement.setString(index + 1, param)
                    param is Double -> statement.setDouble(index + 1, param)
                    param is Float -> statement.setFloat(index + 1, param)
                    param is Long -> statement.setLong(index + 1, param)
                    param is Boolean -> statement.setBoolean(index + 1, param)
                    else -> statement.setObject(index + 1, param)
                }
            }

            val result = statement.executeUpdate()
            Log.d(TAG, "✅ 更新操作成功，影响行数: $result")
            result
        } catch (e: Exception) {
            Log.e(TAG, "❌ 执行更新失败: ${e.message}")
            Log.e(TAG, "SQL: $sql")
            Log.e(TAG, "参数: $params")
            Log.e(TAG, "异常类型: ${e.javaClass.simpleName}")
            e.printStackTrace()
            -1
        } finally {
            closeResources(connection, statement, null)
        }
    }

    /**
     * 执行批量更新操作
     * @param sql SQL语句
     * @param paramsList 参数列表的列表
     * @return 每个操作影响的行数数组
     */
    fun executeBatchUpdate(sql: String, paramsList: List<List<Any>>): IntArray {
        var connection: Connection? = null
        var statement: PreparedStatement? = null

        return try {
            connection = getConnection()
            statement = connection?.prepareStatement(sql)

            // 添加批量参数
            paramsList.forEach { params ->
                params.forEachIndexed { index, param ->
                    when {
                        param == null -> statement?.setNull(index + 1, java.sql.Types.NULL)
                        param is Int -> statement?.setInt(index + 1, param)
                        param is String -> statement?.setString(index + 1, param)
                        param is Double -> statement?.setDouble(index + 1, param)
                        param is Float -> statement?.setFloat(index + 1, param)
                        param is Long -> statement?.setLong(index + 1, param)
                        param is Boolean -> statement?.setBoolean(index + 1, param)
                        else -> statement?.setObject(index + 1, param)
                    }
                }
                statement?.addBatch()
            }

            statement?.executeBatch() ?: intArrayOf()
        } catch (e: Exception) {
            Log.e(TAG, "执行批量更新失败: ${e.message}, SQL: $sql")
            intArrayOf()
        } finally {
            closeResources(connection, statement, null)
        }
    }

    /**
     * 查询单行数据
     * @param sql 查询SQL语句
     * @param params 参数列表
     * @return 包含列名和值的Map，查询失败返回null
     */
    fun querySingleRow(sql: String, params: List<Any> = emptyList()): Map<String, Any>? {
        return processQuery(sql, params) { resultSet ->
            if (resultSet.next()) {
                val metaData = resultSet.metaData
                val rowData = mutableMapOf<String, Any>()
                for (i in 1..metaData.columnCount) {
                    val columnName = metaData.getColumnName(i)
                    val value = if (resultSet.wasNull()) "" else resultSet.getObject(i)
                    rowData[columnName] = value ?: ""
                }
                rowData
            } else {
                null
            }
        }
    }

    /**
     * 查询多行数据
     * @param sql 查询SQL语句
     * @param params 参数列表
     * @return 包含多行数据的List，每行是一个Map
     */
    fun queryMultipleRows(sql: String, params: List<Any> = emptyList()): List<Map<String, Any>> {
        return processQuery(sql, params) { resultSet ->
            val resultList = mutableListOf<Map<String, Any>>()
            val metaData = resultSet.metaData

            while (resultSet.next()) {
                val rowData = mutableMapOf<String, Any>()
                for (i in 1..metaData.columnCount) {
                    val columnName = metaData.getColumnName(i)
                    val value = if (resultSet.wasNull()) "" else resultSet.getObject(i)
                    rowData[columnName] = value ?: ""
                }
                resultList.add(rowData)
            }
            resultList
        } ?: emptyList()
    }

    /**
     * 查询单个值
     * @param sql 查询SQL语句
     * @param params 参数列表
     * @return 查询到的单个值，查询失败返回null
     */
    fun querySingleValue(sql: String, params: List<Any> = emptyList()): Any? {
        return processQuery(sql, params) { resultSet ->
            if (resultSet.next()) {
                val value = resultSet.getObject(1)
                if (resultSet.wasNull()) "" else value
            } else {
                null
            }
        }
    }

    /**
     * 检查表是否存在
     * @param tableName 表名
     * @return 是否存在
     */
    fun tableExists(tableName: String): Boolean {
        return processQuery(
            "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?",
            listOf(tableName)
        ) { resultSet ->
            resultSet.next() && resultSet.getInt(1) > 0
        } ?: false
    }

    /**
     * 获取数据库中的所有表名
     * @return 表名列表
     */
    fun getTableNames(): List<String> {
        return processQuery(
            "SELECT table_name FROM information_schema.tables WHERE table_schema = DATABASE()"
        ) { resultSet ->
            val tables = mutableListOf<String>()
            while (resultSet.next()) {
                tables.add(resultSet.getString(1))
            }
            tables
        } ?: emptyList()
    }

    /**
     * 获取表结构信息
     * @param tableName 表名
     * @return 列信息列表
     */
    fun getTableStructure(tableName: String): List<Map<String, Any>> {
        return queryMultipleRows(
            "SELECT column_name, data_type, is_nullable, column_default, column_key, extra " +
                    "FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = ? ORDER BY ordinal_position",
            listOf(tableName)
        )
    }

    /**
     * 测试数据库连接
     * @return 连接是否成功
     */
    fun testConnection(): Boolean {
        return try {
            val connection = getConnection()
            val success = connection != null && !connection.isClosed
            connection?.close()
            success
        } catch (e: Exception) {
            Log.e(TAG, "测试数据库连接失败: ${e.message}")
            false
        }
    }

    /**
     * 创建users表（如果不存在）
     * @return 是否创建成功
     */
    fun createUsersTable(): Boolean {
        return try {
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
            if (result >= 0) {
                Log.d(TAG, "✅ users表创建成功")
                true
            } else {
                Log.e(TAG, "❌ users表创建失败")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "创建users表异常: ${e.message}")
            false
        }
    }

    /**
     * 删除users表（用于重置）
     * @return 是否删除成功
     */
    fun dropUsersTable(): Boolean {
        return try {
            val sql = "DROP TABLE IF EXISTS users"
            val result = executeUpdate(sql)
            if (result >= 0) {
                Log.d(TAG, "✅ users表删除成功")
                true
            } else {
                Log.e(TAG, "❌ users表删除失败")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "删除users表异常: ${e.message}")
            false
        }
    }
}