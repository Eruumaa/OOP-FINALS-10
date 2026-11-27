package model;

import java.util.ArrayList;
import payment.Pembayaran;

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

    /**
     * Membuat akun penyewa baru beserta keranjang dan daftar riwayat kosong.
     *
     * @param id kode unik akun penyewa
     * @param username username untuk login penyewa
     * @param password password untuk login penyewa
     * @param daftarBarang referensi katalog barang bersama, tidak boleh {@code null}
     * @throws IllegalArgumentException jika {@code daftarBarang} bernilai {@code null}
     *                                  atau data akun kosong
     */
    public Penyewa(String id, String username, String password, DaftarBarang daftarBarang)
    {
        super(id, username, password);

        if (daftarBarang == null) {
            throw new IllegalArgumentException("Daftar barang tidak boleh null.");
        }

        this.daftarBarang = daftarBarang;
        this.keranjang = new KeranjangSewa();
        this.riwayatTransaksi = new ArrayList<>();
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
     * @return objek {@link KeranjangSewa}
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
     * @param pembayaran strategi pembayaran yang dipilih, tidak boleh {@code null}
     * @return objek {@link Transaksi} baru berstatus {@code "Menunggu Konfirmasi"}
     * @throws IllegalArgumentException jika keranjang kosong, {@code lama <= 0},
     *                                  atau {@code pembayaran} bernilai {@code null}
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
     * @param transaksi transaksi yang akan dicatat ke riwayat, tidak boleh {@code null}
     * @throws IllegalArgumentException jika {@code transaksi} bernilai {@code null}
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
     * Mengambil salinan daftar seluruh riwayat transaksi penyewa.
     *
     * @return salinan {@link ArrayList} riwayat transaksi
     */
    public ArrayList<Transaksi> lihatRiwayat()
    {
        return new ArrayList<>(this.riwayatTransaksi);
    }

    /**
     * Mengambil salinan daftar riwayat transaksi penyewa.
     *
     * @return salinan {@link ArrayList} riwayat transaksi
     */
    public ArrayList<Transaksi> getRiwayatTransaksi()
    {
        return new ArrayList<>(this.riwayatTransaksi);
    }
}
