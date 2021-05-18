package dao;

import db.SQLiteDAO;
import org.sql2o.Connection;
import java.util.List;
import model.Presentacion;

public class PresentacionDAO {
    /**
     * Retorna todos los tipos de presentacion.
     *
     * @return A {@code List<Presentacion>}.
     */
    public List<Presentacion> selectAll() {
        String query = "SELECT * FROM presentacion";

        try (Connection con = SQLiteDAO.getConn().open()) {
            List<Presentacion> presentaciones = con
                    .createQuery(query)
                    .executeAndFetch(Presentacion.class);
            return presentaciones;
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }
}
