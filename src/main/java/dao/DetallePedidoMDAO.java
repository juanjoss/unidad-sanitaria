package dao;

import db.SQLiteDAO;
import org.sql2o.Connection;
import java.util.List;
import model.*;
import util.DateUtil;

public class DetallePedidoMDAO {
    /**
     * Retorna todos los detalles pedidos de medicamentos de un pedido.
     *
     * @param pedido_id {@code Integer>} ID del pedido.
     * @return A {@code List<DetallePedidoM>}.
     */
    public List<DetallePedidoM> selectXPedidoId(int pedido_id) {
        String query = "SELECT detallePedidoM.descripcion, detallePedidoM.cantidad FROM detallePedidoM WHERE detallePedidoM.pedido_id = :pedido_id;";

        try (Connection con = SQLiteDAO.getConn().open()) {
            List<DetallePedidoM> detallesMed = con
                    .createQuery(query)
                    .addParameter("pedido_id", pedido_id)
                    .executeAndFetch(DetallePedidoM.class);
            return detallesMed;
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

    /**
     * Inserta un detalle pedido de un medicamento en la BD.
     *
     * @param detallePedidoM {@code DetallePedidoM} el detalle pedido a insertar.
     */
    public void insert(DetallePedidoM detallePedidoM) {
        String query = "INSERT INTO detallePedidoM (cantidad, descripcion, pedido_id, medicamento_id) VALUES (:cantidad, :descripcion, :pedido_id, :medicamento_id)";

        try (Connection con = SQLiteDAO.getConn().open()) {
            con.createQuery(query).bind(detallePedidoM).executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}