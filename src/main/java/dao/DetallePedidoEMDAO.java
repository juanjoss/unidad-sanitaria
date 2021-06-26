package dao;

import db.SQLiteDAO;
import org.sql2o.Connection;
import java.util.List;
import model.DetallePedidoEM;

public class DetallePedidoEMDAO {
    
    /**
     * Retorna todos los detalles pedidos de equipos médicos de un pedido.
     *
     * @param pedido_id {@code Integer} ID del pedido.
     * @return A {@code List<DetallePedidoEM>}.
     */
    public List<DetallePedidoEM> selectXPedidoId(int pedido_id) {
        String query = "SELECT detallePedidoEM.descripcion, detallePedidoEM.cantidad "
                + "FROM detallePedidoEM WHERE detallePedidoEM.pedido_id = :pedido_id;";

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
        String query = "INSERT INTO detallePedidoEM (cantidad, descripcion, "
                + "pedido_id, equipoMedico_id) VALUES (:cantidad, :descripcion, "
                + ":pedido_id, :equipoMedico_id)";

        try (Connection con = SQLiteDAO.getConn().open()) {
            con.createQuery(query).bind(dpem).executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    /**
     * Retorna una lista de detalles de equipo médico que corresponden a un 
     * pedido hecho por el usuario loggeado y que tiene el estado 'Enviado'
     *
     * @param id_usuario {@code Integer} ID del usuario.
     * @param equipoMedico_id {@code Integer} ID del equipo médico.
     * @return A {@code List<DetallePedidoEM>}.
     */
    public List<DetallePedidoEM> selectEMDetalles(int id_usuario, int equipoMedico_id) {
        String query = "SELECT pedido.id FROM detallePedidoEM, pedido WHERE "
                + "detallePedidoEM.pedido_id = pedido.id AND "
                + "pedido.idUsuario = :id_usuario AND "
                + "detallePedidoEM.equipoMedico_id = :equipoMedico_id AND "
                + "UPPER(pedido.estado) LIKE 'ENVIADO';";

        try (Connection con = SQLiteDAO.getConn().open()) {
            List<DetallePedidoEM> detallesEM = con
                    .createQuery(query)
                    .addParameter("id_usuario", id_usuario)
                    .addParameter("equipoMedico_id", equipoMedico_id)
                    .executeAndFetch(DetallePedidoEM.class);
            if (detallesEM.size() > 0)
                return detallesEM;
        } catch (Exception e) {
            System.out.println(e);
        }
        
        return null;
    }
}