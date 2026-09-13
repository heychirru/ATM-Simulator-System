package ASimulatorSystem;

import ASimulatorSystem.service.TransactionService;
import java.awt.*;import java.awt.event.*;import java.math.BigDecimal;import javax.swing.*;

public class Deposit extends JFrame implements ActionListener{
    private final long accountId; private final JTextField amount=new JTextField(); private final JButton deposit=new JButton("DEPOSIT"),back=new JButton("BACK"); private final TransactionService service=new TransactionService();
    public Deposit(long accountId){this.accountId=accountId;setTitle("DEPOSIT");setSize(600,380);setLocationRelativeTo(null);setDefaultCloseOperation(EXIT_ON_CLOSE);setLayout(null);getContentPane().setBackground(Color.WHITE);title("DEPOSIT MONEY");label("Amount (Rs.)",100,125);amount.setBounds(250,120,230,35);add(amount);button(deposit,150,220,130);button(back,300,220,130);setVisible(true);}
    private void title(String s){JLabel l=new JLabel(s);l.setBounds(200,45,250,35);l.setFont(new Font("Arial",Font.BOLD,24));add(l);}private void label(String s,int x,int y){JLabel l=new JLabel(s);l.setBounds(x,y,140,30);l.setFont(new Font("Arial",Font.BOLD,16));add(l);}private void button(JButton b,int x,int y,int w){b.setBounds(x,y,w,35);b.setBackground(Color.BLACK);b.setForeground(Color.WHITE);b.addActionListener(this);add(b);}
    @Override public void actionPerformed(ActionEvent e){if(e.getSource()==back){dispose();new Transactions(accountId);return;}try{BigDecimal a=new BigDecimal(amount.getText().trim());BigDecimal b=service.deposit(accountId,a);JOptionPane.showMessageDialog(this,"Deposited successfully. Balance: Rs. "+b);dispose();new Transactions(accountId);}catch(Exception ex){JOptionPane.showMessageDialog(this,ex.getMessage(),"Deposit failed",JOptionPane.WARNING_MESSAGE);}}
}
