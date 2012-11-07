package forecastbuster.outgoing;

import org.slf4j.LoggerFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class Server {
    static org.slf4j.Logger log = LoggerFactory.getLogger(Server.class);
    Query query;

    public Server() {
    }

    public void startServer(Query query) {
        this.query = query;
        createFile();

    }

    void createFile() {
        synchronized (query) {
            while (query.getForecastsForTomorrow().isEmpty()) {
                try {
                    query.wait();
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
            }
        }

        List list = query.getForecastsForTomorrow();
        try {
            File file = new File("C:\\apache-tomcat-6.0.35\\webapps\\ROOT", "Forecasts.csv");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fstream = new FileWriter(file.getAbsoluteFile());
            BufferedWriter out = new BufferedWriter(fstream);
            // TO DO
            out.write(list.toString());
            log.debug("Wrote " + list.toString() + " to file " + file.getAbsoluteFile());
            out.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
