import jdk.internal.jline.internal.Nullable;

//modify annotation, extract method
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

    @Override
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
}
