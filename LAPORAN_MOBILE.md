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
Program Makan Bergizi Gratis (MBG) merupakan inisiatif nasional berskala besar yang membutuhkan pengelolaan logistik yang rumit, pencatatan nutrisi yang akurat, serta pengawasan mutu pangan dari hulu ke hilir. Sistem **HaloMBG** dikembangkan untuk menjadi garda depan dalam manajemen, transparansi, dan pemantauan distribusi ini. 

Meski pada mulanya diinisiasi dalam bentuk portal Web terpusat (berbasis Laravel dan ReactJS) untuk memfasilitasi peran administratif, ekosistem MBG tidak dapat berjalan secara optimal tanpa keterlibatan aktif para aktor lapangan. Proses penerimaan ulasan langsung dari siswa, dokumentasi penyaluran fisik oleh kurir dari dapur SPPG (Satuan Pelayanan Program Gizi), serta pengawasan disiplin oleh tenaga pengajar di lingkungan sekolah memerlukan perangkat lunak yang sigap dan dinamis. Menyikapi urgensi keterbatasan akses pada komputer desktop di area operasional tersebut, pengembangan versi **Klien Mobile (Aplikasi Android)** menjadi kewajiban yang tidak dapat dihindari.

### 1.2 Batasan Masalah
Laporan dokumentasi ini memiliki cakupan lingkup sebagai berikut:
1. Menitikberatkan pembahasan pada sisi klien aplikasi bergerak (*Mobile Client*) bersistem operasi Android yang berada di repositori `HaloMBG-mobile`.
2. Tidak mengkaji ulang struktur algoritma validasi AI (Gemini Vision) maupun penataan master data di server Laravel, kecuali jika hal tersebut bersinggungan langsung dengan respon *payload* *endpoint* API yang dimanfaatkan oleh ponsel.
3. Aplikasi difokuskan secara eksklusif untuk tiga aktor pengguna akhir (*end-user*): **Siswa, Guru, dan SPPG (Operator/Kurir)**.

### 1.3 Tujuan Pengembangan Aplikasi Mobile
Tujuan spesifik dari rancang bangun klien mobile adalah:
* **Digitalisasi Alur Lapangan:** Menggeser proses dokumentasi distribusi fisik dari pencatatan kertas atau pembaruan manual di meja kantor menjadi dokumentasi *real-time* berbasis perangkat bergerak.
* **Memanfaatkan Fitur Native Ponsel:** Memakai infrastruktur ponsel cerdas berupa kamera *native* untuk bukti *Proof of Delivery* dan pengambilan gambar makanan secara seketika.
* **Respon Krisis Instan:** Memangkas *feedback loop* antara keluhan murid dan aksi dapur melalui penggunaan Push Notification (FCM) jika terdeteksi krisis pada kualitas makanan (seperti terindikasi basi atau kotor).
* **Adaptasi Mudah bagi Warga Sekolah:** Mengizinkan pendaftaran akun dan akses ulasan secara independen oleh siswa dan guru langsung dari saku mereka.

---

## BAB II: ANALISIS KEBUTUHAN DAN PERBANDINGAN PLATFORM

### 2.1 Arsitektur Sistem Berbagi Pakai (Single Source of Truth)
Keseluruhan sistem HaloMBG mengimplementasikan arsitektur *Single Source of Truth* (SSOT). Artinya, antara Web (`praktikum-rpl-a-02`) dan Mobile (`HaloMBG-mobile`) tidak menggunakan *database* yang terpisah. Baik aplikasi web frontend berbasis ReactJS maupun klien mobile Android, seluruhnya bermuara pada gerbang layanan REST API terpusat buatan kerangka kerja Laravel yang berujung pada pangkalan data PostgreSQL. Aksi status yang diubah oleh kurir di jalan lewat HP akan secara seketika mengubah grafik *pie-chart* pemantauan yang dilihat oleh administrator di layar komputer mereka.

### 2.2 Identifikasi Peran Pengguna Aplikasi Mobile
Berdasarkan tinjauan operasional di lapangan, diputuskan bahwa hanya **tiga (3) dari empat (4)** *role* utama yang diperkenankan untuk diekspos melalui antarmuka layar sentuh kecil:
1. **Siswa:** Pengguna terbanyak. Hanya butuh ponsel Android untuk meregistrasi diri mereka ke sekolah, melihat menu gizi hari itu, memotret bekal di atas paha mereka di dalam kelas, dan mempublikasikan ulasan.
2. **Guru:** Memanfaatkan ponsel sambil berkeliling memantau aktivitas jam istirahat; memfasilitasi kemampuan merespons nakalnya tulisan ulasan siswa melalui pelaporan kilat (*flagging*).
3. **SPPG (Operator/Kurir):** Pekerja yang berada di atas sadel kendaraan atau sedang menurunkan kargo berisi ratusan boks bento, yang mutlak membutuhkan piranti satu tangan untuk memindai status dan menyerahkan laporan serah terima.

Sementara itu, peran **Administrator** dieliminasi dari sistem *mobile*, dikarenakan pendaftaran puluhan dapur SPPG baru dan konfigurasi relasi *mapping* sekolah dengan dapur tidaklah nyaman dilakukan lewat ukuran layar 6-inci.

### 2.3 Perbandingan Fungsionalitas (Product Backlog Web vs Mobile)
Merujuk kepada daftar 13 *Product Backlog* Web (BL) yang tertuang dalam dokumen spesifikasi `backlog.md`, di bawah ini adalah rasionalisasi bagaimana tiap-tiap fitur diterjemahkan atau tidak diterjemahkan ke dalam bentuk integrasi fitur Mobile (MOB):

| ID Backlog | Fungsionalitas / Modul | Status di Web | Status di Mobile | Analisis Fungsional dan Alasan Platform |
| :--- | :--- | :---: | :---: | :--- |
| **BL-01 / MOB-01** | Sistem Autentikasi & Manajemen Role | ✅ Ya | ✅ Ya | Modul kunci agar API berfungsi. Pada platform Mobile, *flow* registrasi akun (untuk Siswa & Guru) secara aktif dibuka agar *onboarding* aplikasi ke warga sekolah sangat masif, tanpa mereka harus membiasakan diri membuka Web terlebih dulu. |
| **BL-02** | Profil Dapur MBG & Daftar Sekolah | ✅ Ya | ✅ Ya *(Read-only)* | Operator Web memiliki fungsi CRUD untuk mengatur data profil. Namun di antarmuka Mobile, ini direduksi murni menjadi direktori brosur baca-saja (*read-only*) untuk menghemat kerumitan form. |
| **BL-03** | Pencarian SPPG Wilayah/Sekolah | ✅ Ya | ✅ Ya | Diadopsi pada layar awal (Beranda) aplikasi Android agar orang tua atau publik anonim bisa langsung mengecek siapa *vendor* sekolah anaknya tanpa harus memakan waktu mencari di peramban. |
| **BL-04** | Input Menu Harian oleh SPPG | ✅ Ya | ❌ Tidak | Form ini menuntut operator SPPG menginput data gramasi Karbohidrat, Protein, dsb. yang rentan terhadap *typo*. Ini wajib diketik tenang di atas *keyboard desktop* melalui platform Web. |
| **BL-05** | Validasi Nutrisi AI (Foto + Teks) | ✅ Ya | ❌ Tidak | Proses unggahan ini berjalan sepaket dengan BL-04. Interaksi *back-and-forth* koreksi dari respons Gemini Vision jauh lebih kaya secara visual bila dikerjakan di layar monitor luas (Web). |
| **BL-06** | Panel Admin: Master Data SPPG | ✅ Ya | ❌ Tidak | Pemetaan relasional basis data *many-to-many* untuk ribuan sekolah secara tegas merupakan fungsi meja kerja (*back-office*) Administrator. Absen penuh dari fungsi Mobile. |
| **BL-07 / MOB-03** | Status Distribusi Harian & Bukti Foto | ✅ Ya | ✅ Ya | **Core Feature.** Kurir memperbarui transisi *Proof of Delivery* di perjalanan secara gesit memakai antarmuka kartu berdesain lega dan mengakses lensa kamera ponsel tanpa transisi antar layar panjang. |
| **BL-08** | Notifikasi Keterlambatan Distribusi | ✅ Ya | ❌ Tidak *(Hanya via WA)*| Dipicu secara asinkron oleh skrip *CronJob/Scheduler* Web Laravel pada pukul 11:00 WIB guna menembakkan pesan *WhatsApp API*. Tidak perlu UI di ponsel. |
| **BL-09 / MOB-02** | Ulasan Harian dan Foto Siswa | ✅ Ya | ✅ Ya | **Fitur Inti.** Mengurangi friksi operasional dengan mengizinkan siswa langsung memotret makanan dan memberikan ulasan melalui ponsel secara seketika. |
| **BL-10 / MOB-04** | Moderasi Post-Publish Ulasan (Guru) | ✅ Ya | ✅ Ya | Mempermudah Guru dalam melakukan peninjauan. Guru dapat memantau umpan ulasan secara *mobile* dan langsung memberikan tanda (*flag*) pada ulasan yang terindikasi melanggar pedoman. |
| **BL-11 / MOB-04** | Sistem Notifikasi | ✅ Ya | ✅ Ya | Web memajang notifikasi pasif (*in-app*). Klien Mobile menggunakan teknologi *Firebase Cloud Messaging* (FCM) untuk menghasilkan *push notification* berprioritas tinggi. |
| **BL-12 / MOB-05** | Notifikasi Kritis & Tindak Lanjut SPPG | ✅ Ya | ✅ Ya | Pemberitahuan situasi kritis (seperti indikasi masalah kualitas makanan) akan segera disalurkan ke perangkat pihak SPPG melalui fitur *Push Notification*, sehingga memungkinkan penanganan cepat. |
| **BL-13** | Ringkasan Evaluasi AI Publik | ✅ Ya | ❌ Tidak | Visualisasi analitik tren agregasi evaluasi performa dapur akan lebih efektif dan informatif apabila diakses melalui layar lebar (Web). |

### 2.4 Rasionalisasi Pemilihan Platform Mobile
Berangkat dari tabel di atas, dapat ditarik kesimpulan tajam bahwa aplikasi Klien Mobile bukanlah alat sekadar kloning fitur Web. Ia merupakan alat spesialis (*special-purpose tool*) yang dipangkas tajam secara sengaja guna menyingkirkan fungsionalitas birokratis (formulir gizi, pembuatan SPPG) demi mewadahi tiga pilar terpenting operasional MBG di lapangan: **Mobilitas kurir, Kedaruratan komunikasi, dan Kecepatan dokumentasi native.**

---

## BAB III: ARSITEKTUR DAN PERANCANGAN SISTEM

### 3.1 Teknologi Inti dan Pola Arsitektur
Pengembangan antarmuka pengguna Android dibangun menggunakan pendekatan deklaratif modern dengan memanfaatkan pustaka **Jetpack Compose**, menggantikan metode imperatif berbasis XML.
* **Bahasa**: Kotlin (memanfaatkan keunggulan pemrosesan asinkron dari *Coroutines* dan *StateFlow* guna memastikan antarmuka tetap responsif).
* **Arsitektur**: Mematuhi pola **MVVM (Model-View-ViewModel)**. *View* (UI Compose) hanya bereaksi terhadap perubahan aliran data (*State*) murni dari *ViewModel*, di mana *ViewModel* bertugas mengelola logika bisnis yang diperoleh dari *Repository Layer*.
* **Manajemen Jaringan HTTP**: Melalui modul integrasi komunikasi jaringan **Retrofit2**, lengkap dengan utilitas serialisasi JSON GSON dan utilitas tambahan `OkHttp`.

### 3.2 Desain Antarmuka dan Filosofi Visual
Antarmuka aplikasi dirancang dengan mengedepankan fungsionalitas dan profesionalitas yang sesuai dengan peruntukannya sebagai infrastruktur layanan publik. Pendekatan visual yang diusung berpedoman pada prinsip *Warm Authority*:
* **Desain Profesional dan Minimalis**: Menghindari penggunaan elemen dekoratif yang berlebihan, animasi yang tidak esensial, maupun ornamen visual yang dapat mengganggu konsentrasi. Hal ini memastikan pengguna dapat fokus pada penyelesaian tugas utama di lapangan secara efisien.
* **Palet Warna Terukur**: Menggunakan kombinasi warna utama *Deep Navy* (`#071E49`) yang merepresentasikan otoritas dan kepercayaan, serta aksen pendukung *Fresh Green* (`#92D05D`), yang kesemuanya dipadukan dengan latar belakang *off-white* (`#F8F7F5`) guna menjamin kejernihan bacaan (*readability*) serta mengesankan kebersihan institusional. Warna *Emerald/Deep Green* (`#2E7D32`) hanya dialokasikan secara spesifik untuk indikator status sukses/berhasil.
* **Hierarki Tipografi yang Jelas**: Penempatan angka metrik krusial dipertegas menggunakan tipografi sans-serif modern yang tebal dan proporsi *white-space* yang cukup, sehingga tidak memerlukan garis pembatas tabel yang kaku.

### 3.3 Alur Autentikasi dan Manajemen Sesi Klien
Melanjutkan komitmen sekuritas API, integrasi koneksi antara Android dengan backend Laravel terjadi secara kokoh melalui *token-based authentication*.
1. Saat login, Android menerima JSON dengan entitas *Bearer Token* **Sanctum**.
2. Modul `AuthRepository.kt` bergegas mengambil *Token* tersebut untuk kemudian dienkripsi paksa ke ranah internal perangkat menggunakan **EncryptedSharedPreferences** yang ditangani oleh teknologi AES-256 bawaan *hardware* Google, bukan *Shared Preferences* polos yang gampang diretas.
3. Kapan pun aplikasi mengirimkan lembar ulasan atau menggeser status pengantaran, fungsi `AuthInterceptor` secara otomatis menyelipkan tanda pengenal *Bearer Token* tersebut ke leher (*Header*) permintaan jaringan agar *server* selalu mempercayai validitas sesi pekerja tersebut.

### 3.4 Integrasi Perangkat Keras dan Format Media
Fitur kamera pada ekosistem Android rentan sekali merusak memori karena rumitnya pustaka `CameraX` atau `Camera2`. Aplikasi HaloMBG-mobile melewati permasalahan itu dengan cermat berkat implementasi fitur bawaan OS modern, yaitu `ActivityResultContracts.TakePicture()`. Fitur ini:
* Memanggil piranti lunak kamera bawaan HP (apakah itu milik Samsung, Xiaomi, dsb.) secara utuh.
* Menyimpan berkas sementaranya ke *Local Cache*.
* Me- *resize* lalu melakukan *encoding* menjadi gumpalan teks panjang berformat **Data URI Base64**.
* Hal ini mengizinkan transfer string raksasa tersebut via *payload* REST biasa layaknya string teks. Oleh karenanya, server Laravel di seberang sana terbebas dari keharusan meracik *multipart form-data parsing*. Gambar tersebut kelak bermukim utuh di dalam kolom `TEXT` panjang *database* PostgreSQL.

---

## BAB IV: IMPLEMENTASI FITUR MOBILE (BACKLOG MOB)

Pada bab ini dijelaskan bagaimana fitur fungsional diterjemahkan ke dalam antar muka aplikasi (berdasarkan status spesifikasi dan implementasi dari *product backlog mobile* `MOB-01` s.d `MOB-05`).

### 4.1 MOB-01: Autentikasi Mobile, Registrasi, dan Manajemen Sesi
Aplikasi HaloMBG menyederhanakan layar awal dengan menghindari penggunaan *dropdown* pemilih *role* (*peran*). Pengguna dari segala jenis peran hanya perlu masuk melalui satu gerbang login utama yang terintegrasi.
* **Routing Berbasis Peran Otomatis (Role-Based Routing)**: Saat pengguna memasukkan kredensial yang valid (misalnya menggunakan akun *seeder* pengujian `siswa@halombg.com` atau akun pengguna riil lainnya), API *backend* Laravel akan memvalidasinya dan mengembalikan identitas `role` dari pengguna tersebut. Aplikasi *mobile* kemudian secara dinamis membaca `role` ini (Siswa, Guru, atau SPPG) dan langsung mengarahkan (*routing*) antarmuka menuju *dashboard* spesifik mereka tanpa perlu seleksi manual tambahan.
* **Fasilitas Registrasi Mandiri**: Bagi siswa dan guru yang baru tergabung dalam program ini, tersedia gerbang pendaftaran *native* dengan formulir masukan kredensial (seperti Nomor Induk Siswa Nasional / NISN) dan pemetaan asal instansi sekolah secara langsung di dalam gawai.

![Placeholder Form Registrasi & Login](https://placehold.co/800x400/F8F7F5/2E7D32?text=Layar+Login+%26+Registrasi+Siswa+Guru)

### 4.2 MOB-02: Portal Ulasan Siswa Native
Layar operasional utama bagi siswa. Saat pembagian makanan berlangsung, siswa dapat secara langsung mendokumentasikan dan memberikan ulasan terhadap makanan yang diterima.
* **Antarmuka Minimalis**: Layar didominasi oleh tombol akses cepat **Ambil Foto**, serta komponen isian teks (dengan batas minimum karakter tertentu guna memastikan umpan balik yang kualitatif dan bermakna).
* **Dampak Fungsional**: Terjaminnya validitas data pelaporan berkat integrasi kamera secara langsung tanpa penundaan.

![Placeholder Ulasan Siswa](https://placehold.co/800x400/F8F7F5/2E7D32?text=Layar+Portal+Ulasan+Siswa+%2B+Kamera)

### 4.3 MOB-03: Manajemen Distribusi & Proof-of-Delivery (SPPG)
Dirancang secara ergonomis untuk menyesuaikan mobilitas kurir logistik.
* **Alur Penyerahan**: Daftar sekolah tujuan ditampilkan dalam format daftar yang dapat digulir. Saat proses pengantaran tiba, kurir dapat memilih sekolah yang bersangkutan untuk memunculkan menu interaktif (*Bottom Sheet Dialog*) di layar bagian bawah.
* **Pembaruan Status Cepat**: Kurir dapat memperbarui status (*Siap Diantar* atau *Sudah Diantar*) sekaligus mengambil gambar serah terima fisik secara langsung, yang kemudian memicu pembaruan status *real-time* ke sistem *backend* pusat.

![Placeholder Distribusi SPPG](https://placehold.co/800x400/F8F7F5/2E7D32?text=Layar+Jadwal+%26+Status+Distribusi+SPPG)

### 4.4 MOB-04: Moderasi Cepat oleh Guru
Antarmuka pemantauan *real-time* yang memuat ulasan harian siswa, khusus untuk dikelola oleh guru pada sekolah yang bersangkutan.
* **Penandaan Cepat (*Flagging*)**: Apabila terdeteksi ulasan yang tidak pantas, melanggar etika, atau memuat gambar yang tidak relevan, guru dapat segera mengetuk tombol penanda untuk mengisolasi ulasan tersebut agar tidak memengaruhi statistik evaluasi secara keseluruhan.

![Placeholder Moderasi Guru](https://placehold.co/800x400/F8F7F5/2E7D32?text=Layar+Umpan+Feed+Moderasi+Guru)

### 4.5 MOB-05: Tindak Lanjut Ulasan Kritis (SPPG)
Modul penanganan situasi kritis khusus bagi pengelola Dapur (SPPG) yang sangat krusial untuk menjaga transparansi dan kualitas respons program MBG.
* **Notifikasi Asinkron**: Modul *Firebase Cloud Messaging* berjalan di latar belakang untuk segera memberikan peringatan apabila sistem mendeteksi adanya ulasan yang memicu kata kunci kritis terkait kualitas makanan.
* **Siklus Pemecahan Masalah**: Layar ini mewajibkan pihak SPPG untuk meninjau krisis, merubah fase penanganan ke *Proses Tindak Lanjut*, melampirkan catatan investigasi atau solusi, hingga akhirnya menetapkan status penyelesaian menjadi *Selesai*.

![Placeholder Tindak Lanjut Kritis](https://placehold.co/800x400/F8F7F5/2E7D32?text=Layar+Ruang+Kritis+Ulasan+SPPG)

---

## BAB V: PENUTUP

### 5.1 Kesimpulan
Proses perekayasaan platform Klien Android `HaloMBG-mobile` sukses beroperasi seiring dan sejalan dengan struktur masif *backend* Laravel tanpa harus menderita kelebihan kapasitas fitur (*feature creep*). Keputusan fundamental yang radikal memilah fungsionalitas—memindahkan formulasi gizi makro serta master relasi database seutuhnya ke zona Web Desktop—telah berhasil mendedikasikan aplikasi Mobile ini murni untuk kebutuhan utilitas garda depan: Integrasi kamera *native*, peringatan notifikasi kilat, portabilitas kurir distribusi, moderasi santai, serta transparansi tanpa batas bagi segenap relawan akar rumput program gizi ini. Aplikasi seluler menjadi pelengkap gerak gesit program MBG.

### 5.2 Saran Pengembangan Mendatang
* **Mode Akses Offline (Sinkronisasi Antrean):** Sangat dianjurkan kelak menanamkan basis data `Room` *local caching*, mengingat tidak seluruh unit sarana sekolah berada di wilayah cakupan frekuensi seluler stabil. Data ulasan/bukti serah terima dapat tersimpan di laci memori HP, untuk otomatis ditembakkan serentak bila Kurir kembali menjangkau spot bersinyal (misal terintegrasi API `WorkManager`).
* **Enkripsi Geotagging:** Melampirkan cap lokasi valid (*GPS latitude/longitude*) saat pemotretan foto bekal/serah-terima (*Proof of Delivery*) agar membasmi modus kecurangan manipulasi foto di tempat yang jauh dari koordinat fasilitas sekolah. 
