import java.util.ArrayList;
import java.util.Scanner;

/**
 * Main class untuk menjalankan aplikasi BoardGameRent.
 */
public class Main {

    public static final Scanner scanner = new Scanner(System.in);

    private static Akun akunAktif;
    private static Admin admin;
    private static ArrayList<Akun> daftarAkun = new ArrayList<>();

    /**
     * Method utama program.
     *
     * @param args argumen program
     */

public static void main(String[] args) {

    // Membuat daftar barang bersama
    DaftarBarang daftarBarang = new DaftarBarang();

    // Membuat minimal 6 board game
    daftarBarang.tambahBarang(
        new CardGame("BG001", "UNO", 5000, 2000)
    );

    daftarBarang.tambahBarang(
        new CardGame("BG002", "Exploding Kittens", 7000, 3000)
    );

    daftarBarang.tambahBarang(
        new FamilyBoardGame("BG003", "Monopoly", 10000, 5000, 10000)
    );

    daftarBarang.tambahBarang(
        new FamilyBoardGame("BG004", "Ludo", 5000, 2000, 5000)
    );

    daftarBarang.tambahBarang(
        new StrategyGame("BG005", "Catan", 15000, 7000, 5000)
    );

    daftarBarang.tambahBarang(
        new StrategyGame("BG006", "Chess", 8000, 3000, 2000)
    );

    // Membuat akun Admin
    admin = new Admin(
        "ADM001", "admin", "admin123", daftarBarang
    );

    // Membuat akun Penyewa
    Penyewa penyewa1 = new Penyewa(
        "P001", "penyewa", "12345", daftarBarang
    );

    // Menjalankan aplikasi melalui GUI
    javax.swing.SwingUtilities.invokeLater(() -> {
        new LoginFrame(admin, penyewa1);
    });
}


    /**
     * Memproses login pengguna.
     *
     * @throws LoginGagalException jika login salah
     */
    public static void login() throws LoginGagalException {

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        for (Akun akun : daftarAkun) {
            if (akun.login(username, password)) {
                akunAktif = akun;
                System.out.println("Login berhasil!");
                return;
            }
        }

        throw new LoginGagalException(
            "Username atau password salah."
        );
    }

    /**
     * Menampilkan menu Admin.
     */
    public static void menuAdmin() {

        int pilihan;

        do {
            System.out.println("\n===== MENU ADMIN =====");
            System.out.println("1. Kelola Barang");
            System.out.println("2. Lihat Transaksi Menunggu");
            System.out.println("3. Terima Transaksi");
            System.out.println("4. Konfirmasi Pengembalian");
            System.out.println("0. Logout");
            System.out.print("Pilih menu: ");

            try {
                pilihan = Integer.parseInt(scanner.nextLine());

                switch (pilihan) {
                    case 1:
                        admin.kelolaBarang();
                        break;
                    case 2:
                          admin.lihatTransaksiMenunggu();
                        break;
                    case 3:
                        System.out.println(
                            "Hubungkan dengan proses penerimaan transaksi."
                        );
                        break;
                    case 4:
                        System.out.println(
                            "Hubungkan dengan proses pengembalian."
                        );
                        break;
                    case 0:
                        System.out.println("Logout berhasil.");
                        break;
                    default:
                        System.out.println("Pilihan tidak tersedia.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Masukkan angka yang benar.");
                pilihan = -1;
            }
        } while (pilihan != 0);
    }

    /**
     * Menampilkan menu Penyewa.
     *
     * @param penyewa akun penyewa yang sedang aktif
     */
    public static void menuPenyewa(Penyewa penyewa) {

        int pilihan;

        do {
            System.out.println("\n===== MENU PENYEWA =====");
            System.out.println("1. Lihat Barang");
            System.out.println("2. Tambah Barang ke Keranjang");
            System.out.println("3. Hapus Barang dari Keranjang");
            System.out.println("4. Buat Transaksi");
            System.out.println("5. Lihat Riwayat");
            System.out.println("0. Logout");
            System.out.print("Pilih menu: ");

            try {
                pilihan = Integer.parseInt(scanner.nextLine());

                switch (pilihan) {
                    case 1:
                        penyewa.lihatBarang();
                        break;
                    case 2:
                        System.out.println("Hubungkan dengan keranjang.");
                        break;
                    case 3:
                        System.out.println("Hubungkan dengan keranjang.");
                        break;
                    case 4:
                        System.out.println("Hubungkan dengan proses transaksi.");
                        break;
                    case 5:
                        penyewa.lihatRiwayat();
                        break;
                    case 0:
                        System.out.println("Logout berhasil.");
                        break;
                    default:
                        System.out.println("Pilihan tidak tersedia.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Masukkan angka yang benar.");
                pilihan = -1;
            }
        } while (pilihan != 0);
    }
}
