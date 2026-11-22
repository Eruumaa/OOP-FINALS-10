import java.util.Objects;

/**
 * Class abstract untuk menyimpan informasi akun pengguna.
 *
 * @author YogiShiki
 * @version 1.0
 */
public abstract class Akun
{
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
    public Akun(String id, String username, String password)
    {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    /**
     * Mengambil ID akun.
     *
     * @return ID akun
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * Mengubah ID akun.
     *
     * @param id ID akun baru
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Mengambil username.
     *
     * @return username akun
     */
    public String getUsername()
    {
        return this.username;
    }

    /**
     * Mengubah username akun.
     *
     * @param username username akun baru
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * Mengambil password.
     *
     * @return password akun
     */
    public String getPassword()
    {
        return this.password;
    }

    /**
     * Mengubah password akun.
     *
     * @param password password akun baru
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * Memeriksa kecocokan username dan password.
     *
     * @param username username yang dimasukkan
     * @param password password yang dimasukkan
     * @return {@code true} jika cocok, {@code false} jika tidak
     */
    public boolean login(String username, String password)
    {
        return Objects.equals(this.username, username)
                && Objects.equals(this.password, password);
    }
}
