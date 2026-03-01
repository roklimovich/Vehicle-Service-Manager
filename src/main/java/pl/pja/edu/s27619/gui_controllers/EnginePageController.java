package pl.pja.edu.s27619.gui_controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.hibernate.Session;
import pl.pja.edu.s27619.Main;
import pl.pja.edu.s27619.administration.Admin;
import pl.pja.edu.s27619.gui_controllers.dbconfig.DatabaseConfigSession;
import pl.pja.edu.s27619.gui_controllers.interfaces.DataReceiver;
import pl.pja.edu.s27619.vehicle.component.EmissionLevel;
import pl.pja.edu.s27619.vehicle.component.Engine;
import pl.pja.edu.s27619.vehicle.component.EngineCategory;
import pl.pja.edu.s27619.vehicle.component.EngineType;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static javafx.collections.FXCollections.observableArrayList;

public class EnginePageController implements DataReceiver {

    @FXML
    private Label wrongLabel;
    @FXML
    private Button backButton;
    @FXML
    private Button createButton;
    @FXML
    private TableView<Engine> engineList;
    @FXML
    private TextField findButton;
    @FXML
    private Button deleteButton;
    @FXML
    private TextField engineNameField;
    @FXML
    private ComboBox<EngineType> comboBoxType;
    @FXML
    private ComboBox<EmissionLevel> comboBoxLevel;
    @FXML
    private ComboBox<EngineCategory> comboBoxCategory;
    @FXML
    private TableColumn<Engine, Long> engineIdColumn;
    @FXML
    private TableColumn<Engine, String> engineNameColumn;
    @FXML
    private TableColumn<Engine, String> typeEngineColumn;
    @FXML
    private TableColumn<Engine, String> levelEngineColumn;
    @FXML
    private TableColumn<Engine, String> categoryEngineColumn;
    @FXML
    private TableColumn<Engine, Integer> powerEngineColumn;
    @FXML
    private TextField powerLabel;
    private Admin admin;
    private ObservableList<Engine> mainList;

    public EnginePageController() {}

    /**
     * Method handle left mouse button when it was clicked and change the state based on clicked button.
     *
     * @param mouseEvent contains information about which mouse button was pressed
     * @throws Exception if system could not load the scene
     */
    public void leftMouseClicked(MouseEvent mouseEvent) throws Exception {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            Object source = mouseEvent.getSource();

            if (source instanceof Button clickedButton) {
                String buttonId = clickedButton.getId();
                switch (buttonId) {
                    case "createButton":
                        createEngine();
                        break;
                    case "deleteButton":
                        deleteEngine();
                        break;
                    case "backButton":
                        Main.changeScene("/manage_page/manage.fxml", admin);
                        break;
                }
            }
        }
    }

    /**
     * Method which is needed to create the engine in the system. Automatically add to the database if everything is
     * set properly.
     */
    private void createEngine() {
        String name;
        String type = comboBoxType.getEditor().getText();
        String level = comboBoxLevel.getEditor().getText();
        String category = comboBoxCategory.getEditor().getText();
        int power;

        EngineType engineType;
        EngineCategory engineCategory;
        EmissionLevel emissionLevel;

        try {
            name = engineNameField.getText();
            engineType = EngineType.valueOf(type);
            engineCategory = EngineCategory.valueOf(category);
            emissionLevel = EmissionLevel.valueOf(level);
            power = Integer.parseInt(powerLabel.getText());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            resetFields();
            wrongLabel.setText("Wrong data, try once again");
            return;
        }

        Session session = null;

        try {
            session = DatabaseConfigSession.getSessionFactory().openSession();

            session.beginTransaction();

            Admin managedAdmin = session.find(Admin.class, admin.getId());
            Engine engine = new Engine(name, engineType, emissionLevel, engineCategory, power);
            managedAdmin.addEngine(engine);

            session.getTransaction().commit();

            mainList.add(engine);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }

        resetFields();

        engineList.setItems(mainList);
    }

    /**
     * Method which is needed to delete the engine in the system. Automatically delete in the database if chosen
     * engine exists.
     */
    private void deleteEngine() {
        Engine engineToBeDeleted = engineList.getSelectionModel().getSelectedItem();

        if (engineToBeDeleted != null) {

            Session session = null;

            try {
                session = DatabaseConfigSession.getSessionFactory().openSession();

                session.beginTransaction();

                Admin managedAdmin = session.find(Admin.class, admin.getId());
                Engine managedEngine = session.find(Engine.class, engineToBeDeleted.getId());

                managedAdmin.removeEngine(managedEngine);

                session.getTransaction().commit();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                if (session != null && session.getTransaction().isActive()) {
                    session.getTransaction().rollback();
                }
            } finally {
                if (session != null) {
                    session.close();
                }
            }

            mainList.remove(engineToBeDeleted);
            engineList.setItems(mainList);
        } else {
            System.out.println("No selected engine to delete");
        }

    }

    /**
     * Method generates table for the scene to provide for user information about engines to the user.
     */
    public void generateTableViewAndComBoxes() {
        for (TableColumn<?, ?> column : engineList.getColumns()) {
            column.setResizable(false);
        }

        engineIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        engineNameColumn.setCellValueFactory(new PropertyValueFactory<>("engineName"));
        typeEngineColumn.setCellValueFactory(new PropertyValueFactory<>("engineType"));
        levelEngineColumn.setCellValueFactory(new PropertyValueFactory<>("emissionLevel"));
        categoryEngineColumn.setCellValueFactory(new PropertyValueFactory<>("engineCategory"));
        powerEngineColumn.setCellValueFactory(new PropertyValueFactory<>("power"));


        EngineType[] engineTypes = EngineType.values();
        ObservableList<EngineType> types = observableArrayList();
        types.setAll(engineTypes);
        comboBoxType.setItems(types);

        EmissionLevel[] engineLevels = EmissionLevel.values();
        ObservableList<EmissionLevel> levels = observableArrayList();
        levels.setAll(engineLevels);
        comboBoxLevel.setItems(levels);

        EngineCategory[] engineCategories = EngineCategory.values();
        ObservableList<EngineCategory> categories = observableArrayList();
        categories.setAll(engineCategories);
        comboBoxCategory.setItems(categories);
    }

    /**
     * Method sets user details which is logged into the system using interface.
     *
     * @param data combined variable which can contains more than 1 Object
     */
    @Override
    public void setUserDetailsToLabel(Object... data) {
        Object[] objects = Arrays.stream(data).toArray();

        for (Object obj : objects) {
            admin = (Admin) obj;
        }

        generateTableViewAndComBoxes();

        mainList = enginesFromDB();
        engineList.setItems(mainList);
    }

    /**
     * Method get all engines which is registered into the database and set them into list.
     *
     * @return list of engines
     */
    private ObservableList<Engine> enginesFromDB() {
        ObservableList<Engine> engineList = observableArrayList();

        try {
            Session session = DatabaseConfigSession.getSessionFactory().openSession();

            session.beginTransaction();

            Admin managedAdmin = session.find(Admin.class, admin.getId());
            managedAdmin.getEngines().size();

            engineList.addAll(managedAdmin.getEngines());

            session.getTransaction().commit();
            session.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return engineList;
    }

    /**
     * Method handle enter pressed on the keyboard for find button. It iterates all engines from the list and find
     * only engine which is should be founded by engine name.
     *
     * @param keyEvent contains which key is pressed
     */
    public void enterPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            String engineToBeFind = findButton.getText().toLowerCase();

            if (engineToBeFind.isEmpty() || engineToBeFind.isBlank()) {
                engineList.setItems(mainList);
            } else {
                ObservableList<Engine> sortedEngine = engineList.getItems()
                        .stream()
                        .filter(engine -> engine.getEngineName().toLowerCase().contains(engineToBeFind))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));

                engineList.setItems(sortedEngine);
            }
        }
    }

    /**
     * Method initialize the default state of the scene.
     */
    public void resetFields() {
        wrongLabel.setText("");

        engineNameField.setText("");

        comboBoxType.setValue(null);
        comboBoxType.setPromptText("Choose engine type");

        comboBoxLevel.setValue(null);
        comboBoxLevel.setPromptText("Choose engine level");

        comboBoxCategory.setValue(null);
        comboBoxCategory.setPromptText("Choose engine category");

        powerLabel.setText("");
    }

}
