package model;

/**
 * Representasi board game kategori Strategy Game pada sistem BoardGameRent.
 *
 * <p>Kategori ini memiliki ekspansi atau modul permainan tambahan yang dihitung
 * per hari sewa: {@code (tarifHarian + biayaModulHarian) * hari}.</p>
 *
 * @author Muhammad Aqil Mubarak
 * @version 2026.11.27
 */
public class StrategyGame extends Barang
{
    private double biayaModulHarian;

    /**
     * Membuat objek {@code StrategyGame} baru dengan status awal tersedia.
     *
     * @param id kode unik barang, tidak boleh kosong
     * @param nama nama strategy game, tidak boleh kosong
     * @param tarifHarian tarif sewa dasar per hari dalam rupiah, tidak boleh negatif
     * @param dendaPerHari denda keterlambatan per hari dalam rupiah, tidak boleh negatif
     * @param biayaModulHarian biaya tambahan modul permainan per hari dalam rupiah,
     *                         harus berhingga dan tidak negatif
     * @throws IllegalArgumentException jika salah satu parameter tidak valid
     */
    public StrategyGame(String id, String nama, double tarifHarian,
                        double dendaPerHari, double biayaModulHarian)
    {
        super(id, nama, tarifHarian, dendaPerHari);
        this.setBiayaModulHarian(biayaModulHarian);
    }

    /**
     * Mengembalikan biaya tambahan modul permainan per hari.
     *
     * @return biaya modul harian dalam rupiah
     */
    public double getBiayaModulHarian()
    {
        return this.biayaModulHarian;
    }

    /**
     * Mengubah biaya tambahan modul permainan per hari.
     *
     * @param biayaModulHarian biaya modul harian baru dalam rupiah, harus berhingga dan tidak negatif
     * @throws IllegalArgumentException jika {@code biayaModulHarian} tidak valid atau bernilai negatif
     */
    public void setBiayaModulHarian(double biayaModulHarian)
    {
        if (!Double.isFinite(biayaModulHarian) || biayaModulHarian < 0) {
            throw new IllegalArgumentException("Biaya modul harian tidak valid atau negatif.");
        }
        this.biayaModulHarian = biayaModulHarian;
    }

    /**
     * Menghitung biaya sewa Strategy Game, yaitu jumlah tarif harian dan biaya modul
     * harian dikalikan dengan lama penyewaan.
     *
     * @param hari lama penyewaan dalam satuan hari, harus lebih dari 0
     * @return total biaya sewa sebesar {@code (tarifHarian + biayaModulHarian) * hari}
     * @throws IllegalArgumentException jika {@code hari} kurang dari atau sama dengan 0
     */
    @Override
    public double hitungBiaya(int hari)
    {
        this.validasiLamaSewa(hari);
        return (this.getTarifHarian() + this.biayaModulHarian) * hari;
    }
}
