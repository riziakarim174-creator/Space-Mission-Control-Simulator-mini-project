import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements CommandCenter {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public void connectServer(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("\u2714 Connected to server successfully!"); // Updated (last time)
    }

    @Override
    public void sendCommand(String command) throws Exception {
        if (command == null || command.isEmpty()) {
            throw new InvalidCommandException("Command cannot be empty!");
        }
        out.println("COMMAND:" + command);
    }

    @Override
    public String receiveCommand() throws Exception {
        String line = in.readLine();
        if (line == null) {
            throw new ConnectionException("Connection lost!");
        }
        return line;
    }

    public void receiveAlert() throws IOException {
        String line = in.readLine();
        if (line != null && line.startsWith("ALERT:")) {
            System.out.println("!!! " + line.replace("ALERT:", ""));
        }
    }

    @Override
    public void sendTelemetry(Telemetry telemetry) throws Exception {
        // Updated (last time): status is now included so Mission Control can show it live
        String data = "TELEMETRY:" + telemetry.getFuel() + "," + telemetry.getAltitude()
                + "," + telemetry.getSpeed() + "," + telemetry.getStatus();
        out.println(data);
        System.out.println("[SOCKET] Telemetry sent to Mission Control"); // Updated (last time)
    }

    @Override
    public Telemetry receiveTelemetry() throws Exception {
        String line = in.readLine();
        if (line == null) {
            throw new ConnectionException("Connection lost!");
        }
        String[] parts = line.replace("TELEMETRY:", "").split(",");
        return new Telemetry(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
    }

    public void disconnect() throws IOException {
        if (in != null) in.close();
        if (out != null) out.close();
        if (socket != null) socket.close();
        System.out.println("Disconnected from Mission Control.");
    }
}
