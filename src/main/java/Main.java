import java.util.ArrayList;
import java.util.Timer;

public class Main {
    public static DatabaseAccessObject databaseAccessObject = new DatabaseAccessObject();
    public static final String FORECAST_URL = "http://www.emhi.ee/ilma_andmed/xml/forecast.php";
    public static final String FORECAST_FILE = "D:\\Projekt\\ForecastBusterServer\\forecast.xml";
    public static final String OBSERVATION_URL = "http://www.emhi.ee/ilma_andmed/xml/observations.php";
    public static final String OBSERVATION_FILE = "D:\\Projekt\\ForecastBusterServer\\observation.xml";
    public static final String KEY_DATE = "date";
    public static final String KEY_NIGHT = "night";
    public static final String KEY_DAY = "day";
    public static final String KEY_PHENOMENON = "phenomenon";
    public static final String KEY_TEMPMIN = "tempmin";
    public static final String KEY_TEMPMAX = "tempmax";
    public static final String KEY_TEXT = "text";
    public static final String KEY_PLACE = "place";
    public static final String KEY_NAME = "name";
    public static final String KEY_WIND = "wind";
    public static final String KEY_DIRECTION = "direction";
    public static final String KEY_SPEEDMIN = "speedmin";
    public static final String KEY_SPEEDMAX = "speedmax";
    public static final String KEY_SEA = "sea";
    public static final String KEY_PEIPSI = "peipsi";
    public static ArrayList<Forecast> forecasts = new ArrayList(8);
    public static final String KEY_OBSERVATIONS = "observations";
    static final String KEY_FORECAST = "forecast";
    private static int timeBetweenFetchingData = 3600000;

    public static void main(String[] args) {
        Timer timer = new Timer("timer");
        TimedTask timedTask = new TimedTask();
        timer.schedule(timedTask, 0, timeBetweenFetchingData);
    }


}
