package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import payment.Pembayaran;

/**
 * Kelas untuk mengelola data transaksi penyewaan board game beserta perhitungan
 * total biaya dan denda keterlambatan berbasis {@link LocalDate}.
 *
 * @author Irvan Almidar
 * @version 2026.11.27
 */
public class Transaksi
{
    private String id;
    private Penyewa penyewa;
    private ArrayList<Barang> barang;
    private int lamaSewa;
    private LocalDate tanggalSewa;
    private LocalDate tanggalJatuhTempo;
    private LocalDate tanggalKembali;
    private String status;
    private Pembayaran pembayaran;

    /**
     * Membuat transaksi penyewaan baru berstatus awal {@code "Menunggu Konfirmasi"}.
     *
     * @param id kode unik transaksi, tidak boleh kosong
     * @param penyewa akun penyewa yang melakukan transaksi, tidak boleh {@code null}
     * @param barang daftar barang yang disewa, tidak boleh {@code null} atau kosong
     * @param lamaSewa lama penyewaan dalam satuan hari, harus lebih dari 0
     * @param pembayaran strategi pembayaran yang dipilih, tidak boleh {@code null}
     * @throws IllegalArgumentException jika salah satu data transaksi tidak valid
     */
    public Transaksi(String id, Penyewa penyewa, ArrayList<Barang> barang,
                     int lamaSewa, Pembayaran pembayaran)
    {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID transaksi tidak boleh kosong.");
        }
        if (penyewa == null || barang == null || barang.isEmpty()) {
            throw new IllegalArgumentException("Data penyewa dan barang harus tersedia.");
        }
        if (lamaSewa <= 0) {
            throw new IllegalArgumentException("Lama sewa harus lebih dari 0 hari.");
        }
        if (pembayaran == null) {
            throw new IllegalArgumentException("Metode pembayaran harus dipilih.");
        }

        for (Barang item : barang) {
            if (item == null) {
                throw new IllegalArgumentException("Daftar barang tidak boleh berisi elemen null.");
            }
        }

        this.id = id.trim();
        this.penyewa = penyewa;
        this.barang = new ArrayList<>(barang);
        this.lamaSewa = lamaSewa;
        this.tanggalSewa = LocalDate.now();
        this.tanggalJatuhTempo = this.tanggalSewa.plusDays(lamaSewa);
        this.tanggalKembali = null;
        this.status = "Menunggu Konfirmasi";
        this.pembayaran = pembayaran;
    }

    /**
     * Menghitung total biaya sewa seluruh barang sesuai kategori masing-masing.
     *
     * @return total biaya sewa dalam rupiah
     */
    public double hitungTotal()
    {
        double total = 0;

        for (Barang item : this.barang) {
            total += item.hitungBiaya(this.lamaSewa);
        }

        return total;
    }

    /**
     * Menghitung total denda keterlambatan pengembalian board game.
     *
     * <p>Jika tanggal kembali belum diisi atau tidak melewati tanggal jatuh tempo,
     * denda bernilai 0.</p>
     *
     * @return total denda keterlambatan dalam rupiah
     */
    public double hitungDenda()
    {
        if (this.tanggalKembali == null
                || !this.tanggalKembali.isAfter(this.tanggalJatuhTempo)) {
            return 0;
        }

        long hariTerlambat = ChronoUnit.DAYS.between(
            this.tanggalJatuhTempo, this.tanggalKembali
        );

        double dendaPerHari = 0;
        for (Barang item : this.barang) {
            dendaPerHari += item.getDendaPerHari();
        }

        return hariTerlambat * dendaPerHari;
    }

    /**
     * Mengubah status transaksi saat ini.
     *
     * @param status status baru transaksi, tidak boleh {@code null} atau kosong
     * @throws IllegalArgumentException jika {@code status} bernilai {@code null} atau kosong
     */
    public void ubahStatus(String status)
    {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status tidak boleh kosong.");
        }

        this.status = status.trim();
    }

    /**
     * Mengembalikan kode unik transaksi.
     *
     * @return ID transaksi
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * Mengembalikan objek penyewa yang melakukan transaksi ini.
     *
     * @return akun penyewa
     */
    public Penyewa getPenyewa()
    {
        return this.penyewa;
    }

    /**
     * Mengembalikan salinan daftar barang yang disewa dalam transaksi ini.
     *
     * @return salinan {@link ArrayList} berisi barang sewaan
     */
    public ArrayList<Barang> getBarang()
    {
        return new ArrayList<>(this.barang);
    }

    /**
     * Mengembalikan lama penyewaan dalam satuan hari.
     *
     * @return lama sewa dalam hari
     */
    public int getLamaSewa()
    {
        return this.lamaSewa;
    }

    /**
     * Mengembalikan tanggal saat transaksi dibuat.
     *
     * @return tanggal sewa
     */
    public LocalDate getTanggalSewa()
    {
        return this.tanggalSewa;
    }

    /**
     * Mengembalikan tanggal batas jatuh tempo pengembalian.
     *
     * @return tanggal jatuh tempo
     */
    public LocalDate getTanggalJatuhTempo()
    {
        return this.tanggalJatuhTempo;
    }

    /**
     * Mengembalikan tanggal pengembalian aktual barang.
     *
     * @return tanggal pengembalian aktual, atau {@code null} jika belum dikembalikan
     */
    public LocalDate getTanggalKembali()
    {
        return this.tanggalKembali;
    }

    /**
     * Mengembalikan status transaksi saat ini.
     *
     * @return status transaksi
     */
    public String getStatus()
    {
        return this.status;
    }

    /**
     * Mengembalikan metode pembayaran yang dipilih pada transaksi ini.
     *
     * @return objek implementasi {@link Pembayaran}
     */
    public Pembayaran getPembayaran()
    {
        return this.pembayaran;
    }

    /**
     * Mencatat tanggal pengembalian aktual transaksi.
     *
     * @param tanggalKembali tanggal barang dikembalikan, tidak boleh {@code null}
     *                       atau lebih awal dari tanggal sewa
     * @throws IllegalArgumentException jika tanggal kembali tidak valid
     */
    public void setTanggalKembali(LocalDate tanggalKembali)
    {
        if (tanggalKembali == null || tanggalKembali.isBefore(this.tanggalSewa)) {
            throw new IllegalArgumentException(
                "Tanggal kembali tidak boleh sebelum tanggal sewa (" + this.tanggalSewa + ")."
            );
        }

        this.tanggalKembali = tanggalKembali;
    }

    /**
     * Mengubah status transaksi melalui method standar {@link #ubahStatus(String)}.
     *
     * @param status status baru transaksi
     */
    public void setStatus(String status)
    {
        this.ubahStatus(status);
    }
}
