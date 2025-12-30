package com.example.rideflow.ui.screens.community

import android.os.Handler
import android.os.Looper
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.rideflow.backend.DatabaseHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeDetailScreen(navController: NavController, itemId: Int) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var title by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var externalUrl by remember { mutableStateOf("") }
    var sellerName by remember { mutableStateOf<String?>(null) }
    var isOfficial by remember { mutableStateOf(false) }
    val handler = Handler(Looper.getMainLooper())

    LaunchedEffect(itemId) {
        Thread {
            DatabaseHelper.processQuery(
                "SELECT is_official, title, description, price, image_url, external_url, seller_user_id FROM trade_items WHERE item_id = ? LIMIT 1",
                listOf(itemId)
            ) { rs ->
                if (rs.next()) {
                    val off = rs.getInt(1) == 1
                    val t = rs.getString(2) ?: ""
                    val d = rs.getString(3) ?: ""
                    val p = rs.getBigDecimal(4)?.toPlainString() ?: "0"
                    val img = rs.getString(5) ?: ""
                    val url = rs.getString(6) ?: ""
                    val sellerId = rs.getInt(7)
                    var seller: String? = null
                    if (sellerId > 0) {
                        DatabaseHelper.processQuery("SELECT nickname FROM users WHERE user_id = ?", listOf(sellerId)) { urs ->
                            if (urs.next()) seller = urs.getString(1)
                            Unit
                        }
                    }
                    handler.post {
                        isOfficial = off
                        title = t
                        description = d
                        price = "¥ $p"
                        imageUrl = img
                        externalUrl = url
                        sellerName = seller
                    }
                }
                Unit
            }
        }.start()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "交易详情") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(text = title, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = price, color = Color.Red, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                if (!isOfficial && !sellerName.isNullOrBlank()) {
                    Text(text = "发布者：$sellerName", color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Text(text = description, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(12.dp))
                if (externalUrl.isNotBlank()) {
                    val normalizedUrl = remember(externalUrl) { normalizeLinkUrl(externalUrl) }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = externalUrl.trim(),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    val toOpen = normalizedUrl
                                    if (toOpen.isNullOrBlank()) {
                                        Toast.makeText(context, "链接无效", Toast.LENGTH_SHORT).show()
                                    } else {
                                        openExternalLink(context, toOpen)
                                    }
                                }
                        )

                        IconButton(
                            onClick = {
                                val toCopy = normalizedUrl ?: externalUrl.trim()
                                if (toCopy.isNotBlank()) {
                                    clipboardManager.setText(AnnotatedString(toCopy))
                                    Toast.makeText(context, "已复制链接", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "复制链接")
                        }
                    }
                }
            }
        }
    }
}

private fun normalizeLinkUrl(raw: String): String? {
    val trimmed = raw.trim()
    if (trimmed.isBlank()) return null

    val hasScheme = try {
        !Uri.parse(trimmed).scheme.isNullOrBlank()
    } catch (_: Exception) {
        false
    }

    if (hasScheme) return trimmed
    if (trimmed.startsWith("www.", ignoreCase = true)) return "https://$trimmed"
    if (trimmed.contains('.')) return "https://$trimmed"
    return null
}

private fun openExternalLink(context: android.content.Context, url: String) {
    val uri = try {
        Uri.parse(url)
    } catch (_: Exception) {
        Toast.makeText(context, "链接无效", Toast.LENGTH_SHORT).show()
        return
    }

    val intent = Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(context, "未安装可打开该链接的应用", Toast.LENGTH_SHORT).show()
    } catch (_: Exception) {
        Toast.makeText(context, "链接无效", Toast.LENGTH_SHORT).show()
    }
}
