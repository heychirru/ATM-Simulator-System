package ASimulatorSystem.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** Base window used by every authenticated ATM operation. */
public abstract class AtmFrame extends JFrame {
    protected final long accountId;
    protected final JPanel content = new JPanel();

    protected AtmFrame(String page, long accountId, int width, int height) {
        this.accountId = accountId;
        AtmUi.frame(this, page, width, height);
        setLayout(new BorderLayout());
        add(buildHeader(page), BorderLayout.NORTH);
        content.setBackground(AtmUi.BG);
        content.setBorder(new EmptyBorder(28, 42, 20, 42));
        add(content, BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader(String page) {
        JPanel header = AtmUi.header(page, "Secure desktop banking");
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        JLabel account = new JLabel(AtmUi.maskCard(getCardNumber()));
        account.setForeground(new Color(220, 228, 240));
        account.setFont(new Font("SansSerif", Font.BOLD, 12));
        JLabel status = AtmUi.dbStatus();
        right.add(account);
        right.add(Box.createVerticalStrut(5));
        right.add(status);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Color.WHITE);
        footer.setBorder(new EmptyBorder(9, 24, 9, 24));
        JLabel left = AtmUi.muted("Chirru ATM  •  Your session is secured");
        JLabel right = AtmUi.muted("Connection pool: " + getPoolStatus());
        footer.add(left, BorderLayout.WEST);
        footer.add(right, BorderLayout.EAST);
        return footer;
    }

    private String getCardNumber() {
        try {
            return new ASimulatorSystem.dao.AccountDao().findById(accountId).cardNumber();
        } catch (Exception ex) {
            return "••••";
        }
    }

    private String getPoolStatus() {
        try {
            return ASimulatorSystem.config.DatabaseConfig.poolStatus();
        } catch (Exception ex) {
            return "unavailable";
        }
    }

    protected JPanel contentGrid(int rows, int cols) {
        JPanel panel = new JPanel(new GridLayout(rows, cols, 18, 18));
        panel.setOpaque(false);
        return panel;
    }

    protected JButton dashboardButton() {
        JButton button = AtmUi.secondary("←  DASHBOARD");
        button.addActionListener(e -> AtmUi.backToDashboard(this, accountId));
        return button;
    }
}
