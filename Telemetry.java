public class Telemetry {
    private double fuel;
    private double altitude;
    private double speed;
    private String status; 
    public Telemetry(double fuel, double altitude, double speed) {
        this(fuel, altitude, speed, "IDLE"); 
    }

    // overloaded constructor that also carries a status label
    public Telemetry(double fuel, double altitude, double speed, String status) {
        this.fuel = fuel;
        this.altitude = altitude;
        this.speed = speed;
        this.status = status;
    }

    public void showTelemetry() {
        System.out.println("Telemetry -> Fuel: " + fuel
                + ", Altitude: " + altitude + ", Speed: " + speed + ", Status: " + status);
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

    public String getStatus() { // Updated (last time)
        return status;
    }
}
