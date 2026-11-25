# BoardGameRent

Aplikasi penyewaan board game berbasis **Java Swing (GUI)** untuk tugas UAS Praktikum Pemrograman Berorientasi Objek (PBO). Aplikasi ini menerapkan konsep **Abstraction, Inheritance, Polymorphism, Encapsulation, Interface, Collection (`ArrayList`), Custom Exception**, serta dokumentasi **Javadoc** sesuai *Objects First with Java – Program Style Guide*.

## Fitur Utama

- **Autentikasi Login (`LoginFrame`)** dengan 2 peran akun: **Admin** dan **Penyewa**.
- **3 Kategori Board Game** dengan rumus perhitungan biaya sewa berbeda (`hitungBiaya(int hari)`):
  - `CardGame`: `tarifHarian * hari`
  - `FamilyBoardGame`: `(tarifHarian * hari) + biayaGaransiPion`
  - `StrategyGame`: `(tarifHarian + biayaModulHarian) * hari`
- **3 Metode Pembayaran** melalui interface `Pembayaran`: `TransferBank`, `Tunai`, dan `QRIS`.
- **Panel Admin (`AdminFrame`)**: kelola katalog barang (tambah, edit, hapus), lihat & terima transaksi menunggu (memvalidasi ulang ketersediaan barang), serta konfirmasi pengembalian dan perhitungan denda keterlambatan berbasis `LocalDate`.
- **Panel Penyewa (`PenyewaFrame`)**: lihat katalog barang tersedia, kelola keranjang sewa (`KeranjangSewa`), buat transaksi sewa dengan pilihan metode pembayaran, serta lihat riwayat transaksi dan cetak struk (`Invoice`).
- **Custom Exception**: `LoginGagalException`, `BarangTidakTersediaException`, dan `DataTidakDitemukanException`.

## Struktur Proyek

```text
OOP-FINALS-10/
├── Assets/
│   └── class_diagram_BoardGameRent_GUI.png
├── src/
│   ├── Akun.java
│   ├── Admin.java
│   ├── Penyewa.java
│   ├── Barang.java
│   ├── CardGame.java
│   ├── FamilyBoardGame.java
│   ├── StrategyGame.java
│   ├── DaftarBarang.java
│   ├── KeranjangSewa.java
│   ├── Pembayaran.java
│   ├── TransferBank.java
│   ├── Tunai.java
│   ├── QRIS.java
│   ├── Transaksi.java
│   ├── Invoice.java
│   ├── LoginGagalException.java
│   ├── BarangTidakTersediaException.java
│   ├── DataTidakDitemukanException.java
│   ├── LoginFrame.java
│   ├── AdminFrame.java
│   ├── PenyewaFrame.java
│   └── Main.java
└── README.md
```

## Cara Kompilasi dan Menjalankan

Pastikan **JDK 17** (atau lebih baru) sudah terpasang di komputer Anda.

1. Kompilasi seluruh source code di folder `src/`:
   ```bash
   javac -d out src/*.java
   ```
2. Jalankan aplikasi GUI:
   ```bash
   java -cp out Main
   ```
3. Login pada jendela **BoardGameRent** menggunakan akun **Admin** atau **Penyewa** yang telah diinisialisasi di `src/Main.java`.

## Cara Menghasilkan Dokumentasi Javadoc

```bash
javadoc -d docs -author -version src/*.java
```

## Alur Demo Sistem (Skenario 1–8)

1. Penyewa login ke aplikasi.
2. Penyewa melihat daftar board game yang tersedia.
3. Penyewa menambahkan satu atau lebih barang ke dalam keranjang sewa.
4. Penyewa menentukan lama sewa (hari) dan memilih metode pembayaran untuk membuat transaksi.
5. Admin login dan menerima transaksi menunggu (status barang berubah menjadi *Sedang Disewa*).
6. Transaksi beserta invoice tercetak masuk ke riwayat transaksi milik Penyewa.
7. Saat barang dikembalikan, Admin mengonfirmasi tanggal pengembalian, sistem menghitung denda keterlambatan bila melewati jatuh tempo, dan status barang kembali *Tersedia*.

## Daftar Anggota Kelompok

| Peran | Nama | Akun GitHub | Ruang Lingkup |
|---|---|---|---|
| Anggota 1 | Muhammad Aqil Mubarak | `@Eruumaa` | `Barang` & 3 subclass, `DaftarBarang`, `Admin`, `AdminFrame` |
| Anggota 2 | Irvan Almidar | `@irvanalmidar2` | `Pembayaran` & 3 implementasi, `Transaksi`, `Invoice`, `Penyewa`, `PenyewaFrame` |
| Anggota 3 | M Yogi Kafrawi | `@YogiShiki` | `Akun`, 3 Exception, `KeranjangSewa`, `LoginFrame`, `Main`, Dokumentasi |

## Teknologi

- Java (JDK 17+)
- Java Swing (`JFrame`, `JTable`, `JTabbedPane`, `JOptionPane`)
