package forecastbuster.incoming;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import forecastbuster.Constants;
import forecastbuster.incoming.entities.Forecast;
import forecastbuster.incoming.entities.Observation;
import forecastbuster.incoming.entities.Place;
import forecastbuster.incoming.entities.Station;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FetchTask {
    static Logger log = Logger.getLogger(FetchTask.class.getName());

    public void start() {
        ArrayList<Forecast> forecasts = getForecasts();
        if (forecasts == null) return;
        saveForecasts(forecasts);

        Observation observation = getObservation();
        if (observation == null) return;
        saveObservation(observation);
    }

    private Observation getObservation() {
        Document observationDocument = XMLDownloadAndParse.getDocFromUrl(Constants.OBSERVATION_URL);
        if (observationDocument == null) {
            return null;
        }
        NodeList observationNodeList = observationDocument.getElementsByTagName(Constants.OBSERVATIONS);
        Observation observation = new Observation();

        try {
            observation.createObservation(observationNodeList);
        } catch (ParseException e) {
            log.severe("Error while creating observation objects from xml nodelist" + e);
        }
        return observation;
    }

    private ArrayList<Forecast> getForecasts() {
        ArrayList<Forecast> forecasts = new ArrayList<>(8);
        Document forecastDocument = XMLDownloadAndParse.getDocFromUrl(Constants.FORECAST_URL);
        if (forecastDocument == null) {
            return null;
        }
        NodeList forecastNodeList = forecastDocument.getElementsByTagName(Constants.FORECAST);
        try {
            iterateForecasts(forecastNodeList, forecasts);
        } catch (ParseException e) {
            log.severe("Error while creating forecast objects from xml nodelist" + e);
        }
        return forecasts;
    }

    private void saveForecasts(ArrayList<Forecast> forecasts) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        List<Entity> forecastEntities = new ArrayList<>();
        List<Entity> placesEntities = new ArrayList<>();
        for (Forecast forecast : forecasts) {
            Entity f = new Entity(Constants.FORECAST);
            f.setProperty(Constants.DATE, forecast.getDate());
            f.setProperty(Constants.TIMEOFDAY, forecast.getTimeOfDay());
            f.setProperty(Constants.TIMEOFUDPATE, forecast.getTimeOfUpdate());
            f.setProperty(Constants.PHENOMENON, forecast.getPhenomenon());
            f.setProperty(Constants.TEMPMIN, forecast.getTempMin());
            f.setProperty(Constants.TEMPMAX, forecast.getTempMax());
            f.setProperty(Constants.TEXT, forecast.getText());
            Key key = f.getKey();
            forecastEntities.add(f);
            for (Place place : forecast.getPlaces()) {
                Entity p = new Entity(Constants.PLACE, key);
                p.setProperty(Constants.NAME, place.getName());
                p.setProperty(Constants.PHENOMENON, place.getPhenomenon());
                p.setProperty(Constants.TEMPMIN, place.getTempMin());
                p.setProperty(Constants.TEMPMAX, place.getTempMax());
                placesEntities.add(p);
            }
        }
        datastore.put(forecastEntities);
        datastore.put(placesEntities);
    }

    private void saveObservation(Observation observation) {
        Entity o = new Entity(Constants.OBSERVATIONS);
        o.setProperty(Constants.TIMESTAMP, observation.getTimestamp());
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        List<Entity> stationEntities = new ArrayList<>();
        Key key = o.getKey();
        for (Station station : observation.getStations()) {
            Entity s = new Entity(Constants.STATION, key);
            s.setProperty(Constants.NAME, station.getName());
            s.setProperty(Constants.PHENOMENON, station.getPhenomenon());
            DecimalFormat decimalFormat = new DecimalFormat("#.#");
            s.setProperty(Constants.AIRTEMPERATURE, decimalFormat.format(station.getAirTemperature()));
            stationEntities.add(s);
        }
        datastore.put(o);
        datastore.put(stationEntities);
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