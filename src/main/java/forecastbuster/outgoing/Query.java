package forecastbuster.outgoing;

import forecastbuster.DatabaseAccessObject;
import forecastbuster.Main;
import forecastbuster.outgoing.entities.ForecastedDay;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Scanner;
import java.util.Timer;
import java.util.TreeMap;

public class Query {
    private static DatabaseAccessObject databaseAccessObject;
    private TreeMap<Calendar, ForecastedDay> forecastDays;
    static org.slf4j.Logger log = LoggerFactory.getLogger(Query.class);

    public Query() {
        forecastDays = new TreeMap<Calendar, ForecastedDay>();
    }

    public void startForecastQuery(DatabaseAccessObject databaseAccessObject, Object actionLockObject) {
        this.databaseAccessObject = databaseAccessObject;
        synchronized (actionLockObject){

            Timer queryTimer = new Timer("queryTimer");
            QueryTaskForecast queryTaskForecast = new QueryTaskForecast(this);
            queryTimer.schedule(queryTaskForecast, 0, Main.timeBetweenQuerying);
            QueryTaskObservation queryTaskObservation = new QueryTaskObservation(this);
            queryTimer.schedule(queryTaskObservation, 0, Main.timeBetweenQuerying);
        }
    }

    static DatabaseAccessObject getDatabaseAccessObject() {
        return databaseAccessObject;
    }

    public TreeMap<Calendar, ForecastedDay> getForecastDays() {
        return forecastDays;
    }

    public void setForecastDays(TreeMap<Calendar, ForecastedDay> forecastDays) {
        this.forecastDays = forecastDays;
    }

    String getSqlString(String sqlFileName) {
        InputStream inputStream = this.getClass().getResourceAsStream("/" + sqlFileName);
        String theString = convertStreamToString(inputStream);
        return theString;
    }

    public String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
