package ASimulatorSystem;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MiniStatement extends JFrame implements ActionListener {

    private final JButton close;
    private final String pin;

    MiniStatement(String pin) {
        this.pin = pin;
        setTitle("MINI STATEMENT");
        setLayout(null);

        JLabel title = new JLabel("Mini statement placeholder");
        title.setBounds(155, 100, 340, 30);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title);

        JLabel pinLabel = new JLabel("PIN: " + pin);
        pinLabel.setBounds(220, 150, 220, 25);
        add(pinLabel);

        close = new JButton("CLOSE");
        close.setBounds(240, 230, 110, 35);
        close.setBackground(Color.BLACK);
        close.setForeground(Color.WHITE);
        close.addActionListener(this);
        add(close);

        getContentPane().setBackground(Color.WHITE);
        setSize(600, 400);
        setLocation(600, 250);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == close) {
            setVisible(false);
            new Transactions(pin).setVisible(true);
        }
    }
}
