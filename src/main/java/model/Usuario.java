package model;

import lombok.Data;

@Data
public class Usuario {

    private int id;
    private String pass;
    private String userName;
    private String ultimaSesion;
    private String email;

}
