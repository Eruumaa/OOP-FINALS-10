
/**
 * Class abstract untuk menyimpan informasi akun pengguna.
 */
public abstract class Akun {
    private String id;
    private String username;
    private String password;

    /**
     * Constructor untuk mengisi data akun.
     *
     * @param id ID akun
     * @param username username akun
     * @param password password akun
     */
    public Akun(String id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    /**
     * Mengambil ID akun.
     * @return ID akun
     */
    public String getId() {
        return id;
    }

    /**
     * Mengambil username.
     * @return username akun
     */
    public String getUsername() {
        return username;
    }

    /**
     * Mengambil password.
     * @return password akun
     */
    public String getPassword() {
        return password;
    }

    /**
     * Memeriksa kecocokan username dan password.
     *
     * @param username username yang dimasukkan
     * @param password password yang dimasukkan
     * @return true jika cocok, false jika tidak
     */
    public boolean login(String username, String password) {
        return this.username.equals(username)
                && this.password.equals(password);
    }
}
