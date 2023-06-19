package code.utilities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CurrentDate {

    /**
     * It gets the current date and time
     * @return the current date and time with "dd/MM/yyyy - HH:mm" format
     */
    public static String getCurrentDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");
        LocalDateTime now = LocalDateTime.now();
        
        return formatter.format(now);
    }
}
