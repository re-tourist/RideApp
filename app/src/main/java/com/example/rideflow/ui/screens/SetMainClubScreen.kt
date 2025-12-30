package com.example.rideflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.os.Handler
import android.os.Looper
import androidx.navigation.NavController
import coil.compose.AsyncImage
import androidx.compose.foundation.Image
import com.example.rideflow.R
import com.example.rideflow.auth.AuthViewModel
import com.example.rideflow.backend.DatabaseHelper
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetMainClubScreen(
    navController: NavController
) {
    val handler = Handler(Looper.getMainLooper())
    val authViewModel = koinViewModel<AuthViewModel>()
    val currentUserId = authViewModel.getCurrentUser()?.userId?.toIntOrNull()

    var joinedClubs by remember { mutableStateOf<List<Club>>(emptyList()) }
    var currentMainClub by remember { mutableStateOf<Club?>(null) }
    var selectedClubId by remember { mutableStateOf<Int?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(currentUserId) {
        val uid = currentUserId
        if (uid != null && uid > 0) {
            Thread {
                var mainClubId: Int? = null
                DatabaseHelper.processQuery(
                    "SELECT main_club_id FROM rider_profiles WHERE user_id = ?",
                    listOf(uid)
                ) { rs ->
                    if (rs.next()) {
                        val id = rs.getInt(1)
                        mainClubId = if (rs.wasNull()) null else id
                    }
                    Unit
                }

                val list = mutableListOf<Club>()
                DatabaseHelper.processQuery(
                    "SELECT c.club_id, c.name, c.city, c.logo_url, c.members_count, c.heat FROM club_members m JOIN clubs c ON m.club_id = c.club_id WHERE m.user_id = ? ORDER BY c.heat DESC",
                    listOf(uid)
                ) { rs ->
                    while (rs.next()) {
                        val id = rs.getInt(1)
                        val name = rs.getString(2) ?: "俱乐部"
                        val city = rs.getString(3) ?: ""
                        val logo = rs.getString(4)
                        val members = rs.getInt(5)
                        val heat = rs.getInt(6)
                        list.add(Club(id, name, city, members, heat, R.drawable.ic_launcher_foreground, logo))
                    }
                    Unit
                }

                handler.post {
                    joinedClubs = list
                    val mcid = mainClubId
                    currentMainClub = if (mcid != null) list.firstOrNull { it.id == mcid } else null
                    selectedClubId = mcid
                    isLoading = false
                }
            }.start()
        } else {
            isLoading = false
        }
    }

    fun setMainClub(targetClubId: Int?) {
        val uid = currentUserId
        val cid = targetClubId
        if (uid == null || uid <= 0 || cid == null) return
        Thread {
            val updated = DatabaseHelper.executeUpdate(
                "INSERT INTO rider_profiles (user_id, main_club_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE main_club_id = VALUES(main_club_id)",
                listOf(uid, cid)
            )
            if (updated >= 0) {
                val club = joinedClubs.firstOrNull { it.id == cid }
                handler.post {
                    currentMainClub = club
                    selectedClubId = cid
                    showSuccessDialog = true
                }
            }
        }.start()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "设置主俱乐部") },
                navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "返回"
                )
            }
        }
            )
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "设置主俱乐部后，您上传轨迹所产生的热度值将导入主俱乐部中。",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // 主俱乐部部分
                Text(
                    text = "主俱乐部",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // 主俱乐部选择区域
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .clickable {
                            if (selectedClubId != null) {
                                setMainClub(selectedClubId)
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentMainClub != null) {
                        if (!currentMainClub!!.logoUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = currentMainClub!!.logoUrl,
                                contentDescription = currentMainClub!!.name,
                                modifier = Modifier.size(48.dp),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = currentMainClub!!.logoRes),
                                contentDescription = currentMainClub!!.name,
                                modifier = Modifier.size(48.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = currentMainClub!!.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        // 未设置主俱乐部时显示加号
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFFE0E0E0), shape = MaterialTheme.shapes.small),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                contentDescription = "设置主俱乐部",
                                modifier = Modifier.size(24.dp),
                                tint = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // 提示文字
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (currentMainClub != null) "已设置为主俱乐部" else "点击设置主俱乐部",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    // 右箭头
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "进入设置",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // 我的俱乐部部分
                Text(
                    text = "我的俱乐部",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        items(joinedClubs) { club ->
                            ClubItem(
                                club = club,
                                isSelected = selectedClubId == club.id,
                                onSelect = { selectedClubId = club.id }
                            )
                            Divider()
                        }
                    }
                }

                Button(
                    onClick = {
                        if (selectedClubId != null) {
                            setMainClub(selectedClubId)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    enabled = selectedClubId != null
                ) {
                    Text(text = "设置为主俱乐部")
                }
            }
        }
    }

    // 成功提示对话框
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text(text = "设置成功") },
            text = { Text(text = "主俱乐部已设置成功！") },
            confirmButton = {
                Button(onClick = { 
                    showSuccessDialog = false
                    navController.popBackStack()
                }) {
                    Text(text = "确定")
                }
            }
        )
    }
}

@Composable
fun ClubItem(
    club: Club,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable(onClick = onSelect),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!club.logoUrl.isNullOrBlank()) {
            AsyncImage(
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

        Spacer(modifier = Modifier.width(12.dp))

        // 俱乐部信息
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = club.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = club.city,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        // 单选按钮
        RadioButton(
            selected = isSelected,
            onClick = onSelect
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SetMainClubScreenPreview() {
    SetMainClubScreen(navController = androidx.navigation.compose.rememberNavController())
}
