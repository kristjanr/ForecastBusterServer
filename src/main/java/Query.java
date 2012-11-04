import java.util.List;
import java.util.Timer;

public class Query {
    static List ForecastsForTomorrow;
    static DatabaseAccessObject databaseAccessObject;

    public Query(DatabaseAccessObject databaseAccessObject) {
        this.databaseAccessObject = databaseAccessObject;
        Timer queryTimer = new Timer("queryTimer");
        QueryTask queryTask = new QueryTask();
        queryTimer.schedule(queryTask, 0, Main.timeBetweenQuering);
    }

    public static List getForecastsForTomorrow() {
        return ForecastsForTomorrow;
    }

    public static void setForecastsForTomorrow(List list) {
        Query.ForecastsForTomorrow = list;
    }

}
