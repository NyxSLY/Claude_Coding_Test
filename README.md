# Reddit热门帖子追踪 (Reddit Trending Tracker)

一个Android应用，用于追踪Reddit论坛的热门帖子，帮助你了解每天发生的热门讨论。

## 功能特点

- 支持添加多个Subreddit进行追踪
- 灵活的时间范围筛选（1小时/24小时/一周/一个月/一年/全部）
- 多种排序方式（热门/最新/最高分/上升中）
- 显示帖子标题、分数、评论数、发布时间
- 缩略图预览
- 点击帖子跳转到Reddit查看详情
- 下拉刷新功能
- Material Design 3 现代UI设计

## 环境要求

### 开发环境
- **Android Studio**: Arctic Fox (2020.3.1) 或更高版本，推荐 Hedgehog (2023.1.1)+
- **JDK**: 17 或更高版本
- **Gradle**: 8.2+
- **Kotlin**: 1.9.20+

### 运行环境
- **Android版本**: Android 7.0 (API 24) 或更高
- **网络**: 需要网络连接访问Reddit API

## 安装步骤

### 1. 克隆项目

```bash
git clone <repository-url>
cd Claude_Coding_Test
```

### 2. 打开项目

1. 启动 Android Studio
2. 选择 `File` → `Open`
3. 选择项目根目录 `Claude_Coding_Test`
4. 等待 Gradle 同步完成

### 3. 配置SDK

如果提示缺少SDK，Android Studio会自动提示下载。确保安装：
- Android SDK Platform 34
- Android SDK Build-Tools 34.0.0

### 4. 运行应用

**方式一：使用模拟器**
1. 点击 `Tools` → `Device Manager`
2. 创建一个新的虚拟设备（推荐 Pixel 6, API 34）
3. 点击运行按钮 ▶️

**方式二：使用真机**
1. 在手机上开启「开发者选项」和「USB调试」
2. 用USB线连接电脑
3. 在Android Studio中选择你的设备
4. 点击运行按钮 ▶️

**方式三：生成APK**
```bash
./gradlew assembleDebug
```
APK文件位于：`app/build/outputs/apk/debug/app-debug.apk`

## 使用说明

### 1. 添加Subreddit

- 在输入框中输入subreddit名称（如：`programming`、`android`）
- 点击「添加」按钮或按回车键
- 也可以点击下方的热门推荐标签快速添加
- 点击已添加标签的 ✕ 可以移除

### 2. 设置筛选条件

| 时间范围 | 说明 |
|---------|------|
| 1小时 | 最近1小时内的帖子 |
| 24小时 | 最近一天的帖子 |
| 一周 | 最近7天的帖子 |
| 一个月 | 最近30天的帖子 |
| 一年 | 最近一年的帖子 |
| 全部时间 | 不限时间 |

| 排序方式 | 说明 |
|---------|------|
| 热门 | 按热度排序（Reddit算法） |
| 最新 | 按发布时间排序 |
| 最高分 | 按点赞数排序 |
| 上升中 | 快速上升的帖子 |

### 3. 搜索帖子

点击「搜索热门帖子」按钮获取结果

### 4. 查看详情

点击任意帖子卡片，会跳转到浏览器打开Reddit原帖

### 5. 刷新数据

下拉列表可以刷新获取最新帖子

## 项目结构

```
app/src/main/
├── java/com/example/reddittrending/
│   ├── api/                    # API层
│   │   ├── RedditApi.kt        # Reddit API接口定义
│   │   └── RetrofitClient.kt   # 网络客户端配置
│   ├── data/                   # 数据层
│   │   └── RedditRepository.kt # 数据仓库
│   ├── model/                  # 数据模型
│   │   └── RedditModels.kt     # Reddit数据类
│   └── ui/                     # UI层
│       ├── MainActivity.kt     # 主界面
│       ├── MainViewModel.kt    # 视图模型
│       └── PostAdapter.kt      # 列表适配器
└── res/
    ├── layout/                 # 布局文件
    ├── values/                 # 资源值
    └── drawable/               # 图形资源
```

## 技术栈

| 类别 | 技术 |
|-----|------|
| 语言 | Kotlin |
| 架构 | MVVM |
| 网络 | Retrofit + OkHttp |
| 异步 | Kotlin Coroutines |
| 图片加载 | Coil |
| UI组件 | Material Design 3 |
| 数据绑定 | ViewBinding |

## 注意事项

1. **网络要求**: 应用需要访问 `reddit.com`，请确保网络可以正常访问
2. **API限制**: Reddit公开API有请求频率限制，请勿频繁刷新
3. **NSFW内容**: 应用会标记NSFW帖子，但不会过滤

## 常见问题

**Q: 搜索没有结果？**
- 检查网络连接
- 确认subreddit名称拼写正确
- 尝试扩大时间范围

**Q: 图片不显示？**
- 部分帖子没有缩略图
- 检查网络连接

**Q: 应用崩溃？**
- 确保Android版本 ≥ 7.0
- 尝试清除应用数据重新打开

## License

MIT License
