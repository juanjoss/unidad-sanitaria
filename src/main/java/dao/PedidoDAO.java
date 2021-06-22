package dao;

import db.SQLiteDAO;
import java.util.List;
import model.Pedido;
import org.sql2o.Connection;

public class PedidoDAO {
    
    /**
     * Retorna todos los pedidos.
     *
     * @param idUsuario
     * @return A {@code List<Pedido>}.
     */
    public List<Pedido> selectAllxId(int idUsuario) {
        String query = "SELECT * FROM pedido WHERE idUsuario = :idUsuario";

        try (Connection con = SQLiteDAO.getConn().open()) {
            List<Pedido> pedidos = con
                    .createQuery(query)
                    .addParameter("idUsuario", idUsuario)
                    .executeAndFetch(Pedido.class);
            return pedidos;
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
    
    public void update(Pedido ped) {
        String query = "UPDATE pedido SET estado = :estado WHERE id = :id";
        
        try (Connection con = SQLiteDAO.getConn().open()) {
            con.createQuery(query).bind(ped).executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public Pedido getPedido(int id) {
        String query = "SELECT * FROM pedido WHERE id = :id";

        try (Connection con = SQLiteDAO.getConn().open()) {
            Pedido pedido = con
                    .createQuery(query)
                    .addParameter("id", id)
                    .executeAndFetchFirst(Pedido.class);
            return pedido;
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }
    
    public int ultimoPedido() {
        String query = "SELECT MAX(id) AS id FROM pedido";

        try (Connection con = SQLiteDAO.getConn().open()) {
            Integer idUltimoPedido = con
                    .createQuery(query)
                    .executeAndFetchFirst(Integer.class);
            return idUltimoPedido;
        } catch (Exception e) {
            System.out.println(e);
        }

        return -1;
    }
}