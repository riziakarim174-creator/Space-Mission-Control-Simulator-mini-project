public class Satellite extends Spacecraft {

    public Satellite(String name, double fuel) {
        super(name, fuel);
    }

    public void deploy() {
        System.out.println(name + " deployed into orbit!");
        this.altitude += 500;
    }
}
