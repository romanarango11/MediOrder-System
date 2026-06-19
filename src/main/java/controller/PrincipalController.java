package controller;

import app.MainApp;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.*;

import java.util.ArrayList;
import java.util.List;

public class PrincipalController extends BaseController {

    //Orden
    @FXML private Label lblAsesor;
    @FXML private TextField txtCedulaPaciente;
    @FXML private VBox panelInfoPaciente;
    @FXML private Label lblNombrePac;
    @FXML private Label lblEdadPac;
    @FXML private Label lblSexoPac;
    @FXML private Label lblPlanPac;
    @FXML private Label lblEstadoPac;

    // Códigos
    @FXML private TextField txtCie;
    @FXML private Label lblDescCie;
    @FXML private Label lblEspecialidadCie;
    @FXML private ListView<String> listaSugerenciasCups;
    @FXML private TextField txtCups;
    @FXML private Label lblDescCups;
    @FXML private Label lblPrecioCups;
    @FXML private TextField txtCantidad;

    // Institución y médico
    @FXML private ComboBox<String> cbInstitucion;
    @FXML private TextField txtMedicoSeleccionado;
    @FXML private Label lblIdMedico;

    // Tabla de procedimientos agregados
    @FXML private TableView<Procedimiento> tablaProcs;
    @FXML private TableColumn<Procedimiento, String> colCups;
    @FXML private TableColumn<Procedimiento, String> colCie;
    @FXML private TableColumn<Procedimiento, String> colDescProc;
    @FXML private TableColumn<Procedimiento, Integer> colCantidad;
    @FXML private TableColumn<Procedimiento, Double> colValor;
    @FXML private TableColumn<Procedimiento, Double> colSubtotal;
    @FXML private Label lblTotal;

    //Historial
    @FXML private TextField txtFiltroHistorial;
    @FXML private TableView<Orden> tablaHistorial;
    @FXML private TableColumn<Orden, Integer> colRadicado;
    @FXML private TableColumn<Orden, String> colPaciente;
    @FXML private TableColumn<Orden, String> colFecha;
    @FXML private TableColumn<Orden, String> colEspecialidad;
    @FXML private TableColumn<Orden, String> colInstitucion;
    @FXML private TableColumn<Orden, Double> colTotal;
    @FXML private TableColumn<Orden, String> colEstado;
    @FXML private TableColumn<Orden, String> colAsesorAccion;

    private ObservableList<Procedimiento> listaProcedimientos = FXCollections.observableArrayList();
    private ObservableList<Orden> listaOrdenes = FXCollections.observableArrayList();

    private Medico medicoActual;
    private String especialidadActual;      // especialidad del CIE-10 que se está ingresando ahora
    private String especialidadBloqueada;   // especialidad fijada al agregar el primer procedimiento

    @Override
    public void inicializar() {

        lblAsesor.setText("Asesor: " + controlador.getAsesorNombre());
        panelInfoPaciente.setVisible(false);

        configurarTablaProcs();
        configurarTablaHistorial();
        cargarHistorial();
    }


    //Búsqueda paciente


    @FXML
    public void onBuscarPaciente(ActionEvent event) {

        String cedula = txtCedulaPaciente.getText().trim();
        String resultado = controlador.buscarPacienteParaOrden(cedula);

        if (resultado.startsWith("OK")) {
            Paciente p = controlador.getPacienteActivo();
            lblNombrePac.setText(p.getNombre());
            lblEdadPac.setText(String.valueOf(p.getEdad()));
            lblSexoPac.setText(p.getSexo());
            lblPlanPac.setText(p.getPlan());
            lblEstadoPac.setText(p.isActivo() ? "Activo" : "Inactivo");
            panelInfoPaciente.setVisible(true);
        } else {
            panelInfoPaciente.setVisible(false);
            mostrarAlerta(resultado.split("\\|")[1]);
        }
    }


    // Lógica CIE-10 y CUPS


    @FXML
    public void onValidarCie(ActionEvent event) {

        String cie = txtCie.getText().trim().toUpperCase();

        if (cie.isEmpty()) {
            mostrarAlerta("Ingrese un código CIE-10.");
            return;
        }

        if (!controlador.existeCie(cie)) {
            mostrarAlerta("Código CIE-10 no encontrado: " + cie);
            lblDescCie.setText("");
            lblEspecialidadCie.setText("");
            listaSugerenciasCups.getItems().clear();
            return;
        }

        String desc = controlador.descripcionCie(cie);
        lblDescCie.setText(desc != null ? desc : "");

        String nuevaEspecialidad = controlador.especialidadDesdeCie(cie);

        // Si ya hay procedimientos, verificar que la especialidad del nuevo CIE coincida
        if (especialidadBloqueada != null && !nuevaEspecialidad.equalsIgnoreCase(especialidadBloqueada)) {
            mostrarAlertaEspecialidad(nuevaEspecialidad);
            txtCie.clear();
            lblDescCie.setText("");
            lblEspecialidadCie.setText("");
            listaSugerenciasCups.getItems().clear();
            return;
        }

        especialidadActual = nuevaEspecialidad;
        lblEspecialidadCie.setText("Especialidad: " + especialidadActual
                + (especialidadBloqueada != null ? "  ✔ (fijada)" : ""));

        // Cargar sugerencias CUPS
        List<String[]> sugerencias = controlador.recomendarCupsDesdeCie(cie);
        ObservableList<String> items = FXCollections.observableArrayList();

        for (String[] s : sugerencias) {
            if (s.length > 2) {
                items.add(s[1] + " - " + s[2]);
            }
        }

        listaSugerenciasCups.setItems(items);

        // Actualizar instituciones filtradas
        actualizarInstitucionesFiltradas();
    }

    @FXML
    public void onSugerenciaSeleccionada() {

        String seleccionado = listaSugerenciasCups.getSelectionModel().getSelectedItem();

        if (seleccionado != null) {
            String codigo = seleccionado.split(" - ")[0].trim();
            txtCups.setText(codigo);
            onValidarCups(null);
        }
    }

    @FXML
    public void onValidarCups(ActionEvent event) {

        String cups = txtCups.getText().trim().toUpperCase();

        if (cups.isEmpty()) return;

        if (!controlador.existeCups(cups)) {
            mostrarAlerta("Código CUPS no encontrado: " + cups);
            lblDescCups.setText("");
            lblPrecioCups.setText("");
            return;
        }

        String desc = controlador.descripcionCups(cups);
        double precio = controlador.precioCups(cups);

        lblDescCups.setText(desc != null ? desc : "");
        lblPrecioCups.setText(String.format("$%,.0f", precio));
    }

    private void actualizarInstitucionesFiltradas() {

        if (especialidadActual == null) return;

        List<Institucion> instituciones = controlador.institucionesPorEspecialidad(especialidadActual);
        ObservableList<String> nombres = FXCollections.observableArrayList();

        for (Institucion inst : instituciones) {
            nombres.add(inst.getNombre());
        }

        cbInstitucion.setItems(nombres);
        cbInstitucion.getSelectionModel().clearSelection();
        cbInstitucion.setPromptText("Seleccione institución (" + especialidadActual + ")");

        txtMedicoSeleccionado.clear();
        medicoActual = null;
    }


    // Selección médico (ventana emergente)


    @FXML
    public void onSeleccionarMedico(ActionEvent event) {

        String institucion = cbInstitucion.getValue();

        if (institucion == null || institucion.isEmpty()) {
            mostrarAlerta("Seleccione una institución primero.");
            return;
        }

        if (especialidadActual == null) {
            mostrarAlerta("Primero valide un código CIE-10.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/SeleccionMedico.fxml"));
            Parent root = loader.load();

            SeleccionMedicoController ctrl = loader.getController();
            ctrl.setControlador(controlador);
            ctrl.setFiltros(especialidadActual, institucion);
            ctrl.inicializar();

            Stage stage = new Stage();
            stage.setTitle("Seleccionar Médico");
            stage.setScene(new Scene(root, 700, 450));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            Medico seleccionado = ctrl.getMedicoSeleccionado();

            if (seleccionado != null) {
                medicoActual = seleccionado;
                txtMedicoSeleccionado.setText(seleccionado.getNombre());
                lblIdMedico.setText("ID: " + seleccionado.getId());
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al abrir la ventana de médicos.");
        }
    }


    //Agregar procedimiento a la tabla


    @FXML
    public void onAgregarProcedimiento(ActionEvent event) {

        String cups = txtCups.getText().trim().toUpperCase();
        String cie = txtCie.getText().trim().toUpperCase();
        String cantStr = txtCantidad.getText().trim();

        if (cups.isEmpty() || cie.isEmpty()) {
            mostrarAlerta("Ingrese los códigos CUPS y CIE-10.");
            return;
        }

        if (!controlador.existeCups(cups)) {
            mostrarAlerta("Código CUPS no válido.");
            return;
        }

        if (!controlador.existeCie(cie)) {
            mostrarAlerta("Código CIE-10 no válido.");
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantStr);
            if (cantidad <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarAlerta("La cantidad debe ser un número entero positivo.");
            return;
        }

        String descCups = controlador.descripcionCups(cups);
        String descCie = controlador.descripcionCie(cie);
        double precio = controlador.precioCups(cups);

        Procedimiento proc = new Procedimiento(
                cups, cie,
                descCups != null ? descCups : "",
                descCie != null ? descCie : "",
                cantidad, precio);

        // Bloquear la especialidad con el primer procedimiento agregado
        if (especialidadBloqueada == null) {
            especialidadBloqueada = especialidadActual;
        }

        listaProcedimientos.add(proc);
        actualizarTotal();

        txtCups.clear();
        txtCie.clear();
        txtCantidad.clear();
        lblDescCups.setText("");
        lblDescCie.setText("");
        lblPrecioCups.setText("");
        lblEspecialidadCie.setText("Especialidad fijada: " + especialidadBloqueada + "  ✔");
        listaSugerenciasCups.getItems().clear();
    }

    @FXML
    public void onEliminarProcedimiento(ActionEvent event) {

        Procedimiento sel = tablaProcs.getSelectionModel().getSelectedItem();

        if (sel == null) {
            mostrarAlerta("Seleccione un procedimiento para eliminar.");
            return;
        }

        listaProcedimientos.remove(sel);
        actualizarTotal();

        // Si ya no hay procedimientos, liberar el bloqueo de especialidad
        if (listaProcedimientos.isEmpty()) {
            especialidadBloqueada = null;
            especialidadActual = null;
            lblEspecialidadCie.setText("");
        }
    }

    private void actualizarTotal() {

        double total = 0;
        for (Procedimiento p : listaProcedimientos) {
            total += p.getSubtotal();
        }
        lblTotal.setText(String.format("Total: $%,.0f", total));
    }


    //Registrar orden


    @FXML
    public void onRegistrarOrden(ActionEvent event) {

        if (controlador.getPacienteActivo() == null) {
            mostrarAlerta("Busque y valide un paciente primero.");
            return;
        }

        if (listaProcedimientos.isEmpty()) {
            mostrarAlerta("Agregue al menos un procedimiento.");
            return;
        }

        if (cbInstitucion.getValue() == null) {
            mostrarAlerta("Seleccione una institución.");
            return;
        }

        if (medicoActual == null) {
            mostrarAlerta("Seleccione un médico.");
            return;
        }

        Institucion inst = controlador.buscarInstitucion(cbInstitucion.getValue());

        if (inst == null) {
            mostrarAlerta("Institución no encontrada.");
            return;
        }

        List<Procedimiento> procs = new ArrayList<>(listaProcedimientos);
        String resultado = controlador.generarOrden(medicoActual, inst, procs);

        if (resultado.startsWith("OK")) {
            String radicado = resultado.split("\\|")[1];
            mostrarInfo("Orden generada con éxito. Radicado: " + radicado
                    + "\nSe ha generado el PDF en la carpeta ordenes_pdf/");
            limpiarFormularioOrden();
            cargarHistorial();
        } else {
            mostrarAlerta(resultado.split("\\|")[1]);
        }
    }

    private void limpiarFormularioOrden() {

        txtCedulaPaciente.clear();
        panelInfoPaciente.setVisible(false);
        txtCups.clear();
        txtCie.clear();
        txtCantidad.clear();
        lblDescCups.setText("");
        lblDescCie.setText("");
        lblPrecioCups.setText("");
        lblEspecialidadCie.setText("");
        listaSugerenciasCups.getItems().clear();
        cbInstitucion.getSelectionModel().clearSelection();
        cbInstitucion.setItems(FXCollections.observableArrayList());
        txtMedicoSeleccionado.clear();
        lblIdMedico.setText("");
        medicoActual = null;
        especialidadActual = null;
        especialidadBloqueada = null;
        listaProcedimientos.clear();
        lblTotal.setText("Total: $0");
    }


    //Historial


    @FXML
    public void onFiltrarHistorial(ActionEvent event) {

        String cedula = txtFiltroHistorial.getText().trim();

        if (cedula.isEmpty()) {
            cargarHistorial();
            return;
        }

        List<Orden> filtradas = controlador.ordenesPorPaciente(cedula);
        listaOrdenes.setAll(filtradas);
    }

    @FXML
    public void onModificarOrden(ActionEvent event) {

        Orden sel = tablaHistorial.getSelectionModel().getSelectedItem();

        if (sel == null) {
            mostrarAlerta("Seleccione una orden para modificar.");
            return;
        }

        if (!sel.getEstadoOrden().equalsIgnoreCase("ACTIVA")) {
            mostrarAlerta("Solo se pueden modificar órdenes activas.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/ModificarOrden.fxml"));
            Parent root = loader.load();

            ModificarOrdenController ctrl = loader.getController();
            ctrl.setControlador(controlador);
            ctrl.setOrden(sel);
            ctrl.inicializar();

            Stage stage = new Stage();
            stage.setTitle("Modificar Orden #" + sel.getRadicado());
            Scene sceneMod = new Scene(root, 900, 620);
            sceneMod.getStylesheets().add(
                    getClass().getResource("/view/estilos.css").toExternalForm());
            stage.setScene(sceneMod);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            if (ctrl.isGuardado()) {
                cargarHistorial();
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al abrir la ventana de modificación.");
        }
    }

    @FXML
    public void onCancelarOrden(ActionEvent event) {

        Orden sel = tablaHistorial.getSelectionModel().getSelectedItem();

        if (sel == null) {
            mostrarAlerta("Seleccione una orden para cancelar.");
            return;
        }

        if (!sel.getEstadoOrden().equalsIgnoreCase("ACTIVA")) {
            mostrarAlerta("Solo se pueden cancelar órdenes activas.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar cancelación");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Cancelar la orden #" + sel.getRadicado() + "?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            boolean ok = controlador.cancelarOrden(sel.getRadicado());
            if (ok) {
                mostrarInfo("Orden cancelada correctamente.");
                cargarHistorial();
            } else {
                mostrarAlerta("No se pudo cancelar la orden.");
            }
        }
    }

    @FXML
    public void onRefrescarHistorial(ActionEvent event) {
        cargarHistorial();
    }

    private void cargarHistorial() {
        List<Orden> ordenes = controlador.obtenerHistorial();
        listaOrdenes.setAll(ordenes);
    }


    //Cerrar sesión


    @FXML
    public void onCerrarSesion(ActionEvent event) {
        controlador.cerrarSesion();
        MainApp.cargarVista("/view/Login.fxml", "Sistema de Salud - Inicio", 700, 500);
    }


    //Configuración Tablas


    private void configurarTablaProcs() {

        colCups.setCellValueFactory(new PropertyValueFactory<>("cups"));
        colCie.setCellValueFactory(new PropertyValueFactory<>("cie"));
        colDescProc.setCellValueFactory(new PropertyValueFactory<>("descripcionCups"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        tablaProcs.setItems(listaProcedimientos);
    }

    private void configurarTablaHistorial() {

        colRadicado.setCellValueFactory(new PropertyValueFactory<>("radicado"));
        colPaciente.setCellValueFactory(new PropertyValueFactory<>("nombrePaciente"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaEmision"));
        colEspecialidad.setCellValueFactory(new PropertyValueFactory<>("especialidad"));
        colInstitucion.setCellValueFactory(new PropertyValueFactory<>("institucion"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoOrden"));
        colAsesorAccion.setCellValueFactory(new PropertyValueFactory<>("asesorAccion"));

        tablaHistorial.setItems(listaOrdenes);
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAlertaEspecialidad(String nuevaEsp) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Especialidad no permitida");
        alert.setHeaderText("Esta orden ya está fijada a: " + especialidadBloqueada);
        alert.setContentText(
            "El código CIE-10 ingresado corresponde a " + nuevaEsp + ", "
            + "que es una especialidad diferente.\n\n"
            + "Una orden médica solo puede contener procedimientos de una sola especialidad.\n"
            + "Para atender " + nuevaEsp + " debe crear una orden separada."
        );
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
