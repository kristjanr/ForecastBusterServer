package forecastbuster.outgoing;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import forecastbuster.outgoing.entities.Forecast;
import forecastbuster.outgoing.entities.ForecastedDay;
import forecastbuster.outgoing.entities.Place;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.TreeMap;

public class Server {
    static org.slf4j.Logger log = LoggerFactory.getLogger(Server.class);
    Query query;

    public void startServer(Query query) {
        this.query = query;
        synchronized (query) {
            while (query.getForecastDays().isEmpty()) {
                try {
                    query.wait();
                } catch (InterruptedException e) {
                    log.error(ExceptionUtils.getStackTrace(e));
                }
            }
        }
        writeXMLFile(query.getForecastDays());
    }

    void writeXMLFile(TreeMap<Calendar, ForecastedDay> map) {
        log.info("Starting to write XML file...");
        String osName = System.getProperty("os.name");
        log.info("OS name is "+osName);
        File file;
        String filePath;
        try {
            if(osName.equalsIgnoreCase("Windows 7")){
                filePath = "D:\\Projekt";
                log.info("Putting the xml files into "+filePath);
                file = new File(filePath, "Forecasts.xml");
            }   else {
                filePath = "/var/www";
                log.info("Putting the xml files into "+filePath);
                file = new File(filePath, "Forecasts.xml");

            }
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
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    private String createXMLString(Query query) {
        XStream xstream = new XStream(new DomDriver());
        xstream.useAttributeFor(ForecastedDay.class, "dateString");
        xstream.useAttributeFor(Place.class, "name");
        xstream.alias("day", ForecastedDay.class);
        xstream.alias("place", Place.class);
        xstream.alias("weatherforecasthistory", Query.class);
        xstream.aliasField("date", ForecastedDay.class, "dateString");
        xstream.aliasField("oneB4", ForecastedDay.class, "forecasted1DayBefore");
        xstream.aliasField("twoB4", ForecastedDay.class, "forecasted2DaysBefore");
        xstream.aliasField("threeB4", ForecastedDay.class, "forecasted3DaysBefore");
        xstream.aliasField("fourB4", ForecastedDay.class, "forecasted4DaysBefore");
        xstream.aliasField("nphen", Forecast.class, "nightPhenomenon");
        xstream.aliasField("nmaxt", Forecast.class, "nightMaxTemp");
        xstream.aliasField("nmint", Forecast.class, "nightMinTemp");
        xstream.aliasField("dphen", Forecast.class, "dayPhenomenon");
        xstream.aliasField("dmaxt", Forecast.class, "dayMaxTemp");
        xstream.aliasField("dmint", Forecast.class, "dayMinTemp");
        xstream.aliasField("dmaxt", Place.class, "dayMaxTemp");
        xstream.aliasField("nmint", Place.class, "nightMinTemp");
        xstream.aliasField("nphen", Place.class, "nightPhenomenon");
        xstream.aliasField("dphen", Place.class, "dayPhenomenon");
        xstream.aliasField("", Forecast.class, "");
        xstream.omitField(ForecastedDay.class, "date");
        xstream.omitField(Forecast.class, "date");
        xstream.omitField(Place.class, "date");
        xstream.addImplicitMap(Query.class, "forecastDays", ForecastedDay.class, "date");
        String xmlString = xstream.toXML(query);
        return xmlString;
    }
}

