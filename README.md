<p align="center">
  <img src="https://raw.githubusercontent.com/fora2323/Aexon/main/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" width="120">
</p>

<h1 align="center">Aexon</h1>

<p align="center">Root/system management companion for Android — built entirely from scratch</p>

<p align="center">
  <img src="https://img.shields.io/badge/license-GPL--3.0-blue">
  <img src="https://img.shields.io/badge/platform-Android-brightgreen">
  <img src="https://img.shields.io/badge/made%20with-Sketchware%20%26%20Termux-orange">
  <img src="https://img.shields.io/github/downloads/fora2323/Aexon/total">
</p>

---

## About Aexon

Aexon is a companion application for managing system-level shell daemons on Android, complete with native watchdog, signature verification, and dynamic theming (HCT/CAM16-based). The entire codebase is written from scratch without reusing code from other projects.

## Inspiration

Aexon's interface design was inspired by several projects:

- **AxManager** — [github.com/fahrez182/AxManager](https://github.com/fahrez182/AxManager)
- **KernelSU Next** — [github.com/KernelSU-Next/KernelSU-Next](https://github.com/KernelSU-Next/KernelSU-Next)
- **Magisk Manager** — [github.com/topjohnwu/Magisk](https://github.com/topjohnwu/Magisk)
- **Shizuku** — [github.com/RikkaApps/Shizuku](https://github.com/RikkaApps/Shizuku)

> For Shizuku, Aexon only uses the **official API library** (not mimicking the UI/UX code) for permission/privilege integration purposes: [github.com/RikkaApps/Shizuku-API](https://github.com/RikkaApps/Shizuku-API)

It must be emphasized: **the inspiration here is purely on the design/UI side**, not code reuse. All of Aexon's source code — native daemon, JNI, theming system, UI components — is written from scratch without borrowing from other projects.

## License

This project is licensed under the **GNU General Public License v3.0 (GPL-3.0)**.

This means:

- Free to use, study, modify, and redistribute
- Every derivative/fork **must** remain open-source with the same license (GPL-3.0)
- No warranty for the use of this application

See the [LICENSE](LICENSE) file for the full text.
