package model;

import lombok.Data;

@Data
public class Pedido {

    private int id;
    private int idUsuario;
    private String correoProvedor;
    private String fecha;
    private String estado;
}