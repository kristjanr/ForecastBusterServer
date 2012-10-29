import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.TimerTask;

public class DataFetchTask extends TimerTask {
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

        Document observationDocument = XMLFetcher.getDocFromUrl(Main.OBSERVATION_URL);
        //Document observationDocument = XMLFetcher.getDocFromFile(Main.OBSERVATION_FILE);
        NodeList observationNodeList = observationDocument.getElementsByTagName(Main.KEY_OBSERVATIONS);
        Observation observation = new Observation();

        try {
            observation.createObservation(observationNodeList);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Main.getDatabaseAccessObject().saveObservationsAndSForecasts(observation, forecasts);
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
}