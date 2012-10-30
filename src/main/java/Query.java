import java.util.List;
import java.util.Timer;

public class Query {
    static List queryList;
    static DatabaseAccessObject databaseAccessObject;

    public Query(DatabaseAccessObject databaseAccessObject) {
        this.databaseAccessObject = databaseAccessObject;
        Timer queryTimer = new Timer("queryTimer");
        QueryTask queryTask = new QueryTask();
        queryTimer.schedule(queryTask, 0, Main.timeBetweenQuering);
    }

    public static List getQueryList() {
        return queryList;
    }

    public static void setQueryList(List queryList) {
        Query.queryList = queryList;
    }
}
