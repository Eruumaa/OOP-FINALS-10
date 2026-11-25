import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * Antarmuka grafis (GUI Java Swing) untuk akun Administrator pada sistem BoardGameRent.
 *
 * <p>Jendela ini menggunakan {@link JTabbedPane} dengan tiga tab utama:
 * Kelola Barang, Transaksi Menunggu, dan Pengembalian. Seluruh kesalahan validasi
 * dan exception dari kelas model ditangkap di sini lalu ditampilkan melalui
 * {@link JOptionPane}.</p>
 *
 * @author Aqil Mubarak
 * @version 2026.11.25
 */
public class AdminFrame extends JFrame
{
    private static final long serialVersionUID = 1L;
    private static final Color WARNA_HEADER = new Color(30, 41, 59);
    private static final Color WARNA_LATAR = new Color(248, 250, 252);

    /** Objek admin yang sedang aktif pada jendela ini. */
    private Admin admin;
    /** Tabel katalog board game pada tab Kelola Barang. */
    private JTable tabelBarang;
    /** Tabel transaksi menunggu konfirmasi pada tab Transaksi Menunggu. */
    private JTable tabelTransaksi;
    /** Tabel transaksi sedang disewa pada tab Pengembalian. */
    private JTable tabelPengembalian;
    /** Model data untuk tabel katalog board game. */
    private DefaultTableModel modelBarang;
    /** Model data untuk tabel transaksi menunggu konfirmasi. */
    private DefaultTableModel modelTransaksi;
    /** Model data untuk tabel transaksi pengembalian. */
    private DefaultTableModel modelPengembalian;
    /** Panel tab utama untuk memisahkan tiga fitur admin. */
    private JTabbedPane tabbedPane;
    /** Kolom input tanggal pengembalian berformat yyyy-MM-dd. */
    private JTextField txtTanggalKembali;
    /** Callback opsional yang dijalankan ketika admin melakukan logout. */
    private Runnable onLogout;

    /**
     * Model tabel read-only agar sel pada {@link JTable} tidak dapat diubah langsung oleh pengguna.
     */
    private static class TabelModelReadOnly extends DefaultTableModel
    {
        private static final long serialVersionUID = 1L;

        /**
         * Membuat model tabel read-only dengan nama kolom tertentu.
         *
         * @param kolom array judul kolom tabel
         */
        public TabelModelReadOnly(Object[] kolom)
        {
            super(kolom, 0);
        }

        /**
         * Menolak pengeditan langsung pada seluruh sel tabel.
         *
         * @param row indeks baris
         * @param column indeks kolom
         * @return selalu {@code false}
         */
        @Override
        public boolean isCellEditable(int row, int column)
        {
            return false;
        }
    }

    /**
     * Membuat jendela {@code AdminFrame} baru tanpa aksi callback logout tambahan.
     *
     * @param admin objek {@link Admin} yang sedang login, tidak boleh {@code null}
     * @throws IllegalArgumentException jika {@code admin} bernilai {@code null}
     */
    public AdminFrame(Admin admin)
    {
        this(admin, null);
    }

    /**
     * Membuat jendela {@code AdminFrame} baru dengan callback opsional saat admin logout.
     *
     * @param admin objek {@link Admin} yang sedang login, tidak boleh {@code null}
     * @param onLogout aksi yang dijalankan setelah jendela ditutup melalui tombol Logout,
     *                 boleh {@code null}
     * @throws IllegalArgumentException jika {@code admin} bernilai {@code null}
     */
    public AdminFrame(Admin admin, Runnable onLogout)
    {
        super("BoardGameRent - Panel Admin");

        if (admin == null) {
            throw new IllegalArgumentException("Objek admin tidak boleh null.");
        }

        this.admin = admin;
        this.onLogout = onLogout;

        this.modelBarang = new TabelModelReadOnly(new String[] {
            "ID", "Nama", "Kategori", "Tarif/Hari", "Biaya Tambahan", "Denda/Hari", "Status"
        });
        this.modelTransaksi = new TabelModelReadOnly(new String[] {
            "ID Transaksi", "Penyewa", "Daftar Barang", "Lama (Hari)",
            "Total Biaya", "Pembayaran", "Status"
        });
        this.modelPengembalian = new TabelModelReadOnly(new String[] {
            "ID Transaksi", "Penyewa", "Daftar Barang", "Tgl Sewa", "Jatuh Tempo", "Status"
        });

        this.tabelBarang = this.buatTabelStandar(this.modelBarang);
        this.tabelTransaksi = this.buatTabelStandar(this.modelTransaksi);
        this.tabelPengembalian = this.buatTabelStandar(this.modelPengembalian);
        this.tabbedPane = new JTabbedPane();
        this.txtTanggalKembali = new JTextField(LocalDate.now().toString(), 12);

        this.inisialisasiTampilan();
        this.tampilkanBarang();
    }

    /**
     * Memuat ulang seluruh data board game dari katalog admin ke dalam tabel barang.
     */
    public void tampilkanBarang()
    {
        this.modelBarang.setRowCount(0);
        ArrayList<Barang> daftar = this.admin.kelolaBarang().getBarang();

        for (Barang item : daftar) {
            String kategori = item.getClass().getSimpleName();
            String biayaTambahan = "-";

            if (item instanceof FamilyBoardGame) {
                FamilyBoardGame familyGame = (FamilyBoardGame) item;
                biayaTambahan = "Garansi: " + this.formatRupiah(familyGame.getBiayaGaransiPion());
            } else if (item instanceof StrategyGame) {
                StrategyGame strategyGame = (StrategyGame) item;
                biayaTambahan = "Modul/hari: " + this.formatRupiah(strategyGame.getBiayaModulHarian());
            }

            String status;
            if (item.isTersedia()) {
                status = "Tersedia";
            } else {
                status = "Sedang Disewa";
            }

            this.modelBarang.addRow(new Object[] {
                item.getId(),
                item.getNama(),
                kategori,
                this.formatRupiah(item.getTarifHarian()),
                biayaTambahan,
                this.formatRupiah(item.getDendaPerHari()),
                status
            });
        }
    }

    /**
     * Membuka tab Kelola Barang dan menampilkan dialog pilihan aksi pengelolaan katalog
     * (Tambah, Edit, atau Hapus barang).
     */
    public void aksiKelolaBarang()
    {
        this.tabbedPane.setSelectedIndex(0);

        String[] pilihan = {"Tambah Barang", "Edit Barang", "Hapus Barang", "Batal"};
        int hasil = JOptionPane.showOptionDialog(
            this,
            "Pilih aksi pengelolaan katalog board game:",
            "Kelola Barang",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            pilihan,
            pilihan[0]
        );

        if (hasil == 0) {
            this.bukaDialogTambahBarang();
        } else if (hasil == 1) {
            this.bukaDialogEditBarang();
        } else if (hasil == 2) {
            this.prosesHapusBarang();
        }
    }

    /**
     * Mengembalikan objek {@link Admin} yang terhubung dengan jendela ini.
     *
     * @return objek admin aktif
     */
    public Admin getAdmin()
    {
        return this.admin;
    }

    /**
     * Mengembalikan komponen {@link JTable} yang menampilkan daftar barang.
     *
     * @return tabel barang
     */
    public JTable getTabelBarang()
    {
        return this.tabelBarang;
    }

    /**
     * Mengembalikan komponen {@link JTable} yang menampilkan transaksi menunggu konfirmasi.
     *
     * @return tabel transaksi menunggu
     */
    public JTable getTabelTransaksi()
    {
        return this.tabelTransaksi;
    }

    /**
     * Mengembalikan komponen {@link JTable} yang menampilkan transaksi sedang disewa
     * pada tab Pengembalian.
     *
     * @return tabel transaksi pengembalian
     */
    public JTable getTabelPengembalian()
    {
        return this.tabelPengembalian;
    }

    /**
     * Membangun tata letak utama jendela meliputi header, tiga tab utama, dan properti frame.
     */
    private void inisialisasiTampilan()
    {
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setMinimumSize(new Dimension(860, 540));
        this.setSize(960, 620);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(WARNA_LATAR);

        this.add(this.buatPanelHeader(), BorderLayout.NORTH);

        this.tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));
        this.tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 14, 14, 14));
        this.tabbedPane.addTab("Kelola Barang", this.buatPanelKelolaBarang());
        this.tabbedPane.addTab("Transaksi Menunggu", this.buatPanelTransaksiMenunggu());
        this.tabbedPane.addTab("Pengembalian", this.buatPanelPengembalian());

        this.add(this.tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Membuat panel header yang menampilkan judul aplikasi, nama admin aktif,
     * tombol Refresh, dan tombol Logout.
     *
     * @return panel header aplikasi
     */
    private JPanel buatPanelHeader()
    {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(WARNA_HEADER);
        header.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JPanel infoKiri = new JPanel(new BorderLayout());
        infoKiri.setOpaque(false);

        JLabel lblJudul = new JLabel("BoardGameRent — Panel Admin");
        lblJudul.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblJudul.setForeground(Color.WHITE);

        JLabel lblSubJudul = new JLabel("Login sebagai: " + this.admin.getUsername());
        lblSubJudul.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSubJudul.setForeground(new Color(203, 213, 225));

        infoKiri.add(lblJudul, BorderLayout.NORTH);
        infoKiri.add(lblSubJudul, BorderLayout.SOUTH);

        JPanel aksiKanan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        aksiKanan.setOpaque(false);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(event -> this.tampilkanBarang());

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(event -> this.prosesLogout());

        aksiKanan.add(btnRefresh);
        aksiKanan.add(btnLogout);

        header.add(infoKiri, BorderLayout.WEST);
        header.add(aksiKanan, BorderLayout.EAST);

        return header;
    }

    /**
     * Membuat panel untuk tab Kelola Barang beserta tabel katalog dan tombol CRUD.
     *
     * @return panel tab Kelola Barang
     */
    private JPanel buatPanelKelolaBarang()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(WARNA_LATAR);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JScrollPane scrollPane = new JScrollPane(this.tabelBarang);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelTombol.setOpaque(false);

        JButton btnTambah = new JButton("Tambah Barang");
        btnTambah.addActionListener(event -> this.bukaDialogTambahBarang());

        JButton btnEdit = new JButton("Edit Barang");
        btnEdit.addActionListener(event -> this.bukaDialogEditBarang());

        JButton btnHapus = new JButton("Hapus Barang");
        btnHapus.addActionListener(event -> this.prosesHapusBarang());

        panelTombol.add(btnTambah);
        panelTombol.add(btnEdit);
        panelTombol.add(btnHapus);

        panel.add(panelTombol, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Membuat kerangka panel untuk tab Transaksi Menunggu.
     *
     * @return panel tab Transaksi Menunggu
     */
    private JPanel buatPanelTransaksiMenunggu()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(WARNA_LATAR);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JScrollPane scrollPane = new JScrollPane(this.tabelTransaksi);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Membuat kerangka panel untuk tab Pengembalian.
     *
     * @return panel tab Pengembalian
     */
    private JPanel buatPanelPengembalian()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(WARNA_LATAR);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JScrollPane scrollPane = new JScrollPane(this.tabelPengembalian);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel panelBawah = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBawah.setOpaque(false);
        panelBawah.add(new JLabel("Tanggal Kembali (yyyy-MM-dd):"));
        panelBawah.add(this.txtTanggalKembali);

        panel.add(panelBawah, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Membuat komponen {@link JTable} dengan konfigurasi tampilan dan seleksi standar.
     *
     * @param model model data tabel
     * @return objek {@link JTable} yang telah dikonfigurasi
     */
    private JTable buatTabelStandar(DefaultTableModel model)
    {
        JTable tabel = new JTable(model);
        tabel.setRowHeight(26);
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabel.setFillsViewportHeight(true);
        tabel.getTableHeader().setReorderingAllowed(false);
        tabel.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        return tabel;
    }

    /**
     * Membuka dialog form untuk menambahkan board game baru sesuai kategori yang dipilih.
     */
    private void bukaDialogTambahBarang()
    {
        JComboBox<String> cmbKategori = new JComboBox<>(new String[] {
            "CardGame", "FamilyBoardGame", "StrategyGame"
        });
        JTextField txtId = new JTextField(16);
        JTextField txtNama = new JTextField(16);
        JTextField txtTarif = new JTextField(16);
        JTextField txtDenda = new JTextField(16);
        JLabel lblBiayaTambahan = new JLabel("Biaya Tambahan (Rp):");
        JTextField txtBiayaTambahan = new JTextField("0", 16);
        txtBiayaTambahan.setEnabled(false);

        cmbKategori.addActionListener(event -> {
            String kategoriTerpilih = (String) cmbKategori.getSelectedItem();
            if ("FamilyBoardGame".equals(kategoriTerpilih)) {
                lblBiayaTambahan.setText("Biaya Garansi Pion (Rp):");
                txtBiayaTambahan.setEnabled(true);
                txtBiayaTambahan.setText("");
            } else if ("StrategyGame".equals(kategoriTerpilih)) {
                lblBiayaTambahan.setText("Biaya Modul Harian (Rp):");
                txtBiayaTambahan.setEnabled(true);
                txtBiayaTambahan.setText("");
            } else {
                lblBiayaTambahan.setText("Biaya Tambahan (Rp):");
                txtBiayaTambahan.setText("0");
                txtBiayaTambahan.setEnabled(false);
            }
        });

        JPanel form = this.buatPanelFormBarang(
            cmbKategori, txtId, txtNama, txtTarif, txtDenda, lblBiayaTambahan, txtBiayaTambahan
        );

        int hasil = JOptionPane.showConfirmDialog(
            this,
            form,
            "Tambah Board Game Baru",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (hasil != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String kategori = (String) cmbKategori.getSelectedItem();
            String id = txtId.getText().trim();
            String nama = txtNama.getText().trim();
            double tarif = Double.parseDouble(txtTarif.getText().trim());
            double denda = Double.parseDouble(txtDenda.getText().trim());

            Barang barangBaru;
            if ("FamilyBoardGame".equals(kategori)) {
                double garansi = Double.parseDouble(txtBiayaTambahan.getText().trim());
                barangBaru = new FamilyBoardGame(id, nama, tarif, denda, garansi);
            } else if ("StrategyGame".equals(kategori)) {
                double modul = Double.parseDouble(txtBiayaTambahan.getText().trim());
                barangBaru = new StrategyGame(id, nama, tarif, denda, modul);
            } else {
                barangBaru = new CardGame(id, nama, tarif, denda);
            }

            this.admin.kelolaBarang().tambahBarang(barangBaru);
            this.tampilkanBarang();
            JOptionPane.showMessageDialog(
                this,
                "Barang \"" + barangBaru.getNama() + "\" berhasil ditambahkan.",
                "Berhasil",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(
                this,
                "Tarif, denda, dan biaya tambahan harus berupa angka yang valid.",
                "Input Tidak Valid",
                JOptionPane.ERROR_MESSAGE
            );
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(
                this,
                exception.getMessage(),
                "Gagal Menambahkan Barang",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Menyusun panel form input penambahan barang menggunakan {@link GridBagLayout}.
     *
     * @param cmbKategori pemilih kategori barang
     * @param txtId input ID barang
     * @param txtNama input nama barang
     * @param txtTarif input tarif harian
     * @param txtDenda input denda per hari
     * @param lblBiayaTambahan label dinamis biaya tambahan
     * @param txtBiayaTambahan input biaya tambahan
     * @return panel form siap ditampilkan di dialog
     */
    private JPanel buatPanelFormBarang(JComboBox<String> cmbKategori, JTextField txtId,
                                       JTextField txtNama, JTextField txtTarif,
                                       JTextField txtDenda, JLabel lblBiayaTambahan,
                                       JTextField txtBiayaTambahan)
    {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Object[][] baris = {
            {new JLabel("Kategori:"), cmbKategori},
            {new JLabel("ID Barang:"), txtId},
            {new JLabel("Nama Board Game:"), txtNama},
            {new JLabel("Tarif Harian (Rp):"), txtTarif},
            {new JLabel("Denda per Hari (Rp):"), txtDenda},
            {lblBiayaTambahan, txtBiayaTambahan}
        };

        for (int index = 0; index < baris.length; index++) {
            gbc.gridx = 0;
            gbc.gridy = index;
            gbc.weightx = 0.0;
            form.add((JLabel) baris[index][0], gbc);

            gbc.gridx = 1;
            gbc.weightx = 1.0;
            if (baris[index][1] instanceof JComboBox) {
                form.add((JComboBox<?>) baris[index][1], gbc);
            } else {
                form.add((JTextField) baris[index][1], gbc);
            }
        }

        return form;
    }

    /**
     * Membuka dialog untuk mengubah nama dan tarif harian barang yang sedang dipilih pada tabel.
     */
    private void bukaDialogEditBarang()
    {
        int barisTerpilih = this.tabelBarang.getSelectedRow();
        if (barisTerpilih < 0) {
            JOptionPane.showMessageDialog(
                this,
                "Pilih satu barang pada tabel terlebih dahulu untuk diedit.",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String idBarang = String.valueOf(this.modelBarang.getValueAt(barisTerpilih, 0));

        try {
            Barang barang = this.admin.kelolaBarang().cariBarang(idBarang);

            JTextField txtNama = new JTextField(barang.getNama(), 18);
            JTextField txtTarif = new JTextField(String.valueOf((long) barang.getTarifHarian()), 18);

            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(4, 4, 4, 4);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0;
            gbc.gridy = 0;
            form.add(new JLabel("ID Barang:"), gbc);
            gbc.gridx = 1;
            form.add(new JLabel(barang.getId()), gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            form.add(new JLabel("Nama Baru:"), gbc);
            gbc.gridx = 1;
            form.add(txtNama, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            form.add(new JLabel("Tarif Harian Baru (Rp):"), gbc);
            gbc.gridx = 1;
            form.add(txtTarif, gbc);

            int hasil = JOptionPane.showConfirmDialog(
                this,
                form,
                "Edit Board Game",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );

            if (hasil != JOptionPane.OK_OPTION) {
                return;
            }

            double tarifBaru = Double.parseDouble(txtTarif.getText().trim());
            this.admin.kelolaBarang().editBarang(idBarang, txtNama.getText().trim(), tarifBaru);
            this.tampilkanBarang();
            JOptionPane.showMessageDialog(
                this,
                "Data barang \"" + idBarang + "\" berhasil diperbarui.",
                "Berhasil",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(
                this,
                "Tarif harian harus berupa angka yang valid.",
                "Input Tidak Valid",
                JOptionPane.ERROR_MESSAGE
            );
        } catch (DataTidakDitemukanException | IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(
                this,
                exception.getMessage(),
                "Gagal Mengubah Barang",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Menghapus barang yang sedang dipilih pada tabel setelah mendapat konfirmasi dari admin.
     */
    private void prosesHapusBarang()
    {
        int barisTerpilih = this.tabelBarang.getSelectedRow();
        if (barisTerpilih < 0) {
            JOptionPane.showMessageDialog(
                this,
                "Pilih satu barang pada tabel terlebih dahulu untuk dihapus.",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String idBarang = String.valueOf(this.modelBarang.getValueAt(barisTerpilih, 0));
        String namaBarang = String.valueOf(this.modelBarang.getValueAt(barisTerpilih, 1));

        int konfirmasi = JOptionPane.showConfirmDialog(
            this,
            "Yakin ingin menghapus board game \"" + namaBarang + "\" (" + idBarang + ")?",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            this.admin.kelolaBarang().hapusBarang(idBarang);
            this.tampilkanBarang();
            JOptionPane.showMessageDialog(
                this,
                "Barang \"" + namaBarang + "\" berhasil dihapus.",
                "Berhasil",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (DataTidakDitemukanException | IllegalStateException exception) {
            JOptionPane.showMessageDialog(
                this,
                exception.getMessage(),
                "Gagal Menghapus Barang",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Menutup jendela admin dan menjalankan callback logout bila tersedia.
     */
    private void prosesLogout()
    {
        this.dispose();
        if (this.onLogout != null) {
            this.onLogout.run();
        }
    }

    /**
     * Memformat nilai nominal uang ke dalam format mata uang rupiah Indonesia.
     *
     * @param nominal jumlah uang dalam rupiah
     * @return teks terformat rupiah
     */
    private String formatRupiah(double nominal)
    {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
        return format.format(nominal);
    }
}
