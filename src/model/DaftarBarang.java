package model;

import exception.DataTidakDitemukanException;
import java.util.ArrayList;

/**
 * Kelas pengelola katalog dan inventaris board game pada sistem BoardGameRent.
 *
 * Kelas ini menyimpan kumpulan objek Barang serta menyediakan operasi
 * penambahan, pencarian, pengubahan, dan penghapusan barang tanpa ketergantungan
 * terhadap komponen antarmuka pengguna.
 *
 * @author Muhammad Aqil Mubarak
 * @version 2026.11.27
 */
public class DaftarBarang
{
    private ArrayList<Barang> barang;

    /**
     * Membuat objek DaftarBarang baru dengan daftar inventaris kosong.
     */
    public DaftarBarang()
    {
        this.barang = new ArrayList<>();
    }

    /**
     * Menambahkan satu barang baru ke dalam katalog.
     *
     * @param barang objek barang yang akan ditambahkan, tidak boleh null
     *               dan ID-nya belum terdaftar di katalog
     * @throws IllegalArgumentException jika barang bernilai null
     *                                  atau ID barang sudah terdaftar
     */
    public void tambahBarang(Barang barang)
    {
        if (barang == null) {
            throw new IllegalArgumentException("Barang yang ditambahkan tidak boleh null.");
        }
        if (this.adaIdDiDaftar(this.barang, barang.getId())) {
            throw new IllegalArgumentException("Barang dengan ID \"" + barang.getId() + "\" sudah ada.");
        }

        this.barang.add(barang);
    }

    /**
     * Memperbarui nama dan tarif harian barang berdasarkan kode uniknya.
     *
     * @param id kode unik barang yang akan diubah
     * @param nama nama baru barang, tidak boleh null atau kosong
     * @param tarif tarif harian baru dalam rupiah, harus berhingga dan tidak negatif
     * @throws DataTidakDitemukanException jika barang dengan id tersebut tidak ditemukan
     * @throws IllegalArgumentException jika nama kosong atau tarif tidak valid
     */
    public void editBarang(String id, String nama, double tarif) throws DataTidakDitemukanException
    {
        Barang ditemukan = this.cariBarang(id);

        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama barang tidak boleh kosong.");
        }
        if (!Double.isFinite(tarif) || tarif < 0) {
            throw new IllegalArgumentException("Tarif harian tidak valid atau negatif.");
        }

        ditemukan.setNama(nama);
        ditemukan.setTarifHarian(tarif);
    }

    /**
     * Menghapus barang dari katalog berdasarkan kode uniknya.
     *
     * @param id kode unik barang yang akan dihapus
     * @throws DataTidakDitemukanException jika barang dengan id tersebut tidak ditemukan
     * @throws IllegalStateException jika barang sedang disewa (isTersedia() == false)
     */
    public void hapusBarang(String id) throws DataTidakDitemukanException
    {
        Barang ditemukan = this.cariBarang(id);

        if (!ditemukan.isTersedia()) {
            throw new IllegalStateException("Barang sedang disewa dan tidak dapat dihapus.");
        }

        this.barang.remove(ditemukan);
    }

    /**
     * Mencari barang di dalam katalog berdasarkan kode unik secara case-insensitive.
     *
     * @param id kode unik barang yang dicari
     * @return objek Barang yang sesuai dengan id
     * @throws DataTidakDitemukanException jika id kosong atau barang tidak ditemukan
     */
    public Barang cariBarang(String id) throws DataTidakDitemukanException
    {
        if (id == null || id.trim().isEmpty()) {
            throw new DataTidakDitemukanException("ID barang tidak boleh kosong.");
        }

        String targetId = id.trim();
        for (Barang item : this.barang) {
            if (item.getId().equalsIgnoreCase(targetId)) {
                return item;
            }
        }

        throw new DataTidakDitemukanException("Barang dengan ID \"" + targetId + "\" tidak ditemukan.");
    }

    /**
     * Mengembalikan salinan daftar seluruh barang yang tersimpan di katalog.
     *
     * @return salinan ArrayList berisi objek Barang
     */
    public ArrayList<Barang> getBarang()
    {
        return new ArrayList<>(this.barang);
    }

    /**
     * Mengganti seluruh isi daftar barang dengan daftar baru secara atomik.
     *
     * @param barang daftar barang baru, tidak boleh null, tidak boleh memuat
     *               elemen null, dan tidak boleh memiliki ID duplikat
     * @throws IllegalArgumentException jika daftar bernilai null, memuat elemen
     *                                  null, atau terdapat ID barang duplikat
     */
    public void setBarang(ArrayList<Barang> barang)
    {
        if (barang == null) {
            throw new IllegalArgumentException("Daftar barang tidak boleh null.");
        }

        ArrayList<Barang> salinanBaru = new ArrayList<>();
        for (Barang item : barang) {
            if (item == null) {
                throw new IllegalArgumentException("Elemen barang di dalam daftar tidak boleh null.");
            }
            if (this.adaIdDiDaftar(salinanBaru, item.getId())) {
                throw new IllegalArgumentException("ID barang duplikat dalam daftar: " + item.getId());
            }
            salinanBaru.add(item);
        }

        this.barang = salinanBaru;
    }

    /**
     * Memeriksa apakah suatu ID barang sudah ada di dalam daftar yang diberikan.
     *
     * @param daftar daftar barang yang diperiksa
     * @param id kode unik barang yang dicari
     * @return true jika ID sudah ada secara case-insensitive, false jika belum
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
