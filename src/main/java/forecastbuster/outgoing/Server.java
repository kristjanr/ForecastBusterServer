package forecastbuster.outgoing;

import forecastbuster.outgoing.entities.ForecastedDay;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.TreeMap;

public class Server {
    static org.slf4j.Logger log = LoggerFactory.getLogger(Server.class);
    Query query;

    public Server() {
    }

    public void startServer(Query query) {
        this.query = query;
        synchronized (query) {
            while (query.getForecastDays().isEmpty()) {
                try {
                    query.wait();
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
            }
        }
        createXMLFileFromList(query.getForecastDays());

    }

    void createXMLFileFromList(TreeMap<Calendar, ForecastedDay> map) {

        try {
            File file = new File("D:\\Projekt", "Forecasts.xml");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fstream = new FileWriter(file.getAbsoluteFile());
            BufferedWriter out = new BufferedWriter(fstream);
            String xmlText = XMLParse.getTestXMLText();
            out.write(xmlText);
            log.debug("Wrote " + xmlText + " to file " + file.getAbsoluteFile());
            out.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
