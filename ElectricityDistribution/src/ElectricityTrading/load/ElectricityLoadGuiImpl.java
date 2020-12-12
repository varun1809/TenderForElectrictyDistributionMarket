package ElectricityTrading.load;

import jade.gui.TimeChooser;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.JTextComponent;

import javax.swing.JFrame;

import java.awt.Color;

import java.util.Date;

/**
J2SE (Swing-based) implementation of the GUI of the agent that
tries to buy electricity on behalf of its user
*/



public class ElectricityLoadGuiImpl extends JFrame implements ElectricityLoadGui {
private ElectricityLoadAgent myAgent;

private JTextField titleTF, energyTF, minCostTF, deadlineTF;
private JButton setDeadlineB;
private JButton setCCB, buyB, resetB, exitB;
private JTextArea logTA;

private Date deadline;

public ElectricityLoadGuiImpl() {
super();

addWindowListener(new WindowAdapter() {
public void windowClosing(WindowEvent e) {
myAgent.doDelete();
}
} );

JPanel rootPanel = new JPanel();
rootPanel.setLayout(new GridBagLayout());
rootPanel.setMinimumSize(new Dimension(330, 125));
rootPanel.setPreferredSize(new Dimension(330, 125));

///////////
// Line 0
///////////
JLabel l = new JLabel("Tender for:");
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

///////////
// Line 1
///////////
// l = new JLabel("Energy Required:");
//l.setHorizontalAlignment(SwingConstants.LEFT);
// gridBagConstraints = new GridBagConstraints();
//gridBagConstraints.gridx = 0;
//gridBagConstraints.gridy = 1;
//gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
//gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 3);
//rootPanel.add(l, gridBagConstraints);
//
//energyTF = new JTextField(64);
//energyTF.setMinimumSize(new Dimension(70, 20));
//energyTF.setPreferredSize(new Dimension(70, 20));
//energyTF.setEditable(true); 
//gridBagConstraints = new GridBagConstraints();
//gridBagConstraints.gridx = 1;
//gridBagConstraints.gridy = 1;
//gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
//gridBagConstraints.insets = new Insets(5, 3, 0, 3);
//rootPanel.add(energyTF, gridBagConstraints);

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

minCostTF = new JTextField(64);
minCostTF.setMinimumSize(new Dimension(70, 20));
minCostTF.setPreferredSize(new Dimension(70, 20));
gridBagConstraints = new GridBagConstraints();
gridBagConstraints.gridx = 1;
gridBagConstraints.gridy = 2;
gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
gridBagConstraints.insets = new Insets(5, 3, 0, 3);
rootPanel.add(minCostTF, gridBagConstraints);

///////////
// Line 2
///////////
l = new JLabel("Time Slot:");
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

setDeadlineB = new JButton("Set");
setDeadlineB.setMinimumSize(new Dimension(70, 20));
setDeadlineB.setPreferredSize(new Dimension(70, 20));
setDeadlineB.addActionListener(new ActionListener(){
public void actionPerformed(ActionEvent e) {
Date d = deadline;
if (d == null) {
d = new Date();
}
TimeChooser tc = new TimeChooser(d);
if (tc.showEditTimeDlg(ElectricityLoadGuiImpl.this) == TimeChooser.OK) {
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


//setCCB = new JButton("Set CreditCard");
//setCCB.addActionListener(new ActionListener(){
//public void actionPerformed(ActionEvent e) {
//String cc = JOptionPane.showInputDialog(BookBuyerGuiImpl.this, "Insert the Credit Card number");
//if (cc != null && cc.length() > 0) {
//myAgent.setCreditCard(cc);
//}
//else {
//JOptionPane.showMessageDialog(BookBuyerGuiImpl.this, "Invalid Credit Card number", "WARNING", JOptionPane.WARNING_MESSAGE);
//}
//}
//} );
//setCCB.setMinimumSize(new Dimension(70, 20));
//setCCB.setPreferredSize(new Dimension(70, 20));
//gridBagConstraints = new GridBagConstraints();
//gridBagConstraints.gridx = 0;
//gridBagConstraints.gridy = 3;
//gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
//gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
//gridBagConstraints.insets = new Insets(5, 3, 0, 3);
//rootPanel.add(setCCB, gridBagConstraints);

rootPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
rootPanel.setBackground(Color.LIGHT_GRAY);
l = new JLabel("Welcome to Bidding Process");
l.setFont(new Font("Calibri", Font.PLAIN, 24));
l.setForeground(Color.RED);
l.setHorizontalAlignment(SwingConstants.CENTER);
gridBagConstraints = new GridBagConstraints();
gridBagConstraints.gridx = 1;
gridBagConstraints.gridy = 0;
gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
rootPanel.add(l,gridBagConstraints);

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
buyB = new JButton("Buy");
buyB.setBackground(Color.GREEN);
buyB.setOpaque(true);
buyB.addActionListener(new ActionListener(){
public void actionPerformed(ActionEvent e) {
String title = titleTF.getText();
int desiredCost = -1;
int minCost = -1;
int energy=-1;
if (title != null && title.length() > 0) {
if (deadline != null && deadline.getTime() > System.currentTimeMillis()) {
try {
//desiredCost = Integer.parseInt(desiredCostTF.getText());
try {
minCost = Integer.parseInt(minCostTF.getText());

//energy= Integer.parseInt(energyTF.getText());
// if (minCost >= desiredCost) {
// myAgent.purchase(title, desiredCost, minCost, deadline.getTime());
//myAgent.purchase(title, minCost, deadline, energy);
myAgent.purchase(title, minCost, deadline);
notifyUser("Tender requirement details: "+title+" at max "+minCost+" by "+deadline);
//}
//else {
// Max cost < desiredCost
//JOptionPane.showMessageDialog(BookBuyerGuiImpl.this, "Max cost must be greater than best cost", "WARNING", JOptionPane.WARNING_MESSAGE);
//}
}
catch (Exception ex1) {
// Invalid max cost
JOptionPane.showMessageDialog(ElectricityLoadGuiImpl.this, "Invalid min cost", "WARNING", JOptionPane.WARNING_MESSAGE);
}
}
catch (Exception ex2) {
// Invalid desired cost
JOptionPane.showMessageDialog(ElectricityLoadGuiImpl.this, "Invalid best cost", "WARNING", JOptionPane.WARNING_MESSAGE);
}
}
else {
// No deadline specified
JOptionPane.showMessageDialog(ElectricityLoadGuiImpl.this, "Invalid deadline", "WARNING", JOptionPane.WARNING_MESSAGE);
}
}
else {
}
}
} );
resetB = new JButton("Reset");
resetB.setBackground(Color.YELLOW);
resetB.setOpaque(true);
resetB.addActionListener(new ActionListener(){
public void actionPerformed(ActionEvent e) {
titleTF.setText("");
//energyTF.setText("");
minCostTF.setText("");
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

buyB.setPreferredSize(resetB.getPreferredSize());
exitB.setPreferredSize(resetB.getPreferredSize());

p.add(buyB);
p.add(resetB);
p.add(exitB);

p.setBorder(new BevelBorder(BevelBorder.LOWERED));
getContentPane().add(p, BorderLayout.SOUTH);

pack();

setResizable(true);
}

public void setAgent(ElectricityLoadAgent a) {
myAgent = a;
setTitle(myAgent.getName());
}

public void notifyUser(String message) {
logTA.append(message+"\n");
}


}