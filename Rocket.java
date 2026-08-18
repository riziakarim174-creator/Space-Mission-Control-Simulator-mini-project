public class Rocket extends Spacecraft {

    public Rocket(String name, double fuel) {
        super(name, fuel);
    }

    public void launch() {
        System.out.println(name + " is launching!");
        this.speed += 50;
        this.altitude += 100;
        this.fuel -= 20;
    }

    public void boost() {
        System.out.println(name + " boosting engines!");
        this.speed += 20;
        this.fuel -= 10;
    }
}
