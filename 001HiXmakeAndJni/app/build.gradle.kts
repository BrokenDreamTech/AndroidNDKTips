import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.application)
}

val archList = listOf(
    "arm64-v8a",
    "x86_64",
)

android {
    namespace = "com.brokendream.hixmakeandjni"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }
    ndkVersion = "29.0.13599879"

    defaultConfig {
        applicationId = "com.brokendream.hixmakeandjni"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters += archList
        }

        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++17"
            }
        }
    }
    // 签名
    val releaseSigning = "release"
    signingConfigs {
        create(releaseSigning) {
            storeFile = file("./brokendream.jks")
            storePassword = "123456"
            keyAlias = "key0"
            keyPassword = "123456"
            enableV1Signing = true
            enableV2Signing = true
        }
    }
    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName(releaseSigning)
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        release {
            signingConfig = signingConfigs.getByName(releaseSigning)
            isMinifyEnabled = true
            isShrinkResources = true
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
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}


androidComponents {
    onVariants { variant ->
        // 设置so目录
        // 关键点：调用 addStaticSourceDirectory，这是 AGP 9.x 中添加源码/资源/so的唯一正统且未废弃的方法！
        variant.sources.jniLibs?.addStaticSourceDirectory("src/main/cpp/xmake/build/android")
    }
}


// 1. 获取 NDK 目录的 Provider (AGP 9.1 专有方式)
val ndkDirProvider = androidComponents.sdkComponents.ndkDirectory

// 2. 注册 xmake 构建任务
val taskNames = archList.map { arch ->
    // 替换名称中的连接符，使其作为 Gradle 任务名称更规范
    val taskName = "runCmdBeforeBuild_${arch.replace("-", "_")}"

    tasks.register<Exec>(taskName) {
        group = "xmake build"
        description = "Run xmake to generate cmakelists for $arch"

        // 设置工作目录
        workingDir = file("src/main/cpp/xmake")

        // 声明输入，让 Gradle 知道这个任务依赖 NDK 路径（有助于构建缓存正确工作）
        inputs.property("ndkDir", ndkDirProvider)

        // 指定可执行文件为 cmd
        executable = "cmd"

        // 【核心修改】使用 argumentProviders 延迟获取 NDK 路径并组装参数
        // 这里的代码只有在任务真正执行时才会运行
        argumentProviders.add(CommandLineArgumentProvider {
            // 在执行阶段安全获取 NDK 绝对路径
            val ndkDir = ndkDirProvider.get().asFile.absolutePath

            // 组装命令（注意我为 ndkDir 增加了双引号，防止 Windows 路径中含有空格导致报错）
            val commandStr = """
                xmake f -y -p android --ndk="${ndkDir}" -a ${arch} && xmake
            """.trimIndent()

            println("xmake Command executing for $arch: $commandStr")

            // 返回 cmd 后面跟着的参数列表
            listOf("/c", commandStr)
        })
    }
    taskName
}

// 3. 任务与 Kotlin 编译任务关联
// 【核心修改】使用 configureEach 代替直接使用 { ... }，这是 Gradle 8.0+ 要求的懒加载写法
tasks.withType<KotlinCompile>().configureEach {
    taskNames.forEach { dependsOn(it) }
}