package forecastbuster.outgoing.entities;


import org.junit.Before;
import org.junit.Test;

import java.sql.Date;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ForecastTest {
    Forecast forecast;

    @Before
    public void setUp() {
        forecast = new Forecast();
    }

    @Test
    public void createForecastShouldCreateForecastWithRequiredAttributes() {
        Date date = new Date(Date.valueOf("2012-11-17").getTime());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String nightphenomenon = "17 nov night phen";
        Double nighttempmax = 0.0;
        Double nighttempmin = -1.0;
        String dayphenomenon = "17 nov day phen";
        Double daytempmax = 5.0;
        Double daytempmin = 1.0;
        Object[] forecastData = {date, nightphenomenon, nighttempmax, nighttempmin, dayphenomenon, daytempmax, daytempmin
        };

        forecast.createForecast(forecastData);

        assertEquals(cal, forecast.getDate());
        assertEquals(nightphenomenon, forecast.getNightPhenomenon());
        assertEquals(nighttempmax, forecast.getNightMaxTemp(), 0.001);
        assertEquals(nighttempmin, forecast.getNightMinTemp(), 0.001);
        assertEquals(dayphenomenon, forecast.getDayPhenomenon());
        assertEquals(daytempmax, forecast.getDayMaxTemp(), 0.01);
        assertEquals(daytempmin, forecast.getDayMinTemp(), 0.001);
    }

    @Test
    public void createForecastShouldCreateForecastWithOnlyDateIfNoMoreData() {
        Date date = new Date(Date.valueOf("2012-11-17").getTime());
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Object[] forecastData = {date, null, null, null, null, null, null};

        forecast.createForecast(forecastData);

        assertEquals(cal,forecast.getDate());
        assertNull(forecast.getNightPhenomenon());
    }
}
