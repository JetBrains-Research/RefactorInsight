package main.java.vehicles.birds;

public class NorwegianBlue extends Bird {


    public NorwegianBlue(String type, boolean isNailed) {
        super(type, isNailed);
    }

    @Override
    public double getSpeed() {
        return (isNailed) ? 0 : getBaseSpeed();
    }
}
