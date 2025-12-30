plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.kapt")
}

import java.util.Properties

val localProperties = Properties().apply {
    val propsFile = rootProject.file("local.properties")
    if (propsFile.exists()) {
        propsFile.inputStream().use { load(it) }
    }
}

fun escapeForBuildConfig(value: String): String =
    value.replace("\\", "\\\\").replace("\"", "\\\"")

fun localProp(name: String): String =
    localProperties.getProperty(name)?.trim().orEmpty()

android {
    namespace = "com.example.rideflow"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.rideflow"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["AMAP_API_KEY"] = "ca12ea7d2af8d7f4e8be13bc1ad5575b"

        val ossAccessKeyId = localProp("OSS_ACCESS_KEY_ID")
        val ossAccessKeySecret = localProp("OSS_ACCESS_KEY_SECRET")
        val ossBucketName = localProp("OSS_BUCKET_NAME").ifBlank { "rideapp" }
        val ossEndpoint = localProp("OSS_ENDPOINT").ifBlank { "oss-cn-hangzhou.aliyuncs.com" }

        buildConfigField("String", "OSS_ACCESS_KEY_ID", "\"${escapeForBuildConfig(ossAccessKeyId)}\"")
        buildConfigField("String", "OSS_ACCESS_KEY_SECRET", "\"${escapeForBuildConfig(ossAccessKeySecret)}\"")
        buildConfigField("String", "OSS_BUCKET_NAME", "\"${escapeForBuildConfig(ossBucketName)}\"")
        buildConfigField("String", "OSS_ENDPOINT", "\"${escapeForBuildConfig(ossEndpoint)}\"")
        buildConfigField("String", "OSS_RIDEMAP_DIR", "\"ridemap\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    sourceSets {
        getByName("main") {
            assets.srcDirs("src/main/assets", "../art/wallpaper")
        }
    }
    lint {
        baseline = file("lint-baseline.xml")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    
    // Koin依赖注入
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.core)
    
    // 位置服务相关依赖
    implementation(libs.google.location.services)
    
    // 权限请求相关依赖
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    
    // 前台服务相关依赖
    implementation(libs.androidx.lifecycle.service)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    
    // 其他工具库
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.work.runtime.ktx)
    
    // 导航相关依赖
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.common.ktx)
    implementation(libs.androidx.navigation.runtime.ktx)
    
    // MySQL数据库驱动 - 使用兼容性更好的旧版本
    implementation(libs.mysql.connector.java)
    
    // Compose运行时LiveData支持
    implementation("androidx.compose.runtime:runtime-livedata:1.5.4")
    
    // Coil图片加载库
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation(libs.androidx.compose.foundation)

    implementation("com.amap.api:map2d:6.0.0")
    implementation("com.amap.api:search:9.4.0")
    implementation("com.amap.api:location:6.4.7")
    implementation("com.aliyun.dpa:oss-android-sdk:2.9.5")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
