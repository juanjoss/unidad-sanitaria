package model;

import lombok.Data;


@Data
public class DetallePedidoM {

    private int id;
    private int cantidad;
    private String descripcion;
    private int pedido_id;
    private int medicamento_id;

    public DetallePedidoM(int id, int cantidad, String descripcion, int pedido_id, int medicamento_id) {
        this.id = id;
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.pedido_id = pedido_id;
        this.medicamento_id = medicamento_id;
    }


}
