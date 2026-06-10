# Product Backlog - HaloMBG Mobile App

## Daftar Backlog Mobile

| ID | Judul | Prioritas | Estimasi | Dependensi |
|----|-------|-----------|----------|------------|
| [MOB-01](#mob-01--autentikasi-mobile-dan-manajemen-sesi) | Autentikasi Mobile dan Manajemen Sesi | 🔴 Must-have | M | - |
| [MOB-02](#mob-02--portal-ulasan-siswa-native) | Portal Ulasan Siswa Native (Kamera Terintegrasi) | 🔴 Must-have | M | MOB-01 |
| [MOB-03](#mob-03--distribusi-dan-proof-of-delivery-kurir) | Status Distribusi & Proof-of-Delivery Kurir SPPG | 🔴 Must-have | L | MOB-01 |
| [MOB-04](#mob-04--moderasi-cepat-dan-notifikasi-guru) | Moderasi Cepat dan Notifikasi Guru | 🟠 Should-have | S | MOB-01, MOB-02 |
| [MOB-05](#mob-05--notifikasi-ulasan-kritis-real-time-sppg) | Notifikasi Ulasan Kritis Real-Time SPPG | 🟠 Should-have | S | MOB-01, MOB-02 |

---

## 🔴 Must-have (MVP Mobile)

### MOB-01 — Autentikasi Mobile dan Manajemen Sesi

| | |
|---|---|
| **Story terkait** | US-01 |
| **Estimasi** | M (Medium) |
| **Dependensi** | - |

Mekanisme login pengguna mobile menggunakan kredensial terdaftar (email & sandi) dan penyimpanan token secara aman di sisi klien untuk autentikasi API selanjutnya.

**Kriteria Selesai:**
- Form login dengan validasi email dan sandi.
- Berhasil bertukar token (Laravel Sanctum Bearer Token).
- Token disimpan aman menggunakan `EncryptedSharedPreferences` (Android).
- Pengguna otomatis masuk (*auto-login*) ketika membuka aplikasi jika token belum kedaluwarsa.

---

### MOB-02 — Portal Ulasan Siswa Native

| | |
|---|---|
| **Story terkait** | US-09 |
| **Estimasi** | M (Medium) |
| **Dependensi** | MOB-01 |

Antarmuka bagi siswa untuk menulis ulasan harian atas makanan MBG yang diterima langsung dari sekolah mereka, lengkap dengan kemampuan mengambil foto menggunakan kamera perangkat.

**Kriteria Selesai:**
- Aplikasi memeriksa status dapur SPPG terhubung via `/api/siswa/sppg-info`.
- Input ulasan teks (minimum 10 karakter).
- Integrasi kamera via `TakePicture` contract untuk mengambil foto makanan secara langsung.
- Mengirimkan payload ulasan beserta foto terenkode Base64 ke `/api/siswa/reviews`.

---

### MOB-03 — Distribusi dan Proof-of-Delivery Kurir

| | |
|---|---|
| **Story terkait** | US-08 |
| **Estimasi** | L (Large) |
| **Dependensi** | MOB-01 |

Fitur khusus kurir/SPPG untuk melihat jadwal pengantaran sekolah hari ini dan memperbarui status distribusi disertai unggahan foto bukti serah terima di tempat.

**Kriteria Selesai:**
- Menampilkan daftar sekolah tujuan pengantaran hari ini berdasarkan `/api/sppg/distribution`.
- Pilihan status distribusi: `belum_diantar`, `siap_diantar`, `sudah_diantar`, `batal`.
- Integrasi kamera untuk bukti pengiriman (`photo` payload Base64).
- Tombol ubah status sekali ketuk dengan timestamp serah terima otomatis.

---

## 🟠 Should-have

### MOB-04 — Moderasi Cepat dan Notifikasi Guru

| | |
|---|---|
| **Story terkait** | US-10 |
| **Estimasi** | S (Small) |
| **Dependensi** | MOB-01, MOB-02 |

Fitur bagi Guru untuk memantau ulasan dari sekolahnya saat di lapangan, menerima notifikasi push ketika ada ulasan baru dari siswa, dan memberikan *flag* cepat ulasan bermasalah.

**Kriteria Selesai:**
- Guru menerima notifikasi push (FCM) jika siswa di sekolahnya mengirim ulasan baru.
- Menampilkan umpan ulasan sekolah (`/api/guru/reviews`).
- Tombol cepat untuk memberi tanda bendera (*flag*) ulasan siswa bermasalah.

---

### MOB-05 — Notifikasi Ulasan Kritis Real-Time SPPG

| | |
|---|---|
| **Story terkait** | US-14 |
| **Estimasi** | S (Small) |
| **Dependensi** | MOB-01, MOB-02 |

Notifikasi darurat push kepada pengelola SPPG apabila siswa mengirimkan ulasan bermasalah (mengandung kata kunci kritis seperti basi/bau/busuk) untuk penanganan segera.

**Kriteria Selesai:**
- SPPG menerima notifikasi push darurat di perangkat mobile saat ulasan kritis masuk.
- Navigasi otomatis ke halaman penanganan tindak lanjut.
- Pilihan status tindak lanjut: `belum_diproses`, `dalam_proses`, `selesai`.

---

## Panduan Prompt Inisialisasi Android Studio (AI Assistant)

Anda dapat menyalin prompt instruksi di bawah ini ke AI Assistant di Android Studio (Gemini/Cursor) untuk memulai pengerjaan:

```markdown
Role: Senior Android Developer
Task: Inisialisasi Project Android Kotlin untuk Platform HaloMBG

Buatkan arsitektur dasar dan contoh implementasi kode Kotlin menggunakan Jetpack Compose, Retrofit untuk jaringan, dan Camera/Media Picker untuk fitur upload foto bukti, dengan spesifikasi peran dan endpoint berikut:

### 1. Struktur Arsitektur & Teknologi Utama
- Bahasa: Kotlin (Modern Kotlin Coroutines + Flow)
- UI: Jetpack Compose (Material 3 dengan tema HSL Sleek/Premium - warna dominan Hijau Mint/Gizi #2E7D32 dan background abu-abu bersih #F8F7F5)
- REST Client: Retrofit2 dengan Gson converter
- Auth: Bearer Token Auth (disimpan aman di EncryptedSharedPreferences)
- Kamera: Menggunakan ActivityResultContracts.TakePicture() untuk kemudahan mengambil foto tanpa setup CameraX yang rumit.

### 2. Fitur & Endpoint untuk Diimplementasikan

#### PERAN A: SISWA (Review Portal)
Siswa menggunakan mobile app untuk mengirim ulasan makanan harian beserta bukti foto saat di sekolah.
- GET /api/siswa/sppg-info
  - Memeriksa apakah sekolah siswa terhubung ke dapur SPPG.
  - Response: { served: Boolean, kitchen_name: String, id: Int }
- POST /api/siswa/reviews
  - Mengirim ulasan baru.
  - Request Body: { content: String, review_date: String (yyyy-MM-dd), photo: String (Base64 encoded image atau Multipart) }

#### PERAN B: KURIR / OPERATOR SPPG (Distribusi Makanan)
Kurir mengantar makanan keliling ke sekolah dan memperbarui status serah terima secara real-time.
- GET /api/sppg/distribution?date=yyyy-MM-dd
  - Mengambil daftar sekolah yang dilayani dan status distribusinya.
- PUT /api/sppg/distribution/{id}
  - Memperbarui status distribusi (belum_diantar, siap_diantar, sudah_diantar, batal) disertai foto serah terima sebagai bukti.
  - Request Body: { status: String, photo: String }

#### PERAN C: GURU (Moderasi Cepat)
Guru memantau ulasan dari sekolahnya saat di lapangan.
- GET /api/guru/reviews
  - Melihat ulasan siswa dari sekolah bersangkutan.
- POST /api/guru/reviews/{id}/flag
  - Melakukan flagging / penandaan ulasan siswa yang dinilai tidak pantas.

---

### Kode yang Dibutuhkan:

Tolong buatkan implementasi file Kotlin berikut yang bersih, modular, dan mengikuti standard MVVM architecture:

1. **`NetworkModule.kt`**: Setup Retrofit, OkHttpClient dengan AuthInterceptor untuk menyisipkan Bearer token, dan API interfaces.
2. **`AuthRepository.kt`**: Helper untuk menyimpan dan mengambil token dari EncryptedSharedPreferences.
3. **`ReviewViewModel.kt` & `ReviewScreen.kt` (Siswa)**:
   - Form input ulasan (min 10 karakter).
   - Tombol "Ambil Foto" yang membuka kamera HP, menyimpan hasil foto, mengonversinya ke Base64, dan menampilkan pratinjau foto di layar sebelum dikirim.
4. **`DistributionViewModel.kt` & `DistributionScreen.kt` (Kurir SPPG)**:
   - Menampilkan list sekolah dengan status berupa badge warna yang modern (Merah: Batal/Belum, Kuning: Siap Diantar, Hijau: Sudah Diantar).
   - Saat item diklik, munculkan BottomSheet/Dialog untuk mengganti status dan mengambil bukti foto pengiriman dari Kamera.
5. **`Theme.kt`**: Konfigurasi HSL Sleek colors (Emerald/Slate theme per HaloMBG brand guidelines).
```