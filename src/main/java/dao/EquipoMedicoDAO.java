package dao;

import db.SQLiteDAO;
import java.util.List;
import model.EquipoMedico;
import org.sql2o.Connection;

public class EquipoMedicoDAO {
    
    /**
     * Retorna un equipo médico.
     *
     * @param nombre {@code String} el nombre del equipo médico.
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
     * Retorna un equipo médico.
     *
     * @param id {@code Integer} id del equipo médico a recuperar.
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
     * Retorna todo el equipo médico con poco stock (5 unidades o menos).
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
     * @param me {@code EquipoMedico} el equipo medico a insertar.
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
     * Retorna un equipo médico.
     *
     * @param name {@code String} nombre del equipo médico a recuperar.
     * @return A {@code EquipoMedico}.
     */
    public EquipoMedico getMedEqByName(String name) {
        String query = "SELECT * FROM equipoMedico WHERE nombre = :name;";
        
        try(Connection con = SQLiteDAO.getConn().open()) {
            EquipoMedico me = con
                    .createQuery(query)
                    .addParameter("name", name)
                    .executeAndFetchFirst(EquipoMedico.class);
            
            return me;
        }
        catch(Exception e) {
            System.out.println(e);
        }
        
        return null;
    }
    
    /**
     * Actualiza un equipo médico.
     *
     * @param me {@code EquipoMedico} el equipo médico a actualizar.
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
     * Elimina un equipo médico.
     *
     * @param id {@code Integer} el id del equipo médico a eliminar.
     * @return A {@code boolean} si se pudo eliminar.
     */
    public boolean delete(int id) {
        String query = "DELETE FROM equipoMedico WHERE id = :id";
        
        try (Connection con = SQLiteDAO.getConn().open()) {
            con.createQuery(query)
                    .addParameter("id", id)
                    .executeUpdate();
            
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}
