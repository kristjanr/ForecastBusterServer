import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class DatabaseAccessObject {
    private Session hibernateSession;
    static org.slf4j.Logger log = LoggerFactory.getLogger(DatabaseAccessObject.class);

    public DatabaseAccessObject() {
    }

    public void initSession() {
        Properties properties = new Properties();
        /*
        try {
            URL pUrl = this.getClass().getResource("config/technical/database.properties");
            properties.load(pUrl.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        Configuration databaseConfiguration = new Configuration();
        databaseConfiguration.configure();
        databaseConfiguration.setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/postgres");
        databaseConfiguration.setProperty("hibernate.connection.username", "postgres");
        databaseConfiguration.setProperty("hibernate.connection.password", "postgres");
        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(databaseConfiguration.getProperties()).buildServiceRegistry();
        SessionFactory sessionFactory = databaseConfiguration.buildSessionFactory(serviceRegistry);
        hibernateSession = sessionFactory.openSession();

    }

    public void save(Object object) {
        Transaction transaction = hibernateSession.beginTransaction();
        hibernateSession.save(object);
        transaction.commit();
    }

    void saveObservationsAndSForecasts(Observation observation, ArrayList<Forecast> forecasts) {
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
        hibernateSession.save(observation);
        for (int i = 0; i < observation.getStations().size(); i++) {
            hibernateSession.save(observation.getStations().get(i));
        }
    }

    void saveForecastsAndPlaces(ArrayList<Forecast> forecasts) {
        Forecast forecast;
        List<Place> places;
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
