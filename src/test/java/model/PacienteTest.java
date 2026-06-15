package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PacienteTest {

    @Test
    void testCrearPaciente() {
        Paciente p = new Paciente("CC", "12345678", "Juan Perez", 30, "M", "Contributivo", true, true, false);

        assertEquals("CC", p.getTipoDocumento());
        assertEquals("12345678", p.getDocumento());
        assertEquals("Juan Perez", p.getNombre());
        assertEquals(30, p.getEdad());
        assertEquals("M", p.getSexo());
        assertEquals("Contributivo", p.getPlan());
        assertTrue(p.isAfiliado());
        assertTrue(p.isActivo());
        assertFalse(p.isConvenio());
    }

    @Test
    void testDesdeCsv() {
        String[] datos = {"CC", "87654321", "Maria Gomez", "25", "F", "Prepagado", "SI", "Activo", "NO"};
        Paciente p = Paciente.desdeCsv(datos);

        assertNotNull(p);
        assertEquals("CC", p.getTipoDocumento());
        assertEquals("87654321", p.getDocumento());
        assertEquals("Maria Gomez", p.getNombre());
        assertEquals(25, p.getEdad());
        assertEquals("Prepagado", p.getPlan());
    }

    @Test
    void testDesdeCsvDatosInvalidos() {
        assertNull(Paciente.desdeCsv(null));
        assertNull(Paciente.desdeCsv(new String[]{"CC"}));
    }

    @Test
    void testToCsvLinea() {
        Paciente p = new Paciente("CC", "111", "Ana", 40, "F", "Subsidiado", true, true, true);
        String linea = p.toCsvLinea();

        assertTrue(linea.contains("CC"));
        assertTrue(linea.contains("111"));
        assertTrue(linea.contains("Ana"));
        assertTrue(linea.contains("SI"));
    }

    @Test
    void testSetters() {
        Paciente p = new Paciente("CC", "1", "A", 1, "M", "Plan", true, true, true);
        p.setNombre("Nuevo Nombre");
        p.setEdad(50);
        p.setActivo(false);

        assertEquals("Nuevo Nombre", p.getNombre());
        assertEquals(50, p.getEdad());
        assertFalse(p.isActivo());
    }
}