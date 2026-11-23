/**
 * Kelas untuk menangani pembayaran secara tunai.
 *
 * @author Irvan Almidar
 * @version 1.0
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
     * Memproses pembayaran menggunakan uang tunai.
     *
     * @param jumlah jumlah uang yang harus dibayarkan
     * @return informasi hasil pembayaran
     * @throws IllegalArgumentException jika jumlah tidak valid
     */
    @Override
    public String prosesPembayaran(double jumlah)
    {
        if (!Double.isFinite(jumlah) || jumlah <= 0) {
            throw new IllegalArgumentException(
                "Jumlah pembayaran harus lebih dari 0 dan valid."
            );
        }

        return "Pembayaran secara Tunai sebesar Rp"
                + String.format("%,.0f", jumlah)
                + " berhasil diproses.";
    }
}
