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

    private TreeMap<Calendar, ForecastedDay> forecastDays;
    static org.slf4j.Logger log = LoggerFactory.getLogger(Query.class);
    private TreeMap<Calendar, Observation> observations;

    public Query() {
        forecastDays = new TreeMap<Calendar, ForecastedDay>();
        observations = new TreeMap<Calendar, Observation>();
    }

    public void start() {
        synchronized (Main.actionLockObject) {
            startForecastQuery();
            startObservationQuery();
        }
    }

    private void startForecastQuery() {
        Timer queryTimer = new Timer("queryTimer");
        log.info("Started FORECAST queries");
        QueryTaskForecast queryTaskForecast = new QueryTaskForecast(this);
        queryTimer.schedule(queryTaskForecast, 0, Main.timeBetweenQuerying);
    }

    private void startObservationQuery() {
        Timer queryTimer2 = new Timer("queryTimer2");
        log.info("Started OBSERVATION queries");
        QueryTaskObservation queryTaskObservation = new QueryTaskObservation(this);
        queryTimer2.schedule(queryTaskObservation, 0, Main.timeBetweenQuerying);
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
