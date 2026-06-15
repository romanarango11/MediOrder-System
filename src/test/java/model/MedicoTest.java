package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MedicoTest {

    @Test
    void testCrearMedico() {
        Medico medico = new Medico("M001", "Dr. Juan Perez", "Cardiología", "Clinica Central");

        assertEquals("M001", medico.getId());
        assertEquals("Dr. Juan Perez", medico.getNombre());
        assertEquals("Cardiología", medico.getEspecialidad());
        assertEquals("Clinica Central", medico.getInstitucion());
    }

    @Test
    void testDesdeCsvValido() {
        String[] datos = {"M010", "Dra. Ana Gomez", "Neurología", "Clinica del Norte"};
        Medico medico = Medico.desdeCsv(datos);

        assertNotNull(medico);
        assertEquals("M010", medico.getId());
        assertEquals("Dra. Ana Gomez", medico.getNombre());
        assertEquals("Neurología", medico.getEspecialidad());
        assertEquals("Clinica del Norte", medico.getInstitucion());
    }

    @Test
    void testDesdeCsvConDatosIncompletos() {
        assertNull(Medico.desdeCsv(null));
        assertNull(Medico.desdeCsv(new String[]{"M001"}));
        assertNull(Medico.desdeCsv(new String[]{"M001", "Dr. Juan"}));
        assertNull(Medico.desdeCsv(new String[]{"M001", "Dr. Juan", "Cardiología"}));
    }

    @Test
    void testDesdeCsvConDatosVacios() {
        String[] datos = {"", "", "", ""};
        Medico medico = Medico.desdeCsv(datos);

        assertNotNull(medico);
        assertEquals("", medico.getId());
        assertEquals("", medico.getNombre());
        assertEquals("", medico.getEspecialidad());
        assertEquals("", medico.getInstitucion());
    }

    @Test
    void testDesdeCsvConEspacios() {
        String[] datos = {"  M020  ", "  Dra. Maria Lopez  ", "  Pediatría  ", "  Clinica Infantil  "};
        Medico medico = Medico.desdeCsv(datos);

        assertEquals("M020", medico.getId());
        assertEquals("Dra. Maria Lopez", medico.getNombre());
        assertEquals("Pediatría", medico.getEspecialidad());
        assertEquals("Clinica Infantil", medico.getInstitucion());
    }

    @Test
    void testToString() {
        Medico medico = new Medico("M001", "Dr. Juan Perez", "Cardiología", "Clinica Central");

        assertEquals("Dr. Juan Perez - Cardiología", medico.toString());
    }

    @Test
    void testGetters() {
        Medico medico = new Medico("M999", "Dr. Test", "Especialidad Test", "Institución Test");

        assertEquals("M999", medico.getId());
        assertEquals("Dr. Test", medico.getNombre());
        assertEquals("Especialidad Test", medico.getEspecialidad());
        assertEquals("Institución Test", medico.getInstitucion());
    }

    @Test
    void testMedicosConMismaEspecialidad() {
        Medico medico1 = new Medico("M01", "Dr. Lopez", "Cardiología", "Clinica A");
        Medico medico2 = new Medico("M02", "Dra. Gomez", "Cardiología", "Clinica B");
        Medico medico3 = new Medico("M03", "Dr. Ruiz", "Neurología", "Clinica A");

        assertEquals(medico1.getEspecialidad(), medico2.getEspecialidad());
        assertNotEquals(medico1.getEspecialidad(), medico3.getEspecialidad());
    }

    @Test
    void testMedicosMismaInstitucion() {
        Medico medico1 = new Medico("M01", "Dr. Lopez", "Cardiología", "Clinica Central");
        Medico medico2 = new Medico("M02", "Dra. Gomez", "Neurología", "Clinica Central");
        Medico medico3 = new Medico("M03", "Dr. Ruiz", "Pediatría", "Clinica Norte");

        assertEquals(medico1.getInstitucion(), medico2.getInstitucion());
        assertNotEquals(medico1.getInstitucion(), medico3.getInstitucion());
    }

    @Test
    void testCompararMedicosPorId() {
        Medico medico1 = new Medico("M001", "Dr. A", "Cardiología", "Clinica A");
        Medico medico2 = new Medico("M001", "Dr. B", "Neurología", "Clinica B");

        // Mismo ID pero diferentes datos (escenario posible con datos corruptos)
        assertEquals(medico1.getId(), medico2.getId());
        assertNotEquals(medico1.getNombre(), medico2.getNombre());
    }

    @Test
    void testMedicoSinInstitucion() {
        Medico medico = new Medico("M001", "Dr. Juan", "Cardiología", null);

        assertNull(medico.getInstitucion());
        assertEquals("Dr. Juan - Cardiología", medico.toString());
    }

    @Test
    void testMedicoSinEspecialidad() {
        Medico medico = new Medico("M001", "Dr. Juan", null, "Clinica Central");

        assertNull(medico.getEspecialidad());
        assertEquals("Dr. Juan - null", medico.toString());
    }

    @Test
    void testCrearMedicoConDatosExtremos() {
        // IDs muy largos
        Medico medico = new Medico("M12345678901234567890", "Dr. Nombre Muy Largo Con Muchos Caracteres",
                "EspecialidadMuyLargaQueSuperaLosLimitesNormales",
                "InstitucionConNombreExtremadamenteLargoParaProbarLimites");

        assertEquals("M12345678901234567890", medico.getId());
        assertEquals("Dr. Nombre Muy Largo Con Muchos Caracteres", medico.getNombre());
        assertEquals("EspecialidadMuyLargaQueSuperaLosLimitesNormales", medico.getEspecialidad());
        assertEquals("InstitucionConNombreExtremadamenteLargoParaProbarLimites", medico.getInstitucion());
    }

    @Test
    void testDesdeCsvConMasColumnas() {
        // CSV con más columnas de las necesarias (debe ignorar las extras)
        String[] datos = {"M001", "Dr. Juan", "Cardiología", "Clinica Central", "Extra1", "Extra2"};
        Medico medico = Medico.desdeCsv(datos);

        assertNotNull(medico);
        assertEquals("M001", medico.getId());
        assertEquals("Dr. Juan", medico.getNombre());
        assertEquals("Cardiología", medico.getEspecialidad());
        assertEquals("Clinica Central", medico.getInstitucion());
    }

    @Test
    void testDesdeCsvConCaracteresEspeciales() {
        String[] datos = {"M-001", "Dr. Juan Pérez-García", "Cardiología/Intervencionista", "Clínica Central & Asociados"};
        Medico medico = Medico.desdeCsv(datos);

        assertEquals("M-001", medico.getId());
        assertEquals("Dr. Juan Pérez-García", medico.getNombre());
        assertEquals("Cardiología/Intervencionista", medico.getEspecialidad());
        assertEquals("Clínica Central & Asociados", medico.getInstitucion());
    }

    @Test
    void testMedicoConIdsNumericos() {
        Medico medico = new Medico("12345", "Dr. Numerico", "Medicina General", "Clinica");

        assertEquals("12345", medico.getId());
    }

    @Test
    void testMedicoConAcentosYTildes() {
        Medico medico = new Medico("M001", "Dr. José Hernández", "Gastroenterología", "Clínica San Martín");

        assertEquals("M001", medico.getId());
        assertEquals("Dr. José Hernández", medico.getNombre());
        assertEquals("Gastroenterología", medico.getEspecialidad());
        assertEquals("Clínica San Martín", medico.getInstitucion());
    }

    @Test
    void testMedicoConNombresCompuestos() {
        Medico medico = new Medico("M001", "Dra. María del Carmen Fernández López", "Medicina Interna", "Hospital Universitario");

        assertEquals("M001", medico.getId());
        assertEquals("Dra. María del Carmen Fernández López", medico.getNombre());
        assertEquals("Medicina Interna", medico.getEspecialidad());
        assertEquals("Hospital Universitario", medico.getInstitucion());
    }
}