/**
 * Exception ketika data tidak ditemukan.
 *
 * @author YogiShiki
 * @version 1.0
 */
public class DataTidakDitemukanException extends Exception
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructor pesan data tidak ditemukan.
     *
     * @param pesan pesan kesalahan
     */
    public DataTidakDitemukanException(String pesan)
    {
        super(pesan);
    }
}
