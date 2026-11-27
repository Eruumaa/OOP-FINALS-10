package model;

import java.util.Locale;
import java.util.Objects;

/**
 * Kelas abstrak yang merepresentasikan satu unit board game yang disewakan
 * pada sistem BoardGameRent.
 *
 * Setiap kategori board game merupakan turunan dari kelas ini dan wajib
 * mengimplementasikan perhitungan biaya sewanya sendiri melalui method
 * hitungBiaya(int).
 *
 * @author Muhammad Aqil Mubarak
 * @version 2026.11.27
 */
public abstract class Barang
{
    private String id;
    private String nama;
    private double tarifHarian;
    private double dendaPerHari;
    private boolean tersedia;

    /**
     * Membuat objek barang sewa baru dengan status awal tersedia (true).
     *
     * @param id kode unik barang, tidak boleh kosong
     * @param nama nama board game, tidak boleh kosong
     * @param tarifHarian tarif sewa dasar per hari dalam rupiah, tidak boleh negatif
     * @param dendaPerHari denda keterlambatan per hari dalam rupiah, tidak boleh negatif
     * @throws IllegalArgumentException jika id atau nama kosong,
     *                                  atau jika tarif maupun denda tidak valid
     */
    public Barang(String id, String nama, double tarifHarian, double dendaPerHari)
    {
        this.setId(id);
        this.setNama(nama);
        this.setTarifHarian(tarifHarian);
        this.setDendaPerHari(dendaPerHari);
        this.tersedia = true;
    }

    /**
     * Menghitung total biaya sewa barang sesuai kategori dan lama penyewaan.
     *
     * @param hari lama penyewaan dalam satuan hari, harus lebih dari 0
     * @return total biaya sewa dalam rupiah
     * @throws IllegalArgumentException jika hari kurang dari atau sama dengan 0
     */
    public abstract double hitungBiaya(int hari);

    /**
     * Mengembalikan kode unik barang.
     *
     * @return ID barang
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * Mengubah kode unik barang.
     *
     * @param id ID barang baru, tidak boleh null atau kosong
     * @throws IllegalArgumentException jika id bernilai null atau kosong
     */
    public void setId(String id)
    {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID barang tidak boleh kosong.");
        }
        this.id = id.trim();
    }

    /**
     * Mengembalikan nama board game.
     *
     * @return nama barang
     */
    public String getNama()
    {
        return this.nama;
    }

    /**
     * Mengubah nama board game.
     *
     * @param nama nama barang baru, tidak boleh null atau kosong
     * @throws IllegalArgumentException jika nama bernilai null atau kosong
     */
    public void setNama(String nama)
    {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama barang tidak boleh kosong.");
        }
        this.nama = nama.trim();
    }

    /**
     * Mengembalikan tarif sewa dasar per hari.
     *
     * @return tarif sewa harian dalam rupiah
     */
    public double getTarifHarian()
    {
        return this.tarifHarian;
    }

    /**
     * Mengubah tarif sewa dasar per hari.
     *
     * @param tarifHarian tarif harian baru dalam rupiah, harus berhingga dan tidak negatif
     * @throws IllegalArgumentException jika tarifHarian tidak valid atau bernilai negatif
     */
    public void setTarifHarian(double tarifHarian)
    {
        if (!Double.isFinite(tarifHarian) || tarifHarian < 0) {
            throw new IllegalArgumentException("Tarif harian tidak valid atau negatif.");
        }
        this.tarifHarian = tarifHarian;
    }

    /**
     * Mengembalikan besaran denda keterlambatan per hari.
     *
     * @return denda keterlambatan per hari dalam rupiah
     */
    public double getDendaPerHari()
    {
        return this.dendaPerHari;
    }

    /**
     * Mengubah besaran denda keterlambatan per hari.
     *
     * @param dendaPerHari denda keterlambatan baru dalam rupiah, harus berhingga dan tidak negatif
     * @throws IllegalArgumentException jika dendaPerHari tidak valid atau bernilai negatif
     */
    public void setDendaPerHari(double dendaPerHari)
    {
        if (!Double.isFinite(dendaPerHari) || dendaPerHari < 0) {
            throw new IllegalArgumentException("Denda per hari tidak valid atau negatif.");
        }
        this.dendaPerHari = dendaPerHari;
    }

    /**
     * Memeriksa apakah barang sedang tersedia untuk disewa.
     *
     * @return true jika barang tersedia, false jika sedang disewa
     */
    public boolean isTersedia()
    {
        return this.tersedia;
    }

    /**
     * Mengubah status ketersediaan barang.
     *
     * @param tersedia true jika tersedia, false jika sedang disewa
     */
    public void setTersedia(boolean tersedia)
    {
        this.tersedia = tersedia;
    }

    /**
     * Memvalidasi bahwa lama sewa bernilai positif sebelum perhitungan biaya dilakukan.
     *
     * @param hari lama sewa dalam satuan hari
     * @throws IllegalArgumentException jika hari kurang dari atau sama dengan 0
     */
    protected void validasiLamaSewa(int hari)
    {
        if (hari <= 0) {
            throw new IllegalArgumentException("Lama sewa harus lebih dari 0 hari.");
        }
    }

    /**
     * Membandingkan kesamaan dua objek barang berdasarkan kode unik (id)
     * secara case-insensitive.
     *
     * @param object objek lain yang akan dibandingkan
     * @return true jika kedua objek merupakan Barang dengan ID yang sama
     */
    @Override
    public boolean equals(Object object)
    {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Barang)) {
            return false;
        }

        Barang other = (Barang) object;
        if (this.id == null || other.id == null) {
            return Objects.equals(this.id, other.id);
        }
        return this.id.equalsIgnoreCase(other.id);
    }

    /**
     * Menghasilkan nilai hash barang berdasarkan kode unik (id) huruf kecil.
     *
     * @return nilai hash objek
     */
    @Override
    public int hashCode()
    {
        String kunciId = (this.id == null) ? null : this.id.toLowerCase(Locale.ROOT);
        return Objects.hash(kunciId);
    }

    /**
     * Mengembalikan representasi teks singkat dari barang sewa.
     *
     * @return informasi ringkas barang meliputi ID, nama, kategori, tarif, dan status
     */
    @Override
    public String toString()
    {
        String status;
        if (this.tersedia) {
            status = "Tersedia";
        } else {
            status = "Sedang Disewa";
        }

        return String.format("[%s] %s (%s) - Rp%.0f/hari [%s]",
            this.id, this.nama, this.getClass().getSimpleName(), this.tarifHarian, status);
    }
}
