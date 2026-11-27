package payment;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Kelas strategi pembayaran secara Tunai di loket rental.
 *
 * @author Irvan Almidar
 * @version 2026.11.27
 */
public class Tunai implements Pembayaran
{
    /**
     * Membuat objek metode pembayaran tunai.
     */
    public Tunai()
    {
    }

    /**
     * Memproses pembayaran secara tunai dan menghasilkan pesan konfirmasi.
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
        return "Pembayaran secara Tunai sebesar "
            + formatRupiah.format(jumlah) + " berhasil diproses.";
    }
}
