package ASimulatorSystem;

import ASimulatorSystem.service.AuthService;
import ASimulatorSystem.ui.AtmUi;
import java.awt.*;
import java.math.BigDecimal;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Account creation screen for the desktop ATM. */
public class Signup extends JFrame {
    private final JTextField card = AtmUi.field();
    private final JPasswordField pin = AtmUi.passwordField();
    private final JPasswordField confirm = AtmUi.passwordField();
    private final JTextField deposit = AtmUi.field();
    private final JButton create = AtmUi.primary("CREATE ACCOUNT");
    private final JButton back = AtmUi.secondary("BACK TO LOGIN");
    private final AuthService authService = new AuthService();

    public Signup() {
        AtmUi.frame(this, "Create Account", 900, 650);
        build();
        setVisible(true);
    }

    private void build() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(AtmUi.BG);
        root.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(new EmptyBorder(30, 45, 30, 45));
        cardPanel.setPreferredSize(new Dimension(570, 550));
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));

        JLabel brand = AtmUi.label("CHIRRU ATM", 14, true);
        brand.setForeground(AtmUi.BLUE);
        cardPanel.add(brand);
        cardPanel.add(Box.createVerticalStrut(7));
        cardPanel.add(AtmUi.label("Create your account", 27, true));
        cardPanel.add(Box.createVerticalStrut(5));
        cardPanel.add(AtmUi.muted("Set up your card and secure 4-digit PIN."));
        cardPanel.add(Box.createVerticalStrut(22));
        cardPanel.add(AtmUi.formRow("CARD NUMBER  •  12–19 DIGITS", card));
        cardPanel.add(Box.createVerticalStrut(12));
        cardPanel.add(AtmUi.formRow("4-DIGIT PIN", pin));
        cardPanel.add(Box.createVerticalStrut(12));
        cardPanel.add(AtmUi.formRow("CONFIRM PIN", confirm));
        cardPanel.add(Box.createVerticalStrut(12));
        deposit.setText("0");
        cardPanel.add(AtmUi.formRow("INITIAL DEPOSIT  •  OPTIONAL", deposit));
        cardPanel.add(Box.createVerticalStrut(18));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        actions.add(back);
        create.setPreferredSize(new Dimension(155, 40));
        actions.add(create);
        cardPanel.add(actions);
        cardPanel.add(Box.createVerticalStrut(14));
        cardPanel.add(AtmUi.muted("Your PIN is securely hashed before it is stored."));
        root.add(cardPanel);
        add(root);

        back.addActionListener(e -> { dispose(); new Login(); });
        create.addActionListener(e -> createAccount());
        getRootPane().setDefaultButton(create);
    }

    private void createAccount() {
        try {
            String p = new String(pin.getPassword());
            String cp = new String(confirm.getPassword());
            if (!p.equals(cp)) throw new IllegalArgumentException("PINs do not match.");
            BigDecimal amount = new BigDecimal(deposit.getText().trim());
            long id = authService.createAccount(card.getText().trim(), p, amount);
            pin.setText(""); confirm.setText("");
            JOptionPane.showMessageDialog(this,
                    "Account created successfully.\nAccount ID: " + id,
                    "Account created", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new Login();
        } catch (NumberFormatException ex) {
            AtmUi.showError(this, "Validation", new IllegalArgumentException("Initial deposit must be a valid amount."));
        } catch (Exception ex) {
            AtmUi.showError(this, "Signup failed", ex);
        } finally {
            pin.setText(""); confirm.setText("");
        }
    }
}
