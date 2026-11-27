package view;

import exception.LoginGagalException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import model.Admin;
import model.Akun;
import model.Penyewa;

/**
 * Jendela login utama aplikasi GUI BoardGameRent dengan tema Warm Tabletop.
 *
 * <p>Jendela ini mencocokkan kredensial terhadap {@link ArrayList} akun terdaftar,
 * melempar dan menangkap {@link LoginGagalException} jika kredensial tidak cocok,
 * serta membuka {@link AdminFrame} atau {@link PenyewaFrame} sesuai jenis akun.</p>
 *
 * @author M. Yogi Kafrawi
 * @version 2026.11.27
 */
public class LoginFrame extends JFrame
{
    private static final long serialVersionUID = 1L;

    /** Daftar seluruh akun pengguna (Admin dan Penyewa) yang terdaftar di sistem. */
    private ArrayList<Akun> daftarAkun;
    /** Referensi akun administrator untuk menerima transaksi dari penyewa. */
    private Admin admin;
    /** Kolom input username pengguna. */
    private JTextField txtUsername;
    /** Kolom input kata sandi pengguna. */
    private JPasswordField txtPassword;
    /** Kotak centang untuk menampilkan atau menyembunyikan teks kata sandi. */
    private JCheckBox chkTampilkanPassword;
    /** Tombol utama untuk memicu proses autentikasi login. */
    private JButton btnLogin;
    /** Karakter masker bawaan pada kolom kata sandi. */
    private char karakterMaskerBawaan;

    /**
     * Membuat jendela login baru menggunakan daftar akun dan referensi admin.
     *
     * @param daftarAkun daftar akun terdaftar, tidak boleh {@code null} atau kosong
     * @param admin akun administrator sistem, tidak boleh {@code null}
     * @throws IllegalArgumentException jika {@code daftarAkun} kosong atau {@code admin} {@code null}
     */
    public LoginFrame(ArrayList<Akun> daftarAkun, Admin admin)
    {
        super("BoardGameRent - Masuk");

        if (daftarAkun == null || daftarAkun.isEmpty()) {
            throw new IllegalArgumentException("Daftar akun tidak boleh kosong.");
        }
        if (admin == null) {
            throw new IllegalArgumentException("Objek admin tidak boleh null.");
        }

        this.daftarAkun = new ArrayList<>(daftarAkun);
        this.admin = admin;
        this.txtUsername = new JTextField(20);
        this.txtPassword = new JPasswordField(20);
        this.karakterMaskerBawaan = this.txtPassword.getEchoChar();
        this.chkTampilkanPassword = new JCheckBox("Tampilkan password");
        this.btnLogin = TemaUI.buatTombolPrimer("Masuk");

        this.inisialisasiTampilan();
    }

    /**
     * Memvalidasi input username dan password, mencocokkan akun terdaftar,
     * serta membuka panel tujuan sesuai peran akun yang berhasil login.
     */
    public void prosesLogin()
    {
        String username = this.txtUsername.getText().trim();
        String password = new String(this.txtPassword.getPassword());

        try {
            Akun akunTerpilih = this.autentikasiAkun(username, password);
            this.bukaPanelAkun(akunTerpilih);
        } catch (LoginGagalException exception) {
            this.txtPassword.setText("");
            this.txtPassword.requestFocusInWindow();
            JOptionPane.showMessageDialog(
                this,
                exception.getMessage(),
                "Login Gagal",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Memeriksa kredensial pengguna pada seluruh daftar akun terdaftar.
     *
     * @param username username yang diinputkan
     * @param password password yang diinputkan
     * @return objek {@link Akun} yang cocok dengan kredensial
     * @throws LoginGagalException jika input kosong atau kombinasi username dan password salah
     */
    private Akun autentikasiAkun(String username, String password) throws LoginGagalException
    {
        if (username.isEmpty() || password.isEmpty()) {
            throw new LoginGagalException("Username dan password wajib diisi.");
        }

        for (Akun akun : this.daftarAkun) {
            if (akun.login(username, password)) {
                return akun;
            }
        }

        throw new LoginGagalException("Username atau password tidak sesuai.");
    }

    /**
     * Membuka {@link AdminFrame} atau {@link PenyewaFrame} sesuai jenis akun
     * serta menyambungkan callback untuk kembali ke layar login saat logout.
     *
     * @param akunTerpilih akun yang berhasil terautentikasi
     */
    private void bukaPanelAkun(Akun akunTerpilih)
    {
        this.txtUsername.setText("");
        this.txtPassword.setText("");
        this.dispose();

        Runnable onLogout = () -> {
            LoginFrame frameLoginBaru = new LoginFrame(this.daftarAkun, this.admin);
            frameLoginBaru.setVisible(true);
        };

        if (akunTerpilih instanceof Admin) {
            AdminFrame adminFrame = new AdminFrame((Admin) akunTerpilih, onLogout);
            adminFrame.setVisible(true);
        } else if (akunTerpilih instanceof Penyewa) {
            PenyewaFrame penyewaFrame = new PenyewaFrame(
                (Penyewa) akunTerpilih,
                this.admin,
                onLogout
            );
            penyewaFrame.setVisible(true);
        }
    }

    /**
     * Membangun tata letak kartu login bertema Warm Tabletop.
     */
    private void inisialisasiTampilan()
    {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(440, 390);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(TemaUI.WARNA_CANVAS);

        JPanel pembungkus = new JPanel(new BorderLayout());
        pembungkus.setBackground(TemaUI.WARNA_CANVAS);
        pembungkus.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel kartu = new JPanel(new GridBagLayout());
        kartu.setBackground(TemaUI.WARNA_SURFACE);
        kartu.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(TemaUI.WARNA_BORDER),
            BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JPanel headerKartu = new JPanel(new BorderLayout(0, 4));
        headerKartu.setOpaque(false);
        headerKartu.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, TemaUI.WARNA_GRID_TABEL),
            BorderFactory.createEmptyBorder(0, 0, 14, 0)
        ));

        JLabel lblJudul = new JLabel("BoardGameRent");
        lblJudul.setFont(new Font(TemaUI.FONT_UI, Font.BOLD, 20));
        lblJudul.setForeground(TemaUI.WARNA_TEKS_UTAMA);

        JLabel lblSubJudul = new JLabel("Sistem Penyewaan Board Game");
        lblSubJudul.setFont(new Font(TemaUI.FONT_UI, Font.PLAIN, 12));
        lblSubJudul.setForeground(TemaUI.WARNA_TEKS_SEKUNDER);

        headerKartu.add(lblJudul, BorderLayout.NORTH);
        headerKartu.add(lblSubJudul, BorderLayout.SOUTH);

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 16, 0);
        kartu.add(headerKartu, gbc);

        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(new Font(TemaUI.FONT_UI, Font.BOLD, 12));
        lblUsername.setForeground(TemaUI.WARNA_TEKS_UTAMA);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 5, 0);
        kartu.add(lblUsername, gbc);

        TemaUI.formatInputField(this.txtUsername);
        this.txtUsername.setPreferredSize(new Dimension(0, 34));

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 12, 0);
        kartu.add(this.txtUsername, gbc);

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font(TemaUI.FONT_UI, Font.BOLD, 12));
        lblPassword.setForeground(TemaUI.WARNA_TEKS_UTAMA);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 5, 0);
        kartu.add(lblPassword, gbc);

        TemaUI.formatInputField(this.txtPassword);
        this.txtPassword.setPreferredSize(new Dimension(0, 34));

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 8, 0);
        kartu.add(this.txtPassword, gbc);

        this.chkTampilkanPassword.setOpaque(false);
        this.chkTampilkanPassword.setFocusPainted(false);
        this.chkTampilkanPassword.setFont(new Font(TemaUI.FONT_UI, Font.PLAIN, 12));
        this.chkTampilkanPassword.setForeground(TemaUI.WARNA_TEKS_SEKUNDER);
        this.chkTampilkanPassword.addActionListener(event -> {
            if (this.chkTampilkanPassword.isSelected()) {
                this.txtPassword.setEchoChar((char) 0);
            } else {
                this.txtPassword.setEchoChar(this.karakterMaskerBawaan);
            }
        });

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 16, 0);
        kartu.add(this.chkTampilkanPassword, gbc);

        this.btnLogin.setPreferredSize(new Dimension(0, 36));
        this.btnLogin.addActionListener(event -> this.prosesLogin());

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 0, 0);
        kartu.add(this.btnLogin, gbc);

        this.getRootPane().setDefaultButton(this.btnLogin);
        pembungkus.add(kartu, BorderLayout.CENTER);
        this.add(pembungkus, BorderLayout.CENTER);
    }
}
