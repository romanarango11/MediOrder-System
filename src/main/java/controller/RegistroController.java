package controller;

import app.MainApp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Paciente;

import java.util.List;

public class RegistroController extends BaseController {

    @FXML private RadioButton rbCC;
    @FXML private RadioButton rbCE;
    @FXML private RadioButton rbTI;
    @FXML private ToggleGroup grupoDocs;

    @FXML private TextField txtDocumento;
    @FXML private TextField txtNombre;
    @FXML private TextField txtEdad;

    @FXML private ComboBox<String> cbSexo;
    @FXML private ComboBox<String> cbPlan;

    @FXML private CheckBox chkAfiliado;
    @FXML private CheckBox chkActivo;
    @FXML private CheckBox chkConvenio;

    @FXML private TableView<Paciente> tablaPacientes;
    @FXML private TableColumn<Paciente, String>  colTipoDoc;
    @FXML private TableColumn<Paciente, String>  colDocumento;
    @FXML private TableColumn<Paciente, String>  colNombre;
    @FXML private TableColumn<Paciente, Integer> colEdad;
    @FXML private TableColumn<Paciente, String>  colSexo;
    @FXML private TableColumn<Paciente, String>  colPlan;
    @FXML private TableColumn<Paciente, Boolean> colAfiliado;
    @FXML private TableColumn<Paciente, Boolean> colActivo;
    @FXML private TableColumn<Paciente, Boolean> colConvenio;

    private ObservableList<Paciente> listaPacientes = FXCollections.observableArrayList();

    @Override
    public void inicializar() {
        cbSexo.setItems(FXCollections.observableArrayList("M", "F", "Otro"));
        cbPlan.setItems(FXCollections.observableArrayList("Prepagado", "Contributivo", "Subsidiado"));
        configurarTabla();
        cargarPacientes();
    }

    private void configurarTabla() {
        colTipoDoc.setCellValueFactory(new PropertyValueFactory<>("tipoDocumento"));
        colDocumento.setCellValueFactory(new PropertyValueFactory<>("documento"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));
        colSexo.setCellValueFactory(new PropertyValueFactory<>("sexo"));
        colPlan.setCellValueFactory(new PropertyValueFactory<>("plan"));
        colAfiliado.setCellValueFactory(new PropertyValueFactory<>("afiliado"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));
        colConvenio.setCellValueFactory(new PropertyValueFactory<>("convenio"));
        tablaPacientes.setItems(listaPacientes);
    }

    private void cargarPacientes() {
        if (controlador != null) {
            List<Paciente> lista = controlador.obtenerTodosPacientes();
            listaPacientes.setAll(lista);
        }
    }

    @FXML
    public void onRegistrar(ActionEvent event) {
        String tipoDoc = obtenerTipoDocSeleccionado();
        String documento = txtDocumento.getText().trim();
        String nombre = txtNombre.getText().trim();
        String edadStr = txtEdad.getText().trim();

        if (tipoDoc == null) { mostrarAlerta("Seleccione el tipo de documento."); return; }
        if (documento.isEmpty()) { mostrarAlerta("Ingrese el número de documento."); return; }
        if (nombre.isEmpty()) { mostrarAlerta("Ingrese el nombre del paciente."); return; }
        if (edadStr.isEmpty()) { mostrarAlerta("Ingrese la edad."); return; }

        int edad;
        try {
            edad = Integer.parseInt(edadStr);
            if (edad < 0 || edad > 130) { mostrarAlerta("Ingrese una edad válida."); return; }
        } catch (NumberFormatException e) {
            mostrarAlerta("La edad debe ser un número.");
            return;
        }

        if (cbSexo.getValue() == null) { mostrarAlerta("Seleccione el sexo."); return; }
        if (cbPlan.getValue() == null) { mostrarAlerta("Seleccione el plan."); return; }

        if (controlador.documentoYaRegistrado(documento)) {
            mostrarAlerta("Ya existe un paciente con ese número de documento.");
            return;
        }
        if(tipoDoc.equals("TI") && edad >= 18) {
            mostrarAlerta("La Tarjeta de Identidad es solo para menores de 18 años." +
                    "Para " + edad + " años debe usar TI.");
            return;
        }
        if ((tipoDoc.equals("CC") || tipoDoc.equals("CE")) && edad < 18){
            mostrarAlerta("CC y CE son para mayores de 18 años. " +
                    "Para " + edad + " años debe usar TI.");
            return;
        }

        Paciente nuevo = new Paciente(
                tipoDoc, documento, nombre, edad,
                cbSexo.getValue(), cbPlan.getValue(),
                chkAfiliado.isSelected(), chkActivo.isSelected(), chkConvenio.isSelected()
        );

        boolean ok = controlador.registrarPaciente(nuevo);
        if (ok) {
            mostrarInfo("Paciente registrado correctamente.");
            limpiarFormulario();
            cargarPacientes();
        } else {
            mostrarAlerta("No se pudo registrar el paciente.");
        }
    }

    @FXML
    public void onVolverLogin(ActionEvent event) {
        MainApp.cargarVista("/view/Login.fxml", "Sistema de Salud - Inicio", 700, 500);
    }

    private String obtenerTipoDocSeleccionado() {
        if (rbCC.isSelected()) return "CC";
        if (rbCE.isSelected()) return "CE";
        if (rbTI.isSelected()) return "TI";
        return null;
    }

    private void limpiarFormulario() {
        grupoDocs.selectToggle(null);
        txtDocumento.clear();
        txtNombre.clear();
        txtEdad.clear();
        cbSexo.getSelectionModel().clearSelection();
        cbPlan.getSelectionModel().clearSelection();
        chkAfiliado.setSelected(false);
        chkActivo.setSelected(false);
        chkConvenio.setSelected(false);
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
