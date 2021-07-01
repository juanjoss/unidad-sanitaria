
import dao.EquipoMedicoDAO;
import java.awt.event.KeyEvent;
import model.EquipoMedico;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.DialogFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ui.MainFrame;
import util.SesionUsuario;

public class AddMEDialogTest {

    private FrameFixture window;

    @Before
    public void setUp() {
        SesionUsuario.getInstance().setLoggedUser(null);
        MainFrame frame = GuiActionRunner.execute(() -> new MainFrame());
        window = new FrameFixture(frame);
        window.show();
    }

    @Test
    public void InsertMedEqSuccess() {
        // ARRANGE
        String usernameTest = "Administracion";
        String passTest = "admin";
        String name = "Barbijo";
        int stock = 10;
        EquipoMedicoDAO meDAO = new EquipoMedicoDAO();

        // PRE
        EquipoMedico me = meDAO.getMedEqByName(name);
        if (me != null) {
            meDAO.delete(me.getId());
        }

        // ACT
        window.textBox("usserTextField").deleteText().enterText(usernameTest);
        window.textBox("passwordField").deleteText().enterText(passTest);
        window.button("loginInButton").click();

        window.tabbedPane("mainTabbedPane").selectTab("Equipo Médico");
        window.button("addMEBtn").click();

        DialogFixture dialog = WindowFinder.findDialog("AddMEDialog").using(window.robot());

        dialog.textBox("nameTF").deleteText().enterText(name);
        dialog.textBox("stockTF").deleteText().enterText(Integer.toString(stock));
        dialog.button("addBtn").click();

        window.robot().pressKey(KeyEvent.VK_ENTER);

        // ASSERT
        assertNotNull(meDAO.buscarPorNombre(name));
    }

    @Test
    public void InsertMedEqFailed() {
        // ARRANGE
        String usernameTest = "Administracion";
        String passTest = "admin";
        String name = "Barbijo";
        int stock = -1;
        EquipoMedicoDAO meDAO = new EquipoMedicoDAO();

        // PRE
        EquipoMedico me = meDAO.getMedEqByName(name);
        if (me != null) {
            meDAO.delete(me.getId());
        }

        // ACT
        window.textBox("usserTextField").deleteText().enterText(usernameTest);
        window.textBox("passwordField").deleteText().enterText(passTest);
        window.button("loginInButton").click();
        window.tabbedPane("mainTabbedPane").selectTab("Equipo Médico");
        window.button("addMEBtn").click();

        DialogFixture dialog = WindowFinder.findDialog("AddMEDialog").using(window.robot());

        dialog.textBox("nameTF").deleteText().enterText(name);
        dialog.textBox("stockTF").deleteText().enterText(Integer.toString(stock));
        dialog.button("addBtn").click();

        window.robot().pressKey(KeyEvent.VK_ENTER);

        // ASSERT
        assertNull(meDAO.buscarPorNombre(name));
    }

    @After
    public void tearDown() {
        window.cleanUp();
    }
}
