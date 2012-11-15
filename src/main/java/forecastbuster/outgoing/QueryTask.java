package forecastbuster.outgoing;

import forecastbuster.DatabaseAccessObject;
import forecastbuster.outgoing.entities.Forecast;
import forecastbuster.outgoing.entities.ForecastedDay;
import forecastbuster.outgoing.entities.Place;
import org.slf4j.LoggerFactory;

import java.util.*;

public class QueryTask extends TimerTask {
    Query query;
    DatabaseAccessObject DAO;
    // the date of the earliest forecast there is
    Calendar earliestDate;
    // the date of the latest forecast there is
    Calendar latestDate;
    ArrayList fourDayForecastQueries;
    static org.slf4j.Logger log = LoggerFactory.getLogger(QueryTask.class);

    public QueryTask(Query query) {
        this.query = query;
        DAO = query.getDatabaseAccessObject();
    }

    @Override
    public void run() {
        TreeMap<Calendar, ForecastedDay> forecastDays = createForecastedDaysFromQueries();
        synchronized (query) {
            query.setForecastDays(forecastDays);
            log.info("Created entities from queries. The size of the TreeMap is "+ forecastDays.size());
            query.notify();
        }
    }

    private TreeMap<Calendar, ForecastedDay> createForecastedDaysFromQueries() {

        fourDayForecastQueries = new ArrayList();
        earliestDate = Calendar.getInstance();
        latestDate = Calendar.getInstance();
        latestDate.set(0, 0, 1);
        populateFourForecastQueriesListAndFindDates();
        TreeMap<Calendar, ArrayList<Place>> places = createPlacesLists();
        TreeMap<Calendar, ForecastedDay> forecastedDayTreeMap = createForecastedDaysBetweenGivenDates(places);
        return forecastedDayTreeMap;
    }

    private void populateFourForecastQueriesListAndFindDates() {

        for (int i = 0; i < 4; i++) {
            TreeMap<Calendar, Forecast> forecastsFromQuery = createForecasts(getForecastsForGivenDayAfterTomorrow(i));
            if (!forecastsFromQuery.isEmpty()) {

                fourDayForecastQueries.add(forecastsFromQuery);

                Calendar forecastDate = forecastsFromQuery.pollFirstEntry().getKey();
                if (forecastDate.before(earliestDate)) {
                    earliestDate = forecastDate;
                }
                if (forecastDate.after(latestDate)) {
                    latestDate = forecastDate;
                }
            }
        }
    }

    private TreeMap<Calendar, Forecast> createForecasts(List list) {

        TreeMap<Calendar, Forecast> forecastTreeMap = new TreeMap<Calendar, Forecast>(new MyDateComparator());
        while (!list.isEmpty()) {
            Forecast forecast = new Forecast();
            Object[] forecastData = (Object[]) list.remove(0);
            forecast.createForecast(forecastData);
            forecastTreeMap.put((Calendar) forecast.getDate().clone(), forecast);
        }
        return forecastTreeMap;
    }

    private TreeMap<Calendar, ArrayList<Place>> createPlacesLists() {
        List placesDataFromQueries = getPlacesForecastsForTomorrow();

        ArrayList<Place> places;
        places = getPlacesList(placesDataFromQueries);

        TreeMap<Calendar, ArrayList<Place>> placesListsMap = new TreeMap<Calendar, ArrayList<Place>>(new MyDateComparator());

        for (Calendar currentDate = (Calendar) earliestDate.clone(); latestDate.compareTo(currentDate) >= 0; currentDate.add(Calendar.DAY_OF_MONTH, 1)) {
            ArrayList<Place> placesWithCurrentDate = getAllPlacesWithSameDate(currentDate, places);
            if (!placesWithCurrentDate.isEmpty()) {
                placesListsMap.put((Calendar) currentDate.clone(), placesWithCurrentDate);
            }
        }

        return placesListsMap;
    }

    private ArrayList<Place> getPlacesList(List placesDataFromQueries) {
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

    private TreeMap<Calendar, ForecastedDay> createForecastedDaysBetweenGivenDates(TreeMap<Calendar, ArrayList<Place>> places) {
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
    private List getPlacesForecastsForTomorrow() {
        String queryString =
                "SELECT dp.fdate as fdate, dp.pname as pname, np.pphen AS nightphen, nt.avg AS nightmintemp, dp.pphen AS dayphen, dt.avg AS daymaxtemp " +
                        "    FROM  " +
                        "    (SELECT sub1.date fdate, sub1.name pname, sub1.phenomenon pphen " +
                        "        FROM ( " +
                        "        SELECT date, name, place.phenomenon, count(*) as cnnt " +
                        "            FROM forecast, place  " +
                        "            WHERE place.forecastid = forecast.forecastid AND forecast.timeofday = 'day' " +
                        "            GROUP BY date, name, place.phenomenon                 " +
                        "        ) AS sub1 " +
                        "        GROUP BY sub1.date, sub1.name, pphen, sub1.cnnt " +
                        "        HAVING    (sub1.date, sub1.name, sub1.cnnt) IN ( " +
                        "        SELECT sub2.date, sub2.name, max(cnt) " +
                        "            FROM ( " +
                        "            SELECT f2.date, p2.name, p2.phenomenon AS pphen, count(*) AS cnt " +
                        "                FROM forecast f2, place p2 " +
                        "                WHERE p2.forecastid = f2.forecastid AND f2.timeofday = 'day' " +
                        "                GROUP BY p2.name, f2.date, pphen                 " +
                        "            ) AS sub2 " +
                        "        GROUP BY date, name " +
                        "        HAVING sub1.date=date AND sub1.name=name " +
                        "        ) " +
                        "    ) dp,     " +
                        "    ( SELECT sub1.date fdate, sub1.name pname, sub1.phenomenon pphen " +
                        "        FROM ( " +
                        "        SELECT date, name, place.phenomenon, count(*) as cnnt " +
                        "            FROM forecast, place  " +
                        "            WHERE place.forecastid = forecast.forecastid AND forecast.timeofday = 'night' " +
                        "            GROUP BY date, name, place.phenomenon                 " +
                        "        ) AS sub1 " +
                        "    GROUP BY sub1.date, sub1.name, pphen, sub1.cnnt " +
                        "    HAVING    (sub1.date, sub1.name, sub1.cnnt) IN ( " +
                        "        SELECT sub2.date, sub2.name, max(cnt) " +
                        "            FROM ( " +
                        "            SELECT f2.date, p2.name, p2.phenomenon AS pphen, count(*) AS cnt " +
                        "                FROM forecast f2, place p2 " +
                        "                WHERE p2.forecastid = f2.forecastid AND f2.timeofday = 'night' " +
                        "                GROUP BY p2.name, f2.date, pphen                 " +
                        "            ) AS sub2 " +
                        "        GROUP BY date, name " +
                        "        HAVING sub1.date=date AND sub1.name=name " +
                        "        ) " +
                        "    ) np,     " +
                        "    ( SELECT forecast.date AS fdate, place.name AS pname, avg(place.tempmax) AS avg     " +
                        "        FROM forecast, place     " +
                        "        WHERE place.forecastid = forecast.forecastid AND forecast.timeofday = 'day' " +
                        "        GROUP BY place.name, forecast.date " +
                        "    ) dt,     " +
                        "        ( SELECT forecast.date AS fdate, place.name AS pname, avg(place.tempmin) AS avg     " +
                        "        FROM forecast, place     " +
                        "        WHERE place.forecastid = forecast.forecastid AND forecast.timeofday = 'night' " +
                        "        GROUP BY place.name, forecast.date " +
                        "    ) nt     " +
                        "    WHERE dp.fdate = np.fdate AND dp.pname = np.pname AND dp.fdate = dt.fdate AND dp.pname = dt.pname AND dp.fdate = nt.fdate AND dp.pname=nt.pname     " +
                        "    GROUP BY dp.fdate, dp.pname, dayphen, daymaxtemp, nightphen, nightmintemp     " +
                        "    ORDER BY dp.fdate, dp.pname;";

        return DAO.querySQL(queryString);

    }

    /**
     * This is the query for all forecasts.
     *
     * @param daysAfterTomorrow the number of days before the forecasted day.
     * @return The list of objects arrays (Object[]) containing data of forecasts with all possible days which were made on the given day before. There are one forecast for each day.
     */
    private List getForecastsForGivenDayAfterTomorrow(int daysAfterTomorrow) {
        String queryString = "SELECT dp.fdate as fdate, np.phen AS nightphen, nt.tempmax AS nighttempmax, nt.tempmin AS nighttempmin, dp.phen AS dayphen, dt.tempmax AS daytempmax, dt.tempmin AS daytempmin " +
                "    FROM  " +
                "    (SELECT sub1.date fdate, sub1.phenomenon phen " +
                "        FROM ( " +
                "        SELECT date, phenomenon, count(*) as cnnt " +
                "            FROM forecast " +
                "            WHERE forecast.timeofday = 'day' " +
                "                AND date_part('day', forecast.date - forecast.timeofupdate) = :daysAfterTomorrow  " +
                "            GROUP BY date, phenomenon " +
                "        ) AS sub1 " +
                "        GROUP BY sub1.date, phen, sub1.cnnt " +
                "        HAVING    (sub1.date, sub1.cnnt) IN ( " +
                "        SELECT sub2.date, max(cnt) " +
                "            FROM ( " +
                "            SELECT date, phenomenon, count(*) AS cnt " +
                "                FROM forecast " +
                "                WHERE timeofday = 'day' " +
                "                    AND date_part('day', forecast.date - forecast.timeofupdate) = :daysAfterTomorrow " +
                "                GROUP BY date, phenomenon " +
                "            ) AS sub2 " +
                "        GROUP BY date " +
                "        HAVING sub1.date=date " +
                "        ) " +
                "    ) dp, " +
                "    (SELECT sub1.date fdate, sub1.phenomenon phen " +
                "        FROM ( " +
                "        SELECT date, phenomenon, count(*) as cnnt " +
                "            FROM forecast " +
                "            WHERE forecast.timeofday = 'day' " +
                "                AND date_part('day', forecast.date - forecast.timeofupdate) = :daysAfterTomorrow " +
                "            GROUP BY date, phenomenon " +
                "        ) AS sub1 " +
                "        GROUP BY sub1.date, phen, sub1.cnnt " +
                "        HAVING    (sub1.date, sub1.cnnt) IN ( " +
                "        SELECT sub2.date, max(cnt) " +
                "            FROM ( " +
                "            SELECT date, phenomenon, count(*) AS cnt " +
                "                FROM forecast " +
                "                WHERE timeofday = 'night' " +
                "                    AND date_part('day', forecast.date - forecast.timeofupdate) = :daysAfterTomorrow " +
                "                GROUP BY date, phenomenon " +
                "            ) AS sub2 " +
                "        GROUP BY date " +
                "        HAVING sub1.date=date " +
                "        ) " +
                "    ) np, " +
                "    ( SELECT date, avg(tempmax) AS tempmax, avg(tempmin) AS tempmin " +
                "        FROM forecast " +
                "        WHERE timeofday = 'day' " +
                "            AND date_part('day', forecast.date - forecast.timeofupdate) = :daysAfterTomorrow " +
                "        GROUP BY date " +
                "    ) dt, " +
                "    ( SELECT date, avg(tempmax) AS tempmax, avg(tempmin) AS tempmin " +
                "        FROM forecast " +
                "        WHERE timeofday = 'night' " +
                "            AND date_part('day', forecast.date - forecast.timeofupdate) = :daysAfterTomorrow " +
                "        GROUP BY date " +
                "    ) nt  " +
                "    WHERE dp.fdate = np.fdate AND dt.date = dp.fdate AND nt.date = dp.fdate " +
                "    ORDER BY dp.fdate;";

        return DAO.querySQL(queryString, "daysAfterTomorrow", daysAfterTomorrow);
    }
}
