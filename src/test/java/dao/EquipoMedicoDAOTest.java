package dao;

import java.util.List;
import model.EquipoMedico;
import org.junit.Test;
import static org.junit.Assert.*;

public class EquipoMedicoDAOTest {
    
    public EquipoMedicoDAOTest() {
    }

    @Test
    public void testSelectAll() {
        System.out.println("selectAll");
        
        EquipoMedicoDAO instance = new EquipoMedicoDAO();
        List<EquipoMedico> result = instance.selectAll();
        
        assertNotNull("pass", result);
    }

    @Test
    public void testGetMedEq() {
        System.out.println("getMedEq");
        
        EquipoMedicoDAO instance = new EquipoMedicoDAO();
        EquipoMedico result = instance.getMedEq(5);
        
        assertNotNull("pass", result);
    }

    @Test
    public void testMedEqWithLowStock() {
        System.out.println("medEqWithLowStock");
        
        EquipoMedicoDAO instance = new EquipoMedicoDAO();
        List<EquipoMedico> result = instance.medEqWithLowStock();
        
        assertNotNull("pass", result);
    }

    @Test
    public void testInsert() {
        System.out.println("insert");
        
        EquipoMedicoDAO instance = new EquipoMedicoDAO();
        
        // caso exitoso
        EquipoMedico me1 = new EquipoMedico("Test Insert ME", 10);
        boolean result1 = instance.insert(me1);
        
        assertTrue("pass", result1);
    }
    
    @Test
    public void testGetMedEqByName() {
        System.out.println("getMedEq");
        
        EquipoMedicoDAO instance = new EquipoMedicoDAO();
        EquipoMedico result = instance.getMedEqByName("Test Insert ME");
        
        assertNotNull("pass", result);
    }

    @Test
    public void testUpdate() {
        System.out.println("update");
        
        EquipoMedicoDAO instance = new EquipoMedicoDAO();
        
        // caso exitoso
        EquipoMedico me = instance.getMedEqByName("Test Insert ME");
        me.setNombre("Test Update ME");
        me.setStock(3);
        boolean result1 = instance.update(me);
        
        assertTrue("pass", result1);
    }

    @Test
    public void testDelete() {
        System.out.println("delete");
        
        EquipoMedicoDAO instance = new EquipoMedicoDAO();
        
        // caso exitoso
        EquipoMedico me = instance.getMedEqByName("Test Update ME");
        boolean result = instance.delete(me.getId());
        
        assertTrue("pass", result);
    }
}
