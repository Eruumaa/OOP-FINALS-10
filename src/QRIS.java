
/**
 * Kelas untuk menangani pembayaran menggunakan QRIS.
 *
 * @author Irvan Almidar
 * @version 1.0
 */
public class QRIS implements Pembayaran {

    /**
     * Memproses pembayaran menggunakan QRIS.
     *
     * @param jumlah jumlah uang yang harus dibayarkan
     * @return informasi hasil pembayaran
     * @throws IllegalArgumentException jika jumlah tidak valid
     */
    @Override
    public String prosesPembayaran(double jumlah) {
        if (jumlah <= 0) {
            throw new IllegalArgumentException(
                "Jumlah pembayaran harus lebih dari 0."
            );
        }

        return "Pembayaran melalui QRIS sebesar Rp"
                + String.format("%,.0f", jumlah)
                + " berhasil diproses.";
    }
}
