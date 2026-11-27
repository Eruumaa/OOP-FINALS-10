import java.util.ArrayList;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import model.Admin;
import model.Akun;
import model.CardGame;
import model.DaftarBarang;
import model.FamilyBoardGame;
import model.Penyewa;
import model.StrategyGame;
import view.LoginFrame;

/**
 * Titik masuk utama (entry point) aplikasi GUI Swing BoardGameRent.
 *
 * <p>Kelas ini menginisialisasi satu objek katalog {@link DaftarBarang} bersama,
 * enam board game dari tiga kategori ({@link CardGame}, {@link FamilyBoardGame},
 * dan {@link StrategyGame}), serta daftar akun pengguna sebelum menampilkan
 * jendela {@link LoginFrame}.</p>
 *
 * @author M. Yogi Kafrawi
 * @version 2026.11.27
 */
public class Main
{
    /**
     * Membuat instance {@code Main}.
     */
    public Main()
    {
    }

    /**
     * Menjalankan inisialisasi data awal dan membuka jendela login aplikasi GUI.
     *
     * @param args argumen baris perintah (tidak digunakan)
     */
    public static void main(String[] args)
    {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Gunakan Look and Feel bawaan jika sistem tidak mendukung
        }

        DaftarBarang daftarBarang = new DaftarBarang();
        daftarBarang.tambahBarang(new CardGame("BG01", "UNO No Mercy", 15000, 5000));
        daftarBarang.tambahBarang(new CardGame("BG02", "Exploding Kittens", 20000, 5000));
        daftarBarang.tambahBarang(new FamilyBoardGame("BG03", "Monopoly Classic", 25000, 8000, 10000));
        daftarBarang.tambahBarang(new FamilyBoardGame("BG04", "Cluedo", 30000, 8000, 12000));
        daftarBarang.tambahBarang(new StrategyGame("BG05", "Catan", 40000, 12000, 5000));
        daftarBarang.tambahBarang(new StrategyGame("BG06", "Ticket to Ride", 35000, 10000, 5000));

        Admin admin = new Admin("ADM01", "admin", "admin123", daftarBarang);
        Penyewa budi = new Penyewa("P01", "budi", "budi123", daftarBarang);
        Penyewa siti = new Penyewa("P02", "siti", "siti123", daftarBarang);

        ArrayList<Akun> daftarAkun = new ArrayList<>();
        daftarAkun.add(admin);
        daftarAkun.add(budi);
        daftarAkun.add(siti);

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame(daftarAkun, admin);
            loginFrame.setVisible(true);
        });
    }
}
