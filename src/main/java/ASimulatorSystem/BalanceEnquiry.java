package ASimulatorSystem;

import ASimulatorSystem.service.TransactionService;
import ASimulatorSystem.ui.AtmFrame;
import ASimulatorSystem.ui.AtmUi;
import java.awt.*;
import java.math.BigDecimal;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Current balance screen. */
public class BalanceEnquiry extends AtmFrame {
    private final JLabel balance = AtmUi.label("₹ --", 38, true);
    private final TransactionService service = new TransactionService();

    public BalanceEnquiry(long accountId) {
        super("Balance Enquiry", accountId, 760, 500);
        build();
        loadBalance();
        setVisible(true);
    }

    private void build() {
        content.setLayout(new GridBagLayout());
        JPanel card = AtmUi.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(500, 285));
        JLabel caption = AtmUi.muted("CURRENT AVAILABLE BALANCE");
        caption.setAlignmentX(Component.CENTER_ALIGNMENT);
        balance.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(caption);
        card.add(Box.createVerticalStrut(8));
        card.add(balance);
        card.add(Box.createVerticalStrut(8));
        JLabel note = AtmUi.muted("Balance is retrieved securely from the database.");
        note.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(note);
        card.add(Box.createVerticalGlue());
        JButton back = dashboardButton();
        back.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(back);
        content.add(card);
    }

    private void loadBalance() {
        try {
            BigDecimal value = service.balance(accountId);
            balance.setText("₹ " + value.setScale(2));
        } catch (Exception ex) {
            balance.setText("Unavailable");
            AtmUi.showError(this, "Balance unavailable", ex);
        }
    }
}
