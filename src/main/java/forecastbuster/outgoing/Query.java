package forecastbuster.outgoing;

import forecastbuster.DatabaseAccessObject;
import forecastbuster.Main;
import forecastbuster.outgoing.entities.ForecastedDay;

import java.util.*;

public class Query {
    private static DatabaseAccessObject databaseAccessObject;
    private TreeMap<Calendar, ForecastedDay> forecastDays;

    public Query() {
        forecastDays = new TreeMap<Calendar, ForecastedDay>();
    }

    public void startQuery(DatabaseAccessObject databaseAccessObject) {
        this.databaseAccessObject = databaseAccessObject;
        Timer queryTimer = new Timer("queryTimer");
        QueryTask queryTask = new QueryTask(this);
        queryTimer.schedule(queryTask, 0, Main.timeBetweenQuering);
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
