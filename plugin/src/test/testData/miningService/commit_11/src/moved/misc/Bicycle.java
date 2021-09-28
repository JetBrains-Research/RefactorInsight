package misc;
public class Bicycle {

    public int gear2;
    public int speed;
    public PriceWithDiscount price;
    int time;
    public int seatHeight;

    public Bicycle(int gear, int speed, int time, PriceWithDiscount price, int seatHeight) {
        this.gear2 = gear;
        this.speed = speed;
        this.speed *= 2;
        this.time = time;
        this.price = price;
        this.seatHeight = seatHeight;
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
        return "No of gears are " + gear2 + "\n";
    }

    private int doubleSpeed() {
        this.speed *= 2;
        return this.speed;
    }

    public int getPrice() {
        return price.getPrice();
    }

    public PriceWithDiscount get() {
        return price;
    }

}
