package forecastbuster.outgoing;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import forecastbuster.outgoing.entities.Forecast;
import forecastbuster.outgoing.entities.ForecastedDay;
import forecastbuster.outgoing.entities.Place;
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
        writeXMLFile(query.getForecastDays());
    }

    void writeXMLFile(TreeMap<Calendar, ForecastedDay> map) {

        try {
            File file = new File("D:\\Projekt", "Forecasts.xml");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fstream = new FileWriter(file.getAbsoluteFile());
            BufferedWriter out = new BufferedWriter(fstream);
            String xmlText = createXMLString(query);
            out.write(xmlText);
            log.info("Wrote XML to file " + file.getAbsoluteFile());
            out.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private String createXMLString(Query query) {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("Forecast", Forecast.class);
        xstream.alias("ForecastedDay", ForecastedDay.class);
        xstream.alias("Place", Place.class);
        xstream.addImplicitMap(Query.class, "forecastDays", ForecastedDay.class, "date");
        String xmlString = xstream.toXML(query);
        return xmlString;
    }
}

