package pl.pja.edu.s27619.service;

import jakarta.persistence.*;
import pl.pja.edu.s27619.administration.User;
import pl.pja.edu.s27619.exceptions.ServiceRecordException;
import pl.pja.edu.s27619.schedule.ScheduledTask;
import pl.pja.edu.s27619.vehicle.repair.ServiceRecord;
import pl.pja.edu.s27619.vehicle.repair.ServiceRecordStatus;
import java.util.*;

@Entity
@DiscriminatorValue("MECHANIC")
public class Mechanic extends User {

    @OneToMany(mappedBy = "certifiedMechanic", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ServiceRecord> certifiedServiceRecords = new HashSet<>();

    @OneToMany(mappedBy = "mechanic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduledTask> scheduledTasks = new ArrayList<>();

    public Mechanic() {}

    /**
     * Constructor to initialize object of class Mechanic.
     *
     * @param name    String which contains the name of mechanic
     * @param surname String which contains the surname of mechanic
     * @param email   String variable which contains email of the mechanic
     */
    public Mechanic(String login, String password, String name, String surname, String email) {
        super(login, password, name, surname, email);
    }

    /**
     * Method sets service record to the list of certified service records by mechanic, but already don't add service
     * record as performed once. Firstly, it checks if service record is null and throws exception if it is, otherwise
     * service record will not add to certified once.
     *
     * @param serviceRecord contains information about the certified service records
     */
    public void addCertifiedServiceRecord(ServiceRecord serviceRecord) {
        if (serviceRecord != null) {
            certifiedServiceRecords.add(serviceRecord);
        }
    }

//    /**
//     * Method sets service record to the list of performed service records by mechanic and set mark from UNDONE to DONE
//     * for the service record. Firstly, it checks if service record is null and throws exception if it is, otherwise
//     * it adds to the list of performed service records and set the status for service record as DONE.
//     *
//     * @param serviceRecord contains information about the certified service records
//     */
//    public void addPerformedServiceRecord(ServiceRecord serviceRecord) {
//        if (!certifiedServiceRecords.contains(serviceRecord)) {
//            throw new ServiceRecordException("Service record: " + serviceRecord.getUniqueId() + " not in certified " +
//                    "service records");
//        }
//
//        performedServiceRecords.add(serviceRecord);
//        serviceRecord.setServiceRecordStatus(ServiceRecordStatus.DONE);
//    }

    /**
     * Method which add scheduled task for the mechanic.
     *
     * @param task variable which contains proper information for the scheduled task
     */
    public void addScheduledTask(ScheduledTask task) {
        scheduledTasks.add(task);
        task.setMechanic(this);
    }

    public Set<ServiceRecord> getCertifiedServiceRecords() {
        return Set.copyOf(certifiedServiceRecords);
    }

//    public Set<ServiceRecord> getPerformedServiceRecords() {
//        return Set.copyOf(performedServiceRecords);
//    }

    public List<ScheduledTask> getScheduledTasks() {
        return scheduledTasks;
    }

    @Override
    public void displayInfo() {
        System.out.println("Mechanic info:" + '\n' + "Name: " + getName() + " | " + "Surname: " + getSurname()
                + " | Email: " + getEmail() + ";");
    }

    @Override
    public String toString() {
        return "Mechanic{" +
                "mechanicId='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", surname='" + getSurname() + '\'' +
                ", email='" + getEmail() + '\'' +
                '}';
    }
}
