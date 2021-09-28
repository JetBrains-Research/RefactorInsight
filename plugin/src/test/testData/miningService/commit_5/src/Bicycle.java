//remove annotation
public class Bicycle {

    public int gear;
    public int speed;
    public Price price;

    public Bicycle(int gear, int speed, Price price) {
        this.gear = gear;
        this.speed = speed;
        this.price = price;
    }

    public void applyBrake(int decrement) {
        speed -= decrement;
    }

    public String toString() {
        String gears = gearToString();
        String speed = "speed of bicycle is " + speed;
        return gears + speed;
    }

    private String gearToString() {
        return "No of gears are " + gear + "\n";
    }

    public void doubleSpeed() {
        this.speed *= 2;
    }

    public int getPrice() {
        return price.price;
    }
}
