package dao;

import db.SQLiteDAO;
import org.sql2o.Connection;
import java.util.List;
import model.*;

public class DetallePedidoMDAO {
    
    /**
     * Retorna todos los detalles de un pedido que tengan que ver con un medicamento.
     *
     * @param pedidoId {@code Integer} ID del pedido.
     * @return A {@code List<DetallePedidoM>}.
     */
    public List<DetallePedidoM> selectXPedidoId(int pedidoId) {
        String query = "SELECT detallePedidoM.descripcion, detallePedidoM.cantidad "
                + "FROM detallePedidoM WHERE detallePedidoM.pedido_id = :pedidoId;";

        try (Connection con = SQLiteDAO.getConn().open()) {
            List<DetallePedidoM> detallesMed = con
                    .createQuery(query)
                    .addParameter("pedidoId", pedidoId)
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
        String query = "INSERT INTO detallePedidoM (cantidad, descripcion, "
                + "pedido_id, medicamento_id) VALUES (:cantidad, :descripcion, "
                + ":pedido_id, :medicamento_id)";

        try (Connection con = SQLiteDAO.getConn().open()) {
            con.createQuery(query).bind(detallePedidoM).executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    /**
     * Retorna una lista de detalles con medicamentos que corresponden a un 
     * pedido hecho por el usuario loggeado y que tiene el estado 'Enviado'.
     *
     * @param idUsuario {@code Integer} ID del usuario.
     * @param medicamentoId {@code Integer} ID del medicamento.
     * @return A {@code List<DetallePedidoM>}.
     */
    public List<DetallePedidoM> selectMedDetalles(int idUsuario, int medicamentoId) {
        String query = "SELECT pedido.id FROM detallePedidoM, pedido WHERE "
                + "detallePedidoM.pedido_id = pedido.id AND "
                + "pedido.idUsuario = :idUsuario AND "
                + "detallePedidoM.medicamento_id = :medicamentoId "
                + "AND UPPER(pedido.estado) LIKE 'ENVIADO';";

        try (Connection con = SQLiteDAO.getConn().open()) {
            List<DetallePedidoM> detallesMed = con
                    .createQuery(query)
                    .addParameter("idUsuario", idUsuario)
                    .addParameter("medicamentoId", medicamentoId)
                    .executeAndFetch(DetallePedidoM.class);
            
            if (detallesMed.size() > 0)
                return detallesMed;
        } catch (Exception e) {
            System.out.println(e);
        }
        
        return null;
    }
}