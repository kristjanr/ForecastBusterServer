package forecastbuster.outgoing.entities;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

public class Forecast {
    private Calendar date;
    private String nightPhenomenon;
    private Double nightMaxTemp;
    private Double nightMinTemp;
    private String dayPhenomenon;
    private Double dayMaxTemp;
    private Double dayMinTemp;
    private List<Place> places;

    public Forecast() {
        nightPhenomenon = null;
        nightMaxTemp = null;
        nightMinTemp = null;
        dayPhenomenon = null;
        dayMaxTemp = null;
        dayMinTemp = null;
    }

    public void createForecast(Object[] forecastData) {
        setDate(forecastData[0]);
        if (forecastData[1] != null) {
            setNightPhenomenon(forecastData[1]);
            setNightMaxTemp((Double) forecastData[2]);
            setNightMinTemp((Double) forecastData[3]);
            setDayPhenomenon(forecastData[4]);
            setDayMaxTemp((Double) forecastData[5]);
            setDayMinTemp((Double) forecastData[6]);
        }
    }

    private void setNightPhenomenon(Object o) {
        setNightPhenomenon((String) o);
    }

    private void setDate(Object date) {
        Date tempDate = (Date) date;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(tempDate.getTime());
        setDate(cal);
    }

    public void setNightPhenomenon(String nightPhenomenon) {
        this.nightPhenomenon = nightPhenomenon;
    }

    public void setNightMaxTemp(double nightMaxTemp) {
        this.nightMaxTemp = (double) Math.round(nightMaxTemp * 100) / 100;
    }

    public void setNightMinTemp(double nightMinTemp) {
        this.nightMinTemp = (double) Math.round(nightMinTemp * 100) / 100;
    }

    private void setDayPhenomenon(Object o) {
        setDayPhenomenon((String) o);
    }

    public void setDayMaxTemp(double dayMaxTemp) {
        this.dayMaxTemp = (double) Math.round(dayMaxTemp * 100) / 100;
    }

    public void setDayMinTemp(double dayMinTemp) {
        this.dayMinTemp = (double) Math.round(dayMinTemp * 100) / 100;
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

    public double getNightMaxTemp() {
        return nightMaxTemp;
    }

    public double getNightMinTemp() {
        return nightMinTemp;
    }

    public double getDayMaxTemp() {
        return dayMaxTemp;
    }

    public double getDayMinTemp() {
        return dayMinTemp;
    }
}
