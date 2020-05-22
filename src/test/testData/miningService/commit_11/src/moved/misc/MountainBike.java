package misc;
public class MountainBike extends Bicycle {

    public MountainBike(int gear, int speed, PriceWithDiscount price, int seatHeight) {
        super(gear, speed, price, seatHeight);
    }

    public void setHeight(int newValue)
    {
        seatHeight = newValue;
    }

    public void speedUp(int increment) {
        speed += increment;
    }
}
