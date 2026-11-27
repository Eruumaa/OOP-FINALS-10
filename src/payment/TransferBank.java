package payment;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Kelas strategi pembayaran melalui Transfer Bank.
 *
 * @author Irvan Almidar
 * @version 2026.11.27
 */
public class TransferBank implements Pembayaran
{
    /**
     * Membuat objek metode pembayaran transfer bank.
     */
    public TransferBank()
    {
    }

    /**
     * Memproses pembayaran melalui transfer bank dan menghasilkan pesan konfirmasi.
     *
     * @param jumlah jumlah nominal pembayaran dalam rupiah, harus lebih dari 0
     * @return teks konfirmasi pembayaran berhasil
     * @throws IllegalArgumentException jika {@code jumlah} tidak valid atau kurang dari sama dengan 0
     */
    @Override
    public String prosesPembayaran(double jumlah)
    {
        if (!Double.isFinite(jumlah) || jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah pembayaran harus lebih dari 0 dan valid.");
        }

        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
        return "Pembayaran melalui Transfer Bank sebesar "
            + formatRupiah.format(jumlah) + " berhasil diproses.";
    }
}
