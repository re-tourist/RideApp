package com.example.rideflow.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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

data class Club(
    val id: Int,
    val name: String,
    val city: String,
    val members: Int,
    val heat: Int,
    val logoRes: Int,
    val logoUrl: String? = null
)

private fun loadClubs(handler: Handler, onLoaded: (List<Club>) -> Unit) {
    Thread {
        val list = mutableListOf<Club>()
        DatabaseHelper.processQuery("SELECT club_id, name, city, logo_url, members_count, heat FROM clubs ORDER BY heat DESC LIMIT 100") { rs ->
            while (rs.next()) {
                val id = rs.getInt(1)
                val name = rs.getString(2)
                val city = rs.getString(3) ?: ""
                val logo = rs.getString(4)
                val members = rs.getInt(5)
                val heat = rs.getInt(6)
                list.add(Club(id, name, city, members, heat, R.drawable.ic_launcher_foreground, logo))
            }
            handler.post { onLoaded(list) }
            Unit
        }
    }.start()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubScreen(onBack: () -> Unit, navController: androidx.navigation.NavController) {
    var search by remember { mutableStateOf("") }
    val handler = Handler(Looper.getMainLooper())
    var clubs by remember { mutableStateOf<List<Club>>(emptyList()) }
    LaunchedEffect(Unit) { loadClubs(handler) { list -> clubs = list } }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "俱乐部") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(com.example.rideflow.navigation.AppRoutes.CREATE_CLUB) }) { Icon(Icons.Filled.Add, contentDescription = "添加") }
                    IconButton(onClick = { navController.navigate(com.example.rideflow.navigation.AppRoutes.SET_MAIN_CLUB) }) { Icon(Icons.Filled.Settings, contentDescription = "设置") }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(12.dp)
        ) {
            item {
                Surface(color = Color(0xFF2196F3), shape = MaterialTheme.shapes.small) {
                    Text(
                        text = "你还没有设置主俱乐部，请前往进行设置！",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(text = "搜索编号、名称、关键字") }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            item { SectionTitle(text = "热门俱乐部") }
            items(clubs) { club ->
                ClubRow(club) { navController.navigate("club_detail/${club.id}") }
                Divider()
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = "查看更多", color = Color(0xFF007AFF), fontSize = 12.sp)
    }
}

@Composable
private fun ClubRow(club: Club, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (club.logoUrl != null) {
                coil.compose.AsyncImage(
                    model = club.logoUrl,
                    contentDescription = club.name,
                    modifier = Modifier.size(48.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = club.logoRes),
                    contentDescription = club.name,
                    modifier = Modifier.size(48.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(text = club.name, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Row {
                    Text(text = club.city, fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "${club.members}人", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = "奖杯")
            Text(text = "x${club.heat}", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(start = 6.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ClubScreenPreview() {
    ClubScreen(onBack = {}, navController = androidx.navigation.compose.rememberNavController())
}

