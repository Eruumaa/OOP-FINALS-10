package model;

import java.util.Objects;

/**
 * Kelas abstrak untuk menyimpan informasi dasar akun pengguna sistem BoardGameRent.
 *
 * @author M. Yogi Kafrawi
 * @version 2026.11.27
 */
public abstract class Akun
{
    private String id;
    private String username;
    private String password;

    /**
     * Membuat objek akun baru dengan validasi data wajib.
     *
     * @param id ID unik akun, tidak boleh kosong
     * @param username username untuk login, tidak boleh kosong
     * @param password password untuk login, tidak boleh kosong
     * @throws IllegalArgumentException jika salah satu parameter bernilai {@code null} atau kosong
     */
    public Akun(String id, String username, String password)
    {
        this.setId(id);
        this.setUsername(username);
        this.setPassword(password);
    }

    /**
     * Mengambil ID unik akun.
     *
     * @return ID akun
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * Mengubah ID unik akun.
     *
     * @param id ID akun baru, tidak boleh {@code null} atau kosong
     * @throws IllegalArgumentException jika {@code id} bernilai {@code null} atau kosong
     */
    public void setId(String id)
    {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID akun tidak boleh kosong.");
        }
        this.id = id.trim();
    }

    /**
     * Mengambil username akun.
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
     * @param username username baru, tidak boleh {@code null} atau kosong
     * @throws IllegalArgumentException jika {@code username} bernilai {@code null} atau kosong
     */
    public void setUsername(String username)
    {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username tidak boleh kosong.");
        }
        this.username = username.trim();
    }

    /**
     * Mengambil password akun.
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
     * @param password password baru, tidak boleh {@code null} atau kosong
     * @throws IllegalArgumentException jika {@code password} bernilai {@code null} atau kosong
     */
    public void setPassword(String password)
    {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password tidak boleh kosong.");
        }
        this.password = password;
    }

    /**
     * Memeriksa kecocokan username dan password pengguna.
     *
     * @param username username yang dimasukkan
     * @param password password yang dimasukkan
     * @return {@code true} jika kredensial cocok, {@code false} jika tidak
     */
    public boolean login(String username, String password)
    {
        return Objects.equals(this.username, username)
            && Objects.equals(this.password, password);
    }
}
