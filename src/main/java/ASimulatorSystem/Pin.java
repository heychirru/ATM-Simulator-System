package ASimulatorSystem;

import ASimulatorSystem.service.AuthService;
import ASimulatorSystem.ui.AtmFrame;
import ASimulatorSystem.ui.AtmUi;
import java.awt.*;
import javax.swing.*;

/** Secure PIN change screen. */
public class Pin extends AtmFrame {
    private final JPasswordField current = AtmUi.passwordField();
    private final JPasswordField next = AtmUi.passwordField();
    private final JPasswordField confirm = AtmUi.passwordField();
    private final AuthService service = new AuthService();

    public Pin(long accountId) {
        super("PIN Change", accountId, 760, 570);
        build();
        setVisible(true);
    }

    private void build() {
        content.setLayout(new GridBagLayout());
        JPanel card = AtmUi.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(520, 380));
        card.add(AtmUi.label("Change your PIN", 25, true));
        card.add(Box.createVerticalStrut(6));
        card.add(AtmUi.muted("Use a new 4-digit PIN that is different from your current PIN."));
        card.add(Box.createVerticalStrut(24));
        card.add(AtmUi.formRow("CURRENT PIN", current));
        card.add(Box.createVerticalStrut(12));
        card.add(AtmUi.formRow("NEW PIN", next));
        card.add(Box.createVerticalStrut(12));
        card.add(AtmUi.formRow("CONFIRM NEW PIN", confirm));
        card.add(Box.createVerticalStrut(22));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        actions.add(dashboardButton());
        JButton change = AtmUi.primary("CHANGE PIN");
        change.setPreferredSize(new Dimension(135, 40));
        change.addActionListener(e -> changePin());
        actions.add(change);
        card.add(actions);
        content.add(card);
        getRootPane().setDefaultButton(change);
    }

    private void changePin() {
        String currentPin = new String(current.getPassword());
        String newPin = new String(next.getPassword());
        String confirmPin = new String(confirm.getPassword());
        if (!newPin.equals(confirmPin)) {
            JOptionPane.showMessageDialog(this, "New PINs do not match.", "PIN change", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            service.changePin(accountId, currentPin, newPin);
            JOptionPane.showMessageDialog(this, "Your PIN was changed successfully.", "PIN changed", JOptionPane.INFORMATION_MESSAGE);
            AtmUi.backToDashboard(this, accountId);
        } catch (Exception ex) {
            AtmUi.showError(this, "PIN change failed", ex);
        } finally {
            current.setText(""); next.setText(""); confirm.setText("");
        }
    }
}
