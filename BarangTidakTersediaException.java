
/**
 * Exception ketika barang tidak tersedia.
 */
public class BarangTidakTersediaException extends Exception {

    /**
     * Constructor pesan kesalahan barang.
     *
     * @param pesan pesan kesalahan
     */
    public BarangTidakTersediaException(String pesan) {
        super(pesan);
    }
}
