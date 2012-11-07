package forecastbuster.outgoing;

import forecastbuster.DatabaseAccessObject;
import forecastbuster.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class Query {
    List forecastsForTomorrow;
    List forecastsFor1DayAfterTomorrow;
    List forecastsFor2DaysAfterTomorrow;
    List forecastsFor3DaysAfterTomorrow;

    static DatabaseAccessObject databaseAccessObject;
    public Query() {
        forecastsForTomorrow = new ArrayList();
        forecastsFor1DayAfterTomorrow = new ArrayList();
        forecastsFor2DaysAfterTomorrow  = new ArrayList();
        forecastsFor3DaysAfterTomorrow  = new ArrayList();
    }

    public void startQuery(DatabaseAccessObject databaseAccessObject) {
        this.databaseAccessObject = databaseAccessObject;
        Timer queryTimer = new Timer("queryTimer");
        QueryTask queryTask = new QueryTask(this);
        queryTimer.schedule(queryTask, 0, Main.timeBetweenQuering);
    }

    public List getForecastsForTomorrow() {
        return forecastsForTomorrow;
    }

    public void setForecastsForTomorrow(List list) {
        forecastsForTomorrow = list;
    }

    public void setForecastsFor1DayAfterTomorrow(List list) {
        this.forecastsFor1DayAfterTomorrow =list;
    }

    public List getForecastsFor1DayAfterTomorrow() {
        return forecastsFor1DayAfterTomorrow;
    }

    public List getForecastsFor2DaysAfterTomorrow() {
        return forecastsFor2DaysAfterTomorrow;
    }

    public void setForecastsFor2DaysAfterTomorrow(List forecastsFor2DaysAfterTomorrow) {
        this.forecastsFor2DaysAfterTomorrow = forecastsFor2DaysAfterTomorrow;
    }

    public List getForecastsFor3DaysAfterTomorrow() {
        return forecastsFor3DaysAfterTomorrow;
    }

    public void setForecastsFor3DaysAfterTomorrow(List forecastsFor3DaysAfterTomorrow) {
        this.forecastsFor3DaysAfterTomorrow = forecastsFor3DaysAfterTomorrow;
    }
}
