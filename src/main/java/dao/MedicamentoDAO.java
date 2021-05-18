package dao;

import db.SQLiteDAO;
import org.sql2o.Connection;
import java.util.List;
import model.Medicamento;
import util.DateUtil;

public class MedicamentoDAO {
    
    /**
     * Retorna todos los medicamentos.
     *
     * @return A {@code List<Medicameto>}.
     */
    public List<Medicamento> selectAll() {
        String query = "SELECT medicamento.id, medicamento.nombre, stock, fechaVencimiento, laboratorio, dosis, "
                + "presentacion.nombre as 'presentacion' FROM medicamento JOIN presentacion ON medicamento.id_presentacion = presentacion.id;";

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
    
    /**
     * Retorna un medicamento.
     *
     * @param id A {@code int} el ID del medicamento.
     * @return A {@code Medicameto}.
     */
    public Medicamento getMedicamento(int id) {
        String query = "SELECT id, nombre, stock, fechaVencimiento, laboratorio, dosis, id_presentacion AS 'presentacion' "
                + "FROM medicamento WHERE id = :id";

        try (Connection con = SQLiteDAO.getConn().open()) {
            Medicamento med = con
                    .createQuery(query)
                    .addParameter("id", id)
                    .executeAndFetchFirst(Medicamento.class);

            med.setFechaVencimiento(
                    DateUtil.formatDate(
                            med.getFechaVencimiento(),
                            "yyyy-mm-dd",
                            "dd/mm/yyyy"
                    )
            );

            return med;
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

    /**
     * Retorna todos los medicamentos con poco stock (5 unidades o menos).
     *
     * @return A {@code List<Medicameto>}.
     */
    public List<Medicamento> medsWithLowStock() {
        String query = "SELECT id, nombre, stock, fechaVencimiento, laboratorio, dosis, id_presentacion AS 'presentacion'"
                + "FROM medicamento WHERE stock <= 5";

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
    
    /**
     * Retorna todos los medicamentos en rango de vencimiento (rango de 15 dias).
     *
     * @return A {@code List<Medicameto>}.
     */
    public List<Medicamento> medsInExpRange() {
        String query = "SELECT id, nombre, stock, fechaVencimiento, laboratorio, dosis, id_presentacion AS 'presentacion' "
                + "FROM medicamento WHERE (julianday(fechaVencimiento) - julianday('now')) <= 15";

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
    
    /**
     * Inserta un medicamento en la BD.
     *
     * @param med A {@code Medicamento} el medicamento a insertar.
     */
    public void insert(Medicamento med) {
        String query = "INSERT INTO medicamento (nombre, stock, fechaVencimiento, laboratorio, dosis, id_presentacion) "
                + "VALUES (:nombre, :stock, :fechaVencimiento, 'Pfizer', '600 ml', 1)";

        try (Connection con = SQLiteDAO.getConn().open()) {
            con.createQuery(query).bind(med).executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    /**
     * Elimina un medicamento de la BD.
     *
     * @param id A {@code int} el ID del medicamento a eliminar.
     */
    public void deleteXId(int id) {
        String query = "DELETE FROM medicamento WHERE id = :id";

        try (Connection con = SQLiteDAO.getConn().open()) {
            con.createQuery(query).addParameter("id", id).executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    /**
     * Actualiza un medicamento de la BD.
     *
     * @param med A {@code Medicameto} el medicamento a actualizar.
     */
    public void update(Medicamento med) {
        String query1 = "SELECT id FROM presentacion WHERE nombre = :presName";
        
        try (Connection con = SQLiteDAO.getConn().open()) {
            Integer medPresId = con
                    .createQuery(query1)
                    .addParameter("presName", med.getPresentacion())
                    .executeAndFetchFirst(Integer.class);
            
            if(medPresId != null) {
                String query2 = "UPDATE medicamento "
                        + "SET nombre = :nombre, stock = :stock, fechaVencimiento = :fechaVencimiento, "
                        + "laboratorio = :laboratorio, dosis = :dosis, id_presentacion = " + medPresId
                        + " WHERE id = :id";

                con.createQuery(query2).bind(med).executeUpdate();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public boolean exist( Medicamento med){
        boolean var = false;
        String query1 = "SELECT id FROM presentacion WHERE nombre = :presName";
        try (Connection con = SQLiteDAO.getConn().open()) {
            Integer medPresId = con
                    .createQuery(query1)
                    .addParameter("presName", med.getPresentacion())
                    .executeAndFetchFirst(Integer.class);
            
            if(medPresId != null) {
               var = true;
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return var;
    }
}
