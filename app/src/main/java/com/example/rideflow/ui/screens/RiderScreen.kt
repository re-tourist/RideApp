package com.example.rideflow.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rideflow.R
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.rideflow.backend.DatabaseHelper
import android.os.Handler
import android.os.Looper
import coil.compose.AsyncImage

data class Rider(
    val id: Int,
    val name: String,
    val city: String,
    val level: String,
    val avatarRes: Int,
    val avatarUrl: String? = null
)

@Composable
private fun loadRiders(userId: String): Pair<List<Rider>, List<Rider>> {
    val handler = Handler(Looper.getMainLooper())
    var nearby by remember { mutableStateOf<List<Rider>>(emptyList()) }
    var mine by remember { mutableStateOf<List<Rider>>(emptyList()) }
    LaunchedEffect(userId) {
        Thread {
            val nlist = mutableListOf<Rider>()
            DatabaseHelper.processQuery(
                "SELECT u.user_id, u.nickname, rp.city, rp.level, u.avatar_url, rp.main_club_id FROM rider_profiles rp JOIN users u ON rp.user_id = u.user_id ORDER BY u.user_id"
            ) { rs ->
                val clubMap = mutableMapOf<Int, Int>()
                while (rs.next()) {
                    val id = rs.getInt(1)
                    val name = rs.getString(2)
                    val city = rs.getString(3) ?: ""
                    val level = rs.getString(4) ?: "普通"
                    val avatar = rs.getString(5)
                    val clubId = rs.getInt(6)
                    clubMap[id] = clubId
                    nlist.add(Rider(id, name, city, level, R.drawable.ic_launcher_foreground, avatar))
                }
                val uid = userId.toIntOrNull()
                val myClub = if (uid != null) clubMap[uid] else null
                val mlist = if (myClub != null && myClub > 0) nlist.filter { rider -> clubMap[rider.id] == myClub } else nlist.filter { rider -> rider.id.toString() == userId }
                handler.post { nearby = nlist; mine = mlist }
                Unit
            }
        }.start()
    }
    return Pair(nearby, mine)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiderScreen(onBack: () -> Unit, userId: String = "") {
    var selectedTab by remember { mutableStateOf(0) }
    val (nearbyRiders, myRiders) = loadRiders(userId)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "骑友") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text(text = "附近") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text(text = "我的") })
            }
            val data = if (selectedTab == 0) nearbyRiders else myRiders
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp)
            ) {
                items(data) { rider ->
                    RiderRow(rider)
                    Divider()
                }
            }
        }
    }
}

@Composable
private fun RiderRow(rider: Rider) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (rider.avatarUrl != null) {
                AsyncImage(model = rider.avatarUrl, contentDescription = rider.name, modifier = Modifier.size(48.dp))
            } else {
                Image(painter = painterResource(id = rider.avatarRes), contentDescription = rider.name, modifier = Modifier.size(48.dp))
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(text = rider.name, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Row {
                    Text(text = rider.city, fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = rider.level, fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RiderScreenPreview() {
    RiderScreen(onBack = {})
}

