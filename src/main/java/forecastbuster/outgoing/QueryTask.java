package forecastbuster.outgoing;

import forecastbuster.DatabaseAccessObject;
import forecastbuster.outgoing.entities.Forecast;
import forecastbuster.outgoing.entities.ForecastedDay;
import forecastbuster.outgoing.entities.Place;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.*;

public class QueryTask extends TimerTask {
    Query query;
    DatabaseAccessObject DAO;
    Calendar earliestDate;
    Calendar latestDate;
    ArrayList fourDayForecastQueries;
    static org.slf4j.Logger log = LoggerFactory.getLogger(QueryTask.class);

    public QueryTask(Query query) {
        this.query = query;
        DAO = query.getDatabaseAccessObject();
        fourDayForecastQueries = new ArrayList();
        earliestDate = Calendar.getInstance();
        latestDate = Calendar.getInstance();
        latestDate.setTimeInMillis(0);
    }

    @Override
    public void run() {
        TreeMap<Calendar, ForecastedDay> forecastDays = createForecastedDaysFromQueries();
        synchronized (query) {
            query.setForecastDays(forecastDays);
            log.info("Created entities from queries. The size of the TreeMap is " + forecastDays.size());
            query.notify();
        }
    }

    private TreeMap<Calendar, ForecastedDay> createForecastedDaysFromQueries() {
        doQueryAndPutForecastMapsToList();
        TreeMap<Calendar, ArrayList<Place>> places = createMapOfPlaceLists();
        return createForecastedDays(places);
    }

    void doQueryAndPutForecastMapsToList() {

        for (int i = 0; i < 4; i++) {
            List forecastsForGivenDayAfterTomorrow = queryForecastsForGivenDayAfterToday(i);
            log.info("I have forecasts query from database in the list. It is the "+(i+1)+"-st query. The size of the list is "+forecastsForGivenDayAfterTomorrow.size());
            TreeMap<Calendar, Forecast> forecastsFromQuery = createForecasts(forecastsForGivenDayAfterTomorrow);
            if (!forecastsFromQuery.isEmpty()) {
                fourDayForecastQueries.add(forecastsFromQuery);
                if (forecastsFromQuery.firstKey().before(earliestDate)) {
                    earliestDate = (Calendar) forecastsFromQuery.firstKey().clone();
                }
                if (forecastsFromQuery.lastKey().after(latestDate)) {
                    latestDate = (Calendar) forecastsFromQuery.lastKey().clone();
                }
            }
        }
    }

    TreeMap<Calendar, Forecast> createForecasts(List list) {

        TreeMap<Calendar, Forecast> forecastTreeMap = new TreeMap<Calendar, Forecast>(new MyDateComparator());
        log.info("Starting while cycle inf createForecasts.");
        while (!list.isEmpty()) {
            Forecast forecast = new Forecast();
            Object[] forecastData = (Object[]) list.remove(0);
            forecast.createForecast(forecastData);
            forecastTreeMap.put((Calendar) forecast.getDate().clone(), forecast);
            log.info("I have put a forecast with the date "+forecast.getDate().getTime()+ " in a TreeMap");
        }
        log.info("Ended while cycle.");
        return forecastTreeMap;
    }

    TreeMap<Calendar, ArrayList<Place>> createMapOfPlaceLists() {
        List placesDataFromQueries = getPlacesForecastsForTomorrow();
        log.info("I have places query from database in the list. The size is "+placesDataFromQueries.size());
        ArrayList<Place> places;
        places = getPlacesFromObjectArrays(placesDataFromQueries);

        TreeMap<Calendar, ArrayList<Place>> placesListsMap = new TreeMap<Calendar, ArrayList<Place>>(new MyDateComparator());

        for (Calendar currentDate = (Calendar) earliestDate.clone(); latestDate.compareTo(currentDate) >= 0; currentDate.add(Calendar.DAY_OF_MONTH, 1)) {
            ArrayList<Place> placesWithCurrentDate = getAllPlacesWithSameDate(currentDate, places);
            if (!placesWithCurrentDate.isEmpty()) {
                placesListsMap.put((Calendar) currentDate.clone(), placesWithCurrentDate);
            }
        }

        return placesListsMap;
    }

    private ArrayList<Place> getPlacesFromObjectArrays(List placesDataFromQueries) {
        ArrayList<Place> places = new ArrayList();
        Iterator iterator = placesDataFromQueries.iterator();
        while (iterator.hasNext()) {
            Place place = new Place();
            place.createPlace((Object[]) iterator.next());
            places.add(place);
        }
        return places;
    }

    private ArrayList<Place> getAllPlacesWithSameDate(Calendar cal, ArrayList<Place> places) {
        Iterator iterator = places.iterator();
        ArrayList<Place> placesWithTheSameDate = new ArrayList<Place>();
        while (iterator.hasNext()) {
            Place tempPlace = (Place) iterator.next();
            Calendar tempDate = tempPlace.getDate();
            if (cal.compareTo(tempDate) == 0) {
                placesWithTheSameDate.add(tempPlace);
            }
        }
        return placesWithTheSameDate;
    }

    TreeMap<Calendar, ForecastedDay> createForecastedDays(TreeMap<Calendar, ArrayList<Place>> places) {
        TreeMap<Calendar, ForecastedDay> forecastedDays = new TreeMap<Calendar, ForecastedDay>(new MyDateComparator());
        for (Calendar currentDate = (Calendar) earliestDate.clone(); latestDate.compareTo(currentDate) >= 0; currentDate.add(Calendar.DAY_OF_MONTH, 1)) {
            ForecastedDay forecastedDay = new ForecastedDay((Calendar) currentDate.clone());

            // iterate through the fourDayForecastQueries of 4 forecasts for one day and add the forecast to the
            // forecastedDay with the right date and to the right place - setForecastedDaysBefore()
            for (int j = 0; j < fourDayForecastQueries.size(); j++) {
                TreeMap<Calendar, Forecast> forecasts = (TreeMap<Calendar, Forecast>) fourDayForecastQueries.get(j);
                if (forecasts.containsKey(currentDate)) {

                    Forecast forecast = forecasts.get(currentDate);

                    if (j == 0) {
                        ArrayList<Place> forecastPlaces = places.get(currentDate);
                        forecast.setPlaces(forecastPlaces);
                    }
                    // add forecast to the forecasted day
                    forecastedDay.setForecastedDay(j, forecast);
                }
            }
            //put forecasted day to the map
            if (!forecastedDay.isEmpty()) {
                forecastedDays.put(forecastedDay.getDate(), forecastedDay);
            }
        }
        return forecastedDays;
    }

    /**
     * This is the query for PlacesForecasts. In case the forecasts about a place for a certain date change during this one day,
     * the most frequent phenomenon is taken and the average of all different forecast temperatures is calculated.
     *
     * @return The list of objects arrays (Object[]) containing data of places forecasts with all possible days. There are 6 places for each day. The forecasts for places are made only 1 day before teh subject date.     *
     */
    List getPlacesForecastsForTomorrow() {
        String queryString = getSqlString("place.sql");
        List queryList = new ArrayList();
        try {
            queryList = DAO.querySQL(queryString);
        } catch (Exception e) {
            log.error(e.toString());
        }
        return queryList;
    }

    /**
     * This is the query for all forecasts.
     *
     * @param daysAfterToday the number of days before the forecasted day.
     * @return The list of objects arrays (Object[]) containing data of forecasts with all possible days which were made on the given day before. There are one forecast for each day.
     */
    List queryForecastsForGivenDayAfterToday(int daysAfterToday) {
        String queryString = getSqlString("forecast.sql");
        List queryList = new ArrayList();
        try {
            queryList = DAO.querySQL(queryString, "daysAfterToday", daysAfterToday);
        } catch (Exception e) {
            log.error(e.toString());
        }
        return queryList;
    }

    private String getSqlString(String sqlFileName) {
        InputStream inputStream = this.getClass().getResourceAsStream("/" + sqlFileName);
        String theString = convertStreamToString(inputStream);
        return theString;
    }

    public static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}