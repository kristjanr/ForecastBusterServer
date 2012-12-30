package forecastbuster.outgoing;

import forecastbuster.DatabaseAccessObject;
import forecastbuster.Main;
import forecastbuster.outgoing.entities.Station;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.LoggerFactory;
import forecastbuster.outgoing.entities.Observation;

import java.io.IOException;
import java.util.*;

public class QueryTaskObservation extends TimerTask {
    Query query;
    DatabaseAccessObject DAO;
    static org.slf4j.Logger log = LoggerFactory.getLogger(QueryTaskObservation.class);
    Server server;

    public QueryTaskObservation(Query query) {
        this.query = query;
        DAO = Main.getDatabaseAccessObject();
        server = new Server();
    }

    @Override
    public void run() {
        List dayQueryList;
        dayQueryList = queryObservationsForDay();
        List nighQueryList;
        nighQueryList = queryObservationsForNight();

        TreeMap<Calendar, ArrayList<TempStation>> dayTempStations;
        dayTempStations = createTempStations(dayQueryList);

        TreeMap<Calendar, ArrayList<TempStation>> nightTempStations;
        nightTempStations = createTempStations(nighQueryList);
        if (!nightTempStations.isEmpty() && !dayTempStations.isEmpty()) {
            TreeMap<Calendar, Observation> observations;
            synchronized (Main.observationQueryLockObject) {
                observations = createObservations(nightTempStations, dayTempStations);
                log.info("Created observation entities from queries. The size of the TreeMap is " + observations.size());
                query.setObservations(observations);
                Main.observationQueryLockObject.notify();
                try {
                    server.parseObservationsToXML(query);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private TreeMap<Calendar, Observation> createObservations(TreeMap<Calendar, ArrayList<TempStation>> nightTempStations, TreeMap<Calendar, ArrayList<TempStation>> dayTempStations) {
        Calendar earliestDate = findEarliest(nightTempStations, dayTempStations);
        Calendar latestDate = findLatest(nightTempStations, dayTempStations);
        TreeMap<Calendar, Observation> observations = new TreeMap<Calendar, Observation>();
        ArrayList<Station> stations;
        for (Calendar currentDate = (Calendar) earliestDate.clone(); latestDate.compareTo(currentDate) >= 0; currentDate.add(Calendar.DAY_OF_MONTH, 1)) {
            stations = createStationsWithOneDate(nightTempStations.get(currentDate), dayTempStations.get(currentDate));
            observations.put((Calendar) currentDate.clone(), new Observation((Calendar) currentDate.clone(), stations));
        }
        return observations;
    }

    private ArrayList<Station> createStationsWithOneDate(ArrayList<TempStation> nightTempStations, ArrayList<TempStation> dayTempStations) {
        ArrayList<Station> stations = new ArrayList<Station>();
        int dayPhenCount = 0;
        if (nightTempStations != null && dayTempStations != null){
        for (TempStation dayTempStation : dayTempStations) {
            Station station = new Station();
            station.setName(dayTempStation.getName());
            station.setNightPhenomenon(dayTempStation.getPhenomenon());
            station.setNightTemp(dayTempStation.getTemp());
            for (TempStation nightTempStation : nightTempStations) {
                if (dayTempStation.getName().equals(nightTempStation.getName())) {
                    station.setDayPhenomenon(nightTempStation.getPhenomenon());
                    station.setDayTemp(nightTempStation.getTemp());
                }
            }
            stations.add(station);
        }
        return stations;
        }
        else {
            log.debug("could not create station list with because either day or night station lists (or both) are empty");
            return null;
        }
    }

    private Calendar findLatest(TreeMap<Calendar, ArrayList<TempStation>> nightTempStations, TreeMap<Calendar, ArrayList<TempStation>> dayTempStations) {
        Calendar latestDate = (Calendar) nightTempStations.lastKey().clone();
        Calendar tempDate;
        tempDate = dayTempStations.lastKey();
        if (latestDate.after(tempDate)) {
            latestDate = (Calendar) tempDate.clone();
        }
        return latestDate;
    }

    private Calendar findEarliest(TreeMap<Calendar, ArrayList<TempStation>> nightTempStations, TreeMap<Calendar, ArrayList<TempStation>> dayTempStations) {
        Calendar earliestDate = Calendar.getInstance();
        Calendar tempDate = nightTempStations.firstKey();
        if (earliestDate.after(tempDate)) {
            earliestDate = (Calendar) tempDate.clone();
        }
        tempDate = dayTempStations.lastKey();
        if (earliestDate.after(tempDate)) {
            earliestDate = (Calendar) tempDate.clone();
        }
        return earliestDate;
    }

    private TreeMap<Calendar, ArrayList<TempStation>> createTempStations(List list) {
        TreeMap<Calendar, ArrayList<TempStation>> tempStationsMap = new TreeMap<Calendar, ArrayList<TempStation>>();
        Calendar lastDate = null;
        ArrayList<TempStation> tempStationsList = null;
        TempStation tempStation;

        for (Object aList : list) {
            tempStation = new TempStation();
            tempStation.createTempStation((Object[]) aList);
            if (lastDate == null) {
                tempStationsList = new ArrayList<TempStation>();
                tempStationsList.add(tempStation);
                tempStationsMap.put((Calendar) tempStation.getDate().clone(), tempStationsList);
            } else if (lastDate.compareTo(tempStation.getDate()) == 0 && tempStationsMap.get(lastDate) == null) {
                tempStationsList.add(tempStation);
                tempStationsMap.put((Calendar) tempStation.getDate().clone(), tempStationsList);

            } else if (lastDate.compareTo(tempStation.getDate()) != 0 && tempStationsMap.get(lastDate) == null) {
                tempStationsList = new ArrayList<TempStation>();
                tempStationsList.add(tempStation);
                tempStationsMap.put((Calendar) tempStation.getDate().clone(), tempStationsList);
            } else if (tempStationsMap.get(lastDate) != null) {
                tempStationsMap.get(lastDate).add(tempStation);
            }
            lastDate = (Calendar) tempStation.getDate().clone();
        }
        return tempStationsMap;
    }


    List queryObservationsForDay() {
        String queryString = query.getSqlString("observation_day.sql");
        return queryObservations(queryString);
    }

    List queryObservationsForNight() {
        String queryString = query.getSqlString("observation_night.sql");
        return queryObservations(queryString);
    }

    private List queryObservations(String queryString) {
        List queryList = new ArrayList();
        try {
            queryList = DAO.querySQL(queryString);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return queryList;
    }
}
