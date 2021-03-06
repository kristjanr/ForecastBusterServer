package forecastbuster.outgoing;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import forecastbuster.Main;
import forecastbuster.outgoing.entities.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Server {
    static org.slf4j.Logger log = LoggerFactory.getLogger(Server.class);

    public void parseForecastsToXML(Query query) throws IOException {
        XStream forecastFormat = XMLFormatForForecast();
        String xmlString = forecastFormat.toXML(query);
        writeFile(xmlString, Main.FORECAST_FILENAME_OUT);
    }

    public void parseObservationsToXML(Query query) throws IOException {
        XStream observationFormat = XMLFormatForObservation();
        String xmlString = observationFormat.toXML(query);
        writeFile(xmlString, Main.OBSERVATION_FILENAME_OUT);
    }

    private void writeFile(String xml, String filename) throws IOException {
        File file;
        String filePath;
        String osName = System.getProperty("os.name");
        log.info("OS name is " + osName);
        FileWriter fstream = null;
        try {
            if (osName.equalsIgnoreCase("Windows 7")) {
                filePath = "D:\\Projekt";
                log.info("Putting the " + filename + " file into " + filePath);
                file = new File(filePath, filename);
            } else {
                filePath = "/var/www";
                log.info("Putting the " + filename + " file into " + filePath);
                file = new File(filePath, filename);

            }
            if (!file.exists()) {
                boolean newFile = file.createNewFile();
            }
            fstream = new FileWriter(file.getAbsoluteFile());
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(xml);
        out.close();
    }

    private String createXMLString(XStream xstream, Query query) {
        String xmlString = xstream.toXML(query);
        return xmlString;
    }


    private XStream XMLFormatForForecast() {
        XStream xstream = new XStream(new DomDriver());
        xstream.addImplicitMap(Query.class, "forecastDays", ForecastedDay.class, "date");
        xstream.omitField(Query.class, "observations");
        xstream.useAttributeFor(ForecastedDay.class, "dateString");
        xstream.useAttributeFor(Place.class, "name");
        xstream.alias("day", ForecastedDay.class);
        xstream.alias("place", Place.class);
        xstream.alias("forecasts", Query.class);
        xstream.aliasField("date", ForecastedDay.class, "dateString");
        xstream.aliasField("oneB4", ForecastedDay.class, "forecasted1DayBefore");
        xstream.aliasField("twoB4", ForecastedDay.class, "forecasted2DaysBefore");
        xstream.aliasField("threeB4", ForecastedDay.class, "forecasted3DaysBefore");
        xstream.aliasField("fourB4", ForecastedDay.class, "forecasted4DaysBefore");
        xstream.aliasField("np", Forecast.class, "nightPhenomenon");
        xstream.aliasField("nmaxt", Forecast.class, "nightMaxTemp");
        xstream.aliasField("nmint", Forecast.class, "nightMinTemp");
        xstream.aliasField("dp", Forecast.class, "dayPhenomenon");
        xstream.aliasField("dmaxt", Forecast.class, "dayMaxTemp");
        xstream.aliasField("dmint", Forecast.class, "dayMinTemp");
        xstream.aliasField("dmaxt", Place.class, "dayMaxTemp");
        xstream.aliasField("nmint", Place.class, "nightMinTemp");
        xstream.aliasField("np", Place.class, "nightPhenomenon");
        xstream.aliasField("dp", Place.class, "dayPhenomenon");
        xstream.aliasField("", Forecast.class, "");
        xstream.omitField(ForecastedDay.class, "date");
        xstream.omitField(Forecast.class, "date");
        xstream.omitField(Place.class, "date");
        return xstream;
    }

    private XStream XMLFormatForObservation() {
        XStream xstream = new XStream(new DomDriver());
        xstream.addImplicitMap(Query.class, "observations", Observation.class, "date");
        xstream.omitField(Query.class,"forecastDays");
        xstream.useAttributeFor(Observation.class, "dateString");
        xstream.aliasField("date", Observation.class, "dateString");
        xstream.useAttributeFor(Station.class,"name");
        xstream.alias("day", Observation.class);
        xstream.alias("station", Station.class);
        xstream.alias("observations", Query.class);
        xstream.omitField(Observation.class,"date");
        xstream.aliasField("dt", Station.class, "dayTemp");
        xstream.aliasField("nt", Station.class, "nightTemp");
        xstream.aliasField("np", Station.class, "nightPhenomenon");
        xstream.aliasField("dp", Station.class, "dayPhenomenon");
        return xstream;
    }
}

