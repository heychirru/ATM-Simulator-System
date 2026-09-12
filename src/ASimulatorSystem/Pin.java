package ASimulatorSystem;

import ASimulatorSystem.service.AuthService;
import java.awt.*;import java.awt.event.*;import javax.swing.*;

public class Pin extends JFrame implements ActionListener{
    private final long accountId;private final JPasswordField current=new JPasswordField(),next=new JPasswordField(),confirm=new JPasswordField();private final JButton change=new JButton("CHANGE PIN"),back=new JButton("BACK");private final AuthService service=new AuthService();
    public Pin(long accountId){this.accountId=accountId;setTitle("PIN CHANGE");setSize(620,440);setLocationRelativeTo(null);setDefaultCloseOperation(EXIT_ON_CLOSE);setLayout(null);getContentPane().setBackground(Color.WHITE);JLabel t=new JLabel("CHANGE PIN");t.setBounds(235,35,200,35);t.setFont(new Font("Arial",Font.BOLD,24));add(t);field("Current PIN",current,90,100);field("New PIN",next,90,155);field("Confirm New PIN",confirm,90,210);button(change,155,290,150);button(back,315,290,120);setVisible(true);}
    private void field(String n,JComponent c,int x,int y){JLabel l=new JLabel(n);l.setBounds(x,y-25,180,22);add(l);c.setBounds(290,y,220,30);add(c);}private void button(JButton b,int x,int y,int w){b.setBounds(x,y,w,35);b.setBackground(Color.BLACK);b.setForeground(Color.WHITE);b.addActionListener(this);add(b);}
    @Override public void actionPerformed(ActionEvent e){if(e.getSource()==back){dispose();new Transactions(accountId);return;}String n=new String(next.getPassword()),c=new String(confirm.getPassword());if(!n.equals(c)){JOptionPane.showMessageDialog(this,"New PINs do not match.");return;}try{service.changePin(accountId,new String(current.getPassword()),n);JOptionPane.showMessageDialog(this,"PIN changed successfully.");dispose();new Transactions(accountId);}catch(Exception ex){JOptionPane.showMessageDialog(this,ex.getMessage(),"PIN change failed",JOptionPane.WARNING_MESSAGE);}}
}
