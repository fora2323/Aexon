<p align="center">
  <img src="https://raw.githubusercontent.com/fora2323/Aexon/main/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" width="120">
</p>

<h1 align="center">Aexon</h1>

<p align="center">Android 的 Root/系统管理辅助应用</p>

<p align="center">
  <img src="https://img.shields.io/badge/license-GPL--3.0-blue">
  <img src="https://img.shields.io/badge/platform-Android-brightgreen">
  <img src="https://img.shields.io/badge/made%20with-Sketchware%20%26%20Termux-orange">
  <img src="https://img.shields.io/github/downloads/fora2323/Aexon/total">
</p>

---

## 关于 Aexon

Aexon 是一款用于在 Android 上管理系统级 shell 守护进程的辅助应用，具有本地 watchdog、签名校验和动态主题（基于 HCT/CAM16）。Aexon 的核心代码——本地守护进程、JNI、主题系统和 UI 组件——由项目组内部实现。部分功能集成了第三方开源组件；有关完整归属信息，请参见下文的「第三方组件」章节。

## 灵感来源

Aexon 的界面设计受以下项目启发：

- AxManager — https://github.com/fahrez182/AxManager
- KernelSU Next — https://github.com/KernelSU-Next/KernelSU-Next
- Magisk Manager — https://github.com/topjohnwu/Magisk
- Shizuku — https://github.com/RikkaApps/Shizuku

> 上述灵感主要来自设计/UI 层面。有关代码级集成，请见下文。

## 第三方组件

Aexon 集成了以下第三方开源组件。每个组件保留其原始许可；将它们列入此处并不改变 Aexon 本身采用的 GPL-3.0 许可。

### Shizuku

Aexon 使用官方的 Shizuku 库（通过 Gradle 依赖）来实现权限/特权集成。应用在运行时依赖 Shizuku 的 API/provider 包，并与 Shizuku 交互；Aexon 的代码库中没有复制上游 Shizuku 的源代码。上游的 Shizuku 组件仍然受 Apache License 2.0 许可约束。

### BusyBox

Aexon 可以在运行时选择性地调用 BusyBox 风格的工具。应用包含在用户启用该模式时调用 BusyBox 二进制（或包含的本地 helper）的代码路径。如果 BusyBox 二进制和/或相应的本地库随 APK 一并发布，则必须随附 BusyBox（GPL-2.0）要求的对应源代码或分发说明。如果未随 APK 打包，Aexon 会尝试使用设备或 root/Magisk 环境中已存在的 BusyBox。

## 许可证

本项目采用 **GNU General Public License v3.0 (GPL-3.0)** 许可。

这意味着：

- 可自由使用、研究、修改和再分发
- 所有派生/分支项目必须在相同许可（GPL-3.0）下开源
- 本软件不提供任何担保

上文列出的第三方组件保留其各自的原始许可（见「第三方组件」章节）。

完整许可文本见 [LICENSE](LICENSE)。
