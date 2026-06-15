package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class OrdenTest {

    private Paciente paciente;
    private Medico medico;
    private Institucion institucion;
    private List<Procedimiento> procedimientos;

    @BeforeEach
    void setUp() {
        paciente = new Paciente("CC", "123", "Juan Perez", 30, "M", "Contributivo", true, true, false);
        medico = new Medico("M001", "Dr. Lopez", "Cardiología", "Clinica Central");
        institucion = new Institucion("Clinica Central", "Bogotá", "Cardiología;Medicina General");

        procedimientos = new ArrayList<>();
        procedimientos.add(new Procedimiento("200801", "I10", "ECG", "Hipertensión", 1, 150000));
    }

    @Test
    void testCrearOrden() {
        Orden orden = new Orden(12345, paciente, medico, institucion, procedimientos, "A01", "Asesor Test");

        assertEquals(12345, orden.getRadicado());
        assertEquals("123", orden.getDocumento());
        assertEquals("Juan Perez", orden.getNombrePaciente());
        assertEquals("M001", orden.getMedicoId());
        assertEquals("Dr. Lopez", orden.getMedicoNombre());
        assertEquals("Cardiología", orden.getEspecialidad());
        assertEquals("Clinica Central", orden.getInstitucion());
        assertEquals("ACTIVA", orden.getEstadoOrden());
        assertEquals("Asesor Test", orden.getAsesorNombre());
        assertEquals(150000, orden.getTotal());
    }

    @Test
    void testSetProcedimientosRecalculaTotal() {
        Orden orden = new Orden(1, paciente, medico, institucion, procedimientos, "A01", "Asesor");

        List<Procedimiento> nuevos = new ArrayList<>();
        nuevos.add(new Procedimiento("C01", "A01", "Desc", "Desc", 2, 50000));
        nuevos.add(new Procedimiento("C02", "A02", "Desc2", "Desc2", 1, 100000));

        orden.setProcedimientos(nuevos);

        assertEquals(200000, orden.getTotal());
        assertEquals(2, orden.getProcedimientos().size());
    }

    @Test
    void testSetEstadoOrden() {
        Orden orden = new Orden(1, paciente, medico, institucion, procedimientos, "A01", "Asesor");
        orden.setEstadoOrden("CANCELADA");
        assertEquals("CANCELADA", orden.getEstadoOrden());
    }

    @Test
    void testAsesorAccion() {
        Orden orden = new Orden(1, paciente, medico, institucion, procedimientos, "A01", "Asesor");
        orden.setAsesorUltimaAccion("A02");
        orden.setNombreUltimaAccion("Otro Asesor");

        assertTrue(orden.getAsesorAccion().contains("Otro Asesor"));
    }

    @Test
    void testToString() {
        Orden orden = new Orden(999, paciente, medico, institucion, procedimientos, "A01", "Asesor");
        assertTrue(orden.toString().contains("999"));
        assertTrue(orden.toString().contains("Juan Perez"));
    }
}