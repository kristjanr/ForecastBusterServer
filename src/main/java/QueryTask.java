import java.util.List;
import java.util.TimerTask;


public class QueryTask extends TimerTask {

    @Override
    public void run() {
        getTomorrowsForecastForEveryDate();

    }

    /*
    * In case the forecasts about a place for a certain date change during this one day,
    * the most frequent phenomenon is taken and the average of all different forecasted temperatures is calculated.
    * */
    void getTomorrowsForecastForEveryDate() {
        String queryString =
"SELECT dp.fdate as fdate, dp.pname as pname, dp.pphen AS dayphen, dt.avg AS daymaxtemp, np.pphen AS nightphen, nt.avg AS nightmintemp  \n" +
        "\tFROM \n" +
        "\t(SELECT sub1.date fdate, sub1.name pname, sub1.phenomenon pphen\n" +
        "\t\tFROM (\n" +
        "\t\tSELECT date, name, place.phenomenon, count(*) as cnnt\n" +
        "\t\t\tFROM forecast, place \n" +
        "\t\t\tWHERE place.forecastid = forecast.forecastid AND forecast.timeofday LIKE 'day'\n" +
        "\t\t\tGROUP BY date, name, place.phenomenon\t\t\t\t\n" +
        "\t\t) AS sub1\n" +
        "\t\tGROUP BY sub1.date, sub1.name, pphen, sub1.cnnt\n" +
        "\t\tHAVING\t(sub1.date, sub1.name, sub1.cnnt) IN (\n" +
        "\t\tSELECT sub2.date, sub2.name, max(cnt)\n" +
        "\t\t\tFROM (\n" +
        "\t\t\tSELECT f2.date, p2.name, p2.phenomenon AS pphen, count(*) AS cnt\n" +
        "\t\t\t\tFROM forecast f2, place p2\n" +
        "\t\t\t\tWHERE p2.forecastid = f2.forecastid AND f2.timeofday LIKE 'day'\n" +
        "\t\t\t\tGROUP BY p2.name, f2.date, pphen\t\t\t\t\n" +
        "\t\t\t) AS sub2\n" +
        "\t\tGROUP BY date, name\n" +
        "\t\tHAVING sub1.date=date AND sub1.name=name\n" +
        "\t\t)\n" +
        "\t) dp,    \n" +
        "\t( SELECT sub1.date fdate, sub1.name pname, sub1.phenomenon pphen\n" +
        "\t\tFROM (\n" +
        "\t\tSELECT date, name, place.phenomenon, count(*) as cnnt\n" +
        "\t\t\tFROM forecast, place \n" +
        "\t\t\tWHERE place.forecastid = forecast.forecastid AND forecast.timeofday LIKE 'night'\n" +
        "\t\t\tGROUP BY date, name, place.phenomenon\t\t\t\t\n" +
        "\t\t) AS sub1\n" +
        "\tGROUP BY sub1.date, sub1.name, pphen, sub1.cnnt\n" +
        "\tHAVING\t(sub1.date, sub1.name, sub1.cnnt) IN (\n" +
        "\t\tSELECT sub2.date, sub2.name, max(cnt)\n" +
        "\t\t\tFROM (\n" +
        "\t\t\tSELECT f2.date, p2.name, p2.phenomenon AS pphen, count(*) AS cnt\n" +
        "\t\t\t\tFROM forecast f2, place p2\n" +
        "\t\t\t\tWHERE p2.forecastid = f2.forecastid AND f2.timeofday LIKE 'night'\n" +
        "\t\t\t\tGROUP BY p2.name, f2.date, pphen\t\t\t\t\n" +
        "\t\t\t) AS sub2\n" +
        "\t\tGROUP BY date, name\n" +
        "\t\tHAVING sub1.date=date AND sub1.name=name\n" +
        "\t\t)\n" +
        "\t) np,    \n" +
        "\t( SELECT forecast.date AS fdate, place.name AS pname, avg(place.tempmax) AS avg    \n" +
        "\t\tFROM forecast, place    \n" +
        "\t\tWHERE place.forecastid = forecast.forecastid AND forecast.timeofday LIKE 'day'\n" +
        "\t\tAND date_part('day', forecast.date - forecast.timeofupdate) = 0   \n" +
        "\t\tGROUP BY place.name, forecast.date\n" +
        "\t) dt,    \n" +
        "        ( SELECT forecast.date AS fdate, place.name AS pname, avg(place.tempmin) AS avg    \n" +
        "\t\tFROM forecast, place    \n" +
        "\t\tWHERE place.forecastid = forecast.forecastid AND forecast.timeofday LIKE 'night'\n" +
        "\t\tAND date_part('day', forecast.date - forecast.timeofupdate) = 0   \n" +
        "\t\tGROUP BY place.name, forecast.date\n" +
        "\t) nt    \n" +
        "\tWHERE dp.fdate = np.fdate AND dp.pname = np.pname AND dp.fdate = dt.fdate AND dp.pname = dt.pname AND dp.fdate = nt.fdate AND dp.pname=nt.pname    \n" +
        "\tGROUP BY dp.fdate, dp.pname, dayphen, daymaxtemp, nightphen, nightmintemp    \n" +
        "\tORDER BY dp.fdate, dp.pname;";

        List queryList = Main.getDatabaseAccessObject().querySQL(queryString);
        Query.setForecastsForTomorrow(queryList);
    }
}
