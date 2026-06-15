package app;

import controller.BaseController;
import controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static MainController controlador;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        controlador = new MainController();
        cargarVista("/view/Login.fxml", "Sistema de Salud - Inicio", 700, 500);
    }

    public static void cargarVista(String fxmlPath, String titulo, double ancho, double alto) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxmlPath));
            Parent root = loader.load();

            Object fxmlController = loader.getController();
            if (fxmlController instanceof BaseController) {
                ((BaseController) fxmlController).setControlador(controlador);
                ((BaseController) fxmlController).inicializar();
            }

            Scene scene = new Scene(root, ancho, alto);
            scene.getStylesheets().add(
                    MainApp.class.getResource("/view/estilos.css").toExternalForm());

            primaryStage.setTitle(titulo);
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Stage getPrimaryStage() {

        return primaryStage;
    }

    public static MainController getControlador() {

        return controlador;
    }

    public static void main(String[] args) {

        launch(args);
    }
}
