package controller;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Medico;

import java.util.List;

public class SeleccionMedicoController extends BaseController {

    @FXML private Label lblFiltro;
    @FXML private TableView<Medico> tablaMedicos;
    @FXML private TableColumn<Medico, String> colId;
    @FXML private TableColumn<Medico, String> colNombre;
    @FXML private TableColumn<Medico, String> colEspecialidad;
    @FXML private TableColumn<Medico, String> colInstitucion;

    private String especialidad;
    private String institucion;
    private Medico medicoSeleccionado;

    private ObservableList<Medico> listaMedicos = FXCollections.observableArrayList();

    public void setFiltros(String especialidad, String institucion) {
        this.especialidad = especialidad;
        this.institucion = institucion;
    }

    public void setControlador(MainController controlador) {
        this.controlador = controlador;
    }

    @Override
    public void inicializar() {

        lblFiltro.setText("Especialidad: " + especialidad + "  |  Institución: " + institucion);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEspecialidad.setCellValueFactory(new PropertyValueFactory<>("especialidad"));
        colInstitucion.setCellValueFactory(new PropertyValueFactory<>("institucion"));

        tablaMedicos.setItems(listaMedicos);

        cargarMedicos();
    }

    private void cargarMedicos() {

        List<Medico> lista = controlador.medicosPorEspecialidadEInstitucion(especialidad, institucion);
        listaMedicos.setAll(lista);

        if (lista.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION,
                    "No hay médicos disponibles con esa especialidad en la institución seleccionada.")
                    .showAndWait();
        }
    }

    @FXML
    public void onSeleccionar(ActionEvent event) {

        Medico sel = tablaMedicos.getSelectionModel().getSelectedItem();

        if (sel == null) {
            new Alert(Alert.AlertType.WARNING, "Seleccione un médico de la tabla.").showAndWait();
            return;
        }

        medicoSeleccionado = sel;
        cerrarVentana();
    }

    @FXML
    public void onCancelar(ActionEvent event) {
        medicoSeleccionado = null;
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) tablaMedicos.getScene().getWindow();
        stage.close();
    }

    public Medico getMedicoSeleccionado() {

        return medicoSeleccionado;
    }
}
