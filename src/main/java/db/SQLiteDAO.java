package db;

import org.sql2o.Sql2o;

public class SQLiteDAO {

    private static Sql2o sql2o;

    public static Sql2o getConn() {
        if (sql2o == null) {
            sql2o = new Sql2o("jdbc:sqlite:./unidad_sanitaria.db", null, null);
        }
        
        return sql2o;
    }
}
