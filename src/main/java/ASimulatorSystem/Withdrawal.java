package ASimulatorSystem;

import ASimulatorSystem.service.TransactionService;
import java.awt.*;import java.awt.event.*;import java.math.BigDecimal;import javax.swing.*;

public class Withdrawal extends JFrame implements ActionListener{
    private final long accountId; private final JTextField amount=new JTextField(); private final JButton withdraw=new JButton("WITHDRAW"),back=new JButton("BACK"); private final TransactionService service=new TransactionService();
    public Withdrawal(long accountId){this.accountId=accountId;setTitle("CASH WITHDRAWAL");setSize(620,400);setLocationRelativeTo(null);setDefaultCloseOperation(EXIT_ON_CLOSE);setLayout(null);getContentPane().setBackground(Color.WHITE);JLabel t=new JLabel("CASH WITHDRAWAL");t.setBounds(205,40,260,35);t.setFont(new Font("Arial",Font.BOLD,24));add(t);JLabel l=new JLabel("Amount (max Rs. 10,000)");l.setBounds(85,125,200,30);l.setFont(new Font("Arial",Font.BOLD,15));add(l);amount.setBounds(300,120,220,35);add(amount);button(withdraw,160,220,140);button(back,320,220,120);setVisible(true);}
    private void button(JButton b,int x,int y,int w){b.setBounds(x,y,w,35);b.setBackground(Color.BLACK);b.setForeground(Color.WHITE);b.addActionListener(this);add(b);}
    @Override public void actionPerformed(ActionEvent e){if(e.getSource()==back){dispose();new Transactions(accountId);return;}try{BigDecimal a=new BigDecimal(amount.getText().trim());BigDecimal b=service.withdraw(accountId,a);JOptionPane.showMessageDialog(this,"Rs. "+a+" withdrawn. Balance: Rs. "+b);dispose();new Transactions(accountId);}catch(Exception ex){JOptionPane.showMessageDialog(this,ex.getMessage(),"Withdrawal failed",JOptionPane.WARNING_MESSAGE);}}
}
