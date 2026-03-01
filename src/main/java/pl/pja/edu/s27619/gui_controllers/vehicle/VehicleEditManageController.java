package pl.pja.edu.s27619.gui_controllers.vehicle;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

import java.util.Arrays;
import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;

public class VehicleEditManageController implements DataReceiver {
    private List<Engine> enginesFromDB;
    private List<Client> clientsFromDB;
    private Admin admin;
    private Vehicle vehicle;
    @FXML
    private Button backButtonFromEditVehicle;
    @FXML
    private Button saveButton;
    @FXML
    private TextField vehicleColorLabel;
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
    private ComboBox<String> vehicleOwnerComboBox;

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
                    case "saveButton":
                        saveVehicle();
                        break;
                    case "backButtonFromEditVehicle":
                        back();
                        break;
                }

            }
        }
    }

    /**
     * Method handles backButton and change the scene with proper data.
     *
     * @throws Exception is system could not load the scene
     */
    private void back() throws Exception {
        Main.changeScene("/manage_page/vehicle_page/vehicle.fxml", admin);
    }

    /**
     *
     */
    private void saveVehicle() {

        try {
            String type = comboBoxVehicleType.getEditor().getText();
            String engineText = vehicleEngineComboBox.getEditor().getText();
            String ownerText = vehicleOwnerComboBox.getEditor().getText();

            vehicle.setVehicleType(VehicleType.valueOf(type));
            vehicle.setName(vehicleNameField.getText());
            vehicle.setModel(vehicleModelField.getText());
            vehicle.setColor(vehicleColorLabel.getText());

            Engine engine = null;
            Client owner = null;

            if (!engineText.isEmpty() || !engineText.isBlank()) {
                for (Engine engineFound : enginesFromDB) {
                    if (engineFound.getEngineName().equals(engineText)) {
                        engine = engineFound;
                        vehicle.setEngine(engine);
                        break;
                    }
                }
            }

            if (!ownerText.isEmpty() || !ownerText.isBlank()) {
                for (Client ownerFound : clientsFromDB) {
                    if (ownerFound.getEmail().equals(ownerText)) {
                        owner = ownerFound;
                        vehicle.setOwner(owner);
                        break;
                    }
                }
            }

            mergeIntoDatabase();

            Main.changeScene("/manage_page/vehicle_page/vehicle.fxml", admin);
        } catch (Exception e) {
            initializeVehicleFields();
            wrongLabel.setText("Wrong data, try once again");

        }


    }

    /**
     * Method to merge vehicle with edited fields, and merge in the database automatically if everything sets properly.
     */
    private void mergeIntoDatabase() {
        Session session = null;

        try {
            session = DatabaseConfigSession.getSessionFactory().openSession();

            session.beginTransaction();

            session.merge(vehicle);

            session.getTransaction().commit();

            session.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            wrongLabel.setText("Could not save, try once again");
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }


    }

    /**
     * Method generates and initialize information for fields to the user.
     */
    public void initializeVehicleFields() {

        comboBoxVehicleType.setValue(vehicle.getVehicleType());
        vehicleNameField.setText(vehicle.getName());
        vehicleModelField.setText(vehicle.getModel());
        vehicleEngineComboBox.setValue(vehicle.getEngine().getEngineName());
        vehicleColorLabel.setText(vehicle.getColor());
        vehicleOwnerComboBox.setValue(vehicle.getOwnerEmail());

        VehicleType[] vehicleTypes = VehicleType.values();
        ObservableList<VehicleType> types = observableArrayList();
        types.setAll(vehicleTypes);
        comboBoxVehicleType.setItems(types);

        ObservableList<String> engines = getEnginesFromDB();
        vehicleEngineComboBox.setItems(engines);

        ObservableList<String> clients = getClientsFromDB();
        vehicleOwnerComboBox.setItems(clients);

        wrongLabel.setText("");
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
     * Method sets admin or user which is logged into the system.
     *
     * @param data combined variable which can contains more than 1 Object
     */
    @Override
    public void setUserDetailsToLabel(Object... data) {
        System.out.println("setUserDetailsToLabel called with: " + Arrays.toString(data));
        Object[] objects = Arrays.stream(data).toArray();

        for (Object obj : objects) {
            if (obj instanceof Admin) {
                admin = (Admin) obj;
            } else if (obj instanceof Vehicle) {
                vehicle = (Vehicle) obj;
            }
        }

        initializeVehicleFields();
    }
}
