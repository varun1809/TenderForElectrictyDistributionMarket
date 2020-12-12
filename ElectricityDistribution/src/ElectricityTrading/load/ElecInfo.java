package ElectricityTrading.load;

import java.util.Date;

class ElecInfo {
String title;
int maxPrice,energy;
Date deadline;

public String getTitle() { return title; }
public void setTitle(String title) {this.title = title;}

public int getMaxPrice() { return maxPrice; }
public void setMaxPrice(int maxPrice) {this.maxPrice = maxPrice;}

public Date getDeadline() { return deadline;}
public void setDeadline(Date deadline) {this.deadline = deadline;}


public int getEnergy() { return energy;}
public void setEnergy(Date deadline) {this.energy = energy;}

}