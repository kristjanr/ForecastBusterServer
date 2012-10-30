import java.util.List;
import java.util.TimerTask;


public class QueryTask extends TimerTask {

    @Override
    public void run() {
        getPlacesWithDayPhenomenon();
    }

    void getPlacesWithDayPhenomenon() {
        String queryString = "SELECT sub.fdate, sub.pname, sub.pphen, max(sub.count) AS max " +
                "FROM ( " +
                    "SELECT Forecast.date AS fdate, Place.name AS pname, Place.phenomenon AS pphen, count(Place.placeid) AS count " +
                    "FROM Forecast, Place " +
                    "WHERE Place.forecastid = Forecast.forecastid AND Forecast.timeofday LIKE 'day' " +
                        "AND date_part('hour', Forecast.timeofupdate) > 8 AND date_part('hour', Forecast.timeofupdate) < 23 " +
                    "GROUP BY Place.name, Forecast.date, Place.phenomenon" +
                    ") AS sub " +
                "GROUP BY sub.fdate, sub.pname, sub.pphen " +
                "ORDER BY sub.fdate;";

        List queryList = Main.getDatabaseAccessObject().querySQL(queryString);
        Query.setQueryList(queryList);
    }
}
