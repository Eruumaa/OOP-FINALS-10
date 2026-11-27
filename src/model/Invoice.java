package model;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Kelas untuk menyusun dan mencetak rincian struk invoice transaksi penyewaan board game.
 *
 * @author Irvan Almidar
 * @version 2026.11.27
 */
public class Invoice
{
    private Transaksi transaksi;
    private double total;
    private double denda;

    /**
     * Membuat objek invoice baru berdasarkan data transaksi terkini.
     *
     * @param transaksi transaksi yang akan dicetak struknya, tidak boleh {@code null}
     * @throws IllegalArgumentException jika {@code transaksi} bernilai {@code null}
     */
    public Invoice(Transaksi transaksi)
    {
        if (transaksi == null) {
            throw new IllegalArgumentException("Transaksi tidak boleh null.");
        }

        this.transaksi = transaksi;
        this.total = transaksi.hitungTotal();
        this.denda = transaksi.hitungDenda();
    }

    /**
     * Mengembalikan objek transaksi yang terkait dengan invoice ini.
     *
     * @return objek {@link Transaksi}
     */
    public Transaksi getTransaksi()
    {
        return this.transaksi;
    }

    /**
     * Mengembalikan total biaya sewa pada invoice ini.
     *
     * @return total biaya sewa dalam rupiah
     */
    public double getTotal()
    {
        return this.total;
    }

    /**
     * Mengembalikan besaran denda keterlambatan pada invoice ini.
     *
     * @return total denda keterlambatan dalam rupiah
     */
    public double getDenda()
    {
        return this.denda;
    }

    /**
     * Menyusun teks struk invoice terformat yang siap ditampilkan pada dialog monospace.
     *
     * @return teks lengkap invoice penyewaan
     */
    public String cetakInvoice()
    {
        NumberFormat formatRupiah =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

        String tanggalKembali = (this.transaksi.getTanggalKembali() == null)
            ? "-"
            : this.transaksi.getTanggalKembali().toString();

        StringBuilder invoice = new StringBuilder();
        invoice.append("========== INVOICE BOARDGAMERENT ==========\n");
        invoice.append("ID Transaksi      : ").append(this.transaksi.getId()).append("\n");
        invoice.append("Nama Penyewa      : ").append(this.transaksi.getPenyewa().getUsername()).append("\n");
        invoice.append("Lama Sewa         : ").append(this.transaksi.getLamaSewa()).append(" hari\n");
        invoice.append("-------------------------------------------\n");
        invoice.append("Daftar Barang:\n");

        for (Barang item : this.transaksi.getBarang()) {
            invoice.append("- ")
                   .append(item.getNama())
                   .append(" (")
                   .append(item.getClass().getSimpleName())
                   .append(") - ")
                   .append(formatRupiah.format(item.hitungBiaya(this.transaksi.getLamaSewa())))
                   .append("\n");
        }

        invoice.append("-------------------------------------------\n");
        invoice.append("Tanggal Sewa      : ").append(this.transaksi.getTanggalSewa()).append("\n");
        invoice.append("Jatuh Tempo       : ").append(this.transaksi.getTanggalJatuhTempo()).append("\n");
        invoice.append("Tanggal Kembali   : ").append(tanggalKembali).append("\n");
        invoice.append("Metode Pembayaran : ")
               .append(this.transaksi.getPembayaran().getClass().getSimpleName()).append("\n");
        invoice.append("Total Sewa        : ").append(formatRupiah.format(this.total)).append("\n");
        invoice.append("Denda             : ").append(formatRupiah.format(this.denda)).append("\n");
        invoice.append("Total Bayar       : ")
               .append(formatRupiah.format(this.total + this.denda)).append("\n");
        invoice.append("Status            : ").append(this.transaksi.getStatus()).append("\n");
        invoice.append("===========================================\n");

        return invoice.toString();
    }
}
