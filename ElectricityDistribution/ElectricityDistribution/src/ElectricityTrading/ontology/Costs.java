package ElectricityTrading.ontology;

import jade.content.Predicate;
import jade.core.AID;

public class Costs implements Predicate {
private Electricity item;
private int price;
private int energy;

public Electricity getItem() {
return item;
}

public void setItem(Electricity item) {
this.item = item;
}

public int getPrice() {
return price;
}

public void setPrice(int price) {
this.price = price;
}


public int getEnergy()
{
	return energy;
}



public void setEnergy(int energy) {
	this.energy=energy;
	
}


}