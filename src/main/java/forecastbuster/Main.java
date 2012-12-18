package forecastbuster;

import forecastbuster.outgoing.Query;
import forecastbuster.outgoing.Server;

import java.io.IOException;

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
    public static final String KEY_OBSERVATIONS = "observations";
    public static final String KEY_FORECAST = "forecast";
    public static int timeBetweenFetchingData = 1000 * 60 * 60;
    public static int timeBetweenQuerying = 1000 * 60 * 60 * 24;
    public static final Object actionLockObject = new Object();
    public static final Object forecastQueryLockObject = new Object();
    public static final Object observationQueryLockObject = new Object();
    public static final String FORECAST_FILENAME_OUT = "Forecast.xml";
    public static final String OBSERVATION_FILENAME_OUT = "Observation.xml";

    public static void main(String[] args) throws IOException {
        databaseAccessObject.initSession();
        Fetch fetch = new Fetch(getDatabaseAccessObject());

        Query query = new Query();
        query.start(getDatabaseAccessObject());

        Server server = new Server();
        server.startServer(query);
    }

    public static DatabaseAccessObject getDatabaseAccessObject() {
        if (databaseAccessObject == null) {
            databaseAccessObject = new DatabaseAccessObject();
        }
        return databaseAccessObject;
    }
}