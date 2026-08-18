import java.io.BufferedReader;
import java.io.IOException;

// Handles listening for telemetry from the connected spacecraft (client).
// Its background thread only runs while "View Live Mission Status" is open -
// it is explicitly started/stopped, never runs permanently in the background.
public class ClientHandler {
    private final BufferedReader in;
    private Thread listenerThread;
    private volatile boolean active = false;
    private volatile Telemetry latestTelemetry;

    public ClientHandler(BufferedReader in) {
        this.in = in;
    }

    // Starts the background listening thread - call ONLY when Live Mission Status opens
    public void start() {
        if (active) {
            return; // already running, avoid starting a second thread
        }
        active = true;
        listenerThread = new Thread(() -> {
            try {
                while (active) {
                    if (in.ready()) {
                        String line = in.readLine();
                        if (line != null && line.startsWith("TELEMETRY:")) {
                            String[] parts = line.replace("TELEMETRY:", "").split(",");
                            double fuel = Double.parseDouble(parts[0]);
                            double altitude = Double.parseDouble(parts[1]);
                            double speed = Double.parseDouble(parts[2]);
                            String status = parts.length > 3 ? parts[3] : "LAUNCHED";
                            latestTelemetry = new Telemetry(fuel, altitude, speed, status);
                            System.out.println("[SOCKET] Telemetry received from Spacecraft");
                        }
                    } else {
                        Thread.sleep(200);
                    }
                }
            } catch (IOException | InterruptedException e) {
                // Loop stopped - this is the expected, clean way stop() ends the thread
            }
        });
        listenerThread.start();
    }

    // Stops the background listening thread completely - call when the user presses Q
    public void stop() {
        active = false;
        if (listenerThread != null) {
            listenerThread.interrupt();
        }
    }

    public void setLatestTelemetry(Telemetry telemetry) {
        this.latestTelemetry = telemetry;
    }

    public Telemetry getLatestTelemetry() {
        return latestTelemetry;
    }
}
