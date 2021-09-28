public class Bicycle {

    public int gear;
    public int speed;

    public Bicycle(int gear, int speed) {
        this.gear = gear;
        this.speed = speed;
    }

    public void applyBrake(int decrement) {
        speed -= decrement;
    }

    public void speedUp(int increment) {
        speed += increment;
    }

    public String toString() {
        return ("No of gears are " + gear
                + "\n"
                + "speed of bicycle is " + speed);
    }
}
