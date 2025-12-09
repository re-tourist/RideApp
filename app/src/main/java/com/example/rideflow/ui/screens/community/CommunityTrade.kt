package com.example.rideflow.ui.screens.community

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rideflow.backend.DatabaseHelper
import com.example.rideflow.model.TradeItem
import com.example.rideflow.ui.components.TradePostCard

@Composable
fun CommunityTradeScreen() {
    val tabs = listOf("二手交易", "官方售卖")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val tradeItems = remember { mutableStateListOf<TradeItem>() }
    val handler = Handler(Looper.getMainLooper())

    // 加载交易数据
    LaunchedEffect(Unit) {
        Thread {
            val list = mutableListOf<TradeItem>()
            DatabaseHelper.processQuery("SELECT item_id, is_official, title, description, price, image_url, external_url, seller_user_id, category, is_published, created_at FROM trade_items ORDER BY created_at DESC LIMIT 200") { rs ->
                while (rs.next()) {
                    val id = rs.getInt(1)
                    val off = rs.getInt(2) == 1
                    val title = rs.getString(3) ?: ""
                    val desc = rs.getString(4) ?: ""
                    val price = rs.getBigDecimal(5)?.toPlainString() ?: "0"
                    val img = rs.getString(6) ?: "[图片]"
                    val url = rs.getString(7) ?: ""
                    val sellerId = rs.getInt(8)
                    val cat = rs.getString(9) ?: ""
                    val pub = rs.getInt(10) == 1
                    var sellerName: String? = null
                    if (sellerId > 0) {
                        DatabaseHelper.processQuery("SELECT nickname FROM users WHERE user_id = ?", listOf(sellerId)) { urs ->
                            if (urs.next()) sellerName = urs.getString(1)
                            Unit
                        }
                    }
                    list.add(TradeItem(id, off, title, desc, "¥ ${price}", img, url, sellerName, pub))
                }
                handler.post { tradeItems.clear(); tradeItems.addAll(list) }
                Unit
            }
        }.start()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex, containerColor = Color.White) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title, color = if (selectedTabIndex == index) Color.Red else Color.Black) }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        when (tabs[selectedTabIndex]) {
            "二手交易" -> SecondHandMarketScreen(tradeItems)
            "官方售卖" -> OfficialStoreScreen(tradeItems)
        }
    }
}

@Composable
private fun SecondHandMarketScreen(allTradeItems: List<TradeItem>) {
    val secondhandItems = allTradeItems.filter { !it.isOfficial && it.isPublished }
    var searchText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("搜索二手商品关键词...") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "搜索") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize().weight(1f),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(secondhandItems, key = { it.id }) { item ->
                TradePostCard(item = item) { /* 点击详情逻辑 */ }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }

        Button(
            onClick = { /* 模拟发布弹窗 */ },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f))
        ) {
            Text("发布二手交易链接")
        }
    }
}

@Composable
private fun OfficialStoreScreen(allTradeItems: List<TradeItem>) {
    val officialItems = allTradeItems.filter { it.isOfficial }
    val categories = listOf("骑行服", "配件", "整车", "其他")
    var selectedCategory by remember { mutableStateOf("骑行服") }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            categories.forEach { category ->
                Text(
                    text = category,
                    color = if (category == selectedCategory) Color.Red else Color.Gray,
                    fontWeight = if (category == selectedCategory) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.clickable { selectedCategory = category }
                )
            }
        }
        HorizontalDivider()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            val filteredItems = officialItems.filter {
                it.description.contains(selectedCategory) || selectedCategory == "配件" // 简化逻辑保留原样
            }
            items(filteredItems, key = { it.id }) { item ->
                TradePostCard(item = item) { /* 点击详情 */ }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}