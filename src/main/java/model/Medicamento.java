package model;

import lombok.Data;

@Data
public class Medicamento {

    private int id;
    private String nombre;
    private int stock;
    private String fechaVencimiento;
}
