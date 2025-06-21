package pl.pja.edu.s27619;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import pl.pja.edu.s27619.gui_controllers.dbconfig.DatabaseConfigSession;
import pl.pja.edu.s27619.gui_controllers.interfaces.DataReceiver;


public class Main extends Application {

    private static Stage stage;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        primaryStage.setResizable(false);
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/welcome_page/welcome.fxml")
        );
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("ZR PERFORMANCE");
        primaryStage.getIcons().add(new Image("/welcome_page/img/zr_performance_logo_icon.png"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void changeScene(String fxmlPath) throws Exception {
        Parent pane = FXMLLoader.load(Main.class.getResource(fxmlPath));
        stage.getScene().setRoot(pane);
    }

    public static void changeScene(String fxmlPath, Object ... data) throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlPath));
        Parent pane = loader.load();

        Object controller = loader.getController();
        if (controller instanceof DataReceiver) {
            ((DataReceiver) controller).setUserDetailsToLabel(data);
        }

        stage.getScene().setRoot(pane);

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        DatabaseConfigSession.shutdown();
    }
}

