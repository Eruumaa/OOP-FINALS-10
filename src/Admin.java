import java.time.LocalDate;
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
     * Memverifikasi dan menerima transaksi penyewaan yang berstatus menunggu konfirmasi.
     *
     * <p>Seluruh barang pada transaksi diperiksa ulang ketersediaannya terlebih dahulu.
     * Jika ada barang yang tidak tersedia, status transaksi diubah menjadi {@code "Ditolak"}
     * dan {@link BarangTidakTersediaException} dilemparkan tanpa mengubah ketersediaan barang lain.
     * Jika seluruh barang tersedia, status transaksi diubah menjadi {@code "Sedang Disewa"},
     * seluruh barang ditandai tidak tersedia, dan transaksi ditambahkan ke riwayat penyewa.</p>
     *
     * @param transaksi transaksi yang akan diterima
     * @throws BarangTidakTersediaException jika salah satu barang pada transaksi sudah disewa
     *                                      atau tidak valid
     * @throws IllegalArgumentException jika {@code transaksi} atau data penyewa/barangnya tidak valid
     * @throws IllegalStateException jika status transaksi bukan {@code "Menunggu Konfirmasi"}
     */
    public void terimaTransaksi(Transaksi transaksi) throws BarangTidakTersediaException
    {
        if (transaksi == null) {
            throw new IllegalArgumentException("Transaksi tidak boleh null.");
        }
        if (transaksi.getPenyewa() == null) {
            throw new IllegalArgumentException("Data penyewa pada transaksi tidak boleh null.");
        }
        if (!"Menunggu Konfirmasi".equalsIgnoreCase(transaksi.getStatus())) {
            throw new IllegalStateException(
                "Hanya transaksi berstatus Menunggu Konfirmasi yang dapat diterima."
            );
        }

        ArrayList<Barang> barangDisewa = transaksi.getBarang();
        if (barangDisewa == null || barangDisewa.isEmpty()) {
            throw new IllegalArgumentException("Daftar barang pada transaksi tidak boleh kosong.");
        }

        for (Barang item : barangDisewa) {
            if (item == null || !item.isTersedia()) {
                transaksi.ubahStatus("Ditolak");
                String namaBarang = (item == null) ? "(tidak dikenal)" : item.getNama();
                throw new BarangTidakTersediaException(
                    "Barang \"" + namaBarang + "\" sudah tidak tersedia. Transaksi ditolak."
                );
            }
        }

        for (Barang item : barangDisewa) {
            item.setTersedia(false);
        }

        transaksi.ubahStatus("Sedang Disewa");
        transaksi.getPenyewa().tambahRiwayat(transaksi);
    }

    /**
     * Mengonfirmasi pengembalian board game pada transaksi yang sedang disewa.
     *
     * <p>Tanggal pengembalian dicatat terlebih dahulu untuk memastikan validitasnya,
     * kemudian status transaksi diubah menjadi {@code "Selesai"} dan seluruh barang
     * pada transaksi ditandai tersedia kembali.</p>
     *
     * @param transaksi transaksi yang dikembalikan
     * @param tanggalKembali tanggal pengembalian aktual
     * @throws IllegalArgumentException jika {@code transaksi} atau {@code tanggalKembali} bernilai
     *                                  {@code null}, atau tanggal kembali sebelum tanggal sewa
     * @throws IllegalStateException jika status transaksi bukan {@code "Sedang Disewa"}
     */
    public void konfirmasiKembali(Transaksi transaksi, LocalDate tanggalKembali)
    {
        if (transaksi == null) {
            throw new IllegalArgumentException("Transaksi tidak boleh null.");
        }
        if (tanggalKembali == null) {
            throw new IllegalArgumentException("Tanggal kembali tidak boleh null.");
        }
        if (!"Sedang Disewa".equalsIgnoreCase(transaksi.getStatus())) {
            throw new IllegalStateException(
                "Hanya transaksi berstatus Sedang Disewa yang dapat dikonfirmasi pengembaliannya."
            );
        }

        transaksi.setTanggalKembali(tanggalKembali);
        transaksi.ubahStatus("Selesai");

        for (Barang item : transaksi.getBarang()) {
            if (item != null) {
                item.setTersedia(true);
            }
        }
    }

/**
 * Menampilkan seluruh transaksi yang menunggu konfirmasi admin.
 */
public void lihatTransaksiMenunggu()
{
    System.out.println("\n===== TRANSAKSI MENUNGGU KONFIRMASI =====");

    boolean ditemukan = false;

    for (Transaksi transaksi : this.daftarTransaksi) {
        if ("Menunggu Konfirmasi".equalsIgnoreCase(transaksi.getStatus())) {
            System.out.println("ID Transaksi : " + transaksi.getId());
            System.out.println("Nama Penyewa : " + transaksi.getPenyewa().getUsername());
            System.out.println("Total Biaya  : Rp" + transaksi.hitungTotal());
            System.out.println("Status       : " + transaksi.getStatus());
            System.out.println("--------------------------------");
            ditemukan = true;
        }
    }

    if (!ditemukan) {
        System.out.println("Tidak ada transaksi yang menunggu konfirmasi.");
    }
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

