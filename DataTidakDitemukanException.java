
/**
 * Exception ketika data tidak ditemukan.
 */
public class DataTidakDitemukanException extends Exception {

    /**
     * Constructor pesan data tidak ditemukan.
     *
     * @param pesan pesan kesalahan
     */
    public DataTidakDitemukanException(String pesan) {
        super(pesan);
    }
}
