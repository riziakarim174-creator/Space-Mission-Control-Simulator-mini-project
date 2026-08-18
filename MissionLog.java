import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class MissionLog {
    private String fileName;

    public MissionLog() {
        this.fileName = "mission_log.txt";
    }

    public void logEvent(String event) {
        try (FileWriter writer = new FileWriter(fileName, true)) {
            writer.write("[" + LocalDateTime.now() + "] " + event + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Error writing to log: " + e.getMessage());
        }
    }
}
