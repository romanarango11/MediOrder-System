package controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.*;

import java.util.ArrayList;
import java.util.List;

public class ModificarOrdenController extends BaseController {

    // Información de solo lectura
    @FXML private Label lblTitulo;
    @FXML private Label lblRadicado;
    @FXML private Label lblPaciente;
    @FXML private Label lblFecha;
    @FXML private Label lblEstado;

    // Institución y médico
    @FXML private Label     lblEspecialidadMod;
    @FXML private ComboBox<String> cbInstitucionMod;
    @FXML private TextField txtMedicoMod;
    @FXML private Label     lblIdMedicoMod;

    // Agregar procedimiento
    @FXML private TextField txtCupsMod;
    @FXML private TextField txtCieMod;
    @FXML private TextField txtCantidadMod;
    @FXML private Label     lblDescProcMod;

    // Tabla de procedimientos
    @FXML private TableView<Procedimiento>              tablaProcsMod;
    @FXML private TableColumn<Procedimiento, String>  colCupsMod;
    @FXML private TableColumn<Procedimiento, String>  colCieMod;
    @FXML private TableColumn<Procedimiento, String>  colDescMod;
    @FXML private TableColumn<Procedimiento, Integer> colCantMod;
    @FXML private TableColumn<Procedimiento, Double>  colValorMod;
    @FXML private TableColumn<Procedimiento, Double>  colSubtotalMod;

    @FXML private Label lblTotalMod;

    // Estado interno
    private Orden ordenActual;
    private Medico medicoSeleccionado;
    private String especialidadActual;
    private boolean guardado = false;

    private ObservableList<Procedimiento> listaProcedimientos = FXCollections.observableArrayList();


    //Inicialización


    public void setOrden(Orden orden) {

        this.ordenActual = orden;
    }

    @Override
    public void inicializar() {
        configurarTabla();
        cargarDatosOrden();
    }

    private void configurarTabla() {
        colCupsMod.setCellValueFactory(new PropertyValueFactory<>("cups"));
        colCieMod.setCellValueFactory(new PropertyValueFactory<>("cie"));
        colDescMod.setCellValueFactory(new PropertyValueFactory<>("descripcionCups"));
        colCantMod.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colValorMod.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colSubtotalMod.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        tablaProcsMod.setItems(listaProcedimientos);
    }

    private void cargarDatosOrden() {
        lblTitulo.setText("Modificar Orden #" + ordenActual.getRadicado());
        lblRadicado.setText(String.valueOf(ordenActual.getRadicado()));
        lblPaciente.setText(ordenActual.getNombrePaciente());
        lblFecha.setText(ordenActual.getFechaEmision());
        lblEstado.setText(ordenActual.getEstadoOrden());

        especialidadActual = ordenActual.getEspecialidad();
        lblEspecialidadMod.setText("Especialidad: " + especialidadActual);

        // Cargar instituciones filtradas por especialidad
        cargarInstituciones();

        // Preseleccionar institución actual
        cbInstitucionMod.setValue(ordenActual.getInstitucion());

        // Precargar médico actual
        txtMedicoMod.setText(ordenActual.getMedicoNombre());
        lblIdMedicoMod.setText("ID: " + ordenActual.getMedicoId());

        // Crear objeto Medico con los datos actuales de la orden
        medicoSeleccionado = new Medico(
                ordenActual.getMedicoId(),
                ordenActual.getMedicoNombre(),
                ordenActual.getEspecialidad(),
                ordenActual.getInstitucion());

        // Cargar procedimientos actuales
        listaProcedimientos.setAll(ordenActual.getProcedimientos());
        actualizarTotal();
    }

    private void cargarInstituciones() {
        List<Institucion> instituciones = controlador.institucionesPorEspecialidad(especialidadActual);
        ObservableList<String> nombres = FXCollections.observableArrayList();
        for (Institucion inst : instituciones) {
            nombres.add(inst.getNombre());
        }
        cbInstitucionMod.setItems(nombres);
    }


    //Cambio de institución


    @FXML
    public void onCambiarInstitucion(ActionEvent event) {
        // Al cambiar institución, limpiar médico para que el usuario lo reseleccione
        String nuevaInst = cbInstitucionMod.getValue();
        if (nuevaInst != null && !nuevaInst.equals(ordenActual.getInstitucion())) {
            txtMedicoMod.clear();
            lblIdMedicoMod.setText("");
            medicoSeleccionado = null;
        }
    }


    //Selección Médico


    @FXML
    public void onSeleccionarMedicoMod(ActionEvent event) {
        String institucion = cbInstitucionMod.getValue();

        if (institucion == null || institucion.isEmpty()) {
            mostrarAlerta("Seleccione una institución primero.");
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
                medicoSeleccionado = seleccionado;
                txtMedicoMod.setText(seleccionado.getNombre());
                lblIdMedicoMod.setText("ID: " + seleccionado.getId());
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al abrir la ventana de médicos.");
        }
    }


    //Agregar Procedimiento

    @FXML
    public void onAgregarProcMod(ActionEvent event) {
        String cups    = txtCupsMod.getText().trim().toUpperCase();
        String cie     = txtCieMod.getText().trim().toUpperCase();
        String cantStr = txtCantidadMod.getText().trim();

        if (cups.isEmpty() || cie.isEmpty()) {
            mostrarAlerta("Ingrese los códigos CUPS y CIE-10.");
            return;
        }

        if (!controlador.existeCups(cups)) {
            mostrarAlerta("Código CUPS no encontrado: " + cups);
            return;
        }

        if (!controlador.existeCie(cie)) {
            mostrarAlerta("Código CIE-10 no encontrado: " + cie);
            return;
        }
        // Validar que la especialidad del nuevo CIE coincida con la de la orden
        String espNueva = controlador.especialidadDesdeCie(cie);
        if (!espNueva.equalsIgnoreCase(ordenActual.getEspecialidad())) {
            mostrarAlerta("El código CIE-10 " + cie + " corresponde a " + espNueva + ".\n" +
                    "Esta orden es de " + ordenActual.getEspecialidad() + ".\n" +
                    "No se pueden mezclar especialidades en una misma orden.");
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
        String descCie  = controlador.descripcionCie(cie);
        double precio   = controlador.precioCups(cups);

        Procedimiento proc = new Procedimiento(
                cups, cie,
                descCups != null ? descCups : "",
                descCie  != null ? descCie  : "",
                cantidad, precio);

        listaProcedimientos.add(proc);
        actualizarTotal();

        lblDescProcMod.setText("✓ Agregado: " + (descCups != null ? descCups : cups));
        txtCupsMod.clear();
        txtCieMod.clear();
        txtCantidadMod.clear();
    }


    //Eliminar Procedimiento


    @FXML
    public void onEliminarProcMod(ActionEvent event) {
        Procedimiento sel = tablaProcsMod.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Seleccione un procedimiento para eliminar.");
            return;
        }
        listaProcedimientos.remove(sel);
        actualizarTotal();
    }

    private void actualizarTotal() {
        double total = 0;
        for (Procedimiento p : listaProcedimientos) {
            total += p.getSubtotal();
        }
        lblTotalMod.setText(String.format("Total: $%,.0f", total));
    }


    //Guardar Cambios


    @FXML
    public void onGuardarCambios(ActionEvent event) {
        if (medicoSeleccionado == null) {
            mostrarAlerta("Seleccione un médico.");
            return;
        }

        if (cbInstitucionMod.getValue() == null) {
            mostrarAlerta("Seleccione una institución.");
            return;
        }

        if (listaProcedimientos.isEmpty()) {
            mostrarAlerta("Debe haber al menos un procedimiento en la orden.");
            return;
        }

        Institucion inst = controlador.buscarInstitucion(cbInstitucionMod.getValue());
        if (inst == null) {
            mostrarAlerta("Institución no encontrada en el sistema.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar modificación");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Guardar los cambios en la orden #" + ordenActual.getRadicado() + "?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            List<Procedimiento> procs = new ArrayList<>(listaProcedimientos);
            boolean ok = controlador.modificarOrden(
                    ordenActual.getRadicado(),
                    medicoSeleccionado,
                    inst,
                    procs);

            if (ok) {
                guardado = true;
                mostrarInfo("Orden modificada correctamente.");
                cerrarVentana();
            } else {
                mostrarAlerta("No se pudo guardar los cambios.");
            }
        }
    }


    //Cancelar


    @FXML
    public void onCancelarMod(ActionEvent event) {
        guardado = false;
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) lblRadicado.getScene().getWindow();
        stage.close();
    }

    public boolean isGuardado() {
        return guardado;
    }


    //Helpers


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
