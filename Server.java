import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements CommandCenter {
    private ServerSocket serverSocket;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    // Updated (last time): telemetry listening is now handled by a dedicated ClientHandler
    // whose thread only runs while Live Mission Status is open - no permanent background thread.
    private ClientHandler clientHandler;

    public void startServer(int port) throws IOException {
        System.out.println("========== MISSION CONTROL SERVER ==========");
        System.out.println("Server started...");
        System.out.println("Waiting for spacecraft connection...");

        serverSocket = new ServerSocket(port);
        socket = serverSocket.accept();
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        System.out.println("\n\u2714 Spacecraft Client connected!");
        System.out.println("Socket connection established.");
        System.out.println("Client IP: " + socket.getInetAddress().getHostAddress());
        System.out.println("==============================================\n");

        // Updated (last time): just wires up the handler - its thread does NOT start yet
        clientHandler = new ClientHandler(in);
    }

    // Updated (last time): starts the live telemetry listening thread -
    // call ONLY when "View Live Mission Status" is opened
    public void startLiveTelemetry() {
        clientHandler.start();
    }

    // Updated (last time): stops the live telemetry listening thread completely -
    // call the moment the user presses Q to leave Live Mission Status
    public void stopLiveTelemetry() {
        clientHandler.stop();
    }

    // Updated (last time): now delegates to the ClientHandler instead of a raw field
    public void updateLatestTelemetry(Telemetry telemetry) {
        clientHandler.setLatestTelemetry(telemetry);
    }

    public Telemetry getLatestTelemetry() {
        return clientHandler.getLatestTelemetry();
    }

    @Override
    public void sendCommand(String command) throws Exception {
        if (command == null || command.isEmpty()) {
            throw new InvalidCommandException("Command cannot be empty!");
        }
        out.println("COMMAND:" + command);
        System.out.println("[SOCKET] Command sent to Spacecraft: " + command);
    }

    @Override
    public String receiveCommand() throws Exception {
        String line = in.readLine();
        if (line == null) {
            throw new ConnectionException("Connection lost!");
        }
        return line;
    }

    public void broadcastAlert(String alert) {
        out.println("ALERT:" + alert);
    }

    @Override
    public void sendTelemetry(Telemetry telemetry) throws Exception {
        String data = "TELEMETRY:" + telemetry.getFuel() + "," + telemetry.getAltitude() + "," + telemetry.getSpeed();
        out.println(data);
    }

    // Kept for interface compatibility; superseded by ClientHandler above.
    @Override
    public Telemetry receiveTelemetry() throws Exception {
        String line = in.readLine();
        if (line == null) {
            throw new ConnectionException("Connection lost!");
        }
        String[] parts = line.replace("TELEMETRY:", "").split(",");
        return new Telemetry(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
    }

    public void closeServer() throws IOException {
        if (clientHandler != null) {
            clientHandler.stop(); // Updated (last time): make sure nothing lingers on exit
        }
        if (in != null) in.close();
        if (out != null) out.close();
        if (socket != null) socket.close();
        if (serverSocket != null) serverSocket.close();
        System.out.println("Server closed.");
    }
}
