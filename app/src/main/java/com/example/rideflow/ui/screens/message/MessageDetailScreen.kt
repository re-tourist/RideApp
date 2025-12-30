package com.example.rideflow.ui.screens.message

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rideflow.message.MessageDetailViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageDetailScreen(
    navController: NavController,
    ownerId: String,
    peerId: String
) {
    if (ownerId.isBlank() || peerId.isBlank()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "会话") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "返回")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "参数缺失", color = Color.Gray)
            }
        }
        return
    }

    val vm: MessageDetailViewModel = koinViewModel(parameters = { parametersOf(ownerId, peerId) })
    val list by vm.messages.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var input by remember { mutableStateOf("") }

    LaunchedEffect(list.size) {
        if (list.isNotEmpty()) {
            listState.animateScrollToItem(list.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = vm.peerName()) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).imePadding()
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().background(Color(0xFFF7F7F7)),
                state = listState,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(list, key = { it.id }) { msg ->
                    val isMine = msg.senderId == ownerId
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
                    ) {
                        MessageBubble(text = msg.content, isMine = isMine)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(text = "输入留言") },
                    maxLines = 4
                )
                Spacer(modifier = Modifier.width(10.dp))
                TextButton(
                    onClick = {
                        val text = input
                        input = ""
                        vm.sendText(text)
                        scope.launch {
                            vm.markRead()
                        }
                    }
                ) {
                    Text(text = "发送")
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(text: String, isMine: Boolean) {
    val bg = if (isMine) Color(0xFFCCE5FF) else Color.White
    val fg = Color(0xFF222222)
    Box(
        modifier = Modifier
            .background(color = bg, shape = MaterialTheme.shapes.medium)
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .widthIn(max = 280.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = text, color = fg, textAlign = TextAlign.Start)
    }
}
