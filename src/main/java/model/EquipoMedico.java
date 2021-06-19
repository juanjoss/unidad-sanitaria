package model;

import lombok.Data;

@Data
public class EquipoMedico {

    private int id;
    private String nombre;
    private int stock;
    
    public EquipoMedico() {}
    
    public EquipoMedico(String nombre, int stock) {
        this.nombre = nombre;
        this.stock = stock;
    }
}
