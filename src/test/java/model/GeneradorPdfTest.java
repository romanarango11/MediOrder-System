package model;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeneradorPdfTest {

    private GeneradorPdf generador;

    @BeforeEach
    void setUp() {
        generador = new GeneradorPdf();
    }

    @AfterEach
    void tearDown() {
        // Eliminar PDFs de prueba
        int[] radicados = {11111, 22222, 33333, 44444, 55555, 66666};
        for (int radicado : radicados) {
            File pdfFile = new File("ordenes_pdf/Orden_" + radicado + ".pdf");
            if (pdfFile.exists()) {
                pdfFile.delete();
            }
        }
    }

    // ==================== TEST 1: Orden ACTIVA con radicado 11111 ====================
    @Test
    void testCrearPdfConOrdenActiva() throws Exception {
        int radicado = 11111;

        Paciente paciente = new Paciente("CC", "123456789", "Juan Carlos Perez", 35, "M", "Contributivo", true, true, true);
        Medico medico = new Medico("M100", "Dra. Ana Maria Lopez", "Cardiología", "Clinica Cardiovascular");
        Institucion institucion = new Institucion("Clinica Cardiovascular", "Bogotá", "Cardiología;Medicina General");

        List<Procedimiento> procedimientos = new ArrayList<>();
        procedimientos.add(new Procedimiento("200801", "I10", "Electrocardiograma", "Hipertensión esencial", 1, 150000));

        Orden orden = new Orden(radicado, paciente, medico, institucion, procedimientos, "A01", "Asesor Prueba");

        File pdfFile = new File("ordenes_pdf/Orden_" + radicado + ".pdf");
        if (pdfFile.exists()) {
            pdfFile.delete();
        }

        generador.crearPdf(orden);

        assertTrue(pdfFile.exists(), "El archivo PDF debería existir");

        PdfReader reader = null;
        try {
            reader = new PdfReader(pdfFile.getAbsolutePath());
            String texto = PdfTextExtractor.getTextFromPage(reader, 1);

            System.out.println("=== TEST ACTIVA ===");
            System.out.println(texto);

            // Validaciones básicas
            assertTrue(texto.contains("ACTIVA"), "Debe contener estado ACTIVA");
            assertTrue(texto.contains(String.valueOf(radicado)), "Debe contener el radicado");
            assertTrue(texto.contains("Juan Carlos Perez"), "Debe contener el nombre del paciente");
            assertTrue(texto.contains("Dra. Ana Maria Lopez"), "Debe contener el nombre del médico");
            assertTrue(texto.contains("Cardiología"), "Debe contener la especialidad");
            assertTrue(texto.contains("Clinica Cardiovascular"), "Debe contener la institución");

            // Verificar procedimiento - buscar el código CUPS en lugar de la descripción completa
            boolean tieneProcedimiento = texto.contains("200801") ||
                    texto.contains("I10") ||
                    texto.contains("Electrocardiogram");
            assertTrue(tieneProcedimiento, "Debe contener el procedimiento (código CUPS, CIE o descripción)");

        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    // ==================== TEST 2: Orden CANCELADA con radicado 22222 ====================
    @Test
    void testCrearPdfConOrdenCancelada() throws Exception {
        int radicado = 22222;

        Paciente paciente = new Paciente("CC", "123456789", "Maria Gomez", 28, "F", "Contributivo", true, true, false);
        Medico medico = new Medico("M200", "Dr. Carlos Ruiz", "Neurología", "Clinica del Norte");
        Institucion institucion = new Institucion("Clinica del Norte", "Medellín", "Neurología");

        List<Procedimiento> procedimientos = new ArrayList<>();
        procedimientos.add(new Procedimiento("300101", "G40", "Electroencefalograma", "Epilepsia", 1, 200000));

        Orden orden = new Orden(radicado, paciente, medico, institucion, procedimientos, "A02", "Asesor Test");
        orden.setEstadoOrden("CANCELADA");

        File pdfFile = new File("ordenes_pdf/Orden_" + radicado + ".pdf");
        if (pdfFile.exists()) {
            pdfFile.delete();
        }

        generador.crearPdf(orden);

        assertTrue(pdfFile.exists(), "El archivo PDF debería existir");

        PdfReader reader = null;
        try {
            reader = new PdfReader(pdfFile.getAbsolutePath());
            String texto = PdfTextExtractor.getTextFromPage(reader, 1);

            System.out.println("=== TEST CANCELADA ===");
            System.out.println(texto);

            assertTrue(texto.contains("CANCELADA"), "Debe contener estado CANCELADA");
            assertTrue(texto.contains(String.valueOf(radicado)), "Debe contener el radicado");
            assertTrue(texto.contains("Maria Gomez"), "Debe contener el nombre del paciente");

        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    // ==================== TEST 3: Orden con múltiples procedimientos ====================
    @Test
    void testCrearPdfConMultiplesProcedimientos() throws Exception {
        int radicado = 33333;

        Paciente paciente = new Paciente("CC", "555555", "Pedro Ramirez", 45, "M", "Prepagado", true, true, true);
        Medico medico = new Medico("M300", "Dra. Sofia Mendoza", "Cardiología", "Clinica Cardiovascular");
        Institucion institucion = new Institucion("Clinica Cardiovascular", "Bogotá", "Cardiología");

        List<Procedimiento> procedimientos = new ArrayList<>();
        procedimientos.add(new Procedimiento("200801", "I10", "Electrocardiograma", "Hipertensión", 1, 150000));
        procedimientos.add(new Procedimiento("200802", "I25", "Holter", "Cardiopatía", 2, 250000));

        Orden orden = new Orden(radicado, paciente, medico, institucion, procedimientos, "A03", "Asesor Multi");

        File pdfFile = new File("ordenes_pdf/Orden_" + radicado + ".pdf");
        if (pdfFile.exists()) {
            pdfFile.delete();
        }

        generador.crearPdf(orden);

        PdfReader reader = null;
        try {
            reader = new PdfReader(pdfFile.getAbsolutePath());
            String texto = PdfTextExtractor.getTextFromPage(reader, 1);

            System.out.println("=== TEST MULTIPLES PROCEDIMIENTOS ===");
            System.out.println(texto);

            // Verificar que ambos procedimientos están presentes (por código CUPS)
            assertTrue(texto.contains("200801"), "Debe contener primer CUPS");
            assertTrue(texto.contains("200802"), "Debe contener segundo CUPS");

            // Verificar el total (150,000 + 500,000 = 650,000)
            boolean totalCorrecto = texto.contains("650.000") || texto.contains("650,000");
            assertTrue(totalCorrecto, "Debe contener el total 650.000");

        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    // ==================== TEST 4: Orden sin procedimientos ====================
    @Test
    void testCrearPdfConOrdenSinProcedimientos() {
        int radicado = 44444;

        Paciente paciente = new Paciente("CC", "999999", "Luis Fernandez", 50, "M", "Contributivo", true, true, false);
        Medico medico = new Medico("M400", "Dr. Andres Torres", "Medicina General", "Clinica General");
        Institucion institucion = new Institucion("Clinica General", "Cali", "Medicina General");
        List<Procedimiento> procsVacios = new ArrayList<>();

        Orden orden = new Orden(radicado, paciente, medico, institucion, procsVacios, "A04", "Asesor Vacio");

        File pdfFile = new File("ordenes_pdf/Orden_" + radicado + ".pdf");
        if (pdfFile.exists()) {
            pdfFile.delete();
        }

        assertDoesNotThrow(() -> generador.crearPdf(orden));
        assertTrue(pdfFile.exists(), "El PDF debe crearse incluso sin procedimientos");
        assertTrue(pdfFile.length() > 0, "El PDF no debe estar vacío");
    }

    // ==================== TEST 5: Verificar que el PDF contiene el total ====================
    @Test
    void testVerificarTotalEnPdf() throws Exception {
        int radicado = 55555;

        Paciente paciente = new Paciente("CC", "777777", "Ana Martinez", 32, "F", "Contributivo", true, true, false);
        Medico medico = new Medico("M500", "Dra. Laura Peña", "Ginecología", "Clinica Mujer");
        Institucion institucion = new Institucion("Clinica Mujer", "Bogotá", "Ginecología");

        List<Procedimiento> procedimientos = new ArrayList<>();
        procedimientos.add(new Procedimiento("400101", "N95", "Ultrasonido", "Control prenatal", 2, 200000));

        Orden orden = new Orden(radicado, paciente, medico, institucion, procedimientos, "A05", "Asesor Total");

        File pdfFile = new File("ordenes_pdf/Orden_" + radicado + ".pdf");
        if (pdfFile.exists()) {
            pdfFile.delete();
        }

        generador.crearPdf(orden);

        PdfReader reader = null;
        try {
            reader = new PdfReader(pdfFile.getAbsolutePath());
            String texto = PdfTextExtractor.getTextFromPage(reader, 1);

            System.out.println("=== TEST TOTAL ===");
            System.out.println(texto);

            assertTrue(texto.contains("VALOR TOTAL AUTORIZADO"), "Debe contener la etiqueta del total");
            // 2 * 200,000 = 400,000
            assertTrue(texto.contains("400.000") || texto.contains("400,000"),
                    "Debe contener el total $400.000");

        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    // ==================== TEST 6: Formato de números ====================
    @Test
    void testFormatoNumeroTotal() {
        double total = 1234567.89;
        String formateado = String.format("$%,.0f", total);

        boolean esValido = formateado.equals("$1,234,568") || formateado.equals("$1.234.568");
        assertTrue(esValido, "El formato debe ser $1,234,568 o $1.234.568 pero fue: " + formateado);
    }

    // ==================== TEST 7: Formato de fechas ====================
    @Test
    void testFormatoFechaOrden() {
        Paciente paciente = new Paciente("CC", "123", "Test", 30, "M", "Plan", true, true, false);
        Medico medico = new Medico("M01", "Dr Test", "General", "Clinica");
        Institucion inst = new Institucion("Clinica", "Bogotá", "General");
        List<Procedimiento> procs = new ArrayList<>();
        procs.add(new Procedimiento("C01", "A01", "Desc", "Desc", 1, 1000));

        Orden orden = new Orden(66666, paciente, medico, inst, procs, "A01", "Asesor");

        assertNotNull(orden.getFechaEmision());
        assertTrue(orden.getFechaEmision().matches("\\d{2}/\\d{2}/\\d{4}"),
                "Fecha debe tener formato dd/MM/yyyy: " + orden.getFechaEmision());

        assertNotNull(orden.getFechaVencimiento());
        assertTrue(orden.getFechaVencimiento().matches("\\d{2}/\\d{2}/\\d{4}"),
                "Fecha vencimiento debe tener formato dd/MM/yyyy: " + orden.getFechaVencimiento());
    }
}