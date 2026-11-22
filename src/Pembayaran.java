
/**
 * Interface untuk proses pembayaran penyewaan board game.
 *
 * @author Irvan Almidar
 * @version 1.0
 */
public interface Pembayaran {

    /**javac src/Pembayaran.java
     * Memproses pembayaran sesuai metode yang digunakan.
     *
     * @param jumlah jumlah uang yang harus dibayarkan
     * @return informasi hasil proses pembayaran
     * @throws IllegalArgumentException jika jumlah pembayaran tidak valid
     */
    String prosesPembayaran(double jumlah);
}
