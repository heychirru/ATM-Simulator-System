package ASimulatorSystem.ui;

import ASimulatorSystem.Login;
import ASimulatorSystem.Transactions;
import ASimulatorSystem.config.DatabaseConfig;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Shared visual language and navigation helpers for the desktop ATM. */
public final class AtmUi {
    public static final Color NAVY = new Color(18, 28, 45);
    public static final Color BLUE = new Color(38, 99, 235);
    public static final Color BG = new Color(245, 247, 250);
    public static final Color TEXT = new Color(28, 35, 48);
    public static final Color MUTED = new Color(105, 116, 133);
    public static final Color BORDER = new Color(220, 225, 232);

    private AtmUi() { }

    public static void frame(JFrame frame, String title, int width, int height) {
        frame.setTitle("Chirru ATM  •  " + title);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.getContentPane().setBackground(BG);
    }

    public static JPanel header(String title, String subtitle) {
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setBackground(NAVY);
        header.setBorder(new EmptyBorder(18, 28, 18, 28));
        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        JLabel name = new JLabel("CHIRRU ATM");
        name.setForeground(Color.WHITE);
        name.setFont(new Font("SansSerif", Font.BOLD, 20));
        JLabel sub = new JLabel(subtitle == null ? "Secure desktop banking" : subtitle);
        sub.setForeground(new Color(190, 200, 215));
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        brand.add(name); brand.add(Box.createVerticalStrut(3)); brand.add(sub);
        header.add(brand, BorderLayout.WEST);
        JLabel page = new JLabel(title.toUpperCase());
        page.setForeground(Color.WHITE);
        page.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.add(page, BorderLayout.EAST);
        return header;
    }

    public static JLabel dbStatus() {
        boolean connected = DatabaseConfig.isDatabaseAvailable();
        JLabel status = new JLabel(connected ? "● DATABASE CONNECTED" : "● DATABASE OFFLINE");
        status.setFont(new Font("SansSerif", Font.BOLD, 11));
        status.setForeground(connected ? new Color(88, 210, 145) : new Color(255, 125, 125));
        return status;
    }

    public static JLabel label(String text, int size, boolean bold) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, size));
        return label;
    }

    public static JLabel muted(String text) {
        JLabel label = label(text, 13, false);
        label.setForeground(MUTED);
        return label;
    }

    public static JButton primary(String text) {
        JButton button = new JButton(text);
        button.setBackground(BLUE);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JButton secondary(String text) {
        JButton button = new JButton(text);
        button.setBackground(Color.WHITE);
        button.setForeground(TEXT);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(BORDER));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JTextField field() {
        JTextField field = new JTextField();
        styleField(field);
        return field;
    }

    public static JPasswordField passwordField() {
        JPasswordField field = new JPasswordField();
        styleField(field);
        return field;
    }

    private static void styleField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setForeground(TEXT);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(8, 10, 8, 10)));
    }

    public static String maskCard(String card) {
        if (card == null || card.length() < 4) return "••••";
        return "•••• •••• •••• " + card.substring(card.length() - 4);
    }

    public static void backToDashboard(JFrame current, long accountId) {
        current.dispose();
        new Transactions(accountId);
    }

    public static void logout(JFrame current) {
        current.dispose();
        new Login();
    }

    public static void wire(JButton button, ActionListener listener) {
        button.addActionListener(listener);
    }

    public static void showError(Component parent, String title, Exception ex) {
        String message = ex.getMessage() == null || ex.getMessage().isBlank()
                ? "The operation could not be completed." : ex.getMessage();
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public static JPanel formRow(String name, JComponent input) {
        JPanel row = new JPanel(new BorderLayout(0, 7));
        row.setOpaque(false);
        row.add(label(name, 12, true), BorderLayout.NORTH);
        row.add(input, BorderLayout.CENTER);
        return row;
    }

    public static JPanel card() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(26, 30, 26, 30));
        return panel;
    }
}
