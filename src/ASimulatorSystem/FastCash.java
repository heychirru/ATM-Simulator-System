package ASimulatorSystem;

import ASimulatorSystem.service.TransactionService;
import java.awt.*;import java.awt.event.*;import java.math.BigDecimal;import javax.swing.*;

public class FastCash extends JFrame implements ActionListener{
    private final long accountId; private final TransactionService service=new TransactionService(); private final JButton back=new JButton("BACK");
    private final int[] amounts={100,500,1000,2000,5000,10000};
    public FastCash(long accountId){this.accountId=accountId;setTitle("FAST CASH");setSize(620,480);setLocationRelativeTo(null);setDefaultCloseOperation(EXIT_ON_CLOSE);setLayout(null);getContentPane().setBackground(Color.WHITE);JLabel t=new JLabel("SELECT WITHDRAWAL AMOUNT");t.setBounds(170,45,300,35);t.setFont(new Font("Arial",Font.BOLD,22));add(t);for(int i=0;i<amounts.length;i++){JButton b=new JButton("Rs. "+amounts[i]);b.setBounds(i%2==0?100:330,110+(i/2)*55,190,38);b.setBackground(Color.BLACK);b.setForeground(Color.WHITE);b.putClientProperty("amount",amounts[i]);b.addActionListener(this);add(b);}back.setBounds(215,285,190,38);back.setBackground(Color.BLACK);back.setForeground(Color.WHITE);back.addActionListener(this);add(back);setVisible(true);}
    @Override public void actionPerformed(ActionEvent e){if(e.getSource()==back){dispose();new Transactions(accountId);return;}JButton b=(JButton)e.getSource();int a=(int)b.getClientProperty("amount");try{BigDecimal balance=service.withdraw(accountId,BigDecimal.valueOf(a));JOptionPane.showMessageDialog(this,"Rs. "+a+" withdrawn. Balance: Rs. "+balance);dispose();new Transactions(accountId);}catch(Exception ex){JOptionPane.showMessageDialog(this,ex.getMessage(),"Withdrawal failed",JOptionPane.WARNING_MESSAGE);}}
}
