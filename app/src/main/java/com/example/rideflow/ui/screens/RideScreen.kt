package com.example.rideflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.width
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.amap.api.maps2d.MapView
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.model.LatLng
import com.amap.api.maps2d.model.PolylineOptions
import com.amap.api.maps2d.model.MarkerOptions
import android.Manifest
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.foundation.clickable
import androidx.compose.material3.TextField
import androidx.compose.material3.TextButton
import com.amap.api.services.help.Inputtips
import com.amap.api.services.help.InputtipsQuery
import com.amap.api.services.help.Tip
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlin.math.*
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import androidx.compose.runtime.getValue   // for by
import androidx.compose.runtime.setValue   // for by
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.compose.koinViewModel
import com.example.rideflow.auth.AuthViewModel
import com.example.rideflow.backend.RideRecordDatabaseHelper
 

sealed class RideStatus {
    object NotStarted : RideStatus()
    object InProgress : RideStatus()
    object Paused : RideStatus()
}

data class RideHistory(
    val date: String,
    val duration: String,
    val distance: String,
    val avgSpeed: String,
    val calories: String
)

data class RideReport(
    val duration: String,
    val distance: String,
    val avgSpeed: String,
    val maxSpeed: String,
    val calories: String,
    val elevation: String
)

@Composable
fun AMap2DContainer(
    modifier: Modifier = Modifier,
    myLocation: LatLng? = null,
    routePoints: List<LatLng> = emptyList(),
    selectedLocation: LatLng? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapView = remember { MapView(context) }
    
    // 处理生命周期
    DisposableEffect(lifecycleOwner) {
        mapView.onCreate(null)
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDestroy()
        }
    }
    
    // 记住上次的参数，避免重复更新
    val lastMyLocation = remember { mutableStateOf<LatLng?>(null) }
    val lastRoutePoints = remember { mutableStateOf<List<LatLng>>(emptyList()) }
    val isInitialSetup = remember { mutableStateOf(true) }
    val mapInitialized = remember { mutableStateOf(false) }
    val lastSelectedLocation = remember { mutableStateOf<LatLng?>(null) }
    
    // 简化的AndroidView实现
    AndroidView(
        factory = { 
            // 初始化地图
            try {
                val map = mapView.map
                
                // 基础设置 - 优化地图显示
                map.uiSettings.isZoomControlsEnabled = true
                map.uiSettings.isMyLocationButtonEnabled = false
                map.uiSettings.isScaleControlsEnabled = true
                map.uiSettings.isCompassEnabled = false
                
                // 设置地图类型为标准地图
                map.mapType = com.amap.api.maps2d.AMap.MAP_TYPE_NORMAL
                
                // 添加相机监听器，仅用于调试日志
                map.setOnCameraChangeListener(object : com.amap.api.maps2d.AMap.OnCameraChangeListener {
                    override fun onCameraChange(position: com.amap.api.maps2d.model.CameraPosition?) {
                        position?.let {
                            android.util.Log.d("AMap", "相机变化中: 缩放=${it.zoom}, 位置=${it.target.latitude},${it.target.longitude}")
                        }
                    }
                    
                    override fun onCameraChangeFinish(position: com.amap.api.maps2d.model.CameraPosition?) {
                        position?.let {
                            android.util.Log.d("AMap", "相机变化完成: 缩放=${it.zoom}, 位置=${it.target.latitude},${it.target.longitude}")
                            if (it.zoom < 3f) {
                                android.util.Log.w("AMap", "缩放级别异常(${it.zoom})，可能显示异常")
                            }
                        }
                    }
                })
                
                // 设置默认位置（杭州）- 使用适中的缩放级别
                val defaultLocation = LatLng(30.311664, 120.394605) // 杭州坐标
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(defaultLocation, 14f) // 14f适合显示城市区域
                map.moveCamera(cameraUpdate)
                mapInitialized.value = true
                
            } catch (e: Exception) {
                e.printStackTrace()
            }
            
            mapView 
        },
        modifier = modifier,
        update = { view ->
            try {
                val map = view.map
                
                // 确保地图已初始化
                if (!mapInitialized.value) return@AndroidView
                
                // 只在参数真正变化时更新，避免重复更新导致的闪烁
                val locationChanged = myLocation != lastMyLocation.value
                val routeChanged = routePoints != lastRoutePoints.value
                val selectedChanged = selectedLocation != lastSelectedLocation.value
                val shouldUpdate = locationChanged || routeChanged || selectedChanged || isInitialSetup.value
                
                if (!shouldUpdate) return@AndroidView
                
                // 清除之前的标记和覆盖物
                map.clear()
                
                // 记录调试信息
                android.util.Log.d("AMapUpdate", "更新地图: location=${myLocation}, routes=${routePoints.size}个点")
                
                // 添加当前位置 - 确保始终显示当前位置
                if (myLocation != null && (locationChanged || isInitialSetup.value)) {
                    map.addMarker(
                        MarkerOptions()
                            .position(myLocation)
                            .title("当前位置")
                            .icon(com.amap.api.maps2d.model.BitmapDescriptorFactory.defaultMarker(com.amap.api.maps2d.model.BitmapDescriptorFactory.HUE_BLUE))
                    )
                    
                    // 只在初始设置时移动相机到当前位置
                    if (isInitialSetup.value && routePoints.isEmpty()) {
                        // 没有路线时，以当前位置为中心显示
                        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLocation, 14f) // 14f适合显示当前位置周围区域
                        map.animateCamera(cameraUpdate)
                    }
                }

                if (selectedLocation != null) {
                    map.addMarker(
                        MarkerOptions()
                            .position(selectedLocation)
                            .title("搜索地点")
                    )
                    if (selectedChanged && routePoints.isEmpty()) {
                        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(selectedLocation, 16f)
                        map.animateCamera(cameraUpdate)
                    }
                }
                
                // 添加路线 - 只在路线变化时更新
                if (routeChanged || isInitialSetup.value) {
                    if (routePoints.size >= 2) {
                        map.addPolyline(
                            PolylineOptions()
                                .add(*routePoints.toTypedArray())
                                .width(6f)
                                .color(Color(0xFF007AFF).toArgb())
                                .visible(true)
                        )
                        
                        // 添加起点和终点标记
                        map.addMarker(MarkerOptions().position(routePoints.first()).title("起点"))
                        map.addMarker(MarkerOptions().position(routePoints.last()).title("终点"))
                        
                        // 有路线时调整视野以包含整个路线
                        if (isInitialSetup.value) {
                            try {
                                val builder = com.amap.api.maps2d.model.LatLngBounds.Builder()
                                routePoints.forEach { builder.include(it) }
                                val bounds = builder.build()
                                val padding = 100 // 像素
                                val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
                                map.moveCamera(cameraUpdate)
                            } catch (e: Exception) {
                                // 边界计算失败时退回到显示起点
                                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(routePoints.first(), 14f)
                                map.moveCamera(cameraUpdate)
                            }
                        }
                    } else if (myLocation != null && isInitialSetup.value) {
                        // 没有路线但有当前位置时，显示当前位置
                        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLocation, 14f)
                        map.animateCamera(cameraUpdate)
                    }
                }
                
                // 更新记住的值
                lastMyLocation.value = myLocation
                lastRoutePoints.value = routePoints
                lastSelectedLocation.value = selectedLocation
                if (isInitialSetup.value) {
                    isInitialSetup.value = false
                }
                
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideScreen(navController: androidx.navigation.NavController) {
    // 状态控制：是否显示历史记录、路书页面
    var showHistoryScreen by remember { mutableStateOf(false) }
    var showRouteBookScreen by remember { mutableStateOf(false) }
    var pendingAutoStart by remember { mutableStateOf(false) }

    // 根据状态切换页面
    if (showHistoryScreen) {
        RideHistoryScreen(onBack = { showHistoryScreen = false })
    } else if (showRouteBookScreen) {
        RouteBookScreen(
            onBackToMain = { showRouteBookScreen = false },
            onShowHistory = { showHistoryScreen = true },
            onStartRide = {
                pendingAutoStart = true
                showRouteBookScreen = false
            }
        )
    } else {
        RideMainContent(
            onShowHistory = { showHistoryScreen = true },
            onShowRouteBook = { showRouteBookScreen = true },
            shouldAutoStart = pendingAutoStart,
            onAutoStartConsumed = { pendingAutoStart = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideMainContent(onShowHistory: () -> Unit, onShowRouteBook: () -> Unit, shouldAutoStart: Boolean, onAutoStartConsumed: () -> Unit) {
    val authViewModel: com.example.rideflow.auth.AuthViewModel = org.koin.androidx.compose.koinViewModel()
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
    var rideStatus = remember { mutableStateOf<RideStatus>(RideStatus.NotStarted) }
    var elapsedSeconds by remember { mutableStateOf(0L) }
    val reportSeconds = remember { mutableStateOf(0L) }
    val rideDurationText = remember(elapsedSeconds) {
        val h = elapsedSeconds / 3600
        val m = (elapsedSeconds % 3600) / 60
        val s = elapsedSeconds % 60
        String.format("%02d:%02d:%02d", h, m, s)
    }
    var showReportDialog = remember { mutableStateOf(false) }

    var rideDuration = remember { mutableStateOf("00:00:00") }
    var rideDistance = remember { mutableStateOf("0.00") }
    var currentSpeed = remember { mutableStateOf("0.0") }
    var avgSpeed = remember { mutableStateOf("0.0") }
    var calories = remember { mutableStateOf("0") }
    var maxSpeed = remember { mutableStateOf("0.0") }
    var elevation = remember { mutableStateOf("0") }
    
    // 骑行统计变量
    var startTime = remember { mutableStateOf<Long?>(null) }
    var totalDistance = remember { mutableStateOf(0.0) } // 米
    var maxSpeedValue = remember { mutableStateOf(0.0) } // 用于计算最大速度
    var previousLocation = remember { mutableStateOf<LatLng?>(null) }

    val context = LocalContext.current
    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val currentLocation = remember { mutableStateOf<LatLng?>(null) }
    val trackPoints = remember { mutableStateListOf<LatLng>() }

    // 辅助函数：计算两点之间的距离（米）
    fun calculateDistance(start: LatLng, end: LatLng): Double {
        val earthRadius = 6371000.0 // 地球半径（米）
        val lat1 = Math.toRadians(start.latitude)
        val lat2 = Math.toRadians(end.latitude)
        val deltaLat = Math.toRadians(end.latitude - start.latitude)
        val deltaLng = Math.toRadians(end.longitude - start.longitude)
        
        val a = sin(deltaLat / 2) * sin(deltaLat / 2) +
                cos(lat1) * cos(lat2) *
                sin(deltaLng / 2) * sin(deltaLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        
        return earthRadius * c
    }
    
    // 辅助函数：获取骑行时长（小时）
    fun getRideDurationHours(): Double {
        val start = startTime.value ?: return 0.0
        val durationMs = System.currentTimeMillis() - start
        return durationMs / (1000.0 * 60.0 * 60.0)
    }
    
    // 辅助函数：更新骑行时长显示
    fun updateRideDuration() {
        val start = startTime.value ?: return
        val durationMs = System.currentTimeMillis() - start
        val totalSeconds = durationMs / 1000
        
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
    }
    
    val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        val granted = (result[Manifest.permission.ACCESS_FINE_LOCATION] == true) || (result[Manifest.permission.ACCESS_COARSE_LOCATION] == true)
        if (granted) {
            fusedClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) {
                    currentLocation.value = LatLng(loc.latitude, loc.longitude)
                } else {
                    // 使用杭州坐标作为fallback
                    currentLocation.value = LatLng(30.311664, 120.394605)
                }
            }.addOnFailureListener {
                // 定位失败时使用杭州坐标
                currentLocation.value = LatLng(30.311664, 120.394605)
            }
        } else {
            // 没有权限时使用杭州坐标
            currentLocation.value = LatLng(30.311664, 120.394605)
        }
    }

    DisposableEffect(rideStatus.value) {
        val hasPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return
                val latLng = LatLng(loc.latitude, loc.longitude)
                currentLocation.value = latLng
                
                if (rideStatus.value is RideStatus.InProgress) {
                    trackPoints.add(latLng)
                    
                    // 计算距离和速度
                    val prevLoc = previousLocation.value
                    if (prevLoc != null) {
                        val distance = calculateDistance(prevLoc, latLng)
                        totalDistance.value += distance
                        
                        // 更新距离显示（公里）
                        rideDistance.value = String.format("%.2f", totalDistance.value / 1000)
                        
                        // 计算当前速度（km/h）
                        val speed = loc.speed * 3.6 // m/s to km/h
                        currentSpeed.value = String.format("%.1f", speed)
                        
                        // 更新最大速度
                        if (speed > maxSpeedValue.value) {
                            maxSpeedValue.value = speed
                            maxSpeed.value = String.format("%.1f", speed)
                        }
                        
                        // 计算平均速度
                        val durationHours = getRideDurationHours()
                        if (durationHours > 0) {
                            val avgSpeedValue = (totalDistance.value / 1000) / durationHours
                            avgSpeed.value = String.format("%.1f", avgSpeedValue)
                        }
                        
                        // 计算卡路里（简化公式：距离(公里) * 体重系数 * 1.036）
                        // 假设平均体重70kg的系数
                        val caloriesValue = (totalDistance.value / 1000) * 50
                        calories.value = caloriesValue.toInt().toString()
                        
                        // 更新骑行时长
                        updateRideDuration()
                    }
                    
                    previousLocation.value = latLng
                }
            }
        }

        if (!hasPermission) {
            permissionLauncher.launch(permissions)
        } else {
            fusedClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) {
                    currentLocation.value = LatLng(loc.latitude, loc.longitude)
                } else {
                    // 使用杭州坐标
                    currentLocation.value = LatLng(30.311664, 120.394605)
                }
            }.addOnFailureListener {
                // 定位失败时使用杭州坐标
                currentLocation.value = LatLng(30.311664, 120.394605)
            }
            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000L)
                .setMinUpdateIntervalMillis(1000L)
                .setWaitForAccurateLocation(true)
                .build()
            fusedClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
        }

        onDispose { fusedClient.removeLocationUpdates(callback) }
    }

    val historyList = listOf(
        RideHistory(
            date = "2025-10-18 15:41",
            duration = "00:08:08",
            distance = "3.25 km",
            avgSpeed = "24.1 km/h",
            calories = "156 kcal"
        ),
        RideHistory(
            date = "2025-10-17 22:33",
            duration = "00:15:03",
            distance = "5.67 km",
            avgSpeed = "22.6 km/h",
            calories = "210 kcal"
        )
    )

    val rideReport = RideReport(
        duration = remember(reportSeconds.value) {
            val h = reportSeconds.value / 3600
            val m = (reportSeconds.value % 3600) / 60
            val s = reportSeconds.value % 60
            String.format("%02d:%02d:%02d", h, m, s)
        },
        distance = "${rideDistance.value} km",
        avgSpeed = "${avgSpeed.value} km/h",
        maxSpeed = "${maxSpeed.value} km/h",
        calories = "${calories.value} kcal",
        elevation = "${elevation.value} m"
    )

    fun startRide() {
        trackPoints.clear()
        startTime.value = System.currentTimeMillis()
        totalDistance.value = 0.0
        maxSpeedValue.value = 0.0
        previousLocation.value = null
        rideDistance.value = "0.00"
        currentSpeed.value = "0.0"
        avgSpeed.value = "0.0"
        calories.value = "0"
        maxSpeed.value = "0.0"
        elevation.value = "0"
        rideStatus.value = RideStatus.InProgress 
    }
    val onStartClick = { startRide() }
    val onPauseClick = { rideStatus.value = RideStatus.Paused }
    val onResumeClick = { rideStatus.value = RideStatus.InProgress }
    val onStopClick: () -> Unit = {
        reportSeconds.value = elapsedSeconds      // 先记住
        elapsedSeconds = 0                        // 再清零
        startTime.value = null
        rideStatus.value = RideStatus.NotStarted
        showReportDialog.value = true
        val userId = authViewModel.getCurrentUser()?.userId?.toIntOrNull() ?: 0
        val startMillis = startTime.value ?: System.currentTimeMillis()
        val durationSec = reportSeconds.value.toInt()
        val distanceKm = totalDistance.value / 1000.0
        val avg = avgSpeed.value.toDoubleOrNull() ?: run {
            val hours = getRideDurationHours()
            if (hours > 0) (totalDistance.value / 1000.0) / hours else 0.0
        }
        val caloriesVal = calories.value.toIntOrNull() ?: ((totalDistance.value / 1000.0) * 50).toInt()
        val climbVal = elevation.value.toIntOrNull() ?: 0
        val maxVal = maxSpeedValue.value
        val recordId = com.example.rideflow.backend.RideRecordDatabaseHelper.generateRecordId(userId)
        coroutineScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            com.example.rideflow.backend.RideRecordDatabaseHelper.insertUserRideRecord(
                recordId = recordId,
                userId = userId,
                startTimeMillis = startMillis,
                durationSec = durationSec,
                distanceKm = distanceKm,
                avgSpeedKmh = avg,
                calories = caloriesVal,
                climb = climbVal,
                maxSpeedKmh = maxVal
            )
        }
        Unit
    }
    val onCloseReport = { showReportDialog.value = false }

    if (showReportDialog.value) {
        AlertDialog(
            onDismissRequest = onCloseReport,
            confirmButton = {
                Button(onClick = onCloseReport) { Text("关闭") }
            },
            title = { Text("骑行报告") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("用时: ${rideReport.duration}")
                    Text("距离: ${rideReport.distance}")
                    Text("均速: ${rideReport.avgSpeed}")
                    Text("最高速: ${rideReport.maxSpeed}")
                    Text("卡路里: ${rideReport.calories}")
                    Text("爬升: ${rideReport.elevation}")
                }
            }
        )
    }

    LaunchedEffect(rideStatus.value) {
        when (rideStatus.value) {
            RideStatus.InProgress -> {
                // 进入骑行状态：如果还没记录过 startTime，就记一下
                if (startTime.value == null) startTime.value = System.currentTimeMillis()
                // 每秒 +1
                while (true) {
                    delay(1_000)
                    elapsedSeconds += 1
                }
            }
            RideStatus.Paused  -> {
                // 暂停时什么都不做，elapsedSeconds 保持
            }
            RideStatus.NotStarted -> {
                // 回到未开始：清零
                elapsedSeconds = 0
                startTime.value = null
            }
        }
    }

    LaunchedEffect(shouldAutoStart) {
        if (shouldAutoStart && rideStatus.value is RideStatus.NotStarted) {
            startRide()
            onAutoStartConsumed()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            when (rideStatus.value) {
                is RideStatus.NotStarted -> {
                    TopAppBar(
                        title = { ModeToggle(activeIsSport = true, onClick = onShowRouteBook) },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFF007AFF)
                        ),
                        actions = {
                            IconButton(onClick = onShowHistory) {
                                Icon(
                                    imageVector = Icons.Filled.DateRange,
                                    contentDescription = "骑行记录",
                                    tint = Color.White
                                )
                            }
                        }
                    )
                }
                is RideStatus.InProgress -> {
                    TopAppBar(
                        title = { Text("正在运动", color = Color.White) },
                        navigationIcon = {
                            Button(
                                onClick = onStopClick,
                                modifier = Modifier.size(40.dp),
                                shape = RoundedCornerShape(4.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                elevation = ButtonDefaults.buttonElevation(0.dp)
                            ) { Text("结束", color = Color.White, fontSize = 14.sp) }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF007AFF))
                    )
                }
                is RideStatus.Paused -> {
                    TopAppBar(
                        title = { Text("已暂停", color = Color.White) },
                        navigationIcon = {
                            Button(
                                onClick = onStopClick,
                                modifier = Modifier.size(40.dp),
                                shape = RoundedCornerShape(4.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                elevation = ButtonDefaults.buttonElevation(0.dp)
                            ) { Text("结束", color = Color.White, fontSize = 14.sp) }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF007AFF))
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (rideStatus.value) {
                RideStatus.NotStarted -> NotStartedContent(onStartClick = onStartClick, myLocation = currentLocation.value)
                RideStatus.InProgress -> InProgressContent(
                    duration = rideDurationText,
                    distance = rideDistance.value,
                    currentSpeed = currentSpeed.value,
                    avgSpeed = avgSpeed.value,
                    calories = calories.value,
                    onPauseClick = onPauseClick,
                    onStopClick = onStopClick,
                    myLocation = currentLocation.value,
                    routePoints = trackPoints
                )
                RideStatus.Paused -> PausedContent(
                    duration = rideDurationText,
                    distance = rideDistance.value,
                    currentSpeed = currentSpeed.value,
                    avgSpeed = avgSpeed.value,
                    calories = calories.value,
                    onResumeClick = onResumeClick,
                    onStopClick = onStopClick,
                    myLocation = currentLocation.value,
                    routePoints = trackPoints
                )
            }
        }
    }
}

@Composable
fun NotStartedContent(
    onStartClick: () -> Unit,
    myLocation: LatLng?
) {
    val context = LocalContext.current
    var showSearchDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val searchResults = remember { mutableStateListOf<Tip>() }
    val selectedPlace = remember { mutableStateOf<LatLng?>(null) }
    val historyList = remember {
        listOf(
            RideHistory(
                date = "2025-10-18 15:41",
                duration = "00:08:08",
                distance = "3.25 km",
                avgSpeed = "24.1 km/h",
                calories = "156 kcal"
            ),
            RideHistory(
                date = "2025-10-17 22:33",
                duration = "00:15:03",
                distance = "5.67 km",
                avgSpeed = "22.6 km/h",
                calories = "210 kcal"
            )
        )
    }

    LaunchedEffect(searchQuery) {
        val q = searchQuery.trim()
        if (q.isEmpty()) {
            searchResults.clear()
        } else {
            delay(300)
            val query = InputtipsQuery(q, "")
            query.cityLimit = false
            val inputtips = Inputtips(context, query)
            inputtips.setInputtipsListener { tips, code ->
                searchResults.clear()
                if (code == 1000) {
                    if (tips != null) searchResults.addAll(tips)
                }
            }
            inputtips.requestInputtipsAsyn()
        }
    }

    if (showSearchDialog) {
        AlertDialog(
            onDismissRequest = { showSearchDialog = false },
            confirmButton = {
                TextButton(onClick = { showSearchDialog = false }) { Text("关闭") }
            },
            title = { Text("地点查询") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("输入关键词") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.fillMaxWidth().height(240.dp)) {
                        items(searchResults) { tip ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val p = tip.point
                                        if (p != null) {
                                            selectedPlace.value = LatLng(p.latitude, p.longitude)
                                            showSearchDialog = false
                                        }
                                    }
                                    .padding(vertical = 10.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(tip.name)
                                    val addr = (tip.district ?: "") + (tip.address ?: "")
                                    Text(addr, fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(440.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    AMap2DContainer(
                        modifier = Modifier.fillMaxSize(),
                        myLocation = myLocation,
                        selectedLocation = selectedPlace.value
                    )
                }
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("经度: ${myLocation?.longitude?.let { String.format("%.6f", it) } ?: "定位中"}", fontSize = 14.sp, color = Color.Gray)
                        Text("纬度: ${myLocation?.latitude?.let { String.format("%.6f", it) } ?: "定位中"}", fontSize = 14.sp, color = Color.Gray)
                        Text("精度: 30.0m", fontSize = 14.sp, color = Color.Gray)
                        Text("浙江省杭州市钱塘区2号大街48-64号靠近工商大学云滨(地铁站)", fontSize = 14.sp, color = Color.Black, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(120.dp)) }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 84.dp)
        ) {
            Button(
                onClick = onStartClick,
                modifier = Modifier.size(width = 240.dp, height = 56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF))
            ) { Text("开始运动", fontSize = 18.sp, color = Color.White) }
        }
    }
}

@Composable
fun InProgressContent(
    duration: String,
    distance: String,
    currentSpeed: String,
    avgSpeed: String,
    calories: String,
    onPauseClick: () -> Unit,
    onStopClick: () -> Unit,
    myLocation: LatLng?,
    routePoints: List<LatLng>
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(duration, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF007AFF), modifier = Modifier.align(Alignment.CenterHorizontally))
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(distance, fontSize = 18.sp, fontWeight = FontWeight.Bold); Text("距离(km)", fontSize = 12.sp, color = Color.Gray) }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(currentSpeed, fontSize = 18.sp, fontWeight = FontWeight.Bold); Text("速度(km/h)", fontSize = 12.sp, color = Color.Gray) }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(avgSpeed, fontSize = 18.sp, fontWeight = FontWeight.Bold); Text("均速(km/h)", fontSize = 12.sp, color = Color.Gray) }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(calories, fontSize = 18.sp, fontWeight = FontWeight.Bold); Text("卡路里", fontSize = 12.sp, color = Color.Gray) }
                    }
                }
            }
            
            // 地图区域使用固定高度和最小化重绘
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(440.dp)
                    .padding(horizontal = 16.dp)
            ) {
                AMap2DContainer(
                    modifier = Modifier.fillMaxSize(),
                    myLocation = myLocation,
                    routePoints = routePoints
                )
            }
        }
        
        // 下半部分：按钮区域，固定在导航栏上方
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 84.dp)
        ) {
            ControlButtons(
                leftText = "暂停",
                rightText = "结束",
                onLeftClick = onPauseClick,
                onRightClick = onStopClick
            )
        }
    }
}

@Composable
fun PausedContent(
    duration: String,
    distance: String,
    currentSpeed: String,
    avgSpeed: String,
    calories: String,
    onResumeClick: () -> Unit,
    onStopClick: () -> Unit,
    myLocation: LatLng?,
    routePoints: List<LatLng>
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = duration,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF9500)
                            )
                            Spacer(modifier = Modifier.width(6.dp))   // 可选：让两个字之间空一点
                            Text(
                                text = "已暂停",
                                fontSize = 14.sp,                      // 比原来再小一点
                                color = Color(0xFFFF9500)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(distance, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Text("距离(km)", fontSize = 12.sp, color = Color.Gray)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(currentSpeed, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Text("速度(km/h)", fontSize = 12.sp, color = Color.Gray)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(avgSpeed, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Text("均速(km/h)", fontSize = 12.sp, color = Color.Gray)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(calories, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                Text("卡路里", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(440.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    AMap2DContainer(
                        modifier = Modifier.fillMaxSize(),
                        myLocation = myLocation,
                        routePoints = routePoints
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(120.dp)) }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 84.dp)
        ) {
            ControlButtons(
                leftText = "继续",
                rightText = "结束",
                onLeftClick = onResumeClick,
                onRightClick = onStopClick
            )
        }
    }
}

// ------------------------------
// 骑行记录页面
// ------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideHistoryScreen(onBack: () -> Unit) {
    
    val authViewModel: com.example.rideflow.auth.AuthViewModel = org.koin.androidx.compose.koinViewModel()
    val records = remember { mutableStateListOf<com.example.rideflow.backend.RideRecordDatabaseHelper.UserRideRecord>() }
    fun formatDuration(seconds: Int): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return String.format("%02d:%02d:%02d", h, m, s)
    }
    LaunchedEffect(Unit) {
        val id = authViewModel.getCurrentUser()?.userId?.toIntOrNull()
        if (id != null && id > 0) {
            val list = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                com.example.rideflow.backend.RideRecordDatabaseHelper.getUserRideRecords(id)
            }
            records.clear()
            records.addAll(list)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("骑行记录") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            items(records) { r ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = r.startTime,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                RideDataRow("用时", formatDuration(r.durationSec))
                                RideDataRow("距离", String.format("%.2f km", r.distanceKm))
                            }
                            Column {
                                RideDataRow("均速", String.format("%.2f km/h", r.avgSpeedKmh))
                                RideDataRow("消耗", String.format("%d kcal", r.calories))
                            }
                            // 模拟路线缩略图区域
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(
                                        Color.LightGray.copy(alpha = 0.3f),
                                        RoundedCornerShape(4.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("路线", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
            // 底部留白
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun RideDataRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(bottom = 4.dp)) {
        Text(text = "$label: ", fontSize = 14.sp, color = Color.Gray)
        Text(text = value, fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ModeToggle(activeIsSport: Boolean, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "运动",
                color = Color.White,
                fontSize = if (activeIsSport) 16.sp else 13.sp
            )
            Text(text = "/", color = Color.White, modifier = Modifier.padding(horizontal = 4.dp))
            Text(
                text = "导航",
                color = Color.White,
                fontSize = if (activeIsSport) 13.sp else 16.sp
            )
        }
    }
}

@Composable
fun ControlButtons(
    leftText: String,
    rightText: String,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit
) {
    Row(
        modifier = Modifier.padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onLeftClick,
            modifier = Modifier.size(90.dp, 50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9500))
        ) { Text(leftText, color = Color.White, fontSize = 14.sp) }
        Spacer(modifier = Modifier.width(40.dp))
        Button(
            onClick = onRightClick,
            modifier = Modifier.size(90.dp, 50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3B30))
        ) { Text(rightText, color = Color.White, fontSize = 14.sp) }
    }
}

