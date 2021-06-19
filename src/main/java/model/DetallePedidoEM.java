package model;

import lombok.Data;

@Data
public class DetallePedidoEM {

    private int id;
    private int cantidad;
    private String descripcion;
    private int pedido_id;
    private int equipoMedico_id;
    
    public DetallePedidoEM(int id, int cantidad, String descripcion, int pedido_id, int equipoMedico_id) {
        this.id = id;
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.pedido_id = pedido_id;
        this.equipoMedico_id = equipoMedico_id;
    }

}