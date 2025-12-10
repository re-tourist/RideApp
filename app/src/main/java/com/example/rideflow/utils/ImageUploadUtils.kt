package com.example.rideflow.utils

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 图片上传工具类
 */
object ImageUploadUtils {
    const val TAG = "ImageUploadUtils"
    private const val FILE_PROVIDER_AUTHORITY = "com.example.rideflow.fileprovider"
    
    /**
     * 创建临时图片文件
     */
    @Throws(IOException::class)
    fun createImageFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(Date())
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }
    
    /**
     * 获取图片的Uri
     */
    fun getImageUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            FILE_PROVIDER_AUTHORITY,
            file
        )
    }
    

    
    /**
     * 旋转Bitmap
     */
    fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            else -> return bitmap
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    
    /**
     * 压缩Bitmap
     */
    fun compressBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        var width = bitmap.width
        var height = bitmap.height
        
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }
    
    /**
     * 将Bitmap保存为文件
     */
    fun saveBitmapToFile(bitmap: Bitmap, file: File): Boolean {
        return try {
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            outputStream.flush()
            outputStream.close()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving bitmap to file: ${e.message}")
            false
        }
    }
    
    /**
     * 从Uri加载图片
     */
    fun loadImageFromUri(context: Context, uri: Uri): ImageBitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            bitmap?.asImageBitmap()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading image from uri: ${e.message}")
            null
        }
    }
    

    
    /**
     * 从相册选择图片
     */
    fun pickImageFromGallery(context: Context, onImagePicked: (File?) -> Unit) {
        try {
            // 创建临时文件
            val tempFile = createImageFile(context)
            
            // 这里需要ActivityResultLauncher来处理相册结果
            // 由于Compose的限制，我们返回一个文件，由调用者处理实际的启动
            // 实际应用中应该使用rememberLauncherForActivityResult
            onImagePicked(tempFile)
        } catch (e: Exception) {
            Log.e(TAG, "Error picking image from gallery: ${e.message}")
            onImagePicked(null)
        }
    }
    
    /**
     * 将Uri转换为File
     * @param context 上下文
     * @param uri 图片Uri
     * @return File对象
     */
    fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val contentResolver: ContentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri)
            
            // 创建临时文件
            val tempFile = createImageFile(context)
            val outputStream = FileOutputStream(tempFile)
            
            // 将输入流内容复制到输出流
            inputStream?.use { input ->
                outputStream.use { output ->
                    val buffer = ByteArray(4 * 1024) // 4KB buffer
                    var read: Int
                    while (input.read(buffer).also { read = it } != -1) {
                        output.write(buffer, 0, read)
                    }
                }
            }
            
            tempFile
        } catch (e: Exception) {
            Log.e(TAG, "Error converting uri to file: ${e.message}")
            null
        }
    }
}

/**
 * 图片选择器Composable
 */
@Composable
fun ImagePicker(
    context: Context,
    selectedImage: MutableState<File?>
) {
    // 相册选择启动器
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                // 将Uri转换为File
                val file = ImageUploadUtils.uriToFile(context, uri)
                if (file != null) {
                    selectedImage.value = file
                }
            }
        }
    )
    
    // 相册选择操作
    "相册" to { galleryLauncher.launch("image/*") }
}
