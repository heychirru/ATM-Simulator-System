package ASimulatorSystem;

import ASimulatorSystem.service.TransactionService;
import ASimulatorSystem.ui.AtmFrame;
import ASimulatorSystem.ui.AtmUi;
import java.awt.*;
import java.math.BigDecimal;
import javax.swing.*;

/** Custom cash withdrawal screen. */
public class Withdrawal extends AtmFrame {
    private final JTextField amount = AtmUi.field();
    private final TransactionService service = new TransactionService();

    public Withdrawal(long accountId) {
        super("Cash Withdrawal", accountId, 760, 520);
        build();
        setVisible(true);
    }

    private void build() {
        content.setLayout(new GridBagLayout());
        JPanel card = AtmUi.cardLayoutPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(520, 320));
        card.add(AtmUi.label("Cash withdrawal", 25, true));
        card.add(Box.createVerticalStrut(6));
        card.add(AtmUi.muted("Enter an amount up to ₹10,000 per transaction."));
        card.add(Box.createVerticalStrut(28));
        card.add(AtmUi.formRow("AMOUNT (₹)", amount));
        card.add(Box.createVerticalStrut(12));
        card.add(AtmUi.muted("Rolling 24-hour withdrawal limit: ₹20,000"));
        card.add(Box.createVerticalStrut(24));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        actions.add(dashboardButton());
        JButton submit = AtmUi.primary("WITHDRAW CASH");
        submit.setPreferredSize(new Dimension(155, 40));
        submit.addActionListener(e -> withdraw());
        actions.add(submit);
        card.add(actions);
        content.add(card);
        getRootPane().setDefaultButton(submit);
    }

    private void withdraw() {
        try {
            BigDecimal value = new BigDecimal(amount.getText().trim());
            BigDecimal balance = service.withdraw(accountId, value);
            JOptionPane.showMessageDialog(this,
                    "Please collect your cash.\nRemaining balance: ₹ " + balance.setScale(2),
                    "Withdrawal successful", JOptionPane.INFORMATION_MESSAGE);
            AtmUi.backToDashboard(this, accountId);
        } catch (Exception ex) {
            AtmUi.showError(this, "Withdrawal failed", ex);
        }
    }
}
