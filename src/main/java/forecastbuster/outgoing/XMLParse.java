package forecastbuster.outgoing;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import forecastbuster.outgoing.entities.Forecast;
import forecastbuster.outgoing.entities.ForecastedDay;
import forecastbuster.outgoing.entities.Place;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class XMLParse {

    private static String getXMLStringFromForecastList(ForecastedDay forecasts) {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("Forecast", Forecast.class);
        xstream.alias("Forecasts", ForecastedDay.class);
        xstream.alias("Place", Place.class);
        xstream.addImplicitCollection(ForecastedDay.class, "list");
        String xmlString = xstream.toXML(forecasts);
        return xmlString;
    }

    private static ForecastedDay createTestData() {
        Place outPlace = new Place();
        List list = new ArrayList();
        list.add(outPlace);
        Forecast outForecast = new Forecast();
        //outForecast.createForecast();
        ForecastedDay forecastedDay = new ForecastedDay(Calendar.getInstance());
        forecastedDay.setForecasted1DayBefore(outForecast);
        return forecastedDay;
    }

    public static String getTestXMLText() {
        return getXMLStringFromForecastList(createTestData());
    }

}
