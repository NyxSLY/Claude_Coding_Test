# Android Studio 新手教程：从零开始到生成APK

本教程以 Reddit热门追踪 项目为例，手把手教你完成整个流程。

---

## 第一部分：安装和首次配置 Android Studio

### Step 1: 完成安装向导

1. 启动 Android Studio 安装程序
2. 选择 **Standard** 安装类型（推荐）
3. 选择 UI 主题（深色/浅色随你喜欢）
4. 等待下载组件（可能需要10-30分钟，取决于网速）
   - Android SDK
   - Android SDK Platform
   - Android Virtual Device (模拟器)

### Step 2: 首次启动配置

安装完成后首次启动，如果弹出提示：
- "Import settings" → 选择 **Do not import settings**
- 代理设置 → 如果在国内，可能需要配置代理或镜像

---

## 第二部分：导入项目

### Step 3: 获取项目代码

打开终端（Windows用PowerShell，Mac用Terminal）：

```bash
# 方式一：克隆仓库（如果有git）
git clone <你的仓库地址>

# 方式二：直接下载ZIP并解压
```

### Step 4: 在 Android Studio 中打开项目

1. 启动 Android Studio
2. 在欢迎界面点击 **Open**（或 File → Open）
3. 找到项目文件夹 `Claude_Coding_Test`，选中它
4. 点击 **OK**

### Step 5: 等待 Gradle 同步

打开项目后，右下角会显示进度条：

```
Gradle sync started
Downloading gradle-8.2-bin.zip...
Syncing project with Gradle files...
```

⚠️ **首次同步可能需要 5-15 分钟**（需要下载依赖）

**如果同步失败：**
- 检查网络连接
- 点击 `File` → `Sync Project with Gradle Files` 重试
- 如果提示缺少SDK，点击提示中的链接自动安装

### Step 6: 确认同步成功

同步成功后，你会看到：
- 左侧项目结构正常显示
- 底部 Build 窗口显示 `BUILD SUCCESSFUL`
- 没有红色错误提示

---

## 第三部分：创建模拟器（在电脑上测试）

### Step 7: 打开设备管理器

点击菜单：`Tools` → `Device Manager`

或者点击工具栏上的手机图标 📱

### Step 8: 创建虚拟设备

1. 点击 **Create Device**（或 + 号）

2. **选择设备型号**
   - 推荐选择：`Pixel 6` 或 `Pixel 7`
   - 点击 **Next**

3. **选择系统镜像**
   - 选择 **API 34** (Android 14) 或 **API 33** (Android 13)
   - 如果显示 "Download"，点击下载（约1-2GB）
   - 下载完成后选中它，点击 **Next**

4. **配置模拟器**
   - 名称：保持默认即可
   - 点击 **Finish**

### Step 9: 启动模拟器

1. 在 Device Manager 中找到刚创建的设备
2. 点击 ▶️ **启动按钮**
3. 等待模拟器启动（首次可能需要1-2分钟）
4. 看到 Android 手机界面就说明成功了

---

## 第四部分：运行和测试应用

### Step 10: 选择运行设备

1. 在工具栏中间找到设备下拉菜单
2. 选择你刚创建的模拟器（如 `Pixel 6 API 34`）

```
┌─────────────────────────────────────────────────────────┐
│  [app ▼]  [Pixel 6 API 34 ▼]  [▶️ Run]  [🐛 Debug]     │
└─────────────────────────────────────────────────────────┘
```

### Step 11: 运行应用

点击绿色 **▶️ Run** 按钮（或按 `Shift + F10`）

首次运行会：
1. 编译项目（Build）
2. 安装APK到模拟器
3. 自动启动应用

### Step 12: 测试应用功能

应用启动后，按以下步骤测试：

```
测试清单：
□ 1. 点击热门推荐标签（如 programming），看是否添加到"已选择"
□ 2. 在输入框输入 "android"，点击添加
□ 3. 点击已选择标签的 ✕，看是否能删除
□ 4. 选择时间范围：24小时
□ 5. 选择排序方式：热门
□ 6. 点击"搜索热门帖子"按钮
□ 7. 等待加载，看是否显示帖子列表
□ 8. 下拉刷新，看是否正常工作
□ 9. 点击任意帖子，看是否跳转到浏览器
```

### 常见问题排查

**问题：点击搜索没反应**
- 检查是否添加了至少一个 subreddit
- 检查模拟器是否联网（模拟器默认使用电脑网络）

**问题：显示"加载失败"**
- Reddit 在某些地区可能需要代理
- 检查网络连接

**问题：应用闪退**
- 查看底部 `Logcat` 窗口的错误信息
- 确保 Android 版本 ≥ API 24

---

## 第五部分：生成 APK 安装包

### Step 13: 生成调试版 APK（用于测试）

1. 点击菜单 `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
2. 等待构建完成
3. 右下角弹出提示，点击 **locate** 打开文件夹
4. APK 文件位置：
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

### Step 14: 生成正式版 APK（用于分发）

正式版需要签名，步骤如下：

1. 点击菜单 `Build` → `Generate Signed Bundle / APK...`

2. 选择 **APK**，点击 Next

3. **创建签名密钥**（首次需要）
   - 点击 **Create new...**
   - 填写信息：
   ```
   Key store path: 选择保存位置，如 my-release-key.jks
   Password: 设置密码（记住它！）
   Alias: 输入别名，如 my-key
   Password: 再设置一个密码
   Validity: 25（年）
   Certificate: 填写你的信息（可随意填）
   ```
   - 点击 **OK**

4. 选择刚创建的密钥文件，输入密码，点击 **Next**

5. 选择 **release**，点击 **Create**

6. APK 文件位置：
   ```
   app/release/app-release.apk
   ```

---

## 第六部分：安装到真实手机

### Step 15: 手机开启开发者选项

1. 打开手机 **设置**
2. 找到 **关于手机**
3. 连续点击 **版本号** 7次
4. 返回设置，找到新出现的 **开发者选项**
5. 开启 **USB调试**

### Step 16: 连接手机安装

**方式一：通过 Android Studio**
1. 用 USB 线连接手机和电脑
2. 手机上点击"允许USB调试"
3. Android Studio 设备列表中选择你的手机
4. 点击 Run 直接安装运行

**方式二：直接安装 APK**
1. 把 `app-debug.apk` 发送到手机
2. 在手机上点击 APK 文件
3. 允许"安装未知来源应用"
4. 完成安装

---

## 快速命令参考

如果你喜欢用命令行：

```bash
# 进入项目目录
cd Claude_Coding_Test

# 清理项目
./gradlew clean

# 编译调试版
./gradlew assembleDebug

# 编译正式版
./gradlew assembleRelease

# 运行测试
./gradlew test

# 安装到已连接的设备
./gradlew installDebug
```

---

## 项目文件结构说明

```
Claude_Coding_Test/
├── app/
│   ├── build.gradle.kts      ← 应用配置（依赖、版本等）
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml  ← 应用清单
│   │   │   ├── java/...             ← Kotlin 源代码
│   │   │   └── res/                 ← 资源文件
│   │   │       ├── layout/          ← 界面布局
│   │   │       ├── values/          ← 字符串、颜色
│   │   │       └── drawable/        ← 图标图片
│   │   └── test/                    ← 测试代码
│   └── build/
│       └── outputs/apk/             ← 生成的 APK
├── build.gradle.kts          ← 项目配置
├── settings.gradle.kts       ← 项目设置
└── gradle/                   ← Gradle 配置
```

---

## 遇到问题？

1. **Gradle 同步失败** → 检查网络，可能需要配置代理
2. **模拟器太慢** → 开启 CPU 虚拟化（BIOS设置）或使用真机测试
3. **编译错误** → 查看 Build 窗口的具体错误信息

祝你顺利！🎉
