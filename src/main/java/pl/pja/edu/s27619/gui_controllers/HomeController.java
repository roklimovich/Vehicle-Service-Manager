package pl.pja.edu.s27619.gui_controllers;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.hibernate.Session;
import pl.pja.edu.s27619.Main;
import pl.pja.edu.s27619.administration.Admin;
import pl.pja.edu.s27619.gui_controllers.dbconfig.DatabaseConfigSession;
import pl.pja.edu.s27619.gui_controllers.interfaces.DataReceiver;
import pl.pja.edu.s27619.schedule.ScheduledTask;
import pl.pja.edu.s27619.vehicle.repair.ServiceRecord;
import pl.pja.edu.s27619.vehicle.repair.ServiceRecordStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;

public class HomeController implements DataReceiver {
    @FXML
    private MenuItem doneItem;
    private ObservableList<ScheduledTask> mainTaskList = observableArrayList();

    private Admin administrator;

    @FXML
    private TableView<ScheduledTask> scheduleTable;

    @FXML
    private TableColumn<ScheduledTask, String> dateColumn;

    @FXML
    private TableColumn<ScheduledTask, String> timeColumn;

    @FXML
    private TableColumn<ScheduledTask, String> taskDescriptionColumn;

    @FXML
    private TableColumn<ScheduledTask, String> taskStatus;

    @FXML
    private Label scheduleLabel;

    @FXML
    private Label dateTime;

    @FXML
    private VBox vBoxContainer;

    @FXML
    private Label userDetails;

    @FXML
    private Button homeButton;

    @FXML
    private Button manageButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button shopButton;

    @FXML
    private Button logOutButton;

    public HomeController() {}


    /**
     * Method initialize the HOMEPAGE.
     */
    @FXML
    public void initialize() {
        String formatted = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        dateTime.setText(formatted);
        scheduleLabel.setText("Schedule for " + formatted);

        setupContextMenu();
    }

    /**
     * Method sets user details which is logged into the system using interface.
     *
     * @param data combined variable which can contains more than 1 Object
     */
    public void setUserDetailsToLabel(Object... data) {
        Object[] obj = Arrays.stream(data).toArray();

        for (Object object : obj) {
            if (object instanceof Admin) {
                administrator = (Admin) object;
                userDetails.setText(administrator.getFullName());
            }
        }

        generateTablesAndColumns();
        getTasksFromDB();
    }

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
                    case "homeButton":
                        Main.changeScene("/home_page/admin_home.fxml", administrator);
                        break;
                    case "manageButton":
                        Main.changeScene("/manage_page/manage.fxml", administrator);
                        break;
                    case "settingsButton":
                        Main.changeScene("/settings_page/settings.fxml", administrator);
                        break;
                    case "shopButton":
                        Main.changeScene("/shop_page/shop.fxml", administrator);
                        break;
                    case "logOutButton":
                        Main.changeScene("/welcome_page/welcome.fxml");
                        break;
                }
            }
        }
    }

    /**
     * Method generates table for the scene to provide for user information about service records for chosen date
     * to the admin.
     */
    public void generateTablesAndColumns() {
        for (TableColumn<?, ?> column : scheduleTable.getColumns()) {
            column.setResizable(false);
        }

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("scheduledDate"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("scheduledTime"));
        taskDescriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()
                .getServiceRecord()
                .getDescription()));
        taskStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()
                .getServiceRecord()
                .getServiceRecordStatus()
                .toString()));

    }

    /**
     * Method get all tasks which is registered into the database and set them into list for current date.
     */
    public void getTasksFromDB() {
        if (!mainTaskList.isEmpty()) {
            mainTaskList.clear();
            scheduleTable.getItems().clear();
        }

        ObservableList<ScheduledTask> scheduledTasks = observableArrayList();

        try {
            Session session = DatabaseConfigSession.getSessionFactory().openSession();

            session.beginTransaction();

            Admin managedAdmin = session.find(Admin.class, administrator.getId());

            List<ScheduledTask> tasks = managedAdmin.getMechanics().stream()
                            .flatMap(mechanic -> mechanic.getScheduledTasks().stream())
                                    .filter(task -> task.getScheduledDate().equals(LocalDate.now()))
                                            .toList();

            scheduledTasks.addAll(tasks);

            session.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        mainTaskList.addAll(scheduledTasks);
        scheduleTable.setItems(mainTaskList);
    }

    /**
     * Method sets the custom context menu to the table for HOMEPAGE to set status DONE, or UNDONE for chosen
     * service record.
     */
    private void setupContextMenu() {
        scheduleTable.setRowFactory(tv -> {
            TableRow<ScheduledTask> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem markDone = new MenuItem("Mark as DONE");
            markDone.setStyle("-fx-font-family: Century Gothic");
            markDone.setStyle("-fx-font-size: 16");
            markDone.setOnAction(event -> updateStatus(row.getItem(), ServiceRecordStatus.DONE));

            MenuItem markUndone = new MenuItem("Mark as UNDONE");
            markUndone.setStyle("-fx-font-family: Century Gothic");
            markUndone.setStyle("-fx-font-size: 16");
            markUndone.setOnAction(event -> updateStatus(row.getItem(), ServiceRecordStatus.UNDONE));

            contextMenu.getItems().addAll(markDone, markUndone);

            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );

            return row;
        });
    }

    /**
     * Method updates the status of chosen service record.
     *
     * @param task contains information of which service record was scheduled
     * @param status contains DONE/UNDONE status, for default it sets as UNDONE
     */
    private void updateStatus(ScheduledTask task, ServiceRecordStatus status) {
        if (task != null) {
            ServiceRecord record = task.getServiceRecord();

            Session session = null;

            try {
                session = DatabaseConfigSession.getSessionFactory().openSession();

                session.beginTransaction();

                Admin managedAdmin = session.find(Admin.class, administrator.getId());

                ServiceRecord managedRecord = administrator.getAllServiceRecords().stream()
                                .filter(serviceRecord -> serviceRecord.getUniqueId().equals(record.getUniqueId()))
                                        .findFirst()
                                                .orElse(null);

                managedRecord.setServiceRecordStatus(status);
                session.merge(managedRecord);

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

            getTasksFromDB();
        }
    }
}
