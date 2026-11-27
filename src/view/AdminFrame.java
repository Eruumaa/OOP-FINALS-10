package view;

import exception.BarangTidakTersediaException;
import exception.DataTidakDitemukanException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.Admin;
import model.Barang;
import model.CardGame;
import model.FamilyBoardGame;
import model.StrategyGame;
import model.Transaksi;

/**
 * Antarmuka grafis (GUI Java Swing) untuk akun Administrator pada sistem BoardGameRent.
 *
 * <p>Jendela ini menggunakan tema Warm Tabletop dengan {@link JTabbedPane} berisi
 * tiga tab utama: Kelola Barang, Transaksi Menunggu, dan Pengembalian. Seluruh kesalahan
 * validasi dan exception dari kelas model ditangkap di sini lalu ditampilkan melalui
 * {@link JOptionPane}.</p>
 *
 * @author Muhammad Aqil Mubarak
 * @version 2026.11.27
 */
public class AdminFrame extends JFrame
{
    private static final long serialVersionUID = 1L;

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
    /** Label ringkasan jumlah total dan ketersediaan barang pada tab Kelola Barang. */
    private JLabel lblRingkasanBarang;
    /** Label ringkasan jumlah transaksi menunggu pada tab Transaksi Menunggu. */
    private JLabel lblRingkasanTransaksi;
    /** Label ringkasan jumlah transaksi sedang disewa pada tab Pengembalian. */
    private JLabel lblRingkasanPengembalian;
    /** Callback opsional yang dijalankan ketika admin melakukan logout. */
    private Runnable onLogout;

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

        this.modelBarang = new TemaUI.TabelModelReadOnly(new String[] {
            "ID", "Nama", "Kategori", "Tarif/Hari", "Biaya Tambahan", "Denda/Hari", "Status"
        });
        this.modelTransaksi = new TemaUI.TabelModelReadOnly(new String[] {
            "ID Transaksi", "Penyewa", "Daftar Barang", "Lama (Hari)",
            "Total Biaya", "Pembayaran", "Status"
        });
        this.modelPengembalian = new TemaUI.TabelModelReadOnly(new String[] {
            "ID Transaksi", "Penyewa", "Daftar Barang", "Tgl Sewa", "Jatuh Tempo", "Status"
        });

        this.tabelBarang = TemaUI.buatTabelStandar(this.modelBarang);
        this.tabelTransaksi = TemaUI.buatTabelStandar(this.modelTransaksi);
        this.tabelPengembalian = TemaUI.buatTabelStandar(this.modelPengembalian);

        TemaUI.aturKolomTabel(
            this.tabelBarang,
            new int[] {70, 190, 130, 120, 180, 115, 125},
            new int[] {3, 5},
            new int[] {6}
        );
        TemaUI.aturKolomTabel(
            this.tabelTransaksi,
            new int[] {95, 85, 240, 85, 120, 100, 185},
            new int[] {3, 4},
            new int[] {6}
        );
        TemaUI.aturKolomTabel(
            this.tabelPengembalian,
            new int[] {95, 100, 300, 110, 110, 140},
            new int[] {},
            new int[] {5}
        );

        this.tabbedPane = new JTabbedPane();
        this.txtTanggalKembali = new JTextField(LocalDate.now().toString(), 11);
        TemaUI.formatInputField(this.txtTanggalKembali);
        this.txtTanggalKembali.setToolTipText("Format: yyyy-MM-dd (contoh: 2026-11-28)");

        this.lblRingkasanBarang = TemaUI.buatLabelRingkasan("0 barang | 0 tersedia");
        this.lblRingkasanTransaksi = TemaUI.buatLabelRingkasan("0 transaksi menunggu");
        this.lblRingkasanPengembalian = TemaUI.buatLabelRingkasan("0 transaksi aktif");

        this.inisialisasiTampilan();
        this.refreshSemuaTabel();
    }

    /**
     * Memuat ulang seluruh data board game dari katalog admin ke dalam tabel barang
     * serta memperbarui ringkasan jumlah barang tersedia.
     */
    public void tampilkanBarang()
    {
        this.modelBarang.setRowCount(0);
        ArrayList<Barang> daftar = this.admin.kelolaBarang().getBarang();
        int jumlahTersedia = 0;

        for (Barang item : daftar) {
            String kategori = item.getClass().getSimpleName();
            String biayaTambahan = "-";

            if (item instanceof FamilyBoardGame) {
                FamilyBoardGame familyGame = (FamilyBoardGame) item;
                biayaTambahan = "Garansi: " + TemaUI.formatRupiah(familyGame.getBiayaGaransiPion());
            } else if (item instanceof StrategyGame) {
                StrategyGame strategyGame = (StrategyGame) item;
                biayaTambahan = "Modul/hari: " + TemaUI.formatRupiah(strategyGame.getBiayaModulHarian());
            }

            String status;
            if (item.isTersedia()) {
                status = "Tersedia";
                jumlahTersedia++;
            } else {
                status = "Sedang Disewa";
            }

            this.modelBarang.addRow(new Object[] {
                item.getId(),
                item.getNama(),
                kategori,
                TemaUI.formatRupiah(item.getTarifHarian()),
                biayaTambahan,
                TemaUI.formatRupiah(item.getDendaPerHari()),
                status
            });
        }

        this.lblRingkasanBarang.setText(
            daftar.size() + " barang | " + jumlahTersedia + " tersedia"
        );
    }

    /**
     * Memuat ulang daftar transaksi berstatus {@code "Menunggu Konfirmasi"} ke tabel transaksi.
     */
    public void tampilkanTransaksiMenunggu()
    {
        this.modelTransaksi.setRowCount(0);
        ArrayList<Transaksi> daftar = this.admin.lihatTransaksiMenunggu();

        for (Transaksi transaksi : daftar) {
            String metodePembayaran = transaksi.getPembayaran().getClass().getSimpleName();
            this.modelTransaksi.addRow(new Object[] {
                transaksi.getId(),
                transaksi.getPenyewa().getUsername(),
                TemaUI.formatDaftarBarang(transaksi.getBarang()),
                transaksi.getLamaSewa(),
                TemaUI.formatRupiah(transaksi.hitungTotal()),
                metodePembayaran,
                transaksi.getStatus()
            });
        }

        this.lblRingkasanTransaksi.setText(daftar.size() + " transaksi menunggu");
    }

    /**
     * Memuat ulang daftar transaksi berstatus {@code "Sedang Disewa"} ke tabel pengembalian.
     */
    public void tampilkanTransaksiPengembalian()
    {
        this.modelPengembalian.setRowCount(0);
        ArrayList<Transaksi> daftar = this.admin.getDaftarTransaksi();
        int jumlahAktif = 0;

        for (Transaksi transaksi : daftar) {
            if ("Sedang Disewa".equalsIgnoreCase(transaksi.getStatus())) {
                this.modelPengembalian.addRow(new Object[] {
                    transaksi.getId(),
                    transaksi.getPenyewa().getUsername(),
                    TemaUI.formatDaftarBarang(transaksi.getBarang()),
                    transaksi.getTanggalSewa().toString(),
                    transaksi.getTanggalJatuhTempo().toString(),
                    transaksi.getStatus()
                });
                jumlahAktif++;
            }
        }

        this.lblRingkasanPengembalian.setText(jumlahAktif + " transaksi aktif");
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
     * Memproses penerimaan transaksi yang sedang dipilih pada tab Transaksi Menunggu.
     *
     * <p>Jika salah satu barang pada transaksi sudah disewa penyewa lain,
     * {@link BarangTidakTersediaException} ditangkap dan pesan penolakan
     * ditampilkan melalui {@link JOptionPane}.</p>
     */
    public void aksiTerimaTransaksi()
    {
        int barisTerpilih = this.tabelTransaksi.getSelectedRow();
        if (barisTerpilih < 0) {
            JOptionPane.showMessageDialog(
                this,
                "Pilih satu transaksi yang menunggu konfirmasi terlebih dahulu.",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String idTransaksi = String.valueOf(this.modelTransaksi.getValueAt(barisTerpilih, 0));
        Transaksi transaksi = this.cariTransaksiById(idTransaksi);

        if (transaksi == null) {
            JOptionPane.showMessageDialog(
                this,
                "Data transaksi \"" + idTransaksi + "\" tidak ditemukan.",
                "Kesalahan",
                JOptionPane.ERROR_MESSAGE
            );
            this.refreshSemuaTabel();
            return;
        }

        try {
            this.admin.terimaTransaksi(transaksi);
            this.refreshSemuaTabel();
            JOptionPane.showMessageDialog(
                this,
                "Transaksi \"" + transaksi.getId()
                    + "\" berhasil diterima.\nStatus barang kini berubah menjadi Sedang Disewa.",
                "Transaksi Diterima",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (BarangTidakTersediaException exception) {
            this.refreshSemuaTabel();
            JOptionPane.showMessageDialog(
                this,
                exception.getMessage(),
                "Transaksi Ditolak",
                JOptionPane.ERROR_MESSAGE
            );
        } catch (IllegalArgumentException | IllegalStateException exception) {
            JOptionPane.showMessageDialog(
                this,
                exception.getMessage(),
                "Gagal Menerima Transaksi",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Memproses konfirmasi pengembalian untuk transaksi yang dipilih pada tab Pengembalian.
     */
    public void aksiKonfirmasiKembali()
    {
        int barisTerpilih = this.tabelPengembalian.getSelectedRow();
        if (barisTerpilih < 0) {
            JOptionPane.showMessageDialog(
                this,
                "Pilih satu transaksi yang sedang disewa terlebih dahulu.",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String idTransaksi = String.valueOf(this.modelPengembalian.getValueAt(barisTerpilih, 0));
        Transaksi transaksi = this.cariTransaksiById(idTransaksi);

        if (transaksi == null) {
            JOptionPane.showMessageDialog(
                this,
                "Data transaksi \"" + idTransaksi + "\" tidak ditemukan.",
                "Kesalahan",
                JOptionPane.ERROR_MESSAGE
            );
            this.refreshSemuaTabel();
            return;
        }

        try {
            String teksTanggal = this.txtTanggalKembali.getText().trim();
            LocalDate tanggalKembali = LocalDate.parse(teksTanggal);

            this.admin.konfirmasiKembali(transaksi, tanggalKembali);
            double denda = transaksi.hitungDenda();

            StringBuilder pesan = new StringBuilder();
            pesan.append("Pengembalian transaksi \"")
                 .append(transaksi.getId())
                 .append("\" berhasil dikonfirmasi.\n")
                 .append("Tanggal Kembali     : ")
                 .append(tanggalKembali)
                 .append("\nDenda Keterlambatan : ")
                 .append(TemaUI.formatRupiah(denda));

            if (denda > 0) {
                String infoPembayaranDenda = transaksi.getPembayaran().prosesPembayaran(denda);
                pesan.append("\n\nPembayaran Denda:\n").append(infoPembayaranDenda);
            } else {
                pesan.append("\n\nPengembalian tepat waktu (tidak ada denda).");
            }

            this.txtTanggalKembali.setText(LocalDate.now().toString());
            this.refreshSemuaTabel();

            JOptionPane.showMessageDialog(
                this,
                pesan.toString(),
                "Pengembalian Dikonfirmasi",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (DateTimeParseException exception) {
            JOptionPane.showMessageDialog(
                this,
                "Format tanggal kembali tidak valid. Gunakan format yyyy-MM-dd (contoh: 2026-11-28).",
                "Format Tanggal Salah",
                JOptionPane.ERROR_MESSAGE
            );
        } catch (IllegalArgumentException | IllegalStateException exception) {
            JOptionPane.showMessageDialog(
                this,
                exception.getMessage(),
                "Gagal Mengonfirmasi Pengembalian",
                JOptionPane.ERROR_MESSAGE
            );
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
     * Memperbarui seluruh data pada ketiga tabel admin sekaligus.
     */
    private void refreshSemuaTabel()
    {
        this.tampilkanBarang();
        this.tampilkanTransaksiMenunggu();
        this.tampilkanTransaksiPengembalian();
    }

    /**
     * Membangun tata letak utama jendela meliputi header, tiga tab utama, dan properti frame.
     */
    private void inisialisasiTampilan()
    {
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setMinimumSize(new Dimension(880, 540));
        this.setSize(980, 620);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(TemaUI.WARNA_CANVAS);

        JPanel header = TemaUI.buatPanelHeader(
            "BoardGameRent - Panel Admin",
            "Login sebagai: " + this.admin.getUsername(),
            this::refreshSemuaTabel,
            this::prosesLogout
        );
        this.add(header, BorderLayout.NORTH);

        this.tabbedPane.setFont(new Font(TemaUI.FONT_UI, Font.BOLD, 12));
        this.tabbedPane.setBackground(TemaUI.WARNA_CANVAS);
        this.tabbedPane.setForeground(TemaUI.WARNA_TEKS_UTAMA);
        this.tabbedPane.setBorder(BorderFactory.createEmptyBorder(12, 16, 16, 16));
        this.tabbedPane.addTab("  Kelola Barang  ", this.buatPanelKelolaBarang());
        this.tabbedPane.addTab("  Transaksi Menunggu  ", this.buatPanelTransaksiMenunggu());
        this.tabbedPane.addTab("  Pengembalian  ", this.buatPanelPengembalian());
        this.tabbedPane.addChangeListener(event -> this.refreshSemuaTabel());

        this.add(this.tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Membuat panel untuk tab Kelola Barang beserta tabel katalog dan tombol CRUD.
     *
     * @return panel tab Kelola Barang
     */
    private JPanel buatPanelKelolaBarang()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(TemaUI.WARNA_CANVAS);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        panel.add(TemaUI.buatScrollTabel(this.tabelBarang), BorderLayout.CENTER);

        JPanel panelBawah = new JPanel(new BorderLayout());
        panelBawah.setOpaque(false);
        panelBawah.add(this.lblRingkasanBarang, BorderLayout.WEST);

        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelTombol.setOpaque(false);

        JButton btnHapus = TemaUI.buatTombolBahaya("Hapus Barang");
        btnHapus.addActionListener(event -> this.prosesHapusBarang());

        JButton btnEdit = TemaUI.buatTombolSekunder("Edit Barang");
        btnEdit.addActionListener(event -> this.bukaDialogEditBarang());

        JButton btnTambah = TemaUI.buatTombolPrimer("Tambah Barang");
        btnTambah.addActionListener(event -> this.bukaDialogTambahBarang());

        panelTombol.add(btnHapus);
        panelTombol.add(btnEdit);
        panelTombol.add(btnTambah);

        panelBawah.add(panelTombol, BorderLayout.EAST);
        panel.add(panelBawah, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Membuat panel untuk tab Transaksi Menunggu beserta tabel dan tombol Terima.
     *
     * @return panel tab Transaksi Menunggu
     */
    private JPanel buatPanelTransaksiMenunggu()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(TemaUI.WARNA_CANVAS);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        panel.add(TemaUI.buatScrollTabel(this.tabelTransaksi), BorderLayout.CENTER);

        JPanel panelBawah = new JPanel(new BorderLayout());
        panelBawah.setOpaque(false);
        panelBawah.add(this.lblRingkasanTransaksi, BorderLayout.WEST);

        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelTombol.setOpaque(false);

        JButton btnTerima = TemaUI.buatTombolPrimer("Terima Transaksi");
        btnTerima.addActionListener(event -> this.aksiTerimaTransaksi());

        panelTombol.add(btnTerima);
        panelBawah.add(panelTombol, BorderLayout.EAST);
        panel.add(panelBawah, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Membuat panel untuk tab Pengembalian beserta tabel, input tanggal kembali,
     * dan tombol Konfirmasi.
     *
     * @return panel tab Pengembalian
     */
    private JPanel buatPanelPengembalian()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(TemaUI.WARNA_CANVAS);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        panel.add(TemaUI.buatScrollTabel(this.tabelPengembalian), BorderLayout.CENTER);

        JPanel panelBawah = new JPanel(new BorderLayout());
        panelBawah.setOpaque(false);
        panelBawah.add(this.lblRingkasanPengembalian, BorderLayout.WEST);

        JPanel panelAksi = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelAksi.setOpaque(false);

        JLabel lblTanggal = new JLabel("Tgl Kembali (yyyy-MM-dd):");
        lblTanggal.setFont(new Font(TemaUI.FONT_UI, Font.PLAIN, 12));
        lblTanggal.setForeground(TemaUI.WARNA_TEKS_UTAMA);
        lblTanggal.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));

        JButton btnKonfirmasi = TemaUI.buatTombolPrimer("Konfirmasi Pengembalian");
        btnKonfirmasi.addActionListener(event -> this.aksiKonfirmasiKembali());

        panelAksi.add(lblTanggal);
        panelAksi.add(this.txtTanggalKembali);
        panelAksi.add(btnKonfirmasi);

        panelBawah.add(panelAksi, BorderLayout.EAST);
        panel.add(panelBawah, BorderLayout.SOUTH);
        return panel;
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
            this.refreshSemuaTabel();
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
        gbc.insets = new Insets(5, 6, 5, 6);
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
            gbc.insets = new Insets(5, 6, 5, 6);
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
            this.refreshSemuaTabel();
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
            this.refreshSemuaTabel();
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
     * Mencari objek {@link Transaksi} di daftar transaksi admin berdasarkan ID transaksi.
     *
     * @param idTransaksi kode unik transaksi yang dicari
     * @return objek {@link Transaksi} jika ditemukan, atau {@code null} jika tidak ada
     */
    private Transaksi cariTransaksiById(String idTransaksi)
    {
        for (Transaksi transaksi : this.admin.getDaftarTransaksi()) {
            if (transaksi.getId().equalsIgnoreCase(idTransaksi)) {
                return transaksi;
            }
        }
        return null;
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
}
