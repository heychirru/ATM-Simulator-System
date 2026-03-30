package ASimulatorSystem;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Pin extends JFrame implements ActionListener {

    private final JButton back;
    private final String pin;

    Pin(String pin) {
        this.pin = pin;
        setTitle("PIN CHANGE");
        setLayout(null);

        JLabel title = new JLabel("PIN change screen placeholder");
        title.setBounds(135, 100, 360, 30);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title);

        back = new JButton("BACK");
        back.setBounds(240, 220, 110, 35);
        back.setBackground(Color.BLACK);
        back.setForeground(Color.WHITE);
        back.addActionListener(this);
        add(back);

        getContentPane().setBackground(Color.WHITE);
        setSize(600, 400);
        setLocation(600, 250);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == back) {
            setVisible(false);
            new Transactions(pin).setVisible(true);
        }
    }
}
