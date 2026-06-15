package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EspecialidadTest {

    @Test
    void testCrearEspecialidad() {
        Especialidad esp = new Especialidad("Cardiología");

        assertEquals("Cardiología", esp.getNombre());
    }

    @Test
    void testCrearEspecialidadConNull() {
        Especialidad esp = new Especialidad(null);

        assertEquals("", esp.getNombre());
    }

    @Test
    void testCrearEspecialidadConEspacios() {
        Especialidad esp = new Especialidad("  Neurología  ");

        assertEquals("Neurología", esp.getNombre());
    }

    @Test
    void testSetNombre() {
        Especialidad esp = new Especialidad("Original");
        esp.setNombre("Nueva");

        assertEquals("Nueva", esp.getNombre());
    }

    @Test
    void testSetNombreNull() {
        Especialidad esp = new Especialidad("Original");
        esp.setNombre(null);

        assertEquals("", esp.getNombre());
    }

    @Test
    void testSetNombreConEspacios() {
        Especialidad esp = new Especialidad("Original");
        esp.setNombre("  Pediatría  ");

        assertEquals("Pediatría", esp.getNombre());
    }

    @Test
    void testToString() {
        Especialidad esp = new Especialidad("Dermatología");

        assertEquals("Dermatología", esp.toString());
    }

    @Test
    void testCompararEspecialidades() {
        Especialidad esp1 = new Especialidad("Cardiología");
        Especialidad esp2 = new Especialidad("Cardiología");
        Especialidad esp3 = new Especialidad("Neurología");

        assertEquals(esp1.getNombre(), esp2.getNombre());
        assertNotEquals(esp1.getNombre(), esp3.getNombre());
    }

    @Test
    void testEspecialidadCaseInsensitive() {
        Especialidad esp1 = new Especialidad("cardiología");
        Especialidad esp2 = new Especialidad("CARDIOLOGÍA");

        // Los nombres se guardan como se ingresan (sin normalizar)
        // Para comparación se debe usar equalsIgnoreCase
        assertEquals("cardiología", esp1.getNombre());
        assertEquals("CARDIOLOGÍA", esp2.getNombre());

        // Comparación case-insensitive
        assertTrue(esp1.getNombre().equalsIgnoreCase(esp2.getNombre()));
    }

    @Test
    void testEspecialidadVacia() {
        Especialidad esp = new Especialidad("");

        assertEquals("", esp.getNombre());
        assertTrue(esp.getNombre().isEmpty());
    }
}