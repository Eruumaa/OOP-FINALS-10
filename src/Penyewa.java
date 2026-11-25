
import java.util.ArrayList;

/**
 * Kelas yang merepresentasikan akun penyewa pada sistem BoardGameRent.
 *
 * @author Irvan Almidar
 * @version 1.0
 */
public class Penyewa extends Akun
{
    private DaftarBarang daftarBarang;
    private KeranjangSewa keranjang;
    private ArrayList<Transaksi> riwayatTransaksi;

    /**
     * Membuat akun penyewa baru.
     *
     * @param id ID akun penyewa
     * @param username username penyewa
     * @param password password penyewa
     * @param daftarBarang katalog barang
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
     * Menampilkan seluruh barang dalam katalog.
     */
    public void lihatBarang()
    {
        System.out.println("\n===== DAFTAR BOARD GAME =====");

        for (Barang item : this.daftarBarang.getBarang()) {
            System.out.println(item);
        }
    }

    /**
     * Mengembalikan keranjang penyewaan.
     *
     * @return objek keranjang
     */
    public KeranjangSewa getKeranjang()
    {
        return this.keranjang;
    }

    /**
     * Menambahkan transaksi ke riwayat penyewaan.
     *
     * @param transaksi transaksi yang ditambahkan
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
     * Menampilkan riwayat transaksi penyewaan.
     */
    public void lihatRiwayat()
    {
        System.out.println("\n===== RIWAYAT PENYEWAAN =====");

        if (this.riwayatTransaksi.isEmpty()) {
            System.out.println("Belum ada riwayat transaksi.");
            return;
        }

        for (Transaksi transaksi : this.riwayatTransaksi) {
            System.out.println("ID Transaksi : " + transaksi.getId());
            System.out.println("Tanggal Sewa : " + transaksi.getTanggalSewa());
            System.out.println("Jatuh Tempo  : " + transaksi.getTanggalJatuhTempo());
            System.out.println("Total Biaya  : Rp" + transaksi.hitungTotal());
            System.out.println("Status       : " + transaksi.getStatus());
            System.out.println("----------------------------");
        }
    }

    /**
     * Mengambil salinan riwayat transaksi.
     *
     * @return daftar riwayat transaksi
     */
    public ArrayList<Transaksi> getRiwayatTransaksi()
    {
        return new ArrayList<>(this.riwayatTransaksi);
    }
}
