package dao;

import db.SQLiteDAO;
import org.sql2o.Connection;
import java.util.List;
import model.*;

public class DetallePedidoMDAO {
    /**
     * Retorna todos los detalles pedidos de medicamentos de un pedido.
     *
     * @param pedido_id {@code Integer} ID del pedido.
     * @return A {@code List<DetallePedidoM>}.
     */
    public List<DetallePedidoM> selectXPedidoId(int pedido_id) {
        String query = "SELECT detallePedidoM.descripcion, detallePedidoM.cantidad "
                + "FROM detallePedidoM WHERE detallePedidoM.pedido_id = :pedido_id;";

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
     * Retorna una lista de detalles de medicamentos que corresponden a un 
     * pedido hecho por el usuario loggeado y que tiene el estado 'Enviado'
     *
     * @param id_usuario {@code Integer} ID del usuario.
     * @param medicamento_id {@code Integer} ID del medicamento.
     * @return A {@code List<DetallePedidoM>}.
     */
    public List<DetallePedidoM> selectMedDetalles(int id_usuario, int medicamento_id) {
        String query = "SELECT pedido.id FROM detallePedidoM, pedido WHERE "
                + "detallePedidoM.pedido_id = pedido.id AND "
                + "pedido.idUsuario = :id_usuario AND "
                + "detallePedidoM.medicamento_id = :medicamento_id "
                + "AND UPPER(pedido.estado) LIKE 'ENVIADO';";

        try (Connection con = SQLiteDAO.getConn().open()) {
            List<DetallePedidoM> detallesMed = con
                    .createQuery(query)
                    .addParameter("id_usuario", id_usuario)
                    .addParameter("medicamento_id", medicamento_id)
                    .executeAndFetch(DetallePedidoM.class);
            if (detallesMed.size() > 0)
                return detallesMed;
        } catch (Exception e) {
            System.out.println(e);
        }
        
        return null;
    }
}