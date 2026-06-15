package controller;

import model.Paciente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistroControllerTest {

    @Mock
    private MainController mainControllerMock;

    private RegistroController controller;

    @BeforeEach
    void setUp() {
        controller = new RegistroController();
        controller.setControlador(mainControllerMock);
    }

    @Test
    void testObtenerTodosPacientes() {
        List<Paciente> pacientesEsperados = new ArrayList<>();
        pacientesEsperados.add(new Paciente("CC", "111", "Ana", 25, "F", "Plan1", true, true, false));
        pacientesEsperados.add(new Paciente("CC", "222", "Luis", 30, "M", "Plan2", false, true, true));

        when(mainControllerMock.obtenerTodosPacientes()).thenReturn(pacientesEsperados);

        List<Paciente> resultado = mainControllerMock.obtenerTodosPacientes();

        assertEquals(2, resultado.size());
        assertEquals("Ana", resultado.get(0).getNombre());
    }

    @Test
    void testDocumentoYaRegistrado() {
        when(mainControllerMock.documentoYaRegistrado("123456")).thenReturn(true);
        when(mainControllerMock.documentoYaRegistrado("999999")).thenReturn(false);

        assertTrue(mainControllerMock.documentoYaRegistrado("123456"));
        assertFalse(mainControllerMock.documentoYaRegistrado("999999"));
    }

    @Test
    void testRegistrarPaciente() {
        Paciente nuevo = new Paciente("CC", "777", "Nuevo", 20, "F", "Plan", true, true, false);
        when(mainControllerMock.registrarPaciente(nuevo)).thenReturn(true);

        boolean resultado = mainControllerMock.registrarPaciente(nuevo);
        assertTrue(resultado);
        verify(mainControllerMock, times(1)).registrarPaciente(nuevo);
    }
}