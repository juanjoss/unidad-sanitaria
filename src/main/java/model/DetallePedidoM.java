package model;

import lombok.Data;


@Data
public class DetallePedidoM {

    private int id;
    private int cantidad;
    private String descripcion;
    private int pedido_id;
    private int medicamento_id;

}
