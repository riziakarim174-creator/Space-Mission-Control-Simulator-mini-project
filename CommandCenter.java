public interface CommandCenter {
    void sendCommand(String command) throws Exception;

    String receiveCommand() throws Exception;

    void sendTelemetry(Telemetry telemetry) throws Exception;

    Telemetry receiveTelemetry() throws Exception;
}
