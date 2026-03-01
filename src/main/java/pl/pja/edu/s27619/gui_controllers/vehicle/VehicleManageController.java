package pl.pja.edu.s27619.gui_controllers.vehicle;

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
import pl.pja.edu.s27619.clients.Client;
import pl.pja.edu.s27619.gui_controllers.dbconfig.DatabaseConfigSession;
import pl.pja.edu.s27619.gui_controllers.interfaces.DataReceiver;
import pl.pja.edu.s27619.vehicle.Vehicle;
import pl.pja.edu.s27619.vehicle.VehicleType;
import pl.pja.edu.s27619.vehicle.component.Engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static javafx.collections.FXCollections.observableArrayList;

public class VehicleManageController implements DataReceiver {

    private List<Engine> enginesFromDB = new ArrayList<>();
    private List<Client> clientsFromDB = new ArrayList<>();

    private Admin admin;
    private ObservableList<Vehicle> mainList;

    @FXML
    private ComboBox<String> vehicleOwnerComboBox;
    @FXML
    private Button backButtonFromVehicle;
    @FXML
    private Button createButton;
    @FXML
    private TableView<Vehicle> vehicleList;
    @FXML
    private TableColumn<Vehicle, String> vehicleIdColumn;
    @FXML
    private TableColumn<Vehicle, String> typeVehicleColumn;
    @FXML
    private TableColumn<Vehicle, String> vehicleNameColumn;
    @FXML
    private TableColumn<Vehicle, String> vehicleModelColumn;
    @FXML
    private TableColumn<Vehicle, String> ownerColumn;
    @FXML
    private TableColumn<Vehicle, String> vehicleColorColumn;
    @FXML
    private TextField findButtonInVehicle;
    @FXML
    private TextField vehicleColorLabel;
    @FXML
    private Button deleteButton;
    @FXML
    private ComboBox<VehicleType> comboBoxVehicleType;
    @FXML
    private ComboBox<String> vehicleEngineComboBox;
    @FXML
    private Label wrongLabel;
    @FXML
    private TextField vehicleNameField;
    @FXML
    private TextField vehicleModelField;
    @FXML
    private Button editButton;



    /**
     * Method generates table for the scene to provide for user information about engines to the user.
     */
    private void generateTableViewAndComBoxes() {
        for (TableColumn<?, ?> column : vehicleList.getColumns()) {
            column.setResizable(false);
        }

        vehicleIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        typeVehicleColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleType"));
        vehicleNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        vehicleModelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        vehicleColorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
        ownerColumn.setCellValueFactory(new PropertyValueFactory<>("ownerEmail"));

        VehicleType[] vehicleTypes = VehicleType.values();
        ObservableList<VehicleType> types = observableArrayList();
        types.setAll(vehicleTypes);
        comboBoxVehicleType.setItems(types);

        ObservableList<String> engines = getEnginesFromDB();
        vehicleEngineComboBox.setItems(engines);

        ObservableList<String> clients = getClientsFromDB();
        vehicleOwnerComboBox.setItems(clients);
    }

    /**
     * Method get all vehicle which is registered into the database and set them into list.
     *
     * @return list of vehicles
     */
    private ObservableList<Vehicle> vehiclesFromDB() {
        ObservableList<Vehicle> vehicles = observableArrayList();

        try {
            Session session = DatabaseConfigSession.getSessionFactory().openSession();

            session.beginTransaction();

            Admin managedAdmin = session.find(Admin.class, admin.getId());

            List<Vehicle> vehiclesFromDB = managedAdmin.getClients().stream()
                            .flatMap(client -> client.getClientVehicles().stream())
                                    .toList();

            vehicles.addAll(vehiclesFromDB);

            session.getTransaction().commit();
            session.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return vehicles;
    }

    /**
     * Method get all engines which is registered into the database and set them into list.
     *
     * @return list of engines
     */
    private ObservableList<String> getEnginesFromDB() {
        ObservableList<String> engineNames = observableArrayList();

        try {
            Session session = DatabaseConfigSession.getSessionFactory().openSession();

            session.beginTransaction();

            Admin managedAdmin = session.find(Admin.class, admin.getId());
            managedAdmin.getEngines().size();

            enginesFromDB.addAll(managedAdmin.getEngines());

            session.getTransaction().commit();
            session.close();

            for (Engine engine : enginesFromDB) {
                engineNames.add(engine.getEngineName());
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return engineNames;
    }

    /**
     * Method get all clients which is registered into the database and set them into list.
     *
     * @return list of clients
     */
    private ObservableList<String> getClientsFromDB() {
        ObservableList<String> clientEmails = observableArrayList();

        try {
            Session session = DatabaseConfigSession.getSessionFactory().openSession();

            session.beginTransaction();

            Admin managedAdmin = session.find(Admin.class, admin.getId());

            clientsFromDB = managedAdmin.getClients();

            session.getTransaction().commit();
            session.close();

            for (Client client : clientsFromDB) {
                clientEmails.add(client.getEmail());
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return clientEmails;
    }

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
                        createVehicle();
                        break;
                    case "deleteButton":
                        deleteVehicle();
                        break;
                    case "editButton":
                        editVehicle();
                        break;
                    case "backButtonFromVehicle":
                        Main.changeScene("/manage_page/manage.fxml", admin);
                        break;
                }
            }
        }
    }

    /**
     * Method download new scene to edit the chosen vehicle.
     *
     * @throws Exception if system could not load new scene
     */
    private void editVehicle() throws Exception {
        Vehicle vehicle = vehicleList.getSelectionModel().getSelectedItem();

        if (vehicle != null) {
            Main.changeScene("/manage_page/vehicle_page/edit_page/edit_vehicle.fxml", admin, vehicle);
        } else {
            wrongLabel.setText("No selected item to edit");
        }

    }

    /**
     * Method which is needed to create the vehicle in the system. Automatically add to the database if everything is
     * set properly.
     */
    private void createVehicle() {
        String type = comboBoxVehicleType.getEditor().getText();
        String name;
        String model;
        String engineText = vehicleEngineComboBox.getEditor().getText();
        String color;
        String ownerText = vehicleOwnerComboBox.getEditor().getText();

        Engine engine = null;
        VehicleType vehicleType;
        Client owner = null;

        try {
            vehicleType = VehicleType.valueOf(type);
            name = vehicleNameField.getText();
            model = vehicleModelField.getText();

            color = vehicleColorLabel.getText();

            if (!engineText.isEmpty() || !engineText.isBlank()) {
                for (Engine engineFound : enginesFromDB) {
                    if (engineFound.getEngineName().equals(engineText)) {
                        engine = engineFound;
                        System.out.println(engine.getEngineName());
                        break;
                    }
                }
            }

            if (!ownerText.isEmpty() || !ownerText.isBlank()) {
                for (Client ownerFound : clientsFromDB) {
                    if (ownerFound.getEmail().equals(ownerText)) {
                        owner = ownerFound;
                        break;
                    }
                }
            }

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

            Vehicle vehicle = new Vehicle(vehicleType, name, model, engine, color);
            vehicle.setOwner(owner);

            session.persist(vehicle);


            session.getTransaction().commit();

            mainList.add(vehicle);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            wrongLabel.setText("Something went wrong, try once again");
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }

        resetFields();

        vehicleList.setItems(mainList);
    }

    /**
     * Method handle enter pressed on the keyboard for find button. It iterates all vehicles from the list and find
     * only vehicle which is should be founded by vehicle name.
     *
     * @param keyEvent contains which key is pressed
     */
    public void enterPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            String vehicleToBeFind = findButtonInVehicle.getText().toLowerCase();

            if (vehicleToBeFind.isEmpty() || vehicleToBeFind.isBlank()) {
                vehicleList.setItems(mainList);
            } else {
                ObservableList<Vehicle> sortedVehicle = vehicleList.getItems()
                        .stream()
                        .filter(vehicle -> vehicle.getName().toLowerCase().contains(vehicleToBeFind))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));

                vehicleList.setItems(sortedVehicle);
            }
        }
    }

    /**
     * Method initialize the default state of the scene.
     */
    public void resetFields() {
        vehicleNameField.setText("");

        comboBoxVehicleType.setValue(null);
        comboBoxVehicleType.setPromptText("Choose vehicle type");

        vehicleModelField.setText("");
        
        vehicleEngineComboBox.setValue(null);
        vehicleEngineComboBox.setPromptText("Choose engine category");

        vehicleOwnerComboBox.setValue(null);
        vehicleEngineComboBox.setPromptText("Choose vehicle owner");

        vehicleColorLabel.setText("");
    }

    /**
     * Method which is needed to delete the vehicle in the system. Automatically delete in the database if chosen
     * vehicle exists.
     */
    private void deleteVehicle() {
        Vehicle vehicleToBeDeleted = vehicleList.getSelectionModel().getSelectedItem();

        if (vehicleToBeDeleted != null) {

            Session session = null;

            try {
                session = DatabaseConfigSession.getSessionFactory().openSession();

                session.beginTransaction();

                session.remove(vehicleToBeDeleted);

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

            mainList.remove(vehicleToBeDeleted);
            vehicleList.setItems(mainList);
        } else {
            System.out.println("No selected vehicle to delete");
        }
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

        mainList = vehiclesFromDB();
        vehicleList.setItems(mainList);
    }
}
