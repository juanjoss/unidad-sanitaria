package util;

import dao.DetallePedidoEMDAO;
import dao.DetallePedidoMDAO;
import dao.EquipoMedicoDAO;
import javax.swing.ListCellRenderer;

import dao.MedicamentoDAO;

import javax.swing.JLabel; 
import javax.swing.DefaultListCellRenderer;
import java.awt.Color; import javax.swing.JList; import java.awt.Component; 
import java.awt.Font;
import java.util.HashMap; 
import model.EquipoMedico;
import model.Medicamento;

public class PintarLista extends DefaultListCellRenderer {
    
    public Component getListCellRendererComponent(JList list,Object value,int index,boolean isSelected, boolean cellHasFocus) {
        Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
        if (component != null && value != null) {
            String descripcion = value.toString(); //descr = value
            String[] partes = descripcion.split("-");

            Medicamento m = null;
            EquipoMedico em = null;
            MedicamentoDAO medicamentoDAO = new MedicamentoDAO();
            EquipoMedicoDAO equipoMedicoDAO = new EquipoMedicoDAO();
            DetallePedidoMDAO dpmDAO = new DetallePedidoMDAO();
            DetallePedidoEMDAO dpemDAO = new DetallePedidoEMDAO();

            int id_usuario = SesionUsuario.getInstance().getLoggedUser().getId();

            Color resaltador = new Color(153, 102, 0);

            if (partes.length == 3) {  //Se controla si es un medicamento o un equipo médico
                m = medicamentoDAO.buscarPorNombrePresentacion(partes[0].trim(), partes[1].trim(), partes[2].trim());
                if (m != null) {
                    if (dpmDAO.selectMedDetalles(id_usuario, m.getId()) != null) {
                        component.setBackground(resaltador);
                    }
                }
            }
            else {
                em = equipoMedicoDAO.buscarPorNombre(partes[0].trim());
                if (em != null) {
                    if (dpemDAO.selectEMDetalles(id_usuario, em.getId()) != null) {
                        component.setBackground(resaltador);
                    }
                }
            }
        }
        return component;
    }
}

