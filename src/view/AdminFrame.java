package view;

import exception.BarangTidakTersediaException;
import exception.DataTidakDitemukanException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
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
 * Jendela ini menggunakan tema Cozy Pixel Tabletop dengan lima tab: Dashboard
 * (ringkasan KPI dan grafik real-time), Kelola Barang (Split View ber-thumbnail foto
 * dan kartu detail), Transaksi Menunggu, Pengembalian, dan Riwayat Transaksi.
 *
 * @author Muhammad Aqil Mubarak
 * @version 2026.11.27
 */
public class AdminFrame extends JFrame
{
    private static final long serialVersionUID = 1L;

    // Data inti dan navigasi utama
    private transient Admin admin;
    private transient Runnable onLogout;
    private transient Runnable onDataChanged;
    private transient JTabbedPane tabbedPane;

    // Tabel dan model data
    private transient JTable tabelBarang;
    private transient JTable tabelTransaksi;
    private transient JTable tabelPengembalian;
    private transient JTable tabelRiwayatAdmin;
    private transient DefaultTableModel modelBarang;
    private transient DefaultTableModel modelTransaksi;
    private transient DefaultTableModel modelPengembalian;
    private transient DefaultTableModel modelRiwayatAdmin;

    // Filter, pencarian, dan kontrol pengembalian
    private transient JTextField txtCariBarang;
    private transient JComboBox<String> cmbFilterKategori;
    private transient JComboBox<String> cmbUrutkan;
    private transient JComboBox<String> cmbFilterStatusRiwayat;
    private transient JTextField txtTanggalKembali;
    private transient JSpinner spnHariTelat;
    private transient boolean sedangSyncPengembalian;

    // Kartu detail board game terpilih
    private transient JLabel lblDetailFoto;
    private transient JLabel lblDetailKategori;
    private transient JLabel lblDetailId;
    private transient JLabel lblDetailNama;
    private transient JLabel lblDetailTarif;
    private transient JLabel lblDetailBiayaTambahan;
    private transient JLabel lblDetailDenda;
    private transient JLabel lblDetailStatus;

    // Ringkasan tiap tab
    private transient JLabel lblRingkasanBarang;
    private transient JLabel lblRingkasanTransaksi;
    private transient JLabel lblRingkasanPengembalian;
    private transient JLabel lblInfoKembali;
    private transient JLabel lblRingkasanRiwayatAdmin;

    // Kartu KPI dan grafik Dashboard
    private transient JLabel lblDashPendapatan;
    private transient JLabel lblDashPendapatanSub;
    private transient JLabel lblDashDenda;
    private transient JLabel lblDashDendaSub;
    private transient JLabel lblDashStok;
    private transient JLabel lblDashStokSub;
    private transient JLabel lblDashTransaksi;
    private transient JLabel lblDashTransaksiSub;
    private transient TemaUI.PanelGrafikBatang grafikKategori;
    private transient TemaUI.PanelGrafikBatang grafikStatusTransaksi;
    private transient TemaUI.PanelGrafikBatang grafikPopuler;

    /**
     * Membuat jendela AdminFrame baru tanpa aksi callback logout tambahan.
     *
     * @param admin objek Admin yang sedang login, tidak boleh null
     * @throws IllegalArgumentException jika admin bernilai null
     */
    public AdminFrame(Admin admin)
    {
        this(admin, null);
    }

    /**
     * Membuat jendela AdminFrame baru dengan callback opsional saat admin logout.
     *
     * @param admin objek Admin yang sedang login, tidak boleh null
     * @param onLogout aksi yang dijalankan setelah jendela ditutup melalui tombol Logout,
     *                 boleh null
     * @throws IllegalArgumentException jika admin bernilai null
     */
    public AdminFrame(Admin admin, Runnable onLogout)
    {
        this(admin, onLogout, null);
    }

    /**
     * Membuat jendela AdminFrame baru dengan callback logout dan callback
     * penyimpanan data persisten.
     *
     * @param admin objek Admin yang sedang login, tidak boleh null
     * @param onLogout aksi yang dijalankan setelah jendela ditutup melalui tombol Logout,
     *                 boleh null
     * @param onDataChanged callback yang dipanggil setiap kali data berubah, boleh null
     * @throws IllegalArgumentException jika admin bernilai null
     */
    public AdminFrame(Admin admin, Runnable onLogout, Runnable onDataChanged)
    {
        super("Rent the Board! - Panel Admin (BoardGameRent)");
        this.setIconImage(TemaUI.muatLogoPixel(64).getImage());

        if (admin == null) {
            throw new IllegalArgumentException("Objek admin tidak boleh null.");
        }

        this.admin = admin;
        this.onLogout = onLogout;
        this.onDataChanged = onDataChanged;

        this.modelBarang = new TemaUI.TabelModelReadOnly(new String[] {
            "Foto", "ID", "Nama Board Game", "Kategori", "Tarif/Hari", "Denda/Hari", "Status"
        });
        this.modelTransaksi = new TemaUI.TabelModelReadOnly(new String[] {
            "ID Transaksi", "Penyewa", "Daftar Barang", "Lama (Hari)",
            "Total Biaya", "Pembayaran", "Status"
        });
        this.modelPengembalian = new TemaUI.TabelModelReadOnly(new String[] {
            "ID Transaksi", "Penyewa", "Daftar Barang", "Tgl Sewa", "Jatuh Tempo", "Status"
        });
        this.modelRiwayatAdmin = new TemaUI.TabelModelReadOnly(new String[] {
            "ID Transaksi", "Penyewa", "Daftar Barang", "Lama", "Total Sewa",
            "Denda", "Pembayaran", "Status"
        });

        this.tabelBarang = TemaUI.buatTabelStandar(this.modelBarang, 56);
        this.tabelTransaksi = TemaUI.buatTabelStandar(this.modelTransaksi, 32);
        this.tabelPengembalian = TemaUI.buatTabelStandar(this.modelPengembalian, 32);
        this.tabelRiwayatAdmin = TemaUI.buatTabelStandar(this.modelRiwayatAdmin, 32);

        TemaUI.aturKolomTabel(
            this.tabelBarang,
            new int[] {62, 58, 160, 145, 105, 100, 115},
            new int[] {4, 5},
            new int[] {6},
            0
        );
        TemaUI.aturKolomTabel(
            this.tabelTransaksi,
            new int[] {95, 95, 290, 90, 130, 115, 175},
            new int[] {3, 4},
            new int[] {6}
        );
        TemaUI.aturKolomTabel(
            this.tabelPengembalian,
            new int[] {95, 105, 340, 115, 115, 145},
            new int[] {},
            new int[] {5}
        );
        TemaUI.aturKolomTabel(
            this.tabelRiwayatAdmin,
            new int[] {90, 85, 235, 55, 115, 95, 105, 155},
            new int[] {3, 4, 5},
            new int[] {7}
        );

        this.txtCariBarang = new JTextField(16);
        TemaUI.formatInputField(this.txtCariBarang);
        this.txtCariBarang.setToolTipText("Cari berdasarkan ID atau nama board game");
        this.txtCariBarang.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyReleased(KeyEvent event)
            {
                AdminFrame.this.tampilkanBarang();
            }
        });

        this.cmbFilterKategori = new JComboBox<>(new String[] {
            "Semua Kategori", "CardGame", "FamilyBoardGame", "StrategyGame"
        });
        this.cmbFilterKategori.setFont(new Font(TemaUI.FONT_UI, Font.PLAIN, 12));
        this.cmbFilterKategori.addActionListener(event -> this.tampilkanBarang());

        this.cmbUrutkan = new JComboBox<>(new String[] {
            "Urutan: Default", "Tarif Termurah", "Tarif Termahal", "Nama A-Z"
        });
        this.cmbUrutkan.setFont(new Font(TemaUI.FONT_UI, Font.PLAIN, 12));
        this.cmbUrutkan.addActionListener(event -> this.tampilkanBarang());

        this.cmbFilterStatusRiwayat = new JComboBox<>(new String[] {
            "Semua Status", "Menunggu Konfirmasi", "Sedang Disewa", "Selesai", "Ditolak"
        });
        this.cmbFilterStatusRiwayat.setFont(new Font(TemaUI.FONT_UI, Font.PLAIN, 12));
        this.cmbFilterStatusRiwayat.addActionListener(event -> this.tampilkanRiwayatTransaksi());

        this.txtTanggalKembali = new JTextField(LocalDate.now().toString(), 11);
        TemaUI.formatInputField(this.txtTanggalKembali);
        this.txtTanggalKembali.setToolTipText("Format: yyyy-MM-dd (contoh: 2026-11-28)");

        this.spnHariTelat = new JSpinner(new SpinnerNumberModel(0, 0, 365, 1));
        this.spnHariTelat.setFont(new Font(TemaUI.FONT_UI, Font.BOLD, 12));
        this.spnHariTelat.setPreferredSize(new Dimension(68, 30));
        this.spnHariTelat.setToolTipText(
            "Masukkan custom berapa hari keterlambatan untuk menghitung denda"
        );
        this.spnHariTelat.addChangeListener(event -> {
            if (!this.sedangSyncPengembalian) {
                this.terapkanHariTelatDariSpinner();
            }
        });

        this.lblDetailFoto = new JLabel("", SwingConstants.CENTER);
        this.lblDetailFoto.setPreferredSize(new Dimension(176, 176));
        this.lblDetailFoto.setOpaque(true);
        this.lblDetailFoto.setBackground(TemaUI.WARNA_SURFACE);
        this.lblDetailFoto.setBorder(TemaUI.buatBorderFotoPixel());

        this.lblDetailKategori = new JLabel("-");
        this.lblDetailId = new JLabel("-");
        this.lblDetailNama = new JLabel("Pilih board game");
        this.lblDetailTarif = new JLabel("-");
        this.lblDetailBiayaTambahan = new JLabel("-");
        this.lblDetailDenda = new JLabel("-");
        this.lblDetailStatus = new JLabel("-");

        this.lblRingkasanBarang = TemaUI.buatLabelRingkasan("0 barang | 0 tersedia");
        this.lblRingkasanTransaksi = TemaUI.buatLabelRingkasan("0 transaksi menunggu");
        this.lblRingkasanPengembalian = TemaUI.buatLabelRingkasan("0 transaksi aktif");
        this.lblRingkasanRiwayatAdmin = TemaUI.buatLabelRingkasan("0 transaksi tercatat");
        this.lblInfoKembali = TemaUI.buatLabelRingkasan(
            "Pilih transaksi aktif untuk melihat jatuh tempo dan estimasi denda."
        );

        this.lblDashPendapatan = new JLabel("Rp0,00");
        this.lblDashPendapatanSub = new JLabel("0 transaksi valid");
        this.lblDashDenda = new JLabel("Rp0,00");
        this.lblDashDendaSub = new JLabel("Dari transaksi selesai");
        this.lblDashStok = new JLabel("0 / 0");
        this.lblDashStokSub = new JLabel("Unit siap disewa");
        this.lblDashTransaksi = new JLabel("0 / 0");
        this.lblDashTransaksiSub = new JLabel("Menunggu / sedang disewa");
        this.grafikKategori = new TemaUI.PanelGrafikBatang(
            "Jumlah Board Game per Kategori", false
        );
        this.grafikStatusTransaksi = new TemaUI.PanelGrafikBatang(
            "Distribusi Status Transaksi", false
        );
        this.grafikPopuler = new TemaUI.PanelGrafikBatang(
            "Board Game Terpopuler", false
        );

        this.tabelBarang.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                this.perbaruiDetailBarang();
            }
        });
        this.tabelBarang.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent event)
            {
                if (event.getClickCount() == 2 && AdminFrame.this.tabelBarang.getSelectedRow() >= 0) {
                    AdminFrame.this.bukaDialogEditBarang();
                }
            }
        });
        this.tabelTransaksi.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent event)
            {
                if (event.getClickCount() == 2 && AdminFrame.this.tabelTransaksi.getSelectedRow() >= 0) {
                    AdminFrame.this.aksiLihatInvoiceDariTabel(
                        AdminFrame.this.tabelTransaksi, AdminFrame.this.modelTransaksi
                    );
                }
            }
        });
        this.tabelPengembalian.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                this.perbaruiInfoPengembalian();
            }
        });
        this.tabelPengembalian.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent event)
            {
                if (event.getClickCount() == 2
                        && AdminFrame.this.tabelPengembalian.getSelectedRow() >= 0) {
                    AdminFrame.this.aksiLihatInvoiceDariTabel(
                        AdminFrame.this.tabelPengembalian, AdminFrame.this.modelPengembalian
                    );
                }
            }
        });
        this.tabelRiwayatAdmin.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent event)
            {
                if (event.getClickCount() == 2
                        && AdminFrame.this.tabelRiwayatAdmin.getSelectedRow() >= 0) {
                    AdminFrame.this.aksiLihatInvoiceDariTabel(
                        AdminFrame.this.tabelRiwayatAdmin, AdminFrame.this.modelRiwayatAdmin
                    );
                }
            }
        });
        this.txtTanggalKembali.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent event)
            {
                AdminFrame.this.perbaruiInfoPengembalian();
            }

            @Override
            public void removeUpdate(DocumentEvent event)
            {
                AdminFrame.this.perbaruiInfoPengembalian();
            }

            @Override
            public void changedUpdate(DocumentEvent event)
            {
                AdminFrame.this.perbaruiInfoPengembalian();
            }
        });

        this.tabbedPane = new JTabbedPane();
        this.inisialisasiTampilan();
        this.refreshSemuaTabel();
    }

    /**
     * Memuat ulang seluruh data board game dari katalog admin ke dalam tabel barang
     * serta memperbarui ringkasan dan kartu detail showcase.
     */
    public void tampilkanBarang()
    {
        this.modelBarang.setRowCount(0);
        ArrayList<Barang> daftar = TemaUI.urutkanDaftarBarang(
            this.admin.kelolaBarang().getBarang(),
            String.valueOf(this.cmbUrutkan.getSelectedItem())
        );
        String kataKunci = this.txtCariBarang.getText().trim().toLowerCase();
        String filterKategori = String.valueOf(this.cmbFilterKategori.getSelectedItem());
        int jumlahTersedia = 0;

        for (Barang item : daftar) {
            String kategori = item.getClass().getSimpleName();
            if (item.isTersedia()) {
                jumlahTersedia++;
            }

            boolean cocokKategori = "Semua Kategori".equals(filterKategori)
                || kategori.equalsIgnoreCase(filterKategori);
            boolean cocokCari = kataKunci.isEmpty()
                || item.getId().toLowerCase().contains(kataKunci)
                || item.getNama().toLowerCase().contains(kataKunci);

            if (cocokKategori && cocokCari) {
                this.modelBarang.addRow(new Object[] {
                    TemaUI.muatGambarBarang(item, 44),
                    item.getId(),
                    item.getNama(),
                    kategori,
                    TemaUI.formatRupiah(item.getTarifHarian()),
                    TemaUI.formatRupiah(item.getDendaPerHari()),
                    item.isTersedia() ? "Tersedia" : "Sedang Disewa"
                });
            }
        }

        this.lblRingkasanBarang.setText(
            this.modelBarang.getRowCount() + " tampil dari "
                + daftar.size() + " barang | " + jumlahTersedia + " tersedia"
        );

        if (this.modelBarang.getRowCount() > 0 && this.tabelBarang.getSelectedRow() < 0) {
            this.tabelBarang.setRowSelectionInterval(0, 0);
        }
        this.perbaruiDetailBarang();
        this.perbaruiDashboard();
    }

    /**
     * Memuat ulang daftar transaksi berstatus "Menunggu Konfirmasi" ke tabel transaksi.
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
        if (this.tabbedPane.getTabCount() > 2) {
            this.tabbedPane.setTitleAt(
                2,
                daftar.isEmpty()
                    ? "  Transaksi Menunggu  "
                    : "  Transaksi Menunggu (" + daftar.size() + ")  "
            );
        }
        if (this.modelTransaksi.getRowCount() > 0 && this.tabelTransaksi.getSelectedRow() < 0) {
            this.tabelTransaksi.setRowSelectionInterval(0, 0);
        }
        this.perbaruiDashboard();
    }

    /**
     * Memuat ulang daftar transaksi berstatus "Sedang Disewa" ke tabel pengembalian.
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
        if (this.tabbedPane.getTabCount() > 3) {
            this.tabbedPane.setTitleAt(
                3,
                jumlahAktif == 0
                    ? "  Pengembalian  "
                    : "  Pengembalian (" + jumlahAktif + ")  "
            );
        }
        if (this.modelPengembalian.getRowCount() > 0
                && this.tabelPengembalian.getSelectedRow() < 0) {
            this.tabelPengembalian.setRowSelectionInterval(0, 0);
        }
        this.perbaruiInfoPengembalian();
        this.perbaruiDashboard();
    }

    /**
     * Membuka tab Kelola Barang dan menampilkan dialog pilihan aksi pengelolaan katalog
     * (Tambah, Edit, atau Hapus barang).
     */
    public void aksiKelolaBarang()
    {
        this.tabbedPane.setSelectedIndex(1);

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
     * Jika salah satu barang pada transaksi sudah disewa penyewa lain,
     * BarangTidakTersediaException ditangkap dan pesan penolakan
     * ditampilkan melalui JOptionPane.
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
            this.simpanPerubahan();
            this.refreshSemuaTabel();
            JOptionPane.showMessageDialog(
                this,
                "Transaksi \"" + transaksi.getId()
                    + "\" berhasil diterima.\nStatus barang kini berubah menjadi Sedang Disewa.",
                "Transaksi Diterima",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (BarangTidakTersediaException exception) {
            this.simpanPerubahan();
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
            this.simpanPerubahan();
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

            this.sedangSyncPengembalian = true;
            this.spnHariTelat.setValue(0);
            this.txtTanggalKembali.setText(LocalDate.now().toString());
            this.sedangSyncPengembalian = false;
            this.refreshSemuaTabel();

            String[] pilihan = {"Lihat Invoice", "Tutup"};
            int aksi = JOptionPane.showOptionDialog(
                this,
                pesan.toString(),
                "Pengembalian Dikonfirmasi",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                pilihan,
                pilihan[1]
            );
            if (aksi == 0) {
                TemaUI.tampilkanDialogInvoice(this, transaksi);
            }
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
     * Mengembalikan objek Admin yang terhubung dengan jendela ini.
     *
     * @return objek admin aktif
     */
    public Admin getAdmin()
    {
        return this.admin;
    }

    /**
     * Mengembalikan komponen JTable yang menampilkan daftar barang.
     *
     * @return tabel barang
     */
    public JTable getTabelBarang()
    {
        return this.tabelBarang;
    }

    /**
     * Mengembalikan komponen JTable yang menampilkan transaksi menunggu konfirmasi.
     *
     * @return tabel transaksi menunggu
     */
    public JTable getTabelTransaksi()
    {
        return this.tabelTransaksi;
    }

    /**
     * Mengembalikan komponen JTable yang menampilkan transaksi sedang disewa
     * pada tab Pengembalian.
     *
     * @return tabel transaksi pengembalian
     */
    public JTable getTabelPengembalian()
    {
        return this.tabelPengembalian;
    }

    /**
     * Memperbarui seluruh data pada Dashboard dan keempat tabel admin sekaligus.
     */
    private void refreshSemuaTabel()
    {
        this.tampilkanBarang();
        this.tampilkanTransaksiMenunggu();
        this.tampilkanTransaksiPengembalian();
        this.tampilkanRiwayatTransaksi();
    }

    /**
     * Memuat ulang seluruh riwayat transaksi admin sesuai filter status yang dipilih.
     */
    public void tampilkanRiwayatTransaksi()
    {
        this.modelRiwayatAdmin.setRowCount(0);
        ArrayList<Transaksi> daftar = this.admin.getDaftarTransaksi();
        String filterStatus = String.valueOf(this.cmbFilterStatusRiwayat.getSelectedItem());

        for (Transaksi transaksi : daftar) {
            if (!"Semua Status".equals(filterStatus)
                    && !transaksi.getStatus().equalsIgnoreCase(filterStatus)) {
                continue;
            }
            this.modelRiwayatAdmin.addRow(new Object[] {
                transaksi.getId(),
                transaksi.getPenyewa().getUsername(),
                TemaUI.formatDaftarBarang(transaksi.getBarang()),
                transaksi.getLamaSewa(),
                TemaUI.formatRupiah(transaksi.hitungTotal()),
                TemaUI.formatRupiah(transaksi.hitungDenda()),
                transaksi.getPembayaran().getClass().getSimpleName(),
                transaksi.getStatus()
            });
        }

        this.lblRingkasanRiwayatAdmin.setText(
            this.modelRiwayatAdmin.getRowCount() + " tampil dari "
                + daftar.size() + " transaksi tercatat"
        );
        if (this.tabbedPane.getTabCount() > 4) {
            this.tabbedPane.setTitleAt(
                4,
                daftar.isEmpty()
                    ? "  Riwayat Transaksi  "
                    : "  Riwayat Transaksi (" + daftar.size() + ")  "
            );
        }
        if (this.modelRiwayatAdmin.getRowCount() > 0
                && this.tabelRiwayatAdmin.getSelectedRow() < 0) {
            this.tabelRiwayatAdmin.setRowSelectionInterval(0, 0);
        }
    }

    /**
     * Menghitung dan memperbarui nilai kartu KPI serta kedua grafik Dashboard dari data terkini.
     */
    private void perbaruiDashboard()
    {
        ArrayList<Barang> semuaBarang = this.admin.kelolaBarang().getBarang();
        int card = 0;
        int family = 0;
        int strategy = 0;
        int tersedia = 0;

        for (Barang item : semuaBarang) {
            if (item.isTersedia()) {
                tersedia++;
            }
            if (item instanceof CardGame) {
                card++;
            } else if (item instanceof FamilyBoardGame) {
                family++;
            } else if (item instanceof StrategyGame) {
                strategy++;
            }
        }

        ArrayList<Transaksi> semuaTrx = this.admin.getDaftarTransaksi();
        double totalPendapatan = 0;
        double totalDenda = 0;
        int jumlahMenunggu = 0;
        int jumlahDisewa = 0;
        int jumlahSelesai = 0;
        int jumlahDitolak = 0;
        int jumlahValidPendapatan = 0;

        for (Transaksi trx : semuaTrx) {
            String status = trx.getStatus();
            if ("Menunggu Konfirmasi".equalsIgnoreCase(status)) {
                jumlahMenunggu++;
            } else if ("Sedang Disewa".equalsIgnoreCase(status)) {
                jumlahDisewa++;
                totalPendapatan += trx.hitungTotal();
                jumlahValidPendapatan++;
            } else if ("Selesai".equalsIgnoreCase(status)) {
                jumlahSelesai++;
                totalPendapatan += trx.hitungTotal();
                totalDenda += trx.hitungDenda();
                jumlahValidPendapatan++;
            } else if ("Ditolak".equalsIgnoreCase(status)) {
                jumlahDitolak++;
            }
        }

        this.lblDashPendapatan.setText(TemaUI.formatRupiah(totalPendapatan));
        this.lblDashPendapatanSub.setText(jumlahValidPendapatan + " transaksi disetujui/selesai");
        this.lblDashDenda.setText(TemaUI.formatRupiah(totalDenda));
        this.lblDashDendaSub.setText(jumlahSelesai + " transaksi sudah kembali");
        this.lblDashStok.setText(tersedia + " / " + semuaBarang.size());
        this.lblDashStokSub.setText((semuaBarang.size() - tersedia) + " sedang disewa");
        this.lblDashTransaksi.setText(jumlahMenunggu + " / " + jumlahDisewa);
        this.lblDashTransaksiSub.setText("Menunggu / sedang disewa");

        this.grafikKategori.setData(
            new String[] {"CardGame", "FamilyBoardGame", "StrategyGame"},
            new double[] {card, family, strategy}
        );
        this.grafikStatusTransaksi.setData(
            new String[] {"Menunggu", "Disewa", "Selesai", "Ditolak"},
            new double[] {jumlahMenunggu, jumlahDisewa, jumlahSelesai, jumlahDitolak}
        );

        Map<String, Integer> hitungPopuler = new LinkedHashMap<>();
        for (Transaksi trx : semuaTrx) {
            if ("Ditolak".equalsIgnoreCase(trx.getStatus())) {
                continue;
            }
            for (Barang item : trx.getBarang()) {
                String id = item.getId();
                hitungPopuler.put(id, hitungPopuler.getOrDefault(id, 0) + 1);
            }
        }
        ArrayList<Map.Entry<String, Integer>> daftarPopuler = new ArrayList<>(hitungPopuler.entrySet());
        daftarPopuler.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        int batas = Math.min(4, daftarPopuler.size());
        String[] labelPopuler = new String[batas];
        double[] nilaiPopuler = new double[batas];
        for (int i = 0; i < batas; i++) {
            labelPopuler[i] = daftarPopuler.get(i).getKey();
            nilaiPopuler[i] = daftarPopuler.get(i).getValue();
        }
        this.grafikPopuler.setData(labelPopuler, nilaiPopuler);
    }

    /**
     * Membangun tata letak utama jendela meliputi header, empat tab utama, dan properti frame.
     */
    private void inisialisasiTampilan()
    {
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setMinimumSize(new Dimension(1020, 640));
        this.setSize(1140, 700);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(TemaUI.WARNA_CANVAS);

        JPanel header = TemaUI.buatPanelHeader(
            "Rent the Board! - Panel Admin",
            "BoardGameRent | Login sebagai: " + this.admin.getUsername(),
            this::refreshSemuaTabel,
            this::prosesLogout
        );
        this.add(header, BorderLayout.NORTH);

        this.tabbedPane.setFont(new Font(TemaUI.FONT_UI, Font.BOLD, 12));
        this.tabbedPane.setBackground(TemaUI.WARNA_CANVAS);
        this.tabbedPane.setForeground(TemaUI.WARNA_TEKS_UTAMA);
        this.tabbedPane.setBorder(BorderFactory.createEmptyBorder(12, 16, 16, 16));
        this.tabbedPane.addTab("  Dashboard  ", this.buatPanelDashboard());
        this.tabbedPane.addTab("  Kelola Barang  ", this.buatPanelKelolaBarang());
        this.tabbedPane.addTab("  Transaksi Menunggu  ", this.buatPanelTransaksiMenunggu());
        this.tabbedPane.addTab("  Pengembalian  ", this.buatPanelPengembalian());
        this.tabbedPane.addTab("  Riwayat Transaksi  ", this.buatPanelRiwayatTransaksi());
        this.tabbedPane.addChangeListener(event -> this.refreshSemuaTabel());

        this.add(this.tabbedPane, BorderLayout.CENTER);
        this.add(TemaUI.buatStatusBar("Administrator"), BorderLayout.SOUTH);
    }

    /**
     * Membuat tab Dashboard ringkasan KPI, dua grafik analitik, dan navigasi cepat.
     *
     * @return panel tab Dashboard
     */
    private JPanel buatPanelDashboard()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(TemaUI.WARNA_CANVAS);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JPanel barisKpi = new JPanel(new GridLayout(1, 4, 14, 0));
        barisKpi.setOpaque(false);
        barisKpi.setPreferredSize(new Dimension(0, 108));
        barisKpi.add(TemaUI.buatKartuStatistik(
            "TOTAL PENDAPATAN SEWA", this.lblDashPendapatan, this.lblDashPendapatanSub
        ));
        barisKpi.add(TemaUI.buatKartuStatistik(
            "TOTAL DENDA TERKUMPUL", this.lblDashDenda, this.lblDashDendaSub
        ));
        barisKpi.add(TemaUI.buatKartuStatistik(
            "KETERSEDIAAN STOK", this.lblDashStok, this.lblDashStokSub
        ));
        barisKpi.add(TemaUI.buatKartuStatistik(
            "ANTREAN & SEWA AKTIF", this.lblDashTransaksi, this.lblDashTransaksiSub
        ));

        JPanel panelGrafik = new JPanel(new GridLayout(1, 3, 14, 0));
        panelGrafik.setOpaque(false);
        panelGrafik.add(this.grafikKategori);
        panelGrafik.add(this.grafikStatusTransaksi);
        panelGrafik.add(this.grafikPopuler);

        JPanel bilahNavigasiCepat = new JPanel(new BorderLayout());
        bilahNavigasiCepat.setBackground(TemaUI.WARNA_SURFACE);
        bilahNavigasiCepat.setBorder(TemaUI.buatBorderKartuPixel(12, 16, 12, 16));

        JLabel lblAksi = new JLabel("Navigasi Cepat Operasional Rental");
        lblAksi.setFont(new Font(TemaUI.FONT_UI, Font.BOLD, 13));
        lblAksi.setForeground(TemaUI.WARNA_TEKS_UTAMA);

        JPanel tombolCepat = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tombolCepat.setOpaque(false);

        JButton btnKeKatalog = TemaUI.buatTombolSekunder("Kelola Barang");
        btnKeKatalog.addActionListener(event -> this.tabbedPane.setSelectedIndex(1));

        JButton btnKeMenunggu = TemaUI.buatTombolPrimer("Tinjau Transaksi Menunggu");
        btnKeMenunggu.addActionListener(event -> this.tabbedPane.setSelectedIndex(2));

        JButton btnKePengembalian = TemaUI.buatTombolSekunder("Proses Pengembalian");
        btnKePengembalian.addActionListener(event -> this.tabbedPane.setSelectedIndex(3));

        tombolCepat.add(btnKeKatalog);
        tombolCepat.add(btnKePengembalian);
        tombolCepat.add(btnKeMenunggu);

        bilahNavigasiCepat.add(lblAksi, BorderLayout.WEST);
        bilahNavigasiCepat.add(tombolCepat, BorderLayout.EAST);

        panel.add(barisKpi, BorderLayout.NORTH);
        panel.add(panelGrafik, BorderLayout.CENTER);
        panel.add(bilahNavigasiCepat, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Membuat panel tab Kelola Barang dengan filter, tabel ber-thumbnail di kiri,
     * dan kartu detail preview di kanan beserta tombol CRUD.
     *
     * @return panel tab Kelola Barang
     */
    private JPanel buatPanelKelolaBarang()
    {
        JPanel panel = new JPanel(new BorderLayout(16, 12));
        panel.setBackground(TemaUI.WARNA_CANVAS);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JPanel bilahFilter = new JPanel(new BorderLayout(12, 0));
        bilahFilter.setOpaque(false);

        JPanel filterKiri = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterKiri.setOpaque(false);

        JLabel lblCari = new JLabel("Cari:");
        lblCari.setFont(new Font(TemaUI.FONT_UI, Font.BOLD, 12));
        lblCari.setForeground(TemaUI.WARNA_TEKS_UTAMA);

        JLabel lblKategori = new JLabel("Kategori:");
        lblKategori.setFont(new Font(TemaUI.FONT_UI, Font.BOLD, 12));
        lblKategori.setForeground(TemaUI.WARNA_TEKS_UTAMA);

        JButton btnResetFilter = TemaUI.buatTombolSekunder("Reset");
        btnResetFilter.addActionListener(event -> {
            this.txtCariBarang.setText("");
            this.cmbFilterKategori.setSelectedIndex(0);
            this.cmbUrutkan.setSelectedIndex(0);
            this.tampilkanBarang();
        });

        filterKiri.add(lblCari);
        filterKiri.add(this.txtCariBarang);
        filterKiri.add(lblKategori);
        filterKiri.add(this.cmbFilterKategori);
        filterKiri.add(this.cmbUrutkan);
        filterKiri.add(btnResetFilter);

        bilahFilter.add(filterKiri, BorderLayout.WEST);
        bilahFilter.add(this.lblRingkasanBarang, BorderLayout.EAST);

        JPanel panelBawah = new JPanel(new BorderLayout());
        panelBawah.setOpaque(false);

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

        panel.add(bilahFilter, BorderLayout.NORTH);
        panel.add(TemaUI.buatScrollTabel(this.tabelBarang), BorderLayout.CENTER);
        panel.add(this.buatKartuDetailBarang(), BorderLayout.EAST);
        panel.add(panelBawah, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Membuat kartu preview detail board game terpilih di sebelah kanan tab Kelola Barang.
     *
     * @return panel kartu detail board game
     */
    private JPanel buatKartuDetailBarang()
    {
        JPanel kartu = new JPanel(new BorderLayout(0, 12));
        kartu.setPreferredSize(new Dimension(310, 0));
        kartu.setBackground(TemaUI.WARNA_SURFACE);
        kartu.setBorder(TemaUI.buatBorderKartuPixel(16, 16, 16, 16));

        JPanel bagianAtas = new JPanel();
        bagianAtas.setOpaque(false);
        bagianAtas.setLayout(new BoxLayout(bagianAtas, BoxLayout.Y_AXIS));

        JPanel wadahFoto = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wadahFoto.setOpaque(false);
        wadahFoto.add(this.lblDetailFoto);

        JPanel barisBadge = new JPanel(new BorderLayout());
        barisBadge.setOpaque(false);
        this.lblDetailId.setFont(new Font(TemaUI.FONT_MONO, Font.BOLD, 12));
        this.lblDetailId.setForeground(TemaUI.WARNA_TEKS_SEKUNDER);
        barisBadge.add(this.lblDetailKategori, BorderLayout.WEST);
        barisBadge.add(this.lblDetailId, BorderLayout.EAST);

        this.lblDetailNama.setFont(new Font(TemaUI.FONT_UI, Font.BOLD, 16));
        this.lblDetailNama.setForeground(TemaUI.WARNA_TEKS_UTAMA);

        bagianAtas.add(wadahFoto);
        bagianAtas.add(Box.createVerticalStrut(12));
        bagianAtas.add(barisBadge);
        bagianAtas.add(Box.createVerticalStrut(8));

        JPanel bungkusNama = new JPanel(new BorderLayout());
        bungkusNama.setOpaque(false);
        bungkusNama.add(this.lblDetailNama, BorderLayout.WEST);
        bagianAtas.add(bungkusNama);

        JPanel kotakInfo = new JPanel(new GridLayout(4, 2, 6, 8));
        kotakInfo.setBackground(TemaUI.WARNA_KARTU_KREM);
        kotakInfo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(TemaUI.WARNA_BORDER, 2),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        kotakInfo.add(this.buatLabelKeterangan("Tarif / Hari"));
        kotakInfo.add(this.formatLabelNilai(this.lblDetailTarif));
        kotakInfo.add(this.buatLabelKeterangan("Biaya Tambahan"));
        kotakInfo.add(this.formatLabelNilai(this.lblDetailBiayaTambahan));
        kotakInfo.add(this.buatLabelKeterangan("Denda / Hari"));
        kotakInfo.add(this.formatLabelNilai(this.lblDetailDenda));
        kotakInfo.add(this.buatLabelKeterangan("Status"));
        kotakInfo.add(this.formatLabelNilai(this.lblDetailStatus));

        kartu.add(bagianAtas, BorderLayout.NORTH);
        kartu.add(kotakInfo, BorderLayout.CENTER);
        return kartu;
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

        JButton btnInvoice = TemaUI.buatTombolSekunder("Lihat Invoice");
        btnInvoice.addActionListener(event -> this.aksiLihatInvoiceDariTabel(
            this.tabelTransaksi, this.modelTransaksi
        ));

        JButton btnTerima = TemaUI.buatTombolPrimer("Terima Transaksi");
        btnTerima.addActionListener(event -> this.aksiTerimaTransaksi());

        panelTombol.add(btnInvoice);
        panelTombol.add(btnTerima);
        panelBawah.add(panelTombol, BorderLayout.EAST);
        panel.add(panelBawah, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Membuat panel untuk tab Pengembalian beserta tabel, pratinjau jatuh tempo dan denda,
     * tombol preset tanggal kembali, dan tombol Konfirmasi.
     *
     * @return panel tab Pengembalian
     */
    private JPanel buatPanelPengembalian()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(TemaUI.WARNA_CANVAS);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        panel.add(TemaUI.buatScrollTabel(this.tabelPengembalian), BorderLayout.CENTER);

        JPanel kartuBawah = new JPanel(new BorderLayout(0, 10));
        kartuBawah.setBackground(TemaUI.WARNA_SURFACE);
        kartuBawah.setBorder(TemaUI.buatBorderKartuPixel(12, 16, 12, 16));

        JPanel barisInfo = new JPanel(new BorderLayout(12, 0));
        barisInfo.setOpaque(false);
        this.lblInfoKembali.setFont(new Font(TemaUI.FONT_UI, Font.BOLD, 12));
        barisInfo.add(this.lblRingkasanPengembalian, BorderLayout.WEST);
        barisInfo.add(this.lblInfoKembali, BorderLayout.EAST);

        JPanel barisKontrol = new JPanel(new BorderLayout(12, 0));
        barisKontrol.setOpaque(false);

        JPanel panelPreset = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panelPreset.setOpaque(false);

        JButton btnHariIni = TemaUI.buatTombolSekunder("Hari Ini");
        btnHariIni.addActionListener(event ->
            this.txtTanggalKembali.setText(LocalDate.now().toString())
        );

        JButton btnTepatTempo = TemaUI.buatTombolSekunder("Tepat Tempo");
        btnTepatTempo.addActionListener(event -> this.isiTanggalKembaliTerpilih(0));

        JLabel lblTelat = new JLabel("Telat (hari):");
        lblTelat.setFont(new Font(TemaUI.FONT_UI, Font.BOLD, 12));
        lblTelat.setForeground(TemaUI.WARNA_TEKS_UTAMA);

        panelPreset.add(btnHariIni);
        panelPreset.add(btnTepatTempo);
        panelPreset.add(lblTelat);
        panelPreset.add(this.spnHariTelat);

        JPanel panelAksi = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelAksi.setOpaque(false);

        JLabel lblTanggal = new JLabel("Tgl Kembali:");
        lblTanggal.setFont(new Font(TemaUI.FONT_UI, Font.BOLD, 12));
        lblTanggal.setForeground(TemaUI.WARNA_TEKS_UTAMA);

        JButton btnInvoice = TemaUI.buatTombolSekunder("Lihat Invoice");
        btnInvoice.addActionListener(event -> this.aksiLihatInvoiceDariTabel(
            this.tabelPengembalian, this.modelPengembalian
        ));

        JButton btnKonfirmasi = TemaUI.buatTombolPrimer("Konfirmasi Pengembalian");
        btnKonfirmasi.addActionListener(event -> this.aksiKonfirmasiKembali());

        panelAksi.add(lblTanggal);
        panelAksi.add(this.txtTanggalKembali);
        panelAksi.add(btnInvoice);
        panelAksi.add(btnKonfirmasi);

        barisKontrol.add(panelPreset, BorderLayout.WEST);
        barisKontrol.add(panelAksi, BorderLayout.EAST);

        kartuBawah.add(barisInfo, BorderLayout.NORTH);
        kartuBawah.add(barisKontrol, BorderLayout.SOUTH);

        panel.add(kartuBawah, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Membuat panel untuk tab Riwayat Transaksi admin beserta filter status dan tombol Lihat Invoice.
     *
     * @return panel tab Riwayat Transaksi
     */
    private JPanel buatPanelRiwayatTransaksi()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(TemaUI.WARNA_CANVAS);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JPanel bilahFilter = new JPanel(new BorderLayout(12, 0));
        bilahFilter.setOpaque(false);

        JPanel kiri = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        kiri.setOpaque(false);

        JLabel lblFilter = new JLabel("Filter Status:");
        lblFilter.setFont(new Font(TemaUI.FONT_UI, Font.BOLD, 12));
        lblFilter.setForeground(TemaUI.WARNA_TEKS_UTAMA);

        kiri.add(lblFilter);
        kiri.add(this.cmbFilterStatusRiwayat);

        bilahFilter.add(kiri, BorderLayout.WEST);
        bilahFilter.add(this.lblRingkasanRiwayatAdmin, BorderLayout.EAST);

        panel.add(bilahFilter, BorderLayout.NORTH);
        panel.add(TemaUI.buatScrollTabel(this.tabelRiwayatAdmin), BorderLayout.CENTER);

        JPanel panelBawah = new JPanel(new BorderLayout());
        panelBawah.setOpaque(false);

        JLabel lblTips = TemaUI.buatLabelRingkasan(
            "Klik ganda baris atau tekan Lihat Invoice untuk mencetak struk transaksi."
        );
        panelBawah.add(lblTips, BorderLayout.WEST);

        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelTombol.setOpaque(false);

        JButton btnExport = TemaUI.buatTombolSekunder("Export Laporan (.txt)");
        btnExport.addActionListener(event -> this.aksiExportLaporan());

        JButton btnInvoice = TemaUI.buatTombolPrimer("Lihat Invoice");
        btnInvoice.addActionListener(event -> this.aksiLihatInvoiceDariTabel(
            this.tabelRiwayatAdmin, this.modelRiwayatAdmin
        ));

        panelTombol.add(btnExport);
        panelTombol.add(btnInvoice);
        panelBawah.add(panelTombol, BorderLayout.EAST);

        panel.add(panelBawah, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Menyimpan laporan rekap seluruh transaksi, pendapatan sewa, dan total denda ke file teks.
     */
    private void aksiExportLaporan()
    {
        ArrayList<Transaksi> daftar = this.admin.getDaftarTransaksi();
        if (daftar.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Belum ada data transaksi untuk diekspor.",
                "Laporan Kosong",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        File dirKerja = new File(System.getProperty("user.dir"));
        JFileChooser chooser = new JFileChooser(dirKerja);
        chooser.setDialogTitle("Simpan Laporan Transaksi Rent the Board!");
        chooser.setSelectedFile(new File(dirKerja, "Laporan_BoardGameRent_" + LocalDate.now() + ".txt"));
        chooser.setFileFilter(new FileNameExtensionFilter("Teks (*.txt)", "txt"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File target = chooser.getSelectedFile();
        if (!target.getName().toLowerCase().endsWith(".txt")) {
            target = new File(target.getParentFile(), target.getName() + ".txt");
        }

        StringBuilder isi = new StringBuilder();
        isi.append("======== LAPORAN TRANSAKSI RENT THE BOARD! (BOARDGAMERENT) ========\n");
        isi.append("Tanggal Cetak : ").append(LocalDate.now()).append("\n");
        isi.append("Admin         : ").append(this.admin.getUsername()).append("\n");
        isi.append("------------------------------------------------------------------\n");

        double totalPendapatan = 0.0;
        double totalDenda = 0.0;
        for (Transaksi trx : daftar) {
            double sewa = trx.hitungTotal();
            double denda = trx.hitungDenda();
            if ("Sedang Disewa".equalsIgnoreCase(trx.getStatus())
                    || "Selesai".equalsIgnoreCase(trx.getStatus())) {
                totalPendapatan += sewa;
                totalDenda += denda;
            }
            isi.append(trx.getId())
               .append(" | ").append(trx.getPenyewa().getUsername())
               .append(" | ").append(TemaUI.formatDaftarBarang(trx.getBarang()))
               .append(" | ").append(trx.getLamaSewa()).append(" hari")
               .append(" | Sewa: ").append(TemaUI.formatRupiah(sewa))
               .append(" | Denda: ").append(TemaUI.formatRupiah(denda))
               .append(" | ").append(trx.getStatus())
               .append("\n");
        }

        isi.append("------------------------------------------------------------------\n");
        isi.append("Total Transaksi       : ").append(daftar.size()).append("\n");
        isi.append("Total Pendapatan Sewa : ").append(TemaUI.formatRupiah(totalPendapatan)).append("\n");
        isi.append("Total Denda Terkumpul : ").append(TemaUI.formatRupiah(totalDenda)).append("\n");
        isi.append("Total Keseluruhan     : ")
           .append(TemaUI.formatRupiah(totalPendapatan + totalDenda)).append("\n");
        isi.append("==================================================================\n");

        try {
            Files.writeString(target.toPath(), isi.toString(), StandardCharsets.UTF_8);
            JOptionPane.showMessageDialog(
                this,
                "Laporan berhasil disimpan ke:\n" + target.getAbsolutePath(),
                "Export Berhasil",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (IOException ioException) {
            JOptionPane.showMessageDialog(
                this,
                "Gagal menyimpan laporan (" + ioException.getClass().getSimpleName()
                    + "): " + ioException.getMessage()
                    + "\nCoba simpan ke folder proyek atau folder lain yang tidak dibatasi.",
                "Kesalahan",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Memperbarui kartu detail foto dan informasi barang di tab Kelola Barang.
     */
    private void perbaruiDetailBarang()
    {
        int baris = this.tabelBarang.getSelectedRow();
        if (baris < 0 || baris >= this.modelBarang.getRowCount()) {
            this.lblDetailFoto.setIcon(null);
            this.lblDetailKategori.setText("-");
            this.lblDetailId.setText("-");
            this.lblDetailNama.setText("Pilih board game");
            this.lblDetailTarif.setText("-");
            this.lblDetailBiayaTambahan.setText("-");
            this.lblDetailDenda.setText("-");
            this.lblDetailStatus.setText("-");
            return;
        }

        String idBarang = String.valueOf(this.modelBarang.getValueAt(baris, 1));
        try {
            Barang barang = this.admin.kelolaBarang().cariBarang(idBarang);
            this.lblDetailFoto.setIcon(TemaUI.muatGambarBarang(barang, 168));
            TemaUI.terapkanGayaBadgeKategori(
                this.lblDetailKategori, barang.getClass().getSimpleName()
            );
            this.lblDetailId.setText(barang.getId());
            this.lblDetailNama.setText(barang.getNama());
            this.lblDetailTarif.setText(TemaUI.formatRupiah(barang.getTarifHarian()));
            this.lblDetailBiayaTambahan.setText(TemaUI.formatBiayaTambahan(barang));
            this.lblDetailDenda.setText(TemaUI.formatRupiah(barang.getDendaPerHari()));
            this.lblDetailStatus.setText(barang.isTersedia() ? "Tersedia" : "Sedang Disewa");
            this.lblDetailStatus.setForeground(
                barang.isTersedia() ? TemaUI.WARNA_STATUS_AMAN : TemaUI.WARNA_STATUS_BAHAYA
            );
        } catch (DataTidakDitemukanException ignored) {
            this.lblDetailFoto.setIcon(null);
        }
    }

    /**
     * Membuat label keterangan kolom kiri pada kotak detail barang.
     *
     * @param teks teks keterangan
     * @return objek JLabel terformat
     */
    private JLabel buatLabelKeterangan(String teks)
    {
        JLabel label = new JLabel(teks);
        label.setFont(new Font(TemaUI.FONT_UI, Font.PLAIN, 12));
        label.setForeground(TemaUI.WARNA_TEKS_SEKUNDER);
        return label;
    }

    /**
     * Memformat label nilai kolom kanan pada kotak detail barang.
     *
     * @param label label target
     * @return label rata kanan dan tebal
     */
    private JLabel formatLabelNilai(JLabel label)
    {
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setFont(new Font(TemaUI.FONT_UI, Font.BOLD, 12));
        label.setForeground(TemaUI.WARNA_TEKS_UTAMA);
        return label;
    }

    /**
     * Membuka dialog form untuk menambahkan board game baru sesuai kategori yang dipilih
     * beserta opsi pengunggahan foto board game.
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

        File[] fotoTerpilih = new File[1];
        JLabel lblPratinjauFoto = new JLabel("Auto", SwingConstants.CENTER);
        lblPratinjauFoto.setPreferredSize(new Dimension(48, 48));
        lblPratinjauFoto.setOpaque(true);
        lblPratinjauFoto.setBackground(TemaUI.WARNA_KARTU_KREM);
        lblPratinjauFoto.setBorder(BorderFactory.createLineBorder(TemaUI.WARNA_BORDER, 2));
        JLabel lblNamaFoto = new JLabel("Ikon pixel otomatis");
        lblNamaFoto.setFont(new Font(TemaUI.FONT_UI, Font.PLAIN, 11));
        lblNamaFoto.setForeground(TemaUI.WARNA_TEKS_SEKUNDER);

        JButton btnPilihFoto = TemaUI.buatTombolSekunder("Pilih Foto...");
        btnPilihFoto.addActionListener(event ->
            this.pilihFileFoto(fotoTerpilih, lblPratinjauFoto, lblNamaFoto)
        );

        JPanel panelFoto = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelFoto.setOpaque(false);
        panelFoto.add(lblPratinjauFoto);
        panelFoto.add(btnPilihFoto);
        panelFoto.add(lblNamaFoto);

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
            cmbKategori, txtId, txtNama, txtTarif, txtDenda,
            lblBiayaTambahan, txtBiayaTambahan, panelFoto
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
            if (fotoTerpilih[0] != null) {
                try {
                    TemaUI.simpanFotoBarang(barangBaru.getId(), fotoTerpilih[0]);
                } catch (IOException ioException) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Barang ditambahkan, tetapi foto gagal disimpan: " + ioException.getMessage(),
                        "Peringatan Foto",
                        JOptionPane.WARNING_MESSAGE
                    );
                }
            }

            this.refreshSemuaTabel();
            this.pilihBarisBarangById(barangBaru.getId());
            this.simpanPerubahan();
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
     * Menyusun panel form input penambahan barang menggunakan GridBagLayout.
     *
     * @param cmbKategori pemilih kategori barang
     * @param txtId input ID barang
     * @param txtNama input nama barang
     * @param txtTarif input tarif harian
     * @param txtDenda input denda per hari
     * @param lblBiayaTambahan label dinamis biaya tambahan
     * @param txtBiayaTambahan input biaya tambahan
     * @param panelFoto panel pemilih foto opsional board game
     * @return panel form siap ditampilkan di dialog
     */
    private JPanel buatPanelFormBarang(JComboBox<String> cmbKategori, JTextField txtId,
                                       JTextField txtNama, JTextField txtTarif,
                                       JTextField txtDenda, JLabel lblBiayaTambahan,
                                       JTextField txtBiayaTambahan, JPanel panelFoto)
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
            {lblBiayaTambahan, txtBiayaTambahan},
            {new JLabel("Foto Board Game:"), panelFoto}
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
            } else if (baris[index][1] instanceof JPanel) {
                form.add((JPanel) baris[index][1], gbc);
            } else {
                form.add((JTextField) baris[index][1], gbc);
            }
        }

        return form;
    }

    /**
     * Membuka dialog untuk mengubah nama, tarif harian, atau foto board game yang dipilih.
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

        String idBarang = String.valueOf(this.modelBarang.getValueAt(barisTerpilih, 1));

        try {
            Barang barang = this.admin.kelolaBarang().cariBarang(idBarang);

            JTextField txtNama = new JTextField(barang.getNama(), 18);
            JTextField txtTarif = new JTextField(String.valueOf((long) barang.getTarifHarian()), 18);

            File[] fotoTerpilih = new File[1];
            JLabel lblPratinjauFoto = new JLabel(TemaUI.muatGambarBarang(barang, 44));
            lblPratinjauFoto.setPreferredSize(new Dimension(48, 48));
            lblPratinjauFoto.setBorder(BorderFactory.createLineBorder(TemaUI.WARNA_BORDER, 2));
            JLabel lblNamaFoto = new JLabel("Foto saat ini");
            lblNamaFoto.setFont(new Font(TemaUI.FONT_UI, Font.PLAIN, 11));
            lblNamaFoto.setForeground(TemaUI.WARNA_TEKS_SEKUNDER);

            JButton btnGantiFoto = TemaUI.buatTombolSekunder("Ganti Foto...");
            btnGantiFoto.addActionListener(event ->
                this.pilihFileFoto(fotoTerpilih, lblPratinjauFoto, lblNamaFoto)
            );

            JPanel panelFoto = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            panelFoto.setOpaque(false);
            panelFoto.add(lblPratinjauFoto);
            panelFoto.add(btnGantiFoto);
            panelFoto.add(lblNamaFoto);

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

            gbc.gridx = 0;
            gbc.gridy = 3;
            form.add(new JLabel("Foto Board Game:"), gbc);
            gbc.gridx = 1;
            form.add(panelFoto, gbc);

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
            if (fotoTerpilih[0] != null) {
                try {
                    TemaUI.simpanFotoBarang(barang.getId(), fotoTerpilih[0]);
                } catch (IOException ioException) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Data diubah, tetapi foto baru gagal disimpan: " + ioException.getMessage(),
                        "Peringatan Foto",
                        JOptionPane.WARNING_MESSAGE
                    );
                }
            }

            this.refreshSemuaTabel();
            this.pilihBarisBarangById(barang.getId());
            this.simpanPerubahan();
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

        String idBarang = String.valueOf(this.modelBarang.getValueAt(barisTerpilih, 1));
        String namaBarang = String.valueOf(this.modelBarang.getValueAt(barisTerpilih, 2));

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
            this.simpanPerubahan();
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
     * Memilih file gambar PNG/JPG menggunakan JFileChooser dan memperbarui pratinjau dialog.
     *
     * @param targetFile array satu elemen penampung file terpilih
     * @param lblPratinjau label penampil ikon pratinjau ukuran kecil
     * @param lblNamaFile label penampil nama file terpilih
     */
    private void pilihFileFoto(File[] targetFile, JLabel lblPratinjau, JLabel lblNamaFile)
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Pilih Foto Board Game");
        chooser.setFileFilter(new FileNameExtensionFilter(
            "Gambar PNG / JPG (*.png, *.jpg, *.jpeg)", "png", "jpg", "jpeg"
        ));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        ImageIcon ikon = TemaUI.muatPratinjauFile(file, 44);
        if (ikon == null) {
            JOptionPane.showMessageDialog(
                this,
                "File tidak dapat dibaca sebagai gambar PNG atau JPG.",
                "Gambar Tidak Valid",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        targetFile[0] = file;
        lblPratinjau.setText("");
        lblPratinjau.setIcon(ikon);
        String nama = file.getName();
        lblNamaFile.setText(nama.length() > 18 ? nama.substring(0, 15) + "..." : nama);
    }

    /**
     * Memilih baris board game pada tabel berdasarkan ID barang.
     *
     * @param idBarang kode unik barang yang dicari
     */
    private void pilihBarisBarangById(String idBarang)
    {
        for (int row = 0; row < this.modelBarang.getRowCount(); row++) {
            if (idBarang.equalsIgnoreCase(String.valueOf(this.modelBarang.getValueAt(row, 1)))) {
                this.tabelBarang.setRowSelectionInterval(row, row);
                this.perbaruiDetailBarang();
                return;
            }
        }
    }

    /**
     * Membuka dialog struk Invoice untuk transaksi yang sedang dipilih pada tabel.
     *
     * @param tabel tabel transaksi aktif
     * @param model model data tabel terkait
     */
    private void aksiLihatInvoiceDariTabel(JTable tabel, DefaultTableModel model)
    {
        int barisTerpilih = tabel.getSelectedRow();
        if (barisTerpilih < 0) {
            JOptionPane.showMessageDialog(
                this,
                "Pilih satu transaksi pada tabel terlebih dahulu.",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String idTransaksi = String.valueOf(model.getValueAt(barisTerpilih, 0));
        Transaksi transaksi = this.cariTransaksiById(idTransaksi);
        if (transaksi == null) {
            JOptionPane.showMessageDialog(
                this,
                "Data transaksi \"" + idTransaksi + "\" tidak ditemukan.",
                "Kesalahan",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        TemaUI.tampilkanDialogInvoice(this, transaksi);
    }

    /**
     * Menerapkan nilai custom hari keterlambatan dari spinner ke kolom tanggal kembali
     * berdasarkan tanggal jatuh tempo transaksi terpilih.
     */
    private void terapkanHariTelatDariSpinner()
    {
        int baris = this.tabelPengembalian.getSelectedRow();
        if (baris < 0 || baris >= this.modelPengembalian.getRowCount()) {
            return;
        }

        String idTransaksi = String.valueOf(this.modelPengembalian.getValueAt(baris, 0));
        Transaksi transaksi = this.cariTransaksiById(idTransaksi);
        if (transaksi == null) {
            return;
        }

        int hariTelat = (Integer) this.spnHariTelat.getValue();
        this.sedangSyncPengembalian = true;
        this.txtTanggalKembali.setText(
            transaksi.getTanggalJatuhTempo().plusDays(hariTelat).toString()
        );
        this.sedangSyncPengembalian = false;
        this.perbaruiInfoPengembalian();
    }

    /**
     * Mengisi kolom tanggal kembali berdasarkan jatuh tempo transaksi yang dipilih ditambah selisih hari.
     *
     * @param tambahHari jumlah hari keterlambatan tambahan (0 untuk tepat jatuh tempo)
     */
    private void isiTanggalKembaliTerpilih(int tambahHari)
    {
        int baris = this.tabelPengembalian.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(
                this,
                "Pilih satu transaksi aktif pada tabel pengembalian terlebih dahulu.",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String idTransaksi = String.valueOf(this.modelPengembalian.getValueAt(baris, 0));
        Transaksi transaksi = this.cariTransaksiById(idTransaksi);
        if (transaksi != null) {
            this.sedangSyncPengembalian = true;
            this.spnHariTelat.setValue(Math.max(0, tambahHari));
            this.txtTanggalKembali.setText(
                transaksi.getTanggalJatuhTempo().plusDays(tambahHari).toString()
            );
            this.sedangSyncPengembalian = false;
            this.perbaruiInfoPengembalian();
        }
    }

    /**
     * Memperbarui label informasi jatuh tempo dan estimasi denda keterlambatan secara live
     * saat baris pengembalian dipilih atau tanggal kembali diubah.
     */
    private void perbaruiInfoPengembalian()
    {
        int baris = this.tabelPengembalian.getSelectedRow();
        if (baris < 0 || baris >= this.modelPengembalian.getRowCount()) {
            this.lblInfoKembali.setText(
                "Pilih transaksi aktif untuk melihat jatuh tempo dan estimasi denda."
            );
            this.lblInfoKembali.setForeground(TemaUI.WARNA_TEKS_SEKUNDER);
            return;
        }

        String idTransaksi = String.valueOf(this.modelPengembalian.getValueAt(baris, 0));
        Transaksi transaksi = this.cariTransaksiById(idTransaksi);
        if (transaksi == null) {
            return;
        }

        try {
            LocalDate tglKembali = LocalDate.parse(this.txtTanggalKembali.getText().trim());
            if (tglKembali.isBefore(transaksi.getTanggalSewa())) {
                this.lblInfoKembali.setText(
                    "Tanggal kembali tidak boleh sebelum " + transaksi.getTanggalSewa()
                );
                this.lblInfoKembali.setForeground(TemaUI.WARNA_STATUS_BAHAYA);
                return;
            }

            long telat = Math.max(
                0L, ChronoUnit.DAYS.between(transaksi.getTanggalJatuhTempo(), tglKembali)
            );
            if (!this.sedangSyncPengembalian) {
                this.sedangSyncPengembalian = true;
                this.spnHariTelat.setValue((int) Math.min(365L, telat));
                this.sedangSyncPengembalian = false;
            }

            if (telat > 0) {
                double dendaPerHari = 0.0;
                for (Barang item : transaksi.getBarang()) {
                    dendaPerHari += item.getDendaPerHari();
                }
                double estimasiDenda = telat * dendaPerHari;
                this.lblInfoKembali.setText(
                    "Jatuh tempo " + transaksi.getTanggalJatuhTempo()
                        + " | Terlambat " + telat + " hari | Est. Denda: "
                        + TemaUI.formatRupiah(estimasiDenda)
                );
                this.lblInfoKembali.setForeground(TemaUI.WARNA_STATUS_BAHAYA);
            } else {
                this.lblInfoKembali.setText(
                    "Jatuh tempo " + transaksi.getTanggalJatuhTempo() + " | Tepat waktu (Rp0,00)"
                );
                this.lblInfoKembali.setForeground(TemaUI.WARNA_STATUS_AMAN);
            }
        } catch (DateTimeParseException ignored) {
            this.lblInfoKembali.setText("Format tanggal harus yyyy-MM-dd");
            this.lblInfoKembali.setForeground(TemaUI.WARNA_STATUS_TUNGGU);
        }
    }

    /**
     * Mencari objek Transaksi di daftar transaksi admin berdasarkan ID transaksi.
     *
     * @param idTransaksi kode unik transaksi yang dicari
     * @return objek Transaksi jika ditemukan, atau null jika tidak ada
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
     * Memanggil callback penyimpanan data persisten jika tersedia.
     */
    private void simpanPerubahan()
    {
        if (this.onDataChanged != null) {
            this.onDataChanged.run();
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
}
