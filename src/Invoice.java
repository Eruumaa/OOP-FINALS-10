
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Kelas untuk membuat dan mencetak invoice transaksi penyewaan board game.
 *
 * @author Irvan Almidar
 * @version 1.0
 */
public class Invoice
{
    private Transaksi transaksi;
    private double total;
    private double denda;

    /**
     * Membuat invoice berdasarkan data transaksi.
     *
     * @param transaksi transaksi yang akan dibuatkan invoice
     * @throws IllegalArgumentException jika transaksi bernilai null
     */
    public Invoice(Transaksi transaksi)
    {
        if (transaksi == null) {
            throw new IllegalArgumentException(
                "Transaksi tidak boleh null."
            );
        }

        this.transaksi = transaksi;
        this.total = transaksi.hitungTotal();
        this.denda = transaksi.hitungDenda();
    }

    /**
     * Mengembalikan objek transaksi yang terkait dengan invoice ini.
     *
     * @return objek transaksi
     */
    public Transaksi getTransaksi()
    {
        return this.transaksi;
    }

    /**
     * Mengembalikan total biaya sewa pada invoice ini.
     *
     * @return total biaya sewa
     */
    public double getTotal()
    {
        return this.total;
    }

    /**
     * Mengembalikan total denda keterlambatan pada invoice ini.
     *
     * @return total denda keterlambatan
     */
    public double getDenda()
    {
        return this.denda;
    }

    /**
     * Membuat informasi invoice berdasarkan data transaksi.
     *
     * @return teks invoice yang berisi informasi penyewaan
     */
    public String cetakInvoice()
    {
        NumberFormat formatRupiah =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

        StringBuilder invoice = new StringBuilder();

        invoice.append("\n========== INVOICE BOARDGAMERENT ==========\n");
        invoice.append("ID Transaksi     : ")
               .append(this.transaksi.getId()).append("\n");

        invoice.append("Nama Penyewa     : ")
               .append(this.transaksi.getPenyewa().getUsername()).append("\n");

        invoice.append("-------------------------------------------\n");
        invoice.append("Daftar Barang:\n");

        for (Barang item : this.transaksi.getBarang()) {
            invoice.append("- ")
                   .append(item.getNama())
                   .append(" (")
                   .append(item.getClass().getSimpleName())
                   .append(")\n");
        }

        invoice.append("-------------------------------------------\n");

        invoice.append("Tanggal Sewa     : ")
               .append(this.transaksi.getTanggalSewa()).append("\n");

        invoice.append("Jatuh Tempo      : ")
               .append(this.transaksi.getTanggalJatuhTempo()).append("\n");

        invoice.append("Metode Pembayaran: ")
               .append(this.transaksi.getPembayaran()
                   .getClass().getSimpleName()).append("\n");

        invoice.append("Total Sewa       : ")
               .append(formatRupiah.format(this.total)).append("\n");

        invoice.append("Denda            : ")
               .append(formatRupiah.format(this.denda)).append("\n");

        invoice.append("Status           : ")
               .append(this.transaksi.getStatus()).append("\n");

        invoice.append("===========================================\n");

        return invoice.toString();
    }
}
