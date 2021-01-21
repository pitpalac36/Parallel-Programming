import java.util.Calendar;

public class TimeUtils {

    public static String getFormatterTime() {
        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        return "ora " + hours + ":" + minutes + ":" + seconds;
    }
}
