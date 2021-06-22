package dao;

import db.SQLiteDAO;
import org.sql2o.Connection;
import java.util.List;
import model.DetallePedidoEM;
import util.DateUtil;

public class DetallePedidoEMDAO {
    
    /**
     * Retorna todos los detalles pedidos de equipos médicos de un pedido.
     *
     * @param pedido_id {@code Integer} ID del pedido.
     * @return A {@code List<DetallePedidoEM>}.
     */
    public List<DetallePedidoEM> selectXPedidoId(int pedido_id) {
        String query = "SELECT detallePedidoEM.descripcion, detallePedidoEM.cantidad FROM detallePedidoEM WHERE detallePedidoEM.pedido_id = :pedido_id;";

        try (Connection con = SQLiteDAO.getConn().open()) {
            List<DetallePedidoEM> detallesEM = con
                    .createQuery(query)
                    .addParameter("pedido_id", pedido_id)
                    .executeAndFetch(DetallePedidoEM.class);
            return detallesEM;
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

    /**
     * Inserta un detalle pedido de un equipo médico en la BD.
     *
     * @param dpem {@code DetallePedidoEM} el detalle pedido a insertar.
     */
    public void insert(DetallePedidoEM dpem) {
        String query = "INSERT INTO detallePedidoEM (cantidad, descripcion, pedido_id, equipomedico_id) VALUES (:cantidad, :descripcion, :pedido_id, :equipomedico_id)";

        try (Connection con = SQLiteDAO.getConn().open()) {
            con.createQuery(query).bind(dpem).executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}