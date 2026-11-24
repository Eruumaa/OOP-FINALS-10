import java.util.ArrayList;

/**
 * Kelas yang merepresentasikan akun Administrator pada sistem BoardGameRent.
 *
 * <p>Admin bertugas mengelola katalog barang melalui {@link DaftarBarang},
 * menampung transaksi penyewaan yang masuk, menerima transaksi, serta
 * mengonfirmasi pengembalian board game.</p>
 *
 * @author Aqil Mubarak
 * @version 2026.11.24
 */
public class Admin extends Akun
{
    private DaftarBarang daftarBarang;
    private ArrayList<Transaksi> daftarTransaksi;

    /**
     * Membuat objek {@code Admin} baru dengan referensi katalog barang bersama.
     *
     * @param id kode unik akun admin
     * @param username username untuk login admin
     * @param password password untuk login admin
     * @param daftarBarang objek {@link DaftarBarang} bersama, tidak boleh {@code null}
     * @throws IllegalArgumentException jika {@code daftarBarang} bernilai {@code null}
     */
    public Admin(String id, String username, String password, DaftarBarang daftarBarang)
    {
        super(id, username, password);

        if (daftarBarang == null) {
            throw new IllegalArgumentException("Daftar barang tidak boleh null.");
        }

        this.daftarBarang = daftarBarang;
        this.daftarTransaksi = new ArrayList<>();
    }

    /**
     * Mengembalikan objek pengelola katalog barang untuk digunakan pada tampilan Admin.
     *
     * @return objek {@link DaftarBarang} yang dikelola admin
     */
    public DaftarBarang kelolaBarang()
    {
        return this.daftarBarang;
    }

    /**
     * Mengembalikan objek katalog barang yang terhubung dengan admin.
     *
     * @return objek {@link DaftarBarang}
     */
    public DaftarBarang getDaftarBarang()
    {
        return this.daftarBarang;
    }

    /**
     * Mengubah referensi katalog barang yang dikelola admin.
     *
     * @param daftarBarang objek {@link DaftarBarang} baru, tidak boleh {@code null}
     * @throws IllegalArgumentException jika {@code daftarBarang} bernilai {@code null}
     */
    public void setDaftarBarang(DaftarBarang daftarBarang)
    {
        if (daftarBarang == null) {
            throw new IllegalArgumentException("Daftar barang tidak boleh null.");
        }

        this.daftarBarang = daftarBarang;
    }

    /**
     * Menambahkan transaksi penyewaan baru ke daftar transaksi yang dikelola admin.
     *
     * @param transaksi objek {@link Transaksi} yang akan ditambahkan, tidak boleh {@code null}
     *                  dan ID-nya belum terdaftar
     * @throws IllegalArgumentException jika {@code transaksi} bernilai {@code null}
     *                                  atau ID transaksi sudah ada
     */
    public void tambahTransaksi(Transaksi transaksi)
    {
        if (transaksi == null) {
            throw new IllegalArgumentException("Transaksi tidak boleh null.");
        }
        if (this.adaIdTransaksi(this.daftarTransaksi, transaksi.getId())) {
            throw new IllegalArgumentException(
                "Transaksi dengan ID \"" + transaksi.getId() + "\" sudah ada."
            );
        }

        this.daftarTransaksi.add(transaksi);
    }

    /**
     * Mengembalikan salinan daftar seluruh transaksi yang tercatat di admin.
     *
     * @return salinan {@link ArrayList} berisi objek {@link Transaksi}
     */
    public ArrayList<Transaksi> getDaftarTransaksi()
    {
        return new ArrayList<>(this.daftarTransaksi);
    }

    /**
     * Mengganti seluruh daftar transaksi admin dengan daftar baru secara atomik.
     *
     * @param daftarTransaksi daftar transaksi baru, tidak boleh {@code null},
     *                        tidak boleh memuat elemen {@code null}, dan tidak boleh
     *                        memiliki ID transaksi duplikat
     * @throws IllegalArgumentException jika daftar bernilai {@code null}, memuat elemen
     *                                  {@code null}, atau terdapat ID transaksi duplikat
     */
    public void setDaftarTransaksi(ArrayList<Transaksi> daftarTransaksi)
    {
        if (daftarTransaksi == null) {
            throw new IllegalArgumentException("Daftar transaksi tidak boleh null.");
        }

        ArrayList<Transaksi> salinanBaru = new ArrayList<>();
        for (Transaksi item : daftarTransaksi) {
            if (item == null) {
                throw new IllegalArgumentException("Elemen transaksi di dalam daftar tidak boleh null.");
            }
            if (this.adaIdTransaksi(salinanBaru, item.getId())) {
                throw new IllegalArgumentException(
                    "ID transaksi duplikat dalam daftar: " + item.getId()
                );
            }
            salinanBaru.add(item);
        }

        this.daftarTransaksi = salinanBaru;
    }

    /**
     * Memeriksa apakah suatu ID transaksi sudah ada di dalam daftar yang diberikan.
     *
     * @param daftar daftar transaksi yang diperiksa
     * @param id kode unik transaksi yang dicari
     * @return {@code true} jika ID sudah ada secara case-insensitive, {@code false} jika belum
     */
    private boolean adaIdTransaksi(ArrayList<Transaksi> daftar, String id)
    {
        for (Transaksi item : daftar) {
            if (item.getId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }
}
