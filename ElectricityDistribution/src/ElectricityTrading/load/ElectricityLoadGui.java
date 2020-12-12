package ElectricityTrading.load;

public interface ElectricityLoadGui {

void setAgent(ElectricityLoadAgent a);
void show();
void hide();
void notifyUser(String message);
void dispose();
}
