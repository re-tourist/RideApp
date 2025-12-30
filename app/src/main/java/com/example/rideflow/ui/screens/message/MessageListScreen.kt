package com.example.rideflow.ui.screens.message

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rideflow.message.MessageListViewModel
import com.example.rideflow.navigation.AppRoutes
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageListScreen(
    navController: NavController,
    ownerId: String
) {
    if (ownerId.isBlank()) {
        Scaffold(
            topBar = { TopAppBar(title = { Text(text = "消息") }) }
        ) { innerPadding ->
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "请先登录", color = Color.Gray)
            }
        }
        return
    }

    val vm: MessageListViewModel = koinViewModel(parameters = { parametersOf(ownerId) })
    val conversations by vm.conversations.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "消息") },
                actions = {
                    IconButton(onClick = { vm.simulateIncoming() }) {
                        Icon(imageVector = Icons.Filled.Refresh, contentDescription = "模拟收信")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(conversations, key = { it.peerId }) { item ->
                ConversationCard(
                    name = item.peerName,
                    lastMessage = item.lastMessage,
                    lastTime = formatTime(item.lastTime),
                    unread = item.unreadCount,
                    onClick = { navController.navigate("${AppRoutes.MESSAGE_DETAIL}/${item.peerId}") }
                )
            }
        }
    }
}

@Composable
private fun ConversationCard(
    name: String,
    lastMessage: String,
    lastTime: String,
    unread: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val initial = name.trim().firstOrNull()?.toString().orEmpty()
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(Color(0xFFE7F0FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = initial, fontWeight = FontWeight.SemiBold, color = Color(0xFF1D5FD3))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = name,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = lastTime, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = lastMessage,
                    color = Color(0xFF555555),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            if (unread > 0) {
                BadgedBox(badge = { Badge { Text(text = unread.coerceAtMost(99).toString()) } }) {
                    Box(modifier = Modifier.size(1.dp))
                }
            }
        }
    }
}

private fun formatTime(ts: Long): String {
    val now = System.currentTimeMillis()
    val sameDay = now / 86_400_000L == ts / 86_400_000L
    val pattern = if (sameDay) "HH:mm" else "MM-dd"
    return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(ts))
}
