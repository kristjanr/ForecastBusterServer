package forecastbuster.incoming;

import forecastbuster.Main;
import forecastbuster.incoming.entities.Forecast;
import forecastbuster.incoming.entities.Observation;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.ParseException;
import java.util.ArrayList;

public class FetchTask implements Runnable {
    static org.slf4j.Logger log = LoggerFactory.getLogger(FetchTask.class);

    @Override
    public void run() {
        synchronized (Main.actionLockObject) {
            Document forecastDocument = XMLFetcher.getDocFromUrl(Main.FORECAST_URL);
            NodeList forecastNodeList = forecastDocument.getElementsByTagName(Main.KEY_FORECAST);
            ArrayList<Forecast> forecasts = new ArrayList<>(8);
            try {
                iterateForecasts(forecastNodeList, forecasts);
            } catch (ParseException e) {
                log.error("Error while creating forecast objects from xml nodelist", e);
            }

            Document observationDocument = XMLFetcher.getDocFromUrl(Main.OBSERVATION_URL);
            NodeList observationNodeList = observationDocument.getElementsByTagName(Main.KEY_OBSERVATIONS);
            Observation observation = new Observation();

            try {
                observation.createObservation(observationNodeList);
            } catch (ParseException e) {
                log.error("Error while creating observation objects from xml nodelist" ,e);
            }
            Main.getDatabaseAccessObject().saveObservationsAndSForecasts(observation, forecasts);
            Main.actionLockObject.notify();
        }
    }

    private void iterateForecasts(NodeList nodeList, ArrayList<Forecast> forecasts) throws ParseException {
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
}