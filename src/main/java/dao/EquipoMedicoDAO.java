package dao;

import db.SQLiteDAO;
import java.util.List;
import model.EquipoMedico;
import org.sql2o.Connection;

public class EquipoMedicoDAO {
    
    /**
     * Retorna todo el equipo medico.
     *
     * @return A {@code List<EquipoMedico>}.
     */
    public List<EquipoMedico> selectAll() {
        String query = "SELECT * FROM equipoMedico;";
        
        try(Connection con = SQLiteDAO.getConn().open()) {
            List<EquipoMedico> medEq = con
                    .createQuery(query)
                    .executeAndFetch(EquipoMedico.class);
            
            return medEq;
        }
        catch(Exception e) {
            System.out.println(e);
        }
        
        return null;
    }
    
    /**
     * Retorna todo el equipo medico.
     *
     * @param id A {@code Integer} id del equipo medico a recuperar.
     * @return A {@code EquipoMedico}.
     */
    public EquipoMedico getMedEq(int id) {
        String query = "SELECT * FROM equipoMedico WHERE id = :id;";
        
        try(Connection con = SQLiteDAO.getConn().open()) {
            EquipoMedico me = con
                    .createQuery(query)
                    .addParameter("id", id)
                    .executeAndFetchFirst(EquipoMedico.class);
            
            return me;
        }
        catch(Exception e) {
            System.out.println(e);
        }
        
        return null;
    }
    
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
     * Inserta un equipo medico.
     *
     * @param me A {@code EquipoMedico} el equipo medico a insertar.
     * @return A {@code boolean} si se pudo insertar.
     */
    public boolean insert(EquipoMedico me) {
        String query = "INSERT INTO equipoMedico (nombre, stock) VALUES (:nombre, :stock)";
        
        try (Connection con = SQLiteDAO.getConn().open()) {
            con.createQuery(query).bind(me).executeUpdate();
            
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
    
    /**
     * actualiza un equipo medico.
     *
     * @param me A {@code EquipoMedico} el equipo medico a actualizar.
     * @return A {@code boolean} si se pudo actualizar.
     */
    public boolean update(EquipoMedico me) {
        String query = "UPDATE equipoMedico SET id = :id, nombre = :nombre, stock = :stock WHERE id = :id";
        
        try (Connection con = SQLiteDAO.getConn().open()) {
            con.createQuery(query).bind(me).executeUpdate();
            
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Elimina un equipo medico.
     *
     * @param id A {@code Integer} el id del equipo medico a eliminar.
     */
    public void delete(int id) {
        String query = "DELETE FROM equipoMedico WHERE id = :id";
        
        try (Connection con = SQLiteDAO.getConn().open()) {
            con.createQuery(query)
                    .addParameter("id", id)
                    .executeUpdate();
        }
        catch (Exception e) {}
    }
}
