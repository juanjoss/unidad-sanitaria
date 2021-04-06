package dao;

import db.SQLiteDAO;
import org.sql2o.Connection;
import java.util.List;
import model.Medicamento;

public class MedicamentoDAO {

    public List<Medicamento> selectAll() {
        String query = "SELECT * FROM medicamento";

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
    
    public void insert(Medicamento med) {
        String query = "INSERT INTO medicamento (nombre, stock, fechaVencimiento) VALUES (:nombre, :stock, :fechaVencimiento)";

        try (Connection con = SQLiteDAO.getConn().open()) {
            con.createQuery(query).bind(med).executeUpdate();
            System.out.println("Medicamento agregado");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public void deleteXId(int id) {
        String query = "DELETE FROM medicamento WHERE id = :id";

        try (Connection con = SQLiteDAO.getConn().open()) {
            con.createQuery(query).addParameter("id", id).executeUpdate();
            System.out.println("Medicamento eliminado");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public void update(Medicamento med) {
        String query = "UPDATE medicamento "
                + "SET nombre = :nombre, stock = :stock, fechaVencimiento = :fechaVencimiento "
                + "WHERE id = :id";
        
        try (Connection con = SQLiteDAO.getConn().open()) {
            con.createQuery(query).bind(med).executeUpdate();
            System.out.println("Medicamento actualizado");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
