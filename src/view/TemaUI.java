package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import model.Barang;

/**
 * Kelas utilitas bersama yang menyediakan palet warna, tipografi, renderer tabel,
 * dan pabrik komponen Swing bertema Warm Tabletop untuk seluruh jendela GUI.
 *
 * @author Muhammad Aqil Mubarak
 * @version 2026.11.27
 */
public final class TemaUI
{
    /** Warna latar belakang utama kanvas aplikasi. */
    public static final Color WARNA_CANVAS = new Color(245, 243, 238);
    /** Warna permukaan bersih untuk kartu, dialog, dan sel tabel. */
    public static final Color WARNA_SURFACE = new Color(255, 255, 255);
    /** Warna latar bilah header bagian atas jendela. */
    public static final Color WARNA_HEADER = new Color(28, 25, 23);
    /** Warna teks sekunder pada bilah header gelap. */
    public static final Color WARNA_HEADER_SUB = new Color(214, 211, 205);
    /** Warna latar datar untuk baris judul kolom tabel. */
    public static final Color WARNA_HEADER_TABEL = new Color(239, 236, 230);
    /** Warna teks utama berkontras tinggi. */
    public static final Color WARNA_TEKS_UTAMA = new Color(28, 25, 23);
    /** Warna teks sekunder untuk metadata dan ringkasan. */
    public static final Color WARNA_TEKS_SEKUNDER = new Color(87, 83, 78);
    /** Warna garis tepi panel, kartu, dan input. */
    public static final Color WARNA_BORDER = new Color(221, 216, 206);
    /** Warna garis pembatas baris tabel yang halus. */
    public static final Color WARNA_GRID_TABEL = new Color(234, 229, 220);
    /** Warna sorotan lembut untuk baris tabel yang sedang dipilih. */
    public static final Color WARNA_SELEKSI = new Color(254, 243, 199);
    /** Warna aksen utama terakota untuk tombol CTA primer. */
    public static final Color WARNA_AKSEN = new Color(180, 83, 9);
    /** Warna tepi gelap untuk tombol CTA primer. */
    public static final Color WARNA_AKSEN_GELAP = new Color(146, 64, 14);
    /** Warna status semantik positif (Tersedia / Selesai). */
    public static final Color WARNA_STATUS_AMAN = new Color(22, 101, 52);
    /** Warna status semantik menunggu konfirmasi. */
    public static final Color WARNA_STATUS_TUNGGU = new Color(180, 83, 9);
    /** Warna status semantik peringatan atau penolakan (Sedang Disewa / Ditolak). */
    public static final Color WARNA_STATUS_BAHAYA = new Color(153, 27, 27);
    /** Warna garis tepi untuk tombol aksi destruktif. */
    public static final Color WARNA_BORDER_BAHAYA = new Color(229, 197, 197);
    /** Warna latar tombol sekunder pada header gelap. */
    public static final Color WARNA_TOMBOL_HEADER = new Color(46, 42, 39);
    /** Warna garis tepi tombol sekunder pada header gelap. */
    public static final Color WARNA_BORDER_HEADER = new Color(87, 83, 78);
    /** Warna hover tombol primer terakota. */
    public static final Color WARNA_AKSEN_HOVER = new Color(146, 64, 14);
    /** Warna tekan tombol primer terakota. */
    public static final Color WARNA_AKSEN_TEKAN = new Color(120, 53, 15);
    /** Warna hover tombol sekunder netral. */
    public static final Color WARNA_SEKUNDER_HOVER = new Color(247, 245, 240);
    /** Warna tekan tombol sekunder netral. */
    public static final Color WARNA_SEKUNDER_TEKAN = new Color(239, 236, 230);
    /** Warna hover tombol bahaya. */
    public static final Color WARNA_BAHAYA_HOVER = new Color(254, 242, 242);
    /** Warna tekan tombol bahaya. */
    public static final Color WARNA_BAHAYA_TEKAN = new Color(254, 226, 226);
    /** Warna hover tombol header gelap. */
    public static final Color WARNA_TOMBOL_HEADER_HOVER = new Color(68, 64, 60);
    /** Warna tekan tombol header gelap. */
    public static final Color WARNA_TOMBOL_HEADER_TEKAN = new Color(28, 25, 23);

    /** Nama font utama antarmuka pengguna. */
    public static final String FONT_UI = "Segoe UI";
    /** Nama font monospace untuk tampilan struk invoice. */
    public static final String FONT_MONO = "Consolas";

    /**
     * Constructor privat karena seluruh anggota kelas bersifat statis.
     */
    private TemaUI()
    {
    }

    /**
     * Model tabel read-only agar sel pada {@link JTable} tidak dapat diubah langsung oleh pengguna.
     */
    public static class TabelModelReadOnly extends DefaultTableModel
    {
        private static final long serialVersionUID = 1L;

        /**
         * Membuat model tabel read-only dengan daftar judul kolom tertentu.
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
     * Renderer sel tabel dengan padding seragam, perataan horizontal terkendali,
     * dan pewarnaan status semantik yang konsisten.
     */
    public static class RendererSelTabel extends DefaultTableCellRenderer
    {
        private static final long serialVersionUID = 1L;

        /** Perataan horizontal teks di dalam sel. */
        private int perataan;
        /** Penanda apakah kolom ini menampilkan status semantik. */
        private boolean kolomStatus;

        /**
         * Membuat renderer sel tabel baru.
         *
         * @param perataan konstanta alignment dari {@link SwingConstants}
         * @param kolomStatus {@code true} jika kolom menampilkan teks status
         */
        public RendererSelTabel(int perataan, boolean kolomStatus)
        {
            this.perataan = perataan;
            this.kolomStatus = kolomStatus;
        }

        /**
         * Menyiapkan komponen tampilan sel sesuai nilai, status seleksi, dan jenis kolom.
         *
         * @param table tabel induk
         * @param value nilai data pada sel
         * @param isSelected {@code true} jika baris sedang dipilih
         * @param hasFocus {@code true} jika sel memiliki fokus
         * @param row indeks baris
         * @param column indeks kolom
         * @return komponen label terformat untuk ditampilkan pada sel
         */
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column)
        {
            Component comp = super.getTableCellRendererComponent(
                table, value, isSelected, false, row, column
            );
            this.setHorizontalAlignment(this.perataan);
            this.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

            if (isSelected) {
                comp.setBackground(WARNA_SELEKSI);
            } else {
                comp.setBackground(WARNA_SURFACE);
            }

            if (this.kolomStatus && value != null) {
                String teks = value.toString();
                this.setFont(new Font(FONT_UI, Font.BOLD, 12));
                if ("Tersedia".equalsIgnoreCase(teks) || "Selesai".equalsIgnoreCase(teks)) {
                    comp.setForeground(WARNA_STATUS_AMAN);
                } else if ("Menunggu Konfirmasi".equalsIgnoreCase(teks)) {
                    comp.setForeground(WARNA_STATUS_TUNGGU);
                } else {
                    comp.setForeground(WARNA_STATUS_BAHAYA);
                }
            } else {
                this.setFont(new Font(FONT_UI, Font.PLAIN, 13));
                comp.setForeground(WARNA_TEKS_UTAMA);
            }

            return comp;
        }
    }

    /**
     * Renderer header kolom tabel bergaya datar Warm Tabletop agar sejajar dengan isi kolom
     * dan tampil konsisten di seluruh Look and Feel.
     */
    public static class RendererHeaderTabel extends DefaultTableCellRenderer
    {
        private static final long serialVersionUID = 1L;

        /** Perataan horizontal teks judul kolom. */
        private int perataan;

        /**
         * Membuat renderer header kolom baru dengan perataan tertentu.
         *
         * @param perataan konstanta alignment dari {@link SwingConstants}
         */
        public RendererHeaderTabel(int perataan)
        {
            this.perataan = perataan;
        }

        /**
         * Menyiapkan tampilan label header kolom tabel.
         *
         * @param table tabel induk
         * @param value teks judul kolom
         * @param isSelected status seleksi
         * @param hasFocus status fokus
         * @param row indeks baris
         * @param column indeks kolom
         * @return komponen header terformat
         */
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column)
        {
            Component comp = super.getTableCellRendererComponent(
                table, value, false, false, row, column
            );
            this.setHorizontalAlignment(this.perataan);
            this.setFont(new Font(FONT_UI, Font.BOLD, 12));
            this.setOpaque(true);
            comp.setBackground(WARNA_HEADER_TABEL);
            comp.setForeground(WARNA_TEKS_SEKUNDER);
            this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, WARNA_BORDER),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
            ));
            return comp;
        }
    }

    /**
     * Membuat panel bilah atas jendela aplikasi dengan judul, subjudul pengguna aktif,
     * tombol Refresh, dan tombol Logout.
     *
     * @param judul teks judul utama di sisi kiri header
     * @param subJudul teks subjudul di bawah judul utama
     * @param onRefresh aksi ketika tombol Refresh ditekan
     * @param onLogout aksi ketika tombol Logout ditekan
     * @return panel header terformat
     */
    public static JPanel buatPanelHeader(String judul, String subJudul,
                                         Runnable onRefresh, Runnable onLogout)
    {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(WARNA_HEADER);
        header.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JPanel infoKiri = new JPanel(new BorderLayout(0, 2));
        infoKiri.setOpaque(false);

        JLabel lblJudul = new JLabel(judul);
        lblJudul.setFont(new Font(FONT_UI, Font.BOLD, 18));
        lblJudul.setForeground(WARNA_SURFACE);

        JLabel lblSubJudul = new JLabel(subJudul);
        lblSubJudul.setFont(new Font(FONT_UI, Font.PLAIN, 12));
        lblSubJudul.setForeground(WARNA_HEADER_SUB);

        infoKiri.add(lblJudul, BorderLayout.NORTH);
        infoKiri.add(lblSubJudul, BorderLayout.SOUTH);

        JPanel aksiKanan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        aksiKanan.setOpaque(false);

        JButton btnRefresh = buatTombolHeader("Refresh");
        btnRefresh.addActionListener(event -> onRefresh.run());

        JButton btnLogout = buatTombolHeader("Logout");
        btnLogout.addActionListener(event -> onLogout.run());

        aksiKanan.add(btnRefresh);
        aksiKanan.add(btnLogout);

        header.add(infoKiri, BorderLayout.WEST);
        header.add(aksiKanan, BorderLayout.EAST);

        return header;
    }

    /**
     * Membuat komponen {@link JTable} standar bertema Warm Tabletop.
     *
     * @param model model data tabel
     * @return objek {@link JTable} yang telah dikonfigurasi
     */
    public static JTable buatTabelStandar(DefaultTableModel model)
    {
        JTable tabel = new JTable(model);
        tabel.setRowHeight(30);
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabel.setFillsViewportHeight(true);
        tabel.setBackground(WARNA_SURFACE);
        tabel.setForeground(WARNA_TEKS_UTAMA);
        tabel.setSelectionBackground(WARNA_SELEKSI);
        tabel.setSelectionForeground(WARNA_TEKS_UTAMA);
        tabel.setGridColor(WARNA_GRID_TABEL);
        tabel.setShowHorizontalLines(true);
        tabel.setShowVerticalLines(false);
        tabel.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = tabel.getTableHeader();
        header.setReorderingAllowed(false);
        header.setFont(new Font(FONT_UI, Font.BOLD, 12));
        header.setBackground(WARNA_HEADER_TABEL);
        header.setForeground(WARNA_TEKS_SEKUNDER);
        header.setPreferredSize(new Dimension(0, 32));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, WARNA_BORDER));

        return tabel;
    }

    /**
     * Mengatur lebar kolom, perataan header, dan renderer sel suatu {@link JTable}.
     *
     * @param tabel tabel yang akan dikonfigurasi
     * @param lebar array lebar pilihan tiap kolom
     * @param rataKanan daftar indeks kolom yang diratakan kanan (angka/mata uang)
     * @param kolomStatus daftar indeks kolom yang menampilkan status semantik
     */
    public static void aturKolomTabel(JTable tabel, int[] lebar, int[] rataKanan, int[] kolomStatus)
    {
        RendererSelTabel kiri = new RendererSelTabel(SwingConstants.LEFT, false);
        RendererSelTabel kanan = new RendererSelTabel(SwingConstants.RIGHT, false);
        RendererSelTabel status = new RendererSelTabel(SwingConstants.LEFT, true);
        RendererHeaderTabel headerKiri = new RendererHeaderTabel(SwingConstants.LEFT);
        RendererHeaderTabel headerKanan = new RendererHeaderTabel(SwingConstants.RIGHT);

        TableColumnModel kolom = tabel.getColumnModel();
        for (int index = 0; index < lebar.length; index++) {
            kolom.getColumn(index).setPreferredWidth(lebar[index]);
            if (termasukIndeks(rataKanan, index)) {
                kolom.getColumn(index).setCellRenderer(kanan);
                kolom.getColumn(index).setHeaderRenderer(headerKanan);
            } else if (termasukIndeks(kolomStatus, index)) {
                kolom.getColumn(index).setCellRenderer(status);
                kolom.getColumn(index).setHeaderRenderer(headerKiri);
            } else {
                kolom.getColumn(index).setCellRenderer(kiri);
                kolom.getColumn(index).setHeaderRenderer(headerKiri);
            }
        }
    }

    /**
     * Membungkus {@link JTable} ke dalam {@link JScrollPane} dengan bingkai tipis.
     *
     * @param tabel tabel yang akan dibungkus
     * @return objek {@link JScrollPane} terformat
     */
    public static JScrollPane buatScrollTabel(JTable tabel)
    {
        JScrollPane scrollPane = new JScrollPane(tabel);
        scrollPane.setBorder(BorderFactory.createLineBorder(WARNA_BORDER));
        scrollPane.getViewport().setBackground(WARNA_SURFACE);
        return scrollPane;
    }

    /**
     * Membuat label ringkasan pada baris bawah panel tab.
     *
     * @param teks teks awal label
     * @return objek {@link JLabel} terformat
     */
    public static JLabel buatLabelRingkasan(String teks)
    {
        JLabel label = new JLabel(teks);
        label.setFont(new Font(FONT_UI, Font.PLAIN, 12));
        label.setForeground(WARNA_TEKS_SEKUNDER);
        label.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 8));
        return label;
    }

    /**
     * Memformat komponen {@link JTextField} dengan tipografi dan padding standar Warm Tabletop.
     *
     * @param field komponen input teks yang akan diformat
     */
    public static void formatInputField(JTextField field)
    {
        field.setFont(new Font(FONT_UI, Font.PLAIN, 13));
        field.setForeground(WARNA_TEKS_UTAMA);
        field.setBackground(WARNA_SURFACE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(WARNA_BORDER),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }

    /**
     * Membuat tombol aksi primer berwarna aksen terakota.
     *
     * @param teks label tombol
     * @return tombol primer terformat
     */
    public static JButton buatTombolPrimer(String teks)
    {
        return formatTombol(
            teks, WARNA_AKSEN, WARNA_AKSEN_HOVER, WARNA_AKSEN_TEKAN,
            WARNA_SURFACE, WARNA_AKSEN_GELAP, Font.BOLD
        );
    }

    /**
     * Membuat tombol aksi sekunder bernuansa netral.
     *
     * @param teks label tombol
     * @return tombol sekunder terformat
     */
    public static JButton buatTombolSekunder(String teks)
    {
        return formatTombol(
            teks, WARNA_SURFACE, WARNA_SEKUNDER_HOVER, WARNA_SEKUNDER_TEKAN,
            WARNA_TEKS_UTAMA, WARNA_BORDER, Font.PLAIN
        );
    }

    /**
     * Membuat tombol aksi destruktif berwarna merah bata.
     *
     * @param teks label tombol
     * @return tombol bahaya terformat
     */
    public static JButton buatTombolBahaya(String teks)
    {
        return formatTombol(
            teks, WARNA_SURFACE, WARNA_BAHAYA_HOVER, WARNA_BAHAYA_TEKAN,
            WARNA_STATUS_BAHAYA, WARNA_BORDER_BAHAYA, Font.PLAIN
        );
    }

    /**
     * Membuat tombol gelap untuk bilah header aplikasi.
     *
     * @param teks label tombol
     * @return tombol header terformat
     */
    public static JButton buatTombolHeader(String teks)
    {
        return formatTombol(
            teks, WARNA_TOMBOL_HEADER, WARNA_TOMBOL_HEADER_HOVER, WARNA_TOMBOL_HEADER_TEKAN,
            WARNA_SURFACE, WARNA_BORDER_HEADER, Font.PLAIN
        );
    }

    /**
     * Komponen {@link JButton} kustom yang menggambar latar sendiri agar warna tombol
     * tidak tertimpa gaya bawaan sistem operasi pada Windows Look and Feel.
     */
    private static class TombolTema extends JButton
    {
        private static final long serialVersionUID = 1L;

        /** Warna latar normal tombol. */
        private Color warnaNormal;
        /** Warna latar saat kursor berada di atas tombol. */
        private Color warnaHover;
        /** Warna latar saat tombol ditekan. */
        private Color warnaTekan;

        /**
         * Membuat tombol kustom dengan warna status interaksi yang terjamin konsisten.
         *
         * @param teks label tombol
         * @param warnaNormal warna latar normal
         * @param warnaHover warna latar hover
         * @param warnaTekan warna latar saat ditekan
         */
        public TombolTema(String teks, Color warnaNormal, Color warnaHover, Color warnaTekan)
        {
            super(teks);
            this.warnaNormal = warnaNormal;
            this.warnaHover = warnaHover;
            this.warnaTekan = warnaTekan;
            this.setOpaque(false);
            this.setContentAreaFilled(false);
            this.setRolloverEnabled(true);
            this.setFocusPainted(false);
        }

        /**
         * Menjaga agar tombol tetap memakai {@link BasicButtonUI} meski tema Look and Feel
         * diperbarui ulang pada komponen induk.
         */
        @Override
        public void updateUI()
        {
            this.setUI(new BasicButtonUI());
        }

        /**
         * Menggambar latar tombol sesuai status interaksi sebelum teks tombol dirender.
         *
         * @param g konteks grafis komponen
         */
        @Override
        protected void paintComponent(Graphics g)
        {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color latar = this.warnaNormal;
            if (!this.isEnabled()) {
                latar = WARNA_HEADER_TABEL;
            } else if (this.getModel().isPressed()) {
                latar = this.warnaTekan;
            } else if (this.getModel().isRollover()) {
                latar = this.warnaHover;
            }

            g2.setColor(latar);
            g2.fillRect(0, 0, this.getWidth(), this.getHeight());
            g2.dispose();

            super.paintComponent(g);
        }
    }

    /**
     * Memformat nominal uang ke dalam format mata uang rupiah Indonesia.
     *
     * @param nominal jumlah uang dalam rupiah
     * @return teks terformat rupiah
     */
    public static String formatRupiah(double nominal)
    {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
        return format.format(nominal);
    }

    /**
     * Merangkum daftar nama barang dalam satu koleksi menjadi teks terpisah koma.
     *
     * @param daftarBarang daftar barang
     * @return teks daftar nama barang
     */
    public static String formatDaftarBarang(ArrayList<Barang> daftarBarang)
    {
        StringBuilder hasil = new StringBuilder();
        for (int index = 0; index < daftarBarang.size(); index++) {
            if (index > 0) {
                hasil.append(", ");
            }
            hasil.append(daftarBarang.get(index).getNama());
        }
        return hasil.toString();
    }

    /**
     * Memformat tombol dengan warna, border, font, dan kursor yang konsisten.
     *
     * @param teks label tombol
     * @param warnaLatar warna latar normal tombol
     * @param warnaHover warna latar saat kursor hover
     * @param warnaTekan warna latar saat tombol ditekan
     * @param warnaTeks warna teks tombol
     * @param warnaBorder warna garis tepi tombol
     * @param gayaFont gaya huruf
     * @return tombol terformat
     */
    private static JButton formatTombol(String teks, Color warnaLatar, Color warnaHover,
                                        Color warnaTekan, Color warnaTeks,
                                        Color warnaBorder, int gayaFont)
    {
        JButton tombol = new TombolTema(teks, warnaLatar, warnaHover, warnaTekan);
        tombol.setFont(new Font(FONT_UI, gayaFont, 12));
        tombol.setBackground(warnaLatar);
        tombol.setForeground(warnaTeks);
        tombol.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tombol.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(warnaBorder),
            BorderFactory.createEmptyBorder(7, 14, 7, 14)
        ));
        return tombol;
    }

    /**
     * Memeriksa apakah suatu indeks terdapat pada array target.
     *
     * @param daftarIndeks array indeks
     * @param target indeks yang diperiksa
     * @return {@code true} jika ditemukan, {@code false} jika tidak
     */
    private static boolean termasukIndeks(int[] daftarIndeks, int target)
    {
        for (int nilai : daftarIndeks) {
            if (nilai == target) {
                return true;
            }
        }
        return false;
    }
}
