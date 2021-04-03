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
    
    //Si NOMBRE no es UNIQUE tenemos que controlar que no esté en la tabla
    public void insert(Medicamento med) {
        String query = "INSERT INTO medicamento (nombre, stock, fechaVencimiento) VALUES (:nombre, :stock, :fechaVencimiento);";

        try (Connection con = SQLiteDAO.getConn().open()) {
            con.createQuery(query).bind(med).executeUpdate();
            System.out.println("Medicamento agregado");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    //Eliminamos por nombre (hay que ver si modificamos la bd y ponemos UNIQUE a la columna NOMBRE)
    public void deleteXNombre(String nombre) {
        String query = "DELETE FROM medicamento WHERE nombre = :nombre;";

        try (Connection con = SQLiteDAO.getConn().open()) {
            con.createQuery(query).addParameter("nombre", nombre).executeUpdate();
            System.out.println("Medicamento eliminado");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public void updateXNombre(String name, Medicamento med) {
        String query = "UPDATE medicamento "
                + "SET nombre = :nombre, stock = :stock, fechaVencimiento = :fechaVencimiento "
                + "WHERE nombre = :nombre";
        
        try (Connection con = SQLiteDAO.getConn().open()) {
            con.createQuery(query).bind(med).executeUpdate();
            System.out.println("Medicamento actualizado");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
