package ElectricityTrading.supplier;

import jade.gui.TimeChooser;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import java.util.Date;

/**
This is the GUI of the agent that tries to sell tender on behalf of its user
*/
public class ElectricitySupplierGuiImpl extends JFrame implements ElectricitySupplierGui {
private ElectricitySupplierAgent myAgent;

private JTextField titleTF, energyTF, minPriceTF, deadlineTF;
private JButton setDeadlineB;
private JButton setCCB, sellB, resetB, exitB;
private JTextArea logTA;

private Date deadline;

public void setAgent(ElectricitySupplierAgent a) {
myAgent = a;
setTitle(myAgent.getName());
}

public ElectricitySupplierGuiImpl() {
super();

addWindowListener(new WindowAdapter() {
public void windowClosing(WindowEvent e) {
myAgent.doDelete();
}
} );

JPanel rootPanel = new JPanel();
rootPanel.setLayout(new GridBagLayout());
rootPanel.setMinimumSize(new Dimension(380, 150));
rootPanel.setPreferredSize(new Dimension(380, 150));

JLabel l = new JLabel("Tender/Bid for:");
l.setHorizontalAlignment(SwingConstants.LEFT);
GridBagConstraints gridBagConstraints = new GridBagConstraints();
gridBagConstraints.gridx = 0;
gridBagConstraints.gridy = 1;
gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 3);
rootPanel.add(l, gridBagConstraints);

titleTF = new JTextField(64);
titleTF.setMinimumSize(new Dimension(222, 20));
titleTF.setPreferredSize(new Dimension(222, 20));
gridBagConstraints = new GridBagConstraints();
gridBagConstraints.gridx = 1;
gridBagConstraints.gridy = 1;
gridBagConstraints.gridwidth = 3;
gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
gridBagConstraints.insets = new Insets(5, 3, 0, 3);
rootPanel.add(titleTF, gridBagConstraints);


l = new JLabel("Cost($/kWh):");
l.setHorizontalAlignment(SwingConstants.LEFT);
l.setMinimumSize(new Dimension(100, 20));
l.setPreferredSize(new Dimension(100, 20));
gridBagConstraints = new GridBagConstraints();
gridBagConstraints.gridx = 0;
gridBagConstraints.gridy = 2;
gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 3);
rootPanel.add(l, gridBagConstraints);

minPriceTF = new JTextField(64);
minPriceTF.setMinimumSize(new Dimension(70, 20));
minPriceTF.setPreferredSize(new Dimension(70, 20));
gridBagConstraints = new GridBagConstraints();
gridBagConstraints.gridx = 1;
gridBagConstraints.gridy = 2;
gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
gridBagConstraints.insets = new Insets(5, 3, 0, 3);
rootPanel.add(minPriceTF, gridBagConstraints);



l = new JLabel("Deadline:");
l.setHorizontalAlignment(SwingConstants.LEFT);
gridBagConstraints = new GridBagConstraints();
gridBagConstraints.gridx = 0;
gridBagConstraints.gridy = 3;
gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 3);
rootPanel.add(l, gridBagConstraints);

deadlineTF = new JTextField(64);
deadlineTF.setMinimumSize(new Dimension(146, 20));
deadlineTF.setPreferredSize(new Dimension(146, 20));
deadlineTF.setEnabled(false);
gridBagConstraints = new GridBagConstraints();
gridBagConstraints.gridx = 1;
gridBagConstraints.gridy = 3;
gridBagConstraints.gridwidth = 2;
gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
gridBagConstraints.insets = new Insets(5, 3, 0, 3);
rootPanel.add(deadlineTF, gridBagConstraints);

setDeadlineB = new JButton("Apply");
setDeadlineB.setMinimumSize(new Dimension(70, 20));
setDeadlineB.setPreferredSize(new Dimension(70, 20));
setDeadlineB.addActionListener(new ActionListener(){
public void actionPerformed(ActionEvent e) {
Date d = deadline;
if (d == null) {
d = new Date();
}
TimeChooser tc = new TimeChooser(d);
if (tc.showEditTimeDlg(ElectricitySupplierGuiImpl.this) == TimeChooser.OK) {
deadline = tc.getDate();
deadlineTF.setText(deadline.toString());
}
}
} );
gridBagConstraints = new GridBagConstraints();
gridBagConstraints.gridx = 3;
gridBagConstraints.gridy = 3;
gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
gridBagConstraints.insets = new Insets(5, 3, 0, 3);
rootPanel.add(setDeadlineB, gridBagConstraints);

rootPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

getContentPane().add(rootPanel, BorderLayout.NORTH);

logTA = new JTextArea();
logTA.setEnabled(false);
JScrollPane jsp = new JScrollPane(logTA);
jsp.setMinimumSize(new Dimension(300, 180));
jsp.setPreferredSize(new Dimension(300, 180));
JPanel p = new JPanel();
p.setBorder(new BevelBorder(BevelBorder.LOWERED));
p.add(jsp);
getContentPane().add(p, BorderLayout.CENTER);

p = new JPanel();
sellB = new JButton("Sell");
sellB.setBackground(Color.GREEN);
sellB.setOpaque(true);
sellB.addActionListener(new ActionListener(){
public void actionPerformed(ActionEvent e) {
String title = titleTF.getText();

int minPrice = -1;
int energy=-1;
if (title != null && title.length() > 0) {
if (deadline != null && deadline.getTime() > System.currentTimeMillis()) {
try {
//desiredPrice = Integer.parseInt(desiredPriceTF.getText());
try {
	//energy=Integer.parseInt(energyTF.getText());
minPrice = Integer.parseInt(minPriceTF.getText());
//if (minPrice <= desiredPrice) {
////myAgent.addToCatalogue(title, desiredPrice, minPrice, deadline.getTime());
myAgent.putForSale(title, energy, minPrice, deadline);
notifyUser("Tender for: "+title+" with the price "+minPrice+" by "+deadline);

}
catch (Exception ex1) {

JOptionPane.showMessageDialog(ElectricitySupplierGuiImpl.this, "Invalid min price", "WARNING", JOptionPane.WARNING_MESSAGE);
}
}
catch (Exception ex2) {

JOptionPane.showMessageDialog(ElectricitySupplierGuiImpl.this, "Invalid best price", "WARNING", JOptionPane.WARNING_MESSAGE);
}
}
else {

JOptionPane.showMessageDialog(ElectricitySupplierGuiImpl.this, "Invalid deadline", "WARNING", JOptionPane.WARNING_MESSAGE);
}
}
else {
// No company title specified
JOptionPane.showMessageDialog(ElectricitySupplierGuiImpl.this, "No Company title specified", "WARNING", JOptionPane.WARNING_MESSAGE);
}
}
} );
resetB = new JButton("Reset");
resetB.setBackground(Color.YELLOW);
resetB.setOpaque(true);
resetB.addActionListener(new ActionListener(){
public void actionPerformed(ActionEvent e) {
titleTF.setText("");

minPriceTF.setText("");
deadlineTF.setText("");
deadline = null;
}
} );
exitB = new JButton("Exit");
exitB.setBackground(Color.RED);
exitB.setOpaque(true);
exitB.addActionListener(new ActionListener(){
public void actionPerformed(ActionEvent e) {
myAgent.doDelete();
}
} );

sellB.setPreferredSize(resetB.getPreferredSize());
exitB.setPreferredSize(resetB.getPreferredSize());

p.add(sellB);
p.add(resetB);
p.add(exitB);

p.setBorder(new BevelBorder(BevelBorder.LOWERED));
rootPanel.setBackground(Color.CYAN);
l = new JLabel("Begin Quoting");
l.setFont(new Font("Calibri", Font.PLAIN, 24));
l.setForeground(Color.WHITE);
l.setHorizontalAlignment(SwingConstants.CENTER);
gridBagConstraints = new GridBagConstraints();
gridBagConstraints.gridx = 1;
gridBagConstraints.gridy = 0;
gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
rootPanel.add(l,gridBagConstraints);

getContentPane().add(p, BorderLayout.SOUTH);

pack();

setResizable(false);
}

public void notifyUser(String message) {
logTA.append(message+"\n");
}
}