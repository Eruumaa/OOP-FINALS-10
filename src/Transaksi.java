
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 * Kelas untuk mengelola transaksi penyewaan board game.
 *
 * @author Irvan Almidar
 * @version 1.0
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
     * Membuat transaksi penyewaan baru.
     *
     * @param id ID transaksi
     * @param penyewa penyewa yang melakukan transaksi
     * @param barang daftar barang yang disewa
     * @param lamaSewa lama penyewaan dalam hari
     * @param pembayaran metode pembayaran
     * @throws IllegalArgumentException jika data transaksi tidak valid
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
                throw new IllegalArgumentException("Daftar barang tidak boleh berisi null.");
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
     * Menghitung total biaya seluruh barang yang disewa.
     *
     * @return total biaya sewa
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
     * Menghitung denda berdasarkan keterlambatan pengembalian.
     *
     * @return total denda keterlambatan
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
     * Mengubah status transaksi.
     *
     * @param status status transaksi yang baru
     * @throws IllegalArgumentException jika status kosong
     */
    public void ubahStatus(String status)
    {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status tidak boleh kosong.");
        }

        this.status = status.trim();
    }

    /** @return ID transaksi */
    public String getId()
    {
        return this.id;
    }

    /** @return penyewa transaksi */
    public Penyewa getPenyewa()
    {
        return this.penyewa;
    }

    /** @return salinan daftar barang */
    public ArrayList<Barang> getBarang()
    {
        return new ArrayList<>(this.barang);
    }

    /** @return lama penyewaan dalam hari */
    public int getLamaSewa()
    {
        return this.lamaSewa;
    }

    /** @return tanggal transaksi dibuat */
    public LocalDate getTanggalSewa()
    {
        return this.tanggalSewa;
    }

    /** @return tanggal jatuh tempo pengembalian */
    public LocalDate getTanggalJatuhTempo()
    {
        return this.tanggalJatuhTempo;
    }

    /** @return tanggal pengembalian aktual */
    public LocalDate getTanggalKembali()
    {
        return this.tanggalKembali;
    }

    /** @return status transaksi */
    public String getStatus()
    {
        return this.status;
    }

    /** @return metode pembayaran */
    public Pembayaran getPembayaran()
    {
        return this.pembayaran;
    }

    /**
     * Mengatur tanggal pengembalian aktual.
     *
     * @param tanggalKembali tanggal barang dikembalikan
     * @throws IllegalArgumentException jika tanggal null atau sebelum tanggal sewa
     */
    public void setTanggalKembali(LocalDate tanggalKembali)
    {
        if (tanggalKembali == null || tanggalKembali.isBefore(this.tanggalSewa)) {
            throw new IllegalArgumentException("Tanggal kembali tidak valid.");
        }

        this.tanggalKembali = tanggalKembali;
    }

    /** @param status status transaksi baru */
    public void setStatus(String status)
    {
        this.ubahStatus(status);
    }
}
