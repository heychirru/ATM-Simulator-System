package ASimulatorSystem;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** Main authenticated ATM menu. No PIN is retained after authentication. */
public class Transactions extends JFrame implements ActionListener {
    private final long accountId;
    private final JButton deposit=new JButton("DEPOSIT"), withdraw=new JButton("CASH WITHDRAWAL"), fastCash=new JButton("FAST CASH"), statement=new JButton("MINI STATEMENT"), pin=new JButton("PIN CHANGE"), balance=new JButton("BALANCE ENQUIRY"), exit=new JButton("LOG OUT");

    public Transactions(long accountId){
        this.accountId=accountId; setTitle("ATM - Transactions");setSize(650,560);setLocationRelativeTo(null);setDefaultCloseOperation(EXIT_ON_CLOSE);setLayout(null);getContentPane().setBackground(Color.WHITE);
        JLabel title=new JLabel("PLEASE SELECT YOUR TRANSACTION");title.setBounds(145,55,400,35);title.setFont(new Font("Arial",Font.BOLD,20));add(title);
        JButton[] buttons={deposit,withdraw,fastCash,statement,pin,balance,exit}; int y=125;
        for(int i=0;i<buttons.length;i++){JButton b=buttons[i];b.setBounds(i==6?225:(i%2==0?105:345),y,200,38);b.setBackground(Color.BLACK);b.setForeground(Color.WHITE);b.addActionListener(this);add(b);if(i<6&&i%2==1)y+=55;if(i==6)y+=55;}
        setVisible(true);
    }
    @Override public void actionPerformed(ActionEvent e){
        if(e.getSource()==deposit){dispose();new Deposit(accountId);}
        else if(e.getSource()==withdraw){dispose();new Withdrawal(accountId);}
        else if(e.getSource()==fastCash){dispose();new FastCash(accountId);}
        else if(e.getSource()==statement){new MiniStatement(accountId);}
        else if(e.getSource()==pin){dispose();new Pin(accountId);}
        else if(e.getSource()==balance){dispose();new BalanceEnquiry(accountId);}
        else {dispose();new Login();}
    }
}
