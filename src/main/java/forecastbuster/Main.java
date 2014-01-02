package forecastbuster;

public class Main {
    public static DatabaseAccessObject databaseAccessObject;
    public static final String FORECAST_URL = "http://www.emhi.ee/ilma_andmed/xml/forecast.php";
    public static final String OBSERVATION_URL = "http://www.emhi.ee/ilma_andmed/xml/observations.php";
    public static final String KEY_DATE = "date";
    public static final String KEY_NIGHT = "night";
    public static final String KEY_DAY = "day";
    public static final String KEY_PHENOMENON = "phenomenon";
    public static final String KEY_TEMPMIN = "tempmin";
    public static final String KEY_TEMPMAX = "tempmax";
    public static final String KEY_TEXT = "text";
    public static final String KEY_PLACE = "place";
    public static final String KEY_NAME = "name";
    public static final String KEY_OBSERVATIONS = "observations";
    public static final String KEY_FORECAST = "forecast";
    public static final Object actionLockObject = new Object();

    public static DatabaseAccessObject getDatabaseAccessObject() {
        if (databaseAccessObject == null) {
            databaseAccessObject = new DatabaseAccessObject();
        }
        return databaseAccessObject;
    }
}