package forecastbuster.outgoing.entities;

import java.sql.Date;
import java.util.Calendar;

public class Place {
    private Calendar date;
    private String name;
    private String nightPhenomenon;
    private double nightMinTemp;
    private String dayPhenomenon;
    private double dayMaxTemp;

    public void createPlace(Object[] queryList) {
        setDate(queryList[0]);
        setName((String) queryList[1]);
        setNightPhenomenon((String) queryList[2]);
        setNightMinTemp((Double) queryList[3]);
        setDayPhenomenon((String) queryList[4]);
        setDayMaxTemp((Double) queryList[5]);
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Object date) {
        Date tempDate = (Date) date;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(tempDate.getTime());
        setDate(cal);
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNightPhenomenon() {
        return nightPhenomenon;
    }

    public void setNightPhenomenon(String nightPhenomenon) {
        this.nightPhenomenon = nightPhenomenon;
    }

    public double getNightMinTemp() {
        return nightMinTemp;
    }

    public void setNightMinTemp(double nightMinTemp) {
        this.nightMinTemp = (double) Math.round(nightMinTemp * 100) / 100;
    }

    public String getDayPhenomenon() {
        return dayPhenomenon;
    }

    public void setDayPhenomenon(String dayPhenomenon) {
        this.dayPhenomenon = dayPhenomenon;
    }

    public double getDayMaxTemp() {
        return dayMaxTemp;
    }

    public void setDayMaxTemp(double dayMaxTemp) {
        this.dayMaxTemp = (double) Math.round(dayMaxTemp * 100) / 100;
    }
}
