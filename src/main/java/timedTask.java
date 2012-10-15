import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.ParseException;
import java.util.TimerTask;

public class TimedTask extends TimerTask {
    static Logger log = LoggerFactory.getLogger(XMLParser.class);

    public void run() {
        getDatabaseAccessObject().initSession();

        Document forecastDocument = XMLParser.getDocFromUrl(Main.FORECAST_URL);
        //Document forecastDocument = XMLParser.getDocFromFile(Main.FORECAST_FILE);
        NodeList forecastNodeList = forecastDocument.getElementsByTagName(Main.KEY_FORECAST);
        try {
            iterateForecasts(forecastNodeList);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        saveForecastsAndPlaces();

        Document observationDocument = XMLParser.getDocFromUrl(Main.FORECAST_URL);
        //Document observationDocument = XMLParser.getDocFromFile(Main.OBSERVATION_FILE);
        NodeList observationNodeList = observationDocument.getElementsByTagName(Main.KEY_OBSERVATIONS);
        Observation observation = new Observation();
        try {
            observation.createObservation(observationNodeList);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        saveObservationAndStations(observation);
    }

    static void iterateForecasts(NodeList nodeList) throws ParseException {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            NodeList secondNodeList = element.getChildNodes();
            for (int j = 0; j < secondNodeList.getLength(); j++) {
                Node daytimeNode = secondNodeList.item(j);
                Forecast.createForecast(element, daytimeNode);
            }
        }
    }

    static void saveForecastsAndPlaces() {
        for (int i = 0; i < Main.forecasts.size(); i++) {
            getDatabaseAccessObject().save(Main.forecasts.get(i));
            for (int j = 0; j < Main.forecasts.get(i).getPlaces().size(); j++) {
                getDatabaseAccessObject().save(Main.forecasts.get(i).getPlaces().get(j));
            }
        }
    }

    private static void saveObservationAndStations(Observation observation) {
        getDatabaseAccessObject().save(observation);
        for (int i = 0; i < observation.getStations().size(); i++) {
            getDatabaseAccessObject().save(observation.getStations().get(i));
        }
    }

    static DatabaseAccessObject getDatabaseAccessObject() {
        if (Main.databaseAccessObject == null) {
            Main.databaseAccessObject = new DatabaseAccessObject();
        }
        return Main.databaseAccessObject;
    }
}