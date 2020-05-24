import jdk.internal.jline.internal.Nullable;

//pull up, push down, add annotation
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

    @Nullable
    public String toString() {
        String gears = "No of gears are " + gear + "\n";
        String speed = "speed of bicycle is " + speed;
        return gears + speed;
    }

    public void doubleSpeed() {
        this.speed *= 2;
    }
}
