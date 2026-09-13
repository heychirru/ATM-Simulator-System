package ASimulatorSystem;

import ASimulatorSystem.dao.TransactionDao;
import ASimulatorSystem.service.TransactionService;
import java.awt.*;import java.awt.event.*;import javax.swing.*;

public class MiniStatement extends JFrame implements ActionListener{
    private final long accountId; private final JTextArea history=new JTextArea(); private final JButton close=new JButton("CLOSE");
    public MiniStatement(long accountId){this.accountId=accountId;setTitle("MINI STATEMENT");setSize(650,500);setLocationRelativeTo(null);setDefaultCloseOperation(DISPOSE_ON_CLOSE);setLayout(null);JLabel t=new JLabel("RECENT TRANSACTIONS");t.setBounds(190,25,300,35);t.setFont(new Font("Arial",Font.BOLD,22));add(t);history.setEditable(false);history.setFont(new Font("Monospaced",Font.PLAIN,13));JScrollPane pane=new JScrollPane(history);pane.setBounds(45,80,550,300);add(pane);close.setBounds(250,405,120,35);close.setBackground(Color.BLACK);close.setForeground(Color.WHITE);close.addActionListener(this);add(close);load();setVisible(true);}
    private void load(){try{StringBuilder s=new StringBuilder();for(TransactionDao.TransactionRecord r:new TransactionService().recent(accountId))s.append(r.createdAt()).append("  ").append(r.type()).append("  Rs. ").append(r.amount()).append("  Bal: Rs. ").append(r.balanceAfter()).append('\n');history.setText(s.length()==0?"No transactions yet.":s.toString());}catch(Exception e){history.setText("Unable to load transaction history.");}}
    @Override public void actionPerformed(ActionEvent e){dispose();}
}
