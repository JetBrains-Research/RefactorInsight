package misc;
public class MountainBike extends Bicycle {

    int time;

    public MountainBike(int gear, int speed, int time, PriceWithDiscount price, int seatHeight) {
        super(gear, speed, price, seatHeight);
        this.time = time;
    }

    public void setHeight(int newValue)
    {
        Integer a = 0;
        seatHeight = newValue + a;
    }

    public void speedUp(int increment) {
        speed += increment;
    }
}
