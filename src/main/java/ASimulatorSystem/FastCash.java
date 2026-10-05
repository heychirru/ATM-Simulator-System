package ASimulatorSystem;

import ASimulatorSystem.service.TransactionService;
import ASimulatorSystem.ui.AtmFrame;
import ASimulatorSystem.ui.AtmUi;
import java.awt.*;
import java.math.BigDecimal;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Quick withdrawal screen with predefined amounts. */
public class FastCash extends AtmFrame {
    private final TransactionService service = new TransactionService();
    private final int[] amounts = {100, 500, 1000, 2000, 5000, 10000};

    public FastCash(long accountId) {
        super("Fast Cash", accountId, 800, 590);
        build();
        setVisible(true);
    }

    private void build() {
        content.setLayout(new BorderLayout(0, 18));
        JPanel intro = new JPanel();
        intro.setOpaque(false);
        intro.setLayout(new BoxLayout(intro, BoxLayout.Y_AXIS));
        intro.add(AtmUi.label("Fast cash", 25, true));
        intro.add(Box.createVerticalStrut(5));
        intro.add(AtmUi.muted("Choose a preset amount for a quicker withdrawal."));
        content.add(intro, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 3, 16, 16));
        grid.setOpaque(false);
        for (int amount : amounts) addAmount(grid, amount);
        content.add(grid, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(AtmUi.muted("Maximum per transaction: ₹10,000"), BorderLayout.WEST);
        bottom.add(dashboardButton(), BorderLayout.EAST);
        content.add(bottom, BorderLayout.SOUTH);
    }

    private void addAmount(JPanel parent, int amount) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AtmUi.BORDER), new EmptyBorder(15, 18, 15, 18)));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JLabel value = AtmUi.label("₹ " + amount, 20, true);
        value.setHorizontalAlignment(SwingConstants.CENTER);
        button.add(value, BorderLayout.CENTER);
        button.addActionListener(e -> withdraw(amount));
        parent.add(button);
    }

    private void withdraw(int amount) {
        try {
            BigDecimal balance = service.withdraw(accountId, BigDecimal.valueOf(amount));
            JOptionPane.showMessageDialog(this,
                    "Please collect ₹ " + amount + ".\nRemaining balance: ₹ " + balance.setScale(2),
                    "Withdrawal successful", JOptionPane.INFORMATION_MESSAGE);
            AtmUi.backToDashboard(this, accountId);
        } catch (Exception ex) {
            AtmUi.showError(this, "Withdrawal failed", ex);
        }
    }
}
