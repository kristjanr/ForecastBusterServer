package forecastbuster.outgoing;

import forecastbuster.DatabaseAccessObject;
import forecastbuster.Main;

import java.util.List;
import java.util.TimerTask;


public class QueryTask extends TimerTask {
    Query query;

    public QueryTask(Query query) {
        this.query = query;
    }

    @Override
    public void run() {
        getTomorrowsForecasts();
        getThreeDaysForecastsAfterTomorrow();
    }

    void getThreeDaysForecastsAfterTomorrow() {
        String queryString = "SELECT dp.fdate as fdate, np.phen AS nightphen, nt.tempmax AS nighttempmax, nt.tempmin AS nighttempmin, dp.phen AS dayphen, dt.tempmax AS daytempmax, dt.tempmin AS daytempmin " +
                "    FROM  " +
                "    (SELECT sub1.date fdate, sub1.phenomenon phen " +
                "        FROM ( " +
                "        SELECT date, phenomenon, count(*) as cnnt " +
                "            FROM forecast " +
                "            WHERE forecast.timeofday = 'day' " +
                "                AND date_part('day', forecast.date - forecast.timeofupdate) = :daysaftertomorrow  " +
                "            GROUP BY date, phenomenon " +
                "        ) AS sub1 " +
                "        GROUP BY sub1.date, phen, sub1.cnnt " +
                "        HAVING    (sub1.date, sub1.cnnt) IN ( " +
                "        SELECT sub2.date, max(cnt) " +
                "            FROM ( " +
                "            SELECT date, phenomenon, count(*) AS cnt " +
                "                FROM forecast " +
                "                WHERE timeofday = 'day' " +
                "                    AND date_part('day', forecast.date - forecast.timeofupdate) = :daysaftertomorrow " +
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
                "                AND date_part('day', forecast.date - forecast.timeofupdate) = :daysaftertomorrow " +
                "            GROUP BY date, phenomenon " +
                "        ) AS sub1 " +
                "        GROUP BY sub1.date, phen, sub1.cnnt " +
                "        HAVING    (sub1.date, sub1.cnnt) IN ( " +
                "        SELECT sub2.date, max(cnt) " +
                "            FROM ( " +
                "            SELECT date, phenomenon, count(*) AS cnt " +
                "                FROM forecast " +
                "                WHERE timeofday = 'night' " +
                "                    AND date_part('day', forecast.date - forecast.timeofupdate) = :daysaftertomorrow " +
                "                GROUP BY date, phenomenon " +
                "            ) AS sub2 " +
                "        GROUP BY date " +
                "        HAVING sub1.date=date " +
                "        ) " +
                "    ) np, " +
                "    ( SELECT date, avg(tempmax) AS tempmax, avg(tempmin) AS tempmin " +
                "        FROM forecast " +
                "        WHERE timeofday = 'day' " +
                "            AND date_part('day', forecast.date - forecast.timeofupdate) = :daysaftertomorrow " +
                "        GROUP BY date " +
                "    ) dt, " +
                "    ( SELECT date, avg(tempmax) AS tempmax, avg(tempmin) AS tempmin " +
                "        FROM forecast " +
                "        WHERE timeofday = 'night' " +
                "            AND date_part('day', forecast.date - forecast.timeofupdate) = :daysaftertomorrow " +
                "        GROUP BY date " +
                "    ) nt  " +
                "    WHERE dp.fdate = np.fdate AND dt.date = dp.fdate AND nt.date = dp.fdate " +
                "    ORDER BY dp.fdate;";
        DatabaseAccessObject DAO = Main.getDatabaseAccessObject();
        List queryList = DAO.querySQL(queryString, "daysaftertomorrow", 1);
        Query query = Main.getQuery();
        query.setForecastsFor1DayAfterTomorrow(queryList);

        queryList = DAO.querySQL(queryString, "daysaftertomorrow", 2);
        query.setForecastsFor2DaysAfterTomorrow(queryList);

        queryList = DAO.querySQL(queryString, "daysaftertomorrow", 3);
        query.setForecastsFor3DaysAfterTomorrow(queryList);
    }

    /*
    * In case the forecasts about a place for a certain date change during this one day,
    * the most frequent phenomenon is taken and the average of all diffeent forecasted temperatures is calculated.
    * */
    void getTomorrowsForecasts() {
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

        List queryList = Main.getDatabaseAccessObject().querySQL(queryString);
        synchronized (query) {
            query.setForecastsForTomorrow(queryList);            ;
            query.notify();
        }
    }
}
