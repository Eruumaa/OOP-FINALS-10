
import java.awt.*;
import javax.swing.*;

/**
 * Tampilan login aplikasi BoardGameRent.
 *
 * @author Yogi
 * @version 1.0
 */
public class LoginFrame extends JFrame
{
    private Admin admin;
    private Penyewa penyewa;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    /**
     * Membuat tampilan login.
     *
     * @param admin akun administrator
     * @param penyewa akun penyewa
     */
    public LoginFrame(Admin admin, Penyewa penyewa)
    {
        this.admin = admin;
        this.penyewa = penyewa;

        setTitle("BoardGameRent - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1, 10, 10));
        panel.setBorder(
            BorderFactory.createEmptyBorder(25, 35, 25, 35)
        );

        JLabel title = new JLabel(
            "BOARD GAMERENT",
            SwingConstants.CENTER
        );
        title.setFont(new Font("Arial", Font.BOLD, 22));

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();

        btnLogin = new JButton("LOGIN");

        panel.add(title);
        panel.add(new JLabel("Username:"));
        panel.add(txtUsername);
        panel.add(new JLabel("Password:"));
        panel.add(txtPassword);

        add(panel, BorderLayout.CENTER);
        add(btnLogin, BorderLayout.SOUTH);

        btnLogin.addActionListener(e -> prosesLogin());

        setVisible(true);
    }

    /**
     * Memproses login Admin atau Penyewa.
     */

private void prosesLogin()
{
    String username = txtUsername.getText().trim();
    String password = new String(
        txtPassword.getPassword()
    );

    if (username.isEmpty() || password.isEmpty())
    {
        JOptionPane.showMessageDialog(
            this,
            "Username dan password harus diisi!"
        );
        return;
    }

    try
    {
        if (admin.login(username, password))
        {
            JOptionPane.showMessageDialog(
                this,
                "Login Admin berhasil!"
            );

            dispose();

            AdminFrame frame = new AdminFrame(
                admin,
                () -> new LoginFrame(admin, penyewa)
            );

            frame.setVisible(true);
        }
        else if (penyewa.login(username, password))
        {
            JOptionPane.showMessageDialog(
                this,
                "Login Penyewa berhasil!"
            );

            dispose();

            PenyewaFrame frame = new PenyewaFrame(
                penyewa,
                admin,
                () -> new LoginFrame(admin, penyewa)
            );

            frame.setVisible(true);
        }
        else
        {
            JOptionPane.showMessageDialog(
                this,
                "Username atau password salah!",
                "Login Gagal",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    catch (Exception ex)
    {
        JOptionPane.showMessageDialog(
            this,
            ex.getMessage(),
            "Login Gagal",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
}