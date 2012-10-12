package ee.kristjan;

/**
 * Hello world!
 *
 */
public class Main
{   private static DatabaseAccessObject databaseAccessObject = null;

    public static void main( String[] args )
    {
        getDatabaseAccessObject().initSession();
    }

    public static DatabaseAccessObject getDatabaseAccessObject() {
        if (databaseAccessObject == null) {
            databaseAccessObject = new DatabaseAccessObject();
        }
        return databaseAccessObject;
    }

    public static void setDatabaseAccessObject(DatabaseAccessObject databaseAccessObject) {
        Main.databaseAccessObject = databaseAccessObject;
    }
}
