public abstract class Mission {
    protected String missionName;

    public Mission(String missionName) {
        this.missionName = missionName;
    }

    public abstract void startMission();

    public abstract void endMission();
}
