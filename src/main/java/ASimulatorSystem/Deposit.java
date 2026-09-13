package ASimulatorSystem;

import ASimulatorSystem.service.TransactionService;
import ASimulatorSystem.ui.AtmFrame;
import ASimulatorSystem.ui.AtmUi;
import java.awt.*;
import java.math.BigDecimal;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Deposit screen using the shared authenticated ATM shell. */
public class Deposit extends AtmFrame {
    private final JTextField amount = AtmUi.field();
    private final TransactionService service = new TransactionService();

    public Deposit(long accountId) {
        super("Deposit", accountId, 760, 520);
        build();
        setVisible(true);
    }

    private void build() {
        content.setLayout(new GridBagLayout());
        JPanel card = AtmUi.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(520, 310));
        card.add(AtmUi.label("Deposit money", 25, true));
        card.add(Box.createVerticalStrut(6));
        card.add(AtmUi.muted("Add funds securely to your ATM account."));
        card.add(Box.createVerticalStrut(28));
        card.add(AtmUi.formRow("AMOUNT (₹)", amount));
        card.add(Box.createVerticalStrut(24));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        actions.add(dashboardButton());
        JButton submit = AtmUi.primary("DEPOSIT MONEY");
        submit.setPreferredSize(new Dimension(155, 40));
        submit.addActionListener(e -> deposit());
        actions.add(submit);
        card.add(actions);
        content.add(card);
        getRootPane().setDefaultButton(submit);
    }

    private void deposit() {
        try {
            BigDecimal value = new BigDecimal(amount.getText().trim());
            BigDecimal balance = service.deposit(accountId, value);
            JOptionPane.showMessageDialog(this,
                    "Deposit completed successfully.\nNew balance: ₹ " + balance.setScale(2),
                    "Deposit successful", JOptionPane.INFORMATION_MESSAGE);
            AtmUi.backToDashboard(this, accountId);
        } catch (Exception ex) {
            AtmUi.showError(this, "Deposit failed", ex);
        }
    }
}
