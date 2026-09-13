package ASimulatorSystem;

import ASimulatorSystem.service.TransactionService;
import ASimulatorSystem.ui.AtmFrame;
import ASimulatorSystem.ui.AtmUi;
import java.awt.*;
import java.math.BigDecimal;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Main authenticated ATM dashboard. */
public class Transactions extends AtmFrame {
    private final TransactionService service = new TransactionService();
    private final JLabel balance = AtmUi.label("₹ --", 28, true);

    public Transactions(long accountId) {
        super("Dashboard", accountId, 900, 650);
        build();
        loadBalance();
        setVisible(true);
    }

    private void build() {
        content.setLayout(new BorderLayout(0, 22));

        JPanel welcome = new JPanel(new BorderLayout());
        welcome.setOpaque(false);
        JPanel copy = new JPanel();
        copy.setOpaque(false);
        copy.setLayout(new BoxLayout(copy, BoxLayout.Y_AXIS));
        copy.add(AtmUi.label("Welcome back", 26, true));
        copy.add(Box.createVerticalStrut(5));
        copy.add(AtmUi.muted("Choose an operation to manage your account."));
        welcome.add(copy, BorderLayout.WEST);

        JPanel balanceCard = new JPanel(new BorderLayout(14, 0));
        balanceCard.setBackground(AtmUi.NAVY);
        balanceCard.setBorder(new EmptyBorder(16, 22, 16, 22));
        JPanel balanceCopy = new JPanel();
        balanceCopy.setOpaque(false);
        balanceCopy.setLayout(new BoxLayout(balanceCopy, BoxLayout.Y_AXIS));
        JLabel small = new JLabel("AVAILABLE BALANCE");
        small.setForeground(new Color(180, 194, 214));
        small.setFont(new Font("SansSerif", Font.BOLD, 10));
        balance.setForeground(Color.WHITE);
        balanceCopy.add(small); balanceCopy.add(Box.createVerticalStrut(3)); balanceCopy.add(balance);
        balanceCard.add(balanceCopy, BorderLayout.CENTER);
        welcome.add(balanceCard, BorderLayout.EAST);
        content.add(welcome, BorderLayout.NORTH);

        JPanel actions = new JPanel(new GridLayout(2, 3, 16, 16));
        actions.setOpaque(false);
        addAction(actions, "Deposit", "Add money to your account", () -> open(new Deposit(accountId)));
        addAction(actions, "Cash Withdrawal", "Withdraw a custom amount", () -> open(new Withdrawal(accountId)));
        addAction(actions, "Fast Cash", "Quick predefined withdrawals", () -> open(new FastCash(accountId)));
        addAction(actions, "Balance Enquiry", "View your current balance", () -> open(new BalanceEnquiry(accountId)));
        addAction(actions, "Mini Statement", "Review recent transactions", () -> new MiniStatement(accountId).setVisible(true));
        addAction(actions, "PIN Change", "Update your 4-digit PIN", () -> open(new Pin(accountId)));
        content.add(actions, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        JLabel security = AtmUi.muted("Never share your card number or PIN with anyone.");
        JButton logout = AtmUi.secondary("LOG OUT");
        logout.setPreferredSize(new Dimension(120, 38));
        logout.addActionListener(e -> AtmUi.logout(this));
        bottom.add(security, BorderLayout.WEST);
        bottom.add(logout, BorderLayout.EAST);
        content.add(bottom, BorderLayout.SOUTH);
    }

    private void addAction(JPanel parent, String title, String subtitle, Runnable action) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout(10, 3));
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AtmUi.BORDER), new EmptyBorder(15, 17, 15, 17)));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JLabel t = AtmUi.label(title, 15, true);
        JLabel s = AtmUi.muted(subtitle);
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(t); text.add(Box.createVerticalStrut(5)); text.add(s);
        JLabel arrow = AtmUi.label("›", 28, false);
        arrow.setForeground(AtmUi.BLUE);
        button.add(text, BorderLayout.CENTER);
        button.add(arrow, BorderLayout.EAST);
        button.addActionListener(e -> action.run());
        parent.add(button);
    }

    private void open(JFrame frame) {
        dispose();
        frame.setVisible(true);
    }

    private void loadBalance() {
        try {
            BigDecimal value = service.balance(accountId);
            balance.setText("₹ " + value.setScale(2));
        } catch (Exception ex) {
            balance.setText("₹ --");
        }
    }
}
