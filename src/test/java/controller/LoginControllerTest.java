package controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private MainController mainControllerMock;

    private LoginController controller;

    @BeforeEach
    void setUp() {
        controller = new LoginController();
        controller.setControlador(mainControllerMock);
    }

    @Test
    void testLoginExitoso() {
        when(mainControllerMock.loginAsesor("A01")).thenReturn("OK|Bienvenido Juan");

        String resultado = mainControllerMock.loginAsesor("A01");

        assertTrue(resultado.startsWith("OK"));
        verify(mainControllerMock, times(1)).loginAsesor("A01");
    }

    @Test
    void testLoginFallido() {
        when(mainControllerMock.loginAsesor("X99")).thenReturn("ERROR|ID no encontrado");

        String resultado = mainControllerMock.loginAsesor("X99");

        assertTrue(resultado.startsWith("ERROR"));
    }

    @Test
    void testLoginConIdVacio() {
        when(mainControllerMock.loginAsesor("")).thenReturn("ERROR|Ingrese un ID de asesor.");

        String resultado = mainControllerMock.loginAsesor("");

        assertTrue(resultado.startsWith("ERROR"));
        verify(mainControllerMock, times(1)).loginAsesor("");
    }
}