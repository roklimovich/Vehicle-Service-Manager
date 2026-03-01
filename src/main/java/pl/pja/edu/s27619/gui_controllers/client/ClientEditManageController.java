package pl.pja.edu.s27619.gui_controllers.client;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.hibernate.Session;
import pl.pja.edu.s27619.Main;
import pl.pja.edu.s27619.administration.Admin;
import pl.pja.edu.s27619.clients.BasicClient;
import pl.pja.edu.s27619.clients.Client;
import pl.pja.edu.s27619.clients.VIPClient;
import pl.pja.edu.s27619.gui_controllers.dbconfig.DatabaseConfigSession;
import pl.pja.edu.s27619.gui_controllers.interfaces.DataReceiver;
import pl.pja.edu.s27619.vehicle.Vehicle;

import java.util.Arrays;
import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;

public class ClientEditManageController implements DataReceiver {

    private Admin admin;
    private Client client;
    @FXML
    private Label noPromoteLabel;
    @FXML
    private Button promoteToVIPButton;
    @FXML
    private Button backButtonFromEdit;
    @FXML
    private ListView<Vehicle> listClientVehicles;
    @FXML
    private TextField clientLoyaltyPoints;
    @FXML
    private TextField clientDiscount;
    @FXML
    private Button saveButton;
    @FXML
    private TextField clientEmail;

    @FXML
    private TextField clientNameField;
    @FXML
    private TextField clientSurnameField;
    @FXML
    private TextField clientPhoneNumber;


    public void initializeClientFields() {
        if (client == null) return;
        ObservableList<Vehicle> vehicles = getClientVehiclesFromDB();

        clientNameField.setText(client.getName());
        clientSurnameField.setText(client.getSurname());
        clientPhoneNumber.setText(client.getPhoneNumber());
        clientEmail.setText(client.getEmail());
        clientLoyaltyPoints.setText(String.valueOf(client.getLoyaltyPoints()));
        listClientVehicles.setItems(vehicles);
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
            } else if (obj instanceof Client) {
                client = (Client) obj;
                initializeClientFields();
            }
        }
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
                    case "saveButton":
                        saveClient();
                        break;
                    case "backButtonFromEdit":
                        back();
                        break;
                    case "promoteToVIPButton":
                        promoteToVIP();
                        break;
                }

            }
        }
    }

    /**
     * Method handle back button on the scene.
     *
     * @throws Exception if system could not load the new scene
     */
    private void back() throws Exception {
        Main.changeScene("/manage_page/client_page/client.fxml", admin);
    }

    /**
     * Method sets the client to VIP type and merge this client in database.
     *
     * @throws Exception if system could not load the new scene
     */
    private void promoteToVIP() throws Exception {
        if (client instanceof BasicClient) {

            // Keep loyalty points and vehicles
            int loyaltyPoints = client.getLoyaltyPoints();
            List<Vehicle> vehicles = client.getClientVehicles();

            deleteFromDatabase(); // safely remove old client

            // Create new VIP client
            client = new VIPClient(client.getName(), client.getSurname(),
                    client.getPhoneNumber(), client.getEmail());
            client.setAdmin(admin);
            client.setLoyaltyPoints(loyaltyPoints);
            client.getClientVehicles().addAll(vehicles); // keep vehicles

            mergeIntoDatabase(); // safely save new VIP client

            Main.changeScene("/manage_page/client_page/client.fxml", admin);
        } else {
            noPromoteLabel.setText("Already VIP client");
        }
    }

    /**
     * Method saves call method to merge into database edited client.
     *
     * @throws Exception if system could not load the scene
     */
    public void saveClient() throws Exception {
        client.setName(clientNameField.getText());
        client.setSurname(clientSurnameField.getText());
        client.setEmail(clientEmail.getText());
        client.setPhoneNumber(clientPhoneNumber.getText());
        client.setLoyaltyPoints(Integer.parseInt(clientLoyaltyPoints.getText()));

        mergeIntoDatabase(); // safe merge

        Main.changeScene("/manage_page/client_page/client.fxml", admin);
    }

    /**
     * Method which is needed to merge the client in the system. Automatically update database if everything is
     * set properly.
     */
    private void mergeIntoDatabase() {
        try (Session session = DatabaseConfigSession.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Merge returns a managed entity
            client = (Client) session.merge(client);

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method which is needed to delete the client in the system. Automatically delete in the database if chosen
     * client exists.
     */
    private void deleteFromDatabase() {
        try (Session session = DatabaseConfigSession.getSessionFactory().openSession()) {
            session.beginTransaction();

            Client managedClient = session.find(Client.class, client.getId());
            if (managedClient != null) {
                Admin managedAdmin = session.find(Admin.class, admin.getId());
                managedAdmin.removeClient(managedClient); // orphanRemoval deletes automatically
            }

            session.remove(managedClient);
            session.getTransaction().commit();
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Method get all client vehicles which is registered into the database and set them into list.
     *
     * @return list of engines
     */
    private ObservableList<Vehicle> getClientVehiclesFromDB() {
        ObservableList<Vehicle> vehicles = observableArrayList();

        try (Session session = DatabaseConfigSession.getSessionFactory().openSession()) {
            session.beginTransaction();

            Client managedClient = session.find(Client.class, client.getId());
            vehicles.addAll(managedClient.getClientVehicles());

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return vehicles;
    }

}
