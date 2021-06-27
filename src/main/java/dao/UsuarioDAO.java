package dao;

import model.Usuario;
import db.SQLiteDAO;
import org.sql2o.Connection;

public class UsuarioDAO {
    
    /**
     * Retorna un Usuario.
     *
     * @param username {@code String} nombre de usuario.
     * @param pass {@code String} contraseña.
     * @return Un {@code Usuario} si es encontrado, o {@code null} en otro caso.
     */
    public Usuario getUsuario(String username, String pass) {
        String query = "SELECT * FROM usuario WHERE userName = :username AND pass = :pass;";

        try (Connection con = SQLiteDAO.getConn().open()) {
            Usuario user = con
                    .createQuery(query)
                    .addParameter("username", username)
                    .addParameter("pass", pass)
                    .executeAndFetchFirst(Usuario.class);

            return user;
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

    /**
     * Retorna un Usuario.
     *
     * @param username {@code String} nombre de usuario.
     * @param email {@code String} email.
     * @return Un {@code Usuario} si es encontrado, o {@code null} en otro caso.
     */
    public Usuario getUsuarioByEmail(String username, String email) {
        String query = "SELECT * FROM usuario WHERE userName = :username AND email = :email;";

        try (Connection con = SQLiteDAO.getConn().open()) {
            Usuario user = con
                    .createQuery(query)
                    .addParameter("username", username)
                    .addParameter("email", email)
                    .executeAndFetchFirst(Usuario.class);

            return user;
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

    /**
     * Actualiza la ultima sesion de usuario.
     *
     * @param id  {@code int} id del usuario.
     * @param date {@code String} fecha nueva.
     */
    public void updateLastLogin(int id, String date) {
        String query = "UPDATE usuario SET ultimaSesion = :date WHERE id = :id";

        try (Connection con = SQLiteDAO.getConn().open()) {
            con.createQuery(query)
            .addParameter("date", date)
            .addParameter("id", id)
            .executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Cambia la contraseña.
     *
     * @param id  {@code int} id del usuario.
     * @param newPass {@code String} contraseña nueva.
     */
    public void changePass(int id, String newPass) {
        String query = "UPDATE usuario SET pass = :newPass WHERE id = :id";

        try (Connection con = SQLiteDAO.getConn().open()) {
            con.createQuery(query)
            .addParameter("newPass", newPass)
            .addParameter("id", id)
            .executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
