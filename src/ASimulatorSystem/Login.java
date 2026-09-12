package ASimulatorSystem;

import ASimulatorSystem.service.AuthService;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** Desktop login screen. */
public class Login extends JFrame implements ActionListener {
    private final JTextField cardField = new JTextField();
    private final JPasswordField pinField = new JPasswordField();
    private final JButton signIn = new JButton("SIGN IN");
    private final JButton clear = new JButton("CLEAR");
    private final JButton signUp = new JButton("SIGN UP");
    private final AuthService authService = new AuthService();

    public Login() {
        setTitle("ATM - Secure Login"); setSize(620, 420); setLocationRelativeTo(null); setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null); getContentPane().setBackground(Color.WHITE);
        add(label("WELCOME TO ATM", 190, 35, 350, 35, 30));
        add(label("Card No:", 80, 125, 180, 30, 20));
        cardField.setBounds(260, 125, 250, 32); add(cardField);
        add(label("PIN:", 80, 185, 180, 30, 20));
        pinField.setBounds(260, 185, 250, 32); add(pinField);
        button(signIn, 150, 260, 120, 35); button(clear, 285, 260, 120, 35); button(signUp, 150, 315, 255, 35);
        setVisible(true);
    }

    private JLabel label(String text, int x, int y, int w, int h, int size) { JLabel l = new JLabel(text); l.setBounds(x,y,w,h); l.setFont(new Font("Arial", Font.BOLD, size)); return l; }
    private void button(JButton b, int x, int y, int w, int h) { b.setBounds(x,y,w,h); b.setBackground(Color.BLACK); b.setForeground(Color.WHITE); b.addActionListener(this); add(b); }

    @Override public void actionPerformed(ActionEvent e) {
        if (e.getSource() == clear) { cardField.setText(""); pinField.setText(""); return; }
        if (e.getSource() == signUp) { dispose(); new Signup(); return; }
        if (e.getSource() == signIn) {
            try {
                String card = cardField.getText().trim(); String pin = new String(pinField.getPassword());
                AuthService.AuthResult result = authService.authenticate(card, pin);
                if (result.success()) { dispose(); new Transactions(result.account().id()); }
                else JOptionPane.showMessageDialog(this, result.message(), "Login", JOptionPane.WARNING_MESSAGE);
                pinField.setText("");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE); }
        }
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(Login::new); }
}
