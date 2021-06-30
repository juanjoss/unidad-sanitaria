import static org.junit.Assert.assertEquals;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ui.MainFrame;
import util.SesionUsuario;


public class GUITests {
    private FrameFixture window;

    @Before
    public void setUp() {
        SesionUsuario.getInstance().setLoggedUser(null);
        MainFrame frame = GuiActionRunner.execute(() -> new MainFrame());
        window = new FrameFixture(frame);
        window.show();
    }

    @Test
    public void LoginSuccessTest() {
        // ARRANGE
        String usernameTest = "Administrador";
        String passTest = "admin";

        // ACT
        window.textBox("usserTextField").deleteText().enterText(usernameTest);
        window.textBox("passwordField").deleteText().enterText(passTest);
        window.button("loginInButton").click();

        // ASSERT
        assertEquals(true, SesionUsuario.getInstance().isLogged());
    }

    @Test
    public void LoginFailTest() {
        // ARRANGE
        String usernameTest = "usuarioErroneo";
        String passTest = "contraseñaErronea";

        // ACT
        window.textBox("usserTextField").deleteText().enterText(usernameTest);
        window.textBox("passwordField").deleteText().enterText(passTest);
        window.button("loginInButton").click();

        // ASSERT
        assertEquals(false, SesionUsuario.getInstance().isLogged());
    }

    @After
    public void tearDown() {
        window.cleanUp();
    }
}
