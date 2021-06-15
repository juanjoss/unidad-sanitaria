package dao;

import db.SQLiteDAO;
import java.util.List;
import model.EquipoMedico;
import org.sql2o.Connection;

public class EquipoMedicoDAO {

    /**
     * Retorna todo el equipo medico con poco stock (5 unidades o menos).
     *
     * @return A {@code List<EquipoMedico>}.
     */
    public List<EquipoMedico> medEqWithLowStock() {
        String query = "SELECT * FROM equipoMedico WHERE stock <= 5";

        try (Connection con = SQLiteDAO.getConn().open()) {
            List<EquipoMedico> medEq = con
                    .createQuery(query)
                    .executeAndFetch(EquipoMedico.class);

            return medEq;
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

    /**
     * Retorna un equipo médico.
     *
     * @param id A {@code string} el nombre del equipo médico.
     * @return A {@code EquipoMedico}.
     */
    public EquipoMedico buscarPorNombre(String nombre) {
        String query = "SELECT * "
                + "FROM equipoMedico "
                + "WHERE equipoMedico.nombre = :nombre;";

        try (Connection con = SQLiteDAO.getConn().open()) {
            EquipoMedico em = con
                    .createQuery(query)
                    .addParameter("nombre", nombre)
                    .executeAndFetchFirst(EquipoMedico.class);
            return em;
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }
}
