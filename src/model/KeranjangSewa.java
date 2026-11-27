package model;

import exception.BarangTidakTersediaException;
import java.util.ArrayList;

/**
 * Kelas untuk mengelola keranjang penyewaan board game milik penyewa.
 *
 * @author M. Yogi Kafrawi
 * @version 2026.11.27
 */
public class KeranjangSewa
{
    private ArrayList<Barang> barang;

    /**
     * Membuat keranjang penyewaan baru dalam keadaan kosong.
     */
    public KeranjangSewa()
    {
        this.barang = new ArrayList<>();
    }

    /**
     * Menambahkan barang ke dalam keranjang penyewaan.
     *
     * @param barang barang yang akan ditambahkan
     * @throws BarangTidakTersediaException jika barang {@code null}, sedang tidak tersedia,
     *                                      atau sudah berada di dalam keranjang
     */
    public void tambahBarang(Barang barang) throws BarangTidakTersediaException
    {
        if (barang == null) {
            throw new BarangTidakTersediaException("Barang tidak ditemukan.");
        }
        if (!barang.isTersedia()) {
            throw new BarangTidakTersediaException("Barang sedang tidak tersedia.");
        }
        if (this.adaIdDiDaftar(this.barang, barang.getId())) {
            throw new BarangTidakTersediaException("Barang sudah ada di keranjang.");
        }

        this.barang.add(barang);
    }

    /**
     * Menghapus barang dari keranjang berdasarkan ID secara case-insensitive.
     *
     * @param id ID barang yang akan dihapus
     */
    public void hapusBarang(String id)
    {
        if (id == null || id.trim().isEmpty()) {
            return;
        }

        String targetId = id.trim();
        this.barang.removeIf(item -> item.getId().equalsIgnoreCase(targetId));
    }

    /**
     * Mengambil salinan daftar barang yang sedang berada di dalam keranjang.
     *
     * @return salinan daftar barang di keranjang
     */
    public ArrayList<Barang> getBarang()
    {
        return new ArrayList<>(this.barang);
    }

    /**
     * Mengganti seluruh isi keranjang dengan daftar barang baru secara atomik.
     *
     * @param barang daftar barang baru, tidak boleh {@code null}, tidak boleh memuat
     *               elemen {@code null}, dan tidak boleh memiliki ID barang duplikat
     * @throws IllegalArgumentException jika daftar bernilai {@code null}, memuat elemen
     *                                  {@code null}, atau terdapat ID barang duplikat
     */
    public void setBarang(ArrayList<Barang> barang)
    {
        if (barang == null) {
            throw new IllegalArgumentException("Daftar barang keranjang tidak boleh null.");
        }

        ArrayList<Barang> salinanBaru = new ArrayList<>();
        for (Barang item : barang) {
            if (item == null) {
                throw new IllegalArgumentException("Elemen barang di dalam keranjang tidak boleh null.");
            }
            if (this.adaIdDiDaftar(salinanBaru, item.getId())) {
                throw new IllegalArgumentException("ID barang duplikat di dalam keranjang: " + item.getId());
            }
            salinanBaru.add(item);
        }

        this.barang = salinanBaru;
    }

    /**
     * Mengosongkan seluruh isi keranjang penyewaan.
     */
    public void kosongkanKeranjang()
    {
        this.barang.clear();
    }

    /**
     * Memeriksa apakah suatu ID barang sudah ada di dalam daftar yang diberikan.
     *
     * @param daftar daftar barang yang diperiksa
     * @param id kode unik barang yang dicari
     * @return {@code true} jika ID sudah ada secara case-insensitive, {@code false} jika belum
     */
    private boolean adaIdDiDaftar(ArrayList<Barang> daftar, String id)
    {
        for (Barang item : daftar) {
            if (item.getId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }
}
