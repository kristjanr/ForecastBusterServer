package forecastbuster.outgoing;

import forecastbuster.DatabaseAccessObject;
import forecastbuster.Main;
import forecastbuster.outgoing.entities.ForecastedDay;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
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
            try {
                actionLockObject.wait();
            } catch (InterruptedException e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
            Timer queryTimer = new Timer("queryTimer");
            QueryTaskForecast queryTaskForecast = new QueryTaskForecast(this);
            queryTimer.schedule(queryTaskForecast, 0, Main.timeBetweenQuerying);
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
}
