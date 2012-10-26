import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

public class DataFetchTask extends TimerTask {
    static Logger log = LoggerFactory.getLogger(DataFetchTask.class);


    public void run() {

        Document forecastDocument = XMLFetcher.getDocFromUrl(Main.FORECAST_URL);
        //Document forecastDocument = XMLFetcher.getDocFromFile(Main.FORECAST_FILE);

        NodeList forecastNodeList = forecastDocument.getElementsByTagName(Main.KEY_FORECAST);
        ArrayList<Forecast> forecasts = new ArrayList(8);
        try {
            iterateForecasts(forecastNodeList, forecasts);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        saveForecastsAndPlaces(forecasts);

        Document observationDocument = XMLFetcher.getDocFromUrl(Main.OBSERVATION_URL);
        //Document observationDocument = XMLFetcher.getDocFromFile(Main.OBSERVATION_FILE);
        NodeList observationNodeList = observationDocument.getElementsByTagName(Main.KEY_OBSERVATIONS);
        Observation observation = new Observation();

        try {
            observation.createObservation(observationNodeList);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        saveObservationAndStations(observation);
    }

    void iterateForecasts(NodeList nodeList, ArrayList<Forecast> forecasts) throws ParseException {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);
            NodeList secondNodeList = element.getChildNodes();
            for (int j = 0; j < secondNodeList.getLength(); j++) {
                Node daytimeNode = secondNodeList.item(j);
                Forecast forecast = new Forecast();
                forecast.createForecast(element, daytimeNode);
                if (forecast.getPhenomenon() != null) {
                    forecasts.add(forecast);
                }
            }
        }
    }

    void saveForecastsAndPlaces(ArrayList<Forecast> forecasts) {
        long begin = new Date().getTime();
        Forecast forecast;
        List<Place> places;
        for (int i = 0; i < forecasts.size(); i++) {
            forecast = forecasts.get(i);
            Main.getDatabaseAccessObject().save(forecast);
            places = forecast.getPlaces();
            for (int j = 0; j < places.size(); j++) {
                Main.getDatabaseAccessObject().save(places.get(j));
            }
        }
        long end = new Date().getTime();
        int timeForSavingForecasts = (int) (end - begin);
        // Why do the saving times get longer?
        log.info("Saved forecasts in " + timeForSavingForecasts);
    }

    void saveObservationAndStations(Observation observation) {
        long begin = new Date().getTime();
        Main.getDatabaseAccessObject().save(observation);
        for (int i = 0; i < observation.getStations().size(); i++) {
            Main.getDatabaseAccessObject().save(observation.getStations().get(i));
        }
        long end = new Date().getTime();
        int timeForSavingObservation = (int) (end - begin);
        // Why do the saving times get longer?
        log.info("Saved observation in " + timeForSavingObservation);
    }
}