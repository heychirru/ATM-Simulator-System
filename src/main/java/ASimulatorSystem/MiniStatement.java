package ASimulatorSystem;

import ASimulatorSystem.dao.TransactionDao;
import ASimulatorSystem.service.TransactionService;
import ASimulatorSystem.ui.AtmFrame;
import ASimulatorSystem.ui.AtmUi;
import java.awt.*;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Recent transaction history screen. */
public class MiniStatement extends AtmFrame {
    private final JTextArea history = new JTextArea();

    public MiniStatement(long accountId) {
        super("Mini Statement", accountId, 850, 620);
        build();
        load();
        setVisible(true);
    }

    private void build() {
        content.setLayout(new BorderLayout(0, 16));
        JPanel intro = new JPanel(new BorderLayout());
        intro.setOpaque(false);
        JPanel copy = new JPanel();
        copy.setOpaque(false);
        copy.setLayout(new BoxLayout(copy, BoxLayout.Y_AXIS));
        copy.add(AtmUi.label("Mini statement", 25, true));
        copy.add(Box.createVerticalStrut(5));
        copy.add(AtmUi.muted("Your 10 most recent account transactions."));
        intro.add(copy, BorderLayout.WEST);
        content.add(intro, BorderLayout.NORTH);

        history.setEditable(false);
        history.setFont(new Font("Monospaced", Font.PLAIN, 13));
        history.setForeground(AtmUi.TEXT);
        history.setBackground(Color.WHITE);
        history.setBorder(new EmptyBorder(12, 14, 12, 14));
        JScrollPane scroll = new JScrollPane(history);
        scroll.setBorder(BorderFactory.createLineBorder(AtmUi.BORDER));
        content.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(AtmUi.muted("Transaction records are read directly from MySQL."), BorderLayout.WEST);
        bottom.add(dashboardButton(), BorderLayout.EAST);
        content.add(bottom, BorderLayout.SOUTH);
    }

    private void load() {
        try {
            StringBuilder text = new StringBuilder();
            text.append(String.format("%-20s %-13s %12s %15s%n", "DATE", "TYPE", "AMOUNT", "BALANCE"));
            text.append("────────────────────────────────────────────────────────────\n");
            SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy  HH:mm");
            for (TransactionDao.TransactionRecord record : new TransactionService().recent(accountId)) {
                text.append(String.format("%-20s %-13s ₹%10s ₹%13s%n",
                        format.format(record.createdAt()), record.type(), record.amount(), record.balanceAfter()));
            }
            history.setText(text.length() > 65 ? text.toString() : "No transactions yet.");
            history.setCaretPosition(0);
        } catch (Exception ex) {
            history.setText("Unable to load transaction history.\n\n" + ex.getMessage());
        }
    }
}
