import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import service.RentalDataStore;
import view.LoginFrame;

/**
 * Titik masuk utama (entry point) aplikasi GUI Swing BoardGameRent.
 *
 * Kelas ini memuat data persisten melalui RentalDataStore (atau menginisialisasi
 * data bawaan bila file belum ada) dan membuka jendela LoginFrame dengan
 * callback penyimpanan otomatis setiap kali terjadi perubahan data.
 *
 * @author M. Yogi Kafrawi
 * @version 2026.11.27
 */
public class Main
{
    /**
     * Membuat instance Main.
     */
    public Main()
    {
    }

    /**
     * Menjalankan inisialisasi data persisten dan membuka jendela login aplikasi GUI.
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

        RentalDataStore dataStore = new RentalDataStore();

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame(
                dataStore.getDaftarAkunRef(),
                dataStore.getAdmin(),
                dataStore::simpanSemua
            );
            loginFrame.setVisible(true);
        });
    }
}
