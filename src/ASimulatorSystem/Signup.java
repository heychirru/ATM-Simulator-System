package ASimulatorSystem;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Signup extends JFrame implements ActionListener {

    private final JButton back;

    Signup() {
        setTitle("SIGNUP");
        setLayout(null);

        JLabel title = new JLabel("Signup screen placeholder");
        title.setBounds(180, 100, 320, 30);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title);

        back = new JButton("BACK TO LOGIN");
        back.setBounds(200, 200, 200, 35);
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
            new Login().setVisible(true);
        }
    }
}
