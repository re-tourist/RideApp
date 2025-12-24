package com.example.rideflow.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rideflow.R
import com.example.rideflow.utils.ImageUploadUtils
import coil.compose.AsyncImage
import java.io.File

/**
 * 图片上传组件
 * @param selectedImage 当前选中的图片文件
 * @param onImageSelected 图片选择回调
 * @param placeholderIcon 占位图标资源ID
 * @param isCircular 是否使用圆形裁剪
 */
@Composable
fun ImageUploadComponent(
    selectedImage: File?, 
    onImageSelected: (File) -> Unit,
    placeholderIcon: Int = R.drawable.ic_launcher_foreground,
    isCircular: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current


    // 相册启动器
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                try {
                    val inputStream = context.contentResolver.openInputStream(it)
                    val tempFile = ImageUploadUtils.createImageFile(context)
                    val outputStream = tempFile.outputStream()
                    
                    inputStream?.use { input ->
                        outputStream.use { output ->
                            input.copyTo(output)
                        }
                    }
                    
                    onImageSelected(tempFile)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    )



    // 图片上传区域
    Box(
        modifier = modifier
            .clip(if (isCircular) CircleShape else RoundedCornerShape(12.dp))
            .background(Color.LightGray)
            .border(
                width = 2.dp,
                color = Color.Gray,
                shape = if (isCircular) CircleShape else RoundedCornerShape(12.dp)
            )
            .clickable { 
                galleryLauncher.launch("image/*")
            }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (selectedImage != null) {
                AsyncImage(
                    model = selectedImage,
                    contentDescription = "上传的图片",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(if (isCircular) CircleShape else RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加图片",
                    modifier = Modifier.size(64.dp),
                    tint = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (selectedImage != null) "点击更换图片" else "点击上传图片",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}
