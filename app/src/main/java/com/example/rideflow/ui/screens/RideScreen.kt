package com.example.rideflow.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.amap.api.maps2d.MapView
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.model.LatLng
import com.amap.api.maps2d.model.PolylineOptions
import com.amap.api.maps2d.model.MarkerOptions
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import com.example.rideflow.backend.DatabaseHelper
import com.example.rideflow.navigation.AppRoutes
import coil.compose.AsyncImage
import com.example.rideflow.R
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

object RideSessionKeeper {
    var keepAlive by mutableStateOf(false)
}

private fun formatRideDuration(elapsedSeconds: Long): String {
    val totalMinutes = elapsedSeconds / 60
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    val seconds = elapsedSeconds % 60
    if (hours <= 99) {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
    val days = hours / 24
    val remainingHours = hours % 24
    return String.format("%03d:%02d:%02d", days, remainingHours, minutes)
}

private object RideGoalSession {
    var selectedMode by mutableStateOf(RideStartMode.FreeRide)
    var selectedGoalType by mutableStateOf(TargetGoalType.Distance)
    var selectedDistanceKm by mutableStateOf(10.0)
    var selectedDurationMinutes by mutableStateOf(30)
    var selectedCaloriesKcal by mutableStateOf(200)

    var active by mutableStateOf(false)
    var goalType by mutableStateOf(TargetGoalType.Distance)
    var targetDistanceKm by mutableStateOf(10.0)
    var targetDurationMinutes by mutableStateOf(30)
    var targetCaloriesKcal by mutableStateOf(200)
    var reached by mutableStateOf(false)

    fun startSession() {
        active = selectedMode == RideStartMode.TargetRide
        reached = false
        goalType = selectedGoalType
        targetDistanceKm = selectedDistanceKm
        targetDurationMinutes = selectedDurationMinutes
        targetCaloriesKcal = selectedCaloriesKcal
    }

    fun stopSession() {
        active = false
        reached = false
    }

    fun updateProgress(distanceKm: Double, elapsedSeconds: Long, caloriesKcal: Int) {
        if (!active || reached) return
        reached = when (goalType) {
            TargetGoalType.Distance -> distanceKm >= targetDistanceKm
            TargetGoalType.Duration -> elapsedSeconds >= targetDurationMinutes * 60L
            TargetGoalType.Calories -> caloriesKcal >= targetCaloriesKcal
        }
    }
}

private enum class RideStartMode {
    FreeRide,
    TargetRide
}

private enum class TargetGoalType {
    Distance,
    Duration,
    Calories
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
    selectedLocation: LatLng? = null,
    snapshotterState: androidx.compose.runtime.MutableState<(((Bitmap) -> Unit) -> Unit)?>? = null
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
            snapshotterState?.value = null
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

                snapshotterState?.value = { onBitmap ->
                    map.getMapScreenShot(object : com.amap.api.maps2d.AMap.OnMapScreenShotListener {
                        override fun onMapScreenShot(p0: Bitmap?) {
                            if (p0 != null) onBitmap(p0)
                        }
                    })
                }
                
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
                if (myLocation != null) {
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
    var showRouteSelect by rememberSaveable { mutableStateOf(false) }
    var selectedRouteId by rememberSaveable { mutableStateOf<Int?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (showHistoryScreen) {
            RideHistoryScreen(onBack = { showHistoryScreen = false })
        } else {
            RideMainContent(
                onShowHistory = { showHistoryScreen = true },
                routeButtonText = "路书",
                onRouteClick = { showRouteSelect = true },
                enableSearch = false
            )
        }

        if (showRouteSelect) {
            RouteSelectBottomSheet(
                selectedRouteId = selectedRouteId,
                onDismiss = { showRouteSelect = false },
                onSelectRouteId = { id ->
                    selectedRouteId = if (selectedRouteId == id) null else id
                }
            )
        }
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
            routeButtonText = null,
            onRouteClick = null,
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
    routeButtonText: String? = null,
    onRouteClick: (() -> Unit)? = null,
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
    val rideDurationText = remember(elapsedSeconds) { formatRideDuration(elapsedSeconds) }
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
    val mapSnapshotterState = remember { mutableStateOf<(((Bitmap) -> Unit) -> Unit)?>(null) }
    val rideMapUrl = remember { mutableStateOf<String?>(null) }
    val rideMapUploading = remember { mutableStateOf(false) }
    val rideMapUploadError = remember { mutableStateOf<String?>(null) }

    val trackPointsSnapshot by remember { derivedStateOf { trackPoints.toList() } }
    val navRoutePointsSnapshot by remember { derivedStateOf { navRoutePoints.toList() } }

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

    fun appendTrackPoint(latLng: LatLng, speedMps: Float, accuracy: Float?) {
        if (rideStatus.value !is RideStatus.InProgress) return
        if (abs(latLng.latitude) < 0.1 && abs(latLng.longitude) < 0.1) return

        val prevLoc = previousLocation.value
        if (prevLoc != null) {
            val distance = calculateDistance(prevLoc, latLng)
            if (distance < 2.0) return

            totalDistance.value += distance
            rideDistance.value = String.format("%.2f", totalDistance.value / 1000)

            val speedKmh = speedMps * 3.6
            currentSpeed.value = String.format("%.1f", speedKmh)

            if (speedKmh.toDouble() > maxSpeedValue.value) {
                maxSpeedValue.value = speedKmh.toDouble()
                maxSpeed.value = String.format("%.1f", speedKmh)
            }

            val durationHours = getRideDurationHours()
            if (durationHours > 0) {
                val avgSpeedValue = (totalDistance.value / 1000) / durationHours
                avgSpeed.value = String.format("%.1f", avgSpeedValue)
            }

            val caloriesValue = (totalDistance.value / 1000) * 50
            calories.value = caloriesValue.toInt().toString()

            updateRideDuration()

            RideGoalSession.updateProgress(
                distanceKm = totalDistance.value / 1000.0,
                elapsedSeconds = elapsedSeconds,
                caloriesKcal = calories.value.toIntOrNull() ?: 0
            )
        }

        trackPoints.add(latLng)
        previousLocation.value = latLng
        if (accuracy != null && accuracy > 0f) {
            currentAccuracy.value = accuracy
        }
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
                    appendTrackPoint(latLng = latLng, speedMps = loc.speed, accuracy = loc.accuracy)
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
                        appendTrackPoint(latLng = latLng, speedMps = aLoc.speed, accuracy = aLoc.accuracy)

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
        RideGoalSession.startSession()
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
        rideMapUrl.value = null
        rideMapUploading.value = false
        rideMapUploadError.value = null
        rideStatus.value = RideStatus.InProgress 
    }
    val onPauseClick = { rideStatus.value = RideStatus.Paused }
    val onResumeClick = { rideStatus.value = RideStatus.InProgress }
    val onStopClick: () -> Unit = {
        RideGoalSession.stopSession()
        rideMapUrl.value = null
        rideMapUploading.value = false
        rideMapUploadError.value = null

        coroutineScope.launch {
            val startMillis = startTime.value ?: System.currentTimeMillis()
            val userId = authViewModel.getCurrentUser()?.userId?.toIntOrNull() ?: 0
            val recordId = com.example.rideflow.backend.RideRecordDatabaseHelper.generateRecordId(userId)

            val snapshotBitmap: Bitmap? = withTimeoutOrNull(1_200) {
                suspendCancellableCoroutine { cont ->
                    val snapshotter = mapSnapshotterState.value
                    if (snapshotter == null) {
                        cont.resume(null)
                        return@suspendCancellableCoroutine
                    }
                    snapshotter { bmp ->
                        if (cont.isActive) cont.resume(bmp)
                    }
                }
            }

            reportSeconds.value = elapsedSeconds
            elapsedSeconds = 0
            startTime.value = null
            rideStatus.value = RideStatus.NotStarted
            showReportDialog.value = true

            val durationSec = reportSeconds.value.toInt()
            val distanceKm = totalDistance.value / 1000.0
            val avg = avgSpeed.value.toDoubleOrNull() ?: run {
                val hours = getRideDurationHours()
                if (hours > 0) (totalDistance.value / 1000.0) / hours else 0.0
            }
            val caloriesVal = calories.value.toIntOrNull() ?: ((totalDistance.value / 1000.0) * 50).toInt()
            val climbVal = elevation.value.toIntOrNull() ?: 0
            val maxVal = maxSpeedValue.value

            if (snapshotBitmap == null) {
                rideMapUploadError.value = "地图截图失败"
                coroutineScope.launch(Dispatchers.IO) {
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
                return@launch
            }

            rideMapUploading.value = true
            coroutineScope.launch(Dispatchers.IO) {
                try {
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
                    val file = com.example.rideflow.utils.ImageUploadUtils.createImageFile(context)
                    val saved = com.example.rideflow.utils.ImageUploadUtils.saveBitmapToFile(snapshotBitmap, file)
                    if (!saved) throw IllegalStateException("图片保存失败")

                    val dir = com.example.rideflow.BuildConfig.OSS_RIDEMAP_DIR.trim().trim('/')
                    val objectKey = "$dir/${recordId}_${startMillis}.jpg"
                    val url = com.example.rideflow.utils.ImageUploadUtils.uploadFileToOss(
                        context = context,
                        localFile = file,
                        ossObjectKey = objectKey
                    )
                    com.example.rideflow.backend.RideRecordDatabaseHelper.updateRideRecordTrackImageUrl(
                        recordId = recordId,
                        trackImageUrl = url
                    )
                    withContext(Dispatchers.Main) {
                        rideMapUrl.value = url
                        rideMapUploading.value = false
                        rideMapUploadError.value = null
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        rideMapUploading.value = false
                        rideMapUploadError.value = e.message ?: "上传失败"
                    }
                }
            }
        }
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
                    if (rideMapUploading.value) {
                        Text("地图: 上传中…")
                    } else if (!rideMapUrl.value.isNullOrBlank()) {
                        Text("地图: ${rideMapUrl.value}")
                    } else if (!rideMapUploadError.value.isNullOrBlank()) {
                        Text("地图: ${rideMapUploadError.value}")
                    }
                }
            }
        )
    }

    LaunchedEffect(rideStatus.value) {
        RideSessionKeeper.keepAlive = rideStatus.value is RideStatus.InProgress || rideStatus.value is RideStatus.Paused
        when (rideStatus.value) {
            RideStatus.InProgress -> {
                // 进入骑行状态：如果还没记录过 startTime，就记一下
                if (startTime.value == null) startTime.value = System.currentTimeMillis()
                // 每秒 +1
                while (true) {
                    delay(1_000)
                    elapsedSeconds += 1
                    RideGoalSession.updateProgress(
                        distanceKm = totalDistance.value / 1000.0,
                        elapsedSeconds = elapsedSeconds,
                        caloriesKcal = calories.value.toIntOrNull() ?: 0
                    )
                }
            }
            RideStatus.Paused  -> {
                // 暂停时什么都不做，elapsedSeconds 保持
            }
            RideStatus.NotStarted -> {
                // 回到未开始：清零
                elapsedSeconds = 0
                startTime.value = null
                RideGoalSession.stopSession()
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
            if (rideStatus.value != RideStatus.NotStarted) {
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
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (rideStatus.value) {
                RideStatus.NotStarted -> NotStartedContent(
                    onStartClick = onStartClick,
                    routeButtonText = routeButtonText,
                    onRouteClick = onRouteClick,
                    myLocation = currentLocation.value,
                    accuracy = currentAccuracy.value,
                    selectedLocation = if (enableSearch) selectedPlace.value else null,
                    routePoints = if (enableSearch) navRoutePointsSnapshot else emptyList(),
                    isLoading = currentLocation.value == null,
                    errorText = locationErrorText
                )
                RideStatus.InProgress -> InProgressContent(
                    duration = rideDurationText,
                    distance = rideDistance.value,
                    currentSpeed = currentSpeed.value,
                    avgSpeed = avgSpeed.value,
                    maxSpeed = maxSpeed.value,
                    calories = calories.value,
                    elevation = elevation.value,
                    onPauseClick = onPauseClick,
                    onStopClick = onStopClick,
                    myLocation = currentLocation.value,
                    accuracy = currentAccuracy.value,
                    routePoints = trackPointsSnapshot,
                    mapSnapshotterState = mapSnapshotterState,
                    isLoading = currentLocation.value == null,
                    errorText = locationErrorText
                )
                RideStatus.Paused -> PausedContent(
                    duration = rideDurationText,
                    distance = rideDistance.value,
                    currentSpeed = currentSpeed.value,
                    avgSpeed = avgSpeed.value,
                    maxSpeed = maxSpeed.value,
                    calories = calories.value,
                    elevation = elevation.value,
                    onResumeClick = onResumeClick,
                    onStopClick = onStopClick,
                    myLocation = currentLocation.value,
                    accuracy = currentAccuracy.value,
                    routePoints = trackPointsSnapshot,
                    mapSnapshotterState = mapSnapshotterState,
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
    routeButtonText: String?,
    onRouteClick: (() -> Unit)?,
    myLocation: LatLng?,
    accuracy: Float? = null,
    selectedLocation: LatLng? = null,
    routePoints: List<LatLng> = emptyList(),
    isLoading: Boolean,
    errorText: String?
) {
    val background = Color(0xFFF6F7F9)
    val primaryGreen = Color(0xFF00C56E)
    val moodTexts = remember {
        listOf(
            "把风留在身后",
            "出发前，先听一听链条的声音",
            "今天的路，刚好适合慢慢骑",
            "先把自己带到路上",
            "准备好了，就走"
        )
    }
    val moodText = remember {
        val index = (System.currentTimeMillis() / (1000L * 60L * 60L)).toInt().let { kotlin.math.abs(it) } % moodTexts.size
        moodTexts[index]
    }

    var mode by rememberSaveable { mutableStateOf(RideStartMode.FreeRide) }
    var targetGoalType by rememberSaveable { mutableStateOf(TargetGoalType.Distance) }
    var targetDistanceKm by rememberSaveable { mutableStateOf(10.0) }
    var targetDurationMinutes by rememberSaveable { mutableStateOf(30) }
    var targetCaloriesKcal by rememberSaveable { mutableStateOf(200) }
    var showTargetSheet by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(mode) {
        RideGoalSession.selectedMode = mode
    }
    LaunchedEffect(targetGoalType) {
        RideGoalSession.selectedGoalType = targetGoalType
    }
    LaunchedEffect(targetDistanceKm) {
        RideGoalSession.selectedDistanceKm = targetDistanceKm
    }
    LaunchedEffect(targetDurationMinutes) {
        RideGoalSession.selectedDurationMinutes = targetDurationMinutes
    }
    LaunchedEffect(targetCaloriesKcal) {
        RideGoalSession.selectedCaloriesKcal = targetCaloriesKcal
    }

    if (showTargetSheet) {
        when (targetGoalType) {
            TargetGoalType.Distance -> {
                TargetDistanceBottomSheet(
                    initialDistanceKm = targetDistanceKm,
                    onDismiss = { showTargetSheet = false },
                    onConfirm = { km ->
                        targetDistanceKm = km
                        showTargetSheet = false
                    },
                    primaryGreen = primaryGreen
                )
            }

            TargetGoalType.Duration -> {
                TargetDurationBottomSheet(
                    initialMinutes = targetDurationMinutes,
                    onDismiss = { showTargetSheet = false },
                    onConfirm = { minutes ->
                        targetDurationMinutes = minutes
                        showTargetSheet = false
                    },
                    primaryGreen = primaryGreen
                )
            }

            TargetGoalType.Calories -> {
                TargetCaloriesBottomSheet(
                    initialKcal = targetCaloriesKcal,
                    onDismiss = { showTargetSheet = false },
                    onConfirm = { kcal ->
                        targetCaloriesKcal = kcal
                        showTargetSheet = false
                    },
                    primaryGreen = primaryGreen
                )
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {
        val navBarBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        val bottomSpacing = navBarBottom + 24.dp
        val mapHeight = maxHeight * 0.60f

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 14.dp)
        ) {
            Text(
                text = moodText,
                fontSize = 14.sp,
                color = Color.Black.copy(alpha = 0.60f),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))

            AnimatedContent(targetState = mode, label = "ride_start_mode") { targetMode ->
                when (targetMode) {
                    RideStartMode.FreeRide -> {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(mapHeight),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AMap2DContainer(
                                    modifier = Modifier.fillMaxSize(),
                                    myLocation = myLocation,
                                    accuracy = accuracy,
                                    routePoints = routePoints,
                                    selectedLocation = selectedLocation
                                )

                                androidx.compose.animation.AnimatedVisibility(visible = isLoading) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Card(
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color.White)
                                        ) {
                                            Box(
                                                modifier = Modifier.padding(12.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator(color = primaryGreen, strokeWidth = 3.dp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    RideStartMode.TargetRide -> {
                        val toggleGoalType = {
                            targetGoalType = when (targetGoalType) {
                                TargetGoalType.Distance -> TargetGoalType.Duration
                                TargetGoalType.Duration -> TargetGoalType.Calories
                                TargetGoalType.Calories -> TargetGoalType.Distance
                            }
                        }
                        TargetGoalHeroCard(
                            height = mapHeight,
                            goalType = targetGoalType,
                            distanceKm = targetDistanceKm,
                            durationMinutes = targetDurationMinutes,
                            caloriesKcal = targetCaloriesKcal,
                            onClick = { showTargetSheet = true },
                            onPrevGoal = toggleGoalType,
                            onNextGoal = toggleGoalType,
                            primaryGreen = primaryGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            RideModeTabs(
                selected = mode,
                onSelect = { mode = it },
                primaryGreen = primaryGreen
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp)
                .padding(bottom = bottomSpacing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val routeGap = 10.dp
            var routeWidthPx by remember { mutableStateOf(0) }
            val routeWidthDp = with(LocalDensity.current) { routeWidthPx.toDp() }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!routeButtonText.isNullOrBlank() && onRouteClick != null) {
                    Spacer(modifier = Modifier.width(routeWidthDp + routeGap))
                }

                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = primaryGreen),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clickable(onClick = onStartClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "GO",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                if (!routeButtonText.isNullOrBlank() && onRouteClick != null) {
                    Spacer(modifier = Modifier.width(routeGap))
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .onSizeChanged { routeWidthPx = it.width }
                            .clickable(onClick = onRouteClick)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Map,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = Color.Black.copy(alpha = 0.72f)
                            )
                            Text(
                                text = routeButtonText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black.copy(alpha = 0.78f)
                            )
                        }
                    }
                }
            }

            androidx.compose.animation.AnimatedVisibility(visible = !errorText.isNullOrBlank()) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Text(
                        text = errorText.orEmpty(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        fontSize = 13.sp,
                        color = Color.Black.copy(alpha = 0.75f)
                    )
                }
            }
        }
    }
}

@Composable
private fun RideModeTabs(
    selected: RideStartMode,
    onSelect: (RideStartMode) -> Unit,
    primaryGreen: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(999.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .height(40.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RideModeTabItem(
                text = "自由骑",
                selected = selected == RideStartMode.FreeRide,
                onClick = { onSelect(RideStartMode.FreeRide) },
                primaryGreen = primaryGreen,
                modifier = Modifier.weight(1f)
            )
            RideModeTabItem(
                text = "目标骑",
                selected = selected == RideStartMode.TargetRide,
                onClick = { onSelect(RideStartMode.TargetRide) },
                primaryGreen = primaryGreen,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun RideModeTabItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    primaryGreen: Color,
    modifier: Modifier = Modifier
) {
    val bg = if (selected) primaryGreen.copy(alpha = 0.14f) else Color.Transparent
    val fg = if (selected) primaryGreen else Color.Black.copy(alpha = 0.62f)
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bg, RoundedCornerShape(999.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = fg
        )
    }
}

@Composable
private fun TargetGoalHeroCard(
    height: Dp,
    goalType: TargetGoalType,
    distanceKm: Double,
    durationMinutes: Int,
    caloriesKcal: Int,
    onClick: () -> Unit,
    onPrevGoal: () -> Unit,
    onNextGoal: () -> Unit,
    primaryGreen: Color
) {
    val title = when (goalType) {
        TargetGoalType.Distance -> "目标公里"
        TargetGoalType.Duration -> "目标时长"
        TargetGoalType.Calories -> "目标消耗"
    }
    val hint = when (goalType) {
        TargetGoalType.Distance -> "点击设置（公里）"
        TargetGoalType.Duration -> "点击设置（时:分）"
        TargetGoalType.Calories -> "点击设置（千卡）"
    }
    val mainValueText = when (goalType) {
        TargetGoalType.Distance -> String.format("%.2f", distanceKm)
        TargetGoalType.Duration -> {
            val h = durationMinutes / 60
            val m = durationMinutes % 60
            String.format("%02d:%02d", h, m)
        }
        TargetGoalType.Calories -> caloriesKcal.toString()
    }
    val unitText = when (goalType) {
        TargetGoalType.Distance -> "km"
        TargetGoalType.Duration -> ""
        TargetGoalType.Calories -> ""
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = primaryGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.Center)) {
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(onClick = onPrevGoal) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "切换目标类型",
                            tint = Color.White.copy(alpha = 0.88f)
                        )
                    }
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.88f)
                    )
                    IconButton(onClick = onNextGoal) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "切换目标类型",
                            tint = Color.White.copy(alpha = 0.88f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = mainValueText,
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    if (unitText.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = unitText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.90f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = hint,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.80f)
                )
            }
        }
    }
}

@Composable
private fun TargetDistanceCard(
    distanceKm: Double,
    onClick: () -> Unit,
    primaryGreen: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Text(
                text = "目标距离",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black.copy(alpha = 0.58f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = String.format("%.1f", distanceKm),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black.copy(alpha = 0.86f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "km",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = primaryGreen
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "点击设置",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black.copy(alpha = 0.48f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TargetDistanceBottomSheet(
    initialDistanceKm: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit,
    primaryGreen: Color
) {
    var distanceKm by rememberSaveable { mutableStateOf(initialDistanceKm) }
    val quick = remember { listOf(5.0, 10.0, 20.0, 50.0) }

    fun clamp(v: Double): Double = v.coerceIn(1.0, 300.0)

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 20.dp)
        ) {
            Text(
                text = "设置目标距离",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black.copy(alpha = 0.86f)
            )
            Spacer(modifier = Modifier.height(14.dp))

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { distanceKm = clamp(distanceKm - 0.5) }) {
                        Icon(imageVector = Icons.Filled.Remove, contentDescription = "减少")
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = String.format("%.1f", distanceKm),
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black.copy(alpha = 0.86f)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "km",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = primaryGreen
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "支持 0.5 km 步进",
                            fontSize = 12.sp,
                            color = Color.Black.copy(alpha = 0.48f)
                        )
                    }

                    IconButton(onClick = { distanceKm = clamp(distanceKm + 0.5) }) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "增加")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                quick.forEach { km ->
                    AssistChip(
                        onClick = { distanceKm = km },
                        label = { Text(text = "${km.toInt()} km") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (distanceKm == km) primaryGreen.copy(alpha = 0.14f) else Color.White,
                            labelColor = if (distanceKm == km) primaryGreen else Color.Black.copy(alpha = 0.70f)
                        ),
                        border = AssistChipDefaults.assistChipBorder(
                            enabled = true,
                            borderColor = Color.Black.copy(alpha = 0.08f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = { onConfirm(distanceKm) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryGreen)
            ) {
                Text(text = "确认", fontWeight = FontWeight.SemiBold, color = Color.White)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TargetDurationBottomSheet(
    initialMinutes: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
    primaryGreen: Color
) {
    var minutes by rememberSaveable { mutableStateOf(initialMinutes) }
    val quick = remember { listOf(15, 30, 45, 60, 90) }

    fun clamp(v: Int): Int = v.coerceIn(5, 600)
    fun format(v: Int): String {
        val h = v / 60
        val m = v % 60
        return String.format("%02d:%02d", h, m)
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 20.dp)
        ) {
            Text(
                text = "设置目标时长",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black.copy(alpha = 0.86f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { minutes = clamp(minutes - 5) }) {
                        Icon(imageVector = Icons.Filled.Remove, contentDescription = "减少")
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = format(minutes),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black.copy(alpha = 0.86f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "支持 5 分钟步进",
                            fontSize = 12.sp,
                            color = Color.Black.copy(alpha = 0.48f)
                        )
                    }

                    IconButton(onClick = { minutes = clamp(minutes + 5) }) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "增加")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                quick.forEach { m ->
                    AssistChip(
                        onClick = { minutes = m },
                        label = { Text(text = format(m)) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (minutes == m) primaryGreen.copy(alpha = 0.14f) else Color.White,
                            labelColor = if (minutes == m) primaryGreen else Color.Black.copy(alpha = 0.70f)
                        ),
                        border = AssistChipDefaults.assistChipBorder(
                            enabled = true,
                            borderColor = Color.Black.copy(alpha = 0.08f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = { onConfirm(minutes) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryGreen)
            ) {
                Text(text = "确认", fontWeight = FontWeight.SemiBold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TargetCaloriesBottomSheet(
    initialKcal: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
    primaryGreen: Color
) {
    var kcal by rememberSaveable { mutableStateOf(initialKcal) }
    val quick = remember { listOf(100, 200, 300, 500, 800) }

    fun clamp(v: Int): Int = v.coerceIn(20, 5000)

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 20.dp)
        ) {
            Text(
                text = "设置目标消耗",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black.copy(alpha = 0.86f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { kcal = clamp(kcal - 20) }) {
                        Icon(imageVector = Icons.Filled.Remove, contentDescription = "减少")
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = kcal.toString(),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black.copy(alpha = 0.86f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "单位：千卡",
                            fontSize = 12.sp,
                            color = Color.Black.copy(alpha = 0.48f)
                        )
                    }

                    IconButton(onClick = { kcal = clamp(kcal + 20) }) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "增加")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                quick.forEach { v ->
                    AssistChip(
                        onClick = { kcal = v },
                        label = { Text(text = v.toString()) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (kcal == v) primaryGreen.copy(alpha = 0.14f) else Color.White,
                            labelColor = if (kcal == v) primaryGreen else Color.Black.copy(alpha = 0.70f)
                        ),
                        border = AssistChipDefaults.assistChipBorder(
                            enabled = true,
                            borderColor = Color.Black.copy(alpha = 0.08f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = { onConfirm(kcal) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryGreen)
            ) {
                Text(text = "确认", fontWeight = FontWeight.SemiBold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun RouteSelectBottomSheet(
    selectedRouteId: Int?,
    onDismiss: () -> Unit,
    onSelectRouteId: (Int) -> Unit
) {
    val primaryGreen = Color(0xFF00C56E)
    var sortIndex by rememberSaveable { mutableStateOf(0) }
    val sortLabels = remember { listOf("附近路线", "长度", "累计爬升", "地点类型") }

    var routes by remember { mutableStateOf<List<RouteBook>>(emptyList()) }

    LaunchedEffect(Unit) {
        val loaded = withContext(Dispatchers.IO) {
            val list = mutableListOf<RouteBook>()
            DatabaseHelper.processQuery(
                "SELECT route_id, title, distance_km, elevation_m, location, difficulty, cover_image_url FROM routes ORDER BY updated_at DESC LIMIT 200"
            ) { rs ->
                while (rs.next()) {
                    val id = rs.getInt(1)
                    val title = rs.getString(2)
                    val dist = rs.getDouble(3)
                    val elev = rs.getInt(4)
                    val loc = rs.getString(5) ?: ""
                    val diff = rs.getString(6) ?: "简单"
                    val img = rs.getString(7)
                    list.add(RouteBook(id, title, dist, elev, loc, emptyList(), R.drawable.ic_launcher_foreground, img, diff))
                }
                Unit
            }
            list
        }
        routes = loaded
    }

    val sortedRoutes = remember(routes, sortIndex) {
        when (sortIndex) {
            1 -> routes.sortedByDescending { it.distanceKm }
            2 -> routes.sortedByDescending { it.elevationM }
            3 -> routes.sortedBy { it.location }
            else -> routes
        }
    }

    val displayRoutes = remember(sortedRoutes, selectedRouteId) {
        val id = selectedRouteId ?: return@remember sortedRoutes
        val pinned = sortedRoutes.firstOrNull { it.id == id } ?: return@remember sortedRoutes
        listOf(pinned) + sortedRoutes.filter { it.id != id }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val sheetMaxHeight = maxHeight * 0.82f
        val interactionSource = remember { MutableInteractionSource() }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.32f))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onDismiss
                )
        )

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 12.dp)
                .padding(bottom = 96.dp)
                .fillMaxWidth()
                .heightIn(max = sheetMaxHeight)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                ),
            shape = RoundedCornerShape(22.dp),
            tonalElevation = 2.dp,
            shadowElevation = 10.dp,
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(top = 14.dp, bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "选择路书",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black.copy(alpha = 0.86f)
                    )
                    AssistChip(
                        onClick = {},
                        label = { Text(text = "我创建的") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = primaryGreen.copy(alpha = 0.12f),
                            labelColor = primaryGreen
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(sortLabels.size) { index ->
                        AssistChip(
                            onClick = { sortIndex = index },
                            label = { Text(text = sortLabels[index]) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (sortIndex == index) Color.Black.copy(alpha = 0.06f) else Color.White,
                                labelColor = Color.Black.copy(alpha = if (sortIndex == index) 0.86f else 0.64f)
                            ),
                            border = AssistChipDefaults.assistChipBorder(
                                enabled = true,
                                borderColor = Color.Black.copy(alpha = 0.08f)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = displayRoutes,
                        key = { it.id }
                    ) { route ->
                        RouteSelectRow(
                            route = route,
                            selected = selectedRouteId == route.id,
                            onClick = { onSelectRouteId(route.id) },
                            modifier = Modifier.animateItem(placementSpec = tween(180))
                        )
                    }
                    item { Spacer(modifier = Modifier.height(12.dp)) }
                }
            }
        }
    }
}

@Composable
private fun RouteSelectRow(
    route: RouteBook,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = route.coverImageUrl ?: "",
                contentDescription = route.title,
                modifier = Modifier
                    .size(width = 76.dp, height = 54.dp)
                    .background(Color.Black.copy(alpha = 0.04f), RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = route.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black.copy(alpha = 0.86f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${String.format("%.1f", route.distanceKm)} 公里 · 爬升 ${route.elevationM} 米 · ${route.location}",
                    fontSize = 12.sp,
                    color = Color.Black.copy(alpha = 0.56f)
                )
            }

            RadioButton(selected = selected, onClick = onClick)
        }
    }
}

@Composable
fun InProgressContent(
    duration: String,
    distance: String,
    currentSpeed: String,
    avgSpeed: String,
    maxSpeed: String,
    calories: String,
    elevation: String,
    onPauseClick: () -> Unit,
    onStopClick: () -> Unit,
    myLocation: LatLng?,
    accuracy: Float?,
    routePoints: List<LatLng>,
    mapSnapshotterState: androidx.compose.runtime.MutableState<(((Bitmap) -> Unit) -> Unit)?>? = null,
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
        avgSpeed = avgSpeed,
        maxSpeed = maxSpeed,
        calories = calories,
        elevation = elevation,
        highlightData = true,
        goalReached = RideGoalSession.active && RideGoalSession.reached,
        myLocation = myLocation,
        accuracy = accuracy,
        routePoints = routePoints,
        selectedLocation = null,
        mapSnapshotterState = mapSnapshotterState,
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
    avgSpeed: String,
    maxSpeed: String,
    calories: String,
    elevation: String,
    onResumeClick: () -> Unit,
    onStopClick: () -> Unit,
    myLocation: LatLng?,
    accuracy: Float?,
    routePoints: List<LatLng>,
    mapSnapshotterState: androidx.compose.runtime.MutableState<(((Bitmap) -> Unit) -> Unit)?>? = null,
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
        avgSpeed = avgSpeed,
        maxSpeed = maxSpeed,
        calories = calories,
        elevation = elevation,
        highlightData = false,
        goalReached = RideGoalSession.active && RideGoalSession.reached,
        myLocation = myLocation,
        accuracy = accuracy,
        routePoints = routePoints,
        selectedLocation = null,
        mapSnapshotterState = mapSnapshotterState,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RideWorkoutLayout(
    duration: String,
    distance: String,
    currentSpeed: String,
    avgSpeed: String,
    maxSpeed: String,
    calories: String,
    elevation: String,
    highlightData: Boolean,
    goalReached: Boolean,
    myLocation: LatLng?,
    accuracy: Float?,
    routePoints: List<LatLng>,
    selectedLocation: LatLng?,
    mapSnapshotterState: androidx.compose.runtime.MutableState<(((Bitmap) -> Unit) -> Unit)?>?,
    isLoading: Boolean,
    errorText: String?,
    primaryButton: @Composable RowScope.() -> Unit,
    secondaryButton: (@Composable RowScope.() -> Unit)?
) {
    val background = Color(0xFFF8F9FA)
    val navBarBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val sheetPeekHeight = 140.dp + navBarBottom
    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = true
    )
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)
    val coroutineScope = rememberCoroutineScope()
    val expanded by remember { derivedStateOf { sheetState.currentValue == SheetValue.Expanded } }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = sheetPeekHeight,
        sheetContainerColor = Color.White,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetContent = {
            RideMetricsSheet(
                duration = duration,
                distance = distance,
                currentSpeed = currentSpeed,
                avgSpeed = avgSpeed,
                maxSpeed = maxSpeed,
                calories = calories,
                elevation = elevation,
                highlight = highlightData,
                goalReached = goalReached,
                expanded = expanded,
                onToggle = {
                    coroutineScope.launch {
                        if (expanded) sheetState.partialExpand() else sheetState.expand()
                    }
                }
            )
        },
        containerColor = background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AMap2DContainer(
                modifier = Modifier.fillMaxSize(),
                myLocation = myLocation,
                accuracy = accuracy,
                routePoints = routePoints,
                selectedLocation = selectedLocation,
                snapshotterState = mapSnapshotterState
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

            val buttonBottomPadding = sheetPeekHeight + 12.dp
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = buttonBottomPadding)
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
            }

            AnimatedVisibility(
                visible = goalReached,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = buttonBottomPadding + 18.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF7EE))
                ) {
                    Text(
                        text = "目标已达成",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        fontSize = 13.sp,
                        color = Color(0xFF1E7E34),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            AnimatedVisibility(
                visible = !errorText.isNullOrBlank(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = buttonBottomPadding + 72.dp)
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
}

@Composable
private fun RideMetricsSheet(
    duration: String,
    distance: String,
    currentSpeed: String,
    avgSpeed: String,
    maxSpeed: String,
    calories: String,
    elevation: String,
    highlight: Boolean,
    goalReached: Boolean,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 14.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(width = 38.dp, height = 4.dp)
                    .background(Color(0xFFCED4DA), RoundedCornerShape(999.dp))
                    .clickable(onClick = onToggle)
            )
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            RideRealtimeDataCard(
                duration = duration,
                distance = distance,
                currentSpeed = currentSpeed,
                highlight = highlight,
                goalReached = goalReached
            )

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp)
                ) {
                    RideMetricsGridRow(
                        leftLabel = "均速",
                        leftValue = avgSpeed,
                        leftUnit = "km/h",
                        rightLabel = "最高速",
                        rightValue = maxSpeed,
                        rightUnit = "km/h"
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    RideMetricsGridRow(
                        leftLabel = "消耗",
                        leftValue = calories,
                        leftUnit = "kcal",
                        rightLabel = "爬升",
                        rightValue = elevation,
                        rightUnit = "m"
                    )
                }
            }
        }
    }
}

@Composable
private fun RideMetricsGridRow(
    leftLabel: String,
    leftValue: String,
    leftUnit: String,
    rightLabel: String,
    rightValue: String,
    rightUnit: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RideMetricsGridItem(
            label = leftLabel,
            value = leftValue,
            unit = leftUnit,
            modifier = Modifier.weight(1f)
        )
        RideMetricsGridItem(
            label = rightLabel,
            value = rightValue,
            unit = rightUnit,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RideMetricsGridItem(
    label: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF6C757D),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF212529),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = unit,
                    fontSize = 12.sp,
                    color = Color(0xFF6C757D),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
    highlight: Boolean,
    goalReached: Boolean
) {
    val borderColor = when {
        goalReached -> Color(0xFF28A745)
        highlight -> Color(0xFF007BFF)
        else -> Color(0xFFE9ECEF)
    }
    val containerColor = when {
        goalReached -> Color(0xFFF0FBF3)
        highlight -> Color(0xFFF2F7FF)
        else -> Color.White
    }

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
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.Center) {
            AnimatedContent(targetState = value, label = "ride-data") { v ->
                Text(
                    text = v,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF212529),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false
                )
            }
            if (unit.isNotEmpty()) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = unit,
                    fontSize = 13.sp,
                    color = Color(0xFF6C757D),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false
                )
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
