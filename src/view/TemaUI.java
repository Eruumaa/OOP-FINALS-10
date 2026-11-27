package view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import model.Barang;
import model.FamilyBoardGame;
import model.Invoice;
import model.StrategyGame;
import model.Transaksi;

/**
 * Kelas utilitas bersama yang menyediakan palet warna, tipografi, pemuat foto board game,
 * grafik analitik, renderer tabel, dan komponen Swing bertema Cozy Pixel Tabletop.
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
    /** Warna permukaan krem hangat untuk kartu detail. */
    public static final Color WARNA_KARTU_KREM = new Color(250, 247, 240);
    /** Warna latar bilah header dan garis tepi pixel tegas. */
    public static final Color WARNA_HEADER = new Color(28, 25, 23);
    /** Warna teks sekunder pada bilah header gelap. */
    public static final Color WARNA_HEADER_SUB = new Color(214, 211, 205);
    /** Warna latar datar untuk baris judul kolom tabel. */
    public static final Color WARNA_HEADER_TABEL = new Color(239, 236, 230);
    /** Warna teks utama berkontras tinggi. */
    public static final Color WARNA_TEKS_UTAMA = new Color(28, 25, 23);
    /** Warna teks sekunder untuk metadata dan ringkasan. */
    public static final Color WARNA_TEKS_SEKUNDER = new Color(87, 83, 78);
    /** Warna garis tepi panel dan input standar. */
    public static final Color WARNA_BORDER = new Color(221, 216, 206);
    /** Warna bayangan blok pixel keras untuk kartu dan panel. */
    public static final Color WARNA_BAYANGAN_PIXEL = new Color(214, 207, 194);
    /** Warna garis pembatas baris tabel yang halus. */
    public static final Color WARNA_GRID_TABEL = new Color(234, 229, 220);
    /** Warna sorotan lembut untuk baris tabel yang sedang dipilih. */
    public static final Color WARNA_SELEKSI = new Color(254, 243, 199);
    /** Warna aksen utama terakota untuk tombol CTA primer dan sorotan grafik. */
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
    /** Warna latar badge kategori CardGame. */
    public static final Color WARNA_BADGE_CARD_BG = new Color(254, 243, 199);
    /** Warna teks badge kategori CardGame. */
    public static final Color WARNA_BADGE_CARD_FG = new Color(146, 64, 14);
    /** Warna latar badge kategori FamilyBoardGame. */
    public static final Color WARNA_BADGE_FAMILY_BG = new Color(220, 252, 231);
    /** Warna teks badge kategori FamilyBoardGame. */
    public static final Color WARNA_BADGE_FAMILY_FG = new Color(22, 101, 52);
    /** Warna latar badge kategori StrategyGame. */
    public static final Color WARNA_BADGE_STRATEGY_BG = new Color(224, 231, 255);
    /** Warna teks badge kategori StrategyGame. */
    public static final Color WARNA_BADGE_STRATEGY_FG = new Color(55, 48, 163);
    /** Warna batang sekunder pada grafik analitik. */
    public static final Color WARNA_BATANG_LAIN = new Color(214, 207, 194);

    /** Nama font utama antarmuka pengguna. */
    public static final String FONT_UI = "Segoe UI";
    /** Nama font monospace untuk tampilan struk invoice dan label pixel. */
    public static final String FONT_MONO = "Consolas";

    /** Cache ikon gambar board game yang telah di-scale agar render tabel tetap ringan. */
    private static final Map<String, ImageIcon> CACHE_GAMBAR = new HashMap<>();

    /**
     * Constructor privat karena seluruh anggota kelas bersifat statis.
     */
    private TemaUI()
    {
    }

    /**
     * Model tabel read-only agar sel pada JTable tidak dapat diubah langsung oleh pengguna.
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
         * @return selalu false
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

        // Perataan teks dan penanda kolom status
        private transient int perataan;
        private transient boolean kolomStatus;

        /**
         * Membuat renderer sel tabel baru.
         *
         * @param perataan konstanta alignment dari SwingConstants
         * @param kolomStatus true jika kolom menampilkan teks status
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
         * @param isSelected true jika baris sedang dipilih
         * @param hasFocus true jika sel memiliki fokus
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
            this.setIcon(null);
            this.setHorizontalAlignment(this.perataan);
            this.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

            if (isSelected) {
                comp.setBackground(WARNA_SELEKSI);
            } else if (adalahBarisHover(table, row)) {
                comp.setBackground(WARNA_KARTU_KREM);
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
     * Renderer sel tabel khusus untuk menampilkan thumbnail foto board game secara terpusat.
     */
    public static class RendererGambarTabel extends DefaultTableCellRenderer
    {
        private static final long serialVersionUID = 1L;

        /**
         * Membuat renderer thumbnail gambar baru.
         */
        public RendererGambarTabel()
        {
        }

        /**
         * Menyiapkan tampilan thumbnail gambar pada sel tabel.
         *
         * @param table tabel induk
         * @param value nilai sel bertipe ImageIcon
         * @param isSelected true jika baris sedang dipilih
         * @param hasFocus true jika sel memiliki fokus
         * @param row indeks baris
         * @param column indeks kolom
         * @return komponen label berisi thumbnail terformat
         */
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column)
        {
            super.getTableCellRendererComponent(table, "", isSelected, false, row, column);
            this.setHorizontalAlignment(SwingConstants.CENTER);
            this.setVerticalAlignment(SwingConstants.CENTER);
            this.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
            if (isSelected) {
                this.setBackground(WARNA_SELEKSI);
            } else if (adalahBarisHover(table, row)) {
                this.setBackground(WARNA_KARTU_KREM);
            } else {
                this.setBackground(WARNA_SURFACE);
            }
            this.setText("");
            this.setIcon(value instanceof ImageIcon ? (ImageIcon) value : null);
            return this;
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
         * @param perataan konstanta alignment dari SwingConstants
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
                BorderFactory.createMatteBorder(0, 0, 2, 0, WARNA_HEADER),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
            ));
            return comp;
        }
    }

    /**
     * Panel grafik batang kustom bergaya pixel tabletop untuk menampilkan data stok,
     * transaksi, atau pengeluaran secara real-time tanpa pustaka eksternal.
     */
    public static class PanelGrafikBatang extends JPanel
    {
        private static final long serialVersionUID = 1L;

        // Data grafik dan status animasi/hover
        private transient String judul;
        private transient boolean formatRupiah;
        private transient String[] labels;
        private transient double[] nilaiAwal;
        private transient double[] nilai;
        private transient double progresAnimasi;
        private transient int indeksHover;
        private transient Timer timerAnimasi;
        private transient int[] xBatang;
        private transient int[] yBatang;
        private transient int[] lebarBatangCache;
        private transient int[] tinggiBatangCache;

        /**
         * Membuat panel grafik batang baru dengan animasi transisi dan hover dinamis.
         *
         * @param judul judul grafik
         * @param formatRupiah true untuk memformat label nilai sebagai mata uang rupiah
         */
        public PanelGrafikBatang(String judul, boolean formatRupiah)
        {
            this.judul = judul;
            this.formatRupiah = formatRupiah;
            this.labels = new String[0];
            this.nilaiAwal = new double[0];
            this.nilai = new double[0];
            this.progresAnimasi = 1.0;
            this.indeksHover = -1;
            this.xBatang = new int[0];
            this.yBatang = new int[0];
            this.lebarBatangCache = new int[0];
            this.tinggiBatangCache = new int[0];
            this.setOpaque(true);
            this.setBackground(WARNA_SURFACE);
            this.setBorder(buatBorderKartuPixel(14, 16, 14, 16));

            this.addMouseMotionListener(new MouseMotionAdapter()
            {
                @Override
                public void mouseMoved(MouseEvent event)
                {
                    PanelGrafikBatang.this.perbaruiHoverMouse(event.getPoint());
                }
            });
            this.addMouseListener(new MouseAdapter()
            {
                @Override
                public void mouseExited(MouseEvent event)
                {
                    if (PanelGrafikBatang.this.indeksHover != -1) {
                        PanelGrafikBatang.this.indeksHover = -1;
                        PanelGrafikBatang.this.setToolTipText(null);
                        PanelGrafikBatang.this.repaint();
                    }
                }
            });
        }

        /**
         * Memperbarui data grafik lalu menggambar ulang komponen secara halus.
         *
         * @param labels array label kategori sumbu-X
         * @param nilai array nilai numerik yang sesuai dengan tiap label
         */
        public void setData(String[] labels, double[] nilai)
        {
            String[] labelBaru = (labels == null) ? new String[0] : labels.clone();
            double[] targetBaru = (nilai == null) ? new double[0] : nilai.clone();

            boolean berubah = this.nilai.length != targetBaru.length;
            if (!berubah) {
                for (int i = 0; i < targetBaru.length; i++) {
                    if (Math.abs(this.nilai[i] - targetBaru[i]) > 0.001) {
                        berubah = true;
                        break;
                    }
                }
            }

            double[] awalBaru = new double[targetBaru.length];
            for (int i = 0; i < targetBaru.length; i++) {
                awalBaru[i] = (i < this.nilai.length) ? this.hitungNilaiInterpolasi(i) : 0.0;
            }

            this.labels = labelBaru;
            this.nilaiAwal = awalBaru;
            this.nilai = targetBaru;

            if (this.timerAnimasi != null && this.timerAnimasi.isRunning()) {
                this.timerAnimasi.stop();
            }

            if (berubah && this.isDisplayable() && this.isShowing()) {
                this.progresAnimasi = 0.0;
                this.timerAnimasi = new Timer(16, event -> {
                    this.progresAnimasi = Math.min(1.0, this.progresAnimasi + 0.09);
                    this.repaint();
                    if (this.progresAnimasi >= 1.0) {
                        ((Timer) event.getSource()).stop();
                    }
                });
                this.timerAnimasi.start();
            } else {
                this.progresAnimasi = 1.0;
                this.repaint();
            }
        }

        /**
         * Menggambar grid horizontal, batang pixel berbingkai tegas, nilai pada puncak batang,
         * sorotan hover interaktif, serta label sumbu-X.
         *
         * @param g konteks grafis Swing
         */
        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB
            );

            int width = this.getWidth();
            int height = this.getHeight();
            int padLeft = 22;
            int padRight = 22;
            int padTop = 44;
            int padBottom = 34;

            g2.setFont(new Font(FONT_UI, Font.BOLD, 13));
            g2.setColor(WARNA_TEKS_UTAMA);
            g2.drawString(this.judul, padLeft, 24);

            int plotW = Math.max(40, width - padLeft - padRight);
            int plotH = Math.max(40, height - padTop - padBottom);

            g2.setColor(WARNA_GRID_TABEL);
            g2.setStroke(new BasicStroke(1f));
            for (int garis = 0; garis <= 3; garis++) {
                int yGaris = padTop + (plotH * garis / 3);
                g2.drawLine(padLeft, yGaris, width - padRight, yGaris);
            }

            if (this.nilai.length == 0) {
                g2.setFont(new Font(FONT_UI, Font.PLAIN, 12));
                g2.setColor(WARNA_TEKS_SEKUNDER);
                g2.drawString("Belum ada data untuk ditampilkan.", padLeft, padTop + plotH / 2);
                g2.dispose();
                return;
            }

            double maks = 1.0;
            int indeksPuncak = 0;
            for (int index = 0; index < this.nilai.length; index++) {
                if (this.nilai[index] >= maks) {
                    maks = this.nilai[index];
                    indeksPuncak = index;
                }
            }

            int jumlah = this.nilai.length;
            this.xBatang = new int[jumlah];
            this.yBatang = new int[jumlah];
            this.lebarBatangCache = new int[jumlah];
            this.tinggiBatangCache = new int[jumlah];

            int lebarSlot = plotW / jumlah;
            int lebarBatang = Math.min(54, Math.max(24, lebarSlot - 22));
            FontMetrics fmLabel = g2.getFontMetrics(new Font(FONT_UI, Font.PLAIN, 11));
            FontMetrics fmNilai = g2.getFontMetrics(new Font(FONT_UI, Font.BOLD, 11));

            for (int index = 0; index < jumlah; index++) {
                double val = Math.max(0.0, this.hitungNilaiInterpolasi(index));
                int tinggiBatang = (val <= 0.001)
                    ? 4
                    : (int) Math.max(12, Math.round((val / maks) * (plotH - 22)));
                int x = padLeft + (index * lebarSlot) + (lebarSlot - lebarBatang) / 2;
                int y = padTop + plotH - tinggiBatang;
                boolean puncak = (index == indeksPuncak && this.nilai[index] > 0.0);
                boolean disorot = (index == this.indeksHover);

                this.xBatang[index] = x;
                this.yBatang[index] = y;
                this.lebarBatangCache[index] = lebarBatang;
                this.tinggiBatangCache[index] = tinggiBatang;

                g2.setColor(WARNA_BAYANGAN_PIXEL);
                g2.fillRect(x + 3, y + 3, lebarBatang, tinggiBatang);

                Color warnaBatang;
                if (disorot) {
                    warnaBatang = puncak ? new Color(217, 119, 6) : WARNA_SELEKSI;
                } else {
                    warnaBatang = puncak ? WARNA_AKSEN : WARNA_BATANG_LAIN;
                }
                g2.setColor(warnaBatang);
                g2.fillRect(x, y, lebarBatang, tinggiBatang);

                g2.setColor(disorot ? WARNA_AKSEN_GELAP : WARNA_HEADER);
                g2.setStroke(new BasicStroke(disorot ? 2.0f : 1.5f));
                g2.drawRect(x, y, lebarBatang, tinggiBatang);

                String teksNilai = this.formatNilaiBatang(this.nilai[index]);
                g2.setFont(new Font(FONT_UI, Font.BOLD, 11));
                int tw = fmNilai.stringWidth(teksNilai);
                int tx = x + (lebarBatang - tw) / 2;
                int ty = Math.max(padTop + 10, y - 6);
                g2.setColor((puncak || disorot) ? WARNA_AKSEN : WARNA_TEKS_UTAMA);
                g2.drawString(teksNilai, tx, ty);

                if (index < this.labels.length) {
                    g2.setFont(new Font(FONT_UI, (puncak || disorot) ? Font.BOLD : Font.PLAIN, 11));
                    g2.setColor((puncak || disorot) ? WARNA_TEKS_UTAMA : WARNA_TEKS_SEKUNDER);
                    int lw = fmLabel.stringWidth(this.labels[index]);
                    int lx = x + (lebarBatang - lw) / 2;
                    g2.drawString(this.labels[index], lx, height - 10);
                }
            }

            g2.dispose();
        }

        /**
         * Menghitung nilai interpolasi kurva ease-out cubic untuk indeks batang tertentu.
         *
         * @param indeks indeks batang
         * @return nilai terinterpolasi saat animasi berjalan
         */
        private double hitungNilaiInterpolasi(int indeks)
        {
            double target = (indeks < this.nilai.length) ? this.nilai[indeks] : 0.0;
            double awal = (indeks < this.nilaiAwal.length) ? this.nilaiAwal[indeks] : 0.0;
            double t = Math.max(0.0, Math.min(1.0, this.progresAnimasi));
            double easeOut = 1.0 - Math.pow(1.0 - t, 3.0);
            return awal + ((target - awal) * easeOut);
        }

        /**
         * Memperbarui indeks batang yang berada di bawah kursor beserta tooltip nilainya.
         *
         * @param titik posisi kursor mouse relatif terhadap panel grafik
         */
        private void perbaruiHoverMouse(Point titik)
        {
            int baru = -1;
            for (int i = 0; i < this.xBatang.length; i++) {
                int x1 = this.xBatang[i] - 4;
                int x2 = this.xBatang[i] + this.lebarBatangCache[i] + 4;
                int y1 = Math.min(this.yBatang[i] - 16, this.getHeight() - 36);
                int y2 = this.getHeight();
                if (titik.x >= x1 && titik.x <= x2 && titik.y >= y1 && titik.y <= y2) {
                    baru = i;
                    break;
                }
            }

            if (baru != this.indeksHover) {
                this.indeksHover = baru;
                if (baru >= 0 && baru < this.nilai.length && baru < this.labels.length) {
                    this.setToolTipText(
                        this.labels[baru] + ": " + this.formatNilaiBatang(this.nilai[baru])
                    );
                } else {
                    this.setToolTipText(null);
                }
                this.repaint();
            }
        }

        /**
         * Memformat angka nilai pada bagian atas batang grafik.
         *
         * @param nilaiBatang nilai numerik batang
         * @return teks terformat ringkas
         */
        private String formatNilaiBatang(double nilaiBatang)
        {
            if (this.formatRupiah) {
                return formatRupiah(nilaiBatang);
            }
            if (Math.abs(nilaiBatang - Math.round(nilaiBatang)) < 0.001) {
                return String.valueOf(Math.round(nilaiBatang));
            }
            return String.format(Locale.forLanguageTag("id-ID"), "%.1f", nilaiBatang);
        }
    }

    /**
     * Membuat panel bilah atas jendela aplikasi lengkap dengan emblem pixel art, judul,
     * subjudul pengguna aktif, tombol Refresh, dan tombol Logout.
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
        JPanel header = new JPanel(new BorderLayout(14, 0))
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                int w = this.getWidth();
                int h = this.getHeight();
                int kotak = 6;
                Color emas = new Color(245, 158, 11);
                for (int x = 0; x < w; x += kotak) {
                    boolean genap = ((x / kotak) % 2 == 0);
                    g.setColor(genap ? WARNA_AKSEN : emas);
                    g.fillRect(x, h - 6, kotak, 3);
                    g.setColor(genap ? emas : WARNA_AKSEN_GELAP);
                    g.fillRect(x, h - 3, kotak, 3);
                }
            }
        };
        header.setBackground(WARNA_HEADER);
        header.setBorder(BorderFactory.createEmptyBorder(12, 20, 16, 20));

        JPanel kiri = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        kiri.setOpaque(false);

        JLabel lblLogo = new JLabel(muatLogoPixel(38));

        JPanel teksJudul = new JPanel(new BorderLayout(0, 2));
        teksJudul.setOpaque(false);

        JLabel lblJudul = new JLabel(judul);
        lblJudul.setFont(new Font(FONT_UI, Font.BOLD, 18));
        lblJudul.setForeground(WARNA_SURFACE);

        JLabel lblSubJudul = new JLabel(subJudul);
        lblSubJudul.setFont(new Font(FONT_UI, Font.PLAIN, 12));
        lblSubJudul.setForeground(WARNA_HEADER_SUB);

        teksJudul.add(lblJudul, BorderLayout.NORTH);
        teksJudul.add(lblSubJudul, BorderLayout.SOUTH);

        kiri.add(lblLogo);
        kiri.add(teksJudul);

        JPanel aksiKanan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        aksiKanan.setOpaque(false);

        JButton btnRefresh = buatTombolHeader("Refresh");
        btnRefresh.addActionListener(event -> onRefresh.run());

        JButton btnLogout = buatTombolHeader("Logout");
        btnLogout.addActionListener(event -> onLogout.run());

        aksiKanan.add(btnRefresh);
        aksiKanan.add(btnLogout);

        header.add(kiri, BorderLayout.WEST);
        header.add(aksiKanan, BorderLayout.EAST);

        return header;
    }

    /**
     * Membuat bingkai border kartu bergaya pixel tegas dengan bayangan blok kanan-bawah
     * serta padding internal yang dapat disesuaikan.
     *
     * @param atas padding atas
     * @param kiri padding kiri
     * @param bawah padding bawah
     * @param kanan padding kanan
     * @return objek Border bergaya pixel tabletop
     */
    public static Border buatBorderKartuPixel(int atas, int kiri, int bawah, int kanan)
    {
        Border bingkai = BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 3, 3, WARNA_BAYANGAN_PIXEL),
            BorderFactory.createLineBorder(WARNA_HEADER, 2)
        );
        return BorderFactory.createCompoundBorder(
            bingkai,
            BorderFactory.createEmptyBorder(atas, kiri, bawah, kanan)
        );
    }

    /**
     * Membuat bingkai foto showcase atau QRIS bergaya sudut pixel (RPG item frame)
     * dengan aksen terakota dan emas pada keempat sudutnya.
     *
     * @return objek Border beraksen sudut pixel
     */
    public static Border buatBorderFotoPixel()
    {
        return new AbstractBorder()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
            {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(WARNA_HEADER);
                g2.drawRect(x, y, width - 1, height - 1);
                g2.drawRect(x + 1, y + 1, width - 3, height - 3);

                int p = 10;
                g2.setColor(WARNA_AKSEN);
                // Sudut kiri atas
                g2.fillRect(x, y, p, 3);
                g2.fillRect(x, y, 3, p);
                // Sudut kanan atas
                g2.fillRect(x + width - p, y, p, 3);
                g2.fillRect(x + width - 3, y, 3, p);
                // Sudut kiri bawah
                g2.fillRect(x, y + height - 3, p, 3);
                g2.fillRect(x, y + height - p, 3, p);
                // Sudut kanan bawah
                g2.fillRect(x + width - p, y + height - 3, p, 3);
                g2.fillRect(x + width - 3, y + height - p, 3, p);

                // Titik kilau emas di keempat pojok
                g2.setColor(new Color(245, 158, 11));
                g2.fillRect(x + 2, y + 2, 3, 3);
                g2.fillRect(x + width - 5, y + 2, 3, 3);
                g2.fillRect(x + 2, y + height - 5, 3, 3);
                g2.fillRect(x + width - 5, y + height - 5, 3, 3);
                g2.dispose();
            }

            @Override
            public Insets getBorderInsets(Component c)
            {
                return new Insets(4, 4, 4, 4);
            }

            @Override
            public Insets getBorderInsets(Component c, Insets insets)
            {
                insets.set(4, 4, 4, 4);
                return insets;
            }
        };
    }

    /**
     * Membuat bilah status tipis di bagian bawah jendela berisi identitas aplikasi
     * serta tanggal dan jam berjalan real-time.
     *
     * @param peran nama peran pengguna aktif (misalnya Administrator atau Penyewa)
     * @return panel status bar terformat
     */
    public static JPanel buatStatusBar(String peran)
    {
        DateTimeFormatter formatWaktu = DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss");
        JLabel lblWaktu = new JLabel(LocalDateTime.now().format(formatWaktu));
        lblWaktu.setFont(new Font(FONT_MONO, Font.BOLD, 11));
        lblWaktu.setForeground(WARNA_TEKS_SEKUNDER);

        JPanel statusBar = new JPanel(new BorderLayout())
        {
            private static final long serialVersionUID = 1L;
            private transient Timer timerJam;

            @Override
            public void addNotify()
            {
                super.addNotify();
                if (this.timerJam == null) {
                    this.timerJam = new Timer(1000, event ->
                        lblWaktu.setText(LocalDateTime.now().format(formatWaktu))
                    );
                    this.timerJam.start();
                }
            }

            @Override
            public void removeNotify()
            {
                if (this.timerJam != null) {
                    this.timerJam.stop();
                    this.timerJam = null;
                }
                super.removeNotify();
            }
        };

        statusBar.setBackground(WARNA_HEADER_TABEL);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, WARNA_HEADER),
            BorderFactory.createEmptyBorder(5, 18, 5, 18)
        ));

        JLabel lblInfo = new JLabel(
            "Rent the Board!  |  Cozy Pixel Tabletop  |  Mode: " + peran
        );
        lblInfo.setFont(new Font(FONT_UI, Font.PLAIN, 11));
        lblInfo.setForeground(WARNA_TEKS_SEKUNDER);

        statusBar.add(lblInfo, BorderLayout.CENTER);
        statusBar.add(lblWaktu, BorderLayout.EAST);
        return statusBar;
    }

    /**
     * Membuat kartu ringkasan KPI bergaya pixel tabletop lengkap dengan garis aksen kiri
     * dan ikon pixel-art mini sesuai topik metrik.
     *
     * @param judul label metrik di bagian atas kartu
     * @param lblNilai komponen label nilai utama yang diperbarui caller
     * @param lblSub komponen label subketerangan yang diperbarui caller
     * @return panel kartu KPI siap dipasang ke layout grid
     */
    public static JPanel buatKartuStatistik(String judul, JLabel lblNilai, JLabel lblSub)
    {
        Color warnaAksenKiri = pilihWarnaAksenKpi(judul);
        JPanel kartu = new JPanel(new BorderLayout(0, 3));
        kartu.setBackground(WARNA_SURFACE);
        Border bingkaiLuar = BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 3, 3, WARNA_BAYANGAN_PIXEL),
            BorderFactory.createLineBorder(WARNA_HEADER, 2)
        );
        Border bingkaiAksen = BorderFactory.createCompoundBorder(
            bingkaiLuar,
            BorderFactory.createMatteBorder(0, 4, 0, 0, warnaAksenKiri)
        );
        kartu.setBorder(BorderFactory.createCompoundBorder(
            bingkaiAksen,
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JPanel barisAtas = new JPanel(new BorderLayout(6, 0));
        barisAtas.setOpaque(false);

        JLabel lblJudul = new JLabel(judul);
        lblJudul.setFont(new Font(FONT_UI, Font.BOLD, 11));
        lblJudul.setForeground(WARNA_TEKS_SEKUNDER);

        JLabel lblIkon = new JLabel(buatIkonKpiPixel(judul));
        barisAtas.add(lblJudul, BorderLayout.WEST);
        barisAtas.add(lblIkon, BorderLayout.EAST);

        lblNilai.setFont(new Font(FONT_UI, Font.BOLD, 17));
        lblNilai.setForeground(WARNA_TEKS_UTAMA);

        lblSub.setFont(new Font(FONT_UI, Font.PLAIN, 11));
        lblSub.setForeground(WARNA_AKSEN);

        kartu.add(barisAtas, BorderLayout.NORTH);
        kartu.add(lblNilai, BorderLayout.CENTER);
        kartu.add(lblSub, BorderLayout.SOUTH);
        return kartu;
    }

    // Menentukan warna strip aksen kiri berdasarkan jenis kartu KPI
    private static Color pilihWarnaAksenKpi(String judul)
    {
        String teks = (judul == null) ? "" : judul.toUpperCase(Locale.ROOT);
        if (teks.contains("DENDA")) {
            return WARNA_STATUS_BAHAYA;
        }
        if (teks.contains("STOK")) {
            return WARNA_STATUS_AMAN;
        }
        if (teks.contains("PENDAPATAN") || teks.contains("BIAYA")) {
            return new Color(217, 119, 6);
        }
        return WARNA_AKSEN;
    }

    // Menggambar ikon pixel-art mini 16x16 untuk kartu statistik KPI
    private static ImageIcon buatIkonKpiPixel(String judul)
    {
        String teks = (judul == null) ? "" : judul.toUpperCase(Locale.ROOT);
        String jenis;
        String[] peta;
        if (teks.contains("PENDAPATAN") || teks.contains("BIAYA")) {
            jenis = "KOIN";
            peta = new String[] {
                "  KKKK  ",
                " KYYYYK ",
                "KYGYYGYK",
                "KYGYGGYK",
                "KYGGYGYK",
                "KYGYYGYK",
                " KYYYYK ",
                "  KKKK  "
            };
        } else if (teks.contains("DENDA")) {
            jenis = "JAM";
            peta = new String[] {
                "KKKKKKKK",
                " KRYYRK ",
                "  KYYK  ",
                "   KK   ",
                "  KRRK  ",
                " KRRRRK ",
                "KKKKKKKK",
                "        "
            };
        } else if (teks.contains("STOK")) {
            jenis = "KOTAK";
            peta = new String[] {
                " KKKKKK ",
                "KLLLLLLK",
                "KLKKKKLK",
                "KMMMMMMK",
                "KMMYYMMK",
                "KMMMMMMK",
                "KMMMMMMK",
                " KKKKKK "
            };
        } else {
            jenis = "TIKET";
            peta = new String[] {
                "KKKKKKKK",
                "KYYYYYYK",
                "KYKKKKYK",
                "KYYYYYYK",
                "KYKKKYYK",
                "KYYYYYYK",
                "KKKKKKKK",
                "        "
            };
        }

        String kunci = "KPI_" + jenis;
        if (CACHE_GAMBAR.containsKey(kunci)) {
            return CACHE_GAMBAR.get(kunci);
        }

        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        for (int y = 0; y < peta.length; y++) {
            for (int x = 0; x < peta[y].length(); x++) {
                char c = peta[y].charAt(x);
                Color warna = null;
                if (c == 'K') {
                    warna = WARNA_HEADER;
                } else if (c == 'Y') {
                    warna = new Color(250, 204, 21);
                } else if (c == 'G') {
                    warna = WARNA_AKSEN;
                } else if (c == 'R') {
                    warna = WARNA_STATUS_BAHAYA;
                } else if (c == 'L') {
                    warna = new Color(34, 197, 94);
                } else if (c == 'M') {
                    warna = WARNA_STATUS_AMAN;
                }
                if (warna != null) {
                    g2.setColor(warna);
                    g2.fillRect(x * 2, y * 2, 2, 2);
                }
            }
        }
        g2.dispose();
        ImageIcon ikon = new ImageIcon(img);
        CACHE_GAMBAR.put(kunci, ikon);
        return ikon;
    }

    /**
     * Memperbarui teks dan warna badge kategori board game sesuai subclass-nya.
     *
     * @param label label badge yang akan diformat
     * @param kategori nama sederhana kelas kategori board game
     */
    public static void terapkanGayaBadgeKategori(JLabel label, String kategori)
    {
        label.setText(" " + kategori + " ");
        label.setOpaque(true);
        label.setFont(new Font(FONT_UI, Font.BOLD, 11));

        if ("CardGame".equalsIgnoreCase(kategori)) {
            label.setBackground(WARNA_BADGE_CARD_BG);
            label.setForeground(WARNA_BADGE_CARD_FG);
        } else if ("FamilyBoardGame".equalsIgnoreCase(kategori)) {
            label.setBackground(WARNA_BADGE_FAMILY_BG);
            label.setForeground(WARNA_BADGE_FAMILY_FG);
        } else {
            label.setBackground(WARNA_BADGE_STRATEGY_BG);
            label.setForeground(WARNA_BADGE_STRATEGY_FG);
        }

        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(WARNA_HEADER, 1),
            BorderFactory.createEmptyBorder(3, 7, 3, 7)
        ));
    }

    /**
     * Membuat komponen JTable standar bertema Cozy Pixel Tabletop.
     *
     * @param model model data tabel
     * @param tinggiBaris tinggi tiap baris dalam piksel
     * @return objek JTable yang telah dikonfigurasi
     */
    public static JTable buatTabelStandar(DefaultTableModel model, int tinggiBaris)
    {
        JTable tabel = new JTable(model);
        tabel.setRowHeight(tinggiBaris);
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
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, WARNA_HEADER));

        tabel.addMouseMotionListener(new MouseMotionAdapter()
        {
            @Override
            public void mouseMoved(MouseEvent event)
            {
                int baris = tabel.rowAtPoint(event.getPoint());
                Object lama = tabel.getClientProperty("barisHover");
                if (!(lama instanceof Integer) || (Integer) lama != baris) {
                    tabel.putClientProperty("barisHover", Integer.valueOf(baris));
                    tabel.repaint();
                }
            }
        });
        tabel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseExited(MouseEvent event)
            {
                Object lama = tabel.getClientProperty("barisHover");
                if (lama instanceof Integer && (Integer) lama != -1) {
                    tabel.putClientProperty("barisHover", Integer.valueOf(-1));
                    tabel.repaint();
                }
            }
        });

        return tabel;
    }

    /**
     * Mengatur lebar kolom, perataan header, dan renderer sel suatu JTable
     * tanpa kolom gambar.
     *
     * @param tabel tabel yang akan dikonfigurasi
     * @param lebar array lebar pilihan tiap kolom
     * @param rataKanan daftar indeks kolom yang diratakan kanan
     * @param kolomStatus daftar indeks kolom yang menampilkan status semantik
     */
    public static void aturKolomTabel(JTable tabel, int[] lebar, int[] rataKanan, int[] kolomStatus)
    {
        aturKolomTabel(tabel, lebar, rataKanan, kolomStatus, -1);
    }

    /**
     * Mengatur lebar kolom, perataan header, renderer gambar thumbnail, dan renderer teks
     * pada suatu JTable.
     *
     * @param tabel tabel yang akan dikonfigurasi
     * @param lebar array lebar pilihan tiap kolom
     * @param rataKanan daftar indeks kolom yang diratakan kanan
     * @param kolomStatus daftar indeks kolom yang menampilkan status semantik
     * @param indeksKolomGambar indeks kolom thumbnail ImageIcon, atau -1 jika tidak ada
     */
    public static void aturKolomTabel(JTable tabel, int[] lebar, int[] rataKanan,
                                      int[] kolomStatus, int indeksKolomGambar)
    {
        RendererSelTabel kiri = new RendererSelTabel(SwingConstants.LEFT, false);
        RendererSelTabel kanan = new RendererSelTabel(SwingConstants.RIGHT, false);
        RendererSelTabel status = new RendererSelTabel(SwingConstants.LEFT, true);
        RendererGambarTabel gambar = new RendererGambarTabel();
        RendererHeaderTabel headerKiri = new RendererHeaderTabel(SwingConstants.LEFT);
        RendererHeaderTabel headerKanan = new RendererHeaderTabel(SwingConstants.RIGHT);
        RendererHeaderTabel headerTengah = new RendererHeaderTabel(SwingConstants.CENTER);

        TableColumnModel kolom = tabel.getColumnModel();
        for (int index = 0; index < lebar.length; index++) {
            kolom.getColumn(index).setPreferredWidth(lebar[index]);
            if (index == indeksKolomGambar) {
                kolom.getColumn(index).setMaxWidth(lebar[index] + 12);
                kolom.getColumn(index).setMinWidth(lebar[index]);
                kolom.getColumn(index).setCellRenderer(gambar);
                kolom.getColumn(index).setHeaderRenderer(headerTengah);
            } else if (termasukIndeks(rataKanan, index)) {
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
     * Membungkus JTable ke dalam JScrollPane dengan bingkai pixel tegas.
     *
     * @param tabel tabel yang akan dibungkus
     * @return objek JScrollPane terformat
     */
    public static JScrollPane buatScrollTabel(JTable tabel)
    {
        JScrollPane scrollPane = new JScrollPane(tabel);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 3, 3, WARNA_BAYANGAN_PIXEL),
            BorderFactory.createLineBorder(WARNA_HEADER, 2)
        ));
        scrollPane.getViewport().setBackground(WARNA_SURFACE);
        return scrollPane;
    }

    /**
     * Membuat label ringkasan pada baris bawah panel tab.
     *
     * @param teks teks awal label
     * @return objek JLabel terformat
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
     * Memformat komponen JTextField dengan bingkai pixel bersih.
     *
     * @param field komponen input teks yang akan diformat
     */
    public static void formatInputField(JTextField field)
    {
        field.setFont(new Font(FONT_UI, Font.PLAIN, 13));
        field.setForeground(WARNA_TEKS_UTAMA);
        field.setBackground(WARNA_SURFACE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(WARNA_BORDER, 2),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
    }

    /**
     * Membuat tombol aksi primer berwarna aksen terakota bergaya pixel bevel.
     *
     * @param teks label tombol
     * @return tombol primer terformat
     */
    public static JButton buatTombolPrimer(String teks)
    {
        return formatTombol(
            teks, WARNA_AKSEN, WARNA_AKSEN_HOVER, WARNA_AKSEN_TEKAN,
            WARNA_SURFACE, WARNA_HEADER, new Color(217, 119, 6), WARNA_AKSEN_GELAP, Font.BOLD
        );
    }

    /**
     * Membuat tombol aksi sekunder bernuansa netral bergaya pixel bevel.
     *
     * @param teks label tombol
     * @return tombol sekunder terformat
     */
    public static JButton buatTombolSekunder(String teks)
    {
        return formatTombol(
            teks, WARNA_SURFACE, WARNA_SEKUNDER_HOVER, WARNA_SEKUNDER_TEKAN,
            WARNA_TEKS_UTAMA, WARNA_HEADER, WARNA_SURFACE, WARNA_BAYANGAN_PIXEL, Font.BOLD
        );
    }

    /**
     * Membuat tombol aksi destruktif berwarna merah bata bergaya pixel bevel.
     *
     * @param teks label tombol
     * @return tombol bahaya terformat
     */
    public static JButton buatTombolBahaya(String teks)
    {
        return formatTombol(
            teks, WARNA_SURFACE, WARNA_BAHAYA_HOVER, WARNA_BAHAYA_TEKAN,
            WARNA_STATUS_BAHAYA, WARNA_STATUS_BAHAYA, WARNA_SURFACE, WARNA_BORDER_BAHAYA, Font.BOLD
        );
    }

    /**
     * Membuat tombol gelap untuk bilah header aplikasi bergaya pixel bevel.
     *
     * @param teks label tombol
     * @return tombol header terformat
     */
    public static JButton buatTombolHeader(String teks)
    {
        return formatTombol(
            teks, WARNA_TOMBOL_HEADER, WARNA_TOMBOL_HEADER_HOVER, WARNA_TOMBOL_HEADER_TEKAN,
            WARNA_SURFACE, WARNA_BORDER_HEADER, new Color(68, 64, 60), WARNA_HEADER, Font.BOLD
        );
    }

    /**
     * Memuat gambar foto board game dari folder Assets/ sesuai ID barang
     * dengan proporsi terjaga, atau menghasilkan ikon pixel art kategori bila foto belum tersedia.
     *
     * @param barang objek board game yang dimuat gambarnya
     * @param ukuran sisi kotak gambar dalam piksel
     * @return objek ImageIcon berukuran ukuran x ukuran
     */
    public static ImageIcon muatGambarBarang(Barang barang, int ukuran)
    {
        if (barang == null || ukuran <= 0) {
            return new ImageIcon(gambarIkonKategoriPixel("CardGame", Math.max(32, ukuran)));
        }

        String idBersih = barang.getId().trim().toUpperCase(Locale.ROOT);
        String kunciCache = idBersih + "_" + ukuran;
        if (CACHE_GAMBAR.containsKey(kunciCache)) {
            return CACHE_GAMBAR.get(kunciCache);
        }

        BufferedImage gambarAsli = cariDanBacaGambar(idBersih);
        BufferedImage hasil;
        if (gambarAsli != null) {
            hasil = ubahUkuranKotakRapi(gambarAsli, ukuran);
        } else {
            hasil = gambarIkonKategoriPixel(barang.getClass().getSimpleName(), ukuran);
        }

        ImageIcon ikon = new ImageIcon(hasil);
        CACHE_GAMBAR.put(kunciCache, ikon);
        return ikon;
    }

    /**
     * Memuat pratinjau gambar langsung dari file foto yang dipilih melalui pemilih file.
     *
     * @param file file gambar yang dipilih
     * @param ukuran sisi kotak pratinjau dalam piksel
     * @return objek ImageIcon berukuran ukuran x ukuran, atau null
     *         jika file tidak valid atau gagal dibaca
     */
    public static ImageIcon muatPratinjauFile(File file, int ukuran)
    {
        if (file == null || !file.isFile() || ukuran <= 0) {
            return null;
        }
        try {
            BufferedImage img = ImageIO.read(file);
            if (img == null) {
                return null;
            }
            return new ImageIcon(ubahUkuranKotakRapi(img, ukuran));
        } catch (IOException ignored) {
            return null;
        }
    }

    /**
     * Memuat frame pertama dari animasi GIF login secara sinkron agar langsung tampil
     * tanpa jeda saat jendela dibuka.
     *
     * @return gambar frame awal bila ditemukan, atau null jika tidak ada
     */
    public static BufferedImage muatGambarAwalLogin()
    {
        File file = cariFileAset("loginbg.gif");
        if (file == null) {
            return null;
        }
        try {
            return ImageIO.read(file);
        } catch (IOException ignored) {
            return null;
        }
    }

    /**
     * Memuat animasi GIF latar belakang layar login dari folder Assets/.
     *
     * @return ImageIcon animasi bila file ditemukan, atau null jika tidak ada
     */
    public static ImageIcon muatAnimasiLogin()
    {
        File file = cariFileAset("loginbg.gif");
        if (file == null) {
            return null;
        }
        return new ImageIcon(file.getAbsolutePath());
    }

    /**
     * Menghasilkan emblem kartu UNO pixel art toko Rent the Board! dengan dua kartu bertumpuk
     * (kartu merah depan beroval kuning serta kartu biru di belakang).
     *
     * @param ukuran lebar dan tinggi ikon dalam piksel
     * @return objek ImageIcon emblem pixel
     */
    public static ImageIcon muatLogoPixel(int ukuran)
    {
        String kunci = "LOGO_UNO_V1_" + ukuran;
        if (CACHE_GAMBAR.containsKey(kunci)) {
            return CACHE_GAMBAR.get(kunci);
        }

        String[] peta = {
            "                        ",
            "        KKKKKKKKKKKKKK  ",
            "       KWWWWWWWWWWWWWWK ",
            "       KWBBBBBBBBBBBBWK ",
            "   KKKKKKKKKKKKKKKKBBWK ",
            "  KWWWWWWWWWWWWWWWWKBBWK",
            " KWRRRRRRRRRRRRRRRWKBBWK",
            " KWRWWWRRRRRRRROORWKBBWK",
            " KWRRRRRRRRRRYYYYRWKBBWK",
            " KWRRRRRRRRYYYYYYRWKBBWK",
            " KWRRRRRRYYYYYYYYRWKBBWK",
            " KWRRRRRYYYWKKWYYRWKBBWK",
            " KWRRRRYYYYWKKWYYRWKBBWK",
            " KWRRRRYYYYWKKWYYRWKBBWK",
            " KWRRRRYYYYWWWWYYRWKBBWK",
            " KWRRRRYYYYYYYYORWKBBWK ",
            " KWRRRRYYYYYYORRRWKBBWK ",
            " KWRROYYYYYRRRRRRWKWWK  ",
            " KWRRRRRRRRRRRWWWRKKKK  ",
            " KWRRRRRRRRRRRRRRRWK    ",
            "  KWWWWWWWWWWWWWWWWK    ",
            "   KKKKKKKKKKKKKKKK     ",
            "                        ",
            "                        "
        };

        BufferedImage sprite = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < peta.length; y++) {
            String baris = peta[y];
            for (int x = 0; x < baris.length(); x++) {
                Color warna = warnaPikselLogo(baris.charAt(x));
                if (warna != null) {
                    sprite.setRGB(x, y, warna.getRGB());
                }
            }
        }

        BufferedImage img = new BufferedImage(ukuran, ukuran, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );
        g2.drawImage(sprite, 0, 0, ukuran, ukuran, null);
        g2.dispose();

        ImageIcon ikon = new ImageIcon(img);
        CACHE_GAMBAR.put(kunci, ikon);
        return ikon;
    }

    /**
     * Memetakan kode karakter sprite logo kartu UNO pixel menjadi objek warna palet.
     *
     * @param kode karakter kode piksel
     * @return warna piksel atau null jika transparan
     */
    private static Color warnaPikselLogo(char kode)
    {
        switch (kode) {
            case 'K':
                return WARNA_HEADER;
            case 'W':
                return WARNA_SURFACE;
            case 'R':
                return new Color(220, 38, 38);
            case 'B':
                return new Color(37, 99, 235);
            case 'Y':
                return new Color(250, 204, 21);
            case 'O':
                return new Color(245, 158, 11);
            default:
                return null;
        }
    }

    /**
     * Membentuk teks keterangan biaya tambahan sesuai kategori board game.
     *
     * @param item objek board game
     * @return ringkasan biaya tambahan atau "-" bila tidak ada
     */
    public static String formatBiayaTambahan(Barang item)
    {
        if (item instanceof FamilyBoardGame) {
            FamilyBoardGame familyGame = (FamilyBoardGame) item;
            return "Garansi: " + formatRupiah(familyGame.getBiayaGaransiPion());
        }
        if (item instanceof StrategyGame) {
            StrategyGame strategyGame = (StrategyGame) item;
            return "Modul/hari: " + formatRupiah(strategyGame.getBiayaModulHarian());
        }
        return "-";
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
     * Komponen JButton kustom bergaya pixel bevel yang menggambar latar sendiri
     * agar warna tombol tetap akurat di Windows Look and Feel.
     */
    private static class TombolTema extends JButton
    {
        private static final long serialVersionUID = 1L;

        // Warna status dan bevel tombol
        private Color warnaNormal;
        private Color warnaHover;
        private Color warnaTekan;
        private Color warnaSorotAtas;
        private Color warnaBayanganBawah;

        /**
         * Membuat tombol kustom pixel bevel baru.
         *
         * @param teks label tombol
         * @param warnaNormal warna latar normal
         * @param warnaHover warna latar hover
         * @param warnaTekan warna latar saat ditekan
         * @param warnaSorotAtas warna highlight bevel atas-kiri
         * @param warnaBayanganBawah warna shadow bevel bawah-kanan
         */
        public TombolTema(String teks, Color warnaNormal, Color warnaHover, Color warnaTekan,
                          Color warnaSorotAtas, Color warnaBayanganBawah)
        {
            super(teks);
            this.warnaNormal = warnaNormal;
            this.warnaHover = warnaHover;
            this.warnaTekan = warnaTekan;
            this.warnaSorotAtas = warnaSorotAtas;
            this.warnaBayanganBawah = warnaBayanganBawah;
            this.setUI(new BasicButtonUI());
            this.setOpaque(false);
            this.setContentAreaFilled(false);
            this.setRolloverEnabled(true);
            this.setFocusPainted(false);
        }

        /**
         * Menjaga agar tombol tetap memakai BasicButtonUI meski Look and Feel diperbarui.
         */
        @Override
        public void updateUI()
        {
            this.setUI(new BasicButtonUI());
        }

        /**
         * Menggambar latar tombol beserta efek bevel pixel atas-kiri dan bawah-kanan.
         *
         * @param g konteks grafis komponen
         */
        @Override
        protected void paintComponent(Graphics g)
        {
            Graphics2D g2 = (Graphics2D) g.create();
            int w = this.getWidth();
            int h = this.getHeight();

            boolean ditekan = this.getModel().isPressed();
            Color latar = this.warnaNormal;
            if (!this.isEnabled()) {
                latar = WARNA_HEADER_TABEL;
            } else if (ditekan) {
                latar = this.warnaTekan;
            } else if (this.getModel().isRollover()) {
                latar = this.warnaHover;
            }

            g2.setColor(latar);
            g2.fillRect(0, 0, w, h);

            g2.setColor(ditekan ? this.warnaBayanganBawah : this.warnaSorotAtas);
            g2.fillRect(0, 0, w, 2);
            g2.fillRect(0, 0, 2, h);

            g2.setColor(ditekan ? this.warnaSorotAtas : this.warnaBayanganBawah);
            g2.fillRect(0, Math.max(0, h - 2), w, 2);
            g2.fillRect(Math.max(0, w - 2), 0, 2, h);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Memformat tombol pixel bevel dengan warna, border, font, dan kursor yang konsisten.
     *
     * @param teks label tombol
     * @param warnaLatar warna latar normal tombol
     * @param warnaHover warna latar saat hover
     * @param warnaTekan warna latar saat ditekan
     * @param warnaTeks warna teks tombol
     * @param warnaBorder warna border luar tombol
     * @param warnaSorotAtas warna highlight bevel
     * @param warnaBayanganBawah warna shadow bevel
     * @param gayaFont gaya huruf
     * @return tombol terformat
     */
    private static JButton formatTombol(String teks, Color warnaLatar, Color warnaHover,
                                        Color warnaTekan, Color warnaTeks, Color warnaBorder,
                                        Color warnaSorotAtas, Color warnaBayanganBawah, int gayaFont)
    {
        JButton tombol = new TombolTema(
            teks, warnaLatar, warnaHover, warnaTekan, warnaSorotAtas, warnaBayanganBawah
        );
        tombol.setFont(new Font(FONT_UI, gayaFont, 12));
        tombol.setBackground(warnaLatar);
        tombol.setForeground(warnaTeks);
        tombol.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tombol.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(warnaBorder, 2),
            BorderFactory.createEmptyBorder(6, 13, 6, 13)
        ));
        return tombol;
    }

    /**
     * Mencari file foto barang pada direktori aset dengan beberapa kemungkinan ekstensi.
     *
     * @param idBarang kode unik barang berhuruf besar
     * @return objek BufferedImage jika ditemukan, atau null jika tidak ada
     */
    private static BufferedImage cariDanBacaGambar(String idBarang)
    {
        String[] kandidatNama = {
            idBarang + ".jpg",
            idBarang + ".png",
            idBarang + ".jpeg",
            "games/" + idBarang + ".jpg",
            "games/" + idBarang + ".png"
        };

        for (String nama : kandidatNama) {
            File file = cariFileAset(nama);
            if (file != null) {
                try {
                    BufferedImage img = ImageIO.read(file);
                    if (img != null) {
                        return img;
                    }
                } catch (IOException ignored) {
                    // Lanjut ke kandidat aset berikutnya bila gagal membaca file
                }
            }
        }
        return null;
    }

    /**
     * Mencari file di dalam direktori aset relatif terhadap working directory saat ini.
     *
     * @param namaRelatif nama file relatif di dalam folder aset
     * @return objek File bila ditemukan dan dapat dibaca, atau null
     */
    private static File cariFileAset(String namaRelatif)
    {
        String[] akarAset = {"Assets", "assets", "../Assets", "../assets"};
        for (String akar : akarAset) {
            File kandidat = new File(akar, namaRelatif);
            if (kandidat.isFile() && kandidat.canRead()) {
                return kandidat;
            }
        }
        return null;
    }

    /**
     * Menampilkan dialog simulasi pembayaran interaktif sesuai metode yang dipilih
     * (QRIS dengan kode QR placeholder pixel, Transfer Bank dengan pilihan bank
     * dan nomor rekening tujuan, atau Tunai dengan kalkulator kembalian langsung).
     *
     * @param induk komponen induk jendela dialog
     * @param metode nama metode pembayaran ("QRIS", "Transfer Bank", atau "Tunai")
     * @param totalTagihan total nominal pembayaran dalam rupiah
     * @param lamaSewa lama penyewaan dalam hari
     * @param jumlahItem jumlah board game yang disewa
     * @return true jika pengguna mengonfirmasi pembayaran berhasil, false jika dibatalkan
     */
    public static boolean tampilkanDialogProsesPembayaran(Component induk, String metode,
                                                          double totalTagihan, int lamaSewa,
                                                          int jumlahItem)
    {
        return tampilkanDialogProsesPembayaran(
            induk, metode, totalTagihan, lamaSewa, jumlahItem, 0.0
        );
    }

    /**
     * Menampilkan dialog simulasi pembayaran interaktif sesuai metode yang dipilih
     * (QRIS, Transfer Bank, Tunai, atau Saldo Wallet) sebelum transaksi dibuat.
     *
     * @param induk komponen induk jendela dialog
     * @param metode nama metode pembayaran
     * @param totalTagihan total nominal pembayaran dalam rupiah
     * @param lamaSewa lama penyewaan dalam hari
     * @param jumlahItem jumlah board game yang disewa
     * @param saldoWallet saldo wallet aktif penyewa dalam rupiah
     * @return true jika pengguna mengonfirmasi pembayaran berhasil, false jika dibatalkan
     */
    public static boolean tampilkanDialogProsesPembayaran(Component induk, String metode,
                                                          double totalTagihan, int lamaSewa,
                                                          int jumlahItem, double saldoWallet)
    {
        JPanel kontainer = new JPanel(new BorderLayout(0, 12));
        kontainer.setBackground(WARNA_CANVAS);
        kontainer.setPreferredSize(new Dimension(410, 400));

        JPanel kartuHeader = new JPanel(new BorderLayout(0, 4));
        kartuHeader.setBackground(WARNA_HEADER);
        kartuHeader.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel lblJudul = new JLabel("Pembayaran " + metode);
        lblJudul.setFont(new Font(FONT_UI, Font.BOLD, 16));
        lblJudul.setForeground(WARNA_SURFACE);

        JLabel lblRingkas = new JLabel(
            jumlahItem > 0
                ? jumlahItem + " board game | Lama sewa " + lamaSewa + " hari"
                : "Top Up Saldo Wallet"
        );
        lblRingkas.setFont(new Font(FONT_UI, Font.PLAIN, 12));
        lblRingkas.setForeground(WARNA_HEADER_SUB);

        kartuHeader.add(lblJudul, BorderLayout.NORTH);
        kartuHeader.add(lblRingkas, BorderLayout.SOUTH);

        JPanel kartuTagihan = new JPanel(new BorderLayout(0, 4));
        kartuTagihan.setBackground(WARNA_KARTU_KREM);
        kartuTagihan.setBorder(buatBorderKartuPixel(10, 14, 10, 14));

        JLabel lblKetTagihan = new JLabel("TOTAL TAGIHAN PEMBAYARAN");
        lblKetTagihan.setFont(new Font(FONT_UI, Font.BOLD, 11));
        lblKetTagihan.setForeground(WARNA_TEKS_SEKUNDER);

        JLabel lblNominal = new JLabel(formatRupiah(totalTagihan));
        lblNominal.setFont(new Font(FONT_UI, Font.BOLD, 22));
        lblNominal.setForeground(WARNA_AKSEN);

        kartuTagihan.add(lblKetTagihan, BorderLayout.NORTH);
        kartuTagihan.add(lblNominal, BorderLayout.SOUTH);

        JPanel kartuMetode = new JPanel(new BorderLayout(0, 10));
        kartuMetode.setBackground(WARNA_SURFACE);
        kartuMetode.setBorder(buatBorderKartuPixel(14, 16, 14, 16));

        JTextField txtUangTunai = new JTextField(String.valueOf((long) Math.ceil(totalTagihan)));
        if ("QRIS".equalsIgnoreCase(metode)) {
            JLabel lblQr = new JLabel(buatGambarQrPlaceholder(168), SwingConstants.CENTER);
            lblQr.setBorder(buatBorderFotoPixel());
            JLabel lblMerchant = new JLabel(
                "QRIS Merchant: Rent the Board! (NMID: ID1020260010)",
                SwingConstants.CENTER
            );
            lblMerchant.setFont(new Font(FONT_MONO, Font.BOLD, 11));
            lblMerchant.setForeground(WARNA_TEKS_UTAMA);

            JLabel lblPetunjuk = new JLabel(
                "Scan kode QR placeholder di atas, lalu klik Konfirmasi Bayar.",
                SwingConstants.CENTER
            );
            lblPetunjuk.setFont(new Font(FONT_UI, Font.PLAIN, 12));
            lblPetunjuk.setForeground(WARNA_TEKS_SEKUNDER);

            kartuMetode.add(lblMerchant, BorderLayout.NORTH);
            kartuMetode.add(lblQr, BorderLayout.CENTER);
            kartuMetode.add(lblPetunjuk, BorderLayout.SOUTH);
        } else if ("Transfer Bank".equalsIgnoreCase(metode)) {
            JPanel isiBank = new JPanel();
            isiBank.setOpaque(false);
            isiBank.setLayout(new BoxLayout(isiBank, BoxLayout.Y_AXIS));

            JLabel lblPilih = new JLabel("Pilih Bank Tujuan Transfer:");
            lblPilih.setFont(new Font(FONT_UI, Font.BOLD, 12));
            lblPilih.setAlignmentX(Component.LEFT_ALIGNMENT);

            JComboBox<String> cmbBank = new JComboBox<>(new String[] {
                "BCA - 8801 2026 0010 (Rent the Board!)",
                "BRI - 0021 2026 0010 (Rent the Board!)",
                "Mandiri - 1300 2026 0010 (Rent the Board!)"
            });
            cmbBank.setFont(new Font(FONT_MONO, Font.BOLD, 12));
            cmbBank.setAlignmentX(Component.LEFT_ALIGNMENT);
            cmbBank.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

            JLabel lblInfoRek = new JLabel("No. VA / Rekening: 8801 2026 0010");
            lblInfoRek.setFont(new Font(FONT_MONO, Font.BOLD, 13));
            lblInfoRek.setForeground(WARNA_AKSEN);
            lblInfoRek.setAlignmentX(Component.LEFT_ALIGNMENT);

            cmbBank.addActionListener(event -> {
                String pilihan = String.valueOf(cmbBank.getSelectedItem());
                lblInfoRek.setText("Rekening Tujuan: " + pilihan);
            });

            JLabel lblPetunjuk = new JLabel(
                "<html>Transfer sesuai nominal tepat hingga digit rupiah, lalu klik Konfirmasi Bayar.</html>"
            );
            lblPetunjuk.setFont(new Font(FONT_UI, Font.PLAIN, 12));
            lblPetunjuk.setForeground(WARNA_TEKS_SEKUNDER);
            lblPetunjuk.setAlignmentX(Component.LEFT_ALIGNMENT);

            isiBank.add(lblPilih);
            isiBank.add(Box.createVerticalStrut(6));
            isiBank.add(cmbBank);
            isiBank.add(Box.createVerticalStrut(14));
            isiBank.add(lblInfoRek);
            isiBank.add(Box.createVerticalStrut(10));
            isiBank.add(lblPetunjuk);
            kartuMetode.add(isiBank, BorderLayout.CENTER);
        } else if ("Saldo Wallet".equalsIgnoreCase(metode)) {
            JPanel isiWallet = new JPanel();
            isiWallet.setOpaque(false);
            isiWallet.setLayout(new BoxLayout(isiWallet, BoxLayout.Y_AXIS));

            JLabel lblJudulWallet = new JLabel("Ringkasan Pembayaran Saldo Wallet");
            lblJudulWallet.setFont(new Font(FONT_UI, Font.BOLD, 13));
            lblJudulWallet.setForeground(WARNA_TEKS_UTAMA);
            lblJudulWallet.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel lblSaldoAktif = new JLabel(
                "Saldo Wallet Saat Ini : " + formatRupiah(saldoWallet)
            );
            lblSaldoAktif.setFont(new Font(FONT_MONO, Font.BOLD, 12));
            lblSaldoAktif.setForeground(WARNA_TEKS_UTAMA);
            lblSaldoAktif.setAlignmentX(Component.LEFT_ALIGNMENT);

            double sisaSaldo = saldoWallet - totalTagihan;
            JLabel lblSisaSaldo = new JLabel(
                sisaSaldo >= 0
                    ? "Sisa Saldo Setelah Bayar : " + formatRupiah(sisaSaldo)
                    : "Saldo Kurang : " + formatRupiah(Math.abs(sisaSaldo))
            );
            lblSisaSaldo.setFont(new Font(FONT_UI, Font.BOLD, 13));
            lblSisaSaldo.setForeground(sisaSaldo >= 0 ? WARNA_STATUS_AMAN : WARNA_STATUS_BAHAYA);
            lblSisaSaldo.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel lblPetunjukWallet = new JLabel(
                sisaSaldo >= 0
                    ? "<html>Saldo Wallet mencukupi. Klik Konfirmasi Bayar untuk memotong saldo secara otomatis.</html>"
                    : "<html>Saldo Wallet tidak mencukupi. Silakan lakukan Top Up pada Panel Wallet terlebih dahulu.</html>"
            );
            lblPetunjukWallet.setFont(new Font(FONT_UI, Font.PLAIN, 12));
            lblPetunjukWallet.setForeground(WARNA_TEKS_SEKUNDER);
            lblPetunjukWallet.setAlignmentX(Component.LEFT_ALIGNMENT);

            isiWallet.add(lblJudulWallet);
            isiWallet.add(Box.createVerticalStrut(10));
            isiWallet.add(lblSaldoAktif);
            isiWallet.add(Box.createVerticalStrut(8));
            isiWallet.add(lblSisaSaldo);
            isiWallet.add(Box.createVerticalStrut(14));
            isiWallet.add(lblPetunjukWallet);
            kartuMetode.add(isiWallet, BorderLayout.CENTER);
        } else {
            JPanel isiTunai = new JPanel();
            isiTunai.setOpaque(false);
            isiTunai.setLayout(new BoxLayout(isiTunai, BoxLayout.Y_AXIS));

            JLabel lblInput = new JLabel("Uang Tunai Diterima di Loket (Rp):");
            lblInput.setFont(new Font(FONT_UI, Font.BOLD, 12));
            lblInput.setAlignmentX(Component.LEFT_ALIGNMENT);

            formatInputField(txtUangTunai);
            txtUangTunai.setAlignmentX(Component.LEFT_ALIGNMENT);
            txtUangTunai.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

            JPanel barisPreset = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            barisPreset.setOpaque(false);
            barisPreset.setAlignmentX(Component.LEFT_ALIGNMENT);
            JButton btnPas = buatTombolSekunder("Uang Pas");
            btnPas.addActionListener(event ->
                txtUangTunai.setText(String.valueOf((long) Math.ceil(totalTagihan)))
            );
            JButton btn100 = buatTombolSekunder("Rp100.000");
            btn100.addActionListener(event -> txtUangTunai.setText("100000"));
            JButton btn200 = buatTombolSekunder("Rp200.000");
            btn200.addActionListener(event -> txtUangTunai.setText("200000"));
            barisPreset.add(btnPas);
            barisPreset.add(btn100);
            barisPreset.add(btn200);

            JLabel lblKembalian = new JLabel("Kembalian: Rp0,00");
            lblKembalian.setFont(new Font(FONT_UI, Font.BOLD, 14));
            lblKembalian.setForeground(WARNA_STATUS_AMAN);
            lblKembalian.setAlignmentX(Component.LEFT_ALIGNMENT);

            Runnable updateKembalian = () -> {
                try {
                    double dibayar = Double.parseDouble(txtUangTunai.getText().trim());
                    double sisa = dibayar - totalTagihan;
                    if (sisa < 0) {
                        lblKembalian.setText("Kurang: " + formatRupiah(Math.abs(sisa)));
                        lblKembalian.setForeground(WARNA_STATUS_BAHAYA);
                    } else {
                        lblKembalian.setText("Kembalian: " + formatRupiah(sisa));
                        lblKembalian.setForeground(WARNA_STATUS_AMAN);
                    }
                } catch (NumberFormatException ignored) {
                    lblKembalian.setText("Masukkan nominal angka yang valid");
                    lblKembalian.setForeground(WARNA_STATUS_TUNGGU);
                }
            };
            txtUangTunai.getDocument().addDocumentListener(new DocumentListener()
            {
                @Override
                public void insertUpdate(DocumentEvent event)
                {
                    updateKembalian.run();
                }

                @Override
                public void removeUpdate(DocumentEvent event)
                {
                    updateKembalian.run();
                }

                @Override
                public void changedUpdate(DocumentEvent event)
                {
                    updateKembalian.run();
                }
            });
            updateKembalian.run();

            isiTunai.add(lblInput);
            isiTunai.add(Box.createVerticalStrut(6));
            isiTunai.add(txtUangTunai);
            isiTunai.add(Box.createVerticalStrut(8));
            isiTunai.add(barisPreset);
            isiTunai.add(Box.createVerticalStrut(14));
            isiTunai.add(lblKembalian);
            kartuMetode.add(isiTunai, BorderLayout.CENTER);
        }

        JPanel badan = new JPanel(new BorderLayout(0, 12));
        badan.setOpaque(false);
        badan.add(kartuTagihan, BorderLayout.NORTH);
        badan.add(kartuMetode, BorderLayout.CENTER);

        kontainer.add(kartuHeader, BorderLayout.NORTH);
        kontainer.add(badan, BorderLayout.CENTER);

        String[] opsi = {"Konfirmasi Bayar", "Batal"};
        while (true) {
            int pilihan = JOptionPane.showOptionDialog(
                induk,
                kontainer,
                "Proses Pembayaran - " + metode,
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                opsi,
                opsi[0]
            );
            if (pilihan != 0) {
                return false;
            }
            if ("Tunai".equalsIgnoreCase(metode)) {
                try {
                    double uang = Double.parseDouble(txtUangTunai.getText().trim());
                    if (uang < totalTagihan) {
                        JOptionPane.showMessageDialog(
                            induk,
                            "Uang tunai yang diterima masih kurang dari total tagihan.",
                            "Nominal Kurang",
                            JOptionPane.WARNING_MESSAGE
                        );
                        continue;
                    }
                } catch (NumberFormatException ignored) {
                    JOptionPane.showMessageDialog(
                        induk,
                        "Nominal uang tunai harus berupa angka yang valid.",
                        "Input Tidak Valid",
                        JOptionPane.ERROR_MESSAGE
                    );
                    continue;
                }
            } else if ("Saldo Wallet".equalsIgnoreCase(metode) && saldoWallet < totalTagihan) {
                JOptionPane.showMessageDialog(
                    induk,
                    "Saldo Wallet tidak mencukupi. Silakan Top Up pada Panel Wallet terlebih dahulu.",
                    "Saldo Tidak Mencukupi",
                    JOptionPane.WARNING_MESSAGE
                );
                return false;
            }
            return true;
        }
    }

    /**
     * Menghasilkan gambar QR Code placeholder yang dapat dipindai (memuat tautan
     * https://www.youtube.com/watch?v=p9qmtoT6XM0) untuk simulasi QRIS.
     *
     * @param ukuran lebar dan tinggi kanvas QR dalam piksel
     * @return objek ImageIcon berisi QR Code
     */
    private static ImageIcon buatGambarQrPlaceholder(int ukuran)
    {
        String[] matriksQr = {
            "#######...#....#.###..#######",
            "#.....#..#...########.#.....#",
            "#.###.#.#..#..#....#..#.###.#",
            "#.###.#..#..#.#.......#.###.#",
            "#.###.#..#..####.###..#.###.#",
            "#.....#...###.###.###.#.....#",
            "#######.#.#.#.#.#.#.#.#######",
            "........#..#.###.##..........",
            "###.#####...#....#..###...#..",
            "#..#.#.###.####.###.#.#..#..#",
            "..#####...###....#..#.....###",
            "..#.#...###.##.#.##.#...#..#.",
            "#####.###.##.###.#.####..#.##",
            ".##.##.#####.##..#...###.#..#",
            "##...###.#...#..#.#..#.#.#.##",
            ".#.##..##..#.#.#.####.#..#.#.",
            "##....#...#.#....#..####.#.##",
            "..###..##..##.#.###.#.#..##.#",
            "#..##.#.##.##.#.....####...##",
            ".##..#.##...##.###.......#.#.",
            "#.#..###...#...#.#..#####....",
            "........##.#.....#.##...#.###",
            "#######.#.#.....#####.#.##.##",
            "#.....#.####.##.###.#...##..#",
            "#.###.#.#...###..#..#####....",
            "#.###.#..#.####.#.......#.###",
            "#.###.#.#####.#...####.###..#",
            "#.....#.###.##..##..#...#..#.",
            "#######.#####.##.####.####.##"
        };

        BufferedImage img = new BufferedImage(ukuran, ukuran, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        g2.setColor(WARNA_SURFACE);
        g2.fillRect(0, 0, ukuran, ukuran);
        g2.setColor(WARNA_HEADER);
        g2.drawRect(0, 0, ukuran - 1, ukuran - 1);

        int modul = matriksQr.length;
        int sel = Math.max(3, (ukuran - 24) / modul);
        int awal = (ukuran - (modul * sel)) / 2;

        g2.setColor(WARNA_HEADER);
        for (int baris = 0; baris < modul; baris++) {
            String row = matriksQr[baris];
            for (int kolom = 0; kolom < row.length(); kolom++) {
                if (row.charAt(kolom) == '#') {
                    g2.fillRect(awal + kolom * sel, awal + baris * sel, sel, sel);
                }
            }
        }

        g2.dispose();
        return new ImageIcon(img);
    }

    /**
     * Menampilkan dialog struk Invoice berformat monospace dengan penanda status bayar.
     *
     * @param induk komponen induk jendela dialog
     * @param transaksi transaksi yang akan dicetak invoice-nya, tidak boleh null
     */
    public static void tampilkanDialogInvoice(Component induk, Transaksi transaksi)
    {
        if (transaksi == null) {
            return;
        }
        if ("Ditolak".equalsIgnoreCase(transaksi.getStatus())) {
            JOptionPane.showMessageDialog(
                induk,
                "Transaksi \"" + transaksi.getId()
                    + "\" berstatus Ditolak sehingga invoice tidak diterbitkan.",
                "Invoice Tidak Tersedia",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        boolean sudahLunas = "Sedang Disewa".equalsIgnoreCase(transaksi.getStatus())
            || "Selesai".equalsIgnoreCase(transaksi.getStatus());

        JPanel panelUtama = new JPanel(new BorderLayout(0, 8));
        panelUtama.setBackground(WARNA_CANVAS);

        JPanel bilahStatus = new JPanel(new BorderLayout());
        bilahStatus.setBackground(WARNA_SURFACE);
        bilahStatus.setBorder(buatBorderKartuPixel(8, 12, 8, 12));

        JLabel lblJudulStruk = new JLabel("STRUK INVOICE RESMI");
        lblJudulStruk.setFont(new Font(FONT_UI, Font.BOLD, 12));
        lblJudulStruk.setForeground(WARNA_TEKS_UTAMA);

        JLabel lblCap = new JLabel(
            sudahLunas ? " LUNAS - SUDAH DIBAYAR " : " TERBAYAR - MENUNGGU ADMIN "
        );
        lblCap.setOpaque(true);
        lblCap.setFont(new Font(FONT_MONO, Font.BOLD, 11));
        lblCap.setBackground(sudahLunas ? WARNA_BADGE_FAMILY_BG : WARNA_BADGE_CARD_BG);
        lblCap.setForeground(sudahLunas ? WARNA_BADGE_FAMILY_FG : WARNA_BADGE_CARD_FG);
        lblCap.setBorder(BorderFactory.createLineBorder(WARNA_HEADER, 1));

        bilahStatus.add(lblJudulStruk, BorderLayout.WEST);
        bilahStatus.add(lblCap, BorderLayout.EAST);

        Invoice invoice = new Invoice(transaksi);
        JTextArea areaInvoice = new JTextArea(invoice.cetakInvoice());
        areaInvoice.setEditable(false);
        areaInvoice.setFont(new Font(FONT_MONO, Font.PLAIN, 12));
        areaInvoice.setForeground(WARNA_TEKS_UTAMA);
        areaInvoice.setBackground(WARNA_SURFACE);
        areaInvoice.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JScrollPane scrollInvoice = new JScrollPane(areaInvoice);
        scrollInvoice.setPreferredSize(new Dimension(510, 355));
        scrollInvoice.setBorder(BorderFactory.createLineBorder(WARNA_HEADER, 2));

        panelUtama.add(bilahStatus, BorderLayout.NORTH);
        panelUtama.add(scrollInvoice, BorderLayout.CENTER);

        String[] opsi = {"Simpan Struk (.txt)", "Tutup"};
        int pilihan = JOptionPane.showOptionDialog(
            induk,
            panelUtama,
            "Invoice - " + transaksi.getId(),
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            opsi,
            opsi[1]
        );
        if (pilihan == 0) {
            File dirKerja = new File(System.getProperty("user.dir"));
            JFileChooser chooser = new JFileChooser(dirKerja);
            chooser.setDialogTitle("Simpan Struk Invoice");
            chooser.setSelectedFile(new File(dirKerja, "Invoice_" + transaksi.getId() + ".txt"));
            chooser.setFileFilter(new FileNameExtensionFilter("Teks (*.txt)", "txt"));
            if (chooser.showSaveDialog(induk) == JFileChooser.APPROVE_OPTION) {
                File target = chooser.getSelectedFile();
                if (!target.getName().toLowerCase(Locale.ROOT).endsWith(".txt")) {
                    target = new File(target.getParentFile(), target.getName() + ".txt");
                }
                try {
                    Files.writeString(
                        target.toPath(), invoice.cetakInvoice(), StandardCharsets.UTF_8
                    );
                    JOptionPane.showMessageDialog(
                        induk,
                        "Struk invoice berhasil disimpan ke:\n" + target.getAbsolutePath(),
                        "Struk Disimpan",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (IOException ioException) {
                    JOptionPane.showMessageDialog(
                        induk,
                        "Gagal menyimpan struk (" + ioException.getClass().getSimpleName()
                            + "): " + ioException.getMessage()
                            + "\nCoba simpan ke folder proyek atau folder lain yang tidak dibatasi.",
                        "Kesalahan",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }
    }

    /**
     * Mengurutkan salinan daftar board game sesuai mode pengurutan yang dipilih pada katalog.
     *
     * @param sumber daftar barang sumber
     * @param modeUrutan pilihan urutan ("Tarif Termurah", "Tarif Termahal",
     *                   "Nama A-Z", atau "Default")
     * @return daftar barang baru dalam urutan terpilih
     */
    public static ArrayList<Barang> urutkanDaftarBarang(ArrayList<Barang> sumber, String modeUrutan)
    {
        ArrayList<Barang> hasil = new ArrayList<>(sumber);
        if ("Tarif Termurah".equalsIgnoreCase(modeUrutan)) {
            hasil.sort(Comparator.comparingDouble(Barang::getTarifHarian));
        } else if ("Tarif Termahal".equalsIgnoreCase(modeUrutan)) {
            hasil.sort(Comparator.comparingDouble(Barang::getTarifHarian).reversed());
        } else if ("Nama A-Z".equalsIgnoreCase(modeUrutan)) {
            hasil.sort(Comparator.comparing(Barang::getNama, String.CASE_INSENSITIVE_ORDER));
        }
        return hasil;
    }

    /**
     * Menyimpan file foto board game baru ke dalam folder Assets/ sesuai ID barang
     * dan membersihkan cache gambar terkait agar foto baru langsung tampil.
     *
     * @param idBarang kode unik board game
     * @param sumberFile file gambar sumber berformat PNG atau JPG/JPEG
     * @return true jika penyimpanan berhasil, false jika file tidak valid
     * @throws IOException jika terjadi kegagalan saat menyalin file foto
     */
    public static boolean simpanFotoBarang(String idBarang, File sumberFile) throws IOException
    {
        if (idBarang == null || idBarang.trim().isEmpty()
                || sumberFile == null || !sumberFile.isFile()) {
            return false;
        }

        String namaAsli = sumberFile.getName().toLowerCase(Locale.ROOT);
        String ekstensi;
        if (namaAsli.endsWith(".png")) {
            ekstensi = ".png";
        } else if (namaAsli.endsWith(".jpeg")) {
            ekstensi = ".jpeg";
        } else if (namaAsli.endsWith(".jpg")) {
            ekstensi = ".jpg";
        } else {
            return false;
        }

        File dirAset = cariAtauBuatDirektoriAset();
        String idBersih = idBarang.trim().toUpperCase(Locale.ROOT);

        String[] ekstensiLama = {".jpg", ".png", ".jpeg"};
        for (String ext : ekstensiLama) {
            File fileLama = new File(dirAset, idBersih + ext);
            if (fileLama.isFile()) {
                fileLama.delete();
            }
        }

        File tujuan = new File(dirAset, idBersih + ekstensi);
        Files.copy(sumberFile.toPath(), tujuan.toPath(), StandardCopyOption.REPLACE_EXISTING);
        hapusCacheGambar(idBersih);
        return true;
    }

    /**
     * Menghapus seluruh entri cache ikon untuk ID barang tertentu agar render ulang memuat aset baru.
     *
     * @param idBarang kode unik barang
     */
    public static void hapusCacheGambar(String idBarang)
    {
        if (idBarang == null) {
            return;
        }
        String awalan = idBarang.trim().toUpperCase(Locale.ROOT) + "_";
        Iterator<String> it = CACHE_GAMBAR.keySet().iterator();
        while (it.hasNext()) {
            if (it.next().startsWith(awalan)) {
                it.remove();
            }
        }
    }

    /**
     * Menskalakan foto asli secara sinkron dan tajam menjadi gambar kotak proporsional
     * di atas latar bersih tanpa efek blur akibat penskalaan satu langkah.
     *
     * @param sumber gambar asli
     * @param ukuran panjang sisi gambar kotak tujuan dalam piksel
     * @return gambar kotak beresolusi target
     */
    private static BufferedImage ubahUkuranKotakRapi(BufferedImage sumber, int ukuran)
    {
        int pad = Math.max(2, ukuran / 24);
        int ruang = Math.max(8, ukuran - 2 * pad);
        double skala = Math.min(
            (double) ruang / sumber.getWidth(),
            (double) ruang / sumber.getHeight()
        );
        int lebarBaru = Math.max(1, (int) Math.round(sumber.getWidth() * skala));
        int tinggiBaru = Math.max(1, (int) Math.round(sumber.getHeight() * skala));

        BufferedImage hasilSkala = sumber;
        int w = sumber.getWidth();
        int h = sumber.getHeight();
        while (w / 2 >= lebarBaru && h / 2 >= tinggiBaru) {
            w = Math.max(lebarBaru, w / 2);
            h = Math.max(tinggiBaru, h / 2);
            BufferedImage langkah = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gLangkah = langkah.createGraphics();
            gLangkah.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR
            );
            gLangkah.setRenderingHint(
                RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY
            );
            gLangkah.drawImage(hasilSkala, 0, 0, w, h, null);
            gLangkah.dispose();
            hasilSkala = langkah;
        }

        BufferedImage kanvas = new BufferedImage(ukuran, ukuran, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = kanvas.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(WARNA_SURFACE);
        g2.fillRect(0, 0, ukuran, ukuran);

        int x = (ukuran - lebarBaru) / 2;
        int y = (ukuran - tinggiBaru) / 2;
        g2.drawImage(hasilSkala, x, y, lebarBaru, tinggiBaru, null);
        g2.dispose();
        return kanvas;
    }

    /**
     * Mencari direktori aset yang ada atau membuatnya jika belum tersedia.
     *
     * @return objek File menunjuk ke direktori aset
     */
    private static File cariAtauBuatDirektoriAset()
    {
        String[] akarAset = {"Assets", "assets", "../Assets", "../assets"};
        for (String akar : akarAset) {
            File dir = new File(akar);
            if (dir.isDirectory()) {
                return dir;
            }
        }
        File fallback = new File("Assets");
        if (!fallback.exists()) {
            fallback.mkdirs();
        }
        return fallback;
    }

    /**
     * Memeriksa apakah baris tabel tertentu sedang berada di bawah kursor mouse.
     *
     * @param table tabel target
     * @param row indeks baris
     * @return true jika baris sedang disorot mouse
     */
    private static boolean adalahBarisHover(JTable table, int row)
    {
        Object nilaiHover = table.getClientProperty("barisHover");
        return nilaiHover instanceof Integer && ((Integer) nilaiHover).intValue() == row;
    }

    /**
     * Menggambar ikon pixel art cadangan berdasarkan kategori board game bila foto tidak ada.
     *
     * @param kategori nama sederhana kelas kategori barang
     * @param ukuran ukuran sisi ikon dalam piksel
     * @return gambar ikon pixel berukuran ukuran x ukuran
     */
    private static BufferedImage gambarIkonKategoriPixel(String kategori, int ukuran)
    {
        BufferedImage img = new BufferedImage(ukuran, ukuran, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        int u = Math.max(2, ukuran / 16);
        int ox = (ukuran - 16 * u) / 2;
        int oy = (ukuran - 16 * u) / 2;

        if ("CardGame".equalsIgnoreCase(kategori)) {
            g2.setColor(WARNA_BADGE_CARD_BG);
            g2.fillRect(0, 0, ukuran, ukuran);
            g2.setColor(WARNA_HEADER);
            g2.fillRect(ox + 3 * u, oy + 2 * u, 7 * u, 10 * u);
            g2.setColor(WARNA_AKSEN);
            g2.fillRect(ox + 4 * u, oy + 3 * u, 5 * u, 8 * u);
            g2.setColor(WARNA_HEADER);
            g2.fillRect(ox + 6 * u, oy + 4 * u, 7 * u, 10 * u);
            g2.setColor(WARNA_SURFACE);
            g2.fillRect(ox + 7 * u, oy + 5 * u, 5 * u, 8 * u);
            g2.setColor(WARNA_STATUS_BAHAYA);
            g2.fillRect(ox + 9 * u, oy + 8 * u, 2 * u, 2 * u);
        } else if ("FamilyBoardGame".equalsIgnoreCase(kategori)) {
            g2.setColor(WARNA_BADGE_FAMILY_BG);
            g2.fillRect(0, 0, ukuran, ukuran);
            g2.setColor(WARNA_HEADER);
            g2.fillRect(ox + 3 * u, oy + 3 * u, 10 * u, 10 * u);
            g2.setColor(WARNA_SURFACE);
            g2.fillRect(ox + 4 * u, oy + 4 * u, 8 * u, 8 * u);
            g2.setColor(WARNA_STATUS_AMAN);
            g2.fillRect(ox + 5 * u, oy + 5 * u, 2 * u, 2 * u);
            g2.fillRect(ox + 9 * u, oy + 9 * u, 2 * u, 2 * u);
            g2.fillRect(ox + 9 * u, oy + 5 * u, 2 * u, 2 * u);
            g2.fillRect(ox + 5 * u, oy + 9 * u, 2 * u, 2 * u);
        } else {
            g2.setColor(WARNA_BADGE_STRATEGY_BG);
            g2.fillRect(0, 0, ukuran, ukuran);
            g2.setColor(WARNA_HEADER);
            g2.fillRect(ox + 3 * u, oy + 4 * u, 2 * u, 3 * u);
            g2.fillRect(ox + 7 * u, oy + 4 * u, 2 * u, 3 * u);
            g2.fillRect(ox + 11 * u, oy + 4 * u, 2 * u, 3 * u);
            g2.fillRect(ox + 3 * u, oy + 6 * u, 10 * u, 7 * u);
            g2.setColor(WARNA_BADGE_STRATEGY_FG);
            g2.fillRect(ox + 4 * u, oy + 7 * u, 8 * u, 5 * u);
            g2.setColor(WARNA_AKSEN);
            g2.fillRect(ox + 7 * u, oy + 9 * u, 2 * u, 3 * u);
        }

        g2.dispose();
        return img;
    }

    /**
     * Memeriksa apakah suatu indeks terdapat pada array target.
     *
     * @param daftarIndeks array indeks
     * @param target indeks yang diperiksa
     * @return true jika ditemukan, false jika tidak
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
