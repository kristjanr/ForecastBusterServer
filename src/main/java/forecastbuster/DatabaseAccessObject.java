package forecastbuster;

import forecastbuster.incoming.entities.Forecast;
import forecastbuster.incoming.entities.Observation;
import forecastbuster.incoming.entities.Place;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.Query;
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
    static Logger log = LoggerFactory.getLogger(DatabaseAccessObject.class);
    private final Object sessionLockObject = new Object();

    public DatabaseAccessObject() {
    }

    public void initSession() {
        Properties properties = new Properties();
        try {
            URL pUrl = this.getClass().getResource("/database.properties");
            properties.load(pUrl.openStream());
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }

        Configuration databaseConfiguration = new Configuration();
        databaseConfiguration.configure();
        databaseConfiguration.setProperty("hibernate.connection.url", properties.getProperty("database.url"));
        databaseConfiguration.setProperty("hibernate.connection.username", properties.getProperty("database.user"));
        databaseConfiguration.setProperty("hibernate.connection.password", properties.getProperty("database.password"));
        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(databaseConfiguration.getProperties()).buildServiceRegistry();
        SessionFactory sessionFactory = databaseConfiguration.buildSessionFactory(serviceRegistry);
        hibernateSession = sessionFactory.openSession();

    }

    public void save(Object object) {
        Transaction transaction = hibernateSession.beginTransaction();
        hibernateSession.save(object);
        transaction.commit();
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

    public void saveObservationAndStations(Observation observation) {
        synchronized (sessionLockObject) {
            hibernateSession.save(observation);

            for (int i = 0; i < observation.getStations().size(); i++) {
                hibernateSession.save(observation.getStations().get(i));
            }
        }
    }

    public void saveForecastsAndPlaces(ArrayList<Forecast> forecasts) {
        Forecast forecast;
        List<Place> places;
        synchronized (sessionLockObject) {
            for (int i = 0; i < forecasts.size(); i++) {
                forecast = forecasts.get(i);
                hibernateSession.save(forecast);
                places = forecast.getPlaces();
                for (int j = 0; j < places.size(); j++) {
                    hibernateSession.save(places.get(j));
                }
            }
        }
    }

    public List querySQL(String queryString, String name, Object value) {
        synchronized (sessionLockObject) {
            Query query = hibernateSession.createSQLQuery(queryString);
            if (value != null) {
                query.setParameter(name, value);
            }
            List results = query.list();
            return results;
        }
    }

    public List querySQL(String queryString, ArrayList parameters) {
        synchronized (sessionLockObject) {
            Query query = hibernateSession.createSQLQuery(queryString);
            if (!parameters.isEmpty()) {
                for (int i = 0; i < parameters.size(); i++) {
                    query.setParameter(i, parameters.get(i));
                }
            }
            List results = query.list();
            return results;
        }
    }

    public List querySQL(String queryString) {
        synchronized (sessionLockObject) {
            Query query = hibernateSession.createSQLQuery(queryString);
            List results = query.list();
            return results;
        }
    }
}
