package db;

import org.sql2o.Sql2o;

/**
 *
 * @author Juan Josserand
 */
public class SQLiteDAO {

    private static Sql2o sql2o;

    public static Sql2o getConn() {
        if (sql2o == null) {
            sql2o = new Sql2o("jdbc:sqlite:./unidad_sanitaria.db", null, null);
            System.out.println("aaa");
        }
        
        return sql2o;
    }
}
