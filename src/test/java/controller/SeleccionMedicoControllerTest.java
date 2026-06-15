package controller;

import model.Medico;
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
class SeleccionMedicoControllerTest {

    @Mock
    private MainController mainControllerMock;

    private SeleccionMedicoController controller;

    @BeforeEach
    void setUp() {
        controller = new SeleccionMedicoController();
        controller.setControlador(mainControllerMock);
        controller.setFiltros("Cardiología", "Clinica Central");
    }

    @Test
    void testCargarMedicosExitoso() {
        List<Medico> medicos = new ArrayList<>();
        medicos.add(new Medico("M01", "Dr Lopez", "Cardiología", "Clinica Central"));
        medicos.add(new Medico("M02", "Dr Gomez", "Cardiología", "Clinica Central"));

        when(mainControllerMock.medicosPorEspecialidadEInstitucion("Cardiología", "Clinica Central"))
                .thenReturn(medicos);

        List<Medico> resultado = mainControllerMock.medicosPorEspecialidadEInstitucion("Cardiología", "Clinica Central");

        assertEquals(2, resultado.size());
        assertEquals("Dr Lopez", resultado.get(0).getNombre());
    }

    @Test
    void testCargarMedicosSinResultados() {
        when(mainControllerMock.medicosPorEspecialidadEInstitucion("Cardiología", "Clinica XYZ"))
                .thenReturn(new ArrayList<>());

        List<Medico> resultado = mainControllerMock.medicosPorEspecialidadEInstitucion("Cardiología", "Clinica XYZ");

        assertTrue(resultado.isEmpty());
    }
}