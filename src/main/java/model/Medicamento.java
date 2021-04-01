package model;

import lombok.Data;

/**
 *
 * @author Juan Josserand
 */
@Data
public class Medicamento {
    private String nombre;
    private int stock;
    private String fechaVencimiento;
}
