package ASimulatorSystem;

import ASimulatorSystem.service.AuthService;
import ASimulatorSystem.ui.AtmUi;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Entry screen for the desktop ATM. */
public class Login extends JFrame {
    private final JTextField cardField = AtmUi.field();
    private final JPasswordField pinField = AtmUi.passwordField();
    private final JButton signIn = AtmUi.primary("SIGN IN");
    private final JButton clear = AtmUi.secondary("CLEAR");
    private final JButton signUp = AtmUi.secondary("CREATE NEW ACCOUNT");
    private final AuthService authService = new AuthService();

    public Login() {
        AtmUi.frame(this, "Secure Login", 900, 600);
        build();
        setVisible(true);
    }

    private void build() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(AtmUi.BG);
        root.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(38, 48, 38, 48));
        card.setPreferredSize(new Dimension(470, 465));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel brand = AtmUi.label("CHIRRU ATM", 14, true);
        brand.setForeground(AtmUi.BLUE);
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(brand);
        card.add(Box.createVerticalStrut(10));
        JLabel title = AtmUi.label("Welcome back", 29, true);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(6));
        JLabel sub = AtmUi.muted("Sign in to access your secure ATM account.");
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(sub);
        card.add(Box.createVerticalStrut(30));
        card.add(AtmUi.formRow("CARD NUMBER", cardField));
        card.add(Box.createVerticalStrut(15));
        card.add(AtmUi.formRow("4-DIGIT PIN", pinField));
        card.add(Box.createVerticalStrut(24));

        JPanel actions = new JPanel(new GridLayout(1, 2, 10, 0));
        actions.setOpaque(false);
        signIn.setPreferredSize(new Dimension(0, 42));
        clear.setPreferredSize(new Dimension(0, 42));
        actions.add(signIn); actions.add(clear);
        card.add(actions);
        card.add(Box.createVerticalStrut(14));
        signUp.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUp.setPreferredSize(new Dimension(372, 40));
        signUp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        card.add(signUp);
        card.add(Box.createVerticalGlue());

        JPanel status = new JPanel(new BorderLayout());
        status.setOpaque(false);
        status.add(AtmUi.dbStatus(), BorderLayout.WEST);
        status.add(AtmUi.muted("PINs are never stored in plain text."), BorderLayout.EAST);
        card.add(status);
        root.add(card);
        add(root);

        signIn.addActionListener(e -> authenticate());
        clear.addActionListener(e -> { cardField.setText(""); pinField.setText(""); cardField.requestFocusInWindow(); });
        signUp.addActionListener(e -> { dispose(); new Signup(); });
        getRootPane().setDefaultButton(signIn);
        cardField.requestFocusInWindow();
    }

    private void authenticate() {
        try {
            String card = cardField.getText().trim();
            String pin = new String(pinField.getPassword());
            AuthService.AuthResult result = authService.authenticate(card, pin);
            pinField.setText("");
            if (result.success()) {
                dispose();
                new Transactions(result.account().id());
            } else {
                JOptionPane.showMessageDialog(this, result.message(), "Login", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            pinField.setText("");
            AtmUi.showError(this, "Database error", ex);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login::new);
    }
}
