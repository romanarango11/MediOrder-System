package controller;

import app.MainApp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class LoginController extends BaseController {

    @FXML private TextField txtIdAsesor;

    @FXML
    public void onIniciarAsesor(ActionEvent event) {
        String id = txtIdAsesor.getText().trim();
        if (id.isEmpty()) {
            mostrarAlerta("Por favor ingrese su ID de asesor.");
            return;
        }
        String resultado = controlador.loginAsesor(id);
        if (resultado.startsWith("OK")) {
            MainApp.cargarVista("/view/Principal.fxml",
                    "Sistema de Salud - " + resultado.split("\\|")[1], 1100, 750);
        } else {
            mostrarAlerta(resultado.split("\\|")[1]);
        }
    }

    @FXML
    public void onIrRegistro(ActionEvent event) {
        MainApp.cargarVista("/view/Registro.fxml",
                "Sistema de Salud - Registro de Paciente", 1000, 680);
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
