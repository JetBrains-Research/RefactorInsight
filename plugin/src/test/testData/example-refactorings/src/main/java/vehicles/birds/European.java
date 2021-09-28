package main.java.vehicles.birds;

public class European extends Bird {

    public European(String type, boolean isNailed) {
        super(type, isNailed);
    }

    @Override
    public double getSpeed() {
        return getBaseSpeed();
    }
}
