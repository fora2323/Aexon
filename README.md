[English](./README.md) · [简体中文](./README.zh-CN.md)

<p align="center">
  <img src="https://raw.githubusercontent.com/fora2323/Aexon/main/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" width="120">
</p>

<h1 align="center">Aexon</h1>

<p align="center">Root/system management companion for Android</p>

<p align="center">
  <img src="https://img.shields.io/badge/license-GPL--3.0-blue">
  <img src="https://img.shields.io/badge/platform-Android-brightgreen">
  <img src="https://img.shields.io/badge/made%20with-Sketchware%20%26%20Termux-orange">
  <img src="https://img.shields.io/github/downloads/fora2323/Aexon/total">
</p>

---

## About Aexon

Aexon is a companion application for managing system-level shell daemons on Android, complete with native watchdog, signature verification, and dynamic theming (HCT/CAM16-based). Aexon's own code — native daemon, JNI, theming system, UI components — is written in-house. Some functionality integrates third-party open-source components; see [Third-Party Components](#third-party-components) below for full attribution.

## Inspiration

Aexon's interface design was inspired by several projects:

- **AxManager** — [https://github.com/fahrez182/AxManager](https://github.com/fahrez182/AxManager)
- **KernelSU Next** — [https://github.com/KernelSU-Next/KernelSU-Next](https://github.com/KernelSU-Next/KernelSU-Next)
- **Magisk Manager** — [https://github.com/topjohnwu/Magisk](https://github.com/topjohnwu/Magisk)
- **Shizuku** — [https://github.com/RikkaApps/Shizuku](https://github.com/RikkaApps/Shizuku)

> The inspiration listed above is on the design/UI side. For code-level integrations, see below.

## Third-Party Components

Aexon integrates the following third-party open-source components. Each retains its original license; inclusion here does not change Aexon's own GPL-3.0 licensing.

### Shizuku

Aexon uses the official Shizuku libraries (via Gradle dependencies) for permission/privilege integration. The app depends on the Shizuku API/provider packages and interacts with Shizuku at runtime; no upstream Shizuku source code is copied into Aexon's own codebase. The upstream Shizuku components remain under the Apache License 2.0.

### BusyBox

Aexon can optionally invoke BusyBox-style utilities at runtime. The app contains code paths that will call a BusyBox binary (or a bundled native helper) if present on the device or included in the app's native libraries. If BusyBox binaries are distributed together with this APK, they must be accompanied by the corresponding source or distribution notices required by BusyBox's GPL-2.0 license. If BusyBox is not bundled, Aexon will attempt to use the system/provided BusyBox when the user enables that mode.

## License

This project is licensed under the **GNU General Public License v3.0 (GPL-3.0)**.

This means:

- Free to use, study, modify, and redistribute  
- Every derivative/fork **must** remain open-source with the same license (GPL-3.0)  
- No warranty for the use of this application

Third-party components listed above retain their own original licenses (see [Third-Party Components](#third-party-components)).

See the [LICENSE](LICENSE) file for the full text.
