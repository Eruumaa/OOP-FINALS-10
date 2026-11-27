# 🎲 BoardGameRent - Rent the Board!

**Aplikasi desktop penyewaan board game berbasis Java Swing (GUI)** untuk toko **Rent the Board!** sebagai tugas akhir UAS Praktikum Pemrograman Berorientasi Objek (PBO) Kelas B - **Kelompok 10**.

Dibangun dengan pemisahan layer `model`, `payment`, `service`, `exception`, dan `view` menggunakan antarmuka bertema **Cozy Pixel Tabletop** (lengkap dengan logo emblem pixel-art kustom), penyimpanan data persisten berbasis file teks (`data/akun.txt`, `data/barang.txt`, `data/transaksi.txt`, `data/invoices/`), serta dokumentasi **Javadoc** sesuai *Objects First with Java - Program Style Guide*.

---

## ✨ Fitur Utama

### 🔐 Autentikasi Multi-Peran (`LoginFrame`)
- Split login showcase dengan animasi pixel-art (`Assets/loginbg.gif`) dan form kartu tegas ber-border pixel.
- Login terpusat memakai satu daftar akun bersama (`ArrayList<Akun>`) untuk peran **Admin** dan **Penyewa**.
- Dilengkapi pengecekan `LoginGagalException` saat input kosong atau kredensial salah, tombol cepat `Enter`, dan opsi **Tampilkan password**.

### 🛠️ Panel Admin (`AdminFrame`)
- **Dashboard & Grafik**: Ringkasan KPI live (total pendapatan, denda, stok siap sewa, antrean aktif) dan 2 grafik batang beranimasi real-time dengan sorotan hover (katalog per kategori & distribusi status transaksi).
- **Kelola Barang**: Split View tabel ber-thumbnail foto + kartu preview detail di kanan, fitur cari, filter kategori, tombol reset, serta tambah (dengan unggah foto opsional), edit, dan hapus board game.
- **Transaksi Menunggu**: Tinjau pesanan masuk, lihat preview invoice, dan validasi ulang ketersediaan stok sebelum menyetujui penyewaan.
- **Pengembalian**: Catat tanggal kembali aktual (`LocalDate`) dengan tombol preset cepat (`Hari Ini`, `Tepat Tempo`, `+2 Hari`), pratinjau estimasi denda live, proses pelunasan denda, pulihkan status barang menjadi `Tersedia`, serta cetak invoice akhir.

### 🛒 Panel Penyewa (`PenyewaFrame`)
- **Daftar Barang**: Split View katalog ber-thumbnail foto + kartu showcase besar, filter kategori, pencarian langsung, tombol reset, dan rincian biaya kategori.
- **Keranjang Sewa & Saldo Wallet**: Kartu **Saldo Wallet** menampilkan saldo persisten penyewa lengkap dengan tombol **+ Top Up Wallet** melalui jalur pembayaran (`QRIS`, `Transfer Bank`, atau `Tunai`) beserta dialog konfirmasi bayar, tabel item bergambar, pengaturan lama sewa (`1 - 365` hari) dengan ringkasan estimasi total & jatuh tempo live, serta checkout memakai **QRIS**, **Transfer Bank**, **Tunai**, atau **Saldo Wallet**.
- **Riwayat & Statistik**: Kartu KPI pengeluaran penyewa, grafik transaksi per metode pembayaran (`QRIS`, `TransferBank`, `Tunai`, `Wallet`), kolom denda & status penyewaan, tombol **Batalkan Pesanan** (dengan refund saldo otomatis untuk pembayaran `Wallet`), serta cetak struk resmi berstempel status bayar via tombol **Lihat Invoice**.

---

## 🧩 Penerapan Konsep OOP

| Konsep | Implementasi di Proyek |
|---|---|
| **Abstraction** | Abstract class `Barang` (`hitungBiaya(int)`) dan `Akun`, serta interface `Pembayaran` (`prosesPembayaran(double)`) |
| **Inheritance** | `CardGame`, `FamilyBoardGame`, `StrategyGame` mewarisi `Barang`; `Admin`, `Penyewa` mewarisi `Akun` |
| **Polymorphism** | Rumus tarif berbeda pada tiap subclass `Barang` dan perilaku pembayaran pada `TransferBank`, `Tunai`, `QRIS`, serta `Wallet` |
| **Encapsulation** | Seluruh field `private` disertai validasi ketat di constructor dan setter/getter `public` |
| **Persistence** | `RentalDataStore` menyimpan dan memuat ulang `akun.txt`, `barang.txt`, `transaksi.txt`, dan arsip `invoices/` secara otomatis |
| **Collection** | `ArrayList<Barang>`, `ArrayList<Transaksi>`, dan `ArrayList<Akun>` dengan salinan aman (*defensive copy*) |
| **Exception Handling** | `LoginGagalException`, `BarangTidakTersediaException`, dan `DataTidakDitemukanException` |

---

## 📁 Struktur Proyek

```text
OOP-FINALS-10/
+-- Assets/
|   +-- BG01.jpg ... BG04.jpg, BG05.png, BG06.jpg, BG07.jpg
|   +-- loginbg.gif
|   `-- class_diagram_BoardGameRent_GUI.png
+-- data/
|   +-- akun.txt
|   +-- barang.txt
|   +-- transaksi.txt
|   `-- invoices/
+-- src/
|   +-- Main.java
|   +-- exception/
|   |   +-- BarangTidakTersediaException.java
|   |   +-- DataTidakDitemukanException.java
|   |   `-- LoginGagalException.java
|   +-- model/
|   |   +-- Admin.java
|   |   +-- Akun.java
|   |   +-- Barang.java
|   |   +-- CardGame.java
|   |   +-- DaftarBarang.java
|   |   +-- FamilyBoardGame.java
|   |   +-- Invoice.java
|   |   +-- KeranjangSewa.java
|   |   +-- Penyewa.java
|   |   +-- StrategyGame.java
|   |   `-- Transaksi.java
|   +-- payment/
|   |   +-- Pembayaran.java
|   |   +-- QRIS.java
|   |   +-- TransferBank.java
|   |   +-- Tunai.java
|   |   `-- Wallet.java
|   +-- service/
|   |   `-- RentalDataStore.java
|   `-- view/
|       +-- AdminFrame.java
|       +-- LoginFrame.java
|       +-- PenyewaFrame.java
|       `-- TemaUI.java
+-- .gitignore
+-- Makefile
`-- README.md
```

<details>
<summary>🗺️ Lihat Class Diagram (Versi GUI)</summary>

<br>

![Class Diagram BoardGameRent GUI](Assets/class_diagram_BoardGameRent_GUI.png)

</details>

---

## 🎮 Data Awal & Akun Demo

### Katalog 7 Board Game (3 Kategori)

| ID | Nama Board Game | Kategori | Tarif/Hari | Biaya Tambahan | Denda/Hari |
|---|---|---|---|---|---|
| `BG01` | UNO No Mercy | `CardGame` | Rp15.000 | - | Rp5.000 |
| `BG02` | Exploding Kittens | `CardGame` | Rp20.000 | - | Rp5.000 |
| `BG03` | Monopoly Classic | `FamilyBoardGame` | Rp25.000 | Garansi pion Rp10.000 (1x bayar) | Rp8.000 |
| `BG04` | Cluedo | `FamilyBoardGame` | Rp30.000 | Garansi pion Rp12.000 (1x bayar) | Rp8.000 |
| `BG05` | WNI Simulator | `FamilyBoardGame` | Rp28.000 | Garansi pion Rp10.000 (1x bayar) | Rp7.000 |
| `BG06` | Catan | `StrategyGame` | Rp40.000 | Modul Rp5.000/hari | Rp12.000 |
| `BG07` | Ticket to Ride | `StrategyGame` | Rp35.000 | Modul Rp5.000/hari | Rp10.000 |

### Akun Login Tersedia

| Peran | ID | Username | Password |
|---|---|---|---|
| 🛡️ Admin | `ADM01` | `admin` | `admin123` |
| 👤 Penyewa 1 | `P01` | `eruma` | `eruma123` |
| 👤 Penyewa 2 | `P02` | `irvan` | `irvan123` |
| 👤 Penyewa 3 | `P03` | `yogi` | `yogi123` |

---

## 🚀 Cara Menjalankan Aplikasi

Pastikan **JDK 17** atau versi lebih baru sudah terpasang di sistem Anda.

### 1. Kompilasi & Jalankan GUI

**Menggunakan Makefile (sekali jalan):**
```powershell
# Windows (MinGW Make)
mingw32-make
mingw32-make run

# Linux / macOS / Git Bash
make
make run
```

**Windows (PowerShell manual):**
```powershell
javac -d out -sourcepath src src/Main.java src/view/*.java src/service/*.java src/payment/*.java src/model/*.java src/exception/*.java
java -cp out Main
```

**Linux / macOS / Git Bash:**
```bash
javac -d out -sourcepath src $(find src -name "*.java")
java -cp out Main
```

### 2. Generate Dokumentasi Javadoc

**Windows (PowerShell):**
```powershell
$files = Get-ChildItem -Recurse src\*.java | ForEach-Object { $_.FullName }
javadoc -d docs -author -version $files
```

**Linux / macOS / Git Bash:**
```bash
javadoc -d docs -author -version $(find src -name "*.java")
```

---

## 🔄 Alur Demo Sistem (Skenario 1 sampai 8)

1. **Login Penyewa**: Masuk memakai akun `eruma`, `irvan`, atau `yogi` (atau daftar akun baru lewat tombol **Daftar Akun Penyewa**) melalui jendela `LoginFrame`.
2. **Lihat Katalog**: Cek daftar board game berstatus `Tersedia` pada tab **Daftar Barang**.
3. **Isi Keranjang**: Pilih satu atau lebih board game lalu klik **Tambah ke Keranjang**.
4. **Atur Lama Sewa**: Buka tab **Keranjang**, tentukan lama sewa (hari), dan cek estimasi biaya totalnya.
5. **Pilih Pembayaran & Konfirmasi Bayar**: Pilih metode `QRIS`, `Transfer Bank`, `Tunai`, atau `Saldo Wallet` (bisa top up terlebih dahulu melalui jalur bayar terkonfirmasi), klik **Buat Transaksi**, lalu selesaikan dialog simulasi pembayaran.
6. **Konfirmasi Admin**: Logout dan masuk sebagai `admin`, lalu setujui transaksi pada tab **Transaksi Menunggu** (status barang otomatis berubah menjadi `Sedang Disewa` dan terkunci dari penyewa lain; jika transaksi `Wallet` ditolak atau dibatalkan penyewa saat `Menunggu Konfirmasi`, saldo otomatis di-refund).
7. **Cek Riwayat & Invoice Lunas**: Login kembali sebagai penyewa, buka tab **Riwayat & Statistik**, lalu klik **Lihat Invoice** untuk menampilkan struk resmi berstatus `LUNAS`.
8. **Pengembalian & Denda**: Masuk lagi sebagai `admin`, buka tab **Pengembalian**, masukkan tanggal kembali (`yyyy-MM-dd`), lalu klik **Konfirmasi Pengembalian** untuk menghitung denda keterlambatan (bila ada) dan mengembalikan status barang menjadi `Tersedia`.

---

## 👥 Anggota Kelompok 10

| Peran | Nama | GitHub | Ruang Lingkup |
|---|---|---|---|
| **Anggota 1** | Muhammad Aqil Mubarak | [@Eruumaa](https://github.com/Eruumaa) | `Barang` & 3 subclass, `DaftarBarang`, `Admin`, `AdminFrame`, `TemaUI`, integrasi & pengumpulan |
| **Anggota 2** | Irvan Almidar | [@irvanalmidar2](https://github.com/irvanalmidar2) | `Pembayaran` & 3 implementasi, `Transaksi`, `Invoice`, `Penyewa`, `PenyewaFrame` |
| **Anggota 3** | M. Yogi Kafrawi | [@YogiShiki](https://github.com/YogiShiki) | `Akun`, 3 Custom Exception, `KeranjangSewa`, `LoginFrame`, `Main`, fondasi `README.md` |
