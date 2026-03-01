package pl.pja.edu.s27619.gui_controllers.mechanic;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.hibernate.Session;
import pl.pja.edu.s27619.Main;
import pl.pja.edu.s27619.administration.Admin;
import pl.pja.edu.s27619.gui_controllers.dbconfig.DatabaseConfigSession;
import pl.pja.edu.s27619.gui_controllers.interfaces.DataReceiver;
import pl.pja.edu.s27619.schedule.ScheduledTask;
import pl.pja.edu.s27619.service.Mechanic;
import pl.pja.edu.s27619.vehicle.repair.ServiceRecord;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;

public class MechanicScheduleController implements DataReceiver {
    @FXML
    private Button backButtonFromSchedule;
    @FXML
    private TextField timeField;
    @FXML
    private ListView<ScheduledTask> mechanicTimeTable = new ListView<>();
    @FXML
    private Button scheduleButton;
    @FXML
    private Button unscheduleButton;
    @FXML
    private Label wrongLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private DatePicker selectDate;
    @FXML
    private ComboBox<ServiceRecord> servicePicker;
    @FXML
    private Label selectedDate;
    private Admin admin;
    private Mechanic mechanic;

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
            } else if (obj instanceof Mechanic) {
                mechanic = (Mechanic) obj;
            }
        }

        generateMechanicSchedulePage();
        selectDate.setValue(LocalDate.now());
        selectedDate.setText(LocalDate.now().toString());
        generateMechanicTimeTableForSelectedDate();
        generateServiceRecordsForSelectedDate(LocalDate.now());
    }



    /**
     * Method generates data for the mechanic schedule page.
     */
    private void generateMechanicSchedulePage() {
        emailLabel.setText(mechanic.getEmail());
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
                    case "scheduleButton":
                        scheduleMechanic();
                        break;
                    case "unscheduleButton":
                        unscheduleMechanic();
                        break;
                    case "backButtonFromSchedule":
                        Main.changeScene("/manage_page/mechanic_page/mechanic.fxml", admin);
                        break;
                }
            }

        }
    }

    /**
     * Method unschedule mechanic for chosen service record.
     */
    private void unscheduleMechanic() {
        ScheduledTask selectedTask = mechanicTimeTable.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            wrongLabel.setText("Choose task to delete");
            return;
        }

        try (Session session = DatabaseConfigSession.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Reload entities in current session
            ScheduledTask taskToDelete = session.find(ScheduledTask.class, selectedTask.getId());

            if (taskToDelete.getMechanic() != null) {
                taskToDelete.getMechanic().getScheduledTasks().remove(taskToDelete);
                taskToDelete.setMechanic(null);
            }
            if (taskToDelete.getServiceRecord() != null) {
                taskToDelete.getServiceRecord().setCertifiedMechanic(null);
            }

            session.remove(taskToDelete);

            session.getTransaction().commit();

            session.close();
        } catch (Exception e) {
            e.printStackTrace();
            wrongLabel.setText("Could not delete task");
            return;
        }

        mechanicTimeTable.getItems().remove(selectedTask);

        // Refresh other UI elements
        generateServiceRecordsForSelectedDate(selectDate.getValue());
        restFields();

    }

    /**
     * Method schedule mechanic for chosen service record and system automatically update the database.
     */
    private void scheduleMechanic() {
        LocalDate date = selectDate.getValue();
        String getTime = timeField.getText();
        ServiceRecord serviceRecord = servicePicker.getValue();

        if (date == null || serviceRecord == null) {
            wrongLabel.setText("All fields must be filled");
            return;
        }

        try {
            Session session = DatabaseConfigSession.getSessionFactory().openSession();

            session.beginTransaction();

            mechanic = session.merge(mechanic);
            serviceRecord = session.merge(serviceRecord);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
            LocalTime time = LocalTime.parse(getTime, formatter);
            ScheduledTask task = new ScheduledTask(mechanic, serviceRecord, date, time);

            mechanic.getScheduledTasks().add(task);
            serviceRecord.setCertifiedMechanic(mechanic);

            session.persist(task);

            session.getTransaction().commit();

            session.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            wrongLabel.setText("Error for scheduling task");
            return;
        }

        generateMechanicTimeTableForSelectedDate();
        generateServiceRecordsForSelectedDate(selectDate.getValue());
        restFields();


    }

    /**
     * Method updates the list of chosen date.
     */
    public void actionPerformed() {
        selectedDate.setText(selectDate.getValue().toString());
        generateMechanicTimeTableForSelectedDate();
        generateServiceRecordsForSelectedDate(selectDate.getValue());
        generateMechanicTimeTableForSelectedDate();
    }

    /**
     * Method generates table for the scene to provide for user information about service records to the user.
     */
    private void generateServiceRecordsForSelectedDate(LocalDate localDate) {
        ObservableList<ServiceRecord> mainServiceList = observableArrayList();

        try {
            Session session = DatabaseConfigSession.getSessionFactory().openSession();

            session.beginTransaction();

            Admin managedAdmin = session.find(Admin.class, admin.getId());

            List<ServiceRecord> filtered =
                    managedAdmin.getAllServiceRecords().stream()
                            .filter(sr -> sr.getServiceDate().equals(localDate))
                            .filter(sr -> sr.getCertifiedMechanic() == null)
                            .toList();

            session.close();

            mainServiceList.addAll(filtered);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        servicePicker.setItems(mainServiceList);
    }

    /**
     * Method generate list of service records to chosen date which is registered into the database.
     */
    private void generateMechanicTimeTableForSelectedDate() {
        LocalDate date = selectDate.getValue();

        ObservableList<ScheduledTask> listOfTasks = observableArrayList();

        try {
            Session session = DatabaseConfigSession.getSessionFactory().openSession();

            session.beginTransaction();
            Mechanic managedMechanic = session.find(Mechanic.class, mechanic.getId());

            List<ScheduledTask> listOfRecords = managedMechanic.getScheduledTasks().stream()
                            .filter(task -> task.getScheduledDate().equals(date))
                                    .toList();

            session.close();

            listOfTasks.addAll(listOfRecords);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        mechanicTimeTable.setItems(listOfTasks);

    }


    /**
     * Method initialize the default state of the scene.
     */
    public void restFields() {
        timeField.setText("");
        wrongLabel.setText("");
    }
}
