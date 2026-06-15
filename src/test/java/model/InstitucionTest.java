package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InstitucionTest {

    private Institucion institucion;

    @BeforeEach
    void setUp() {
        institucion = new Institucion("Clinica Central", "Bogotá", "Cardiología;Neurología;Pediatría");
    }

    @Test
    void testCrearInstitucion() {
        assertEquals("Clinica Central", institucion.getNombre());
        assertEquals("Bogotá", institucion.getCiudad());
    }

    @Test
    void testTieneEspecialidadExistente() {
        assertTrue(institucion.tieneEspecialidad("Cardiología"));
        assertTrue(institucion.tieneEspecialidad("Neurología"));
        assertTrue(institucion.tieneEspecialidad("Pediatría"));
    }

    @Test
    void testTieneEspecialidadNoExistente() {
        assertFalse(institucion.tieneEspecialidad("Dermatología"));
        assertFalse(institucion.tieneEspecialidad("Oftalmología"));
    }

    @Test
    void testTieneEspecialidadCaseInsensitive() {
        assertTrue(institucion.tieneEspecialidad("cardiología"));
        assertTrue(institucion.tieneEspecialidad("CARDIOLOGÍA"));
        assertTrue(institucion.tieneEspecialidad("Neurologia")); // sin acento
    }

    @Test
    void testTieneEspecialidadConNull() {
        assertFalse(institucion.tieneEspecialidad(null));
    }

    @Test
    void testInstitucionSinEspecialidades() {
        Institucion instSinEsp = new Institucion("Clinica Simple", "Cali", "");

        assertFalse(instSinEsp.tieneEspecialidad("Cardiología"));
        assertEquals(0, instSinEsp.getEspecialidadesOfrecidas().length);
    }

    @Test
    void testInstitucionConEspecialidadesNulas() {
        Institucion instNull = new Institucion("Clinica", "Bogotá", null);

        assertNotNull(instNull.getEspecialidadesOfrecidas());
        assertEquals(0, instNull.getEspecialidadesOfrecidas().length);
    }

    @Test
    void testGetEspecialidadesOfrecidas() {
        Especialidad[] especialidades = institucion.getEspecialidadesOfrecidas();

        assertEquals(3, especialidades.length);
        assertEquals("Cardiología", especialidades[0].getNombre());
        assertEquals("Neurología", especialidades[1].getNombre());
        assertEquals("Pediatría", especialidades[2].getNombre());
    }

    @Test
    void testToString() {
        assertEquals("Clinica Central - Bogotá", institucion.toString());
    }

    @Test
    void testInstitucionConEspaciosEnEspecialidades() {
        Institucion inst = new Institucion("Clinica", "Medellín", "Cardiología ; Neurología ; Pediatría");

        assertTrue(inst.tieneEspecialidad("Cardiología"));
        assertTrue(inst.tieneEspecialidad("Neurología"));
        assertTrue(inst.tieneEspecialidad("Pediatría"));
    }

    @Test
    void testInstitucionConUnaSolaEspecialidad() {
        Institucion inst = new Institucion("Clinica Especializada", "Bogotá", "Oftalmología");

        assertTrue(inst.tieneEspecialidad("Oftalmología"));
        assertFalse(inst.tieneEspecialidad("Cardiología"));
        assertEquals(1, inst.getEspecialidadesOfrecidas().length);
    }
}