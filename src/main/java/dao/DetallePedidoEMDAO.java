package dao;

import db.SQLiteDAO;
import org.sql2o.Connection;
import java.util.List;
import model.DetallePedidoEM;

public class DetallePedidoEMDAO {
    
    /**
     * Retorna todos los detalles de un pedido que tengan que ver con un equipo médico.
     *
     * @param pedidoId {@code Integer} ID del pedido.
     * @return A {@code List<DetallePedidoEM>}.
     */
    public List<DetallePedidoEM> selectXPedidoId(int pedidoId) {
        String query = "SELECT detallePedidoEM.descripcion, detallePedidoEM.cantidad "
                + "FROM detallePedidoEM WHERE detallePedidoEM.pedido_id = :pedidoId;";

        try (Connection con = SQLiteDAO.getConn().open()) {
            List<DetallePedidoEM> detallesEM = con
                    .createQuery(query)
                    .addParameter("pedidoId", pedidoId)
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
     * Retorna una lista de detalles con equipos médicos que corresponden a un 
     * pedido hecho por el usuario loggeado y que tiene el estado 'Enviado'.
     *
     * @param idUsuario {@code Integer} ID del usuario.
     * @param equipoMedicoId {@code Integer} ID del equipo médico.
     * @return A {@code List<DetallePedidoEM>}.
     */
    public List<DetallePedidoEM> selectEMDetalles(int idUsuario, int equipoMedicoId) {
        String query = "SELECT pedido.id FROM detallePedidoEM, pedido WHERE "
                + "detallePedidoEM.pedido_id = pedido.id AND "
                + "pedido.idUsuario = :idUsuario AND "
                + "detallePedidoEM.equipoMedico_id = :equipoMedicoId AND "
                + "UPPER(pedido.estado) LIKE 'ENVIADO';";

        try (Connection con = SQLiteDAO.getConn().open()) {
            List<DetallePedidoEM> detallesEM = con
                    .createQuery(query)
                    .addParameter("idUsuario", idUsuario)
                    .addParameter("equipoMedicoId", equipoMedicoId)
                    .executeAndFetch(DetallePedidoEM.class);
            
            if (detallesEM.size() > 0)
                return detallesEM;
        } catch (Exception e) {
            System.out.println(e);
        }
        
        return null;
    }
}