<p align="center">
  <img src="icon.png" width="120" alt="Aexon Icon">
</p>

<h1 align="center">Aexon</h1>

<p align="center">
  Root/system management companion for Android — dibangun sepenuhnya dari nol.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/license-GPL--3.0-blue.svg" alt="License">
  <img src="https://img.shields.io/badge/platform-Android-3DDC84.svg" alt="Platform">
  <img src="https://img.shields.io/badge/made%20with-Sketchware%20%26%20Termux-00E5CC.svg" alt="Made with">
</p>

---

## Tentang Aexon

Aexon adalah aplikasi companion untuk mengelola daemon shell tingkat sistem di Android, lengkap dengan native watchdog, signature verification, dan dynamic theming (HCT/CAM16-based). Seluruh kode — mulai dari native daemon (C++), JNI bridge, hingga UI — ditulis dari nol oleh satu orang menggunakan Sketchware sebagai IDE utama dan Termux untuk kompilasi native (Android NDK).

## Inspirasi

Desain antarmuka Aexon terinspirasi dari beberapa proyek berikut:

- **KernelSU Next**
- **Magisk Manager**
- **Shizuku**

Perlu ditegaskan: **inspirasi di sini murni pada sisi desain/UI**, bukan penggunaan ulang kode. Seluruh source code Aexon — daemon native, JNI, sistem theming, komponen UI — ditulis dari nol tanpa menyalin/fork dari proyek-proyek di atas atau proyek pihak ketiga manapun.

## Fitur

- **Native watchdog daemon** — daemon C++ yang memantau dan otomatis me-restart shell server jika mati
- **Signature verification** — validasi SHA-256 native untuk mendeteksi tampering pada APK
- **Dynamic theming** — sistem warna berbasis CAM16/HCT untuk akurasi warna di dark mode
- **Shell server lokal** — komunikasi socket lokal (127.0.0.1) dengan autentikasi token
- **Live status monitoring** — status daemon, uptime real-time, SELinux context, dan info ABI/Android version

## Lisensi

Proyek ini dilisensikan di bawah **GNU General Public License v3.0 (GPL-3.0)**.

Ini berarti:
- Bebas digunakan, dipelajari, dimodifikasi, dan didistribusikan ulang
- Setiap turunan/fork **wajib** tetap open-source dengan lisensi yang sama (GPL-3.0)
- Tidak ada jaminan (warranty) atas penggunaan aplikasi ini

Lihat file [`LICENSE`](LICENSE) untuk teks lengkap.

## Disclaimer

Aexon adalah proyek pribadi, gratis, dan closed-development (dikembangkan oleh satu orang) namun tetap open-source sesuai ketentuan GPL-3.0. Aexon tidak berafiliasi dengan KernelSU Next, Magisk, Shizuku, atau proyek lain manapun yang disebutkan di atas.

---

<p align="center">Dibuat dengan Sketchware & Termux di atas ponsel.</p>
