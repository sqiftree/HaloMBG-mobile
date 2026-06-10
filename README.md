# HaloMBG-mobile

Aplikasi Monitoring Program Makan Bergizi Gratis (MBG) untuk memantau distribusi makanan, menu harian, kualitas gizi sekolah, ulasan siswa, dokumentasi dari siswa, serta operasional SPPG. Proyek ini merupakan versi **Lite (Android Studio)** dari sistem monitoring web HaloMBG.

## Identitas Tim

| Nama Lengkap | NIM | Email |
|---|---|---|
| Firizqi Aditya Mulya | L0124016 | adityamulyaf@gmail.com |
| Yashif Victoriawan | L0124124 | yashif.vkt@gmail.com |
| Fairuz Shiba Alkhirza | L0124014 | fairuzziba@gmail.com |
| Nurman Aqil Wicaksono | L0124139 | nurmanaqil.25@gmail.com |

---

## Fitur Utama (Android Lite)

Aplikasi Android ini diimplementasikan menggunakan **Kotlin** dan **Material Design 3** dengan menyelaraskan visual guidelines (palette warna brand asli) dari spesifikasi desain:

1. **Pencarian Sekolah Publik**:
   * Bilah pencarian interaktif untuk menemukan sekolah dan dapur SPPG (Satuan Pelayanan Program Gizi) penanggung jawab.
   * Dilengkapi statistik real-time program MBG (jumlah sekolah, provinsi, dan dapur mitra).
2. **Login Multi-Aktor (Simulation-Ready)**:
   * Portal masuk yang memfasilitasi peran: **Siswa**, **SPPG (Dapur)**, **Guru**, dan **Admin**.
3. **Ulasan Gizi Harian (Siswa)**:
   * Menampilkan menu makanan hari ini beserta kandungan kalori, protein, karbohidrat, dan lemak.
   * Form pengiriman ulasan teks dan simulasi unggah foto masakan.
   * Log riwayat ulasan siswa lokal.
4. **Kelola Menu & Status Distribusi (SPPG/Dapur)**:
   * Form entri data menu gizi harian baru dilengkapi fitur validasi kandungan makanan otomatis berbasis AI (Gemini simulation).
   * Pembaruan status pengiriman makanan ke sekolah mitra (*Belum Diantar*, *Siap Diantar*, *Sudah Diantar*, *Batal*).
5. **Validasi Kelayakan Gizi AI**:
   * Workspace simulasi kamera pemindai piring makan siang lengkap dengan visualisasi garis pemindaian (*scanning animation line*).
   * Laporan penilaian gizi makro dan status kelayakan standar gizi program MBG beserta penyimpanan riwayat log harian.
6. **Moderasi Konten (Guru)**:
   * Panel moderasi ulasan siswa sekolah untuk menandai (*flagging*) ulasan yang tidak pantas.
7. **Ringkasan Master Data (Admin)**:
   * Pemantauan metrik agregat dan status hubungan antara sekolah dengan dapur SPPG penanggung jawab.

---

## Teknologi & Arsitektur Project

* **Language**: Kotlin
* **UI Framework**: Native XML (Material Design 3)
* **Sistem Build**: Gradle 8.2 (Android Gradle Plugin 8.2.2)
* **Data Persistence**: `MockData` (offline interactive database) dengan kelas data (POJO) yang sinkron dengan tabel Laravel PostgreSQL backend asli.
* **ViewBinding**: Aktif pada seluruh Activity & Fragment untuk mencegah crash akibat pointer null.

---

## Panduan Menjalankan Project

1. Pastikan Anda telah menginstal **Android Studio** (Koala / Ladybug atau versi terbaru).
2. Jalankan Android Studio, lalu pilih **File -> Open...** dan pilih direktori proyek `/home/vic/HaloMBG-mobile`.
3. Tunggu hingga proses sinkronisasi Gradle (Gradle Sync) selesai.
4. Hubungkan perangkat fisik Android dengan mode debugging aktif, atau gunakan emulator Android (min SDK 24 / Android 7.0).
5. Klik tombol **Run** (Ikon Play hijau) untuk mengompilasi dan menginstal aplikasi di perangkat Anda.
