package main.java.vehicles.birds;

import main.java.vehicles.components.Calculator;

public class African extends Bird{

    public African(String type, boolean isNailed) {
        super(type, isNailed);
    }

    @Override
    public double getSpeed() {
        return getBaseSpeed() - getLoadFactor() * Calculator.numberOfCoconutss;
    }
}
