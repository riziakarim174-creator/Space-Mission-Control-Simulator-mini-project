import java.util.HashMap;
import java.util.Map;

public class User {
    private String username;
    private String password;

 
    private static Map<String, String> userDatabase = new HashMap<>();
    static {
        userDatabase.put("admin", "1234");
        userDatabase.put("operator", "space123");
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean login() {
        return userDatabase.containsKey(username)
                && userDatabase.get(username).equals(password);
    }

    public String getUserName() {
        return username;
    }
}
