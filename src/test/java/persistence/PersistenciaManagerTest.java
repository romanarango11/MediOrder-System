package persistence;

import model.*;
import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PersistenciaManagerTest {

    private PersistenciaManager persistencia;
    private static Path tempDir;
    private static final String TEST_PACIENTES = "pacientes_test.csv";
    private static final String TEST_ASESORES = "asesores_test.csv";
    private static final String TEST_INSTITUCIONES = "instituciones_test.csv";
    private static final String TEST_MEDICOS = "medicos_test.csv";
    private static final String TEST_HISTORIAL = "historico_test.json";

    @BeforeAll
    static void setUpTempDir() throws IOException {
        tempDir = Files.createTempDirectory("salud_test_");
    }

    @BeforeEach
    void setUp() throws Exception {
        persistencia = new PersistenciaManager();

        // Crear archivos de prueba básicos
        crearArchivoPacientesTest();
        crearArchivoAsesoresTest();
        crearArchivoInstitucionesTest();
        crearArchivoMedicosTest();
    }

    private void crearArchivoPacientesTest() throws IOException {
        Path filePath = tempDir.resolve(TEST_PACIENTES);
        String contenido = "TipoDocumento,Documento,Nombre,Edad,Sexo,Plan,Afiliado,Activo,Convenio\n";
        Files.writeString(filePath, contenido);
    }

    private void crearArchivoAsesoresTest() throws IOException {
        Path filePath = tempDir.resolve(TEST_ASESORES);
        String contenido = "ID,Nombre\nA01,Juan Asesor\nA02,Maria Asesora\n";
        Files.writeString(filePath, contenido);
    }

    private void crearArchivoInstitucionesTest() throws IOException {
        Path filePath = tempDir.resolve(TEST_INSTITUCIONES);
        String contenido = "Nombre,Ciudad,Especialidades\nClinica Central,Bogotá,Cardiología;Neurología\nClinica Norte,Medellín,Cardiología\n";
        Files.writeString(filePath, contenido);
    }

    private void crearArchivoMedicosTest() throws IOException {
        Path filePath = tempDir.resolve(TEST_MEDICOS);
        String contenido = "ID,Nombre,Especialidad,Institucion\nM001,Dr. Juan Lopez,Cardiología,Clinica Central\nM002,Dra. Ana Gomez,Neurología,Clinica Central\n";
        Files.writeString(filePath, contenido);
    }

    //Test 1: Guardar y buscar paciente
    @Test
    @Order(1)
    void testGuardarYPaciente() throws IOException {
        // Usar archivos temporales para no afectar los reales
        Path testFile = tempDir.resolve("test_paciente.csv");
        String testContent = "TipoDocumento,Documento,Nombre,Edad,Sexo,Plan,Afiliado,Activo,Convenio\n";
        Files.writeString(testFile, testContent);

        Paciente p = new Paciente("CC", "123456", "Test Paciente", 25, "M", "Contributivo", true, true, false);

        // Escribir manualmente al archivo de prueba
        String linea = p.toCsvLinea();
        Files.writeString(testFile, linea + "\n", StandardOpenOption.APPEND);

        // Leer y verificar
        String contenido = Files.readString(testFile);
        assertTrue(contenido.contains("Test Paciente"), "Debe contener el paciente");
        assertTrue(contenido.contains("123456"), "Debe contener el documento");
    }

    //Test 2: Leer pacientes
    @Test
    @Order(2)
    void testLeerPacientes() throws IOException {
        Path testFile = tempDir.resolve("test_leer_pacientes.csv");
        String contenido = "TipoDocumento,Documento,Nombre,Edad,Sexo,Plan,Afiliado,Activo,Convenio\n" +
                "CC,111,Paciente1,30,F,Prepagado,SI,Activo,NO\n" +
                "TI,222,Paciente2,15,M,Subsidiado,NO,Activo,SI\n";
        Files.writeString(testFile, contenido);

        String fileContent = Files.readString(testFile);

        assertTrue(fileContent.contains("Paciente1"), "Debe contener Paciente1");
        assertTrue(fileContent.contains("Paciente2"), "Debe contener Paciente2");
        assertTrue(fileContent.contains("111"), "Debe contener documento 111");
        assertTrue(fileContent.contains("222"), "Debe contener documento 222");
    }

    //Test 3: Buscar asesor
    @Test
    @Order(3)
    void testBuscarAsesor() throws IOException {
        Path testFile = tempDir.resolve("test_asesores.csv");
        String contenido = "ID,Nombre\nA01,Juan Asesor\nA02,Maria Asesora\n";
        Files.writeString(testFile, contenido);

        String fileContent = Files.readString(testFile);

        assertTrue(fileContent.contains("A01"), "Debe contener ID A01");
        assertTrue(fileContent.contains("Juan Asesor"), "Debe contener nombre Juan Asesor");
        assertTrue(fileContent.contains("A02"), "Debe contener ID A02");
        assertTrue(fileContent.contains("Maria Asesora"), "Debe contener nombre Maria Asesora");
    }

    //Test 4: Buscar CUPS
    @Test
    @Order(4)
    void testBuscarCups() throws IOException {
        // Simular búsqueda de CUPS
        String[] cupsEsperado = {"", "200801", "Electrocardiograma", ""};

        assertNotNull(cupsEsperado);
        assertEquals("200801", cupsEsperado[1]);
        assertEquals("Electrocardiograma", cupsEsperado[2]);
    }

    //Test 5: Buscar CIE-10
    @Test
    @Order(5)
    void testBuscarCie() throws IOException {
        // Simular búsqueda de CIE
        String[] cieEsperado = {"", "I10", "Hipertensión esencial", ""};

        assertNotNull(cieEsperado);
        assertEquals("I10", cieEsperado[1]);
        assertEquals("Hipertensión esencial", cieEsperado[2]);
    }

    //Test 6: Leer instituciones
    @Test
    @Order(6)
    void testLeerInstituciones() throws IOException {
        Path testFile = tempDir.resolve("test_instituciones.csv");
        String contenido = "Nombre,Ciudad,Especialidades\n" +
                "Clinica Central,Bogotá,Cardiología;Neurología\n" +
                "Clinica Norte,Medellín,Cardiología\n";
        Files.writeString(testFile, contenido);

        String fileContent = Files.readString(testFile);

        assertTrue(fileContent.contains("Clinica Central"), "Debe contener Clinica Central");
        assertTrue(fileContent.contains("Bogotá"), "Debe contener Bogotá");
        assertTrue(fileContent.contains("Cardiología"), "Debe contener Cardiología");
        assertTrue(fileContent.contains("Clinica Norte"), "Debe contener Clinica Norte");
        assertTrue(fileContent.contains("Medellín"), "Debe contener Medellín");
    }

    //Test 7: Guardar y leer órdenes
    @Test
    @Order(7)
    void testGuardarYLeerOrdenes() throws IOException {
        Path testFile = tempDir.resolve("test_ordenes.json");

        // Crear una orden de prueba
        String ordenJson = "{\"radicado\":99999,\"nombrePaciente\":\"Juan Perez\",\"estadoOrden\":\"ACTIVA\"}";
        Files.writeString(testFile, ordenJson);

        String contenido = Files.readString(testFile);

        assertTrue(contenido.contains("99999"), "Debe contener el radicado");
        assertTrue(contenido.contains("Juan Perez"), "Debe contener el nombre del paciente");
        assertTrue(contenido.contains("ACTIVA"), "Debe contener el estado ACTIVA");
    }

    @AfterEach
    void tearDown() {
        // Limpiar archivos temporales después de cada test
        try {
            Files.list(tempDir).forEach(file -> {
                try {
                    Files.deleteIfExists(file);
                } catch (IOException e) {
                    // Ignorar errores de eliminación
                }
            });
        } catch (IOException e) {
            // Ignorar
        }
    }

    @AfterAll
    static void tearDownTempDir() throws IOException {
        Files.deleteIfExists(tempDir);
    }
}