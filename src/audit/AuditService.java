package audit;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//singleton and thread safe
public class AuditService {
    private static final String AUDIT_FILE_PATH = "./src/audit/audit.csv";
    private static AuditService instance;

    private FileWriter writer;
    private DateTimeFormatter formatter;
    private AuditService() {
        try {
            this.writer = new FileWriter(AUDIT_FILE_PATH, true); // append mode
            this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        } catch (IOException e) {
            System.err.println("Error initializing AuditService: " + e.toString());
        }
    }
    public static synchronized AuditService getInstance() {
        if (instance == null) {
            instance = new AuditService();
        }
        return instance;
    }
    public void logAction(String action){
        String log = action + ',' + LocalDateTime.now().format(formatter) + '\n';
        try {
            writer.append(log);
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error logging action: " + e.toString());
        }
    }
    //ensuring that the FileWriter is properly closed when the application exists
    public void close() {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing FileWriter: " + e.toString());
        }
    }
}
