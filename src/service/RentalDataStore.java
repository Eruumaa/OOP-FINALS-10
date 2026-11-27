package service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.Admin;
import model.Akun;
import model.Barang;
import model.CardGame;
import model.DaftarBarang;
import model.FamilyBoardGame;
import model.Invoice;
import model.Penyewa;
import model.StrategyGame;
import model.Transaksi;
import payment.Pembayaran;
import payment.QRIS;
import payment.TransferBank;
import payment.Tunai;
import payment.Wallet;

/**
 * Layanan penyimpanan data permanen berbasis file teks (.txt) di folder data/.
 *
 * Mengelola pembacaan dan penulisan data akun (termasuk saldo wallet penyewa),
 * katalog board game, riwayat transaksi, dan arsip struk invoice.
 *
 * @author Muhammad Aqil Mubarak
 * @version 2026.11.27
 */
public class RentalDataStore
{
    private static final String DIR_DATA = "data";
    private static final String FILE_AKUN = DIR_DATA + "/akun.txt";
    private static final String FILE_BARANG = DIR_DATA + "/barang.txt";
    private static final String FILE_TRANSAKSI = DIR_DATA + "/transaksi.txt";
    private static final String DIR_INVOICE = DIR_DATA + "/invoices";

    private DaftarBarang daftarBarang;
    private ArrayList<Akun> daftarAkun;
    private Admin admin;

    /**
     * Membuat instance RentalDataStore dan memuat seluruh data dari folder data/.
     * Jika file belum tersedia, data awal bawaan otomatis dibuat dan disimpan.
     */
    public RentalDataStore()
    {
        this.daftarBarang = new DaftarBarang();
        this.daftarAkun = new ArrayList<>();
        this.pastikanDirektori();
        this.muatBarang();
        this.muatAkun();
        this.muatTransaksi();
    }

    /**
     * Mengembalikan objek katalog barang bersama yang telah dimuat.
     *
     * @return objek DaftarBarang
     */
    public DaftarBarang getDaftarBarang()
    {
        return this.daftarBarang;
    }

    /**
     * Mengembalikan daftar seluruh akun (Admin dan Penyewa) yang tersimpan.
     *
     * @return salinan daftar akun
     */
    public ArrayList<Akun> getDaftarAkun()
    {
        return new ArrayList<>(this.daftarAkun);
    }

    /**
     * Mengembalikan daftar referensi internal akun agar pendaftaran akun baru dapat tersimpan.
     *
     * @return daftar akun internal yang dikelola store
     */
    public ArrayList<Akun> getDaftarAkunRef()
    {
        return this.daftarAkun;
    }

    /**
     * Mengembalikan objek Admin utama sistem.
     *
     * @return objek Admin aktif
     */
    public Admin getAdmin()
    {
        return this.admin;
    }

    /**
     * Menyimpan seluruh perubahan data akun, barang, dan transaksi sekaligus ke file .txt.
     */
    public void simpanSemua()
    {
        this.simpanAkun();
        this.simpanBarang();
        this.simpanTransaksi();
    }

    /**
     * Menyimpan daftar akun dan saldo wallet penyewa ke data/akun.txt.
     */
    public void simpanAkun()
    {
        this.pastikanDirektori();
        List<String> baris = new ArrayList<>();
        baris.add("# ROLE|ID|USERNAME|PASSWORD|SALDO_WALLET");
        for (Akun akun : this.daftarAkun) {
            if (akun instanceof Admin) {
                baris.add(String.join("|",
                    "ADMIN",
                    bersihkan(akun.getId()),
                    bersihkan(akun.getUsername()),
                    bersihkan(akun.getPassword()),
                    "0"
                ));
            } else if (akun instanceof Penyewa) {
                Penyewa penyewa = (Penyewa) akun;
                baris.add(String.join("|",
                    "PENYEWA",
                    bersihkan(penyewa.getId()),
                    bersihkan(penyewa.getUsername()),
                    bersihkan(penyewa.getPassword()),
                    String.valueOf(penyewa.getSaldoWallet())
                ));
            }
        }
        tulisBaris(FILE_AKUN, baris);
    }

    /**
     * Menyimpan katalog board game beserta status ketersediaannya ke data/barang.txt.
     */
    public void simpanBarang()
    {
        this.pastikanDirektori();
        List<String> baris = new ArrayList<>();
        baris.add("# KATEGORI|ID|NAMA|TARIF|DENDA|BIAYA_TAMBAHAN|TERSEDIA");
        for (Barang item : this.daftarBarang.getBarang()) {
            double tambahan = 0.0;
            if (item instanceof FamilyBoardGame) {
                tambahan = ((FamilyBoardGame) item).getBiayaGaransiPion();
            } else if (item instanceof StrategyGame) {
                tambahan = ((StrategyGame) item).getBiayaModulHarian();
            }
            baris.add(String.join("|",
                item.getClass().getSimpleName(),
                bersihkan(item.getId()),
                bersihkan(item.getNama()),
                String.valueOf(item.getTarifHarian()),
                String.valueOf(item.getDendaPerHari()),
                String.valueOf(tambahan),
                String.valueOf(item.isTersedia())
            ));
        }
        tulisBaris(FILE_BARANG, baris);
    }

    /**
     * Menyimpan seluruh daftar transaksi beserta status dan tanggal ke data/transaksi.txt.
     */
    public void simpanTransaksi()
    {
        this.pastikanDirektori();
        List<String> baris = new ArrayList<>();
        baris.add("# ID|ID_PENYEWA|IDS_BARANG|LAMA|TGL_SEWA|TGL_JATUH_TEMPO|TGL_KEMBALI|METODE|STATUS");
        for (Transaksi trx : this.admin.getDaftarTransaksi()) {
            StringBuilder idsBarang = new StringBuilder();
            ArrayList<Barang> daftarItem = trx.getBarang();
            for (int i = 0; i < daftarItem.size(); i++) {
                if (i > 0) {
                    idsBarang.append(",");
                }
                idsBarang.append(bersihkan(daftarItem.get(i).getId()));
            }
            String tglKembali = (trx.getTanggalKembali() == null)
                ? "-"
                : trx.getTanggalKembali().toString();
            baris.add(String.join("|",
                bersihkan(trx.getId()),
                bersihkan(trx.getPenyewa().getId()),
                idsBarang.toString(),
                String.valueOf(trx.getLamaSewa()),
                trx.getTanggalSewa().toString(),
                trx.getTanggalJatuhTempo().toString(),
                tglKembali,
                trx.getPembayaran().getClass().getSimpleName(),
                bersihkan(trx.getStatus())
            ));
        }
        tulisBaris(FILE_TRANSAKSI, baris);
    }

    /**
     * Mengarsipkan struk invoice transaksi ke folder data/invoices/.
     *
     * @param transaksi transaksi yang diarsipkan struknya
     */
    public void simpanInvoice(Transaksi transaksi)
    {
        if (transaksi == null) {
            return;
        }
        this.pastikanDirektori();
        File file = new File(DIR_INVOICE, "Invoice_" + transaksi.getId() + ".txt");
        try {
            Invoice invoice = new Invoice(transaksi);
            Files.writeString(file.toPath(), invoice.cetakInvoice(), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            // Abaikan kegagalan tulis arsip invoice opsional agar alur utama tetap berjalan
        }
    }

    private void pastikanDirektori()
    {
        File dirData = new File(DIR_DATA);
        if (!dirData.exists()) {
            dirData.mkdirs();
        }
        File dirInv = new File(DIR_INVOICE);
        if (!dirInv.exists()) {
            dirInv.mkdirs();
        }
    }

    private void muatBarang()
    {
        File file = new File(FILE_BARANG);
        if (!file.isFile()) {
            this.daftarBarang.tambahBarang(new CardGame("BG01", "UNO No Mercy", 15000, 5000));
            this.daftarBarang.tambahBarang(new CardGame("BG02", "Exploding Kittens", 20000, 5000));
            this.daftarBarang.tambahBarang(new FamilyBoardGame("BG03", "Monopoly Classic", 25000, 8000, 10000));
            this.daftarBarang.tambahBarang(new FamilyBoardGame("BG04", "Cluedo", 30000, 8000, 12000));
            this.daftarBarang.tambahBarang(new FamilyBoardGame("BG05", "WNI Simulator", 28000, 7000, 10000));
            this.daftarBarang.tambahBarang(new StrategyGame("BG06", "Catan", 40000, 12000, 5000));
            this.daftarBarang.tambahBarang(new StrategyGame("BG07", "Ticket to Ride", 35000, 10000, 5000));
            this.simpanBarang();
            return;
        }

        for (String line : bacaBaris(file)) {
            String[] p = line.split("\\|", -1);
            if (p.length < 7) {
                continue;
            }
            try {
                String kategori = p[0].trim();
                String id = p[1].trim();
                String nama = p[2].trim();
                double tarif = Double.parseDouble(p[3].trim());
                double denda = Double.parseDouble(p[4].trim());
                double tambahan = Double.parseDouble(p[5].trim());
                boolean tersedia = Boolean.parseBoolean(p[6].trim());

                Barang item;
                if ("FamilyBoardGame".equalsIgnoreCase(kategori)) {
                    item = new FamilyBoardGame(id, nama, tarif, denda, tambahan);
                } else if ("StrategyGame".equalsIgnoreCase(kategori)) {
                    item = new StrategyGame(id, nama, tarif, denda, tambahan);
                } else {
                    item = new CardGame(id, nama, tarif, denda);
                }
                item.setTersedia(tersedia);
                this.daftarBarang.tambahBarang(item);
            } catch (RuntimeException ignored) {
                // Lewati baris yang rusak agar data lain tetap terbaca
            }
        }
    }

    private void muatAkun()
    {
        File file = new File(FILE_AKUN);
        if (!file.isFile()) {
            this.admin = new Admin("ADM01", "admin", "admin123", this.daftarBarang);
            this.daftarAkun.add(this.admin);
            this.daftarAkun.add(new Penyewa("P01", "eruma", "eruma123", this.daftarBarang, 250000));
            this.daftarAkun.add(new Penyewa("P02", "irvan", "irvan123", this.daftarBarang, 250000));
            this.daftarAkun.add(new Penyewa("P03", "yogi", "yogi123", this.daftarBarang, 250000));
            this.simpanAkun();
            return;
        }

        for (String line : bacaBaris(file)) {
            String[] p = line.split("\\|", -1);
            if (p.length < 5) {
                continue;
            }
            try {
                String role = p[0].trim();
                String id = p[1].trim();
                String user = p[2].trim();
                String pass = p[3];
                double saldo = Double.parseDouble(p[4].trim());

                if ("ADMIN".equalsIgnoreCase(role)) {
                    Admin adm = new Admin(id, user, pass, this.daftarBarang);
                    if (this.admin == null) {
                        this.admin = adm;
                    }
                    this.daftarAkun.add(adm);
                } else if ("PENYEWA".equalsIgnoreCase(role)) {
                    this.daftarAkun.add(new Penyewa(id, user, pass, this.daftarBarang, saldo));
                }
            } catch (RuntimeException ignored) {
                // Lewati baris yang rusak agar akun lain tetap terbaca
            }
        }

        if (this.admin == null) {
            this.admin = new Admin("ADM01", "admin", "admin123", this.daftarBarang);
            this.daftarAkun.add(0, this.admin);
            this.simpanAkun();
        }
    }

    private void muatTransaksi()
    {
        File file = new File(FILE_TRANSAKSI);
        if (!file.isFile()) {
            this.simpanTransaksi();
            return;
        }

        int nomorMaks = 0;
        for (String line : bacaBaris(file)) {
            String[] p = line.split("\\|", -1);
            if (p.length < 9) {
                continue;
            }
            try {
                String idTrx = p[0].trim();
                String idPenyewa = p[1].trim();
                String[] idBarangArr = p[2].trim().split(",");
                int lama = Integer.parseInt(p[3].trim());
                LocalDate tglSewa = LocalDate.parse(p[4].trim());
                LocalDate tglTempo = LocalDate.parse(p[5].trim());
                LocalDate tglKembali = "-".equals(p[6].trim()) ? null : LocalDate.parse(p[6].trim());
                Pembayaran metode = buatPembayaran(p[7].trim());
                String status = p[8].trim();

                Penyewa penyewa = cariPenyewaById(idPenyewa);
                if (penyewa == null) {
                    continue;
                }

                ArrayList<Barang> barangTrx = new ArrayList<>();
                for (String idB : idBarangArr) {
                    Barang found = cariBarangById(idB.trim());
                    if (found != null) {
                        barangTrx.add(found);
                    }
                }
                if (barangTrx.isEmpty()) {
                    continue;
                }

                Transaksi trx = new Transaksi(
                    idTrx, penyewa, barangTrx, lama, tglSewa, tglTempo, tglKembali, status, metode
                );
                this.admin.tambahTransaksi(trx);
                penyewa.tambahRiwayat(trx);
                if (idTrx.toUpperCase().startsWith("TRX-")) {
                    try {
                        int n = Integer.parseInt(idTrx.substring(4));
                        if (n > nomorMaks) {
                            nomorMaks = n;
                        }
                    } catch (NumberFormatException ignored) {
                        // Abaikan ID transaksi non-standar
                    }
                }
            } catch (RuntimeException ignored) {
                // Lewati baris transaksi yang rusak
            }
        }

        if (nomorMaks > 0) {
            Penyewa.sinkronNomorTransaksi(nomorMaks + 1);
        }
    }

    private Penyewa cariPenyewaById(String idPenyewa)
    {
        for (Akun akun : this.daftarAkun) {
            if (akun instanceof Penyewa && akun.getId().equalsIgnoreCase(idPenyewa)) {
                return (Penyewa) akun;
            }
        }
        return null;
    }

    private Barang cariBarangById(String idBarang)
    {
        for (Barang item : this.daftarBarang.getBarang()) {
            if (item.getId().equalsIgnoreCase(idBarang)) {
                return item;
            }
        }
        return null;
    }

    private static Pembayaran buatPembayaran(String namaKelas)
    {
        if ("TransferBank".equalsIgnoreCase(namaKelas)) {
            return new TransferBank();
        }
        if ("Tunai".equalsIgnoreCase(namaKelas)) {
            return new Tunai();
        }
        if ("Wallet".equalsIgnoreCase(namaKelas)) {
            return new Wallet();
        }
        return new QRIS();
    }

    private static List<String> bacaBaris(File file)
    {
        List<String> hasil = new ArrayList<>();
        try {
            for (String line : Files.readAllLines(file.toPath(), StandardCharsets.UTF_8)) {
                if (!line.trim().isEmpty() && !line.trim().startsWith("#")) {
                    hasil.add(line);
                }
            }
        } catch (IOException ignored) {
            // Kembalikan list kosong bila file gagal dibaca
        }
        return hasil;
    }

    private static void tulisBaris(String path, List<String> baris)
    {
        try {
            Files.write(new File(path).toPath(), baris, StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            // Abaikan bila direktori tidak dapat ditulis
        }
    }

    private static String bersihkan(String teks)
    {
        return (teks == null) ? "" : teks.replace("|", " ").trim();
    }
}
