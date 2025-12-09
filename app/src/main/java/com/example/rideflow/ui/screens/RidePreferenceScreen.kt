package com.example.rideflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.example.rideflow.backend.DatabaseHelper

data class OptionItem(val id: Int, val name: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RidePreferenceScreen(navController: NavController, userId: String) {
    val bg = Color(0xFFE8F5E9)
    val sections = remember { mutableStateListOf<Pair<String, List<OptionItem>>>() }
    val selected = remember { mutableStateMapOf<String, SnapshotStateList<OptionItem>>() }
    val context = LocalContext.current
    val handler = Handler(Looper.getMainLooper())
    Thread {
        val map = linkedMapOf<String, MutableList<OptionItem>>()
        DatabaseHelper.processQuery(
            "SELECT c.category_name, o.option_id, o.option_name FROM ride_preference_options o JOIN ride_preference_categories c ON o.category_id = c.category_id ORDER BY c.display_order, o.display_order"
        ) { rs ->
            while (rs.next()) {
                val cn = rs.getString(1)
                val oid = rs.getInt(2)
                val on = rs.getString(3)
                val list = map.getOrPut(cn) { mutableListOf() }
                list.add(OptionItem(oid, on))
            }
            Unit
        }
        val uid = userId.toIntOrNull()
        val pre = mutableSetOf<Int>()
        if (uid != null) {
            DatabaseHelper.processQuery(
                "SELECT option_id FROM user_ride_preferences WHERE user_id = ?",
                listOf(uid)
            ) { rs ->
                while (rs.next()) {
                    pre.add(rs.getInt(1))
                }
                Unit
            }
        }
        handler.post {
            sections.clear()
            map.forEach { (k, v) ->
                sections.add(k to v)
                val list = selected.getOrPut(k) { mutableStateListOf() }
                if (pre.isNotEmpty()) {
                    v.forEach { if (pre.contains(it.id)) list.add(it) }
                }
            }
        }
    }.start()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "骑行偏好") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("${com.example.rideflow.navigation.AppRoutes.MAIN}?tab=profile") }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    Button(
                    onClick = { navController.navigate("${com.example.rideflow.navigation.AppRoutes.MAIN}?tab=profile") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = bg,
                            contentColor = Color.Black
                        )
                    ) { Text(text = "跳过") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bg)
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bg)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                val count = selected.values.sumOf { it.size }
                Button(
                    onClick = {
                        val uid = userId.toIntOrNull()
                        if (uid == null) {
                            Toast.makeText(context, "用户ID无效", Toast.LENGTH_SHORT).show()
                        } else {
                            Thread {
                                DatabaseHelper.executeUpdate(
                                    "DELETE FROM user_ride_preferences WHERE user_id = ?",
                                    listOf(uid)
                                )
                                selected.values.forEach { list ->
                                    list.forEach { opt ->
                                        DatabaseHelper.executeUpdate(
                                            "INSERT IGNORE INTO user_ride_preferences(user_id, option_id) VALUES (?, ?)",
                                            listOf(uid, opt.id)
                                        )
                                    }
                                }
                                handler.post { navController.popBackStack() }
                            }.start()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF66BB6A)),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(text = "我选好了（已选${count}个）", fontSize = 16.sp)
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(bg)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sections) { section ->
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color(0xCCFFFFFF)),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(text = section.first, fontSize = 18.sp, color = Color(0xFF2E7D32), modifier = Modifier.padding(12.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRowSection(
                            labels = section.second,
                            selected = selected.getOrPut(section.first) { mutableStateListOf() }
                        )
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun FlowRowSection(labels: List<OptionItem>, selected: SnapshotStateList<OptionItem>) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        labels.forEach { label ->
            val isSelected = label in selected
            FilterChip(
                selected = isSelected,
                onClick = {
                    if (isSelected) selected.remove(label) else selected.add(label)
                },
                leadingIcon = if (isSelected) {
                    { Icon(imageVector = Icons.Filled.Check, contentDescription = null, tint = Color.White) }
                } else null,
                label = { Text(text = label.name) },
                colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                    containerColor = Color(0xFFF1F8E9),
                    labelColor = Color.Black,
                    iconColor = Color.Black,
                    selectedContainerColor = Color(0xFF66BB6A),
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White
                ),
                border = androidx.compose.material3.FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = Color(0xFFBDBDBD),
                    selectedBorderColor = Color(0xFF2E7D32)
                )
            )
        }
    }
}
