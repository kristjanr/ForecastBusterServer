package forecastbuster.outgoing;

import forecastbuster.DatabaseAccessObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class QueryTaskObservation extends TimerTask {
    Query query;
    DatabaseAccessObject DAO;
    static org.slf4j.Logger log = LoggerFactory.getLogger(QueryTaskObservation.class);
    public QueryTaskObservation(Query query) {
        this.query = query;
        DAO = query.getDatabaseAccessObject();
    }
    @Override
    public void run(){
        List dayList = getObservationsForDay();
        List nightList = getObservationsForNight();
    }

    List getObservationsForDay() {
        String queryString = query.getSqlString("observation_day.sql");
        return getObservations(queryString);
    }

    List getObservationsForNight() {
        String queryString = query.getSqlString("observation_night.sql");
        return getObservations(queryString);
    }

    private List getObservations(String queryString) {
        List queryList = new ArrayList();
        try {
            queryList = DAO.querySQL(queryString);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return queryList;
    }
}
