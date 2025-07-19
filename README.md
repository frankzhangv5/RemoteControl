# 电视遥控器应用

这是一个Android应用，可以将手机变成电视遥控器，通过[ADB协议](https://github.com/cgutman/AdbLib)连接Android电视并发送按键事件，不会中电视端push或安装任何文件，并且无任何广告。

## 功能特性

- 🔍 **局域网设备扫描**: 自动扫描局域网中的Android设备
- 🔗 **ADB连接**: 通过IP地址连接电视的ADB服务
- 🎮 **遥控器界面**: 模拟物理遥控器的按键布局
- 📱 **现代化UI**: 使用Jetpack Compose构建的现代化界面

## 支持的按键

### 方向键
- ⬆️ 上
- ⬇️ 下
- ⬅️ 左
- ➡️ 右
- ✅ 确认

### 功能键
- 🔙 返回
- 📋 菜单
- 🏠 主页

### 音量键
- 音量减
- 音量加

## 使用前准备

### 1. 电视端设置

确保您的电视已开启ADB调试功能：

1. 进入电视的开发者选项
2. 开启"USB调试"或"ADB调试"
3. 开启"网络调试"或"无线调试"
4. 记录电视的IP地址

### 2. 网络要求

- 手机和电视必须在同一个WiFi网络下
- 确保防火墙没有阻止5555端口

## 使用方法

1. **启动应用**: 打开应用后，点击"扫描设备"按钮
2. **选择设备**: 从发现的设备列表中选择您的电视
3. **开始遥控**: 连接成功后，使用遥控器界面控制电视

## 技术实现

### 核心技术栈
- **Jetpack Compose**: 现代化UI框架
- **Kotlin Coroutines**: 异步编程
- **ADB协议**: 与Android设备通信
- **网络扫描**: 发现局域网设备

### 主要组件

- `AdbManager`: ADB连接和通信管理
- `NetworkScanner`: 局域网设备扫描
- `RemoteViewModel`: 应用状态管理
- `RemoteControlScreen`: 遥控器界面

## 项目结构

```
app/src/main/java/zhang/feng/remotecontrol/
├── MainActivity.kt              # 主活动
├── AdbManager.kt               # ADB连接管理
├── NetworkScanner.kt           # 网络扫描
├── RemoteKeys.kt               # 按键常量
├── RemoteViewModel.kt          # 视图模型
└── ui/
    └── RemoteControlScreen.kt  # 遥控器界面
```

## 注意事项

1. **权限要求**: 应用需要网络访问权限来扫描设备
2. **兼容性**: 仅支持开启ADB调试的Android设备
3. **网络稳定性**: 确保WiFi连接稳定以获得最佳体验
4. **安全**: 请确保在安全的网络环境中使用

## 故障排除

### 无法发现设备
- 检查电视是否开启ADB调试
- 确认手机和电视在同一网络
- 检查防火墙设置

### 连接失败
- 验证IP地址是否正确
- 确认5555端口未被占用
- 开关电视的ADB无线调试开关
- 重启电视的ADB服务

### 按键无响应
- 检查ADB连接状态
- 确认电视支持相应的按键事件
- 尝试重新连接设备

## 开发环境

- Android Studio Hedgehog | 2023.1.1
- Kotlin 1.9.0
- Compose BOM 2024.02.00
- minSdk 28
- targetSdk 35

## 许可证

本项目仅供学习和个人使用。 