package ASimulatorSystem;

import ASimulatorSystem.service.AuthService;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import javax.swing.*;

/** Creates an ATM account with a hashed PIN. */
public class Signup extends JFrame implements ActionListener {
    private final JTextField card = new JTextField();
    private final JPasswordField pin = new JPasswordField();
    private final JPasswordField confirm = new JPasswordField();
    private final JTextField deposit = new JTextField("0");
    private final JButton create = new JButton("CREATE ACCOUNT");
    private final JButton back = new JButton("BACK");
    private final AuthService authService = new AuthService();

    public Signup() {
        setTitle("ATM - Create Account"); setSize(650, 470); setLocationRelativeTo(null); setDefaultCloseOperation(EXIT_ON_CLOSE); setLayout(null); getContentPane().setBackground(Color.WHITE);
        add(label("CREATE ATM ACCOUNT", 175, 30, 350, 35, 27));
        field("Card Number (12-19 digits)", card, 85, 105); field("PIN (4 digits)", pin, 85, 160); field("Confirm PIN", confirm, 85, 215); field("Initial Deposit", deposit, 85, 270);
        button(create, 150, 345, 180, 35); button(back, 345, 345, 120, 35); setVisible(true);
    }
    private JLabel label(String s,int x,int y,int w,int h,int size){JLabel l=new JLabel(s);l.setBounds(x,y,w,h);l.setFont(new Font("Arial",Font.BOLD,size));return l;}
    private void field(String name,JComponent c,int x,int y){add(label(name,x,y-28,260,25,14));c.setBounds(300,y,250,30);add(c);}
    private void button(JButton b,int x,int y,int w,int h){b.setBounds(x,y,w,h);b.setBackground(Color.BLACK);b.setForeground(Color.WHITE);b.addActionListener(this);add(b);}
    @Override public void actionPerformed(ActionEvent e){
        if(e.getSource()==back){dispose();new Login();return;}
        try{
            String p=new String(pin.getPassword()), cp=new String(confirm.getPassword());
            if(!p.equals(cp)) throw new IllegalArgumentException("PINs do not match.");
            BigDecimal amount=new BigDecimal(deposit.getText().trim());
            long id=authService.createAccount(card.getText().trim(),p,amount);
            JOptionPane.showMessageDialog(this,"Account created successfully. Account ID: "+id,"Success",JOptionPane.INFORMATION_MESSAGE);
            dispose();new Login();
        }catch(NumberFormatException ex){JOptionPane.showMessageDialog(this,"Initial deposit must be a valid amount.","Validation",JOptionPane.WARNING_MESSAGE);}
        catch(Exception ex){JOptionPane.showMessageDialog(this,ex.getMessage(),"Signup failed",JOptionPane.WARNING_MESSAGE);}
    }
}
