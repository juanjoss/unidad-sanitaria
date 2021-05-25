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
}
