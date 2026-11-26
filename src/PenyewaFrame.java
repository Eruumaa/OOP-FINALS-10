import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.NumberFormat;
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
 * Antarmuka grafis untuk akun penyewa pada sistem BoardGameRent.
 *
 * @author Irvan Almidar
 * @version 1.0
 */
public class PenyewaFrame extends JFrame
{
    private static final long serialVersionUID = 1L;
    private static final Color WARNA_HEADER = new Color(30, 41, 59);
    private static final Color WARNA_LATAR = new Color(248, 250, 252);

    private Penyewa penyewa;
    private Admin admin;
    private Runnable onLogout;
    private JTable tabelBarang;
    private JTable tabelKeranjang;
    private JTable tabelRiwayat;
    private DefaultTableModel modelBarang;
    private DefaultTableModel modelKeranjang;
    private DefaultTableModel modelRiwayat;
    private JTextField txtLamaSewa;
    private JComboBox<String> cmbPembayaran;
    private JTabbedPane tabbedPane;

    /**
     * Membuat jendela utama penyewa.
     *
     * @param penyewa objek penyewa yang sedang login
     */
public PenyewaFrame(Penyewa penyewa, Admin admin)
{
    this(penyewa, admin, null);
}

/**
 * Membuat jendela penyewa dengan callback logout.
 *
 * @param penyewa akun penyewa
 * @param admin akun administrator
 * @param onLogout aksi setelah logout
 */
public PenyewaFrame(
    Penyewa penyewa,
    Admin admin,
    Runnable onLogout
)
{
    super("BoardGameRent - Panel Penyewa");

    if (penyewa == null || admin == null) {
        throw new IllegalArgumentException(
            "Objek penyewa dan admin tidak boleh null."
        );
    }

    this.penyewa = penyewa;
    this.admin = admin;
    this.onLogout = onLogout;

    this.modelBarang = new TabelModelReadOnly(new String[] {
        "ID", "Nama Board Game", "Kategori", "Tarif/Hari", "Status"
    });

    this.modelKeranjang = new TabelModelReadOnly(new String[] {
        "ID", "Nama Board Game", "Tarif/Hari"
    });

    this.modelRiwayat = new TabelModelReadOnly(new String[] {
        "ID Transaksi", "Daftar Barang", "Lama Sewa", "Total Biaya", "Status"
    });

    this.tabelBarang = this.buatTabel(this.modelBarang);
    this.tabelKeranjang = this.buatTabel(this.modelKeranjang);
    this.tabelRiwayat = this.buatTabel(this.modelRiwayat);

    this.txtLamaSewa = new JTextField("1", 5);

    this.cmbPembayaran = new JComboBox<>(new String[] {
        "QRIS", "Transfer Bank", "Tunai"
    });

    this.tabbedPane = new JTabbedPane();

    this.inisialisasiTampilan();
    this.refreshSemuaTabel();
}

    /**
     * Memuat katalog barang yang tersedia.
     */
    public void tampilkanBarang()
    {
        this.modelBarang.setRowCount(0);

        ArrayList<Barang> daftar = this.penyewa.lihatBarang();
        for (Barang barang : daftar) {
            this.modelBarang.addRow(new Object[] {
                barang.getId(),
                barang.getNama(),
                barang.getClass().getSimpleName(),
                this.formatRupiah(barang.getTarifHarian()),
                barang.isTersedia() ? "Tersedia" : "Sedang Disewa"
            });
        }
    }

    /**
     * Memuat isi keranjang penyewa.
     */
    public void tampilkanKeranjang()
    {
        this.modelKeranjang.setRowCount(0);

        ArrayList<Barang> daftar = this.penyewa.getKeranjang().getBarang();
        for (Barang barang : daftar) {
            this.modelKeranjang.addRow(new Object[] {
                barang.getId(),
                barang.getNama(),
                this.formatRupiah(barang.getTarifHarian())
            });
        }
    }

    /**
     * Memuat riwayat transaksi penyewa.
     */
    public void tampilkanRiwayat()
    {
        this.modelRiwayat.setRowCount(0);

        ArrayList<Transaksi> daftar = this.penyewa.lihatRiwayat();
        for (Transaksi transaksi : daftar) {
            this.modelRiwayat.addRow(new Object[] {
                transaksi.getId(),
                this.formatDaftarBarang(transaksi.getBarang()),
                transaksi.getLamaSewa() + " hari",
                this.formatRupiah(transaksi.hitungTotal()),
                transaksi.getStatus()
            });
        }
    }

    /**
     * Menambahkan barang yang dipilih dari katalog ke keranjang.
     */
    public void aksiTambahKeranjang()
    {
        int baris = this.tabelBarang.getSelectedRow();

        if (baris < 0) {
            JOptionPane.showMessageDialog(
                this,
                "Pilih barang yang ingin ditambahkan ke keranjang.",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String idBarang = String.valueOf(this.modelBarang.getValueAt(baris, 0));

        try {
            Barang barang = this.penyewa.lihatBarang().stream()
                .filter(item -> item.getId().equalsIgnoreCase(idBarang))
                .findFirst()
                .orElseThrow(() -> new DataTidakDitemukanException(
                    "Barang tidak ditemukan atau sedang tidak tersedia."
                ));

            this.penyewa.getKeranjang().tambahBarang(barang);
            this.refreshSemuaTabel();

            JOptionPane.showMessageDialog(
                this,
                barang.getNama() + " berhasil ditambahkan ke keranjang.",
                "Berhasil",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (BarangTidakTersediaException | IllegalArgumentException
                 | DataTidakDitemukanException exception) {
            JOptionPane.showMessageDialog(
                this,
                exception.getMessage(),
                "Gagal Menambahkan Barang",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Menghapus barang yang dipilih dari keranjang.
     */
    public void aksiHapusKeranjang()
    {
        int baris = this.tabelKeranjang.getSelectedRow();

        if (baris < 0) {
            JOptionPane.showMessageDialog(
                this,
                "Pilih barang yang ingin dihapus dari keranjang.",
                "Peringatan",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String idBarang = String.valueOf(this.modelKeranjang.getValueAt(baris, 0));

        try {
            this.penyewa.getKeranjang().hapusBarang(idBarang);
            this.refreshSemuaTabel();

            JOptionPane.showMessageDialog(
                this,
                "Barang berhasil dihapus dari keranjang.",
                "Berhasil",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(
                this,
                exception.getMessage(),
                "Gagal Menghapus Barang",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Membuat transaksi penyewaan berdasarkan isi keranjang.
     */
    public void aksiCheckout()
    {
        if (this.penyewa.getKeranjang().getBarang().isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Keranjang masih kosong. Tambahkan barang terlebih dahulu.",
                "Checkout",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            int lamaSewa = Integer.parseInt(this.txtLamaSewa.getText().trim());

            if (lamaSewa <= 0) {
                throw new IllegalArgumentException("Lama sewa harus lebih dari 0 hari.");
            }

            Pembayaran pembayaran;
            String metode = (String) this.cmbPembayaran.getSelectedItem();

            if ("QRIS".equals(metode)) {
                pembayaran = new QRIS();
            } else if ("Transfer Bank".equals(metode)) {
                pembayaran = new TransferBank();
            } else {
                pembayaran = new Tunai();
            }

Transaksi transaksi = this.penyewa.buatTransaksi(lamaSewa, pembayaran);

// Mendaftarkan transaksi ke daftar transaksi Admin.
this.admin.tambahTransaksi(transaksi);

this.refreshSemuaTabel();

            JOptionPane.showMessageDialog(
                this,
                "Transaksi berhasil dibuat.\n"
                    + "ID Transaksi: " + transaksi.getId()
                    + "\nTotal Biaya: " + this.formatRupiah(transaksi.hitungTotal())
                    + "\nStatus: " + transaksi.getStatus(),
                "Checkout Berhasil",
                JOptionPane.INFORMATION_MESSAGE
            );

            this.tabbedPane.setSelectedIndex(2);
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(
                this,
                "Lama sewa harus berupa angka.",
                "Input Tidak Valid",
                JOptionPane.ERROR_MESSAGE
            );
        } catch (IllegalArgumentException | IllegalStateException exception) {
            JOptionPane.showMessageDialog(
                this,
                exception.getMessage(),
                "Checkout Gagal",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Mengembalikan objek penyewa yang sedang aktif.
     *
     * @return objek penyewa
     */
    public Penyewa getPenyewa()
    {
        return this.penyewa;
    }

    private void inisialisasiTampilan()
    {
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setMinimumSize(new Dimension(820, 520));
        this.setSize(940, 600);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(WARNA_LATAR);

        this.add(this.buatPanelHeader(), BorderLayout.NORTH);

        this.tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));
        this.tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 14, 14, 14));
        this.tabbedPane.addTab("Katalog Barang", this.buatPanelBarang());
        this.tabbedPane.addTab("Keranjang", this.buatPanelKeranjang());
        this.tabbedPane.addTab("Riwayat Transaksi", this.buatPanelRiwayat());
        this.tabbedPane.addChangeListener(event -> this.refreshSemuaTabel());

        this.add(this.tabbedPane, BorderLayout.CENTER);
    }

    private JPanel buatPanelHeader()
    {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(WARNA_HEADER);
        header.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JPanel info = new JPanel(new BorderLayout());
        info.setOpaque(false);

        JLabel judul = new JLabel("BoardGameRent - Panel Penyewa");
        judul.setFont(new Font("SansSerif", Font.BOLD, 18));
        judul.setForeground(Color.WHITE);

        JLabel subJudul = new JLabel("Login sebagai: " + this.penyewa.getUsername());
        subJudul.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subJudul.setForeground(new Color(203, 213, 225));

        info.add(judul, BorderLayout.NORTH);
        info.add(subJudul, BorderLayout.SOUTH);

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tombol.setOpaque(false);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(event -> this.refreshSemuaTabel());

        JButton btnLogout = new JButton("Logout");
btnLogout.addActionListener(event -> {
    this.dispose();

    if (this.onLogout != null) {
        this.onLogout.run();
    }
});

        tombol.add(btnRefresh);
        tombol.add(btnLogout);

        header.add(info, BorderLayout.WEST);
        header.add(tombol, BorderLayout.EAST);

        return header;
    }

    private JPanel buatPanelBarang()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(WARNA_LATAR);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        panel.add(new JScrollPane(this.tabelBarang), BorderLayout.CENTER);

        JPanel bawah = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bawah.setOpaque(false);

        JButton btnTambah = new JButton("Tambah ke Keranjang");
        btnTambah.addActionListener(event -> this.aksiTambahKeranjang());
        bawah.add(btnTambah);

        panel.add(bawah, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buatPanelKeranjang()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(WARNA_LATAR);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        panel.add(new JScrollPane(this.tabelKeranjang), BorderLayout.CENTER);

        JPanel bawah = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bawah.setOpaque(false);

        bawah.add(new JLabel("Lama Sewa (hari):"));
        bawah.add(this.txtLamaSewa);
        bawah.add(new JLabel("Metode Pembayaran:"));
        bawah.add(this.cmbPembayaran);

        JButton btnHapus = new JButton("Hapus Barang");
        btnHapus.addActionListener(event -> this.aksiHapusKeranjang());

        JButton btnCheckout = new JButton("Checkout");
        btnCheckout.addActionListener(event -> this.aksiCheckout());

        bawah.add(btnHapus);
        bawah.add(btnCheckout);

        panel.add(bawah, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buatPanelRiwayat()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(WARNA_LATAR);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        panel.add(new JScrollPane(this.tabelRiwayat), BorderLayout.CENTER);
        return panel;
    }

    private JTable buatTabel(DefaultTableModel model)
    {
        JTable tabel = new JTable(model);
        tabel.setRowHeight(26);
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabel.setFillsViewportHeight(true);
        tabel.getTableHeader().setReorderingAllowed(false);
        tabel.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        return tabel;
    }

    private void refreshSemuaTabel()
    {
        this.tampilkanBarang();
        this.tampilkanKeranjang();
        this.tampilkanRiwayat();
    }

    private String formatDaftarBarang(ArrayList<Barang> daftar)
    {
        StringBuilder hasil = new StringBuilder();

        for (int i = 0; i < daftar.size(); i++) {
            if (i > 0) {
                hasil.append(", ");
            }
            hasil.append(daftar.get(i).getNama());
        }

        return hasil.toString();
    }

    private String formatRupiah(double nominal)
    {
        NumberFormat format = NumberFormat.getCurrencyInstance(
            Locale.forLanguageTag("id-ID")
        );
        return format.format(nominal);
    }

    /**
     * Model tabel yang tidak dapat diedit langsung oleh pengguna.
     */
    private static class TabelModelReadOnly extends DefaultTableModel
    {
        private static final long serialVersionUID = 1L;

        public TabelModelReadOnly(Object[] kolom)
        {
            super(kolom, 0);
        }

        @Override
        public boolean isCellEditable(int row, int column)
        {
            return false;
        }
    }
}
