package main.java.vehicles.components;

import main.java.vehicles.components.carComponents.EngineImpl;

public class Calculator {
    public static int numberOfCoconutss = 10;
    private static Integer temp;

    public static Integer doCalculations() {
        temp = EngineImpl.getInteger();
        return temp;
    }
}
