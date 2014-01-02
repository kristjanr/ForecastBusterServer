package forecastbuster;

import forecastbuster.incoming.entities.Forecast;
import forecastbuster.incoming.entities.Observation;
import forecastbuster.incoming.entities.Place;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class DatabaseAccessObject {
    private Session hibernateSession;
    private static final Logger log = LoggerFactory.getLogger(DatabaseAccessObject.class);
    private final Object sessionLockObject = new Object();
    private SessionFactory sessionFactory;

    public DatabaseAccessObject() {
    }

    public void initSession() {
        Properties properties = new Properties();
        try {
            URL pUrl = this.getClass().getResource("/database.properties");
            properties.load(pUrl.openStream());
        } catch (IOException e) {
            log.error("Error while reading database.properties file", e);
        }

        Configuration databaseConfiguration = new Configuration();
        databaseConfiguration.configure();
        databaseConfiguration.setProperty("hibernate.connection.url", properties.getProperty("database.url"));
        databaseConfiguration.setProperty("hibernate.connection.username", properties.getProperty("database.user"));
        databaseConfiguration.setProperty("hibernate.connection.password", properties.getProperty("database.password"));
        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(databaseConfiguration.getProperties()).buildServiceRegistry();
        sessionFactory = databaseConfiguration.buildSessionFactory(serviceRegistry);
        hibernateSession = sessionFactory.openSession();
    }

    public void close() {
        hibernateSession.close();
        sessionFactory.close();
    }

    public void saveObservationsAndSForecasts(Observation observation, ArrayList<Forecast> forecasts) {
        long begin = new Date().getTime();

        Transaction transaction = hibernateSession.beginTransaction();
        saveObservationAndStations(observation);
        saveForecastsAndPlaces(forecasts);
        transaction.commit();

        long end = new Date().getTime();
        int timeForSavingForecastsAndObservations = (int) (end - begin);
        log.info("Saved observation and forecasts in " + timeForSavingForecastsAndObservations);
    }

    void saveObservationAndStations(Observation observation) {
        synchronized (sessionLockObject) {
            hibernateSession.save(observation);

            for (int i = 0; i < observation.getStations().size(); i++) {
                hibernateSession.save(observation.getStations().get(i));
            }
        }
    }

    void saveForecastsAndPlaces(ArrayList<Forecast> forecasts) {
        Forecast forecast;
        List<Place> places;
        synchronized (sessionLockObject) {
            for (Forecast forecast1 : forecasts) {
                forecast = forecast1;
                hibernateSession.save(forecast);
                places = forecast.getPlaces();
                for (Place place : places) {
                    hibernateSession.save(place);
                }
            }
        }
    }
}
