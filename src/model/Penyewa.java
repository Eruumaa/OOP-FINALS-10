package model;

import java.util.ArrayList;
import payment.Pembayaran;
import payment.Wallet;

/**
 * Kelas yang merepresentasikan akun Penyewa pada sistem BoardGameRent.
 *
 * @author Irvan Almidar
 * @version 2026.11.27
 */
public class Penyewa extends Akun
{
    private static int nomorTransaksi = 1;

    private DaftarBarang daftarBarang;
    private KeranjangSewa keranjang;
    private ArrayList<Transaksi> riwayatTransaksi;
    private double saldoWallet;

    /**
     * Membuat akun penyewa baru beserta keranjang, riwayat kosong, dan saldo wallet awal.
     *
     * @param id kode unik akun penyewa
     * @param username username untuk login penyewa
     * @param password password untuk login penyewa
     * @param daftarBarang referensi katalog barang bersama, tidak boleh null
     * @throws IllegalArgumentException jika daftarBarang bernilai null
     *                                  atau data akun kosong
     */
    public Penyewa(String id, String username, String password, DaftarBarang daftarBarang)
    {
        this(id, username, password, daftarBarang, 250000.0);
    }

    /**
     * Membuat akun penyewa dengan saldo wallet awal tertentu (digunakan saat memuat data tersimpan).
     *
     * @param id kode unik akun penyewa
     * @param username username untuk login penyewa
     * @param password password untuk login penyewa
     * @param daftarBarang referensi katalog barang bersama, tidak boleh null
     * @param saldoWallet saldo awal wallet dalam rupiah, tidak boleh negatif
     * @throws IllegalArgumentException jika daftarBarang null atau saldoWallet negatif
     */
    public Penyewa(String id, String username, String password, DaftarBarang daftarBarang,
                   double saldoWallet)
    {
        super(id, username, password);

        if (daftarBarang == null) {
            throw new IllegalArgumentException("Daftar barang tidak boleh null.");
        }

        this.daftarBarang = daftarBarang;
        this.keranjang = new KeranjangSewa();
        this.riwayatTransaksi = new ArrayList<>();
        this.setSaldoWallet(saldoWallet);
    }

    /**
     * Menyelaraskan penomor otomatis transaksi berikutnya agar tidak bentrok dengan data tersimpan.
     *
     * @param nomorBerikutnya angka urut transaksi berikutnya yang valid
     */
    public static void sinkronNomorTransaksi(int nomorBerikutnya)
    {
        if (nomorBerikutnya > nomorTransaksi) {
            nomorTransaksi = nomorBerikutnya;
        }
    }

    /**
     * Mengembalikan saldo wallet penyewa saat ini.
     *
     * @return saldo wallet dalam rupiah
     */
    public double getSaldoWallet()
    {
        return this.saldoWallet;
    }

    /**
     * Mengatur saldo wallet penyewa.
     *
     * @param saldoWallet nominal saldo baru, harus berhingga dan tidak negatif
     * @throws IllegalArgumentException jika saldo tidak valid atau negatif
     */
    public void setSaldoWallet(double saldoWallet)
    {
        if (!Double.isFinite(saldoWallet) || saldoWallet < 0) {
            throw new IllegalArgumentException("Saldo wallet tidak valid atau negatif.");
        }
        this.saldoWallet = saldoWallet;
    }

    /**
     * Menambahkan saldo ke wallet penyewa.
     *
     * @param nominal jumlah top up dalam rupiah, harus lebih dari 0
     * @throws IllegalArgumentException jika nominal tidak valid atau kurang dari sama dengan 0
     */
    public void topUpWallet(double nominal)
    {
        if (!Double.isFinite(nominal) || nominal <= 0) {
            throw new IllegalArgumentException("Nominal top up harus lebih dari 0.");
        }
        this.saldoWallet += nominal;
    }

    /**
     * Memotong saldo wallet saat pembayaran sewa menggunakan metode Wallet.
     *
     * @param jumlah nominal yang dibayarkan dalam rupiah, harus lebih dari 0
     * @throws IllegalArgumentException jika jumlah tidak valid
     * @throws IllegalStateException jika saldo wallet tidak mencukupi
     */
    public void potongSaldoWallet(double jumlah)
    {
        if (!Double.isFinite(jumlah) || jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah pembayaran wallet harus lebih dari 0.");
        }
        if (this.saldoWallet < jumlah) {
            throw new IllegalStateException("Saldo wallet tidak mencukupi untuk transaksi ini.");
        }
        this.saldoWallet -= jumlah;
    }

    /**
     * Mengambil daftar seluruh board game yang saat ini berstatus tersedia untuk disewa.
     *
     * @return daftar barang yang tersedia
     */
    public ArrayList<Barang> lihatBarang()
    {
        ArrayList<Barang> barangTersedia = new ArrayList<>();

        for (Barang item : this.daftarBarang.getBarang()) {
            if (item.isTersedia()) {
                barangTersedia.add(item);
            }
        }

        return barangTersedia;
    }

    /**
     * Mengembalikan keranjang penyewaan milik penyewa ini.
     *
     * @return objek KeranjangSewa
     */
    public KeranjangSewa getKeranjang()
    {
        return this.keranjang;
    }

    /**
     * Membuat transaksi penyewaan baru dari seluruh isi keranjang saat ini,
     * mencatatnya ke riwayat penyewa, dan mengosongkan keranjang.
     *
     * @param lama lama penyewaan dalam satuan hari, harus lebih dari 0
     * @param pembayaran strategi pembayaran yang dipilih, tidak boleh null
     * @return objek Transaksi baru berstatus "Menunggu Konfirmasi"
     * @throws IllegalArgumentException jika keranjang kosong, lama kurang dari atau sama dengan 0,
     *                                  atau pembayaran bernilai null
     */
    public Transaksi buatTransaksi(int lama, Pembayaran pembayaran)
    {
        ArrayList<Barang> barangKeranjang = this.keranjang.getBarang();
        if (barangKeranjang.isEmpty()) {
            throw new IllegalArgumentException("Keranjang penyewaan masih kosong.");
        }
        if (lama <= 0) {
            throw new IllegalArgumentException("Lama sewa harus lebih dari 0 hari.");
        }
        if (pembayaran == null) {
            throw new IllegalArgumentException("Metode pembayaran harus dipilih.");
        }

        String idTransaksi = String.format("TRX-%03d", nomorTransaksi++);
        Transaksi transaksi = new Transaksi(
            idTransaksi,
            this,
            barangKeranjang,
            lama,
            pembayaran
        );

        this.tambahRiwayat(transaksi);
        this.keranjang.kosongkanKeranjang();

        return transaksi;
    }

    /**
     * Menambahkan transaksi ke riwayat penyewaan secara idempoten (mencegah duplikasi ID).
     *
     * @param transaksi transaksi yang akan dicatat ke riwayat, tidak boleh null
     * @throws IllegalArgumentException jika transaksi bernilai null
     */
    public void tambahRiwayat(Transaksi transaksi)
    {
        if (transaksi == null) {
            throw new IllegalArgumentException("Transaksi tidak boleh null.");
        }

        for (Transaksi item : this.riwayatTransaksi) {
            if (item.getId().equalsIgnoreCase(transaksi.getId())) {
                return;
            }
        }

        this.riwayatTransaksi.add(transaksi);
    }

    /**
     * Membatalkan transaksi yang masih berstatus "Menunggu Konfirmasi".
     * Jika pembayaran menggunakan Wallet, saldo sebesar total transaksi dikembalikan.
     *
     * @param transaksi transaksi milik penyewa yang akan dibatalkan
     * @throws IllegalArgumentException jika transaksi null atau bukan milik penyewa ini
     * @throws IllegalStateException jika status transaksi bukan "Menunggu Konfirmasi"
     */
    public void batalkanTransaksi(Transaksi transaksi)
    {
        if (transaksi == null) {
            throw new IllegalArgumentException("Transaksi tidak boleh null.");
        }
        if (transaksi.getPenyewa() != this) {
            throw new IllegalArgumentException("Transaksi bukan milik akun penyewa ini.");
        }
        if (!"Menunggu Konfirmasi".equalsIgnoreCase(transaksi.getStatus())) {
            throw new IllegalStateException(
                "Hanya transaksi berstatus Menunggu Konfirmasi yang dapat dibatalkan."
            );
        }

        transaksi.ubahStatus("Dibatalkan");
        if (transaksi.getPembayaran() instanceof Wallet) {
            this.topUpWallet(transaksi.hitungTotal());
        }
    }

    /**
     * Mengambil salinan daftar seluruh riwayat transaksi penyewa.
     *
     * @return salinan ArrayList riwayat transaksi
     */
    public ArrayList<Transaksi> lihatRiwayat()
    {
        return new ArrayList<>(this.riwayatTransaksi);
    }

    /**
     * Mengambil salinan daftar riwayat transaksi penyewa.
     *
     * @return salinan ArrayList riwayat transaksi
     */
    public ArrayList<Transaksi> getRiwayatTransaksi()
    {
        return new ArrayList<>(this.riwayatTransaksi);
    }
}
