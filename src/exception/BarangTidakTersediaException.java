package exception;

/**
 * Exception ketika barang tidak tersedia untuk disewa atau ditambahkan ke keranjang.
 *
 * @author M. Yogi Kafrawi
 * @version 2026.11.27
 */
public class BarangTidakTersediaException extends Exception
{
    private static final long serialVersionUID = 1L;

    /**
     * Membuat exception baru dengan pesan kesalahan ketersediaan barang.
     *
     * @param pesan pesan kesalahan
     */
    public BarangTidakTersediaException(String pesan)
    {
        super(pesan);
    }
}
