package ElectricityTrading.supplier;

public interface ElectricitySupplierGui {

void setAgent(ElectricitySupplierAgent a);
void show();
void hide();
void notifyUser(String message);
void dispose();
}