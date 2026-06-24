# LAPORAN PENGEMBANGAN APLIKASI MOBILE HALOMBG

---

## DAFTAR ISI

1. [BAB I: PENDAHULUAN](#bab-i-pendahuluan)
   - [1.1 Latar Belakang Masalah](#11-latar-belakang-masalah)
   - [1.2 Batasan Masalah](#12-batasan-masalah)
   - [1.3 Tujuan Pengembangan Aplikasi Mobile](#13-tujuan-pengembangan-aplikasi-mobile)
2. [BAB II: ANALISIS KEBUTUHAN DAN PERBANDINGAN PLATFORM](#bab-ii-analisis-kebutuhan-dan-perbandingan-platform)
   - [2.1 Arsitektur Sistem Berbagi Pakai (Single Source of Truth)](#21-arsitektur-sistem-berbagi-pakai-single-source-of-truth)
   - [2.2 Identifikasi Peran Pengguna Aplikasi Mobile](#22-identifikasi-peran-pengguna-aplikasi-mobile)
   - [2.3 Perbandingan Fungsionalitas (Product Backlog Web vs Mobile)](#23-perbandingan-fungsionalitas-product-backlog-web-vs-mobile)
   - [2.4 Rasionalisasi Pemilihan Platform Mobile](#24-rasionalisasi-pemilihan-platform-mobile)
3. [BAB III: ARSITEKTUR DAN PERANCANGAN SISTEM](#bab-iii-arsitektur-dan-perancangan-sistem)
   - [3.1 Teknologi Inti dan Pola Arsitektur](#31-teknologi-inti-dan-pola-arsitektur)
   - [3.2 Desain Antarmuka dan Filosofi Visual (Design System)](#32-desain-antarmuka-dan-filosofi-visual-design-system)
   - [3.3 Alur Autentikasi dan Manajemen Sesi Klien](#33-alur-autentikasi-dan-manajemen-sesi-klien)
   - [3.4 Integrasi Perangkat Keras dan Format Media](#34-integrasi-perangkat-keras-dan-format-media)
4. [BAB IV: IMPLEMENTASI FITUR MOBILE (BACKLOG MOB)](#bab-iv-implementasi-fitur-mobile-backlog-mob)
   - [4.1 MOB-01: Autentikasi Mobile, Registrasi, dan Manajemen Sesi](#41-mob-01-autentikasi-mobile-registrasi-dan-manajemen-sesi)
   - [4.2 MOB-02: Portal Ulasan Siswa Native](#42-mob-02-portal-ulasan-siswa-native)
   - [4.3 MOB-03: Manajemen Distribusi & Proof-of-Delivery (SPPG)](#43-mob-03-manajemen-distribusi--proof-of-delivery-sppg)
   - [4.4 MOB-04: Moderasi Cepat oleh Guru](#44-mob-04-moderasi-cepat-oleh-guru)
   - [4.5 MOB-05: Tindak Lanjut Ulasan Kritis (SPPG)](#45-mob-05-tindak-lanjut-ulasan-kritis-sppg)
5. [BAB V: PENUTUP](#bab-v-penutup)
   - [5.1 Kesimpulan](#51-kesimpulan)
   - [5.2 Saran Pengembangan Mendatang](#52-saran-pengembangan-mendatang)

---

## BAB I: PENDAHULUAN

### 1.1 Latar Belakang Masalah
Program Makan Bergizi Gratis (MBG) merupakan inisiatif nasional berskala besar yang membutuhkan pengelolaan logistik yang komprehensif, pencatatan nutrisi yang akurat, serta pengawasan mutu pangan dari hulu ke hilir. Sistem **HaloMBG** dikembangkan untuk menjadi pilar utama dalam manajemen, transparansi, dan pemantauan distribusi ini. 

Meskipun pada awalnya diinisiasi dalam bentuk portal web terpusat (berbasis Laravel dan ReactJS) untuk memfasilitasi peran administratif, ekosistem MBG tidak dapat berjalan secara optimal tanpa keterlibatan aktif para aktor lapangan. Proses penerimaan ulasan langsung dari siswa, dokumentasi penyaluran fisik oleh kurir dari dapur SPPG (Satuan Pelayanan Program Gizi), serta pengawasan disiplin oleh tenaga pengajar di lingkungan sekolah memerlukan perangkat lunak yang adaptif dan responsif. Menyikapi urgensi keterbatasan akses pada komputer desktop di area operasional tersebut, pengembangan versi **Klien Seluler (Aplikasi Android)** menjadi solusi penting yang harus diimplementasikan.

### 1.2 Batasan Masalah
Laporan dokumentasi ini memiliki cakupan lingkup sebagai berikut:
1. Menitikberatkan pembahasan pada sisi klien aplikasi seluler (*Mobile Client*) bersistem operasi Android yang berada di repositori `HaloMBG-mobile`.
2. Tidak mengkaji ulang struktur algoritma validasi AI (Gemini Vision) maupun penataan master data di server Laravel, kecuali jika hal tersebut bersinggungan langsung dengan respons *payload* *endpoint* API yang dimanfaatkan oleh aplikasi seluler.
3. Aplikasi difokuskan secara eksklusif untuk tiga aktor pengguna akhir (*end-user*): **Siswa, Guru, dan SPPG (Operator/Kurir)**.

### 1.3 Tujuan Pengembangan Aplikasi Mobile
Tujuan spesifik dari rancang bangun klien seluler adalah:
* **Digitalisasi Alur Lapangan:** Menggeser proses dokumentasi distribusi fisik dari pencatatan manual berbasis kertas menjadi dokumentasi secara waktu-nyata (*real-time*) melalui perangkat seluler.
* **Memanfaatkan Fitur Native Perangkat:** Menggunakan fitur kamera bawaan (*native*) perangkat seluler untuk kebutuhan bukti pengiriman (*Proof of Delivery*) dan pengambilan foto makanan secara langsung.
* **Respon Krisis Instan:** Mempercepat alur koordinasi (*feedback loop*) antara keluhan siswa dan tindakan korektif pihak dapur SPPG dengan mengintegrasikan layanan *Push Notification* (FCM) saat terdeteksi keluhan kritis terkait kualitas makanan (seperti indikasi makanan basi atau tidak higienis).
* **Adaptasi Mudah bagi Warga Sekolah:** Memungkinkan pendaftaran akun dan akses ulasan secara mandiri oleh siswa dan guru langsung melalui perangkat seluler masing-masing.

---

## BAB II: ANALISIS KEBUTUHAN DAN PERBANDINGAN PLATFORM

### 2.1 Arsitektur Sistem Berbagi Pakai (Single Source of Truth)
Keseluruhan sistem HaloMBG mengimplementasikan arsitektur *Single Source of Truth* (SSOT), di mana aplikasi web (`praktikum-rpl-a-02`) dan aplikasi seluler (`HaloMBG-mobile`) menggunakan basis data terintegrasi yang sama. Baik frontend web berbasis ReactJS maupun klien seluler Android terhubung ke layanan REST API terpusat yang dibangun menggunakan kerangka kerja Laravel dan didukung oleh sistem manajemen basis data PostgreSQL. Setiap pembaruan status pengiriman yang dilakukan oleh kurir melalui perangkat seluler akan memperbarui visualisasi diagram lingkaran (*pie chart*) pemantauan pada dasbor administrator secara waktu-nyata (*real-time*).

### 2.2 Identifikasi Peran Pengguna Aplikasi Mobile
Berdasarkan analisis kebutuhan lapangan, disepakati bahwa hanya **tiga (3) dari empat (4)** peran utama yang diakomodasi dalam antarmuka aplikasi seluler:
1. **Siswa:** Pengguna dengan jumlah terbesar. Siswa menggunakan aplikasi Android untuk melakukan pendaftaran secara mandiri ke sekolah masing-masing, melihat menu gizi harian, mengambil foto makanan yang disajikan, serta mengirimkan ulasan secara langsung.
2. **Guru:** Menggunakan aplikasi untuk memantau aktivitas siswa secara fleksibel selama berada di lingkungan sekolah. Guru memiliki wewenang untuk meninjau ulasan siswa dan melakukan tindakan penandaan cepat (*flagging*) terhadap ulasan yang dinilai melanggar etika atau ketentuan penggunaan.
3. **SPPG (Operator/Kurir):** Petugas lapangan dengan mobilitas tinggi (seperti saat mengendarai armada pengantaran atau menurunkan muatan boks makanan) yang membutuhkan antarmuka praktis untuk dioperasikan dengan satu tangan guna memindai status pengiriman serta mengunggah laporan serah terima secara cepat.

Sementara itu, peran **Administrator** tidak diintegrasikan ke dalam platform seluler karena kompleksitas pengelolaan data, seperti registrasi unit dapur SPPG baru dan konfigurasi pemetaan (*mapping*) hubungan antara sekolah dengan dapur, yang lebih efisien dilakukan melalui perangkat desktop.

### 2.3 Perbandingan Fungsionalitas (Product Backlog Web vs Mobile)
Merujuk kepada daftar 13 *Product Backlog* Web (BL) yang tertuang dalam dokumen spesifikasi `backlog.md`, di bawah ini adalah rasionalisasi bagaimana tiap-tiap fitur diterjemahkan atau tidak diterjemahkan ke dalam bentuk integrasi fitur Mobile (MOB):

| ID Backlog | Fungsionalitas / Modul | Status di Web | Status di Mobile | Analisis Fungsional dan Alasan Platform |
| :--- | :--- | :---: | :---: | :--- |
| **BL-01 / MOB-01** | Sistem Autentikasi & Manajemen Role | ✅ Ya | ✅ Ya | Modul utama untuk otorisasi API. Pendaftaran akun Siswa & Guru diakomodasi langsung di aplikasi seluler untuk mempercepat proses *onboarding* pengguna di lingkungan sekolah secara luas tanpa harus mengakses platform web terlebih dahulu. |
| **BL-02** | Profil Dapur MBG & Daftar Sekolah | ✅ Ya | ✅ Ya *(Read-only)* | Pengguna platform web memiliki hak akses penuh (CRUD) untuk mengelola data profil. Pada aplikasi seluler, data ini disajikan dalam format baca-saja (*read-only*) guna menyederhanakan antarmuka. |
| **BL-03** | Pencarian SPPG Wilayah/Sekolah | ✅ Ya | ✅ Ya | Disediakan pada halaman utama (*Home*) aplikasi seluler guna memudahkan masyarakat umum atau orang tua siswa memeriksa informasi penyedia layanan (*vendor* SPPG) sekolah secara langsung tanpa harus melakukan pencarian manual melalui peramban web. |
| **BL-04** | Input Menu Harian oleh SPPG | ✅ Ya | ❌ Tidak | Proses penginputan data detail gizi harian (seperti berat gramasi karbohidrat, protein, dll.) memerlukan ketelitian tinggi guna menghindari kesalahan penulisan (*typo*). Oleh karena itu, pengisian data ini dialokasikan secara khusus melalui platform web. |
| **BL-05** | Validasi Nutrisi AI (Foto + Teks) | ✅ Ya | ❌ Tidak | Fungsionalitas ini terintegrasi erat dengan menu input harian (BL-04). Proses validasi balik (*back-and-forth*) serta penyesuaian terhadap hasil analisis Gemini Vision lebih optimal dilakukan pada resolusi layar monitor yang lebih besar (Web). |
| **BL-06** | Panel Admin: Master Data SPPG | ✅ Ya | ❌ Tidak | Pengelolaan hubungan relasional basis data (*many-to-many*) berskala besar merupakan fungsi administratif kantor belakang (*back-office*) yang sepenuhnya dilakukan oleh Administrator melalui platform web. |
| **BL-07 / MOB-03** | Status Distribusi Harian & Bukti Foto | ✅ Ya | ✅ Ya | **Fitur Utama.** Memungkinkan kurir memperbarui status pengiriman (*Proof of Delivery*) secara efisien menggunakan antarmuka berbasis kartu (*card layout*) yang ergonomis dan mengakses kamera perangkat secara langsung. |
| **BL-08** | Notifikasi Keterlambatan Distribusi | ✅ Ya | ❌ Tidak *(Hanya via WA)*| Sistem mengirimkan pemberitahuan keterlambatan secara otomatis melalui integrasi API WhatsApp yang dijalankan oleh penjadwal tugas (*cron job*) di server web pada pukul 11:00 WIB. |
| **BL-09 / MOB-02** | Ulasan Harian dan Foto Siswa | ✅ Ya | ✅ Ya | **Fitur Utama.** Mempermudah siswa dalam menyampaikan ulasan harian beserta bukti foto makanan secara instan dan waktu-nyata (*real-time*). |
| **BL-10 / MOB-04** | Moderasi Post-Publish Ulasan (Guru) | ✅ Ya | ✅ Ya | Memudahkan guru melakukan pemantauan ulasan siswa secara seluler dan segera menandai (*flag*) ulasan yang dinilai melanggar etika atau ketentuan penggunaan. |
| **BL-11 / MOB-04** | Sistem Notifikasi | ✅ Ya | ✅ Ya | Jika platform web menyajikan notifikasi pasif di dalam aplikasi (*in-app*), maka aplikasi seluler menggunakan *Firebase Cloud Messaging* (FCM) untuk mengirimkan notifikasi dorong (*push notification*) secara waktu-nyata. |
| **BL-12 / MOB-05** | Notifikasi Kritis & Tindak Lanjut SPPG | ✅ Ya | ✅ Ya | Notifikasi instan mengenai kondisi kritis (misalnya laporan makanan tidak layak konsumsi) akan segera dikirimkan ke perangkat SPPG penerima untuk memicu tindakan korektif yang cepat. |
| **BL-13** | Ringkasan Evaluasi AI Publik | ✅ Ya | ❌ Tidak | Visualisasi grafik dan analisis tren performa dapur secara menyeluruh lebih efektif disajikan melalui dasbor web yang beresolusi tinggi. |

### 2.4 Rasionalisasi Pemilihan Platform Mobile
Berdasarkan analisis perbandingan di atas, aplikasi seluler dirancang secara khusus (*special-purpose tool*) untuk mendukung efisiensi kerja lapangan dengan meniadakan fitur administratif yang kompleks (seperti pengisian gizi dan konfigurasi database). Fokus utama aplikasi seluler adalah pada tiga aspek operasional krusial: **mobilitas kurir, efisiensi komunikasi darurat, dan integrasi fitur dokumentasi perangkat secara langsung (*native photo capture*).**

---

## BAB III: ARSITEKTUR DAN PERANCANGAN SISTEM

### 3.1 Teknologi Inti dan Pola Arsitektur
Pengembangan antarmuka pengguna Android dibangun menggunakan pendekatan deklaratif modern dengan memanfaatkan pustaka **Jetpack Compose**, menggantikan metode imperatif berbasis XML.
* **Bahasa**: Kotlin (memanfaatkan keunggulan pemrosesan asinkron dari *Coroutines* dan *StateFlow* guna memastikan antarmuka tetap responsif).
* **Arsitektur**: Mematuhi pola **MVVM (Model-View-ViewModel)**. *View* (UI Compose) hanya bereaksi terhadap perubahan aliran data (*State*) murni dari *ViewModel*, di mana *ViewModel* bertugas mengelola logika bisnis yang diperoleh dari *Repository Layer*.
* **Manajemen Jaringan HTTP**: Melalui modul integrasi komunikasi jaringan **Retrofit2**, lengkap dengan utilitas serialisasi JSON GSON dan utilitas tambahan `OkHttp`.

### 3.2 Desain Antarmuka dan Filosofi Visual
Antarmuka aplikasi dirancang dengan mengedepankan fungsionalitas dan profesionalitas yang sesuai dengan peruntukannya sebagai infrastruktur layanan publik. Pendekatan visual yang diusung berpedoman pada prinsip *Warm Authority*:
* **Desain Profesional dan Minimalis**: Elemen visual dirancang minimalis dengan menghindari animasi yang tidak esensial atau dekorasi berlebih yang dapat mengalihkan perhatian, sehingga pengguna dapat fokus menyelesaikan tugas operasional secara efisien.
* **Palet Warna Terukur**: Menggunakan warna utama *Deep Navy* (`#071E49`) untuk mencerminkan profesionalitas dan kepercayaan, dikombinasikan dengan warna aksen *Fresh Green* (`#92D05D`) dan latar belakang *off-white* (`#F8F7F5`) untuk tingkat keterbacaan (*readability*) yang optimal serta kesan bersih. Warna hijau emerald (`#2E7D32`) digunakan secara spesifik untuk indikator keberhasilan transaksi atau status sukses.
* **Hierarki Tipografi yang Jelas**: Hierarki visual dipertegas menggunakan tipografi sans-serif modern berbobot tebal untuk informasi numerik penting, didukung oleh tata letak ruang kosong (*white-space*) yang seimbang untuk menggantikan garis pembatas kaku.

### 3.3 Alur Autentikasi dan Manajemen Sesi Klien
Untuk menjamin keamanan pertukaran data API, otorisasi antara aplikasi Android dengan layanan backend Laravel dilakukan dengan metode autentikasi berbasis token (*token-based authentication*).
1. Saat berhasil masuk (*login*), aplikasi Android menerima respons JSON berisi token *Bearer* dari Laravel Sanctum.
2. Melalui komponen `AuthRepository.kt`, token tersebut disimpan secara aman di dalam perangkat menggunakan **EncryptedSharedPreferences** dengan enkripsi AES-256 yang didukung oleh perangkat keras (hardware-backed), guna mencegah risiko kebocoran data sesi yang biasa terjadi pada Shared Preferences standar.
3. Untuk setiap permintaan HTTP (seperti pengiriman ulasan atau pembaruan status pengiriman), komponen `AuthInterceptor` secara otomatis menyematkan token *Bearer* tersebut ke dalam *Header* permintaan agar dapat divalidasi oleh server.

### 3.4 Integrasi Perangkat Keras dan Format Media
Untuk mengoptimalkan penggunaan memori dan meminimalkan kerumitan integrasi modul kamera (seperti penggunaan pustaka `CameraX` atau `Camera2`), aplikasi mengimplementasikan fitur bawaan sistem operasi modern melalui `ActivityResultContracts.TakePicture()`. Mekanisme ini bekerja sebagai berikut:
* Sistem memanggil aplikasi kamera bawaan perangkat (native camera) secara langsung.
* Berkas citra sementara disimpan pada direktori penyimpanan lokal (*cache*).
* Gambar tersebut dikompresi (di-resize) dan dikodekan (*encoding*) menjadi representasi string Base64 dalam format Data URI.
* Data tersebut kemudian ditransmisikan sebagai payload teks standar melalui permintaan REST API. Metode ini menyederhanakan pemrosesan di sisi backend Laravel karena tidak memerlukan penguraian data formulir multibidang (*multipart form-data parsing*). Gambar tersebut disimpan langsung dalam kolom bertipe data `TEXT` pada database PostgreSQL.

---

## BAB IV: IMPLEMENTASI FITUR MOBILE (BACKLOG MOB)

Pada bab ini dijelaskan bagaimana fitur fungsional diterjemahkan ke dalam antar muka aplikasi (berdasarkan status spesifikasi dan implementasi dari *product backlog mobile* `MOB-01` s.d `MOB-05`).

### 4.1 MOB-01: Autentikasi Mobile, Registrasi, dan Manajemen Sesi
Aplikasi HaloMBG menyederhanakan halaman awal dengan meniadakan elemen pemilihan peran (*dropdown role selection*). Seluruh kategori pengguna mengakses aplikasi melalui satu pintu masuk (*login screen*) yang sama.
* **Pengalihan Berbasis Peran secara Otomatis (Role-Based Routing)**: Ketika pengguna memasukkan kredensial yang valid (seperti menggunakan akun pengujian `siswa@halombg.com`), API backend Laravel memverifikasi data tersebut dan mengembalikan informasi atribut `role` pengguna. Aplikasi seluler kemudian mendeteksi nilai peran tersebut secara dinamis (Siswa, Guru, atau SPPG) dan mengalihkan halaman ke dasbor spesifik masing-masing pengguna tanpa memerlukan langkah pemilihan manual tambahan.
* **Fasilitas Registrasi Mandiri**: Bagi siswa dan guru baru, aplikasi menyediakan fitur pendaftaran secara langsung (*native registration*) dengan melengkapi formulir kredensial (seperti Nomor Induk Siswa Nasional / NISN) serta menentukan institusi sekolah yang terdaftar.

![Placeholder Form Registrasi & Login](https://placehold.co/800x400/F8F7F5/2E7D32?text=Layar+Login+%26+Registrasi+Siswa+Guru)

### 4.2 MOB-02: Portal Ulasan Siswa Native
Merupakan halaman utama bagi siswa untuk melakukan dokumentasi dan memberikan penilaian secara langsung terhadap makanan yang disajikan pada hari tersebut.
* **Antarmuka Ergonomis**: Halaman ini dirancang secara terfokus dengan menonjolkan tombol aksi pengambilan foto serta area input ulasan tekstual (yang menerapkan validasi batas minimum karakter demi menjaga kualitas umpan balik).
* **Dampak Operasional**: Menjamin keabsahan dokumentasi berkat pemanfaatan fungsi kamera terintegrasi secara cepat.

![Placeholder Ulasan Siswa](https://placehold.co/800x400/F8F7F5/2E7D32?text=Layar+Portal+Ulasan+Siswa+%2B+Kamera)

### 4.3 MOB-03: Manajemen Distribusi & Proof-of-Delivery (SPPG)
Antarmuka yang dirancang dengan tata letak ergonomis untuk mempermudah operasional kurir logistik di lapangan.
* **Alur Distribusi**: Daftar institusi sekolah tujuan disajikan dalam bentuk daftar gulir. Saat kurir tiba di lokasi, kurir dapat memilih instansi sekolah yang dituju untuk menampilkan dialog interaktif di bagian bawah layar (*Bottom Sheet Dialog*).
* **Pembaruan Status Pengiriman**: Kurir dapat memperbarui status (seperti 'Siap Diantar' or 'Sudah Diantar') dan mengambil foto bukti penyerahan barang secara langsung. Aksi ini secara otomatis memperbarui status pada basis data pusat.

![Placeholder Distribusi SPPG](https://placehold.co/800x400/F8F7F5/2E7D32?text=Layar+Jadwal+%26+Status+Distribusi+SPPG)

### 4.4 MOB-04: Moderasi Cepat oleh Guru
Halaman pemantauan secara langsung bagi guru untuk mengawasi ulasan harian yang dikirimkan oleh siswa di lingkungan sekolah mereka.
* **Fitur Moderasi Instan (*Flagging*)**: Jika terdapat ulasan yang tidak layak, melanggar norma kesopanan, atau mengunggah gambar yang tidak relevan, guru dapat langsung menandai (*flag*) ulasan tersebut. Tindakan ini akan menyembunyikan ulasan dari publik dan mengeluarkannya dari perhitungan agregat penilaian.

![Placeholder Moderasi Guru](https://placehold.co/800x400/F8F7F5/2E7D32?text=Layar+Umpan+Feed+Moderasi+Guru)

### 4.5 MOB-05: Tindak Lanjut Ulasan Kritis (SPPG)
Modul penanganan kendala kualitas pangan yang ditujukan bagi pengelola dapur SPPG guna mempercepat tindakan korektif dan menjaga akuntabilitas program.
* **Pemberitahuan Latar Belakang (Push Notification)**: Mengintegrasikan layanan *Firebase Cloud Messaging* untuk mendeteksi ulasan bermakna kritis (seperti keluhan rasa, bau, atau kontaminasi) dan mengirimkan notifikasi instan kepada pengelola dapur secara asinkron.
* **Siklus Resolusi Masalah**: Antarmuka ini mengarahkan pihak SPPG untuk segera memeriksa keluhan, mengubah tahapan laporan menjadi status 'Proses Tindak Lanjut', mendokumentasikan tindakan perbaikan, hingga menyelesaikan status laporan menjadi 'Selesai'.

![Placeholder Tindak Lanjut Kritis](https://placehold.co/800x400/F8F7F5/2E7D32?text=Layar+Ruang+Kritis+Ulasan+SPPG)

---

## BAB V: PENUTUP

### 5.1 Kesimpulan
Pengembangan platform Android `HaloMBG-mobile` berhasil diimplementasikan secara selaras dengan sistem backend Laravel tanpa menimbulkan kendala penumpukan fitur (*feature creep*). Keputusan strategis untuk memisahkan fungsionalitas—dengan menempatkan kalkulasi nutrisi makro dan pengelolaan master relasi database sepenuhnya pada platform web desktop—membuat aplikasi seluler dapat terfokus sepenuhnya pada kebutuhan operasional garda terdepan. Hal ini mencakup integrasi kamera perangkat, pengiriman notifikasi dorong secara cepat, efisiensi kerja kurir distribusi, serta respons moderasi oleh guru guna menunjang kelancaran program Makan Bergizi Gratis.

### 5.2 Saran Pengembangan Mendatang
* **Mekanisme Penyimpanan Lokal & Sinkronisasi Offline**: Disarankan untuk mengintegrasikan basis data lokal `Room` guna mengantisipasi keterbatasan konektivitas internet di beberapa lokasi sekolah. Data ulasan dan bukti serah terima dapat tersimpan sementara pada penyimpanan lokal perangkat, dan secara otomatis disinkronkan ke server saat perangkat kembali mendeteksi sinyal internet yang stabil (misalnya dengan memanfaatkan pustaka `WorkManager`).
* **Verifikasi Lokasi (Geotagging)**: Menyertakan koordinat lokasi geografis (*GPS latitude & longitude*) pada metadata foto ulasan atau bukti serah terima (*Proof of Delivery*) untuk menjamin validitas pengiriman dan mencegah manipulasi data lokasi. 
