import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import java.util.Properties;

public class DatabaseAccessObject {
    private Session hibernateSession;

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
}
