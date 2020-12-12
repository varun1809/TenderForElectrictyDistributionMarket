package ElectricityTrading.load;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;

import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;

import jade.proto.ContractNetInitiator;

import java.util.Vector;
import java.util.Date;

import jade.content.*;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.*;
import jade.content.onto.basic.*;
import ElectricityTrading.ontology.*;
import ElectricityTrading.supplier.ElectricitySupplierAgent;

public class ElectricityLoadAgent extends Agent {
// The list of known seller agents
private Vector sellerAgents = new Vector();
boolean f=true;
// The GUI to interact with the user
private ElectricityLoadGui myGui;

/** 
 * The following parts, where the SLCodec and BookTradingOntology are registered
**/
private Codec codec = new SLCodec();
private Ontology ontology = ElectricityTradingOntology.getInstance();

/**
* Agent initializations
**/
protected void setup() {

/** 
* the following code processes notifications from the external buying system (other modifications
* also need to be introduced to handle the successful purchase or deadline expiration).
**/
// Enable O2A Communication
setEnabledO2ACommunication(true, 0);
// Add the behaviour serving notifications from the external system
addBehaviour(new CyclicBehaviour(this) {
public void action() {
ElecInfo info = (ElecInfo) myAgent.getO2AObject();
if (info != null) {
purchase(info.getTitle(), info.getMaxPrice(), info.getDeadline());
//purchase(info.getTitle(), info.getMaxPrice(), info.getDeadline(),info.getEnergy());
}
else {
block();
}
}
} );

// Printout a welcome message
System.out.println("Electricity Buyer-agent "+getAID().getName()+" is ready.");

getContentManager().registerLanguage(codec);
getContentManager().registerOntology(ontology);

// Get names of seller agents as arguments
Object[] args = getArguments();
if (args != null && args.length > 0) {
for (int i = 0; i < args.length; ++i) {
AID seller = new AID((String) args[i], AID.ISLOCALNAME);
sellerAgents.addElement(seller);
}
}

// Show the GUI to interact with the user

if(f)
{
myGui = new ElectricityLoadGuiImpl();
myGui.setAgent(this);

myGui.show();
f=false;
}

/** 
 * This piece of code, to search services with the DF
**/
// Update the list of seller agents every minute
addBehaviour(new TickerBehaviour(this, 60000) {
protected void onTick() {
// Update the list of seller agents
DFAgentDescription template = new DFAgentDescription();
ServiceDescription sd = new ServiceDescription();
sd.setType("Electricity-tender");
template.addServices(sd);
try {
DFAgentDescription[] result = DFService.search(myAgent, template);
sellerAgents.clear();
for (int i = 0; i < result.length; ++i) {
sellerAgents.addElement(result[i].getName());
}
}
catch (FIPAException fe) {
fe.printStackTrace();
}
}
} );
}

/**
* Agent clean-up
**/
protected void takeDown() {
// Dispose the GUI if it is there
if (myGui != null) {
myGui.dispose();
}

// Printout a dismissal message
System.out.println("Electyricity Buyer-agent "+getAID().getName()+"terminated.");
}

///**
//* This method is called by the GUI when the user inserts a new
//* book to buy
//* @param title The title of the book to buy
//* @param maxPrice The maximum acceptable price to buy the book
//* @param deadline The deadline by which to buy the book
//**/
public void purchase(String title, int maxPrice, Date deadline) {

addBehaviour(new PurchaseManager(this, title, maxPrice, deadline));
}


/**
* Implementation of matchmaker, named as PurchaseManager
**/
private class PurchaseManager extends TickerBehaviour {
private String title;
private int minPrice,energy;
private long deadline, initTime, deltaT;

private PurchaseManager(Agent a, String t, int mp, Date d) {
super(a, 6000); // tick every minute
title = t;
minPrice = mp;
deadline = d.getTime();
initTime = System.currentTimeMillis();
//energy=e;
deltaT = deadline- initTime;
}

public void onTick() {
long currentTime = System.currentTimeMillis();
if (currentTime > deadline) {
// Deadline expired
myGui.notifyUser("The deadline for tender submission is over "+title);
stop();
}
else {
// Compute the currently acceptable price and start a negotiation
long elapsedTime = currentTime - initTime;
//int acceptablePrice = (int)Math.round(1.0 * maxPrice * (1.0 * elapsedTime / deltaT));
int acceptablePrice=minPrice;
myAgent.addBehaviour(new PriceNegotiator(title, acceptablePrice,energy, this));
}
}
}

public ACLMessage cfp = new ACLMessage(ACLMessage.CFP); // variable needed to the ContractNetInitiator constructor

/**
* Inner class PriceNegotiator.
* This is the behaviour reimplemented by using the ContractNetInitiator
**/
public class PriceNegotiator extends ContractNetInitiator {
private String title;
private int maxPrice, enrgy;
private PurchaseManager manager;

public PriceNegotiator(String t, int p,int e, PurchaseManager m) {
super(ElectricityLoadAgent.this, cfp);
title = t;
maxPrice = p;
manager = m;
enrgy=e;
Electricity book = new Electricity();
book.setTitle(title);
Sell sellAction = new Sell();
sellAction.setItem(book);
Action act = new Action(ElectricityLoadAgent.this.getAID(), sellAction);
try {
cfp.setLanguage(codec.getName());
cfp.setOntology(ontology.getName());
ElectricityLoadAgent.this.getContentManager().fillContent(cfp, act);
} catch (Exception a) {
a.printStackTrace();
}
}

protected Vector prepareCfps(ACLMessage cfp) {
cfp.clearAllReceiver();
for (int i = 0; i < sellerAgents.size(); ++i) {
cfp.addReceiver((AID) sellerAgents.get(i));
}
Vector v = new Vector();
v.add(cfp);
if (sellerAgents.size() > 0)
myGui.notifyUser("Sent Call for Proposal to "+sellerAgents.size()+" sellers.");
return v;
}

protected void handleAllResponses(Vector responses, Vector acceptances) {
ACLMessage bestOffer = null;
int bestPrice = -1;
int energy=-1;
for (int i = 0; i < responses.size(); i++) {
ACLMessage rsp = (ACLMessage) responses.get(i);
if (rsp.getPerformative() == ACLMessage.PROPOSE) {
try {
ContentElementList cel = (ContentElementList)myAgent.getContentManager().extractContent(rsp);
int price = ((Costs)cel.get(1)).getPrice();
int en=((Costs)cel.get(1)).getEnergy();
myGui.notifyUser("Received Proposal at "+price+" when maximum acceptable price was "+maxPrice);
energy=en;
if (bestOffer == null || price < bestPrice) {
bestOffer = rsp;
bestPrice = price;
}
} catch (Exception e) {
e.printStackTrace();
}
}
}

for (int i = 0; i < responses.size(); i++) {
ACLMessage rsp = (ACLMessage) responses.get(i);
ACLMessage accept = rsp.createReply();
ElecInfo elec=new ElecInfo();
Costs en=new Costs();
if(elec.getEnergy()<=en.getEnergy())
{
if (rsp == bestOffer) {
boolean acceptedProposal = (bestPrice <= maxPrice);


accept.setPerformative(acceptedProposal ? ACLMessage.ACCEPT_PROPOSAL : ACLMessage.REJECT_PROPOSAL);
accept.setContent(title);
myGui.notifyUser(acceptedProposal ? "sent Accept Proposal" : "sent Reject Proposal");
manager.stop();
} else {
accept.setPerformative(ACLMessage.REJECT_PROPOSAL);
}
}
else
{
	accept.setPerformative(ACLMessage.REJECT_PROPOSAL);
}
//System.out.println(myAgent.getLocalName()+" handleAllResponses.acceptances.add "+accept);
acceptances.add(accept);
}
}
//getAID().getName()

protected void handleInform(ACLMessage inform) {
// Electricity successfully purchased
int price = Integer.parseInt(inform.getContent());
myGui.notifyUser("Electricity for "+title+" successfully purchased by "+getAID().getLocalName()+". Price =" + price+" from "+ElectricitySupplierAgent.seller_name);
manager.stop();
}

} // End of inner class PriceNegotiator
}