package com.example.rideflow.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
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
import com.amap.api.services.help.Inputtips
import com.amap.api.services.help.InputtipsQuery
import com.amap.api.services.help.Tip
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlin.math.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import com.example.rideflow.auth.AuthViewModel
import com.example.rideflow.backend.RideRecordDatabaseHelper
import com.example.rideflow.navigation.AppRoutes
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeQuery
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.geocoder.RegeocodeResult
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.poisearch.PoiSearch
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.route.RouteSearch
import com.amap.api.services.route.WalkRouteResult
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationListener
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.resume
import org.json.JSONObject
 

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
private fun AMap2DContainer(
    modifier: Modifier = Modifier,
    myLocation: LatLng? = null,
    accuracy: Float? = null,
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
    val lastRouteCount = remember { mutableStateOf(0) }
    val isInitialSetup = remember { mutableStateOf(true) }
    val mapInitialized = remember { mutableStateOf(false) }
    val lastSelectedLocation = remember { mutableStateOf<LatLng?>(null) }
    val firstLocationCentered = remember { mutableStateOf(false) }
    
    // 简化的AndroidView实现
    AndroidView(
        factory = { 
            // 初始化地图
            try {
                val map = mapView.map
                
                // 基础设置 - 优化地图显示
                map.uiSettings.isZoomControlsEnabled = false
                map.uiSettings.isMyLocationButtonEnabled = false
                map.uiSettings.isScaleControlsEnabled = false
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
                val routeChanged = routePoints.size != lastRouteCount.value
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
                    
                    if (!firstLocationCentered.value && (accuracy == null || accuracy <= 20f)) {
                        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLocation, 16f)
                        map.animateCamera(cameraUpdate)
                        firstLocationCentered.value = true
                    } else if (isInitialSetup.value && routePoints.isEmpty()) {
                        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLocation, 14f)
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
                
                // 添加路线
                if (routePoints.size >= 2) {
                    map.addPolyline(
                        PolylineOptions()
                            .add(*routePoints.toTypedArray())
                            .width(6f)
                            .color(Color(0xFF007AFF).toArgb())
                            .visible(true)
                    )
                    
                    map.addMarker(MarkerOptions().position(routePoints.first()).title("起点"))
                    map.addMarker(MarkerOptions().position(routePoints.last()).title("终点"))
                    
                    if (isInitialSetup.value) {
                        try {
                            val builder = com.amap.api.maps2d.model.LatLngBounds.Builder()
                            routePoints.forEach { builder.include(it) }
                            val bounds = builder.build()
                            val padding = 100
                            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
                            map.moveCamera(cameraUpdate)
                        } catch (e: Exception) {
                            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(routePoints.first(), 14f)
                            map.moveCamera(cameraUpdate)
                        }
                    }
                } else if (myLocation != null && isInitialSetup.value) {
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLocation, 14f)
                    map.animateCamera(cameraUpdate)
                }
                
                // 更新记住的值
                lastMyLocation.value = myLocation
                lastRouteCount.value = routePoints.size
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
    var showHistoryScreen by remember { mutableStateOf(false) }

    if (showHistoryScreen) {
        RideHistoryScreen(onBack = { showHistoryScreen = false })
    } else {
        RideMainContent(
            onShowHistory = { showHistoryScreen = true },
            switchButtonText = "导航",
            onSwitchClick = { navController.navigate(AppRoutes.RIDE_NAVIGATION) },
            enableSearch = false
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideNavigationScreen(navController: androidx.navigation.NavController) {
    var showHistoryScreen by remember { mutableStateOf(false) }
    if (showHistoryScreen) {
        RideHistoryScreen(onBack = { showHistoryScreen = false })
    } else {
        RideMainContent(
            onShowHistory = { showHistoryScreen = true },
            switchButtonText = "开始",
            onSwitchClick = { navController.popBackStack() },
            enableSearch = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideMainContent(
    onShowHistory: () -> Unit,
    switchButtonText: String? = null,
    onSwitchClick: (() -> Unit)? = null,
    enableSearch: Boolean = false
) {
    val authViewModel: com.example.rideflow.auth.AuthViewModel = org.koin.androidx.compose.koinViewModel()
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
    val rideStatusStateSaver: Saver<androidx.compose.runtime.MutableState<RideStatus>, Int> = Saver(
        save = { state ->
            when (state.value) {
                RideStatus.NotStarted -> 0
                RideStatus.InProgress -> 1
                RideStatus.Paused -> 2
            }
        },
        restore = { saved ->
            mutableStateOf(
                when (saved) {
                    1 -> RideStatus.InProgress
                    2 -> RideStatus.Paused
                    else -> RideStatus.NotStarted
                }
            )
        }
    )

    var rideStatus = rememberSaveable(saver = rideStatusStateSaver) { mutableStateOf<RideStatus>(RideStatus.NotStarted) }
    var elapsedSeconds by rememberSaveable { mutableStateOf(0L) }
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
    val currentAccuracy = remember { mutableStateOf<Float?>(null) }
    var locationErrorText by remember { mutableStateOf<String?>(null) }
    val trackPoints = remember { mutableStateListOf<LatLng>() }
    var showSearchDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val searchResults = remember { mutableStateListOf<Tip>() }
    val selectedPlace = remember { mutableStateOf<LatLng?>(null) }
    val navRoutePoints = remember { mutableStateListOf<LatLng>() }
    val routeComputed = remember { mutableStateOf(false) }
    val totalClimb = remember { mutableStateOf(0.0) }
    val lastAltitude = remember { mutableStateOf<Double?>(null) }
    val lastSmoothedAltitude = remember { mutableStateOf<Double?>(null) }
    val amapClientHolder = remember { mutableStateOf<AMapLocationClient?>(null) }

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

    fun wgs84ToGcj02IfNeeded(latitude: Double, longitude: Double): LatLng {
        fun outOfChina(lat: Double, lon: Double): Boolean {
            return lon < 72.004 || lon > 137.8347 || lat < 0.8293 || lat > 55.8271
        }
        fun transformLat(x: Double, y: Double): Double {
            var ret =
                -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * sqrt(abs(x))
            ret += (20.0 * sin(6.0 * x * PI) + 20.0 * sin(2.0 * x * PI)) * 2.0 / 3.0
            ret += (20.0 * sin(y * PI) + 40.0 * sin(y / 3.0 * PI)) * 2.0 / 3.0
            ret += (160.0 * sin(y / 12.0 * PI) + 320.0 * sin(y * PI / 30.0)) * 2.0 / 3.0
            return ret
        }
        fun transformLon(x: Double, y: Double): Double {
            var ret =
                300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * sqrt(abs(x))
            ret += (20.0 * sin(6.0 * x * PI) + 20.0 * sin(2.0 * x * PI)) * 2.0 / 3.0
            ret += (20.0 * sin(x * PI) + 40.0 * sin(x / 3.0 * PI)) * 2.0 / 3.0
            ret += (150.0 * sin(x / 12.0 * PI) + 300.0 * sin(x / 30.0 * PI)) * 2.0 / 3.0
            return ret
        }

        if (outOfChina(latitude, longitude)) return LatLng(latitude, longitude)

        val a = 6378245.0
        val ee = 0.00669342162296594323
        var dLat = transformLat(longitude - 105.0, latitude - 35.0)
        var dLon = transformLon(longitude - 105.0, latitude - 35.0)
        val radLat = latitude / 180.0 * PI
        var magic = sin(radLat)
        magic = 1 - ee * magic * magic
        val sqrtMagic = sqrt(magic)
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * PI)
        dLon = (dLon * 180.0) / (a / sqrtMagic * cos(radLat) * PI)
        val mgLat = latitude + dLat
        val mgLon = longitude + dLon
        return LatLng(mgLat, mgLon)
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
                    currentLocation.value = wgs84ToGcj02IfNeeded(loc.latitude, loc.longitude)
                    currentAccuracy.value = loc.accuracy
                } else {
                    // 使用杭州坐标作为fallback
                    currentLocation.value = LatLng(30.311664, 120.394605)
                    currentAccuracy.value = null
                }
            }.addOnFailureListener {
                // 定位失败时使用杭州坐标
                currentLocation.value = LatLng(30.311664, 120.394605)
                currentAccuracy.value = null
            }
        } else {
            // 没有权限时使用杭州坐标
            currentLocation.value = LatLng(30.311664, 120.394605)
            currentAccuracy.value = null
        }
    }

    DisposableEffect(rideStatus.value) {
        val hasPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return
                val latLng = wgs84ToGcj02IfNeeded(loc.latitude, loc.longitude)
                currentLocation.value = latLng
                currentAccuracy.value = loc.accuracy
                
                val useGmsForTracking = amapClientHolder.value == null
                if (useGmsForTracking && rideStatus.value is RideStatus.InProgress) {
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
                    currentLocation.value = wgs84ToGcj02IfNeeded(loc.latitude, loc.longitude)
                    currentAccuracy.value = loc.accuracy
                } else {
                    // 使用杭州坐标
                    currentLocation.value = LatLng(30.311664, 120.394605)
                    currentAccuracy.value = null
                }
            }.addOnFailureListener {
                // 定位失败时使用杭州坐标
                currentLocation.value = LatLng(30.311664, 120.394605)
                currentAccuracy.value = null
            }
            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L)
                .setMinUpdateIntervalMillis(500L)
                .setWaitForAccurateLocation(true)
                .build()
            fusedClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
            try {
                val cts = com.google.android.gms.tasks.CancellationTokenSource()
                fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                    .addOnSuccessListener { loc ->
                        if (loc != null) {
                            currentLocation.value = wgs84ToGcj02IfNeeded(loc.latitude, loc.longitude)
                            currentAccuracy.value = loc.accuracy
                        }
                    }
            } catch (_: Exception) {}

            try {
                val aClient = AMapLocationClient(context.applicationContext)
                val option = AMapLocationClientOption()
                option.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
                option.isSensorEnable = true
                option.isOnceLocation = false
                option.interval = 1000L
                option.isGpsFirst = true
                option.isLocationCacheEnable = false
                option.httpTimeOut = 20000
                option.isMockEnable = false
                aClient.setLocationOption(option)
                val listener = AMapLocationListener { aLoc: AMapLocation? ->
                    if (aLoc != null) {
                        val acc = aLoc.accuracy
                        if (acc > 0f) {
                            val better = currentAccuracy.value == null || acc < (currentAccuracy.value ?: Float.MAX_VALUE)
                            if (better || aLoc.locationType == AMapLocation.LOCATION_TYPE_GPS) {
                                currentLocation.value = LatLng(aLoc.latitude, aLoc.longitude)
                                currentAccuracy.value = acc
                            }
                        }
                    }
                    if (aLoc != null && rideStatus.value is RideStatus.InProgress) {
                        val latLng = LatLng(aLoc.latitude, aLoc.longitude)
                        trackPoints.add(latLng)
                        val prevLoc = previousLocation.value
                        if (prevLoc != null) {
                            val distance = calculateDistance(prevLoc, latLng)
                            totalDistance.value += distance
                            rideDistance.value = String.format("%.2f", totalDistance.value / 1000)

                            val speed = aLoc.speed * 3.6
                            currentSpeed.value = String.format("%.1f", speed)
                            if (speed > maxSpeedValue.value) {
                                maxSpeedValue.value = speed.toDouble()
                                maxSpeed.value = String.format("%.1f", speed)
                            }
                            val durationHours = getRideDurationHours()
                            if (durationHours > 0) {
                                val avgSpeedValue = (totalDistance.value / 1000) / durationHours
                                avgSpeed.value = String.format("%.1f", avgSpeedValue)
                            }
                            val caloriesValue = (totalDistance.value / 1000) * 50
                            calories.value = caloriesValue.toInt().toString()
                            updateRideDuration()
                        }
                        previousLocation.value = latLng

                        val alt = aLoc.altitude.toDouble()
                        if (!alt.isNaN() && alt > -1000 && alt < 10000) {
                            val alpha = 0.1
                            val smoothed = if (lastSmoothedAltitude.value == null) alt else alpha * alt + (1 - alpha) * (lastSmoothedAltitude.value ?: alt)
                            lastSmoothedAltitude.value = smoothed
                            val prev = lastAltitude.value
                            if (prev != null) {
                                val delta = smoothed - prev
                                if (delta > 1.0) {
                                    totalClimb.value += delta
                                    elevation.value = totalClimb.value.roundToInt().toString()
                                }
                            }
                            lastAltitude.value = smoothed
                        }
                    }
                }
                aClient.setLocationListener(listener)
                aClient.startLocation()
                amapClientHolder.value = aClient
            } catch (_: Exception) {
                // ignore AMap client init failure
            }
        }

        onDispose { 
            fusedClient.removeLocationUpdates(callback)
            try {
                amapClientHolder.value?.stopLocation()
                amapClientHolder.value?.onDestroy()
                amapClientHolder.value = null
            } catch (_: Exception) {}
        }
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

    val onStartClick = { 
        // 重置轨迹点和统计数据
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
    val onPauseClick = { rideStatus.value = RideStatus.Paused }
    val onResumeClick = { rideStatus.value = RideStatus.InProgress }
    val onStopClick: () -> Unit = {
        val startMillis = startTime.value ?: System.currentTimeMillis()
        reportSeconds.value = elapsedSeconds      // 先记住
        elapsedSeconds = 0                        // 再清零
        startTime.value = null
        rideStatus.value = RideStatus.NotStarted
        showReportDialog.value = true
        val userId = authViewModel.getCurrentUser()?.userId?.toIntOrNull() ?: 0
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

    LaunchedEffect(currentLocation.value) {
        if (currentLocation.value != null) {
            locationErrorText = null
            return@LaunchedEffect
        }
        delay(2500)
        if (currentLocation.value == null) {
            locationErrorText = "位置获取失败，请检查网络"
        }
    }

    LaunchedEffect(searchQuery, enableSearch) {
        if (!enableSearch) {
            searchResults.clear()
        } else {
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
    }

    if (showSearchDialog && enableSearch) {
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
                                            navRoutePoints.clear()
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

    LaunchedEffect(selectedPlace.value, currentLocation.value, enableSearch) {
        val dest = selectedPlace.value
        val src = currentLocation.value
        if (enableSearch && dest != null && src != null) {
            try {
                routeComputed.value = false
                navRoutePoints.clear()

                fun getAmapKeyFromManifest(): String? {
                    return try {
                        val appInfo = context.packageManager.getApplicationInfo(
                            context.packageName,
                            PackageManager.GET_META_DATA
                        )
                        appInfo.metaData?.getString("com.amap.api.v2.apikey")
                    } catch (_: Throwable) {
                        null
                    }
                }

                suspend fun fetchBicyclingRouteFromWebService(): List<LatLng>? {
                    val key = getAmapKeyFromManifest()?.trim().orEmpty()
                    if (key.isEmpty()) return null

                    return withContext(Dispatchers.IO) {
                        val origin = "${src.longitude},${src.latitude}"
                        val destination = "${dest.longitude},${dest.latitude}"
                        val u =
                            URL("https://restapi.amap.com/v4/direction/bicycling?origin=$origin&destination=$destination&key=$key")
                        val conn = (u.openConnection() as HttpURLConnection).apply {
                            connectTimeout = 10000
                            readTimeout = 15000
                            requestMethod = "GET"
                        }
                        try {
                            val code = conn.responseCode
                            val stream =
                                if (code in 200..299) conn.inputStream else conn.errorStream
                                    ?: return@withContext null
                            val body = BufferedReader(InputStreamReader(stream)).use { it.readText() }
                            val json = JSONObject(body)
                            val errcode = json.optInt("errcode", -1)
                            if (errcode != 0) return@withContext null
                            val data = json.optJSONObject("data") ?: return@withContext null
                            val paths = data.optJSONArray("paths") ?: return@withContext null
                            val path0 = paths.optJSONObject(0) ?: return@withContext null
                            val steps = path0.optJSONArray("steps") ?: return@withContext null

                            val points = ArrayList<LatLng>(512)
                            points.add(src)
                            var last: LatLng? = null

                            fun addPoint(p: LatLng) {
                                if (last == null || last!!.latitude != p.latitude || last!!.longitude != p.longitude) {
                                    points.add(p)
                                    last = p
                                }
                            }

                            for (i in 0 until steps.length()) {
                                val step = steps.optJSONObject(i) ?: continue
                                val polyline = step.optString("polyline", "").trim()
                                if (polyline.isEmpty()) continue
                                val segs = polyline.split(';')
                                for (seg in segs) {
                                    val parts = seg.split(',')
                                    if (parts.size != 2) continue
                                    val lon = parts[0].toDoubleOrNull() ?: continue
                                    val lat = parts[1].toDoubleOrNull() ?: continue
                                    addPoint(LatLng(lat, lon))
                                }
                            }
                            addPoint(dest)
                            if (points.size >= 2) points else null
                        } catch (_: Throwable) {
                            null
                        } finally {
                            conn.disconnect()
                        }
                    }
                }

                fun parseDrive(result: com.amap.api.services.route.DriveRouteResult): List<LatLng>? {
                    val path = result.paths?.firstOrNull() ?: return null
                    val points = mutableListOf<LatLng>()
                    points.add(src)
                    path.steps?.forEach { step ->
                        step?.polyline?.forEach { p -> points.add(LatLng(p.latitude, p.longitude)) }
                    }
                    points.add(dest)
                    return if (points.size >= 2) points else null
                }

                fun parseWalk(result: WalkRouteResult): List<LatLng>? {
                    val path = result.paths?.firstOrNull() ?: return null
                    val points = mutableListOf<LatLng>()
                    points.add(src)
                    path.steps?.forEach { step ->
                        step?.polyline?.forEach { p -> points.add(LatLng(p.latitude, p.longitude)) }
                    }
                    points.add(dest)
                    return if (points.size >= 2) points else null
                }

                suspend fun calculateDriveRoutePoints(): List<LatLng>? {
                    val from = LatLonPoint(src.latitude, src.longitude)
                    val to = LatLonPoint(dest.latitude, dest.longitude)
                    val fromAndTo = RouteSearch.FromAndTo(from, to)
                    val routeSearch = RouteSearch(context)
                    val query = RouteSearch.DriveRouteQuery(
                        fromAndTo,
                        RouteSearch.DRIVING_SINGLE_DEFAULT,
                        null,
                        null,
                        ""
                    )
                    return withTimeoutOrNull(12000L) {
                        suspendCancellableCoroutine { cont ->
                            routeSearch.setRouteSearchListener(object : RouteSearch.OnRouteSearchListener {
                                override fun onDriveRouteSearched(
                                    result: com.amap.api.services.route.DriveRouteResult?,
                                    code: Int
                                ) {
                                    if (cont.isCompleted) return
                                    if (code == 1000 && result != null) cont.resume(result) else cont.resume(null)
                                }

                                override fun onWalkRouteSearched(result: WalkRouteResult?, code: Int) {}
                                override fun onBusRouteSearched(
                                    result: com.amap.api.services.route.BusRouteResult?,
                                    code: Int
                                ) {}
                                override fun onRideRouteSearched(
                                    result: com.amap.api.services.route.RideRouteResult?,
                                    code: Int
                                ) {}
                            })
                            try {
                                routeSearch.calculateDriveRouteAsyn(query)
                            } catch (_: Throwable) {
                                if (!cont.isCompleted) cont.resume(null)
                            }
                        }
                    }?.let { parseDrive(it) }
                }

                suspend fun calculateWalkRoutePoints(): List<LatLng>? {
                    val from = LatLonPoint(src.latitude, src.longitude)
                    val to = LatLonPoint(dest.latitude, dest.longitude)
                    val fromAndTo = RouteSearch.FromAndTo(from, to)
                    val routeSearch = RouteSearch(context)
                    val query = RouteSearch.WalkRouteQuery(fromAndTo)
                    return withTimeoutOrNull(12000L) {
                        suspendCancellableCoroutine { cont ->
                            routeSearch.setRouteSearchListener(object : RouteSearch.OnRouteSearchListener {
                                override fun onWalkRouteSearched(result: WalkRouteResult?, code: Int) {
                                    if (cont.isCompleted) return
                                    if (code == 1000 && result != null) cont.resume(result) else cont.resume(null)
                                }

                                override fun onDriveRouteSearched(
                                    result: com.amap.api.services.route.DriveRouteResult?,
                                    code: Int
                                ) {}
                                override fun onBusRouteSearched(
                                    result: com.amap.api.services.route.BusRouteResult?,
                                    code: Int
                                ) {}
                                override fun onRideRouteSearched(
                                    result: com.amap.api.services.route.RideRouteResult?,
                                    code: Int
                                ) {}
                            })
                            try {
                                routeSearch.calculateWalkRouteAsyn(query)
                            } catch (_: Throwable) {
                                if (!cont.isCompleted) cont.resume(null)
                            }
                        }
                    }?.let { parseWalk(it) }
                }

                val points =
                    fetchBicyclingRouteFromWebService()
                        ?: calculateDriveRoutePoints()
                        ?: calculateWalkRoutePoints()
                        ?: listOf(src, dest)

                navRoutePoints.clear()
                navRoutePoints.addAll(points)
                routeComputed.value = true
            } catch (_: Throwable) {
                navRoutePoints.clear()
                navRoutePoints.add(src)
                navRoutePoints.add(dest)
                routeComputed.value = true
            }
        } else {
            navRoutePoints.clear()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            val titleText = when (rideStatus.value) {
                RideStatus.NotStarted -> "运动"
                RideStatus.InProgress -> "运动中"
                RideStatus.Paused -> "已暂停"
            }
            TopAppBar(
                title = { Text(titleText, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF007BFF)),
                actions = {
                    IconButton(onClick = onShowHistory) {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = "骑行记录",
                            tint = Color.White
                        )
                    }
                    if (enableSearch) {
                        TextButton(
                            onClick = { showSearchDialog = true },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                        ) { Text("查询") }
                    }
                    if (switchButtonText != null && onSwitchClick != null) {
                        TextButton(
                            onClick = onSwitchClick,
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                        ) { Text(switchButtonText) }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (rideStatus.value) {
                RideStatus.NotStarted -> NotStartedContent(
                    onStartClick = onStartClick,
                    duration = rideDurationText,
                    distance = rideDistance.value,
                    currentSpeed = currentSpeed.value,
                    myLocation = currentLocation.value,
                    accuracy = currentAccuracy.value,
                    selectedLocation = if (enableSearch) selectedPlace.value else null,
                    routePoints = if (enableSearch) navRoutePoints else emptyList(),
                    isLoading = currentLocation.value == null,
                    errorText = locationErrorText
                )
                RideStatus.InProgress -> InProgressContent(
                    duration = rideDurationText,
                    distance = rideDistance.value,
                    currentSpeed = currentSpeed.value,
                    onPauseClick = onPauseClick,
                    onStopClick = onStopClick,
                    myLocation = currentLocation.value,
                    accuracy = currentAccuracy.value,
                    routePoints = trackPoints,
                    isLoading = currentLocation.value == null,
                    errorText = locationErrorText
                )
                RideStatus.Paused -> PausedContent(
                    duration = rideDurationText,
                    distance = rideDistance.value,
                    currentSpeed = currentSpeed.value,
                    onResumeClick = onResumeClick,
                    onStopClick = onStopClick,
                    myLocation = currentLocation.value,
                    accuracy = currentAccuracy.value,
                    routePoints = trackPoints,
                    isLoading = currentLocation.value == null,
                    errorText = locationErrorText
                )
            }
        }
    }
}

@Composable
fun NotStartedContent(
    onStartClick: () -> Unit,
    duration: String,
    distance: String,
    currentSpeed: String,
    myLocation: LatLng?,
    accuracy: Float? = null,
    selectedLocation: LatLng? = null,
    routePoints: List<LatLng> = emptyList(),
    isLoading: Boolean,
    errorText: String?
) {
    RideWorkoutLayout(
        duration = duration,
        distance = distance,
        currentSpeed = currentSpeed,
        highlightData = false,
        myLocation = myLocation,
        accuracy = accuracy,
        routePoints = routePoints,
        selectedLocation = selectedLocation,
        isLoading = isLoading,
        errorText = errorText,
        primaryButton = {
            RidePrimaryButton(
                text = "开始",
                containerColor = Color(0xFF007BFF),
                onClick = onStartClick,
                modifier = Modifier.fillMaxWidth(0.7f)
            )
        },
        secondaryButton = null
    )
}

@Composable
fun InProgressContent(
    duration: String,
    distance: String,
    currentSpeed: String,
    onPauseClick: () -> Unit,
    onStopClick: () -> Unit,
    myLocation: LatLng?,
    accuracy: Float?,
    routePoints: List<LatLng>,
    isLoading: Boolean,
    errorText: String?
) {
    var showStopConfirm by rememberSaveable { mutableStateOf(false) }
    if (showStopConfirm) {
        AlertDialog(
            onDismissRequest = { showStopConfirm = false },
            title = { Text("结束运动") },
            text = { Text("确认结束本次运动？") },
            confirmButton = {
                Button(
                    onClick = {
                        showStopConfirm = false
                        onStopClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC3545))
                ) { Text("结束", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showStopConfirm = false }) { Text("取消") }
            }
        )
    }

    RideWorkoutLayout(
        duration = duration,
        distance = distance,
        currentSpeed = currentSpeed,
        highlightData = true,
        myLocation = myLocation,
        accuracy = accuracy,
        routePoints = routePoints,
        selectedLocation = null,
        isLoading = isLoading,
        errorText = errorText,
        primaryButton = {
            RidePrimaryButton(
                text = "暂停",
                containerColor = Color(0xFF007BFF),
                onClick = onPauseClick,
                modifier = Modifier.weight(1.2f)
            )
        },
        secondaryButton = {
            RideSecondaryButton(
                text = "结束",
                containerColor = Color(0xFFDC3545),
                onClick = { showStopConfirm = true },
                modifier = Modifier.weight(1f)
            )
        }
    )
}

@Composable
fun PausedContent(
    duration: String,
    distance: String,
    currentSpeed: String,
    onResumeClick: () -> Unit,
    onStopClick: () -> Unit,
    myLocation: LatLng?,
    accuracy: Float?,
    routePoints: List<LatLng>,
    isLoading: Boolean,
    errorText: String?
) {
    var showStopConfirm by rememberSaveable { mutableStateOf(false) }
    if (showStopConfirm) {
        AlertDialog(
            onDismissRequest = { showStopConfirm = false },
            title = { Text("结束运动") },
            text = { Text("确认结束本次运动？") },
            confirmButton = {
                Button(
                    onClick = {
                        showStopConfirm = false
                        onStopClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC3545))
                ) { Text("结束", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showStopConfirm = false }) { Text("取消") }
            }
        )
    }

    RideWorkoutLayout(
        duration = duration,
        distance = distance,
        currentSpeed = currentSpeed,
        highlightData = false,
        myLocation = myLocation,
        accuracy = accuracy,
        routePoints = routePoints,
        selectedLocation = null,
        isLoading = isLoading,
        errorText = errorText,
        primaryButton = {
            RidePrimaryButton(
                text = "继续",
                containerColor = Color(0xFF28A745),
                onClick = onResumeClick,
                modifier = Modifier.weight(1.2f)
            )
        },
        secondaryButton = {
            RideSecondaryButton(
                text = "结束",
                containerColor = Color(0xFFDC3545),
                onClick = { showStopConfirm = true },
                modifier = Modifier.weight(1f)
            )
        }
    )
}

@Composable
private fun RideWorkoutLayout(
    duration: String,
    distance: String,
    currentSpeed: String,
    highlightData: Boolean,
    myLocation: LatLng?,
    accuracy: Float?,
    routePoints: List<LatLng>,
    selectedLocation: LatLng?,
    isLoading: Boolean,
    errorText: String?,
    primaryButton: @Composable RowScope.() -> Unit,
    secondaryButton: (@Composable RowScope.() -> Unit)?
) {
    val background = Color(0xFFF8F9FA)
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {
        val mapHeight = maxHeight * 0.62f

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(mapHeight)
                .align(Alignment.TopCenter)
        ) {
            AMap2DContainer(
                modifier = Modifier.fillMaxSize(),
                myLocation = myLocation,
                accuracy = accuracy,
                routePoints = routePoints,
                selectedLocation = selectedLocation
            )

            AnimatedVisibility(visible = isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF007BFF), strokeWidth = 3.dp)
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 96.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (secondaryButton == null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    primaryButton()
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    primaryButton()
                    secondaryButton()
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            RideRealtimeDataCard(
                duration = duration,
                distance = distance,
                currentSpeed = currentSpeed,
                highlight = highlightData
            )
        }

        AnimatedVisibility(
            visible = !errorText.isNullOrBlank(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text(
                    text = errorText.orEmpty(),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    fontSize = 12.sp,
                    color = Color(0xFF6C757D)
                )
            }
        }
    }
}

@Composable
private fun RidePrimaryButton(
    text: String,
    containerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(1f) }
    var pulseKey by remember { mutableStateOf(0) }
    LaunchedEffect(pulseKey) {
        if (pulseKey > 0) {
            scale.snapTo(1f)
            scale.animateTo(1.1f, tween(durationMillis = 90))
            scale.animateTo(1f, tween(durationMillis = 120))
        }
    }
    Button(
        onClick = {
            pulseKey += 1
            onClick()
        },
        modifier = modifier
            .height(56.dp)
            .scale(scale.value),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor)
    ) {
        Text(text = text, fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun RideSecondaryButton(
    text: String,
    containerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(1f) }
    var pulseKey by remember { mutableStateOf(0) }
    LaunchedEffect(pulseKey) {
        if (pulseKey > 0) {
            scale.snapTo(1f)
            scale.animateTo(1.08f, tween(durationMillis = 90))
            scale.animateTo(1f, tween(durationMillis = 120))
        }
    }
    Button(
        onClick = {
            pulseKey += 1
            onClick()
        },
        modifier = modifier
            .height(56.dp)
            .scale(scale.value),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor)
    ) {
        Text(text = text, fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun RideRealtimeDataCard(
    duration: String,
    distance: String,
    currentSpeed: String,
    highlight: Boolean
) {
    val borderColor = if (highlight) Color(0xFF007BFF) else Color(0xFFE9ECEF)
    val containerColor = if (highlight) Color(0xFFF2F7FF) else Color.White

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RideDataItem(
                label = "当前速度",
                value = currentSpeed,
                unit = "km/h",
                modifier = Modifier.weight(1f)
            )
            RideDataItem(
                label = "总距离",
                value = distance,
                unit = "km",
                modifier = Modifier.weight(1f)
            )
            RideDataItem(
                label = "用时",
                value = duration,
                unit = "",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun RideDataItem(
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.Bottom) {
            AnimatedContent(targetState = value, label = "ride-data") { v ->
                Text(
                    text = v,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF212529)
                )
            }
            if (unit.isNotEmpty()) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = unit, fontSize = 13.sp, color = Color(0xFF6C757D))
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, fontSize = 12.sp, color = Color(0xFF6C757D))
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
