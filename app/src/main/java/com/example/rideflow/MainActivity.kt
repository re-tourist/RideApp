package com.example.rideflow

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.rideflow.auth.AuthViewModel
import com.example.rideflow.backend.DatabaseHelper
import com.example.rideflow.di.appModule
import com.example.rideflow.navigation.AppNavGraph
import com.example.rideflow.ui.theme.RideFlowTheme
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.compose.koinViewModel
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    // 权限请求器
    private val requestMultiplePermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        // 处理权限请求结果
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 初始化Koin依赖注入
        startKoin {
            androidContext(this@MainActivity)
            modules(appModule)
        }

        // 请求必要的权限
        requestNecessaryPermissions()

        // 测试数据库连接
        testDatabaseConnection()

        // 初始化高德地图SDK
        try {
            // 高德地图SDK会自动读取AndroidManifest中的API密钥
            // 这里可以添加一些全局的地图配置
            com.amap.api.maps2d.model.TextOptions()
            android.util.Log.d("AMap", "✅ 高德地图SDK初始化完成")
        } catch (e: Exception) {
            android.util.Log.e("AMap", "❌ 高德地图SDK初始化失败: ${e.message}")
            e.printStackTrace()
        }

        setContent {
            RideFlowTheme {
                // 使用导航图代替直接使用MainScreen
                AppWithNavigation()
            }
        }
    }

    /**
     * 测试数据库连接
     */
    private fun testDatabaseConnection() {
        Thread {
            try {
                android.util.Log.d("DatabaseTest", "🔍 开始测试数据库连接...")

                // 测试数据库连接
                val isConnected = DatabaseHelper.testConnection()

                if (isConnected) {
                    android.util.Log.d("DatabaseTest", "✅ 数据库连接成功!")

                    // 测试查询用户表是否存在
                    val tableExists = DatabaseHelper.tableExists("users")
                    android.util.Log.d("DatabaseTest", "用户表存在: $tableExists")

                    // 获取数据库中的表
                    val tables = DatabaseHelper.getTableNames()
                    android.util.Log.d("DatabaseTest", "数据库中的表: $tables")

                    // 如果users表存在，测试查询功能
                    if (tableExists) {
                        val userCount = DatabaseHelper.querySingleValue("SELECT COUNT(*) FROM users") as? Long
                        android.util.Log.d("DatabaseTest", "用户数量: $userCount")

                        // 测试表结构
                        val tableStructure = DatabaseHelper.getTableStructure("users")
                        android.util.Log.d("DatabaseTest", "users表结构: $tableStructure")

                        // 检查测试用户数据
                        checkTestUsers()
                    }

                    // 测试简单SQL操作
                    val result = DatabaseHelper.querySingleValue("SELECT 1 + 1")
                    android.util.Log.d("DatabaseTest", "简单SQL测试: 1 + 1 = $result")

                    android.util.Log.d("DatabaseTest", "🎉 所有数据库测试通过!")

                } else {
                    android.util.Log.e("DatabaseTest", "❌ 数据库连接失败!")
                }

            } catch (e: Exception) {
                android.util.Log.e("DatabaseTest", "❌ 数据库测试异常: ${e.message}")
                e.printStackTrace()
            }
        }.start()
    }

    /**
     * 检查测试用户数据
     */
    private fun checkTestUsers() {
        try {
            android.util.Log.d("DatabaseTest", "🔍 检查测试用户数据...")

            // 查询所有用户
            val users = DatabaseHelper.queryMultipleRows("SELECT user_id, nickname, email, status FROM users")
            android.util.Log.d("DatabaseTest", "📊 数据库中的用户数量: ${users.size}")

            users.forEach { user ->
                android.util.Log.d("DatabaseTest", "👤 用户: ${user["nickname"]} (${user["email"]}) - 状态: ${user["status"]}")
            }

            // 检查测试用户是否存在
            val testUser = DatabaseHelper.querySingleRow("SELECT * FROM users WHERE nickname = 'testuser' OR email = 'test@example.com'")
            if (testUser != null) {
                android.util.Log.d("DatabaseTest", "✅ 测试用户存在: ${testUser["nickname"]} / ${testUser["email"]}")
            } else {
                android.util.Log.w("DatabaseTest", "⚠️ 测试用户不存在，请检查数据库数据")
            }

        } catch (e: Exception) {
            android.util.Log.e("DatabaseTest", "❌ 检查测试用户数据异常: ${e.message}")
        }
    }

    private fun requestNecessaryPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET
        )

        // Android 10+需要的后台位置权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        // Android 10+需要的活动识别权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACTIVITY_RECOGNITION)
        }

        // 过滤出未授予的权限
        val ungrantedPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (ungrantedPermissions.isNotEmpty()) {
            requestMultiplePermissionsLauncher.launch(ungrantedPermissions.toTypedArray())
        }
    }
}

/**
 * 应用主组件，集成导航和认证
 */
@Composable
fun AppWithNavigation() {
    val authViewModel = koinViewModel<AuthViewModel>()
    androidx.compose.runtime.LaunchedEffect(Unit) { authViewModel.checkSession() }
    AppNavGraph(authViewModel = authViewModel)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    RideFlowTheme {
        // 为了预览简单起见，直接使用导航图
        val authViewModel = koinViewModel<AuthViewModel>()
        AppNavGraph(authViewModel = authViewModel)
    }
}
