package pl.pja.edu.s27619.gui_controllers.service;

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
import pl.pja.edu.s27619.schedule.ScheduledTask;
import pl.pja.edu.s27619.vehicle.Vehicle;
import pl.pja.edu.s27619.vehicle.repair.ServiceRecord;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static javafx.collections.FXCollections.observableArrayList;

public class ServiceRecordManageController implements DataReceiver {


    private List<Vehicle> listVehiclesFromDB = new ArrayList<>();
    private List<Client> clients = new ArrayList<>();
    private ObservableList<ServiceRecord> mainList;
    @FXML
    private Button backButtonFromService;
    @FXML
    private Button createButton;
    @FXML
    private TableView<ServiceRecord> serviceList;
    @FXML
    private TableColumn<ServiceRecord, String> serviceRecordId;
    @FXML
    private TableColumn<ServiceRecord, String> descColumn;
    @FXML
    private TableColumn<ServiceRecord, String> clientColumn;
    @FXML
    private TableColumn<ServiceRecord, String> vehicleColumn;
    @FXML
    private TableColumn<ServiceRecord, Double> costColumn;
    @FXML
    private TableColumn<ServiceRecord, Double> discountColumn;
    @FXML
    private TextField findButtonInService;
    @FXML
    private TextField serviceCostField;
    @FXML
    private Button deleteButton;
    @FXML
    private ComboBox<String> serviceRecordClient;
    @FXML
    private Label wrongLabel;
    @FXML
    private Button editButton;
    @FXML
    private ComboBox<String> clientVehicles;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextArea serviceDescArea;
    private Admin admin;


    @FXML
    public void initialize() {
        // Keep UI setup only
        for (TableColumn<?, ?> column : serviceList.getColumns()) {
            column.setResizable(false);
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
            if (obj instanceof Admin) {
                admin = (Admin) obj;
            }
        }
        loadClientsAndVehicles();
        generateComboBoxesAndTableView();

        mainList = getServiceRecordsFromDB();
        serviceList.setItems(mainList);
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
                        createServiceRecord();
                        break;
                    case "deleteButton":
                        deleteServiceRecord();
                        break;
                    case "backButtonFromService":
                        Main.changeScene("/manage_page/manage.fxml", admin);
                        break;
                }
            }
        }
    }

    /**
     * Method which is needed to delete the service record in the system. Automatically delete in the database if chosen
     * service record exists.
     */
    private void deleteServiceRecord() {
        ServiceRecord selected = serviceList.getSelectionModel().getSelectedItem();

        if (selected == null) {
            wrongLabel.setText("No service record selected");
            return;
        }

        Session session = null;

        try {
            session = DatabaseConfigSession.getSessionFactory().openSession();
            session.beginTransaction();

            // Reload ServiceRecord and Vehicle
            ServiceRecord managedRecord = session.get(ServiceRecord.class, selected.getUniqueId());
            if (managedRecord == null) {
                wrongLabel.setText("Service record not found in DB");
                session.getTransaction().rollback();
                return;
            }

            Vehicle vehicle = managedRecord.getVehicle();
            if (vehicle != null) {
                vehicle.getServiceRecords().remove(managedRecord);
            }

            session.remove(managedRecord);

            session.getTransaction().commit();

            // Remove from UI
            mainList.remove(selected);
            serviceList.setItems(mainList);

        } catch (Exception e) {
            e.printStackTrace();
            wrongLabel.setText("Failed to delete service record");
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
     * Method which is needed to create the service record in the system. Automatically add to the database
     * if everything is set properly.
     */
    private void createServiceRecord() {
        String clientEmail = serviceRecordClient.getEditor().getText();
        LocalDate date = datePicker.getValue();
        String description;
        double cost;
        String vehicleName = clientVehicles.getEditor().getText();

        Client client = null;
        Vehicle vehicle = null;

        try {
            description = serviceDescArea.getText();
            cost = Double.parseDouble(serviceCostField.getText());

            if (!clientEmail.isEmpty() || !clientEmail.isBlank()) {
                for (Client ownerFound : clients) {
                    if (ownerFound.getEmail().equals(clientEmail)) {
                        client = ownerFound;
                        break;
                    }
                }
            }

            if (!vehicleName.isBlank() || !vehicleName.isEmpty()) {
                for (Vehicle veh : listVehiclesFromDB) {
                    if (veh.getFullName().equals(vehicleName)) {
                        vehicle = veh;
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

            ServiceRecord serviceRecord = new ServiceRecord(client, date, description, cost, vehicle);

            session.persist(serviceRecord);

            session.merge(client);


            session.getTransaction().commit();

            mainList.add(serviceRecord);
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

        serviceList.setItems(mainList);
    }

    /**
     * Method handle enter pressed on the keyboard for find button. It iterates all service records from the list and
     * find only service records which is should be founded by service name.
     *
     * @param keyEvent contains which key is pressed
     */
    public void enterPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            String serviceRecordToBeFind = findButtonInService.getText().toLowerCase();

            if (serviceRecordToBeFind.isEmpty() || serviceRecordToBeFind.isBlank()) {
                serviceList.setItems(mainList);
            } else {
                ObservableList<ServiceRecord> sortedServices = serviceList.getItems()
                        .stream()
                        .filter(serviceRecord -> serviceRecord.getClient().getEmail().toLowerCase()
                                .contains(serviceRecordToBeFind))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));

                serviceList.setItems(sortedServices);
            }
        }
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

            clients = session.find(Admin.class, admin.getId()).getClients();

            clientEmails = clients.stream()
                            .map(Client::getEmail)
                            .collect(Collectors.toCollection(FXCollections::observableArrayList));

            session.getTransaction().commit();
            session.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return clientEmails;
    }

    private void loadClientsAndVehicles() {
        try (Session session = DatabaseConfigSession.getSessionFactory().openSession()) {
            session.beginTransaction();

            // reload admin to get clients fully initialized
            Admin managedAdmin = session.find(Admin.class, admin.getId());
            clients.clear();
            clients.addAll(managedAdmin.getClients());

            listVehiclesFromDB.clear();
            for (Client client : clients) {
                // force vehicles to load
                client.getClientVehicles().size();
                listVehiclesFromDB.addAll(client.getClientVehicles());
                // force serviceRecords to load for each vehicle
                for (Vehicle vehicle : client.getClientVehicles()) {
                    vehicle.getServiceRecords().size();
                }
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            wrongLabel.setText("Failed to load clients or vehicles");
        }
    }

    /**
     * Method get all vehicles which is registered into the database and set them into list.
     *
     * @return list of vehicles
     */
    private ObservableList<Vehicle> vehiclesFromDB(String clientEmail) {
        ObservableList<Vehicle> vehicles = observableArrayList();

        try {
            Session session = DatabaseConfigSession.getSessionFactory().openSession();

            session.beginTransaction();

            Client client = clients.stream()
                    .filter(c -> c.getEmail().equals(clientEmail))
                    .findFirst()
                    .orElse(null);

            if (client != null) {
                vehicles.addAll(client.getClientVehicles());
                listVehiclesFromDB.addAll(vehicles);
            }

            session.getTransaction().commit();
            session.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return vehicles;
    }

    /**
     * Method generates table for the scene to provide for user information about service records to the user.
     */
    private void generateComboBoxesAndTableView() {
        for (TableColumn<?, ?> column : serviceList.getColumns()) {
            column.setResizable(false);
        }

        serviceRecordId.setCellValueFactory(new PropertyValueFactory<>("uniqueId"));
        descColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        clientColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        vehicleColumn.setCellValueFactory(new PropertyValueFactory<>("vehicleName"));
        costColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));
        discountColumn.setCellValueFactory(new PropertyValueFactory<>("costWithDiscount"));

        ObservableList<String> clients = getClientsFromDB();
        serviceRecordClient.setItems(clients);
    }

    /**
     * Method get all vehicles which is connected to chosen email, it there is no such vehicles, then box will be
     * empty.
     */
    public void selectedClient() {
        String selectedClientEmail = serviceRecordClient.getEditor().getText();

        if (selectedClientEmail.isEmpty() || selectedClientEmail.isBlank()) {
            clientVehicles.setItems(FXCollections.observableArrayList());
            return;
        }

        try {
            ObservableList<Vehicle> vehicles = vehiclesFromDB(selectedClientEmail);

            ObservableList<String> fullNameVehicles = observableArrayList();
            for (Vehicle vehicle : vehicles) {
                fullNameVehicles.add(vehicle.getFullName().toUpperCase());
            }

            clientVehicles.setItems(fullNameVehicles);

        } catch (Exception e) {
            wrongLabel.setText("Something went wrong, try once more");
        }

    }

    /**
     * Method get all service records which is registered into the database and set them into list.
     *
     * @return list of service records
     */
    private ObservableList<ServiceRecord> getServiceRecordsFromDB() {
        ObservableList<ServiceRecord> serviceRecords = observableArrayList();
        for (Client client : clients) {
            for (Vehicle vehicle : client.getClientVehicles()) {
                serviceRecords.addAll(vehicle.getServiceRecords());
            }
        }
        return serviceRecords;
    }

    /**
     * Method initialize the default state of the scene.
     */
    public void resetFields() {
        serviceDescArea.setText("");
        serviceCostField.setText("");

        serviceRecordClient.setValue(null);
        serviceRecordClient.setPromptText("Choose client");

        clientVehicles.setValue(null);
        clientVehicles.setPromptText("Choose client vehicle");

        datePicker.setValue(null);
        datePicker.setPromptText("Choose date");

        wrongLabel.setText("");
    }
}
