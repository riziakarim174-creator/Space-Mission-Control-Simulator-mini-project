public abstract class Spacecraft {
    protected String name;
    protected double fuel;
    protected double altitude;
    protected double speed;

    public Spacecraft(String name, double fuel) {
        this.name = name;
        this.fuel = fuel;
        this.altitude = 0;
        this.speed = 0;
    }

    public void updateTelemetry() {
        this.altitude += 10;
        this.speed += 5;
        this.fuel -= 2;
        if (this.fuel < 0) this.fuel = 0;
    }

    public double getFuel() {
        return fuel;
    }

    public double getAltitude() {
        return altitude;
    }

    public double getSpeed() {
        return speed;
    }

    public String getName() {
        return name;
    }


    public void restoreState(double fuel, double altitude, double speed) {
        this.fuel = fuel;
        this.altitude = altitude;
        this.speed = speed;
    }

    public void displayInfo() {
        System.out.println("Spacecraft: " + name + " | Fuel: " + fuel
                + " | Altitude: " + altitude + " | Speed: " + speed);
    }
}
