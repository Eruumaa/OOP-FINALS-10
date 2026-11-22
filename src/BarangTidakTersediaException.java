/**
 * Exception ketika barang tidak tersedia.
 *
 * @author YogiShiki
 * @version 1.0
 */
public class BarangTidakTersediaException extends Exception
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructor pesan kesalahan barang.
     *
     * @param pesan pesan kesalahan
     */
    public BarangTidakTersediaException(String pesan)
    {
        super(pesan);
    }
}
