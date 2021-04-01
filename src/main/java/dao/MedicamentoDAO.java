package dao;

import db.SQLiteDAO;
import org.sql2o.Connection;
import java.util.List;
import model.Medicamento;

/**
 *
 * @author Juan Josserand
 */
public class MedicamentoDAO {

    public List<Medicamento> selectAll() {
        String query = "SELECT nombre, stock, fechaVencimiento FROM medicamento";

        try (Connection con = SQLiteDAO.getConn().open()) {
            List<Medicamento> medicamentos = con
                    .createQuery(query)
                    .executeAndFetch(Medicamento.class);

            return medicamentos;
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }
}
