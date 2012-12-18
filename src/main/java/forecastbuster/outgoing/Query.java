package forecastbuster.outgoing;

import forecastbuster.DatabaseAccessObject;
import forecastbuster.Main;
import forecastbuster.outgoing.entities.ForecastedDay;
import forecastbuster.outgoing.entities.Observation;
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
    private TreeMap<Calendar, Observation> observations;

    public Query() {
        forecastDays = new TreeMap<Calendar, ForecastedDay>();
        observations = new TreeMap<Calendar, Observation>();
    }

    public void start(DatabaseAccessObject databaseAccessObject) {
        Query.databaseAccessObject = databaseAccessObject;
        synchronized (Main.actionLockObject){
            Timer queryTimer = new Timer("queryTimer");
            startForecastQuery(queryTimer);
            Timer queryTimer2 = new Timer("queryTimer2");
            startObservationQuery(queryTimer2);
        }
    }

    private void startForecastQuery(Timer queryTimer) {
        QueryTaskForecast queryTaskForecast = new QueryTaskForecast(this);
        queryTimer.schedule(queryTaskForecast, 0, Main.timeBetweenQuerying);
    }

    private void startObservationQuery(Timer queryTimer) {
        QueryTaskObservation queryTaskObservation = new QueryTaskObservation(this);
        queryTimer.schedule(queryTaskObservation, 0, Main.timeBetweenQuerying);
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
        return convertStreamToString(inputStream);
    }

    public String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public void setObservations(TreeMap<Calendar, Observation> observations) {
        this.observations = observations;
    }

    public TreeMap<Calendar, Observation> getObservations() {
        return observations;
    }
}
