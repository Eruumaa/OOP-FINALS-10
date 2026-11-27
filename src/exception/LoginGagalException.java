package exception;

/**
 * Exception ketika proses autentikasi login pengguna gagal.
 *
 * @author M. Yogi Kafrawi
 * @version 2026.11.27
 */
public class LoginGagalException extends Exception
{
    private static final long serialVersionUID = 1L;

    /**
     * Membuat exception baru dengan pesan kegagalan login.
     *
     * @param pesan pesan kesalahan
     */
    public LoginGagalException(String pesan)
    {
        super(pesan);
    }
}
