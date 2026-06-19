package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import persistence.PersistenciaManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SistemaModelTest {

    private SistemaModel modelo;

    @Mock
    private PersistenciaManager persistenciaMock;

    @BeforeEach
    void setUp() throws Exception {
        modelo = new SistemaModel();
        // Usar reflexión para inyectar el mock en el modelo
        Field field = SistemaModel.class.getDeclaredField("persistencia");
        field.setAccessible(true);
        field.set(modelo, persistenciaMock);
    }

    //Login

    @Test
    void testLoginAsesorExitoso() {
        String[] asesorData = {"A01", "Juan Asesor"};
        when(persistenciaMock.buscarAsesor("A01")).thenReturn(asesorData);

        String[] resultado = modelo.loginAsesor("A01");

        assertNotNull(resultado);
        assertEquals("A01", resultado[0]);
        assertEquals("Juan Asesor", resultado[1]);
    }

    @Test
    void testLoginAsesorNoEncontrado() {
        when(persistenciaMock.buscarAsesor("X99")).thenReturn(null);

        String[] resultado = modelo.loginAsesor("X99");

        assertNull(resultado);
    }

    //Cups

    @Test
    void testExisteCups() {
        String[] cupsData = {"", "200801", "ECG", ""};
        when(persistenciaMock.buscarCups("200801")).thenReturn(cupsData);
        when(persistenciaMock.buscarCups("INVALIDO")).thenReturn(null);

        assertTrue(modelo.existeCups("200801"));
        assertFalse(modelo.existeCups("INVALIDO"));
    }

    @Test
    void testDescripcionCups() {
        String[] cupsData = {"", "200801", "Electrocardiograma", ""};
        when(persistenciaMock.buscarCups("200801")).thenReturn(cupsData);

        assertEquals("Electrocardiograma", modelo.descripcionCups("200801"));
        assertNull(modelo.descripcionCups("INVALIDO"));
    }

    @Test
    void testPrecioCupsPorPrefijo() {
        // El precio se calcula como: 50000 + (prefijo * 7500)
        // Luego se redondea al millar más cercano (múltiplo de 1000)
        // Y tiene un tope máximo de 800000

        // Prefijo 20: 50000 + (20 * 7500) = 200000
        assertEquals(200000, modelo.precioCups("200801"));

        // Prefijo 30: 50000 + (30 * 7500) = 275000
        assertEquals(275000, modelo.precioCups("300101"));

        // Prefijo 50: 50000 + (50 * 7500) = 425000
        assertEquals(425000, modelo.precioCups("500001"));

        // Prefijo 89: 50000 + (89 * 7500) = 717500 -> redondea a 718000
        assertEquals(718000, modelo.precioCups("890001"));

        // Prefijo 99: 50000 + (99 * 7500) = 792500 -> redondea a 793000
        assertEquals(793000, modelo.precioCups("990000"));

        // Prefijo 100 (toma solo "10"): 50000 + (10 * 7500) = 125000
        assertEquals(125000, modelo.precioCups("100001"));

        // Prefijo 01 (código que empieza con 0): 50000 + (1 * 7500) = 57500 -> redondea a 58000
        assertEquals(58000, modelo.precioCups("010001"));

        // Código vacío - devuelve 0
        assertEquals(0, modelo.precioCups(""));

        // Código null - devuelve 0
        assertEquals(0, modelo.precioCups(null));

        // Código inválido (no numérico) - devuelve 85000
        assertEquals(85000, modelo.precioCups("ABCDE"));

        // Verificar que no supera el tope de 800000
        double precioMaximo = modelo.precioCups("999999");
        assertTrue(precioMaximo <= 800000, "El precio no debe superar 800,000. Valor: " + precioMaximo);
    }

    //Cie-10

    @Test
    void testEspecialidadDesdeCie() {
        assertEquals("Infectología", modelo.especialidadDesdeCie("A00"));
        assertEquals("Oncología", modelo.especialidadDesdeCie("C50"));
        assertEquals("Cardiología", modelo.especialidadDesdeCie("I10"));
        assertEquals("Medicina General", modelo.especialidadDesdeCie("J00"));
        assertEquals("Medicina General", modelo.especialidadDesdeCie("Z00"));
        assertEquals("Medicina General", modelo.especialidadDesdeCie(""));
        assertEquals("Medicina General", modelo.especialidadDesdeCie(null));
    }

    @Test
    void testExisteCie() {
        String[] cieData = {"", "I10", "Hipertensión", ""};
        when(persistenciaMock.buscarCie("I10")).thenReturn(cieData);
        when(persistenciaMock.buscarCie("INVALIDO")).thenReturn(null);

        assertTrue(modelo.existeCie("I10"));
        assertFalse(modelo.existeCie("INVALIDO"));
    }

    @Test
    void testDescripcionCie() {
        String[] cieData = {"", "I10", "Hipertensión esencial", ""};
        when(persistenciaMock.buscarCie("I10")).thenReturn(cieData);

        assertEquals("Hipertensión esencial", modelo.descripcionCie("I10"));
        assertNull(modelo.descripcionCie("INVALIDO"));
    }

    //Instituciones

    @Test
    void testInstitucionesPorEspecialidad() {
        List<Institucion> instituciones = new ArrayList<>();
        instituciones.add(new Institucion("Clinica A", "Bogotá", "Cardiología;Neurología"));
        instituciones.add(new Institucion("Clinica B", "Medellín", "Cardiología"));
        instituciones.add(new Institucion("Clinica C", "Cali", "Pediatría"));

        when(persistenciaMock.leerInstituciones()).thenReturn(instituciones);

        List<Institucion> resultado = modelo.institucionesPorEspecialidad("Cardiología");

        assertEquals(2, resultado.size());
        assertEquals("Clinica A", resultado.get(0).getNombre());
        assertEquals("Clinica B", resultado.get(1).getNombre());
    }

    @Test
    void testBuscarInstitucion() {
        Institucion inst = new Institucion("Clinica Central", "Bogotá", "Cardiología");
        when(persistenciaMock.buscarInstitucion("Clinica Central")).thenReturn(inst);

        Institucion resultado = modelo.buscarInstitucion("Clinica Central");

        assertNotNull(resultado);
        assertEquals("Clinica Central", resultado.getNombre());
    }

    @Test
    void testBuscarInstitucionNoExistente() {
        when(persistenciaMock.buscarInstitucion("Inexistente")).thenReturn(null);

        Institucion resultado = modelo.buscarInstitucion("Inexistente");

        assertNull(resultado);
    }

    //Médicos

    @Test
    void testMedicosPorEspecialidadEInstitucion() {
        List<Medico> medicos = new ArrayList<>();
        medicos.add(new Medico("M01", "Dr A", "Cardiología", "Clinica A"));
        medicos.add(new Medico("M02", "Dr B", "Cardiología", "Clinica B"));
        medicos.add(new Medico("M03", "Dr C", "Neurología", "Clinica A"));

        when(persistenciaMock.leerMedicos()).thenReturn(medicos);

        List<Medico> resultado = modelo.medicosPorEspecialidadEInstitucion("Cardiología", "Clinica A");

        assertEquals(1, resultado.size());
        assertEquals("Dr A", resultado.get(0).getNombre());
    }

    @Test
    void testMedicosPorEspecialidadEInstitucionSinFiltroInstitucion() {
        List<Medico> medicos = new ArrayList<>();
        medicos.add(new Medico("M01", "Dr A", "Cardiología", "Clinica A"));
        medicos.add(new Medico("M02", "Dr B", "Cardiología", "Clinica B"));

        when(persistenciaMock.leerMedicos()).thenReturn(medicos);

        List<Medico> resultado = modelo.medicosPorEspecialidadEInstitucion("Cardiología", null);

        assertEquals(2, resultado.size());
    }

    //Pacientes

    @Test
    void testValidarPacienteParaOrdenExitoso() throws Exception {
        Paciente pacienteActivo = new Paciente("CC", "123", "Juan", 30, "M", "Plan", true, true, false);
        when(persistenciaMock.buscarPaciente("123")).thenReturn(pacienteActivo);

        Paciente resultado = modelo.validarPacienteParaOrden("123");

        assertNotNull(resultado);
        assertEquals("Juan", resultado.getNombre());
        assertTrue(resultado.isActivo());
    }

    @Test
    void testValidarPacienteParaOrdenNoEncontrado() {
        when(persistenciaMock.buscarPaciente("999")).thenReturn(null);

        Exception exception = assertThrows(Exception.class, () -> {
            modelo.validarPacienteParaOrden("999");
        });
        assertTrue(exception.getMessage().contains("no encontrado"));
    }

    @Test
    void testValidarPacienteInactivo() {
        Paciente pacienteInactivo = new Paciente("CC", "123", "Juan", 30, "M", "Plan", true, false, false);
        when(persistenciaMock.buscarPaciente("123")).thenReturn(pacienteInactivo);

        Exception exception = assertThrows(Exception.class, () -> {
            modelo.validarPacienteParaOrden("123");
        });
        assertTrue(exception.getMessage().contains("inactivo"));
    }

    @Test
    void testDocumentoYaRegistrado() {
        Paciente paciente = new Paciente("CC", "123", "Juan", 30, "M", "Plan", true, true, false);
        when(persistenciaMock.buscarPaciente("123")).thenReturn(paciente);
        when(persistenciaMock.buscarPaciente("999")).thenReturn(null);

        assertTrue(modelo.documentoYaRegistrado("123"));
        assertFalse(modelo.documentoYaRegistrado("999"));
    }

    //Órdenes

    @Test
    void testCrearOrdenConConvenio() {
        Paciente pacienteConvenio = new Paciente("CC", "123", "Juan", 30, "M", "Plan", true, true, true);
        Medico medico = new Medico("M01", "Dr A", "Cardiología", "Clinica");
        Institucion inst = new Institucion("Clinica", "Bogotá", "Cardiología");
        List<Procedimiento> procs = new ArrayList<>();
        procs.add(new Procedimiento("200801", "I10", "ECG", "Desc", 1, 100000));

        Orden orden = modelo.crearOrden("A01", "Asesor", pacienteConvenio, medico, inst, procs);

        // Con convenio: 15% descuento = 85000
        assertEquals(85000, orden.getTotal());
        assertNotNull(orden.getRadicado());
        assertTrue(orden.getRadicado() >= 10000);
    }

    @Test
    void testCrearOrdenSinConvenio() {
        Paciente pacienteSinConvenio = new Paciente("CC", "123", "Juan", 30, "M", "Plan", true, true, false);
        Medico medico = new Medico("M01", "Dr A", "Cardiología", "Clinica");
        Institucion inst = new Institucion("Clinica", "Bogotá", "Cardiología");
        List<Procedimiento> procs = new ArrayList<>();
        procs.add(new Procedimiento("200801", "I10", "ECG", "Desc", 1, 100000));

        Orden orden = modelo.crearOrden("A01", "Asesor", pacienteSinConvenio, medico, inst, procs);

        assertEquals(100000, orden.getTotal());
    }

    @Test
    void testGuardarOrden() {
        Paciente paciente = new Paciente("CC", "123", "Juan", 30, "M", "Plan", true, true, false);
        Medico medico = new Medico("M01", "Dr A", "Cardiología", "Clinica");
        Institucion inst = new Institucion("Clinica", "Bogotá", "Cardiología");
        List<Procedimiento> procs = new ArrayList<>();
        procs.add(new Procedimiento("200801", "I10", "ECG", "Desc", 1, 100000));

        Orden orden = modelo.crearOrden("A01", "Asesor", paciente, medico, inst, procs);

        List<Orden> historialExistente = new ArrayList<>();
        when(persistenciaMock.leerOrdenes()).thenReturn(historialExistente);
        when(persistenciaMock.guardarOrdenes(anyList())).thenReturn(true);

        assertDoesNotThrow(() -> modelo.guardarOrden(orden));
    }

    @Test
    void testObtenerHistorialOrdenes() {
        List<Orden> historialMock = new ArrayList<>();
        when(persistenciaMock.leerOrdenes()).thenReturn(historialMock);

        List<Orden> resultado = modelo.obtenerHistorialOrdenes();

        assertNotNull(resultado);
        verify(persistenciaMock, times(1)).leerOrdenes();
    }

    @Test
    void testOrdenesPorPaciente() {
        Paciente paciente = new Paciente("CC", "123", "Juan", 30, "M", "Plan", true, true, false);
        Medico medico = new Medico("M01", "Dr A", "Cardiología", "Clinica");
        Institucion inst = new Institucion("Clinica", "Bogotá", "Cardiología");
        List<Procedimiento> procs = new ArrayList<>();
        procs.add(new Procedimiento("200801", "I10", "ECG", "Desc", 1, 100000));

        Orden orden1 = new Orden(11111, paciente, medico, inst, procs, "A01", "Asesor");
        Orden orden2 = new Orden(22222, paciente, medico, inst, procs, "A01", "Asesor");

        List<Orden> historial = new ArrayList<>();
        historial.add(orden1);
        historial.add(orden2);

        when(persistenciaMock.leerOrdenes()).thenReturn(historial);

        List<Orden> resultado = modelo.ordenesPorPaciente("123");

        assertEquals(2, resultado.size());
    }

    @Test
    void testModificarOrden() {
        Paciente paciente = new Paciente("CC", "123", "Juan", 30, "M", "Plan", true, true, false);
        Medico medicoOriginal = new Medico("M01", "Dr A", "Cardiología", "Clinica A");
        Medico medicoNuevo = new Medico("M02", "Dr B", "Neurología", "Clinica B");
        Institucion inst = new Institucion("Clinica B", "Bogotá", "Neurología");
        List<Procedimiento> procs = new ArrayList<>();
        procs.add(new Procedimiento("200801", "I10", "ECG", "Desc", 1, 100000));

        Orden orden = new Orden(11111, paciente, medicoOriginal, inst, procs, "A01", "Asesor");
        List<Orden> historial = new ArrayList<>();
        historial.add(orden);

        when(persistenciaMock.leerOrdenes()).thenReturn(historial);
        when(persistenciaMock.guardarOrdenes(anyList())).thenReturn(true);

        boolean resultado = modelo.modificarOrden(11111, medicoNuevo, inst, procs, "A02", "Asesor2");

        assertTrue(resultado);
    }

    @Test
    void testModificarOrdenNoExistente() {
        Medico medicoNuevo = new Medico("M02", "Dr B", "Neurología", "Clinica B");
        Institucion inst = new Institucion("Clinica B", "Bogotá", "Neurología");
        List<Procedimiento> procs = new ArrayList<>();

        when(persistenciaMock.leerOrdenes()).thenReturn(new ArrayList<>());

        boolean resultado = modelo.modificarOrden(99999, medicoNuevo, inst, procs, "A02", "Asesor2");

        assertFalse(resultado);
    }

    @Test
    void testCancelarOrden() {
        Paciente paciente = new Paciente("CC", "123", "Juan", 30, "M", "Plan", true, true, false);
        Medico medico = new Medico("M01", "Dr A", "Cardiología", "Clinica A");
        Institucion inst = new Institucion("Clinica A", "Bogotá", "Cardiología");
        List<Procedimiento> procs = new ArrayList<>();
        procs.add(new Procedimiento("200801", "I10", "ECG", "Desc", 1, 100000));

        Orden orden = new Orden(11111, paciente, medico, inst, procs, "A01", "Asesor");
        List<Orden> historial = new ArrayList<>();
        historial.add(orden);

        when(persistenciaMock.leerOrdenes()).thenReturn(historial);
        when(persistenciaMock.guardarOrdenes(anyList())).thenReturn(true);

        boolean resultado = modelo.cancelarOrden(11111, "A01", "Asesor");

        assertTrue(resultado);
        assertEquals("CANCELADA", orden.getEstadoOrden());
    }

    @Test
    void testCancelarOrdenNoExistente() {
        when(persistenciaMock.leerOrdenes()).thenReturn(new ArrayList<>());

        boolean resultado = modelo.cancelarOrden(99999, "A01", "Asesor");

        assertFalse(resultado);
    }

    //Recomendaciones

    @Test
    void testRecomendarCupsDesdeCie() {
        List<String[]> sugerenciasMock = new ArrayList<>();
        sugerenciasMock.add(new String[]{"", "200801", "Electrocardiograma", ""});
        sugerenciasMock.add(new String[]{"", "200802", "Holter", ""});

        when(persistenciaMock.leerCupsPorPrefijo("88", 10)).thenReturn(sugerenciasMock);

        List<String[]> resultado = modelo.recomendarCupsDesdeCie("I10"); // I10 -> Cardiología -> prefijo "88"

        assertEquals(2, resultado.size());
    }

    @Test
    void testRecomendarCupsDesdeCieConCieNull() {
        List<String[]> resultado = modelo.recomendarCupsDesdeCie(null);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testRecomendarCupsDesdeCieConCieVacio() {
        List<String[]> resultado = modelo.recomendarCupsDesdeCie("");

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testRecomendarCupsDesdeCieConLetraInfectologia() {
        List<String[]> sugerenciasMock = new ArrayList<>();
        sugerenciasMock.add(new String[]{"", "190001", "Procedimiento Infectología", ""});

        when(persistenciaMock.leerCupsPorPrefijo("19", 10)).thenReturn(sugerenciasMock);

        List<String[]> resultado = modelo.recomendarCupsDesdeCie("A00"); // A00 -> Infectología -> prefijo "19"

        assertEquals(1, resultado.size());
    }

    @Test
    void testRecomendarCupsDesdeCieConLetraOncologia() {
        List<String[]> sugerenciasMock = new ArrayList<>();
        sugerenciasMock.add(new String[]{"", "300001", "Procedimiento Oncología", ""});

        when(persistenciaMock.leerCupsPorPrefijo("30", 10)).thenReturn(sugerenciasMock);

        List<String[]> resultado = modelo.recomendarCupsDesdeCie("C50"); // C50 -> Oncología -> prefijo "30"

        assertEquals(1, resultado.size());
    }
}