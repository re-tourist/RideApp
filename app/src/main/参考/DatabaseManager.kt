package com.zjgsu.test

import android.util.Log
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

object DatabaseManager {
    // MySQL 连接配置
    private const val DB_URL = "jdbc:mysql://47.111.105.168:3306/app_database"
    private const val DB_USER = "root"
    private const val DB_PASSWORD = "aA@123456"

    /**
     * 获取数据库连接
     */
    private fun getConnection(): Connection? {
        var connection: Connection? = null
        try {
            // 加载 MySQL 驱动
            Class.forName("com.mysql.jdbc.Driver")
            // 建立数据库连接
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            Log.e("DatabaseManager", "ClassNotFoundException: ${e.message}")
        } catch (e: SQLException) {
            e.printStackTrace()
            Log.e("DatabaseManager", "SQLException: ${e.message}")
        }
        return connection
    }

    /**
     * 执行查询操作
     */
    fun executeQuery(query: String, vararg params: Any): ResultSet? {
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null
        var resultSet: ResultSet? = null
        try {
            connection = getConnection()
            if (connection != null) {
                preparedStatement = connection.prepareStatement(query)
                for ((index, param) in params.withIndex()) {
                    preparedStatement.setObject(index + 1, param)
                }
                resultSet = preparedStatement.executeQuery()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            Log.e("DatabaseManager", "SQLException: ${e.message}")
        }
        return resultSet
    }

    /**
     * 执行更新操作（插入、更新、删除）
     */
    fun executeUpdate(query: String, vararg params: Any): Int {
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null
        var rowsAffected = 0
        try {
            connection = getConnection()
            if (connection != null) {
                preparedStatement = connection.prepareStatement(query)
                for ((index, param) in params.withIndex()) {
                    preparedStatement.setObject(index + 1, param)
                }
                rowsAffected = preparedStatement.executeUpdate()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            Log.e("DatabaseManager", "SQLException: ${e.message}")
        } finally {
            closeResources(connection, preparedStatement, null)
        }
        return rowsAffected
    }

    /**
     * 关闭数据库资源
     */
    private fun closeResources(connection: Connection?, preparedStatement: PreparedStatement?, resultSet: ResultSet?) {
        try {
            resultSet?.close()
            preparedStatement?.close()
            connection?.close()
        } catch (e: SQLException) {
            e.printStackTrace()
            Log.e("DatabaseManager", "Error closing resources: ${e.message}")
        }
    }
}