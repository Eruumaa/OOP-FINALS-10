package payment;

/**
 * Interface untuk memproses pembayaran penyewaan board game pada sistem BoardGameRent.
 *
 * @author Irvan Almidar
 * @version 2026.11.27
 */
public interface Pembayaran
{
    /**
     * Memproses pembayaran sesuai metode yang dipilih pengguna.
     *
     * @param jumlah jumlah nominal yang harus dibayarkan dalam rupiah
     * @return teks konfirmasi hasil pemrosesan pembayaran
     * @throws IllegalArgumentException jika jumlah pembayaran tidak valid
     */
    public String prosesPembayaran(double jumlah);
}
