package controller;


 // Clase base que deben extender todos los controladores FXML.
 // Permite inyectar el MainController desde MainApp.

public abstract class BaseController {

    protected MainController controlador;

    public void setControlador(MainController controlador) {

        this.controlador = controlador;
    }


    // Se llama después de setControlador para cargar datos iniciales.

    public void inicializar() {
        // Opcional: sobreescribir en cada controlador FXML
    }
}
