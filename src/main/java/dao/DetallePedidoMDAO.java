package dao;

import db.SQLiteDAO;
import org.sql2o.Connection;
import java.util.List;
import model.*;
import util.DateUtil;

public class DetallePedidoMDAO {
        /**
     * Retorna todos los Detalles de pedido medicamento.
     *
     * @return A {@code List<Medicameto>}.
     */
    public List<DetallePedidoM> selectAllxId(int pedido_id) {
        String query = "SELECT detallePedidoM.descripcion, detallePedidoM.cantidad"
        + " FROM detallePedidoM JOIN pedido ON pedido.id = detallePedidoM.pedido_id AND pedido.id = :pedido_id;";

        try (Connection con = SQLiteDAO.getConn().open()) {
            List<DetallePedidoM> medicamentos = con
                    .createQuery(query)
                    .addParameter("pedido_id", pedido_id)
                    .executeAndFetch(DetallePedidoM.class);
            return medicamentos;
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

    /**
    * Inserta un detalle pedido de un equipo médico en la BD.
    *
    * @param med A {@code Medicamento} el medicamento a insertar.
    */
    public void insert(DetallePedidoM detallePedidoM) {
        String query = "INSERT INTO detallePedidoM (cantidad, descripcion, pedido_id, medicameto_id) "
                + "VALUES (:cantidad, :descripcion, :pedido_id, :medicamento_id)";

        try (Connection con = SQLiteDAO.getConn().open()) {
            con.createQuery(query).bind(detallePedidoM).executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}