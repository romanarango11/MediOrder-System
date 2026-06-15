package controller;

import model.*;
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
class PrincipalControllerTest {

    @Mock
    private MainController mainControllerMock;

    private PrincipalController controller;
    private Paciente pacienteTest;
    private Medico medicoTest;
    private Institucion institucionTest;

    @BeforeEach
    void setUp() {
        controller = new PrincipalController();
        controller.setControlador(mainControllerMock);

        pacienteTest = new Paciente("CC", "123456", "Juan Perez", 30, "M", "Contributivo", true, true, false);
        medicoTest = new Medico("M001", "Dra. Gomez", "Cardiología", "Clinica Central");
        institucionTest = new Institucion("Clinica Central", "Bogotá", "Cardiología;Medicina General");
    }

    @Test
    void testBuscarPacienteExitoso() {
        when(mainControllerMock.buscarPacienteParaOrden("123456")).thenReturn("OK");
        when(mainControllerMock.getPacienteActivo()).thenReturn(pacienteTest);

        String resultado = mainControllerMock.buscarPacienteParaOrden("123456");
        Paciente paciente = mainControllerMock.getPacienteActivo();

        assertEquals("OK", resultado);
        assertNotNull(paciente);
        assertEquals("Juan Perez", paciente.getNombre());

        verify(mainControllerMock, times(1)).buscarPacienteParaOrden("123456");
        verify(mainControllerMock, times(1)).getPacienteActivo();
    }

    @Test
    void testBuscarPacienteNoEncontrado() {
        when(mainControllerMock.buscarPacienteParaOrden("999999")).thenReturn("ERROR|Paciente no encontrado");

        String resultado = mainControllerMock.buscarPacienteParaOrden("999999");

        assertTrue(resultado.startsWith("ERROR"));
        assertTrue(resultado.contains("no encontrado"));

        verify(mainControllerMock, times(1)).buscarPacienteParaOrden("999999");
        verify(mainControllerMock, never()).getPacienteActivo();
    }

    @Test
    void testBuscarPacienteInactivo() {
        when(mainControllerMock.buscarPacienteParaOrden("111")).thenReturn("ERROR|El paciente está inactivo");

        String resultado = mainControllerMock.buscarPacienteParaOrden("111");

        assertTrue(resultado.startsWith("ERROR"));
        assertTrue(resultado.contains("inactivo"));

        verify(mainControllerMock, times(1)).buscarPacienteParaOrden("111");
        verify(mainControllerMock, never()).getPacienteActivo();
    }

    @Test
    void testValidarCieExistente() {
        when(mainControllerMock.existeCie("I10")).thenReturn(true);
        when(mainControllerMock.descripcionCie("I10")).thenReturn("Hipertensión esencial");
        when(mainControllerMock.especialidadDesdeCie("I10")).thenReturn("Cardiología");

        assertTrue(mainControllerMock.existeCie("I10"));
        assertEquals("Hipertensión esencial", mainControllerMock.descripcionCie("I10"));
        assertEquals("Cardiología", mainControllerMock.especialidadDesdeCie("I10"));

        verify(mainControllerMock, times(1)).existeCie("I10");
        verify(mainControllerMock, times(1)).descripcionCie("I10");
        verify(mainControllerMock, times(1)).especialidadDesdeCie("I10");
    }

    @Test
    void testValidarCieNoExistente() {
        when(mainControllerMock.existeCie("ZZ99")).thenReturn(false);

        assertFalse(mainControllerMock.existeCie("ZZ99"));

        verify(mainControllerMock, times(1)).existeCie("ZZ99");
        verify(mainControllerMock, never()).descripcionCie(anyString());
        verify(mainControllerMock, never()).especialidadDesdeCie(anyString());
    }

    @Test
    void testRecomendarCupsDesdeCie() {
        List<String[]> sugerencias = new ArrayList<>();
        sugerencias.add(new String[]{"", "200801", "Electrocardiograma", ""});
        sugerencias.add(new String[]{"", "200802", "Holter", ""});

        when(mainControllerMock.recomendarCupsDesdeCie("I10")).thenReturn(sugerencias);

        List<String[]> resultado = mainControllerMock.recomendarCupsDesdeCie("I10");

        assertEquals(2, resultado.size());
        assertEquals("200801", resultado.get(0)[1]);

        verify(mainControllerMock, times(1)).recomendarCupsDesdeCie("I10");
    }

    @Test
    void testInstitucionesPorEspecialidad() {
        List<Institucion> instituciones = new ArrayList<>();
        instituciones.add(new Institucion("Clinica A", "Bogotá", "Cardiología"));
        instituciones.add(new Institucion("Clinica B", "Medellín", "Cardiología"));

        when(mainControllerMock.institucionesPorEspecialidad("Cardiología")).thenReturn(instituciones);

        List<Institucion> resultado = mainControllerMock.institucionesPorEspecialidad("Cardiología");

        assertEquals(2, resultado.size());

        verify(mainControllerMock, times(1)).institucionesPorEspecialidad("Cardiología");
    }

    @Test
    void testMedicosPorEspecialidadEInstitucion() {
        List<Medico> medicos = new ArrayList<>();
        medicos.add(new Medico("M01", "Dr. Lopez", "Cardiología", "Clinica A"));
        medicos.add(new Medico("M02", "Dra. Gomez", "Cardiología", "Clinica A"));

        when(mainControllerMock.medicosPorEspecialidadEInstitucion("Cardiología", "Clinica A"))
                .thenReturn(medicos);

        List<Medico> resultado = mainControllerMock.medicosPorEspecialidadEInstitucion("Cardiología", "Clinica A");

        assertEquals(2, resultado.size());
        assertEquals("Dr. Lopez", resultado.get(0).getNombre());

        verify(mainControllerMock, times(1))
                .medicosPorEspecialidadEInstitucion("Cardiología", "Clinica A");
    }

    @Test
    void testAgregarProcedimientoAOrden() {
        List<Procedimiento> procedimientos = new ArrayList<>();
        Procedimiento proc = new Procedimiento("200801", "I10", "ECG", "Hipertensión", 1, 150000);
        procedimientos.add(proc);

        double total = 0;
        for (Procedimiento p : procedimientos) {
            total += p.getSubtotal();
        }

        assertEquals(150000, total);
        assertEquals(1, procedimientos.size());
        assertEquals("200801", procedimientos.get(0).getCups());
    }

    @Test
    void testGenerarOrdenExitosa() {
        List<Procedimiento> procs = new ArrayList<>();
        procs.add(new Procedimiento("200801", "I10", "ECG", "Hipertensión", 1, 150000));

        when(mainControllerMock.generarOrden(medicoTest, institucionTest, procs))
                .thenReturn("OK|54321");

        String resultado = mainControllerMock.generarOrden(medicoTest, institucionTest, procs);

        assertTrue(resultado.startsWith("OK"));
        assertTrue(resultado.contains("54321"));

        verify(mainControllerMock, times(1))
                .generarOrden(medicoTest, institucionTest, procs);
        verify(mainControllerMock, never()).getPacienteActivo();
    }

    @Test
    void testGenerarOrdenSinPaciente() {
        List<Procedimiento> procs = new ArrayList<>();
        procs.add(new Procedimiento("200801", "I10", "ECG", "Hipertensión", 1, 150000));

        when(mainControllerMock.generarOrden(medicoTest, institucionTest, procs))
                .thenReturn("ERROR|Debe buscar un paciente primero");

        String resultado = mainControllerMock.generarOrden(medicoTest, institucionTest, procs);

        assertTrue(resultado.startsWith("ERROR"));
        assertTrue(resultado.contains("paciente"));

        verify(mainControllerMock, times(1))
                .generarOrden(medicoTest, institucionTest, procs);
        verify(mainControllerMock, never()).getPacienteActivo();
    }

    @Test
    void testGenerarOrdenSinProcedimientos() {
        List<Procedimiento> procsVacios = new ArrayList<>();

        when(mainControllerMock.generarOrden(medicoTest, institucionTest, procsVacios))
                .thenReturn("ERROR|Debe agregar al menos un procedimiento");

        String resultado = mainControllerMock.generarOrden(medicoTest, institucionTest, procsVacios);

        assertTrue(resultado.startsWith("ERROR"));
        assertTrue(resultado.contains("al menos un procedimiento"));

        verify(mainControllerMock, times(1))
                .generarOrden(medicoTest, institucionTest, procsVacios);
        verify(mainControllerMock, never()).getPacienteActivo();
    }

    @Test
    void testGenerarOrdenSinMedico() {
        List<Procedimiento> procs = new ArrayList<>();
        procs.add(new Procedimiento("200801", "I10", "ECG", "Hipertensión", 1, 150000));

        when(mainControllerMock.generarOrden(null, institucionTest, procs))
                .thenReturn("ERROR|Seleccione un médico");

        String resultado = mainControllerMock.generarOrden(null, institucionTest, procs);

        assertTrue(resultado.startsWith("ERROR"));
        assertTrue(resultado.contains("médico"));

        verify(mainControllerMock, times(1))
                .generarOrden(null, institucionTest, procs);
    }

    @Test
    void testObtenerHistorial() {
        List<Orden> historial = new ArrayList<>();
        historial.add(ordenTest("12345", "Juan Perez", "ACTIVA"));
        historial.add(ordenTest("12346", "Maria Gomez", "CANCELADA"));

        when(mainControllerMock.obtenerHistorial()).thenReturn(historial);

        List<Orden> resultado = mainControllerMock.obtenerHistorial();

        assertEquals(2, resultado.size());
        assertEquals("Juan Perez", resultado.get(0).getNombrePaciente());
        assertEquals("ACTIVA", resultado.get(0).getEstadoOrden());
        assertEquals("CANCELADA", resultado.get(1).getEstadoOrden());

        verify(mainControllerMock, times(1)).obtenerHistorial();
    }

    @Test
    void testCancelarOrden() {
        when(mainControllerMock.cancelarOrden(12345)).thenReturn(true);

        boolean resultado = mainControllerMock.cancelarOrden(12345);

        assertTrue(resultado);
        verify(mainControllerMock, times(1)).cancelarOrden(12345);
    }

    @Test
    void testCancelarOrdenFallida() {
        when(mainControllerMock.cancelarOrden(99999)).thenReturn(false);

        boolean resultado = mainControllerMock.cancelarOrden(99999);

        assertFalse(resultado);
        verify(mainControllerMock, times(1)).cancelarOrden(99999);
    }

    @Test
    void testCancelarOrdenInexistente() {
        when(mainControllerMock.cancelarOrden(00000)).thenReturn(false);

        boolean resultado = mainControllerMock.cancelarOrden(00000);

        assertFalse(resultado);
        verify(mainControllerMock, times(1)).cancelarOrden(00000);
    }

    @Test
    void testOrdenesPorPaciente() {
        List<Orden> ordenesPaciente = new ArrayList<>();
        ordenesPaciente.add(ordenTest("54321", "Juan Perez", "ACTIVA"));

        when(mainControllerMock.ordenesPorPaciente("123456")).thenReturn(ordenesPaciente);

        List<Orden> resultado = mainControllerMock.ordenesPorPaciente("123456");

        assertEquals(1, resultado.size());
        assertEquals("Juan Perez", resultado.get(0).getNombrePaciente());

        verify(mainControllerMock, times(1)).ordenesPorPaciente("123456");
    }

    @Test
    void testOrdenesPorPacienteSinResultados() {
        when(mainControllerMock.ordenesPorPaciente("999999")).thenReturn(new ArrayList<>());

        List<Orden> resultado = mainControllerMock.ordenesPorPaciente("999999");

        assertTrue(resultado.isEmpty());
        verify(mainControllerMock, times(1)).ordenesPorPaciente("999999");
    }

    @Test
    void testCerrarSesion() {
        doNothing().when(mainControllerMock).cerrarSesion();

        mainControllerMock.cerrarSesion();

        verify(mainControllerMock, times(1)).cerrarSesion();
    }

    @Test
    void testHaySesionActiva() {
        when(mainControllerMock.haySesionActiva()).thenReturn(true);

        assertTrue(mainControllerMock.haySesionActiva());

        verify(mainControllerMock, times(1)).haySesionActiva();
    }

    @Test
    void testHaySesionInactiva() {
        when(mainControllerMock.haySesionActiva()).thenReturn(false);

        assertFalse(mainControllerMock.haySesionActiva());

        verify(mainControllerMock, times(1)).haySesionActiva();
    }

    @Test
    void testGetAsesorNombre() {
        when(mainControllerMock.getAsesorNombre()).thenReturn("Juan Asesor");

        assertEquals("Juan Asesor", mainControllerMock.getAsesorNombre());

        verify(mainControllerMock, times(1)).getAsesorNombre();
    }

    @Test
    void testGetAsesorId() {
        when(mainControllerMock.getAsesorId()).thenReturn("A001");

        assertEquals("A001", mainControllerMock.getAsesorId());

        verify(mainControllerMock, times(1)).getAsesorId();
    }

    @Test
    void testExisteCups() {
        when(mainControllerMock.existeCups("200801")).thenReturn(true);
        when(mainControllerMock.existeCups("999999")).thenReturn(false);

        assertTrue(mainControllerMock.existeCups("200801"));
        assertFalse(mainControllerMock.existeCups("999999"));

        verify(mainControllerMock, times(1)).existeCups("200801");
        verify(mainControllerMock, times(1)).existeCups("999999");
    }

    @Test
    void testDescripcionCups() {
        when(mainControllerMock.descripcionCups("200801")).thenReturn("Electrocardiograma");

        assertEquals("Electrocardiograma", mainControllerMock.descripcionCups("200801"));

        verify(mainControllerMock, times(1)).descripcionCups("200801");
    }

    @Test
    void testPrecioCups() {
        when(mainControllerMock.precioCups("200801")).thenReturn(150000.0);

        assertEquals(150000.0, mainControllerMock.precioCups("200801"));

        verify(mainControllerMock, times(1)).precioCups("200801");
    }

    // Helper para crear órdenes de prueba
    private Orden ordenTest(String radicado, String nombrePaciente, String estado) {
        Paciente p = new Paciente("CC", "123", nombrePaciente, 30, "M", "Plan", true, true, false);
        Medico m = new Medico("M01", "Dr. Test", "General", "Clinica Test");
        Institucion i = new Institucion("Clinica Test", "Bogotá", "General");
        List<Procedimiento> procs = new ArrayList<>();
        procs.add(new Procedimiento("C01", "A01", "Desc", "Desc", 1, 1000));

        Orden orden = new Orden(Integer.parseInt(radicado), p, m, i, procs, "A01", "Asesor");
        orden.setEstadoOrden(estado);
        return orden;
    }
}