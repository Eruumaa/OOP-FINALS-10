package view;

import exception.LoginGagalException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
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
 * Jendela login utama aplikasi GUI BoardGameRent dengan showcase animasi pixel art
 * dan form login bertema Cozy Pixel Tabletop.
 *
 * Jendela ini mencocokkan kredensial terhadap ArrayList akun terdaftar,
 * melempar dan menangkap LoginGagalException jika kredensial tidak cocok,
 * serta membuka AdminFrame atau PenyewaFrame sesuai jenis akun.
 *
 * @author M. Yogi Kafrawi
 * @version 2026.11.27
 */
public class LoginFrame extends JFrame
{
    private static final long serialVersionUID = 1L;

    // Data akun dan komponen form login
    private transient ArrayList<Akun> daftarAkun;
    private transient Admin admin;
    private transient Runnable onDataChanged;
    private transient JTextField txtUsername;
    private transient JPasswordField txtPassword;
    private transient JCheckBox chkTampilkanPassword;
    private transient JButton btnLogin;
    private transient char karakterMaskerBawaan;

    /**
     * Membuat jendela login baru menggunakan daftar akun dan referensi admin.
     *
     * @param daftarAkun daftar akun terdaftar, tidak boleh null atau kosong
     * @param admin akun administrator sistem, tidak boleh null
     * @throws IllegalArgumentException jika daftarAkun kosong atau admin null
     */
    public LoginFrame(ArrayList<Akun> daftarAkun, Admin admin)
    {
        this(daftarAkun, admin, null);
    }

    /**
     * Membuat jendela login baru dengan callback penyimpanan data persisten.
     *
     * @param daftarAkun daftar akun terdaftar, tidak boleh null atau kosong
     * @param admin akun administrator sistem, tidak boleh null
     * @param onDataChanged callback yang dipanggil setiap kali data berubah, boleh null
     * @throws IllegalArgumentException jika daftarAkun kosong atau admin null
     */
    public LoginFrame(ArrayList<Akun> daftarAkun, Admin admin, Runnable onDataChanged)
    {
        super("Rent the Board! - BoardGameRent | Masuk");
        this.setIconImage(TemaUI.muatLogoPixel(64).getImage());

        if (daftarAkun == null || daftarAkun.isEmpty()) {
            throw new IllegalArgumentException("Daftar akun tidak boleh kosong.");
        }
        if (admin == null) {
            throw new IllegalArgumentException("Objek admin tidak boleh null.");
        }

        this.daftarAkun = daftarAkun;
        this.admin = admin;
        this.onDataChanged = onDataChanged;
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
     * @return objek Akun yang cocok dengan kredensial
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
     * Membuka AdminFrame atau PenyewaFrame sesuai jenis akun
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
            LoginFrame frameLoginBaru = new LoginFrame(
                this.daftarAkun, this.admin, this.onDataChanged
            );
            frameLoginBaru.setVisible(true);
        };

        if (akunTerpilih instanceof Admin) {
            AdminFrame adminFrame = new AdminFrame(
                (Admin) akunTerpilih, onLogout, this.onDataChanged
            );
            adminFrame.setVisible(true);
        } else if (akunTerpilih instanceof Penyewa) {
            PenyewaFrame penyewaFrame = new PenyewaFrame(
                (Penyewa) akunTerpilih,
                this.admin,
                onLogout,
                this.onDataChanged
            );
            penyewaFrame.setVisible(true);
        }
    }

    /**
     * Membangun tata letak split showcase kiri beranimasi GIF pixel art
     * dan kartu form login kanan bertema Cozy Pixel Tabletop.
     */
    private void inisialisasiTampilan()
    {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(920, 520);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(TemaUI.WARNA_CANVAS);

        JPanel pembungkus = new JPanel(new GridLayout(1, 2, 22, 0));
        pembungkus.setBackground(TemaUI.WARNA_CANVAS);
        pembungkus.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        pembungkus.add(new PanelShowcaseAnimasi(this.admin.kelolaBarang().getBarang().size()));
        pembungkus.add(this.buatPanelFormLogin());

        this.add(pembungkus, BorderLayout.CENTER);
    }

    /**
     * Menyusun kartu form login di sisi kanan jendela.
     *
     * @return panel form login terformat
     */
    private JPanel buatPanelFormLogin()
    {
        JPanel kartu = new JPanel(new GridBagLayout());
        kartu.setBackground(TemaUI.WARNA_SURFACE);
        kartu.setBorder(TemaUI.buatBorderKartuPixel(26, 26, 26, 26));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JPanel headerKartu = new JPanel(new BorderLayout(0, 4));
        headerKartu.setOpaque(false);
        headerKartu.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, TemaUI.WARNA_GRID_TABEL),
            BorderFactory.createEmptyBorder(0, 0, 14, 0)
        ));

        JLabel lblJudul = new JLabel("Masuk ke Akun");
        lblJudul.setFont(new Font(TemaUI.FONT_UI, Font.BOLD, 20));
        lblJudul.setForeground(TemaUI.WARNA_TEKS_UTAMA);

        JLabel lblSubJudul = new JLabel("Gunakan akun Admin atau Penyewa terdaftar");
        lblSubJudul.setFont(new Font(TemaUI.FONT_UI, Font.PLAIN, 12));
        lblSubJudul.setForeground(TemaUI.WARNA_TEKS_SEKUNDER);

        headerKartu.add(lblJudul, BorderLayout.NORTH);
        headerKartu.add(lblSubJudul, BorderLayout.SOUTH);

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 18, 0);
        kartu.add(headerKartu, gbc);

        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(new Font(TemaUI.FONT_UI, Font.BOLD, 12));
        lblUsername.setForeground(TemaUI.WARNA_TEKS_UTAMA);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 6, 0);
        kartu.add(lblUsername, gbc);

        TemaUI.formatInputField(this.txtUsername);
        this.txtUsername.setPreferredSize(new Dimension(0, 36));

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 14, 0);
        kartu.add(this.txtUsername, gbc);

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font(TemaUI.FONT_UI, Font.BOLD, 12));
        lblPassword.setForeground(TemaUI.WARNA_TEKS_UTAMA);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 6, 0);
        kartu.add(lblPassword, gbc);

        TemaUI.formatInputField(this.txtPassword);
        this.txtPassword.setPreferredSize(new Dimension(0, 36));

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 10, 0);
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
        gbc.insets = new Insets(0, 0, 18, 0);
        kartu.add(this.chkTampilkanPassword, gbc);

        this.btnLogin.setPreferredSize(new Dimension(0, 40));
        this.btnLogin.addActionListener(event -> this.prosesLogin());

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 10, 0);
        kartu.add(this.btnLogin, gbc);

        JButton btnDaftar = TemaUI.buatTombolSekunder("Daftar Akun Penyewa");
        btnDaftar.setPreferredSize(new Dimension(0, 36));
        btnDaftar.addActionListener(event -> this.bukaDialogDaftarPenyewa());

        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 0, 0);
        kartu.add(btnDaftar, gbc);

        this.getRootPane().setDefaultButton(this.btnLogin);
        return kartu;
    }

    /**
     * Membuka dialog pendaftaran akun penyewa baru dan menambahkannya ke daftar akun aktif.
     */
    private void bukaDialogDaftarPenyewa()
    {
        JTextField txtUserBaru = new JTextField(18);
        JPasswordField txtPassBaru = new JPasswordField(18);
        JPasswordField txtKonfirmasi = new JPasswordField(18);
        TemaUI.formatInputField(txtUserBaru);
        TemaUI.formatInputField(txtPassBaru);
        TemaUI.formatInputField(txtKonfirmasi);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Username Baru:"), gbc);
        gbc.gridx = 1;
        form.add(txtUserBaru, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        form.add(txtPassBaru, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        form.add(new JLabel("Ulangi Password:"), gbc);
        gbc.gridx = 1;
        form.add(txtKonfirmasi, gbc);

        int hasil = JOptionPane.showConfirmDialog(
            this,
            form,
            "Daftar Akun Penyewa Baru",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        if (hasil != JOptionPane.OK_OPTION) {
            return;
        }

        String username = txtUserBaru.getText().trim();
        String password = new String(txtPassBaru.getPassword());
        String konfirmasi = new String(txtKonfirmasi.getPassword());

        if (username.isEmpty() || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Username dan password tidak boleh kosong.",
                "Input Tidak Valid",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        if (!password.equals(konfirmasi)) {
            JOptionPane.showMessageDialog(
                this,
                "Konfirmasi password tidak cocok.",
                "Pendaftaran Gagal",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        for (Akun akun : this.daftarAkun) {
            if (akun.getUsername().equalsIgnoreCase(username)) {
                JOptionPane.showMessageDialog(
                    this,
                    "Username \"" + username + "\" sudah terdaftar.",
                    "Username Sudah Dipakai",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }
        }

        int jumlahPenyewa = 0;
        for (Akun akun : this.daftarAkun) {
            if (akun instanceof Penyewa) {
                jumlahPenyewa++;
            }
        }
        String idBaru = String.format("P%02d", jumlahPenyewa + 1);
        Penyewa penyewaBaru = new Penyewa(idBaru, username, password, this.admin.kelolaBarang());
        this.daftarAkun.add(penyewaBaru);
        if (this.onDataChanged != null) {
            this.onDataChanged.run();
        }

        this.txtUsername.setText(username);
        this.txtPassword.setText("");
        this.txtPassword.requestFocusInWindow();

        JOptionPane.showMessageDialog(
            this,
            "Akun penyewa \"" + username + "\" (" + idBaru + ") berhasil dibuat.\nSilakan masuk.",
            "Pendaftaran Berhasil",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Panel showcase sisi kiri layar login yang menampilkan animasi GIF pixel art
     * beserta ringkasan merek BoardGameRent di atas gradasi gelap.
     */
    private static class PanelShowcaseAnimasi extends JPanel
    {
        private static final long serialVersionUID = 1L;

        // GIF animasi dan frame awal latar showcase
        private ImageIcon animasiLatar;
        private BufferedImage gambarAwal;

        /**
         * Membuat panel showcase dengan animasi pixel art dan informasi singkat katalog.
         *
         * @param jumlahKoleksi jumlah board game awal yang terdaftar
         */
        public PanelShowcaseAnimasi(int jumlahKoleksi)
        {
            this.animasiLatar = TemaUI.muatAnimasiLogin();
            this.gambarAwal = TemaUI.muatGambarAwalLogin();
            this.setLayout(new BorderLayout());
            this.setBackground(TemaUI.WARNA_HEADER);
            this.setBorder(TemaUI.buatBorderKartuPixel(20, 22, 22, 22));

            JPanel kontenBawah = new JPanel(new GridBagLayout());
            kontenBawah.setOpaque(false);

            JLabel lblLogo = new JLabel(TemaUI.muatLogoPixel(56));
            JLabel lblMerek = new JLabel("Rent the Board!");
            lblMerek.setFont(new Font(TemaUI.FONT_UI, Font.BOLD, 28));
            lblMerek.setForeground(Color.WHITE);

            JLabel lblSub = new JLabel("BoardGameRent | Cozy Pixel Tabletop Store");
            lblSub.setFont(new Font(TemaUI.FONT_UI, Font.PLAIN, 13));
            lblSub.setForeground(TemaUI.WARNA_HEADER_SUB);

            JLabel lblInfo = new JLabel(
                "\"Every roll brings a new story to the table.\""
            );
            lblInfo.setFont(new Font(TemaUI.FONT_MONO, Font.ITALIC, 11));
            lblInfo.setForeground(TemaUI.WARNA_SELEKSI);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            gbc.anchor = GridBagConstraints.WEST;

            gbc.gridy = 0;
            gbc.insets = new Insets(0, 0, 10, 0);
            JPanel wadahLogo = new JPanel(new BorderLayout());
            wadahLogo.setOpaque(false);
            wadahLogo.add(lblLogo, BorderLayout.WEST);
            kontenBawah.add(wadahLogo, gbc);

            gbc.gridy = 1;
            gbc.insets = new Insets(0, 0, 4, 0);
            kontenBawah.add(lblMerek, gbc);

            gbc.gridy = 2;
            gbc.insets = new Insets(0, 0, 10, 0);
            kontenBawah.add(lblSub, gbc);

            gbc.gridy = 3;
            gbc.insets = new Insets(0, 0, 0, 0);
            kontenBawah.add(lblInfo, gbc);

            this.add(kontenBawah, BorderLayout.SOUTH);
        }

        /**
         * Menggambar animasi GIF latar secara penuh proporsional beserta gradasi gelap
         * agar teks tetap mudah dibaca.
         *
         * @param g konteks grafis panel
         */
        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            int w = this.getWidth();
            int h = this.getHeight();

            if (this.gambarAwal != null && this.gambarAwal.getWidth() > 0) {
                this.gambarProporsional(g2, this.gambarAwal, this.gambarAwal.getWidth(),
                    this.gambarAwal.getHeight(), w, h, false);
            } else {
                g2.setColor(TemaUI.WARNA_HEADER);
                g2.fillRect(0, 0, w, h);
            }

            if (this.animasiLatar != null && this.animasiLatar.getIconWidth() > 0) {
                this.gambarProporsional(g2, this.animasiLatar.getImage(),
                    this.animasiLatar.getIconWidth(), this.animasiLatar.getIconHeight(), w, h, true);
            }

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint scrim = new GradientPaint(
                0, (float) (h * 0.35), new Color(28, 25, 23, 35),
                0, h, new Color(28, 25, 23, 235)
            );
            g2.setPaint(scrim);
            g2.fillRect(0, 0, w, h);
            g2.dispose();
        }

        /**
         * Menggambar gambar secara proporsional memenuhi area panel target.
         *
         * @param g2 konteks grafis 2D
         * @param img gambar yang akan digambar
         * @param iw lebar asli gambar
         * @param ih tinggi asli gambar
         * @param w lebar panel target
         * @param h tinggi panel target
         * @param pantauAnimasi true untuk meneruskan observer animasi GIF
         */
        private void gambarProporsional(Graphics2D g2, Image img, int iw, int ih,
                                        int w, int h, boolean pantauAnimasi)
        {
            double skala = Math.max((double) w / iw, (double) h / ih);
            int dw = (int) Math.ceil(iw * skala);
            int dh = (int) Math.ceil(ih * skala);
            int dx = (w - dw) / 2;
            int dy = (h - dh) / 2;
            g2.drawImage(img, dx, dy, dw, dh, pantauAnimasi ? this : null);
        }
    }
}
