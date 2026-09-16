package ASimulatorSystem;

import ASimulatorSystem.dao.TransactionDao;
import ASimulatorSystem.service.TransactionService;
import ASimulatorSystem.ui.AtmFrame;
import ASimulatorSystem.ui.AtmUi;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;

/** Full recent transaction history for the authenticated account. */
public class TransactionHistory extends AtmFrame {
    private final TransactionService service = new TransactionService();
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"DATE", "REFERENCE", "TYPE", "AMOUNT", "BALANCE AFTER"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private final JLabel status = AtmUi.muted("Loading transaction history...");

    public TransactionHistory(long accountId) {
        super("Transaction History", accountId, 980, 680);
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
        copy.setLayout(new javax.swing.BoxLayout(copy, javax.swing.BoxLayout.Y_AXIS));
        copy.add(AtmUi.label("Transaction history", 25, true));
        copy.add(javax.swing.Box.createVerticalStrut(5));
        copy.add(AtmUi.muted("View your latest 50 deposits and withdrawals."));
        intro.add(copy, BorderLayout.WEST);
        content.add(intro, BorderLayout.NORTH);

        table.setRowHeight(42);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setForeground(AtmUi.TEXT);
        table.setBackground(Color.WHITE);
        table.setGridColor(AtmUi.BORDER);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 11));
        table.getTableHeader().setForeground(AtmUi.MUTED);
        table.getTableHeader().setBackground(new Color(248, 249, 251));
        table.getTableHeader().setReorderingAllowed(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(145);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(130);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);

        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(3).setCellRenderer(right);
        table.getColumnModel().getColumn(4).setCellRenderer(right);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(AtmUi.BORDER));
        content.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(status, BorderLayout.WEST);
        JButton dashboard = dashboardButton();
        bottom.add(dashboard, BorderLayout.EAST);
        content.add(bottom, BorderLayout.SOUTH);
    }

    private void load() {
        try {
            List<TransactionDao.TransactionRecord> records = service.history(accountId);
            model.setRowCount(0);
            SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy  HH:mm");

            for (TransactionDao.TransactionRecord record : records) {
                model.addRow(new Object[]{
                        format.format(record.createdAt()),
                        record.reference(),
                        record.type(),
                        money(record.amount()),
                        money(record.balanceAfter())
                });
            }

            status.setText(records.isEmpty()
                    ? "No transactions yet."
                    : records.size() + " transaction" + (records.size() == 1 ? "" : "s") + " shown");
        } catch (Exception ex) {
            model.setRowCount(0);
            status.setText("Unable to load transaction history: " + safeMessage(ex));
        }
    }

    private String money(BigDecimal value) {
        return "₹ " + value.setScale(2).toPlainString();
    }

    private String safeMessage(Exception ex) {
        String message = ex.getMessage();
        return message == null || message.isBlank() ? "Database error" : message;
    }
}
