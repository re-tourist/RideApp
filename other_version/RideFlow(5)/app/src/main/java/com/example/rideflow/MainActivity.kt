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
        
        setContent {
            RideFlowTheme {
                // 使用导航图代替直接使用MainScreen
                AppWithNavigation()
            }
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