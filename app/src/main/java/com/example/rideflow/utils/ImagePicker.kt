package com.example.rideflow.utils

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.MutableState
import java.io.IOException

/**
 * 图片选择器工具类
 * 提供相册选择功能
 * @param context 上下文
 * @param selectedImageUri 选中的图片Uri状态
 * @param selectedImageBitmap 选中的图片Bitmap状态
 * @return 包含相册选项的Pair，选项是(显示文本, 点击事件)
 */
fun ImagePicker(
    context: Context,
    selectedImageUri: MutableState<Uri?>,
    selectedImageBitmap: MutableState<Bitmap?>
): Pair<String, () -> Unit> {
    // 相册选项
    val galleryOption = "相册" to { ->
        val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickPhotoIntent.type = "image/*"
        pickPhotoIntent.resolveActivity(context.packageManager)?.let {
            // 同样需要ActivityResultLauncher来处理相册结果
        }
        Unit
    }
    
    return galleryOption
}

/**
 * 从Uri获取Bitmap
 * @param context 上下文
 * @param uri 图片Uri
 * @return Bitmap对象
 */
fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT < 28) {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        } else {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        }
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

/**
 * 创建相册选择的Intent
 * @return 相册Intent
 */
fun createGalleryIntent(): Intent {
    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    intent.type = "image/*"
    return intent
}
