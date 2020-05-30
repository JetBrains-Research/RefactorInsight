package main.java.vehicles.birds;

public abstract class Bird {

    private String type;
    boolean isNailed;

    public Bird(String type, boolean isNailed) {
        this.type = type;
        this.isNailed = isNailed;
    }

    public abstract double getSpeed();

    double getBaseSpeed() {
        return 15.2;
   }

    double getLoadFactor() {
        return 1.3;
   }
}
