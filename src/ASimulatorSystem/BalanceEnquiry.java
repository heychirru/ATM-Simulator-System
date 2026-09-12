package ASimulatorSystem;

import ASimulatorSystem.service.TransactionService;
import java.awt.*;import java.awt.event.*;import javax.swing.*;

public class BalanceEnquiry extends JFrame implements ActionListener{
    private final long accountId; private final JLabel balance=new JLabel(); private final JButton back=new JButton("BACK");
    public BalanceEnquiry(long accountId){this.accountId=accountId;setTitle("BALANCE ENQUIRY");setSize(620,360);setLocationRelativeTo(null);setDefaultCloseOperation(EXIT_ON_CLOSE);setLayout(null);getContentPane().setBackground(Color.WHITE);JLabel t=new JLabel("BALANCE ENQUIRY");t.setBounds(205,45,260,35);t.setFont(new Font("Arial",Font.BOLD,24));add(t);balance.setBounds(120,130,400,35);balance.setFont(new Font("Arial",Font.BOLD,18));balance.setHorizontalAlignment(SwingConstants.CENTER);add(balance);back.setBounds(235,220,140,38);back.setBackground(Color.BLACK);back.setForeground(Color.WHITE);back.addActionListener(this);add(back);try{balance.setText("Current Balance: Rs. "+new TransactionService().balance(accountId));}catch(Exception e){balance.setText("Unable to load balance");}setVisible(true);}
    @Override public void actionPerformed(ActionEvent e){dispose();new Transactions(accountId);}
}
