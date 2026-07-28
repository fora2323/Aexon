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

- **AxManager** — [github.com/fahrez182/AxManager](https://github.com/fahrez182/AxManager)
- **KernelSU Next** — [github.com/KernelSU-Next/KernelSU-Next](https://github.com/KernelSU-Next/KernelSU-Next)
- **Magisk Manager** — [github.com/topjohnwu/Magisk](https://github.com/topjohnwu/Magisk)
- **Shizuku** — [github.com/RikkaApps/Shizuku](https://github.com/RikkaApps/Shizuku)

> The inspiration listed above is on the design/UI side. For code-level integrations, see below.

## Third-Party Components

Aexon integrates the following third-party open-source components. Each retains its original license; inclusion here does not change Aexon's own GPL-3.0 licensing.

### Shizuku

<!-- PILIH SALAH SATU SESUAI CARA INTEGRASI TEMEN KAMU -->

<!-- OPSI A — kalau cuma pakai Shizuku-API resmi (dependency, bukan copy kode) -->
Aexon uses the official [Shizuku API](https://github.com/RikkaApps/Shizuku-API) library for permission/privilege integration, licensed under the **Apache License 2.0**. No Shizuku source code is copied into Aexon's own codebase.

<!-- OPSI B — kalau kode Shizuku (bukan cuma API) di-copy-paste / diadaptasi ke source Aexon -->
Aexon incorporates and adapts source code from [Shizuku](https://github.com/RikkaApps/Shizuku) by RikkaApps, licensed under the **Apache License 2.0**. Modified files retain their original copyright notice along with a note indicating the changes made, per the terms of the Apache License 2.0. See [`licenses/shizuku-LICENSE`](licenses/shizuku-LICENSE) and [`NOTICE`](NOTICE) for full attribution.

<!-- OPSI C — kalau di-embed sebagai module/submodule terpisah, tidak dicampur ke source Aexon -->
Aexon embeds [Shizuku](https://github.com/RikkaApps/Shizuku) by RikkaApps as a separate module, licensed under the **Apache License 2.0**. See [`licenses/shizuku-LICENSE`](licenses/shizuku-LICENSE) for the full license text.

### BusyBox

<!-- PILIH SALAH SATU SESUAI CARA DISTRIBUSI -->

<!-- OPSI A — busybox binary dibundle di dalam APK -->
Aexon bundles a BusyBox binary (via Magisk's build, originally from [BusyBox](https://busybox.net/)), licensed under **GPL-2.0**. In accordance with GPL-2.0, the corresponding source code is available at: `<link ke source busybox versi yang dipakai>`. If you received this binary without accompanying source, you may request it by opening an issue on this repository.

<!-- OPSI B — busybox binary tidak dibundle, hanya dipanggil/didownload dari sumber lain saat runtime -->
Aexon can optionally invoke a BusyBox binary (via Magisk, originally from [BusyBox](https://busybox.net/), licensed under GPL-2.0) if present on the device at runtime. Aexon does not bundle, redistribute, or modify this binary — it is expected to already be provided by the user's root/Magisk environment.

## License

This project is licensed under the **GNU General Public License v3.0 (GPL-3.0)**.

This means:

- Free to use, study, modify, and redistribute
- Every derivative/fork **must** remain open-source with the same license (GPL-3.0)
- No warranty for the use of this application

Third-party components listed above retain their own original licenses (see [Third-Party Components](#third-party-components)).

See the [LICENSE](LICENSE) file for the full text.