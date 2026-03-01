package pl.pja.edu.s27619.gui_controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
import pl.pja.edu.s27619.gui_controllers.interfaces.TableGenerator;
import pl.pja.edu.s27619.warehouse.PartOrder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static javafx.collections.FXCollections.observableArrayList;

public class ShopController implements DataReceiver, TableGenerator {


    private Admin admin;

    private ObservableList<PartOrder> mainList;

    @FXML
    private TextField findButton;

    @FXML
    private Button backButton;

    @FXML
    private Button buyButton;

    @FXML
    private TableView<PartOrder> orderList;

    @FXML
    private TableColumn<PartOrder, Long> partOrderID;

    @FXML
    private TableColumn<PartOrder, String> partName;

    @FXML
    private TableColumn<PartOrder, Integer> quantity;

    @FXML
    private TableColumn<PartOrder, Double> cost;

    @FXML
    private TableColumn<PartOrder, String> adminId;

    @FXML
    private TableColumn<PartOrder, LocalDateTime> orderDate;

    @FXML
    private TextField partNameField;

    @FXML
    private TextField quantityField;

    @FXML
    private TextField costField;


    /**
     * Method handle left mouse button when it was clicked.
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
                    case "buyButton":
                        buy();
                        //updateData();
                        break;
                    case "backButton":
                        Main.changeScene("/home_page/admin_home.fxml", admin);
                        break;
                }
            }
        }
    }

    /**
     * Method which is needed to buy the product in the shop. Automatically add to the database if everything is
     * set properly.
     */
    private void buy() {
        String partName = partNameField.getText();
        int quantity = Integer.parseInt(quantityField.getText());
        double cost = Double.parseDouble(costField.getText());

        Session session = null;

        try {
            session = DatabaseConfigSession.getSessionFactory().openSession();

            session.beginTransaction();

            PartOrder partOrder = new PartOrder(admin, partName, quantity, cost);

            session.persist(partOrder);

            session.getTransaction().commit();

            mainList.add(partOrder);
            orderList.setItems(mainList);

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

        partNameField.setText("");
        quantityField.setText("");
        costField.setText("");
    }

    /**
     * Method get all orders which is registered into the database and set them into list.
     *
     * @return list of part orders
     */
    private ObservableList<PartOrder> ordersFromDB() {
        ObservableList<PartOrder> orderList = observableArrayList();

        try {
            Session session = DatabaseConfigSession.getSessionFactory().openSession();

            session.beginTransaction();

            List<PartOrder> partOrdersList = session.find(Admin.class, admin.getId()).getPartOrders();

            orderList.addAll(partOrdersList);

            session.getTransaction().commit();
            session.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return orderList;
    }

    /**
     * Method sets user details which is logged into the system using interface.
     *
     * @param receivedData combined variable which can contains more than 1 Object
     */
    @Override
    public void setUserDetailsToLabel(Object... receivedData) {
        Object[] objects = Arrays.stream(receivedData).toArray();

        for (Object obj : objects) {
            admin = (Admin) obj;
        }

        partNameField.setText("");
        quantityField.setText("");
        costField.setText("");

        generateTableView();

        mainList = ordersFromDB();
        orderList.setItems(ordersFromDB());
    }

    /**
     * Method handle enter pressed on the keyboard for find button. It iterates all part orders from the list and find
     * only parts which is should be founded.
     *
     * @param keyEvent contains which key is pressed
     */
    public void enterPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            String partToBeFind = findButton.getText().toLowerCase();

            if (partToBeFind.isEmpty() || partToBeFind.isBlank()) {
                orderList.setItems(mainList);
            } else {
                ObservableList<PartOrder> sortedList = orderList.getItems()
                        .stream()
                        .filter(part -> part.getPartName().toLowerCase().contains(partToBeFind))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));

                orderList.setItems(sortedList);
            }

        }
    }


    /**
     * Method generates table for the scene to provide for user information about part orders to the user.
     */
    @Override
    public void generateTableView() {
        for (TableColumn<?, ?> column : orderList.getColumns()) {
            column.setResizable(false);
        }

        partOrderID.setCellValueFactory(new PropertyValueFactory<>("id"));
        partName.setCellValueFactory(new PropertyValueFactory<>("partName"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        cost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        adminId.setCellValueFactory(new PropertyValueFactory<>("adminEmail"));
        orderDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
    }
}
