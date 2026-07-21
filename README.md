<p align="center">
  <img src="https://raw.githubusercontent.com/fora2323/Aexon/main/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" width="120">
</p>

<h1 align="center">Aexon</h1>

<p align="center">Root/system management companion for Android — dibangun sepenuhnya dari nol.</p>

<p align="center">
  <img src="https://img.shields.io/badge/license-GPL--3.0-blue">
  <img src="https://img.shields.io/badge/platform-Android-brightgreen">
  <img src="https://img.shields.io/badge/made%20with-Sketchware%20%26%20Termux-orange">
  <img src="https://img.shields.io/github/downloads/fora2323/Aexon/total">
</p>

---

## Tentang Aexon

Aexon adalah aplikasi companion untuk mengelola daemon shell tingkat sistem di Android, lengkap dengan native watchdog, signature verification, dan dynamic theming (HCT/CAM16-based). Seluruh kode — mulai dari native daemon (C++), JNI bridge, hingga UI — ditulis dari nol oleh satu orang menggunakan Sketchware sebagai IDE utama dan Termux untuk kompilasi native (Android NDK).

## Inspirasi

Desain antarmuka Aexon terinspirasi dari beberapa proyek berikut:

- **AxManager** — [github.com/fahrez182/AxManager](https://github.com/fahrez182/AxManager)
- **KernelSU Next** — [github.com/KernelSU-Next/KernelSU-Next](https://github.com/KernelSU-Next/KernelSU-Next)
- **Magisk Manager** — [github.com/topjohnwu/Magisk](https://github.com/topjohnwu/Magisk)
- **Shizuku** — [github.com/RikkaApps/Shizuku](https://github.com/RikkaApps/Shizuku)

> Untuk Shizuku, Aexon hanya menggunakan **library API resminya** (bukan meniru kode UI/UX-nya), untuk keperluan integrasi permission/privilege: [github.com/RikkaApps/Shizuku-API](https://github.com/RikkaApps/Shizuku-API)

Perlu ditegaskan: **inspirasi di sini murni pada sisi desain/UI**, bukan penggunaan ulang kode. Seluruh source code Aexon — daemon native, JNI, sistem theming, komponen UI — ditulis dari nol tanpa menyalin/fork dari proyek-proyek di atas atau proyek pihak ketiga manapun.

## Lisensi

Proyek ini dilisensikan di bawah **GNU General Public License v3.0 (GPL-3.0)**.

Ini berarti:

- Bebas digunakan, dipelajari, dimodifikasi, dan didistribusikan ulang
- Setiap turunan/fork **wajib** tetap open-source dengan lisensi yang sama (GPL-3.0)
- Tidak ada jaminan (warranty) atas penggunaan aplikasi ini

Lihat file [LICENSE](LICENSE) untuk teks lengkap.

## Disclaimer

Aplikasi ini dikembangkan untuk keperluan pribadi/edukasi seputar manajemen sistem Android tingkat root. Gunakan dengan risiko ditanggung sendiri.