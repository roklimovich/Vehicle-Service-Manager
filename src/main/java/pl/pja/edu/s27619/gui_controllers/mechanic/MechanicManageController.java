package pl.pja.edu.s27619.gui_controllers.mechanic;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.hibernate.Session;
import pl.pja.edu.s27619.Main;
import pl.pja.edu.s27619.administration.Admin;
import pl.pja.edu.s27619.gui_controllers.dbconfig.DatabaseConfigSession;
import pl.pja.edu.s27619.gui_controllers.interfaces.DataReceiver;
import pl.pja.edu.s27619.gui_controllers.interfaces.TableGenerator;
import pl.pja.edu.s27619.service.Mechanic;

import java.util.Arrays;
import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;

public class MechanicManageController implements DataReceiver, TableGenerator {

    private ObservableList<Mechanic> mainList;
    @FXML
    private ComboBox<String> mechanicType;
    @FXML
    private Button backButtonFromMechanics;
    @FXML
    private TextField nameField;
    @FXML
    private TextField surnameField;
    @FXML
    private TextField loginField;
    @FXML
    private Button createButton;
    @FXML
    private TableView<Mechanic> mechanicList;
    @FXML
    private TableColumn<Mechanic, Long> idColumn;
    @FXML
    private TableColumn<Mechanic, String> nameColumn;
    @FXML
    private TableColumn<Mechanic, String> surnameColumn;
    @FXML
    private TableColumn<Mechanic, String> loginColumn;
    @FXML
    private TableColumn<Mechanic, String> passwordColumn;
    @FXML
    private TableColumn<Mechanic, String> emailColumn;
    @FXML
    private Button deleteButton;
    @FXML
    private Button scheduleButton;
    @FXML
    private Label wrongLabel;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField emailField;
    private Admin admin;

    /**
     * Method get all mechanics which is registered into the database and set them into list.
     *
     * @return list of mechanics
     */
    private ObservableList<Mechanic> mechanicsFromDB() {
        ObservableList<Mechanic> mechanics = observableArrayList();

        try {
            Session session = DatabaseConfigSession.getSessionFactory().openSession();

            session.beginTransaction();

            Admin managedAdmin = session.find(Admin.class, admin.getId());

            List<Mechanic> mechanicsFromDB = managedAdmin.getMechanics();

            mechanics.addAll(mechanicsFromDB);

            session.getTransaction().commit();
            session.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return mechanics;
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

        generateTableView();
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
                        createMechanic();
                        break;
                    case "deleteButton":
                        deleteMechanic();
                        break;
                    case "scheduleButton":
                        scheduleMechanic();
                        break;
                    case "backButtonFromMechanics":
                        Main.changeScene("/manage_page/manage.fxml", admin);
                        break;
                }
            }

        }
    }

    /**
     * Method which is needed to delete the mechanic in the system. Automatically delete in the database if chosen
     * mechanic exists.
     */
    private void deleteMechanic() {
        Mechanic mechanic = mechanicList.getSelectionModel().getSelectedItem();
        System.out.println(mechanic.toString());

        if (mechanic != null) {
            Session session = DatabaseConfigSession.getSessionFactory().openSession();

            try {

                session.beginTransaction();
                Admin managedAdmin = session.find(Admin.class, admin.getId());
                Mechanic managedMechanic = session.find(Mechanic.class, mechanic.getId());

                managedAdmin.removeMechanic(managedMechanic);

                session.remove(managedMechanic);

                session.getTransaction().commit();

            } catch (Exception e) {
                System.out.println(e.getMessage());
                wrongLabel.setText("Something is going wrong, try again");
                if (session != null && session.getTransaction().isActive()) {
                    session.getTransaction().rollback();
                }
            } finally {
                if (session != null) {
                    session.close();
                }
            }

            resetFields();
            mainList.remove(mechanic);
            System.out.println(mainList);
            mechanicList.setItems(mainList);

        } else {
            wrongLabel.setText("No selected mechanic to delete");
        }

    }

    /**
     * Method schedule mechanic to chosen service record.
     *
     * @throws Exception if system could not load the scene
     */
    private void scheduleMechanic() throws Exception {
        Mechanic mechanic = mechanicList.getSelectionModel().getSelectedItem();

        if (mechanic == null) {
            wrongLabel.setText("Select mechanic first");
        } else {
            Main.changeScene("/manage_page/mechanic_page/schedule_page/schedule_mechanic.fxml",  admin,
                    mechanic);
        }

    }

    /**
     * Method which is needed to create the mechanic in the system. Automatically add to the database if everything is
     * set properly.
     */
    private void createMechanic() {
        String name;
        String surname;
        String login;
        String password;
        String email;

        try {
            name = nameField.getText();
            surname = surnameField.getText();
            login = loginField.getText();
            password = passwordField.getText();
            email = emailField.getText();


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
            Mechanic mechanic = new Mechanic(login, password, name, surname, email);

            managedAdmin.addMechanic(mechanic);
            session.persist(mechanic);
            session.getTransaction().commit();

            mainList.add(mechanic);
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

        mechanicList.setItems(mainList);
    }

    /**
     * Method initialize the default state of the scene.
     */
    private void resetFields() {
        nameField.setText("");
        surnameField.setText("");
        loginField.setText("");
        passwordField.setText("");
        emailField.setText("");


    }

    /**
     * Method generates table for the scene to provide for user information about mechanics to the user.
     */
    @Override
    public void generateTableView() {

        mainList = mechanicsFromDB();
        mechanicList.setItems(mainList);

        for (TableColumn<?, ?> column : mechanicList.getColumns()) {
            column.setResizable(false);
        }

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

    }


}
