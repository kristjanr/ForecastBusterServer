package forecastbuster.outgoing.entities;

import java.math.BigDecimal;

public class Station {
    String name;
    String nightPhenomenon;
    BigDecimal nightTemp;
    String dayPhenomenon;
    BigDecimal dayTemp;

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

    public BigDecimal getNightTemp() {
        return nightTemp;
    }

    public void setNightTemp(BigDecimal nightTemp) {
        this.nightTemp = nightTemp;
    }

    public String getDayPhenomenon() {
        return dayPhenomenon;
    }

    public void setDayPhenomenon(String dayPhenomenon) {
        this.dayPhenomenon = dayPhenomenon;
    }

    public BigDecimal getDayTemp() {
        return dayTemp;
    }

    public void setDayTemp(BigDecimal dayTemp) {
        this.dayTemp = dayTemp;
    }
}
