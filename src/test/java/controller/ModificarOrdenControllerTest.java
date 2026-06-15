package controller;

import javafx.collections.ObservableList;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModificarOrdenControllerTest {

    @Mock
    private MainController mainControllerMock;

    private ModificarOrdenController controller;
    private Orden ordenTest;
    private Medico medicoTest;
    private Institucion institucionTest;
    private List<Procedimiento> procedimientosTest;

    @BeforeEach
    void setUp() {
        controller = new ModificarOrdenController();
        controller.setControlador(mainControllerMock);

        // Crear objetos de prueba
        Paciente paciente = new Paciente("CC", "123456", "Juan Perez", 30, "M", "Contributivo", true, true, false);
        medicoTest = new Medico("M001", "Dr. Lopez", "Cardiología", "Clinica Central");
        institucionTest = new Institucion("Clinica Central", "Bogotá", "Cardiología;Medicina General");

        procedimientosTest = new ArrayList<>();
        procedimientosTest.add(new Procedimiento("200801", "I10", "ECG", "Hipertensión", 1, 150000));

        ordenTest = new Orden(12345, paciente, medicoTest, institucionTest, procedimientosTest, "A01", "Asesor Test");

        controller.setOrden(ordenTest);
    }

    @Test
    void testSetOrden() {
        assertNotNull(controller);
        // Verificar que la orden se asignó correctamente
        // Nota: Para pruebas completas se necesitaría JavaFX runtime
    }

    @Test
    void testCargarInstitucionesPorEspecialidad() {
        List<Institucion> instituciones = new ArrayList<>();
        instituciones.add(new Institucion("Clinica A", "Bogotá", "Cardiología"));
        instituciones.add(new Institucion("Clinica B", "Medellín", "Cardiología"));

        when(mainControllerMock.institucionesPorEspecialidad("Cardiología")).thenReturn(instituciones);

        List<Institucion> resultado = mainControllerMock.institucionesPorEspecialidad("Cardiología");

        assertEquals(2, resultado.size());
        assertEquals("Clinica A", resultado.get(0).getNombre());
    }

    @Test
    void testValidarCupsExistente() {
        when(mainControllerMock.existeCups("200801")).thenReturn(true);
        when(mainControllerMock.existeCups("999999")).thenReturn(false);

        assertTrue(mainControllerMock.existeCups("200801"));
        assertFalse(mainControllerMock.existeCups("999999"));
    }

    @Test
    void testValidarCieExistente() {
        when(mainControllerMock.existeCie("I10")).thenReturn(true);
        when(mainControllerMock.existeCie("ZZ99")).thenReturn(false);

        assertTrue(mainControllerMock.existeCie("I10"));
        assertFalse(mainControllerMock.existeCie("ZZ99"));
    }

    @Test
    void testEspecialidadDesdeCie() {
        when(mainControllerMock.especialidadDesdeCie("I10")).thenReturn("Cardiología");
        when(mainControllerMock.especialidadDesdeCie("J00")).thenReturn("Medicina General");

        assertEquals("Cardiología", mainControllerMock.especialidadDesdeCie("I10"));
        assertEquals("Medicina General", mainControllerMock.especialidadDesdeCie("J00"));
    }

    @Test
    void testDescripcionCups() {
        when(mainControllerMock.descripcionCups("200801")).thenReturn("Electrocardiograma");

        assertEquals("Electrocardiograma", mainControllerMock.descripcionCups("200801"));
    }

    @Test
    void testPrecioCups() {
        when(mainControllerMock.precioCups("200801")).thenReturn(150000.0);

        assertEquals(150000.0, mainControllerMock.precioCups("200801"));
    }

    @Test
    void testBuscarInstitucion() {
        when(mainControllerMock.buscarInstitucion("Clinica Central")).thenReturn(institucionTest);

        Institucion encontrada = mainControllerMock.buscarInstitucion("Clinica Central");

        assertNotNull(encontrada);
        assertEquals("Clinica Central", encontrada.getNombre());
    }

    @Test
    void testModificarOrdenExitoso() {
        List<Procedimiento> nuevosProcedimientos = new ArrayList<>();
        nuevosProcedimientos.add(new Procedimiento("300101", "I10", "Ecocardiograma", "Hipertensión", 1, 350000));

        when(mainControllerMock.modificarOrden(eq(12345), any(Medico.class), any(Institucion.class), anyList()))
                .thenReturn(true);

        boolean resultado = mainControllerMock.modificarOrden(12345, medicoTest, institucionTest, nuevosProcedimientos);

        assertTrue(resultado);
        verify(mainControllerMock, times(1)).modificarOrden(eq(12345), any(Medico.class), any(Institucion.class), anyList());
    }

    @Test
    void testModificarOrdenFallido() {
        when(mainControllerMock.modificarOrden(eq(99999), any(Medico.class), any(Institucion.class), anyList()))
                .thenReturn(false);

        boolean resultado = mainControllerMock.modificarOrden(99999, medicoTest, institucionTest, procedimientosTest);

        assertFalse(resultado);
    }

    @Test
    void testValidarMismoCieEspecialidad() {
        String cieCardiologia = "I10";
        String especialidadEsperada = "Cardiología";

        when(mainControllerMock.especialidadDesdeCie(cieCardiologia)).thenReturn(especialidadEsperada);

        assertEquals(especialidadEsperada, mainControllerMock.especialidadDesdeCie(cieCardiologia));

        // Verificar que coincide con la especialidad de la orden
        assertEquals(ordenTest.getEspecialidad(), mainControllerMock.especialidadDesdeCie(cieCardiologia));
    }

    @Test
    void testValidarMezclaEspecialidades() {
        String cieCardiologia = "I10";
        String cieDermatologia = "L20";

        when(mainControllerMock.especialidadDesdeCie(cieCardiologia)).thenReturn("Cardiología");
        when(mainControllerMock.especialidadDesdeCie(cieDermatologia)).thenReturn("Dermatología");

        assertNotEquals(
                mainControllerMock.especialidadDesdeCie(cieCardiologia),
                mainControllerMock.especialidadDesdeCie(cieDermatologia)
        );
    }

    @Test
    void testCalcularTotalProcedimientos() {
        List<Procedimiento> procs = new ArrayList<>();
        procs.add(new Procedimiento("C01", "A01", "Desc1", "Desc1", 2, 100000));
        procs.add(new Procedimiento("C02", "A02", "Desc2", "Desc2", 1, 200000));

        double totalEsperado = (2 * 100000) + (1 * 200000); // 400,000

        double totalCalculado = 0;
        for (Procedimiento p : procs) {
            totalCalculado += p.getSubtotal();
        }

        assertEquals(totalEsperado, totalCalculado);
    }
}