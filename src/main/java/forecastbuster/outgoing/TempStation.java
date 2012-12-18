package forecastbuster.outgoing;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Calendar;

class TempStation {
    private Calendar date;
    private String name;
    private String phenomenon;
    private BigDecimal temp;

    void createTempStation(Object[] tempStationData) {
        setDate(tempStationData[0]);
        setName(tempStationData[1]);
        setPhenomenon(tempStationData[2]);
        setTemp(tempStationData[3]);
    }

    private void setTemp(Object temp) {
        this.temp = (BigDecimal) temp;
    }

    private void setPhenomenon(Object phenomenon) {
        this.phenomenon = (String) phenomenon;
    }

    private void setName(Object name) {
        this.name = (String) name;
    }

    private void setDate(Object date) {
        Date tempDate = (Date) date;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(tempDate.getTime());
        setDate(cal);
    }
    private void setDate(Calendar calendar){
        this.date=calendar;
    }

    public Calendar getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getPhenomenon() {
        return phenomenon;
    }

    public BigDecimal getTemp() {
        return temp;
    }
}
