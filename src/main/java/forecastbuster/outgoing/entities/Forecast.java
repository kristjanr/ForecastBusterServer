package forecastbuster.outgoing.entities;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

public class Forecast {
    private Calendar date;
    private String nightPhenomenon;
    private double nightMaxTemp;
    private double nightMinTemp;
    private String dayPhenomenon;
    private double dayMaxTemp;
    private double dayMinTemp;
    private List<Place> places;

    public void createForecast(Object[] forecastData) {
        setDate(forecastData[0]);
        setNightPhenomenon(forecastData[1]);
        setNightMaxTemp((Double) forecastData[2]);
        setNightMinTemp((Double) forecastData[3]);
        setDayPhenomenon(forecastData[4]);
        setDayMaxTemp((Double) forecastData[5]);
        setDayMinTemp((Double) forecastData[6]);
    }

    private void setNightPhenomenon(Object o) {
        setNightPhenomenon((String) o);
    }

    private void setDate(Object date) {
        Timestamp timestamp = (Timestamp) date;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp.getTime());
        setDate(cal);
    }

    public void setNightPhenomenon(String nightPhenomenon) {
        this.nightPhenomenon = nightPhenomenon;
    }

    public void setNightMaxTemp(double nightMaxTemp) {
        this.nightMaxTemp = nightMaxTemp;
    }

    public void setNightMinTemp(double nightMinTemp) {
        this.nightMinTemp = nightMinTemp;
    }

    private void setDayPhenomenon(Object o) {
        setDayPhenomenon((String) o);
    }

    public void setDayMaxTemp(double dayMaxTemp) {
        this.dayMaxTemp = dayMaxTemp;
    }

    public void setDayMinTemp(double dayMinTemp) {
        this.dayMinTemp = dayMinTemp;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public String getNightPhenomenon() {
        return nightPhenomenon;
    }

    public String getDayPhenomenon() {
        return dayPhenomenon;
    }

    public void setDayPhenomenon(String dayPhenomenon) {
        this.dayPhenomenon = dayPhenomenon;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }
}
