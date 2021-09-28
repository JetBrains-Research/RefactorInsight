package main.java.vehicles.cars;

public class Speed {
    int maxPossibleSpeed;
    double currentSpeed;

    public Speed() {
    }

    public double getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(double currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public int getMaxPossibleSpeed() {
        return maxPossibleSpeed;
    }

    public void setMaxPossibleSpeed(int maxPossibleSpeed) {
        this.maxPossibleSpeed = maxPossibleSpeed;
    }
}