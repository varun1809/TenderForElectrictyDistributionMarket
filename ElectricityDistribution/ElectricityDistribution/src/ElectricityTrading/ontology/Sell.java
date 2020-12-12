package ElectricityTrading.ontology;

import jade.content.AgentAction;
import jade.core.AID;

public class Sell implements AgentAction {
private Electricity item;

public Electricity getItem() {
return item;
}

public void setItem(Electricity item) {
this.item = item;
}

}