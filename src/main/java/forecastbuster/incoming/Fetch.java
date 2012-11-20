package forecastbuster.incoming;

import forecastbuster.DatabaseAccessObject;

import java.util.Timer;

import static forecastbuster.Main.timeBetweenFetchingData;

public class Fetch {
    DatabaseAccessObject databaseAccessObject;

    public Fetch(DatabaseAccessObject databaseAccessObject, Object actionLockObject) {
        this.databaseAccessObject = databaseAccessObject;
        Timer fetchTimer = new Timer("fetchTimer");
        FetchTask dataFetchTask = new FetchTask(actionLockObject);
        fetchTimer.schedule(dataFetchTask, 0, timeBetweenFetchingData);
    }
}
