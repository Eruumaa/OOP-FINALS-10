
import java.util.ArrayList;

/**
 * Kelas yang merepresentasikan akun penyewa pada sistem BoardGameRent.
 *
 * @author Irvan Almidar
 * @version 1.1
 */
public class Penyewa extends Akun
{
    private DaftarBarang daftarBarang;
    private KeranjangSewa keranjang;
    private ArrayList<Transaksi> riwayatTransaksi;
    private static int nomorTransaksi = 1;

    /**
     * Membuat akun penyewa baru.
     *
     * @param id ID akun penyewa
     * @param username username penyewa
     * @param password password penyewa
     * @param daftarBarang katalog barang
     * @throws IllegalArgumentException jika katalog barang null
     */
    public Penyewa(String id, String username, String password,
                   DaftarBarang daftarBarang)
    {
        super(id, username, password);

        if (daftarBarang == null) {
            throw new IllegalArgumentException(
                "Daftar barang tidak boleh null."
            );
        }

        this.daftarBarang = daftarBarang;
        this.keranjang = new KeranjangSewa();
        this.riwayatTransaksi = new ArrayList<>();
    }

    /**
     * Mengambil seluruh barang yang tersedia untuk disewa.
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
     * Mengembalikan keranjang penyewaan.
     *
     * @return objek keranjang penyewaan
     */
    public KeranjangSewa getKeranjang()
    {
        return this.keranjang;
    }

    /**
     * Membuat transaksi penyewaan berdasarkan isi keranjang.
     *
     * @param lama lama penyewaan dalam hari
     * @param pembayaran metode pembayaran yang dipilih
     * @return transaksi penyewaan yang berhasil dibuat
     * @throws IllegalArgumentException jika keranjang kosong,
     *                                   lama sewa tidak valid,
     *                                   atau pembayaran null
     */


public Transaksi buatTransaksi(int lama, Pembayaran pembayaran)
{
    if (this.keranjang.getBarang().isEmpty()) {
        throw new IllegalArgumentException(
            "Keranjang penyewaan masih kosong."
        );
    }

    if (lama <= 0) {
        throw new IllegalArgumentException(
            "Lama sewa harus lebih dari 0 hari."
        );
    }

    if (pembayaran == null) {
        throw new IllegalArgumentException(
            "Metode pembayaran harus dipilih."
        );
    }

    String idTransaksi = String.format(
        "TRX-%03d", nomorTransaksi++
    );

    Transaksi transaksi = new Transaksi(
        idTransaksi,
        this,
        this.keranjang.getBarang(),
        lama,
        pembayaran
    );

    // Simpan transaksi ke riwayat penyewa
    this.tambahRiwayat(transaksi);

    // Kosongkan keranjang setelah transaksi disimpan
    this.keranjang.kosongkanKeranjang();

    return transaksi;
}

    /**
     * Menambahkan transaksi ke riwayat penyewaan.
     *
     * @param transaksi transaksi yang akan ditambahkan
     * @throws IllegalArgumentException jika transaksi null
     */
    public void tambahRiwayat(Transaksi transaksi)
    {
        if (transaksi == null) {
            throw new IllegalArgumentException(
                "Transaksi tidak boleh null."
            );
        }

        this.riwayatTransaksi.add(transaksi);
    }

    /**
     * Mengambil seluruh riwayat transaksi penyewaan.
     *
     * @return salinan daftar riwayat transaksi
     */
    public ArrayList<Transaksi> lihatRiwayat()
    {
        return new ArrayList<>(this.riwayatTransaksi);
    }

    /**
     * Mengambil salinan riwayat transaksi.
     *
     * @return salinan daftar transaksi penyewa
     */
    public ArrayList<Transaksi> getRiwayatTransaksi()
    {
        return new ArrayList<>(this.riwayatTransaksi);
    }
}
