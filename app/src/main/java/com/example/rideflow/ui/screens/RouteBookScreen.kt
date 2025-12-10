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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("骑迹", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF007AFF)),
                actions = {
                    IconButton(onClick = onShowHistory) { Icon(imageVector = Icons.Filled.DateRange, contentDescription = "骑行记录", tint = Color.White) }
                    ModeToggleRoute(activeIsSport = false, onClick = onBackToMain)
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
                    Box {
                        TextField(
                            value = searchQuery,
                            onValueChange = { },
                            modifier = Modifier.fillMaxWidth().clickable { showSearchOverlay = true },
                            readOnly = true,
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
