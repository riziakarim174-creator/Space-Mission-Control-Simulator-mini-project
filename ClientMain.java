public class ClientMain {

    private static volatile boolean launched = false;
    private static volatile boolean liveTelemetryActive = false; 
    private static volatile boolean running = true;
    private static volatile double currentSpeed = 0;
    private static int tick = 0;

    private static final double CRUISE_SPEED = 300;
    private static final double RAMP_STEP = 100;
    private static final int RAMP_TICKS = 3;

    private static Rocket spacecraft;
    private static Client client;
    private static Thread telemetryThread; 

    public static void main(String[] args) throws Exception {
        System.out.println("========== SPACECRAFT CLIENT ==========");
        System.out.println("Connecting to Mission Control...");

        client = new Client();
        client.connectServer("localhost", 5000);

        System.out.println("Spacecraft ready.");
        System.out.println("========================================\n");

   
        spacecraft = new Rocket("Client-Spacecraft", 100);


        while (running) {
            String message = client.receiveCommand();
            if (message == null) {
                break;
            }

            if (message.startsWith("COMMAND:")) {
                String command = message.replace("COMMAND:", "");
                System.out.println("Command received: " + command);
                handleCommand(command);
            } else if (message.startsWith("ALERT:")) {
                System.out.println("!!! ALERT: " + message.replace("ALERT:", ""));
            }
        }

        stopTelemetryLoop(); 
        client.disconnect();
        System.out.println("Client shut down.");
    }

    private static void handleCommand(String command) {
        if (command.equals("DISCONNECT")) {
            running = false;
        } else if (command.equals("LAUNCH")) {
            spacecraft.launch();
            launched = true;
            tick = 0;
            currentSpeed = 0;
        } else if (command.equals("BOOST")) {
            currentSpeed += 50; // exactly one +50 km/h bump per boost, held constant after
        } else if (command.startsWith("SET_FUEL:")) {
            double fuelAmount = Double.parseDouble(command.replace("SET_FUEL:", ""));
            spacecraft.restoreState(fuelAmount, spacecraft.getAltitude(), spacecraft.getSpeed());
            System.out.println("Initial fuel set to: " + fuelAmount);
        } else if (command.equals("START_LIVE_TELEMETRY")) { 
            startTelemetryLoop();
        } else if (command.equals("STOP_LIVE_TELEMETRY")) { 
            stopTelemetryLoop();
        }
        // Other commands (MISSION_START, DEPLOY, custom text) don't affect telemetry
    }

    //  starts the ticking thread - only called while Live Mission Status is open
    private static void startTelemetryLoop() {
        if (liveTelemetryActive) {
            return; // already running, don't start a second thread
        }
        liveTelemetryActive = true;
        telemetryThread = new Thread(ClientMain::telemetryLoop);
        telemetryThread.start();
    }

    // : stops the ticking thread completely - called the moment Q is pressed
    private static void stopTelemetryLoop() {
        liveTelemetryActive = false;
        if (telemetryThread != null) {
            telemetryThread.interrupt();
        }
    }

    //  runs only between startTelemetryLoop() and stopTelemetryLoop().
    // Fuel/altitude/speed keep whatever value they had last time - never reset here.
    private static void telemetryLoop() {
        while (liveTelemetryActive && running) {
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                break; // stop() was called - exit cleanly
            }

            if (!liveTelemetryActive) {
                break;
            }
            if (!launched) {
                continue;
            }

            tick++;


            if (tick <= RAMP_TICKS) {
                currentSpeed = Math.min(CRUISE_SPEED, tick * RAMP_STEP);
            }

            double newFuel = Math.max(0, spacecraft.getFuel() - 50);
            double newAltitude = spacecraft.getAltitude() + 50;
            spacecraft.restoreState(newFuel, newAltitude, currentSpeed);

            String status = newFuel <= 0 ? "FUEL EMPTY" : "LAUNCHED";

            try {
                Telemetry telemetry = new Telemetry(newFuel, newAltitude, currentSpeed, status);
                client.sendTelemetry(telemetry);
                System.out.println("Telemetry tick -> Fuel: " + newFuel
                        + ", Altitude: " + newAltitude + ", Speed: " + currentSpeed);
            } catch (Exception e) {
                System.out.println("Error sending telemetry: " + e.getMessage());
            }
        }
    }
}
