package dao;

import db.SQLiteDAO;
import org.sql2o.Connection;
import java.util.List;
import model.Presentacion;

public class PresentacionDAO {
    
    /**
     * Retorna todos los tipos de presentacion.
     *
     * @return {@code List<Presentacion>}.
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

    /**
     * Inserta una presentacion en la BD.
     *
     * @param p {@code Presentacion} la presentacion a insertar.
     */
    public void insert(Presentacion p) {
        String query = "INSERT INTO presentacion (nombre) VALUES (:nombre)";

        try (Connection con = SQLiteDAO.getConn().open()) {
            con.createQuery(query).bind(p).executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
