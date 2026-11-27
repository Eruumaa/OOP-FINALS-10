package view;

import exception.BarangTidakTersediaException;
import exception.DataTidakDitemukanException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import model.Admin;
import model.Barang;
import model.FamilyBoardGame;
import model.Invoice;
import model.Penyewa;
import model.StrategyGame;
import model.Transaksi;
import payment.Pembayaran;
import payment.QRIS;
import payment.TransferBank;
import payment.Tunai;

/**
 * Antarmuka grafis (GUI Java Swing) untuk akun Penyewa pada sistem BoardGameRent.
 *
 * <p>Jendela ini memakai tema Warm Tabletop yang seragam dengan {@link AdminFrame},
 * berisi tiga tab utama: Daftar Barang, Keranjang, dan Riwayat (beserta cetak Invoice).</p>
 *
 * @author Irvan Almidar
 * @version 2026.11.27
 */
public class PenyewaFrame extends JFrame
{
    private static final long serialVersionUID = 1L;

    /** Objek penyewa yang sedang aktif pada jendela ini. */
    private Penyewa penyewa;
    /** Referensi admin untuk meneruskan transaksi baru yang dibuat penyewa. */
    private Admin admin;
    /** Callback opsional yang dijalankan ketika penyewa melakukan logout. */
    private Runnable onLogout;
    /** Tabel katalog board game yang tersedia untuk disewa. */
    private JTable tabelBarang;
    /** Tabel isi keranjang sewa beserta estimasi biaya. */
    private JTable tabelKeranjang;
    /** Tabel riwayat transaksi penyewa. */
    private JTable tabelRiwayat;
    /** Model data tabel katalog board game. */
    private DefaultTableModel modelBarang;
    /** Model data tabel keranjang sewa. */
    private DefaultTableModel modelKeranjang;
    /** Model data tabel riwayat transaksi. */
    private DefaultTableModel modelRiwayat;
    /** Pemilih lama penyewaan dalam satuan hari. */
    private JSpinner spnLamaSewa;
    /** Pilihan strategi metode pembayaran. */
    private JComboBox<String> cmbPembayaran;
    /** Panel tab utama untuk memisahkan tiga fitur penyewa. */
    private JTabbedPane tabbedPane;
    /** Label ringkasan jumlah barang tersedia pada tab Daftar Barang. */
    private JLabel lblRingkasanBarang;
    /** Label ringkasan jumlah item dan estimasi total biaya pada tab Keranjang. */
    private JLabel lblRingkasanKeranjang;
    /** Label ringkasan jumlah transaksi pada tab Riwayat. */
    private JLabel lblRingkasanRiwayat;

    /**
     * Membuat jendela {@code PenyewaFrame} baru tanpa callback logout tambahan.
     *
     * @param penyewa objek {@link Penyewa} yang sedang login, tidak boleh {@code null}
     * @param admin objek {@link Admin} penerima transaksi, tidak boleh {@code null}
     * @throws IllegalArgumentException jika {@code penyewa} atau {@code admin} bernilai {@code null}
     */
    public PenyewaFrame(Penyewa penyewa, Admin admin)
    {
        this(penyewa, admin, null);
    }

    /**
     * Membuat jendela {@code PenyewaFrame} baru dengan callback opsional saat logout.
     *
     * @param penyewa objek {@link Penyewa} yang sedang login, tidak boleh {@code null}
     * @param admin objek {@link Admin} penerima transaksi, tidak boleh {@code null}
     * @param onLogout aksi yang dijalankan setelah jendela ditutup melalui tombol Logout,
     *                 boleh {@code null}
     * @throws IllegalArgumentException jika {@code penyewa} atau {@code admin} bernilai {@code null}
     */
    public PenyewaFrame(Penyewa penyewa, Admin admin, Runnable onLogout)
    {
        super("BoardGameRent - Panel Penyewa");

        if (penyewa == null || admin == null) {
            throw new IllegalArgumentException("Objek penyewa dan admin tidak boleh null.");
        }

        this.penyewa = penyewa;
        this.admin = admin;
        this.onLogout = onLogout;

        this.modelBarang = new TemaUI.TabelModelReadOnly(new String[] {
            "ID", "Nama", "Kategori", "Tarif/Hari", "Biaya Tambahan", "Denda/Hari", "Status"
        });
        this.modelKeranjang = new TemaUI.TabelModelReadOnly(new String[] {
            "ID", "Nama", "Kategori", "Tarif/Hari", "Biaya Tambahan", "Estimasi Sewa"
        });
        this.modelRiwayat = new TemaUI.TabelModelReadOnly(new String[] {
            "ID Transaksi", "Daftar Barang", "Lama (Hari)", "Total Biaya",
            "Pembayaran", "Tgl Kembali", "Status"
        });

        this.tabelBarang = TemaUI.buatTabelStandar(this.modelBarang);
        this.tabelKeranjang = TemaUI.buatTabelStandar(this.modelKeranjang);
        this.tabelRiwayat = TemaUI.buatTabelStandar(this.modelRiwayat);

        TemaUI.aturKolomTabel(
            this.tabelBarang,
            new int[] {70, 190, 130, 120, 180, 115, 125},
            new int[] {3, 5},
            new int[] {6}
        );
        TemaUI.aturKolomTabel(
            this.tabelKeranjang,
            new int[] {70, 200, 135, 120, 185, 150},
            new int[] {3, 5},
            new int[] {}
        );
        TemaUI.aturKolomTabel(
            this.tabelRiwayat,
            new int[] {95, 240, 85, 120, 105, 100, 165},
            new int[] {2, 3},
            new int[] {6}
        );

        this.spnLamaSewa = new JSpinner(new SpinnerNumberModel(1, 1, 365, 1));
        this.spnLamaSewa.setFont(new Font(TemaUI.FONT_UI, Font.PLAIN, 13));
        this.spnLamaSewa.setPreferredSize(new Dimension(64, 30));
        this.spnLamaSewa.addChangeListener(event -> this.tampilkanKeranjang());

        this.cmbPembayaran = new JComboBox<>(new String[] {
            "QRIS", "Transfer Bank", "Tunai"
        });
        this.cmbPembayaran.setFont(new Font(TemaUI.FONT_UI, Font.PLAIN, 12));
        this.cmbPembayaran.setPreferredSize(new Dimension(125, 30));

        this.tabbedPane = new JTabbedPane();
        this.lblRingkasanBarang = TemaUI.buatLabelRingkasan("0 board game tersedia");
        this.lblRingkasanKeranjang = TemaUI.buatLabelRingkasan("0 item | Estimasi: Rp0,00");
        this.lblRingkasanRiwayat = TemaUI.buatLabelRingkasan("0 transaksi");

        this.inisialisasiTampilan();
        this.refreshSemuaTabel();
    }

    /**
     * Memuat ulang daftar board game yang tersedia ke tabel katalog penyewa.
     */
    public void tampilkanBarang()
    {
        this.modelBarang.setRowCount(0);
        ArrayList<Barang> daftar = this.penyewa.lihatBarang();

        for (Barang item : daftar) {
            this.modelBarang.addRow(new Object[] {
                item.getId(),
                item.getNama(),
                item.getClass().getSimpleName(),
                TemaUI.formatRupiah(item.getTarifHarian()),
                this.formatBiayaTambahan(item),
                TemaUI.formatRupiah(item.getDendaPerHari()),
                item.isTersedia() ? "Tersedia" : "Sedang Disewa"
            });
        }

        this.lblRingkasanBarang.setText(daftar.size() + " board game tersedia");
    }

    /**
     * Memuat ulang isi keranjang sewa beserta perhitungan estimasi total biaya
     * sesuai nilai lama sewa pada spinner.
     */
    public void tampilkanKeranjang()
    {
        this.modelKeranjang.setRowCount(0);
        ArrayList<Barang> daftar = this.penyewa.getKeranjang().getBarang();
        int lamaSewa = (Integer) this.spnLamaSewa.getValue();
        double estimasiTotal = 0;

        for (Barang item : daftar) {
            double estimasiItem = item.hitungBiaya(lamaSewa);
            estimasiTotal += estimasiItem;
            this.modelKeranjang.addRow(new Object[] {
                item.getId(),
                item.getNama(),
                item.getClass().getSimpleName(),
                TemaUI.formatRupiah(item.getTarifHarian()),
                this.formatBiayaTambahan(item),
                TemaUI.formatRupiah(estimasiItem)
            });
        }

        this.lblRingkasanKeranjang.setText(
            daftar.size() + " item | Estimasi Total (" + lamaSewa + " hari): "
                + TemaUI.formatRupiah(estimasiTotal)
        );
    }

    /**
     * Memuat ulang seluruh riwayat transaksi penyewa ke tabel Riwayat.
     */
    public void tampilkanRiwayat()
    {
        this.modelRiwayat.setRowCount(0);
        ArrayList<Transaksi> daftar = this.penyewa.lihatRiwayat();

        for (Transaksi transaksi : daftar) {
            String tanggalKembali = (transaksi.getTanggalKembali() == null)
                ? "-"
                : transaksi.getTanggalKembali().toString();

            this.modelRiwayat.addRow(new Object[] {
                transaksi.getId(),
                TemaUI.formatDaftarBarang(transaksi.getBarang()),
                transaksi.getLamaSewa(),
                TemaUI.formatRupiah(transaksi.hitungTotal()),
                transaksi.getPembayaran().getClass().getSimpleName(),
                tanggalKembali,
                transaksi.getStatus()
            });
        }

        this.lblRingkasanRiwayat.setText(daftar.size() + " transaksi");
    }

    /**
     * Menambahkan board game yang sedang dipilih pada katalog ke dalam keranjang sewa.
     */
    public void aksiTambahKeKeranjang()
    {
        int baris = this.tabelBarang.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(
                this,
                "Pilih satu board game pada tabel terlebih dahulu.",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String idBarang = String.valueOf(this.modelBarang.getValueAt(baris, 0));

        try {
            Barang barang = this.admin.kelolaBarang().cariBarang(idBarang);
            this.penyewa.getKeranjang().tambahBarang(barang);
            this.refreshSemuaTabel();

            JOptionPane.showMessageDialog(
                this,
                "\"" + barang.getNama() + "\" berhasil ditambahkan ke keranjang.",
                "Berhasil",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (BarangTidakTersediaException | DataTidakDitemukanException
                 | IllegalArgumentException exception) {
            this.refreshSemuaTabel();
            JOptionPane.showMessageDialog(
                this,
                exception.getMessage(),
                "Gagal Menambahkan ke Keranjang",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Menghapus board game yang sedang dipilih dari keranjang sewa.
     */
    public void aksiHapusKeranjang()
    {
        int baris = this.tabelKeranjang.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(
                this,
                "Pilih satu item pada keranjang terlebih dahulu untuk dihapus.",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String idBarang = String.valueOf(this.modelKeranjang.getValueAt(baris, 0));
        this.penyewa.getKeranjang().hapusBarang(idBarang);
        this.refreshSemuaTabel();

        JOptionPane.showMessageDialog(
            this,
            "Barang berhasil dihapus dari keranjang.",
            "Berhasil",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Membuat transaksi baru dari isi keranjang, memproses pembayaran melalui strategi
     * yang dipilih, dan meneruskannya ke daftar transaksi admin.
     */
    public void aksiBuatTransaksi()
    {
        if (this.penyewa.getKeranjang().getBarang().isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Keranjang masih kosong. Tambahkan board game terlebih dahulu.",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            int lamaSewa = (Integer) this.spnLamaSewa.getValue();
            Pembayaran pembayaran = this.buatStrategiPembayaran();

            Transaksi transaksi = this.penyewa.buatTransaksi(lamaSewa, pembayaran);
            String hasilPembayaran = transaksi.getPembayaran()
                .prosesPembayaran(transaksi.hitungTotal());
            this.admin.tambahTransaksi(transaksi);

            this.refreshSemuaTabel();

            JOptionPane.showMessageDialog(
                this,
                "Transaksi \"" + transaksi.getId() + "\" berhasil dibuat.\n"
                    + hasilPembayaran + "\nStatus saat ini: " + transaksi.getStatus(),
                "Transaksi Dibuat",
                JOptionPane.INFORMATION_MESSAGE
            );

            this.tabbedPane.setSelectedIndex(2);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            JOptionPane.showMessageDialog(
                this,
                exception.getMessage(),
                "Gagal Membuat Transaksi",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Menampilkan struk {@link Invoice} untuk transaksi yang dipilih pada tab Riwayat.
     */
    public void aksiLihatInvoice()
    {
        int baris = this.tabelRiwayat.getSelectedRow();
        if (baris < 0) {
            JOptionPane.showMessageDialog(
                this,
                "Pilih satu transaksi pada tabel riwayat terlebih dahulu.",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String idTransaksi = String.valueOf(this.modelRiwayat.getValueAt(baris, 0));
        Transaksi transaksi = this.cariTransaksiRiwayat(idTransaksi);

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

        Invoice invoice = new Invoice(transaksi);
        JTextArea areaInvoice = new JTextArea(invoice.cetakInvoice());
        areaInvoice.setEditable(false);
        areaInvoice.setFont(new Font(TemaUI.FONT_MONO, Font.PLAIN, 12));
        areaInvoice.setForeground(TemaUI.WARNA_TEKS_UTAMA);
        areaInvoice.setBackground(TemaUI.WARNA_SURFACE);
        areaInvoice.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JScrollPane scrollInvoice = new JScrollPane(areaInvoice);
        scrollInvoice.setPreferredSize(new Dimension(470, 320));
        scrollInvoice.setBorder(BorderFactory.createLineBorder(TemaUI.WARNA_BORDER));

        JOptionPane.showMessageDialog(
            this,
            scrollInvoice,
            "Invoice - " + transaksi.getId(),
            JOptionPane.PLAIN_MESSAGE
        );
    }

    /**
     * Mengembalikan objek {@link Penyewa} yang sedang login.
     *
     * @return objek penyewa aktif
     */
    public Penyewa getPenyewa()
    {
        return this.penyewa;
    }

    /**
     * Mengembalikan tabel katalog barang yang tersedia.
     *
     * @return tabel katalog barang
     */
    public JTable getTabelBarang()
    {
        return this.tabelBarang;
    }

    /**
     * Mengembalikan tabel isi keranjang sewa.
     *
     * @return tabel keranjang
     */
    public JTable getTabelKeranjang()
    {
        return this.tabelKeranjang;
    }

    /**
     * Mengembalikan tabel riwayat transaksi penyewa.
     *
     * @return tabel riwayat transaksi
     */
    public JTable getTabelRiwayat()
    {
        return this.tabelRiwayat;
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
            "BoardGameRent - Panel Penyewa",
            "Login sebagai: " + this.penyewa.getUsername(),
            this::refreshSemuaTabel,
            this::prosesLogout
        );
        this.add(header, BorderLayout.NORTH);

        this.tabbedPane.setFont(new Font(TemaUI.FONT_UI, Font.BOLD, 12));
        this.tabbedPane.setBackground(TemaUI.WARNA_CANVAS);
        this.tabbedPane.setForeground(TemaUI.WARNA_TEKS_UTAMA);
        this.tabbedPane.setBorder(BorderFactory.createEmptyBorder(12, 16, 16, 16));
        this.tabbedPane.addTab("  Daftar Barang  ", this.buatPanelBarang());
        this.tabbedPane.addTab("  Keranjang  ", this.buatPanelKeranjang());
        this.tabbedPane.addTab("  Riwayat  ", this.buatPanelRiwayat());
        this.tabbedPane.addChangeListener(event -> this.refreshSemuaTabel());

        this.add(this.tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Membuat panel tab Daftar Barang beserta tombol Tambah ke Keranjang.
     *
     * @return panel tab Daftar Barang
     */
    private JPanel buatPanelBarang()
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

        JButton btnTambah = TemaUI.buatTombolPrimer("Tambah ke Keranjang");
        btnTambah.addActionListener(event -> this.aksiTambahKeKeranjang());

        panelTombol.add(btnTambah);
        panelBawah.add(panelTombol, BorderLayout.EAST);
        panel.add(panelBawah, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Membuat panel tab Keranjang beserta pengaturan lama sewa, pilihan pembayaran,
     * dan tombol Buat Transaksi.
     *
     * @return panel tab Keranjang
     */
    private JPanel buatPanelKeranjang()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(TemaUI.WARNA_CANVAS);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        panel.add(TemaUI.buatScrollTabel(this.tabelKeranjang), BorderLayout.CENTER);

        JPanel panelBawah = new JPanel(new BorderLayout());
        panelBawah.setOpaque(false);
        panelBawah.add(this.lblRingkasanKeranjang, BorderLayout.WEST);

        JPanel panelAksi = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelAksi.setOpaque(false);

        JLabel lblLama = new JLabel("Lama (hari):");
        lblLama.setFont(new Font(TemaUI.FONT_UI, Font.PLAIN, 12));
        lblLama.setForeground(TemaUI.WARNA_TEKS_UTAMA);

        JLabel lblMetode = new JLabel("Pembayaran:");
        lblMetode.setFont(new Font(TemaUI.FONT_UI, Font.PLAIN, 12));
        lblMetode.setForeground(TemaUI.WARNA_TEKS_UTAMA);

        JButton btnHapus = TemaUI.buatTombolBahaya("Hapus Barang");
        btnHapus.addActionListener(event -> this.aksiHapusKeranjang());

        JButton btnBuatTransaksi = TemaUI.buatTombolPrimer("Buat Transaksi");
        btnBuatTransaksi.addActionListener(event -> this.aksiBuatTransaksi());

        panelAksi.add(lblLama);
        panelAksi.add(this.spnLamaSewa);
        panelAksi.add(lblMetode);
        panelAksi.add(this.cmbPembayaran);
        panelAksi.add(btnHapus);
        panelAksi.add(btnBuatTransaksi);

        panelBawah.add(panelAksi, BorderLayout.EAST);
        panel.add(panelBawah, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Membuat panel tab Riwayat beserta tombol Lihat Invoice.
     *
     * @return panel tab Riwayat
     */
    private JPanel buatPanelRiwayat()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(TemaUI.WARNA_CANVAS);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        panel.add(TemaUI.buatScrollTabel(this.tabelRiwayat), BorderLayout.CENTER);

        JPanel panelBawah = new JPanel(new BorderLayout());
        panelBawah.setOpaque(false);
        panelBawah.add(this.lblRingkasanRiwayat, BorderLayout.WEST);

        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelTombol.setOpaque(false);

        JButton btnInvoice = TemaUI.buatTombolPrimer("Lihat Invoice");
        btnInvoice.addActionListener(event -> this.aksiLihatInvoice());

        panelTombol.add(btnInvoice);
        panelBawah.add(panelTombol, BorderLayout.EAST);
        panel.add(panelBawah, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Memperbarui seluruh data pada ketiga tabel penyewa sekaligus.
     */
    private void refreshSemuaTabel()
    {
        this.tampilkanBarang();
        this.tampilkanKeranjang();
        this.tampilkanRiwayat();
    }

    /**
     * Membentuk teks keterangan biaya tambahan sesuai kategori board game.
     *
     * @param item objek board game
     * @return ringkasan biaya tambahan atau {@code "-"} bila tidak ada
     */
    private String formatBiayaTambahan(Barang item)
    {
        if (item instanceof FamilyBoardGame) {
            FamilyBoardGame familyGame = (FamilyBoardGame) item;
            return "Garansi: " + TemaUI.formatRupiah(familyGame.getBiayaGaransiPion());
        }
        if (item instanceof StrategyGame) {
            StrategyGame strategyGame = (StrategyGame) item;
            return "Modul/hari: " + TemaUI.formatRupiah(strategyGame.getBiayaModulHarian());
        }
        return "-";
    }

    /**
     * Membuat instansiasi objek strategi {@link Pembayaran} sesuai pilihan pada combo box.
     *
     * @return objek implementasi {@link Pembayaran}
     */
    private Pembayaran buatStrategiPembayaran()
    {
        String metode = String.valueOf(this.cmbPembayaran.getSelectedItem());
        if ("QRIS".equals(metode)) {
            return new QRIS();
        }
        if ("Transfer Bank".equals(metode)) {
            return new TransferBank();
        }
        return new Tunai();
    }

    /**
     * Mencari objek {@link Transaksi} di daftar riwayat penyewa berdasarkan ID transaksi.
     *
     * @param idTransaksi kode unik transaksi yang dicari
     * @return objek transaksi jika ditemukan, atau {@code null} jika tidak ada
     */
    private Transaksi cariTransaksiRiwayat(String idTransaksi)
    {
        for (Transaksi transaksi : this.penyewa.lihatRiwayat()) {
            if (transaksi.getId().equalsIgnoreCase(idTransaksi)) {
                return transaksi;
            }
        }
        return null;
    }

    /**
     * Menutup jendela penyewa dan menjalankan callback logout bila tersedia.
     */
    private void prosesLogout()
    {
        this.dispose();
        if (this.onLogout != null) {
            this.onLogout.run();
        }
    }
}
