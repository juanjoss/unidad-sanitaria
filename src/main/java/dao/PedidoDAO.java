package dao;

import db.SQLiteDAO;
import java.util.List;
import model.Pedido;
import org.sql2o.Connection;


public class PedidoDAO{
        /**
     * Retorna todos los pedidos.
     *
     * @return A {@code List<Pedido>}.
     */
    public List<Pedido> selectAllxId(int id) {
        String query = "SELECT * FROM pedido where id = :id";

        try (Connection con = SQLiteDAO.getConn().open()) {
            List<Pedido> pedido = con
                    .createQuery(query)
                    .addParameter("id", id)
                    .executeAndFetch(Pedido.class);
            return pedido;
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

    public void insert(Pedido pedido) {
        String query = "INSERT INTO pedido (idUsuario, correoProveedor, fecha, estado) "
                + "VALUES (:idUsuario, :correoProveedor, :fecha, :estado)";

        try (Connection con = SQLiteDAO.getConn().open()) {
            con.createQuery(query).bind(pedido).executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}