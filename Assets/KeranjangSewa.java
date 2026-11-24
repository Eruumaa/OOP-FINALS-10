
import java.util.ArrayList;

/**
 * Kelas untuk mengelola keranjang penyewaan board game.
 *
 * @author Yogi
 * @version 2026.11.24
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
     * Menambahkan barang ke dalam keranjang.
     *
     * @param b barang yang akan ditambahkan
     * @throws BarangTidakTersediaException jika barang null,
     * tidak tersedia, atau sudah ada di keranjang
     */
    public void tambahBarang(Barang b) throws BarangTidakTersediaException
    {
        if (b == null) {
            throw new BarangTidakTersediaException(
                "Barang tidak ditemukan."
            );
        }

        if (!b.isTersedia()) {
            throw new BarangTidakTersediaException(
                "Barang sedang tidak tersedia."
            );
        }

        for (Barang item : this.barang) {
            if (item.getId().equalsIgnoreCase(b.getId())) {
                throw new BarangTidakTersediaException(
                    "Barang sudah ada di keranjang."
                );
            }
        }

        this.barang.add(b);
    }

    /**
     * Menghapus barang dari keranjang berdasarkan ID.
     *
     * @param id ID barang yang akan dihapus
     */
    public void hapusBarang(String id)
    {
        this.barang.removeIf(
            item -> item.getId().equalsIgnoreCase(id)
        );
    }

    /**
     * Mengambil daftar barang dalam keranjang.
     *
     * @return salinan daftar barang dalam keranjang
     */
    public ArrayList<Barang> getBarang()
    {
        return new ArrayList<>(this.barang);
    }

    /**
     * Mengosongkan seluruh isi keranjang.
     */
    public void kosongkanKeranjang()
    {
        this.barang.clear();
    }
}
