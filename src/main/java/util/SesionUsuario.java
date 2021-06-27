package util;

import model.Usuario;

public class SesionUsuario {
    
    private static SesionUsuario sesion;
    private Usuario usuario;

    public static SesionUsuario getInstance() {
        if (sesion == null) {
            sesion = new SesionUsuario();
        }

        return sesion;
    }

    private SesionUsuario() {
        usuario = null;
    }

    /**
     * Setea usuario en sesion
     *
     * @param user {@code User}.
     */
    public void setLoggedUser(Usuario user) {
        usuario = user;
    }

    /**
     * Retorna usuario en sesion
     *
     * @return Un {@code Usuario} en caso de haber iniciado sesion o {@code null} en otro caso.
     */
    public Usuario getLoggedUser() {
        return usuario;
    }

    /**
     *
     * @return Un {@code boolean} {@code true} en caso de haber iniciado sesion, {@code false} en otro caso.
     */
    public boolean isLogged() {
        return usuario != null;
    }
}
