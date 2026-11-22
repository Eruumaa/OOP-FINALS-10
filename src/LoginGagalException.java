/**
 * Exception ketika proses login gagal.
 *
 * @author YogiShiki
 * @version 1.0
 */
public class LoginGagalException extends Exception
{
    private static final long serialVersionUID = 1L;

    /**
     * Constructor pesan kesalahan login.
     *
     * @param pesan pesan kesalahan
     */
    public LoginGagalException(String pesan)
    {
        super(pesan);
    }
}
