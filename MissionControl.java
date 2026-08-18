public class MissionControl {
    private Spacecraft spacecraft;
    private SpaceMission mission;
    private Server server;
    private MissionLog log;

    public MissionControl() {
        this.log = new MissionLog();
    }

    public void createMission(String missionName) {
        this.mission = new SpaceMission(missionName);
        log.logEvent("Mission created: " + missionName);
    }

    public void assignSpacecraft(Spacecraft spacecraft) {
        this.spacecraft = spacecraft;
        log.logEvent("Spacecraft assigned: " + spacecraft.getName());
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public void startMission() {
        if (mission != null) {
            mission.startMission();
            log.logEvent("Mission started.");
        }
    }

    public void sendMissionCommand(String command) {
        try {
            server.sendCommand(command);
            log.logEvent("Command sent: " + command);
        } catch (Exception e) {
            System.out.println("Error sending command: " + e.getMessage());
        }
    }

    public void endMission() {
        if (mission != null) {
            mission.endMission();
            log.logEvent("Mission ended.");
        }
    }

    public void updateTelemetry() {
        if (spacecraft != null) {
            spacecraft.updateTelemetry();
            spacecraft.displayInfo();
        }
    }

    // Updated (last time): exposes the server's live telemetry without blocking -
    // used by the "View Live Mission Status" menu option
    public Telemetry getLatestTelemetry() {
        return server.getLatestTelemetry();
    }

    // Updated (last time): start/stop the live telemetry listening thread -
    // strictly tied to Live Mission Status being open/closed
    public void startLiveTelemetry() {
        server.startLiveTelemetry();
    }

    public void stopLiveTelemetry() {
        server.stopLiveTelemetry();
    }
}
