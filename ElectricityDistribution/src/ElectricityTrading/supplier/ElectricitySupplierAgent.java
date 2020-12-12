package ElectricityTrading.supplier;


import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;
import jade.proto.ContractNetResponder;
import jade.content.*;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.*;
import jade.content.onto.basic.*;
import ElectricityTrading.ontology.*;

import java.util.*;

public class ElectricitySupplierAgent extends Agent {
// The list of available sellers
private Map catalogue = new HashMap();
public static String seller_name;


// The GUI to interact with the user
private ElectricitySupplierGui myGui;

/** The following parts, where the SLCodec and ElectricityTradingOntology are
* registered
**/
private Codec codec = new SLCodec();
private Ontology ontology = ElectricityTradingOntology.getInstance();

/**
* Agent initializations
**/
protected void setup() {
// Printout a welcome message
System.out.println("Seller-agent "+getAID().getName()+" is ready.");

getContentManager().registerLanguage(codec);
getContentManager().registerOntology(ontology);

// Create and show the GUI
myGui = new ElectricitySupplierGuiImpl();
myGui.setAgent(this);
myGui.show();

// Add the behaviour serving calls for price from buyer agent
addBehaviour(new CallForOfferServer());



/** 
 * This piece of code, to register services with the DF, is explained
**/
// Register the company names in the yellow pages
DFAgentDescription dfd = new DFAgentDescription();
dfd.setName(getAID());
ServiceDescription sd = new ServiceDescription();
sd.setType("Electricity-tender");
sd.setName(getLocalName()+"-Electricity-tender");
dfd.addServices(sd);
try {
DFService.register(this, dfd);
}
catch (FIPAException fe) {
fe.printStackTrace();
}
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
System.out.println("Seller-agent "+getAID().getName()+"terminating.");

/** 
 * This piece of code, to deregister with the DF, is explained
**/
// Deregister from the yellow pages
try {
DFService.deregister(this);
}
catch (FIPAException fe) {
fe.printStackTrace();
}
}

/**
* This method is called by the GUI when the user inserts a new
* Tender
* @param title The title of the company
* @param initialPrice The initial price
* @param minPrice The minimum price
* @param deadline The deadline by which to sell the book
**/
public void putForSale(String title, int energy, int minPrice, Date deadline) {
addBehaviour(new PriceManager(this, title, energy, minPrice, deadline));
}

private class PriceManager extends TickerBehaviour {
private String title;
private int minPrice, currentPrice, initPrice, deltaP, energy;
private long initTime, deadline, deltaT;

private PriceManager(Agent a, String t, int e, int mp, Date d) {
super(a, 3000); // tick every minute
title = t;

currentPrice = initPrice;
deltaP = mp;
//energy=e;
deadline = d.getTime();
initTime = System.currentTimeMillis();
deltaT = ((deadline- initTime) > 0 ? (deadline- initTime) : 60000);
}

public void onStart() {
// Insert the tenders available
catalogue.put(title, this);
super.onStart();
}

public void onTick() {
long currentTime = System.currentTimeMillis();
if (currentTime > deadline) {
// Deadline expired
myGui.notifyUser("Cannot sell electricity "+title);
catalogue.remove(title);
stop();
}
else {
// Compute the current price
long elapsedTime = currentTime- initTime;
// System.out.println("initPrice"+initPrice+"deltaP"+deltaP+"elapsedTime"+elapsedTime+"deltaT"+deltaT+"currentPrice"+currentPrice+"");
//currentPrice = (int)Math.round(initPrice-1.0 * deltaP * (1.0 * elapsedTime / deltaT));
currentPrice=deltaP;
}
}

public int getCurrentPrice() {
return currentPrice;
}

public int getCurrentEnergy() {
	return energy;
}
}

private class CallForOfferServer extends ContractNetResponder {

int price, energy;

CallForOfferServer() {
super(ElectricitySupplierAgent.this, MessageTemplate.and(MessageTemplate.MatchOntology(ontology.getName()),MessageTemplate.MatchPerformative(ACLMessage.CFP)));
}

protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException, FailureException, NotUnderstoodException {
// CFP Message received. Process it
ACLMessage reply = cfp.createReply();
//System.out.println(cfp);
/* if (cfp.getPerformative() != ACLMessage.CFP) {
reply.setPerformative(ACLMessage.FAILURE);
System.out.println(myAgent.getLocalName()+"REINIT"+cfp);
reinit();
}
else*/ {
try {
ContentManager cm = myAgent.getContentManager();
Action act = (Action) cm.extractContent(cfp);
Sell sellAction = (Sell) act.getAction();
Electricity book = sellAction.getItem();
myGui.notifyUser("Received Proposal to buy "+book.getTitle());
PriceManager pm = (PriceManager)catalogue.get(book.getTitle());

if (pm != null) {
// The requested book is available for sale
reply.setPerformative(ACLMessage.PROPOSE);
ContentElementList cel = new ContentElementList();
cel.add(act);
Costs costs = new Costs();
costs.setItem(book);
//energy=pm.getCurrentEnergy();
//costs.setEnergy(energy);
price = pm.getCurrentPrice();
costs.setPrice(price);
cel.add(costs);
cm.fillContent(reply, cel);
}
else {
// The requested book is NOT available for sale.
reply.setPerformative(ACLMessage.REFUSE);
}
}
catch (OntologyException oe) {
oe.printStackTrace();
reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
}
catch (CodecException ce) {
ce.printStackTrace();
reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
}
catch (Exception e) {
e.printStackTrace();
reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
}
}
//System.out.println(myAgent.getLocalName()+"RX"+cfp+"\nTX"+reply+"\n\n");
myGui.notifyUser(reply.getPerformative() == ACLMessage.PROPOSE ? "Sent Proposed tender at "+ price : "Refused Proposed tender");
return reply;
}

protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
ACLMessage inform = accept.createReply();
inform.setPerformative(ACLMessage.INFORM);
inform.setContent(Integer.toString(price));
myGui.notifyUser("Sent Inform at price "+price+" for Tender: "+getAID().getLocalName());
seller_name=getAID().getLocalName();
return inform;
}

}

}