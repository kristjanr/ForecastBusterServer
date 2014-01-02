package forecastbuster.incoming;

import forecastbuster.Main;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Fetch implements ServletContextListener {
    private final static int TIME_BETWEEN_FETCHING_DATA = 1;
    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        Main.getDatabaseAccessObject().initSession();
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new FetchTask(), 0, TIME_BETWEEN_FETCHING_DATA, TimeUnit.HOURS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        scheduler.shutdownNow();
        Main.getDatabaseAccessObject().close();
    }
}
