
/**
 * Exception ketika proses login gagal.
 */
public class LoginGagalException extends Exception {

    /**
     * Constructor pesan kesalahan login.
     *
     * @param pesan pesan kesalahan
     */
    public LoginGagalException(String pesan) {
        super(pesan);
    }
}
