public class SpaceMission extends Mission {

    public SpaceMission(String missionName) {
        super(missionName);
    }

    @Override
    public void startMission() {
        System.out.println("Mission '" + missionName + "' has started!");
    }

    @Override
    public void endMission() {
        System.out.println("Mission '" + missionName + "' has ended!");
    }
}
