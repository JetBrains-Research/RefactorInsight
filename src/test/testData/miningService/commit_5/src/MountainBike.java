public class MountainBike extends Bicycle {

    public int seatHeight;

    public MountainBike(int gear, int speed, Price price, int seatHeight) {
        super(gear, speed, price);
        this.seatHeight = seatHeight;
    }

    public void setHeight(int newValue)
    {
        seatHeight = newValue;
    }

    public void speedUp(int increment) {
        speed += increment;
    }
}
