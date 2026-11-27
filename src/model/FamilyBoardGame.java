package model;

/**
 * Representasi board game kategori Family Board Game pada sistem BoardGameRent.
 *
 * Kategori ini memiliki komponen pion/bidak keluarga sehingga dikenakan
 * biaya garansi pion tetap sekali bayar di luar tarif harian:
 * (tarifHarian * hari) + biayaGaransiPion.
 *
 * @author Muhammad Aqil Mubarak
 * @version 2026.11.27
 */
public class FamilyBoardGame extends Barang
{
    private double biayaGaransiPion;

    /**
     * Membuat objek FamilyBoardGame baru dengan status awal tersedia.
     *
     * @param id kode unik barang, tidak boleh kosong
     * @param nama nama family board game, tidak boleh kosong
     * @param tarifHarian tarif sewa dasar per hari dalam rupiah, tidak boleh negatif
     * @param dendaPerHari denda keterlambatan per hari dalam rupiah, tidak boleh negatif
     * @param biayaGaransiPion biaya tetap garansi pion sekali bayar dalam rupiah,
     *                         harus berhingga dan tidak negatif
     * @throws IllegalArgumentException jika salah satu parameter tidak valid
     */
    public FamilyBoardGame(String id, String nama, double tarifHarian,
                           double dendaPerHari, double biayaGaransiPion)
    {
        super(id, nama, tarifHarian, dendaPerHari);
        this.setBiayaGaransiPion(biayaGaransiPion);
    }

    /**
     * Mengembalikan biaya tetap garansi pion.
     *
     * @return biaya garansi pion dalam rupiah
     */
    public double getBiayaGaransiPion()
    {
        return this.biayaGaransiPion;
    }

    /**
     * Mengubah biaya tetap garansi pion.
     *
     * @param biayaGaransiPion biaya garansi pion baru dalam rupiah, harus berhingga dan tidak negatif
     * @throws IllegalArgumentException jika biayaGaransiPion tidak valid atau bernilai negatif
     */
    public void setBiayaGaransiPion(double biayaGaransiPion)
    {
        if (!Double.isFinite(biayaGaransiPion) || biayaGaransiPion < 0) {
            throw new IllegalArgumentException("Biaya garansi pion tidak valid atau negatif.");
        }
        this.biayaGaransiPion = biayaGaransiPion;
    }

    /**
     * Menghitung biaya sewa Family Board Game, yaitu tarif harian dikali lama sewa
     * ditambah biaya garansi pion sekali bayar.
     *
     * @param hari lama penyewaan dalam satuan hari, harus lebih dari 0
     * @return total biaya sewa sebesar (tarifHarian * hari) + biayaGaransiPion
     * @throws IllegalArgumentException jika hari kurang dari atau sama dengan 0
     */
    @Override
    public double hitungBiaya(int hari)
    {
        this.validasiLamaSewa(hari);
        return (this.getTarifHarian() * hari) + this.biayaGaransiPion;
    }
}
