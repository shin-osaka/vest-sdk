# Vest-SDK

最新版本：1.0.1   
这是一个可以用于控制游戏跳转的三方依赖库，工程提供开源代码，可自行修改。

SDK总共四个依赖库：  
vest-core: 项目运行所必须的核心库（必须引入）  
vest-sdk: 运行B面游戏的平台  
vest-shf: 用于切换A/B面的远程开关
vest-firebase: 用于切换A/B面的远程开关

## 开发环境

- JdkVersion:  11
- GradleVersion: 7.4
- GradlePluginVersion: 7.3.0
- minSdkVersion    : 24
- targetSdkVersion : 34
- compileSdkVersion: 34

## 工程说明

- app-core是核心库
- app-sdk用于构建游戏运行的平台
- app-shf用于构建审核服开关功能，用于切换A/B面
- app-firebase用于构建Firebase开关功能，用于切换A/B面
- app是用于测试sdk的测试工程
- 开源sdk使用者可以使用以下构建命令构建出aar，再自行导入自己的工程。（总共四个aar文件，分别输出到sdk目录和app/libs目录）
    ```
    ./gradlew clean app-core:assembleRelease app-sdk:assembleRelease app-shf:assembleRelease app-firebase:assembleRelease
    ```

## SDK集成步骤

1. 集成插件（Kotlin或者Google Service插件）

- 项目根目录build.gradle或者setting.gradle
   ```
   buildscript {
       repositories {
           mavenCentral()
           google()
       }
   }
   
   plugins {
       id 'com.android.application' version '7.3.0' apply false
       id 'org.jetbrains.kotlin.android' version '1.9.22' apply false
   }
   ```
- app/build.gradle
   ```
   plugins {
       id 'org.jetbrains.kotlin.android'
       //如果用Firebase控制需要引入这个版本的插件，不要随便更换版本
       id 'com.google.gms.google-services' version "4.3.15"
   }
   ```

2. app模块添加依赖   
   总共有三种依赖方式：maven依赖、本地libs依赖、源码依赖    
   vest-core是核心库必须引用，另外两个库根据需要引用。
   vest-sdk则是B面游戏运行平台。
   vest-shf只提供审核服控制的A/B面切换开关功能。   
   vest-firebase只提供Firebase控制的A/B面切换开关功能。   
   注意：vest-shf和vest-firebase两种控制方式二选一，不要同时引入

   (1) maven依赖方式
      ```
      dependencies {
          //核心库（必须引入）
          implementation 'io.github.shin-osaka:vest-core:1.0.1'
          //B面游戏运行平台
          implementation 'io.github.shin-osaka:vest-sdk:1.0.1'
          //A/B面切换开关
          implementation 'io.github.shin-osaka:vest-shf:1.0.1'
          //vest-shf和vest-firebase 二选一
          //implementation 'io.github.shin-osaka:vest-firebase:1.0.1'
      }
      ```
   (2) 本地依赖方式
   -
   a.拷贝sdk目录下的aar文件，包括vest-core、vest-sdk、（vest-shf和vest-firebase二选一）到app/libs文件夹，然后在app/build.gradle添加如下配置：
    ```
    //三方依赖必须引入
    dependencies {
        implementation fileTree(dir: 'libs', include: ['*.jar', '*.aar'])
        implementation "androidx.appcompat:appcompat:1.6.1"
        implementation "androidx.multidex:multidex:2.0.1"
        implementation "androidx.annotation:annotation:1.7.0"
        implementation "com.android.installreferrer:installreferrer:2.2"
        implementation "com.google.android.gms:play-services-ads-identifier:18.0.1"
        implementation "com.squareup.okhttp3:okhttp:4.10.0"
        implementation "com.squareup.okhttp3:logging-interceptor:4.10.0"
        implementation "io.github.shin-osaka:adjust-android:4.38.4.5"
        implementation "io.reactivex.rxjava3:rxjava:3.0.0"
        implementation "io.reactivex.rxjava3:rxandroid:3.0.2"
        implementation "com.squareup.retrofit2:retrofit:2.9.0"
        implementation "com.squareup.retrofit2:adapter-rxjava3:2.9.0"
        implementation "com.squareup.retrofit2:converter-gson:2.9.0"
        implementation "org.greenrobot:eventbus:3.3.1"
        implementation "androidx.activity:activity-compose:1.8.2"
        implementation "androidx.compose.material3:material3:1.1.2"
        implementation "androidx.compose.ui:ui:1.6.0"
        implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"
        //使用firebase控制才需要引入这个库
        //implementation "com.google.firebase:firebase-config-ktx:21.4.1"
    }
    ```
    - b.添加混淆配置[proguard-rules.md](./docs/proguard-rules.md)

   (3) 源码依赖方式（适用于使用开源工程的开发者）
    - a.把模块app-core, app-sdk, app-shf, app-firebase导入到你的工程中（注意还有其他依赖模块，统一以lib-开头）
    - b.在app模块build.gradle中添加如下依赖：
      ```
      dependencies {
          implementation project(":app-core")
          implementation project(":app-sdk")
          //vest-shf和vest-firebase 二选一
          implementation project(":app-shf")
          implementation project(":app-firebase")
      }
      ```

3. 在Application中初始化VestSDK   
   (1) `VestSDK.init()`
   方法中传入配置文件名称，请把该配置文件放在assets根目录，配置文件来源将在第4点说明。   
   (2) `VestSDK.setReleaseMode()`
   方法设置发布模式，发布模式跟出包的用途有关，会影响到`VestSHF.getInstance().inspect()`方法的返回值。
    - `MODE_VEST`表示当前发布的是马甲包，也就是用于上架的包，该模式是默认值
    - `MODE_CHANNEL`表示当前发布的是渠道包，放在落地页用于推广的包
   ```
   class AppApplication : MussltiDexApplication()  {

      override fun onCreate() {
          super.onCreate()
          VestSDK.setLoggable(BuildConfig.DEBUG)
          VestSDK.setReleaseMode(VestReleaseMode.MODE_VEST)
          VestSDK.init(baseContext, "config")
      }

   }
   ```
4. 实现A/B面切换   
   (1) 审核服方式实现开关：在闪屏页实现方法`VestSHF.getInstance().inspect()`
   获取A/B面切换开关，参照例子`vest/com/example/vest/sdk/app/SplashActivity`
    ```
    VestSHF.getInstance().apply {
            /**
             * setup the date of apk build
             * time format: yyyy-MM-dd HH:mm:ss
             */
            setReleaseTime("2023-11-29 10:23:20")

            /**
             * setup duration of silent period for requesting A/B switching starting from the date of apk build
             */
            setInspectDelayTime(5, TimeUnit.DAYS)

            /**
             * set true to check the remote and local url, this could make effect on A/B switching
             */
            setCheckUrl(true)

            /** 
             * 「Optional」If there is no need, you can skip calling this method
             * 
             * set up a device whitelist for SHF, where devices in the whitelist can bypass the interception of Install Referrer in the Release environment
             * only effective in Release package, Debug package will not be intercepted due to attribution being a natural quantity
             */
            setDeviceWhiteList(listOf("xxxx",...))
   
            /**
             * trying to request A/B switching, depends on setReleaseTime & setInspectDelayTime & backend config
             */
        }.inspect(this, object : VestInspectCallback {
            /**
             * showing A-side
             */
            override fun onShowASide(reason: Int) {
                Log.d(TAG, "show A-side activity")
                gotoASide()
                finish()
            }

            /**
             * showing B-side
             */
            override fun onShowBSide(url: String, launchResult: Boolean) {
                Log.d(TAG, "show B-side activity: $url, result: $launchResult")
                if (!launchResult) {
                    gotoASide()
                }
                finish()
            }

            private fun gotoASide() {
                val intent = Intent(baseContext, ASideActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        })
    ```
   (2) Firebase方式实现开关：在闪屏页实现方法`VestFirebase.getInstance().inspect()`
   获取A/B面切换开关，参照例子`firebase/com/example/vest/sdk/app/SplashActivity`
   ```
        VestFirebase.getInstance().apply {
            /**
             * setup the date of apk build
             * time format: yyyy-MM-dd HH:mm:ss
             */
            setReleaseTime("2023-11-29 10:23:20")

            /**
             * setup duration of silent period for requesting A/B switching starting from the date of apk build
             */
            setInspectDelayTime(0, TimeUnit.DAYS)

            /**
             * 「Optional」If there is no need, you can skip calling this method
             * 
             * set up a device whitelist for Firebase, where devices in the whitelist can bypass the interception of Install Referrer in the Release environment
             * only effective in Release package, Debug package will not be intercepted due to attribution being a natural quantity
             */
            setDeviceWhiteList(listOf("xxxx",...))
   
        }.inspect(this, object : VestInspectCallback {

            /**
             * showing A-side
             */
            override fun onShowASide(reason: Int) {
                Log.d(TAG, "show A-side activity")
                gotoASide()
                finish()
            }

            /**
             * showing B-side
             */
            override fun onShowBSide(url: String, launchResult: Boolean) {
                Log.d(TAG, "show B-side activity: $url, result: $launchResult")
                if (!launchResult) {
                    gotoASide()
                }
                finish()
            }

        })
   ```
   (3) 在上面的示例中，提供了方法`setInspectDelayTime()`和`setReleaseTime()`
   控制A/B面开关的请求静默期，目的是为了在审核期间不访问服务器暴露行为，默认延迟1天，可自行修改系统时间进行测试。
     ```
       VestSHF.getInstance().setReleaseTime("2023-11-29 10:23:20");
     ```
     ```
       VestFirebase.getInstance().setReleaseTime("2023-11-29 10:23:20");
     ```
   (4) 在Activity中实现vest-sdk生命周期
     ```
       override fun onPause() {
           super.onPause()
           VestSDK.onPause()
       }

       override fun onResume() {
           super.onResume()
           VestSDK.onResume()
       }

       override fun onDestroy() {
           super.onDestroy()
           VestSDK.onDestroy()
       }
     ```

5. 把厂商提供的配置文件`config`，放到工程的assets根目录。为避免出包之间文件关联，请自行更改`config`
   文件名（config文件内容按控制方式选择不同的数据格式，见文件示例：config-firebase.json、config-vest.json）
6. 使用vest-firebase的控制方式，还需要从Firebase控制台下载google-services.json文件，放到app模块根目录下。
7. 至此Vest-SDK集成完毕。

## 使用快照

上面集成的是release版本，稳定可靠，但是代码特征一成不变。为了及时更改SDK代码特征，我们会每天更新一个快照版本到快照仓库。
快照版本是在每个稳定版本的基础上增加混淆代码，不影响正常功能使用。使用方法如下：

1. 添加快照仓库

- setting.gradle（Gradle Plugin版本7.x及以上）

```
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
     google()
     mavenCentral()
     #添加快照仓库
     maven { url("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
  }
}
```

- 或者build.gradle（Gradle Plugin版本7.x以下）

```
allprojects {
    repositories {
      google()
      mavenCentral()
      #添加快照仓库
      maven { url("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
    }
}
```

2. 在sdk的依赖版本号后面加上-SNAPSHOT，则可以使用release版本的快照版本。

```
 dependencies {
    implementation 'io.github.shin-osaka:vest-core:1.0.1-SNAPSHOT'
    implementation 'io.github.shin-osaka:vest-sdk:1.0.1-SNAPSHOT'
    implementation 'io.github.shin-osaka:vest-shf:1.0.1-SNAPSHOT'
    //vest-shf和vest-firebase 二选一
    //implementation 'io.github.shin-osaka:vest-firebase:1.0.1-SNAPSHOT'
 }
```

3. 在build.gradle android节点下添加以下代码，可以帮助及时更新sdk版本依赖缓存。

```
    android {
      ...
      //gradle依赖默认缓存24小时，在此期间内相同版本只会使用本地资源
      configurations.all {
        //修改缓存周期
        resolutionStrategy.cacheDynamicVersionsFor 0, 'seconds' // 动态版本
        resolutionStrategy.cacheChangingModulesFor 0, 'seconds' // 变化模块
      }
      ...
    }
```

## 测试说明

- 游戏切换开关由厂商后台配置，测试时请联系厂商修改配置。
- 获取到正式游戏地址后，会一直使用缓存的正式游戏链接，后台关闭开关不会跳转回马甲游戏，清除缓存后再次进入游戏获取。

## 版本说明

### 1.0.0

- 重新发布1.0.0到新仓库

### 1.0.1

- 修复自然量判断错误问题


