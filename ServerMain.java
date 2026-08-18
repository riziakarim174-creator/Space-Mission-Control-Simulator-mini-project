import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

public class ServerMain {

    private static final String STATE_FILE = "mission_state.properties";

    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);

        // 1. Real login
        System.out.println("===== MISSION CONTROL LOGIN =====");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        User user = new User(username, password);
        if (!user.login()) {
            System.out.println("Login failed! Invalid username or password.");
            scanner.close();
            return;
        }
        System.out.println("Welcome, " + user.getUserName() + "!\n");

        // 2. Start server and wait for the spacecraft (client) to connect
        Server server = new Server();
        server.startServer(5000); 

        MissionControl control = new MissionControl();
        control.setServer(server);

        String missionName = null;
        Spacecraft spacecraft = null;

        // 3. Restore previous session if a saved state file exists
        Properties saved = loadState();
        if (!saved.isEmpty()) {
            System.out.println(">>> Previous session found! Restoring saved state...");

            if (saved.containsKey("mission")) {
                missionName = saved.getProperty("mission");
                control.createMission(missionName);
                System.out.println("Mission restored: " + missionName);
            }

            if (saved.containsKey("name")) {
                String type = saved.getProperty("type");
                String name = saved.getProperty("name");
                double fuel = Double.parseDouble(saved.getProperty("fuel"));
                double altitude = Double.parseDouble(saved.getProperty("altitude"));
                double speed = Double.parseDouble(saved.getProperty("speed"));

                spacecraft = "SATELLITE".equals(type) ? new Satellite(name, fuel) : new Rocket(name, fuel);
                spacecraft.restoreState(fuel, altitude, speed);
                control.assignSpacecraft(spacecraft);

                System.out.println("Spacecraft restored:");
                spacecraft.displayInfo();

                //  seed the live status view with the restored baseline
                server.updateLatestTelemetry(new Telemetry(fuel, altitude, speed, "IDLE"));
            }
        }

        // 4. Main menu loop
        boolean running = true;
        while (running) {
            System.out.println("\n===== MISSION CONTROL MENU =====");
            System.out.println("1.  Create Mission");
            System.out.println("2.  Assign Spacecraft (Rocket)");
            System.out.println("3.  Assign Spacecraft (Satellite)");
            System.out.println("4.  Start Mission");
            System.out.println("5.  Launch Rocket");
            System.out.println("6.  Boost Rocket");
            System.out.println("7.  Deploy Satellite");
            System.out.println("8.  Send Custom Command");
            System.out.println("9.  Broadcast Alert");
            System.out.println("10. View Live Mission Status"); 
            System.out.println("11. End Mission");
            System.out.println("0.  Exit & Close Server");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.print("Enter mission name: ");
                    missionName = scanner.nextLine();
                    control.createMission(missionName);
                    saveState(missionName, spacecraft);
                    break;

                case "2":
                    System.out.print("Enter rocket name: ");
                    String rName = scanner.nextLine();
                    System.out.print("Enter fuel amount: ");
                    double rFuel = Double.parseDouble(scanner.nextLine());
                    spacecraft = new Rocket(rName, rFuel);
                    control.assignSpacecraft(spacecraft);
                    saveState(missionName, spacecraft);

               
                    control.sendMissionCommand("SET_FUEL:" + rFuel);
                    server.updateLatestTelemetry(new Telemetry(rFuel, 0, 0, "IDLE"));
                    break;

                case "3":
                    System.out.print("Enter satellite name: ");
                    String sName = scanner.nextLine();
                    System.out.print("Enter fuel amount: ");
                    double sFuel = Double.parseDouble(scanner.nextLine());
                    spacecraft = new Satellite(sName, sFuel);
                    control.assignSpacecraft(spacecraft);
                    saveState(missionName, spacecraft);

                    control.sendMissionCommand("SET_FUEL:" + sFuel); 
                    server.updateLatestTelemetry(new Telemetry(sFuel, 0, 0, "IDLE"));
                    break;

                case "4":
                    control.startMission();
                    control.sendMissionCommand("MISSION_START");
                    break;

                case "5":
                    if (spacecraft instanceof Rocket) {
                        ((Rocket) spacecraft).launch();
                        control.sendMissionCommand("LAUNCH");
                        saveState(missionName, spacecraft);
                    
                    } else {
                        System.out.println("No rocket assigned yet! Choose option 2 first.");
                    }
                    break;

                case "6":
                    if (spacecraft instanceof Rocket) {
                        ((Rocket) spacecraft).boost();
                        control.sendMissionCommand("BOOST");
                        saveState(missionName, spacecraft);
                    } else {
                        System.out.println("No rocket assigned yet! Choose option 2 first.");
                    }
                    break;

                case "7":
                    if (spacecraft instanceof Satellite) {
                        ((Satellite) spacecraft).deploy();
                        control.sendMissionCommand("DEPLOY");
                        saveState(missionName, spacecraft);
                    } else {
                        System.out.println("No satellite assigned yet! Choose option 3 first.");
                    }
                    break;

                case "8":
                    System.out.print("Enter custom command: ");
                    control.sendMissionCommand(scanner.nextLine());
                    break;

                case "9":
                    System.out.print("Enter alert message: ");
                    server.broadcastAlert(scanner.nextLine());
                    break;

                case "10":
                    //  live, auto-refreshing telemetry screen
                    viewLiveMissionStatus(control, scanner);
                    break;

                case "11":
                    control.endMission();
                    break;

                case "0":
                    running = false;
                    control.sendMissionCommand("DISCONNECT");
                    break;

                default:
                    System.out.println("Invalid option, try again.");
            }
        }

        server.closeServer();
        scanner.close();
        System.out.println("Mission Control shut down.");
    }

   
    private static void viewLiveMissionStatus(MissionControl control, Scanner scanner) throws InterruptedException {
        System.out.println("\n========== LIVE MISSION STATUS ==========");


        control.sendMissionCommand("START_LIVE_TELEMETRY");
        control.startLiveTelemetry();

        final boolean[] stop = {false};
        Thread inputWatcher = new Thread(() -> {
            while (!stop[0]) {
                String line = scanner.nextLine();
                if (line.trim().equalsIgnoreCase("Q")) {
                    stop[0] = true;
                }
            }
        });
        inputWatcher.setDaemon(true);
        inputWatcher.start();

        while (!stop[0]) {
            Telemetry t = control.getLatestTelemetry();
            System.out.println();
            if (t != null) {
                System.out.println("Fuel       : " + t.getFuel());
                System.out.println("Altitude   : " + t.getAltitude() + " km");
                System.out.println("Speed      : " + t.getSpeed() + " km/h");
                System.out.println("Status     : " + t.getStatus());
            } else {
                System.out.println("No telemetry yet - assign a spacecraft first.");
            }
            System.out.println();
            System.out.println("Updating every 2 seconds...");
            System.out.println("Press Q to return to Main Menu");
            Thread.sleep(2500);
        }

   
        control.sendMissionCommand("STOP_LIVE_TELEMETRY");
        control.stopLiveTelemetry();

        System.out.println("Returning to Main Menu...\n");
    }



    private static void saveState(String missionName, Spacecraft spacecraft) {
        Properties props = new Properties();
        if (missionName != null) {
            props.setProperty("mission", missionName);
        }
        if (spacecraft != null) {
            props.setProperty("type", spacecraft instanceof Satellite ? "SATELLITE" : "ROCKET");
            props.setProperty("name", spacecraft.getName());
            props.setProperty("fuel", String.valueOf(spacecraft.getFuel()));
            props.setProperty("altitude", String.valueOf(spacecraft.getAltitude()));
            props.setProperty("speed", String.valueOf(spacecraft.getSpeed()));
        }
        try (FileOutputStream out = new FileOutputStream(STATE_FILE)) {
            props.store(out, "Space Mission Control - saved session state");
        } catch (IOException e) {
            System.out.println("Could not save session state: " + e.getMessage());
        }
    }

    private static Properties loadState() {
        Properties props = new Properties();
        File file = new File(STATE_FILE);
        if (file.exists()) {
            try (FileInputStream in = new FileInputStream(file)) {
                props.load(in);
            } catch (IOException e) {
                System.out.println("Could not load previous session state: " + e.getMessage());
            }
        }
        return props;
    }
}
