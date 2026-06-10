# HaloMBG-mobile

Aplikasi Monitoring Program Makan Bergizi Gratis (MBG) untuk memantau distribusi makanan, menu harian, ulasan siswa, dokumentasi dari siswa, serta operasional SPPG. Proyek ini merupakan versi **Mobile Client (Android)** dari sistem monitoring HaloMBG.

## Identitas Tim

| Nama Lengkap | NIM | Email |
|---|---|---|
| Firizqi Aditya Mulya | L0124016 | adityamulyaf@gmail.com |
| Yashif Victoriawan | L0124124 | yashif.vkt@gmail.com |
| Fairuz Shiba Alkhirza | L0124014 | fairuzziba@gmail.com |
| Nurman Aqil Wicaksono | L0124139 | nurmanaqil.25@gmail.com |

---

## Fitur Utama (Android Client)

Aplikasi Android ini diimplementasikan menggunakan **Jetpack Compose** dan **Material Design 3** dengan menyelaraskan visual guidelines (palette warna brand asli, yaitu Emerald/Slate - Hijau Mint `#2E7D32` dan background abu-abu bersih `#F8F7F5`):

1. **Pencarian Sekolah Publik & Beranda**:
   * Bilah pencarian interaktif di beranda untuk menemukan sekolah dan melihat penanggung jawab dapur SPPG (Satuan Pelayanan Program Gizi) terkait.
   * Dilengkapi statistik singkat program MBG.
2. **Direktori Profil Dapur SPPG (Read-Only)**:
   * Fitur melihat detail informasi profil dapur SPPG (nama dapur, alamat, kapasitas porsi, penanggung jawab, kontak WhatsApp/Telepon, deskripsi, dan menu harian).
   * Bersifat read-only dan dapat diakses baik oleh tamu (guest) maupun pengguna terautentikasi (siswa, guru, kurir/SPPG).
3. **Login Multi-Peran Otomatis (Simulation-Ready)**:
   * Portal masuk terintegrasi tanpa dropdown pemilihan peran. Sistem mendeteksi peran secara otomatis berdasarkan kredensial surel (misal: surel mengandung `"siswa"` untuk Siswa, `"guru"` untuk Guru, `"sppg"` atau `"dapur"` untuk SPPG).
4. **Portal Ulasan Siswa Native (MOB-02)**:
   * Form pengiriman ulasan makanan harian (minimal 10 karakter) dilengkapi rating bintang.
   * Integrasi kamera native menggunakan `ActivityResultContracts.TakePicture()` untuk mengambil bukti foto makanan secara langsung dan mengirimkannya ke API.
5. **Status Distribusi & Bukti Pengiriman Kurir SPPG (MOB-03)**:
   * Layar jadwal pengantaran harian kurir/SPPG ke sekolah mitra.
   * Pembaruan status pengiriman (*Belum Diantar*, *Siap Diantar*, *Sudah Diantar*, *Batal*) dilengkapi foto bukti serah terima menggunakan kamera native dan timestamp otomatis.
6. **Moderasi Konten (Guru - MOB-04)**:
   * Umpan (feed) ulasan siswa dari sekolah bersangkutan untuk dipantau secara real-time.
   * Tombol cepat untuk memberi tanda bendera (*flagging*) ulasan siswa yang tidak layak/salah.
7. **Peringatan & Tindak Lanjut Ulasan Kritis (SPPG - MOB-05)**:
   * Deteksi otomatis kata kunci kritis pada ulasan siswa (misal: basi, bau, busuk, kotor).
   * Pemicu push notification real-time via FCM bagi pengelola SPPG untuk penanganan tindak lanjut segera.

---

## Teknologi & Arsitektur Project

* **Language**: Kotlin (Modern Coroutines & Flow)
* **UI Framework**: Jetpack Compose (Material Design 3)
* **Design System**: Emerald/Slate Palette (Hijau Mint `#2E7D32` & Abu Bersih `#F8F7F5`)
* **REST Client**: Retrofit2 & OkHttpClient (dengan `AuthInterceptor` untuk global Bearer token)
* **Data Persistence**: `EncryptedSharedPreferences` (penyimpanan token/sesi aman) & MockData (database offline untuk simulasi/interaktif)
* **Architecture Pattern**: MVVM (Model-View-ViewModel)

---

## Dokumentasi Tambahan

Untuk detail spesifikasi teknis dan perencanaan jadwal kerja, silakan merujuk ke dokumen berikut:
* 📄 [Product Backlog Mobile](file:///c:/Users/ACER/OneDrive/Dokumen/HaloMBG-mobile/docs/backlog-mobile.md) - Rincian backlog fitur (MOB-01 s.d. MOB-05).
* 📄 [Timeline Pengembangan](file:///c:/Users/ACER/OneDrive/Dokumen/HaloMBG-mobile/docs/timeline-mobile.md) - Jadwal pengerjaan 6 minggu.

---

## Panduan Menjalankan Project

1. Pastikan Anda telah menginstal **Android Studio** (Koala / Ladybug atau versi terbaru).
2. Jalankan Android Studio, lalu pilih **File -> Open...** dan arahkan ke direktori proyek `HaloMBG-mobile`.
3. Tunggu hingga proses sinkronisasi Gradle (Gradle Sync) selesai.
4. Hubungkan perangkat fisik Android dengan mode debugging aktif (USB Debugging), atau jalankan Emulator Android (min SDK 24 / Android 7.0).
5. Klik tombol **Run** (Ikon Play hijau) untuk mengompilasi dan menginstal aplikasi di perangkat Anda.

