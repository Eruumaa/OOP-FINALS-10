package view;

import exception.BarangTidakTersediaException;
import exception.DataTidakDitemukanException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import javax.swing.table.DefaultTableModel;
import model.Admin;
import model.Barang;
import model.Penyewa;
import model.Transaksi;
import payment.Pembayaran;
import payment.QRIS;
import payment.TransferBank;
import payment.Tunai;
import payment.Wallet;

/**
 * Antarmuka grafis (GUI Java Swing) untuk akun Penyewa pada sistem BoardGameRent.
 *
 * Menggunakan tema Cozy Pixel Tabletop dengan tampilan katalog Split View (tabel
 * ber-thumbnail dan kartu showcase foto), filter kategori serta pencarian langsung,
 * keranjang sewa dengan kalkulator live, dan tab riwayat lengkap dengan grafik
 * pengeluaran serta dialog struk Invoice.
 *
 * @author Irvan Almidar
 * @version 2026.11.27
 */
public class PenyewaFrame extends JFrame
{
    private static final long serialVersionUID = 1L;

    // Data inti dan navigasi utama
    private transient Penyewa penyewa;
    private transient Admin admin;
    private transient Runnable onLogout;
    private transient Runnable onDataChanged;
    private transient JTabbedPane tabbedPane;

    // Tabel dan model data
    private transient JTable tabelBarang;
    private transient JTable tabelKeranjang;
    private transient JTable tabelRiwayat;
    private transient DefaultTableModel modelBarang;
    private transient DefaultTableModel modelKeranjang;
    private transient DefaultTableModel modelRiwayat;

    // Filter katalog dan kontrol checkout
    private transient JTextField txtCariBarang;
    private transient JComboBox<String> cmbFilterKategori;
    private transient JComboBox<String> cmbUrutkan;
    private transient JSpinner spnLamaSewa;
    private transient JComboBox<String> cmbPembayaran;

    // Kartu showcase katalog
    private transient JLabel lblDetailFoto;
    private transient JLabel lblDetailKategori;
    private transient JLabel lblDetailId;
    private transient JLabel lblDetailNama;
    private transient JLabel lblDetailTarif;
    private transient JLabel lblDetailBiayaTambahan;
    private transient JLabel lblDetailDenda;
    private transient JLabel lblDetailStatus;
    private transient JButton btnTambahShowcase;

    // Ringkasan tab dan checkout keranjang
    private transient JLabel lblRingkasanBarang;
    private transient JLabel lblRingkasanKeranjang;
    private transient JLabel lblEstimasiTotal;
    private transient JLabel lblRincianCheckout;
    private transient JLabel lblKeranjangFoto;
    private transient JLabel lblKeranjangInfo;
    private transient JLabel lblSaldoWallet;
    private transient JLabel lblRingkasanRiwayat;

    // Statistik pengeluaran dan grafik riwayat
    private transient JLabel lblStatTotalTrx;
    private transient JLabel lblStatTotalTrxSub;
    private transient JLabel lblStatAktif;
    private transient JLabel lblStatAktifSub;
    private transient JLabel lblStatTotalBelanja;
    private transient JLabel lblStatTotalBelanjaSub;
    private transient TemaUI.PanelGrafikBatang grafikMetodeBayar;

    /**
     * Membuat jendela PenyewaFrame baru tanpa callback logout tambahan.
     *
     * @param penyewa objek Penyewa yang sedang login, tidak boleh null
     * @param admin objek Admin penerima transaksi, tidak boleh null
     * @throws IllegalArgumentException jika penyewa atau admin bernilai null
     */
    public PenyewaFrame(Penyewa penyewa, Admin admin)
    {
        this(penyewa, admin, null, null);
    }

    /**
     * Membuat jendela PenyewaFrame baru dengan callback opsional saat logout.
     *
     * @param penyewa objek Penyewa yang sedang login, tidak boleh null
     * @param admin objek Admin penerima transaksi, tidak boleh null
     * @param onLogout aksi yang dijalankan setelah jendela ditutup melalui tombol Logout,
     *                 boleh null
     * @throws IllegalArgumentException jika penyewa atau admin bernilai null
     */
    public PenyewaFrame(Penyewa penyewa, Admin admin, Runnable onLogout)
    {
        this(penyewa, admin, onLogout, null);
    }

    /**
     * Membuat jendela PenyewaFrame baru dengan callback logout dan callback
     * penyimpanan data persisten.
     *
     * @param penyewa objek Penyewa yang sedang login, tidak boleh null
     * @param admin objek Admin penerima transaksi, tidak boleh null
     * @param onLogout aksi yang dijalankan setelah jendela ditutup melalui tombol Logout,
     *                 boleh null
     * @param onDataChanged callback yang dipanggil setiap kali data berubah, boleh null
     * @throws IllegalArgumentException jika penyewa atau admin bernilai null
     */
    public PenyewaFrame(Penyewa penyewa, Admin admin, Runnable onLogout, Runnable onDataChanged)
    {
        super("Rent the Board! - Panel Penyewa (BoardGameRent)");
        this.setIconImage(TemaUI.muatLogoPixel(64).getImage());

        if (penyewa == null || admin == null) {
            throw new IllegalArgumentException("Objek penyewa dan admin tidak boleh null.");
        }

        this.penyewa = penyewa;
        this.admin = admin;
        this.onLogout = onLogout;
        this.onDataChanged = onDataChanged;

        this.modelBarang = new TemaUI.TabelModelReadOnly(new String[] {
            "Foto", "ID", "Nama Board Game", "Kategori", "Tarif/Hari", "Status"
        });
        this.modelKeranjang = new TemaUI.TabelModelReadOnly(new String[] {
            "Foto", "ID", "Nama Board Game", "Kategori", "Tarif/Hari", "Estimasi Sewa"
        });
        this.modelRiwayat = new TemaUI.TabelModelReadOnly(new String[] {
            "ID Transaksi", "Daftar Barang", "Lama (Hari)", "Total Sewa",
            "Denda", "Pembayaran", "Tgl Kembali", "Status"
        });

        this.tabelBarang = TemaUI.buatTabelStandar(this.modelBarang, 56);
        this.tabelKeranjang = TemaUI.buatTabelStandar(this.modelKeranjang, 56);
        this.tabelRiwayat = TemaUI.buatTabelStandar(this.modelRiwayat, 32);

        TemaUI.aturKolomTabel(
            this.tabelBarang,
            new int[] {62, 66, 195, 130, 115, 110},
            new int[] {4},
            new int[] {5},
            0
        );
        TemaUI.aturKolomTabel(
            this.tabelKeranjang,
            new int[] {62, 66, 200, 130, 115, 135},
            new int[] {4, 5},
            new int[] {},
            0
        );
        TemaUI.aturKolomTabel(
            this.tabelRiwayat,
            new int[] {90, 240, 75, 115, 95, 105, 95, 145},
            new int[] {2, 3, 4},
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
                PenyewaFrame.this.tampilkanBarang();
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

        this.spnLamaSewa = new JSpinner(new SpinnerNumberModel(1, 1, 365, 1));
        this.spnLamaSewa.setFont(new Font(TemaUI.FONT_UI, Font.BOLD, 13));
        this.spnLamaSewa.setPreferredSize(new Dimension(78, 32));
        this.spnLamaSewa.addChangeListener(event -> this.tampilkanKeranjang());

        this.cmbPembayaran = new JComboBox<>(new String[] {
            "QRIS", "Transfer Bank", "Tunai", "Saldo Wallet"
        });
        this.cmbPembayaran.setFont(new Font(TemaUI.FONT_UI, Font.PLAIN, 12));
        this.cmbPembayaran.setPreferredSize(new Dimension(160, 32));
        this.cmbPembayaran.addActionListener(event -> this.perbaruiRingkasanCheckout());

        this.lblSaldoWallet = new JLabel(TemaUI.formatRupiah(this.penyewa.getSaldoWallet()));

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
        this.btnTambahShowcase = TemaUI.buatTombolPrimer("Tambah ke Keranjang");
        this.btnTambahShowcase.setPreferredSize(new Dimension(0, 38));
        this.btnTambahShowcase.addActionListener(event -> this.aksiTambahKeKeranjang());

        this.lblRingkasanBarang = TemaUI.buatLabelRingkasan("0 board game tersedia");
        this.lblRingkasanKeranjang = TemaUI.buatLabelRingkasan("0 item di keranjang");
        this.lblEstimasiTotal = new JLabel("Rp0,00");
        this.lblRincianCheckout = TemaUI.buatLabelRingkasan("Jatuh tempo: - | Metode: QRIS");
        this.lblKeranjangFoto = new JLabel("", SwingConstants.CENTER);
        this.lblKeranjangFoto.setPreferredSize(new Dimension(52, 52));
        this.lblKeranjangFoto.setOpaque(true);
        this.lblKeranjangFoto.setBackground(TemaUI.WARNA_SURFACE);
        this.lblKeranjangFoto.setBorder(BorderFactory.createLineBorder(TemaUI.WARNA_BORDER, 2));
        this.lblKeranjangInfo = new JLabel("Pilih item keranjang");
        this.lblKeranjangInfo.setFont(new Font(TemaUI.FONT_UI, Font.PLAIN, 12));
        this.lblKeranjangInfo.setForeground(TemaUI.WARNA_TEKS_SEKUNDER);
        this.lblRingkasanRiwayat = TemaUI.buatLabelRingkasan("0 transaksi");

        this.lblStatTotalTrx = new JLabel("0");
        this.lblStatTotalTrxSub = new JLabel("Semua pesanan");
        this.lblStatAktif = new JLabel("0");
        this.lblStatAktifSub = new JLabel("Menunggu / disewa");
        this.lblStatTotalBelanja = new JLabel("Rp0,00");
        this.lblStatTotalBelanjaSub = new JLabel("Total nilai transaksi");
        this.grafikMetodeBayar = new TemaUI.PanelGrafikBatang(
            "Total Sewa per Metode Pembayaran", true
        );

        this.tabelBarang.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                this.perbaruiShowcaseBarang();
            }
        });
        this.tabelKeranjang.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                this.perbaruiPreviewKeranjang();
            }
        });
        this.tabelBarang.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent event)
            {
                if (event.getClickCount() == 2 && PenyewaFrame.this.tabelBarang.getSelectedRow() >= 0) {
                    PenyewaFrame.this.aksiTambahKeKeranjang();
                }
            }
        });
        this.tabelRiwayat.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent event)
            {
                if (event.getClickCount() == 2
                        && PenyewaFrame.this.tabelRiwayat.getSelectedRow() >= 0) {
                    PenyewaFrame.this.aksiLihatInvoice();
                }
            }
        });

        this.tabbedPane = new JTabbedPane();
        this.inisialisasiTampilan();
        this.refreshSemuaTabel();
    }

    /**
     * Memuat ulang daftar board game yang tersedia ke tabel katalog sesuai filter aktif.
     */
    public void tampilkanBarang()
    {
        this.modelBarang.setRowCount(0);
        ArrayList<Barang> daftar = TemaUI.urutkanDaftarBarang(
            this.penyewa.lihatBarang(),
            String.valueOf(this.cmbUrutkan.getSelectedItem())
        );
        String kataKunci = this.txtCariBarang.getText().trim().toLowerCase();
        String filterKategori = String.valueOf(this.cmbFilterKategori.getSelectedItem());

        int jumlahCard = 0;
        int jumlahFamily = 0;
        int jumlahStrategy = 0;

        for (Barang item : daftar) {
            String kategori = item.getClass().getSimpleName();
            if ("CardGame".equalsIgnoreCase(kategori)) {
                jumlahCard++;
            } else if ("FamilyBoardGame".equalsIgnoreCase(kategori)) {
                jumlahFamily++;
            } else if ("StrategyGame".equalsIgnoreCase(kategori)) {
                jumlahStrategy++;
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
                    item.isTersedia() ? "Tersedia" : "Sedang Disewa"
                });
            }
        }

        this.lblRingkasanBarang.setText(
            this.modelBarang.getRowCount() + " tampil dari " + daftar.size()
                + " tersedia  |  Card: " + jumlahCard
                + "  |  Family: " + jumlahFamily
                + "  |  Strategy: " + jumlahStrategy
        );

        if (this.modelBarang.getRowCount() > 0) {
            if (this.tabelBarang.getSelectedRow() < 0) {
                this.tabelBarang.setRowSelectionInterval(0, 0);
            }
        }
        this.perbaruiShowcaseBarang();
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
                TemaUI.muatGambarBarang(item, 44),
                item.getId(),
                item.getNama(),
                item.getClass().getSimpleName(),
                TemaUI.formatRupiah(item.getTarifHarian()),
                TemaUI.formatRupiah(estimasiItem)
            });
        }

        this.lblRingkasanKeranjang.setText(
            daftar.size() + " item siap disewa (" + lamaSewa + " hari)"
        );
        this.lblEstimasiTotal.setText(TemaUI.formatRupiah(estimasiTotal));
        if (this.tabbedPane.getTabCount() > 1) {
            this.tabbedPane.setTitleAt(
                1,
                daftar.isEmpty() ? "  Keranjang  " : "  Keranjang (" + daftar.size() + ")  "
            );
        }
        if (this.modelKeranjang.getRowCount() > 0 && this.tabelKeranjang.getSelectedRow() < 0) {
            this.tabelKeranjang.setRowSelectionInterval(0, 0);
        }
        this.perbaruiPreviewKeranjang();
        this.perbaruiRingkasanCheckout();
    }

    /**
     * Memuat ulang seluruh riwayat transaksi penyewa beserta statistik dan grafik pengeluaran.
     */
    public void tampilkanRiwayat()
    {
        this.modelRiwayat.setRowCount(0);
        ArrayList<Transaksi> daftar = this.penyewa.lihatRiwayat();

        int aktif = 0;
        double totalBelanja = 0;
        double totalQris = 0;
        double totalTransfer = 0;
        double totalTunai = 0;
        double totalWallet = 0;

        for (Transaksi transaksi : daftar) {
            String tanggalKembali = (transaksi.getTanggalKembali() == null)
                ? "-"
                : transaksi.getTanggalKembali().toString();
            String metode = transaksi.getPembayaran().getClass().getSimpleName();
            double biaya = transaksi.hitungTotal();
            double denda = transaksi.hitungDenda();

            this.modelRiwayat.addRow(new Object[] {
                transaksi.getId(),
                TemaUI.formatDaftarBarang(transaksi.getBarang()),
                transaksi.getLamaSewa(),
                TemaUI.formatRupiah(biaya),
                TemaUI.formatRupiah(denda),
                metode,
                tanggalKembali,
                transaksi.getStatus()
            });

            if ("Menunggu Konfirmasi".equalsIgnoreCase(transaksi.getStatus())
                    || "Sedang Disewa".equalsIgnoreCase(transaksi.getStatus())) {
                aktif++;
            }
            if (!"Ditolak".equalsIgnoreCase(transaksi.getStatus())
                    && !"Dibatalkan".equalsIgnoreCase(transaksi.getStatus())) {
                totalBelanja += biaya + denda;
                if ("QRIS".equalsIgnoreCase(metode)) {
                    totalQris += biaya;
                } else if ("TransferBank".equalsIgnoreCase(metode)) {
                    totalTransfer += biaya;
                } else if ("Wallet".equalsIgnoreCase(metode)) {
                    totalWallet += biaya;
                } else {
                    totalTunai += biaya;
                }
            }
        }

        this.lblRingkasanRiwayat.setText(daftar.size() + " transaksi tercatat");
        if (this.tabbedPane.getTabCount() > 2) {
            this.tabbedPane.setTitleAt(
                2,
                daftar.isEmpty()
                    ? "  Riwayat & Statistik  "
                    : "  Riwayat & Statistik (" + daftar.size() + ")  "
            );
        }
        this.lblStatTotalTrx.setText(String.valueOf(daftar.size()));
        this.lblStatAktif.setText(String.valueOf(aktif));
        this.lblStatTotalBelanja.setText(TemaUI.formatRupiah(totalBelanja));

        if (this.modelRiwayat.getRowCount() > 0 && this.tabelRiwayat.getSelectedRow() < 0) {
            this.tabelRiwayat.setRowSelectionInterval(0, 0);
        }

        this.grafikMetodeBayar.setData(
            new String[] {"QRIS", "TransferBank", "Tunai", "Wallet"},
            new double[] {totalQris, totalTransfer, totalTunai, totalWallet}
        );
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

        String idBarang = String.valueOf(this.modelBarang.getValueAt(baris, 1));

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

        String idBarang = String.valueOf(this.modelKeranjang.getValueAt(baris, 1));
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
            double totalTagihan = 0.0;
            for (Barang item : this.penyewa.getKeranjang().getBarang()) {
                totalTagihan += item.hitungBiaya(lamaSewa);
            }

            String namaMetode = String.valueOf(this.cmbPembayaran.getSelectedItem());
            boolean pembayaranDikonfirmasi = TemaUI.tampilkanDialogProsesPembayaran(
                this,
                namaMetode,
                totalTagihan,
                lamaSewa,
                this.penyewa.getKeranjang().getBarang().size(),
                this.penyewa.getSaldoWallet()
            );
            if (!pembayaranDikonfirmasi) {
                return;
            }

            Pembayaran pembayaran = this.buatStrategiPembayaran();
            Transaksi transaksi = this.penyewa.buatTransaksi(lamaSewa, pembayaran);
            if ("Saldo Wallet".equals(namaMetode)) {
                this.penyewa.potongSaldoWallet(transaksi.hitungTotal());
            }
            String hasilPembayaran = transaksi.getPembayaran()
                .prosesPembayaran(transaksi.hitungTotal());
            this.admin.tambahTransaksi(transaksi);
            this.simpanPerubahan();

            this.refreshSemuaTabel();

            JOptionPane.showMessageDialog(
                this,
                "Transaksi \"" + transaksi.getId() + "\" berhasil dibuat.\n"
                    + hasilPembayaran + "\nStatus saat ini: " + transaksi.getStatus(),
                "Pembayaran & Transaksi Berhasil",
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
     * Menampilkan struk Invoice untuk transaksi yang dipilih pada tab Riwayat.
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

        TemaUI.tampilkanDialogInvoice(this, transaksi);
    }

    /**
     * Membatalkan transaksi yang dipilih pada tabel riwayat jika masih berstatus
     * "Menunggu Konfirmasi", sekaligus mengembalikan saldo Wallet bila digunakan.
     */
    public void aksiBatalkanTransaksi()
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

        if (!"Menunggu Konfirmasi".equalsIgnoreCase(transaksi.getStatus())) {
            JOptionPane.showMessageDialog(
                this,
                "Hanya transaksi berstatus \"Menunggu Konfirmasi\" yang dapat dibatalkan.",
                "Tidak Dapat Dibatalkan",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        boolean pakaiWallet = transaksi.getPembayaran() instanceof Wallet;
        String pesanKonfirmasi = "Yakin ingin membatalkan transaksi \"" + transaksi.getId() + "\"?";
        if (pakaiWallet) {
            pesanKonfirmasi += "\nSaldo Wallet sebesar "
                + TemaUI.formatRupiah(transaksi.hitungTotal()) + " akan dikembalikan otomatis.";
        }

        int konfirmasi = JOptionPane.showConfirmDialog(
            this,
            pesanKonfirmasi,
            "Konfirmasi Pembatalan",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            this.penyewa.batalkanTransaksi(transaksi);
            this.simpanPerubahan();
            this.refreshSemuaTabel();

            String pesanBerhasil = "Transaksi \"" + transaksi.getId() + "\" berhasil dibatalkan.";
            if (pakaiWallet) {
                pesanBerhasil += "\nSaldo Wallet dikembalikan menjadi "
                    + TemaUI.formatRupiah(this.penyewa.getSaldoWallet()) + ".";
            }
            JOptionPane.showMessageDialog(
                this,
                pesanBerhasil,
                "Pesanan Dibatalkan",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (IllegalArgumentException | IllegalStateException exception) {
            JOptionPane.showMessageDialog(
                this,
                exception.getMessage(),
                "Gagal Membatalkan Pesanan",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Mengembalikan objek Penyewa yang sedang login.
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
        this.setMinimumSize(new Dimension(1020, 640));
        this.setSize(1140, 700);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(TemaUI.WARNA_CANVAS);

        JPanel header = TemaUI.buatPanelHeader(
            "Rent the Board! - Panel Penyewa",
            "BoardGameRent | Login sebagai: " + this.penyewa.getUsername(),
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
        this.tabbedPane.addTab("  Riwayat & Statistik  ", this.buatPanelRiwayat());
        this.tabbedPane.addChangeListener(event -> this.refreshSemuaTabel());

        this.add(this.tabbedPane, BorderLayout.CENTER);
        this.add(TemaUI.buatStatusBar("Penyewa"), BorderLayout.SOUTH);
    }

    /**
     * Membuat panel tab Daftar Barang dengan bilah filter, tabel ber-thumbnail di kiri,
     * dan kartu showcase foto detail board game di sisi kanan.
     *
     * @return panel tab Daftar Barang
     */
    private JPanel buatPanelBarang()
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

        panel.add(bilahFilter, BorderLayout.NORTH);
        panel.add(TemaUI.buatScrollTabel(this.tabelBarang), BorderLayout.CENTER);
        panel.add(this.buatKartuShowcaseBarang(), BorderLayout.EAST);

        return panel;
    }

    /**
     * Membuat kartu showcase foto besar dan rincian tarif board game terpilih di kanan katalog.
     *
     * @return panel kartu showcase board game
     */
    private JPanel buatKartuShowcaseBarang()
    {
        JPanel kartu = new JPanel(new BorderLayout(0, 12));
        kartu.setPreferredSize(new Dimension(320, 0));
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

        this.lblDetailNama.setFont(new Font(TemaUI.FONT_UI, Font.BOLD, 17));
        this.lblDetailNama.setForeground(TemaUI.WARNA_TEKS_UTAMA);

        bagianAtas.add(wadahFoto);
        bagianAtas.add(Box.createVerticalStrut(12));
        bagianAtas.add(barisBadge);
        bagianAtas.add(Box.createVerticalStrut(8));

        JPanel bungkusNama = new JPanel(new BorderLayout());
        bungkusNama.setOpaque(false);
        bungkusNama.add(this.lblDetailNama, BorderLayout.WEST);
        bagianAtas.add(bungkusNama);

        JPanel kotakHarga = new JPanel(new GridLayout(4, 2, 6, 8));
        kotakHarga.setBackground(TemaUI.WARNA_KARTU_KREM);
        kotakHarga.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(TemaUI.WARNA_BORDER, 2),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        kotakHarga.add(this.buatLabelKeteranganDetail("Tarif / Hari"));
        kotakHarga.add(this.formatLabelNilaiDetail(this.lblDetailTarif));
        kotakHarga.add(this.buatLabelKeteranganDetail("Biaya Tambahan"));
        kotakHarga.add(this.formatLabelNilaiDetail(this.lblDetailBiayaTambahan));
        kotakHarga.add(this.buatLabelKeteranganDetail("Denda / Hari"));
        kotakHarga.add(this.formatLabelNilaiDetail(this.lblDetailDenda));
        kotakHarga.add(this.buatLabelKeteranganDetail("Status"));
        kotakHarga.add(this.formatLabelNilaiDetail(this.lblDetailStatus));

        kartu.add(bagianAtas, BorderLayout.NORTH);
        kartu.add(kotakHarga, BorderLayout.CENTER);
        kartu.add(this.btnTambahShowcase, BorderLayout.SOUTH);
        return kartu;
    }

    /**
     * Membuat panel tab Keranjang beserta kartu ringkasan checkout di sisi kanan.
     *
     * @return panel tab Keranjang
     */
    private JPanel buatPanelKeranjang()
    {
        JPanel panel = new JPanel(new BorderLayout(16, 12));
        panel.setBackground(TemaUI.WARNA_CANVAS);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JPanel panelKiri = new JPanel(new BorderLayout(0, 12));
        panelKiri.setOpaque(false);
        panelKiri.add(this.buatPanelWallet(), BorderLayout.NORTH);
        panelKiri.add(TemaUI.buatScrollTabel(this.tabelKeranjang), BorderLayout.CENTER);

        panel.add(panelKiri, BorderLayout.CENTER);

        JPanel kartuCheckout = new JPanel(new BorderLayout(0, 10));
        kartuCheckout.setPreferredSize(new Dimension(320, 0));
        kartuCheckout.setBackground(TemaUI.WARNA_SURFACE);
        kartuCheckout.setBorder(TemaUI.buatBorderKartuPixel(14, 16, 14, 16));

        JPanel isiKartu = new JPanel();
        isiKartu.setOpaque(false);
        isiKartu.setLayout(new BoxLayout(isiKartu, BoxLayout.Y_AXIS));

        JLabel lblJudul = new JLabel("Ringkasan & Checkout");
        lblJudul.setFont(new Font(TemaUI.FONT_UI, Font.BOLD, 16));
        lblJudul.setForeground(TemaUI.WARNA_TEKS_UTAMA);
        lblJudul.setAlignmentX(LEFT_ALIGNMENT);

        this.lblRingkasanKeranjang.setAlignmentX(LEFT_ALIGNMENT);
        this.lblRingkasanKeranjang.setBorder(BorderFactory.createEmptyBorder(2, 0, 10, 0));

        JPanel kotakTotal = new JPanel(new BorderLayout(0, 4));
        kotakTotal.setBackground(TemaUI.WARNA_KARTU_KREM);
        kotakTotal.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(TemaUI.WARNA_BORDER, 2),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        kotakTotal.setAlignmentX(LEFT_ALIGNMENT);
        kotakTotal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));

        JLabel lblKetTotal = new JLabel("Estimasi Total Sewa");
        lblKetTotal.setFont(new Font(TemaUI.FONT_UI, Font.BOLD, 11));
        lblKetTotal.setForeground(TemaUI.WARNA_TEKS_SEKUNDER);

        this.lblEstimasiTotal.setFont(new Font(TemaUI.FONT_UI, Font.BOLD, 22));
        this.lblEstimasiTotal.setForeground(TemaUI.WARNA_AKSEN);

        kotakTotal.add(lblKetTotal, BorderLayout.NORTH);
        kotakTotal.add(this.lblEstimasiTotal, BorderLayout.SOUTH);

        this.lblRincianCheckout.setAlignmentX(LEFT_ALIGNMENT);
        this.lblRincianCheckout.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        JPanel kartuItemKeranjang = new JPanel(new BorderLayout(10, 0));
        kartuItemKeranjang.setBackground(TemaUI.WARNA_KARTU_KREM);
        kartuItemKeranjang.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(TemaUI.WARNA_BORDER, 2),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        kartuItemKeranjang.setAlignmentX(LEFT_ALIGNMENT);
        kartuItemKeranjang.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        kartuItemKeranjang.add(this.lblKeranjangFoto, BorderLayout.WEST);
        kartuItemKeranjang.add(this.lblKeranjangInfo, BorderLayout.CENTER);

        JLabel lblLama = new JLabel("Lama Sewa (hari)");
        lblLama.setFont(new Font(TemaUI.FONT_UI, Font.BOLD, 12));
        lblLama.setForeground(TemaUI.WARNA_TEKS_UTAMA);
        lblLama.setAlignmentX(LEFT_ALIGNMENT);

        this.spnLamaSewa.setAlignmentX(LEFT_ALIGNMENT);
        this.spnLamaSewa.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        JLabel lblMetode = new JLabel("Metode Pembayaran");
        lblMetode.setFont(new Font(TemaUI.FONT_UI, Font.BOLD, 12));
        lblMetode.setForeground(TemaUI.WARNA_TEKS_UTAMA);
        lblMetode.setAlignmentX(LEFT_ALIGNMENT);

        this.cmbPembayaran.setAlignmentX(LEFT_ALIGNMENT);
        this.cmbPembayaran.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        isiKartu.add(lblJudul);
        isiKartu.add(this.lblRingkasanKeranjang);
        isiKartu.add(kotakTotal);
        isiKartu.add(this.lblRincianCheckout);
        isiKartu.add(Box.createVerticalStrut(6));
        isiKartu.add(kartuItemKeranjang);
        isiKartu.add(Box.createVerticalStrut(8));
        isiKartu.add(lblLama);
        isiKartu.add(Box.createVerticalStrut(4));
        isiKartu.add(this.spnLamaSewa);
        isiKartu.add(Box.createVerticalStrut(8));
        isiKartu.add(lblMetode);
        isiKartu.add(Box.createVerticalStrut(4));
        isiKartu.add(this.cmbPembayaran);

        JPanel panelTombol = new JPanel(new GridLayout(2, 1, 0, 8));
        panelTombol.setOpaque(false);

        JButton btnHapus = TemaUI.buatTombolBahaya("Hapus Item Terpilih");
        btnHapus.setPreferredSize(new Dimension(0, 36));
        btnHapus.addActionListener(event -> this.aksiHapusKeranjang());

        JButton btnBuatTransaksi = TemaUI.buatTombolPrimer("Buat Transaksi");
        btnBuatTransaksi.setPreferredSize(new Dimension(0, 40));
        btnBuatTransaksi.addActionListener(event -> this.aksiBuatTransaksi());

        panelTombol.add(btnHapus);
        panelTombol.add(btnBuatTransaksi);

        kartuCheckout.add(isiKartu, BorderLayout.NORTH);
        kartuCheckout.add(panelTombol, BorderLayout.SOUTH);

        panel.add(kartuCheckout, BorderLayout.EAST);
        return panel;
    }

    /**
     * Membuat panel tab Riwayat beserta ringkasan statistik, grafik metode pembayaran,
     * dan tombol Lihat Invoice.
     *
     * @return panel tab Riwayat & Statistik
     */
    private JPanel buatPanelRiwayat()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(TemaUI.WARNA_CANVAS);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JPanel panelAtas = new JPanel(new BorderLayout(16, 0));
        panelAtas.setOpaque(false);
        panelAtas.setPreferredSize(new Dimension(0, 260));

        JPanel kolomStat = new JPanel(new GridLayout(3, 1, 0, 8));
        kolomStat.setOpaque(false);
        kolomStat.setPreferredSize(new Dimension(280, 0));
        kolomStat.add(TemaUI.buatKartuStatistik(
            "TOTAL TRANSAKSI", this.lblStatTotalTrx, this.lblStatTotalTrxSub
        ));
        kolomStat.add(TemaUI.buatKartuStatistik(
            "TRANSAKSI AKTIF", this.lblStatAktif, this.lblStatAktifSub
        ));
        kolomStat.add(TemaUI.buatKartuStatistik(
            "TOTAL BIAYA SEWA", this.lblStatTotalBelanja, this.lblStatTotalBelanjaSub
        ));

        panelAtas.add(kolomStat, BorderLayout.WEST);
        panelAtas.add(this.grafikMetodeBayar, BorderLayout.CENTER);

        panel.add(panelAtas, BorderLayout.NORTH);
        panel.add(TemaUI.buatScrollTabel(this.tabelRiwayat), BorderLayout.CENTER);

        JPanel panelBawah = new JPanel(new BorderLayout());
        panelBawah.setOpaque(false);
        panelBawah.add(this.lblRingkasanRiwayat, BorderLayout.WEST);

        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelTombol.setOpaque(false);

        JButton btnBatalkan = TemaUI.buatTombolBahaya("Batalkan Pesanan");
        btnBatalkan.addActionListener(event -> this.aksiBatalkanTransaksi());

        JButton btnInvoice = TemaUI.buatTombolPrimer("Lihat Invoice");
        btnInvoice.addActionListener(event -> this.aksiLihatInvoice());

        panelTombol.add(btnBatalkan);
        panelTombol.add(btnInvoice);
        panelBawah.add(panelTombol, BorderLayout.EAST);
        panel.add(panelBawah, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Memperbarui kartu showcase di kanan tab Daftar Barang mengikuti baris yang dipilih.
     */
    private void perbaruiShowcaseBarang()
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
            this.btnTambahShowcase.setText("Tambah ke Keranjang");
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

            boolean sudahDiKeranjang = false;
            for (Barang item : this.penyewa.getKeranjang().getBarang()) {
                if (item.getId().equalsIgnoreCase(barang.getId())) {
                    sudahDiKeranjang = true;
                    break;
                }
            }
            if (sudahDiKeranjang) {
                this.btnTambahShowcase.setText("Sudah di Keranjang");
            } else {
                this.btnTambahShowcase.setText("Tambah ke Keranjang");
            }
        } catch (DataTidakDitemukanException ignored) {
            this.lblDetailFoto.setIcon(null);
        }
    }

    /**
     * Memperbarui kartu pratinjau item terpilih di dalam tab Keranjang.
     */
    private void perbaruiPreviewKeranjang()
    {
        int baris = this.tabelKeranjang.getSelectedRow();
        if (baris < 0 || baris >= this.modelKeranjang.getRowCount()) {
            this.lblKeranjangFoto.setIcon(null);
            this.lblKeranjangInfo.setText("Keranjang kosong");
            return;
        }

        String idBarang = String.valueOf(this.modelKeranjang.getValueAt(baris, 1));
        for (Barang item : this.penyewa.getKeranjang().getBarang()) {
            if (item.getId().equalsIgnoreCase(idBarang)) {
                this.lblKeranjangFoto.setIcon(TemaUI.muatGambarBarang(item, 46));
                int lamaSewa = (Integer) this.spnLamaSewa.getValue();
                this.lblKeranjangInfo.setText(
                    "<html><b>" + item.getNama() + "</b><br>"
                        + item.getClass().getSimpleName() + " | "
                        + TemaUI.formatRupiah(item.hitungBiaya(lamaSewa)) + "</html>"
                );
                return;
            }
        }
    }

    /**
     * Memperbarui rincian jatuh tempo dan metode pembayaran secara live pada kartu checkout.
     */
    private void perbaruiRingkasanCheckout()
    {
        int lamaSewa = (Integer) this.spnLamaSewa.getValue();
        LocalDate jatuhTempo = LocalDate.now().plusDays(lamaSewa);
        String metode = String.valueOf(this.cmbPembayaran.getSelectedItem());
        this.lblRincianCheckout.setText(
            "Jatuh tempo: " + jatuhTempo + " | Metode: " + metode
        );
    }

    /**
     * Memperbarui seluruh data pada ketiga tabel penyewa sekaligus.
     */
    private void refreshSemuaTabel()
    {
        this.tampilkanBarang();
        this.tampilkanKeranjang();
        this.tampilkanRiwayat();
        this.perbaruiTampilanWallet();
    }

    /**
     * Membuat label keterangan kolom kiri pada kotak rincian harga.
     *
     * @param teks teks keterangan
     * @return objek JLabel terformat
     */
    private JLabel buatLabelKeteranganDetail(String teks)
    {
        JLabel label = new JLabel(teks);
        label.setFont(new Font(TemaUI.FONT_UI, Font.PLAIN, 12));
        label.setForeground(TemaUI.WARNA_TEKS_SEKUNDER);
        return label;
    }

    /**
     * Memformat label nilai kolom kanan pada kotak rincian harga.
     *
     * @param label komponen label target
     * @return komponen label yang sudah diformat rata kanan dan tebal
     */
    private JLabel formatLabelNilaiDetail(JLabel label)
    {
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setFont(new Font(TemaUI.FONT_UI, Font.BOLD, 12));
        label.setForeground(TemaUI.WARNA_TEKS_UTAMA);
        return label;
    }

    /**
     * Membuat instansiasi objek strategi Pembayaran sesuai pilihan pada combo box.
     *
     * @return objek implementasi Pembayaran
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
        if ("Saldo Wallet".equals(metode)) {
            return new Wallet();
        }
        return new Tunai();
    }

    /**
     * Membuat Panel Wallet untuk menampilkan saldo wallet penyewa dan tombol Top Up.
     *
     * @return panel kartu wallet
     */
    private JPanel buatPanelWallet()
    {
        JPanel panelWallet = new JPanel(new BorderLayout(16, 0));
        panelWallet.setBackground(TemaUI.WARNA_KARTU_KREM);
        panelWallet.setBorder(TemaUI.buatBorderKartuPixel(12, 16, 12, 16));

        JPanel kiri = new JPanel();
        kiri.setOpaque(false);
        kiri.setLayout(new BoxLayout(kiri, BoxLayout.Y_AXIS));

        JLabel lblJudul = new JLabel("SALDO WALLET");
        lblJudul.setFont(new Font(TemaUI.FONT_UI, Font.BOLD, 11));
        lblJudul.setForeground(TemaUI.WARNA_TEKS_SEKUNDER);
        lblJudul.setAlignmentX(LEFT_ALIGNMENT);

        this.lblSaldoWallet.setFont(new Font(TemaUI.FONT_UI, Font.BOLD, 20));
        this.lblSaldoWallet.setForeground(TemaUI.WARNA_AKSEN);
        this.lblSaldoWallet.setAlignmentX(LEFT_ALIGNMENT);
        this.lblSaldoWallet.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel lblInfo = new JLabel(
            "Saldo tersimpan permanen di akun dan siap digunakan untuk checkout instan."
        );
        lblInfo.setFont(new Font(TemaUI.FONT_UI, Font.PLAIN, 12));
        lblInfo.setForeground(TemaUI.WARNA_TEKS_SEKUNDER);
        lblInfo.setAlignmentX(LEFT_ALIGNMENT);

        kiri.add(lblJudul);
        kiri.add(Box.createVerticalStrut(2));
        kiri.add(this.lblSaldoWallet);
        kiri.add(Box.createVerticalStrut(2));
        kiri.add(lblInfo);

        JPanel kanan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        kanan.setOpaque(false);
        JButton btnTopUp = TemaUI.buatTombolPrimer("+ Top Up Wallet");
        btnTopUp.setPreferredSize(new Dimension(150, 36));
        btnTopUp.addActionListener(event -> this.bukaDialogTopUpWallet());
        kanan.add(btnTopUp);

        panelWallet.add(kiri, BorderLayout.CENTER);
        panelWallet.add(kanan, BorderLayout.EAST);
        return panelWallet;
    }

    /**
     * Membuka dialog Top Up Saldo Wallet melalui pilihan jalur pembayaran
     * (QRIS, Transfer Bank, atau Tunai) dan konfirmasi pembayaran sebelum saldo bertambah.
     */
    private void bukaDialogTopUpWallet()
    {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JLabel lblSaldoSekarang = new JLabel(
            "Saldo Saat Ini: " + TemaUI.formatRupiah(this.penyewa.getSaldoWallet())
        );
        lblSaldoSekarang.setFont(new Font(TemaUI.FONT_UI, Font.BOLD, 13));
        lblSaldoSekarang.setAlignmentX(LEFT_ALIGNMENT);

        JTextField txtNominal = new JTextField("100000", 16);
        TemaUI.formatInputField(txtNominal);
        txtNominal.setAlignmentX(LEFT_ALIGNMENT);

        JPanel barisPreset = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        barisPreset.setOpaque(false);
        barisPreset.setAlignmentX(LEFT_ALIGNMENT);
        JButton btn50 = TemaUI.buatTombolSekunder("+50rb");
        btn50.addActionListener(event -> txtNominal.setText("50000"));
        JButton btn100 = TemaUI.buatTombolSekunder("+100rb");
        btn100.addActionListener(event -> txtNominal.setText("100000"));
        JButton btn250 = TemaUI.buatTombolSekunder("+250rb");
        btn250.addActionListener(event -> txtNominal.setText("250000"));
        barisPreset.add(btn50);
        barisPreset.add(btn100);
        barisPreset.add(btn250);

        JComboBox<String> cmbJalurTopUp = new JComboBox<>(new String[] {
            "QRIS", "Transfer Bank", "Tunai"
        });
        cmbJalurTopUp.setFont(new Font(TemaUI.FONT_UI, Font.PLAIN, 12));
        cmbJalurTopUp.setAlignmentX(LEFT_ALIGNMENT);
        cmbJalurTopUp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        form.add(lblSaldoSekarang);
        form.add(Box.createVerticalStrut(10));
        form.add(new JLabel("Nominal Top Up (Rp):"));
        form.add(Box.createVerticalStrut(4));
        form.add(txtNominal);
        form.add(Box.createVerticalStrut(8));
        form.add(barisPreset);
        form.add(Box.createVerticalStrut(10));
        form.add(new JLabel("Jalur Pembayaran Top Up:"));
        form.add(Box.createVerticalStrut(4));
        form.add(cmbJalurTopUp);

        int hasil = JOptionPane.showConfirmDialog(
            this,
            form,
            "Top Up Saldo Wallet",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        if (hasil != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            double nominal = Double.parseDouble(txtNominal.getText().trim());
            if (!Double.isFinite(nominal) || nominal <= 0) {
                throw new IllegalArgumentException("Nominal top up harus lebih dari 0.");
            }

            String jalurTopUp = String.valueOf(cmbJalurTopUp.getSelectedItem());
            boolean terkonfirmasi = TemaUI.tampilkanDialogProsesPembayaran(
                this,
                jalurTopUp,
                nominal,
                0,
                0
            );
            if (!terkonfirmasi) {
                return;
            }

            Pembayaran metodeBayar = "QRIS".equals(jalurTopUp)
                ? new QRIS()
                : ("Transfer Bank".equals(jalurTopUp) ? new TransferBank() : new Tunai());
            String buktiBayar = metodeBayar.prosesPembayaran(nominal);

            this.penyewa.topUpWallet(nominal);
            this.perbaruiTampilanWallet();
            this.simpanPerubahan();
            JOptionPane.showMessageDialog(
                this,
                "Top Up berhasil sebesar " + TemaUI.formatRupiah(nominal)
                    + ".\n" + buktiBayar
                    + "\nSaldo Wallet kini: "
                    + TemaUI.formatRupiah(this.penyewa.getSaldoWallet()),
                "Top Up Berhasil",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(
                this,
                "Nominal top up harus berupa angka yang valid.",
                "Input Tidak Valid",
                JOptionPane.ERROR_MESSAGE
            );
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(
                this,
                exception.getMessage(),
                "Top Up Gagal",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Memperbarui teks label saldo wallet sesuai nilai saldo penyewa saat ini.
     */
    private void perbaruiTampilanWallet()
    {
        if (this.lblSaldoWallet != null) {
            this.lblSaldoWallet.setText(TemaUI.formatRupiah(this.penyewa.getSaldoWallet()));
        }
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
     * Mencari objek Transaksi di daftar riwayat penyewa berdasarkan ID transaksi.
     *
     * @param idTransaksi kode unik transaksi yang dicari
     * @return objek transaksi jika ditemukan, atau null jika tidak ada
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
