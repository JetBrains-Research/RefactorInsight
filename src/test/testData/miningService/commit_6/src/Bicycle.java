
//extract and move, rename method
public class Bicycle {

    public int gear;
    public int speed;
    public Price price;

    public Bicycle(int gear, int speed, Price price) {
        this.gear = gear;
        this.speed = speed;
        this.speed = doubleSpeed();
        this.price = price;
    }

    public void applyBrakeAndDecrement(int decrement) {
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

    private int doubleSpeed() {
        this.speed *= 2;
        return this.speed;
    }

    public int getPrice() {
        return price.getPrice();
    }

    public Price get() {
        return price;
    }

    public void applyDiscount() {
        price.price -= price.discount;
    }
}
