import static org.junit.Assert.assertTrue;

import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ui.MainFrame;


public class GUITests {
    private FrameFixture window;

    @Before
    public void setUp() {
        MainFrame frame = GuiActionRunner.execute(() -> new MainFrame());
        window = new FrameFixture(frame);
        window.show();
    }

    @Test
    public void LoginSuccessTest() {

        String usernameTest = "Administracion";
        String passTest = "admin";

        window.textBox("usserTextField").deleteText().enterText(usernameTest);
        window.textBox("passwordField").deleteText().enterText(passTest);
        window.button("loginInButton").click();

        // ASSERT
        try {
            window.textBox("searchBar").requireVisible();
            assertTrue(true);
        } catch (Exception e) {
            assertTrue(false);
        }
        
        /**
         * Con try-catch, porque si no hace match con un componente
         * tira una excepción.
         */
    }

    @Test
    public void LoginFailTest() {

        String usernameTest = "usuarioErroneo";
        String passTest = "contraseñaErronea";

        window.textBox("usserTextField").deleteText().enterText(usernameTest);
        window.textBox("passwordField").deleteText().enterText(passTest);
        window.button("loginInButton").click();
        
        // ASSERT
        try {
            window.textBox("searchBar").requireVisible();
            assertTrue(false);
        } catch (Exception e) {
            assertTrue(true);
        }
        window.cleanUp();
    }

    @After
    public void tearDown() {
        window.cleanUp();
    }
}
