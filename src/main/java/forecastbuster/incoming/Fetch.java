package forecastbuster.incoming;

import java.util.Timer;

import static forecastbuster.Main.timeBetweenFetchingData;

public class Fetch {

    public void start() {
        Timer fetchTimer = new Timer("fetchTimer");
        FetchTask dataFetchTask = new FetchTask();
        fetchTimer.schedule(dataFetchTask, 0, timeBetweenFetchingData);
    }
}
