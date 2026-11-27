package model;

/**
 * Representasi board game kategori Card Game pada sistem BoardGameRent.
 *
 * <p>Perhitungan biaya sewa untuk kategori ini menggunakan tarif harian standar
 * tanpa biaya tambahan: {@code tarifHarian * hari}.</p>
 *
 * @author Muhammad Aqil Mubarak
 * @version 2026.11.27
 */
public class CardGame extends Barang
{
    /**
     * Membuat objek {@code CardGame} baru dengan status awal tersedia.
     *
     * @param id kode unik barang, tidak boleh kosong
     * @param nama nama permainan kartu, tidak boleh kosong
     * @param tarifHarian tarif sewa dasar per hari dalam rupiah, tidak boleh negatif
     * @param dendaPerHari denda keterlambatan per hari dalam rupiah, tidak boleh negatif
     * @throws IllegalArgumentException jika parameter tidak valid
     */
    public CardGame(String id, String nama, double tarifHarian, double dendaPerHari)
    {
        super(id, nama, tarifHarian, dendaPerHari);
    }

    /**
     * Menghitung biaya sewa Card Game berdasarkan tarif harian standar.
     *
     * @param hari lama penyewaan dalam satuan hari, harus lebih dari 0
     * @return total biaya sewa sebesar {@code tarifHarian * hari}
     * @throws IllegalArgumentException jika {@code hari} kurang dari atau sama dengan 0
     */
    @Override
    public double hitungBiaya(int hari)
    {
        this.validasiLamaSewa(hari);
        return this.getTarifHarian() * hari;
    }
}
