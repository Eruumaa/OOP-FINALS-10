package exception;

/**
 * Exception ketika data yang dicari tidak ditemukan di dalam sistem.
 *
 * @author M. Yogi Kafrawi
 * @version 2026.11.27
 */
public class DataTidakDitemukanException extends Exception
{
    private static final long serialVersionUID = 1L;

    /**
     * Membuat exception baru dengan pesan data tidak ditemukan.
     *
     * @param pesan pesan kesalahan
     */
    public DataTidakDitemukanException(String pesan)
    {
        super(pesan);
    }
}
