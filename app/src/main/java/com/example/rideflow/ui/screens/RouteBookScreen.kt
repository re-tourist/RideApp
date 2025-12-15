package com.example.rideflow.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.amap.api.maps2d.model.LatLng
import com.amap.api.services.help.Inputtips
import com.amap.api.services.help.InputtipsQuery
import com.amap.api.services.help.Tip
import com.google.android.gms.location.LocationServices
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.RouteSearch
import com.amap.api.services.route.RideRouteResult
import com.amap.api.services.route.WalkRouteResult
import android.os.Handler
import android.os.Looper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteBookScreen(onBackToMain: () -> Unit, onShowHistory: () -> Unit, onStartRide: () -> Unit) {
    val context = LocalContext.current
    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val currentLocation = remember { mutableStateOf<LatLng?>(null) }

    val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        val granted = (result[Manifest.permission.ACCESS_FINE_LOCATION] == true) || (result[Manifest.permission.ACCESS_COARSE_LOCATION] == true)
        if (granted) {
            fusedClient.lastLocation.addOnSuccessListener { loc ->
                currentLocation.value = if (loc != null) LatLng(loc.latitude, loc.longitude) else LatLng(30.311664, 120.394605)
            }.addOnFailureListener {
                currentLocation.value = LatLng(30.311664, 120.394605)
            }
        } else {
            currentLocation.value = LatLng(30.311664, 120.394605)
        }
    }

    LaunchedEffect(Unit) {
        val hasPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!hasPermission) {
            permissionLauncher.launch(permissions)
        } else {
            fusedClient.lastLocation.addOnSuccessListener { loc ->
                currentLocation.value = if (loc != null) LatLng(loc.latitude, loc.longitude) else LatLng(30.311664, 120.394605)
            }.addOnFailureListener {
                currentLocation.value = LatLng(30.311664, 120.394605)
            }
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    val searchResults = remember { mutableStateListOf<Tip>() }
    val selectedPlace = remember { mutableStateOf<LatLng?>(null) }
    var showSearchOverlay by remember { mutableStateOf(false) }
    val routePoints = remember { mutableStateListOf<LatLng>() }
    val mainHandler = remember { Handler(Looper.getMainLooper()) }

    fun calculateRoute(from: LatLng?, to: LatLng?) {
        if (from == null || to == null) return
        try {
            val routeSearch = RouteSearch(context)
            val fromAndTo = RouteSearch.FromAndTo(LatLonPoint(from.latitude, from.longitude), LatLonPoint(to.latitude, to.longitude))
            val rideQuery = RouteSearch.RideRouteQuery(fromAndTo, RouteSearch.RidingDefault)
            val walkQuery = RouteSearch.WalkRouteQuery(fromAndTo, RouteSearch.WalkDefault)

            fun updatePointsSafely(points: List<LatLng>) {
                mainHandler.post {
                    routePoints.clear()
                    routePoints.addAll(points)
                }
            }

            fun fallbackWalk() {
                routeSearch.calculateWalkRouteAsyn(walkQuery)
            }

            routeSearch.setRouteSearchListener(object : RouteSearch.OnRouteSearchListener {
                override fun onRideRouteSearched(result: RideRouteResult?, code: Int) {
                    try {
                        if (code == 1000) {
                            val path = result?.paths?.firstOrNull()
                            val steps = path?.steps
                            if (steps != null && steps.isNotEmpty()) {
                                val temp = mutableListOf<LatLng>()
                                steps.forEach { step ->
                                    val pts = step.polyline
                                    pts?.forEach { p -> temp.add(LatLng(p.latitude, p.longitude)) }
                                }
                                if (temp.size >= 2) {
                                    updatePointsSafely(temp)
                                    return
                                }
                            }
                        }
                        fallbackWalk()
                    } catch (e: Exception) {
                        fallbackWalk()
                    }
                }
                override fun onWalkRouteSearched(result: WalkRouteResult?, code: Int) {
                    try {
                        if (code == 1000) {
                            val path = result?.paths?.firstOrNull()
                            val steps = path?.steps
                            if (steps != null && steps.isNotEmpty()) {
                                val temp = mutableListOf<LatLng>()
                                steps.forEach { step ->
                                    val pts = step.polyline
                                    pts?.forEach { p -> temp.add(LatLng(p.latitude, p.longitude)) }
                                }
                                if (temp.size >= 2) {
                                    updatePointsSafely(temp)
                                    return
                                }
                            }
                        }
                        val fallback = listOf(from, to)
                        updatePointsSafely(fallback)
                    } catch (e: Exception) {
                        val fallback = listOf(from, to)
                        updatePointsSafely(fallback)
                    }
                }
                override fun onDriveRouteSearched(p0: com.amap.api.services.route.DriveRouteResult?, p1: Int) {}
                override fun onBusRouteSearched(p0: com.amap.api.services.route.BusRouteResult?, p1: Int) {}
            })

            routeSearch.calculateRideRouteAsyn(rideQuery)
        } catch (e: Exception) {
            mainHandler.post {
                routePoints.clear()
                routePoints.addAll(listOf(from, to))
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { ModeToggleRoute(activeIsSport = false, onClick = onBackToMain) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF007AFF)),
                actions = {
                    IconButton(onClick = onShowHistory) { Icon(imageVector = Icons.Filled.DateRange, contentDescription = "骑行记录", tint = Color.White) }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Box(modifier = Modifier.fillMaxWidth().clickable { showSearchOverlay = true }) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            enabled = false,
                            placeholder = { Text("输入关键词") }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    AMap2DContainer(
                        modifier = Modifier.fillMaxSize(),
                        myLocation = currentLocation.value,
                        routePoints = routePoints,
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
                        Text("经度: ${currentLocation.value?.longitude?.let { String.format("%.6f", it) } ?: "定位中"}", fontSize = 14.sp, color = Color.Gray)
                        Text("纬度: ${currentLocation.value?.latitude?.let { String.format("%.6f", it) } ?: "定位中"}", fontSize = 14.sp, color = Color.Gray)
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
                onClick = onStartRide,
                modifier = Modifier.size(width = 240.dp, height = 56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF))
            ) { Text("开始运动", fontSize = 18.sp, color = Color.White) }
        }
        }
    }

    if (showSearchOverlay) {
        AlertDialog(
            onDismissRequest = { showSearchOverlay = false },
            confirmButton = { TextButton(onClick = { showSearchOverlay = false }) { Text("关闭") } },
            title = { Text("地点查询") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    var overlayQuery by remember { mutableStateOf("") }
                    val overlayResults = remember { mutableStateListOf<Tip>() }
                    TextField(
                        value = overlayQuery,
                        onValueChange = { overlayQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("输入关键词") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LaunchedEffect(overlayQuery) {
                        val q = overlayQuery.trim()
                        overlayResults.clear()
                        if (q.isNotEmpty()) {
                            val query = InputtipsQuery(q, "")
                            query.cityLimit = false
                            val inputtips = Inputtips(context, query)
                            inputtips.setInputtipsListener { tips, code ->
                                overlayResults.clear()
                                if (code == 1000 && tips != null) overlayResults.addAll(tips)
                            }
                            inputtips.requestInputtipsAsyn()
                        }
                    }
                    LazyColumn(modifier = Modifier.fillMaxWidth().height(240.dp)) {
                        items(overlayResults) { tip ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val p = tip.point
                                        if (p != null) {
                                            selectedPlace.value = LatLng(p.latitude, p.longitude)
                                            searchQuery = tip.name
                                            calculateRoute(currentLocation.value, selectedPlace.value)
                                        }
                                        showSearchOverlay = false
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
    LaunchedEffect(selectedPlace.value, currentLocation.value) {
        calculateRoute(currentLocation.value, selectedPlace.value)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteBookScreen(onBack: () -> Unit, onOpenMyRouteBook: () -> Unit, userId: String) {
    RouteBookScreen(onBackToMain = onBack, onShowHistory = {}, onStartRide = {})
}

@Composable
private fun ModeToggleRoute(activeIsSport: Boolean, onClick: () -> Unit) {
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
