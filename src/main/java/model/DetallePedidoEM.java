package model;

import lombok.Data;

@Data
public class DetallePedidoEM {

    private int id;
    private int cantidad;
    private String descripcion;
    private int pedido_id;
    private int equipoMedico_id;
}