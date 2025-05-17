package pl.pja.edu.s27619.service.mechanic;

import pl.pja.edu.s27619.exceptions.CheckDataException;
import pl.pja.edu.s27619.exceptions.ServiceRecordException;
import pl.pja.edu.s27619.vehicle.repair.ServiceRecord;
import pl.pja.edu.s27619.vehicle.repair.ServiceRecordStatus;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

public class Mechanic {
    private String mechanicId;
    private String name;
    private String surname;
    private String email;
    private Freelancer freelancer;
    private ServiceCenter serviceCenter;
    private Set<ServiceRecord> certifiedServiceRecords = new HashSet<>();
    private Set<ServiceRecord> performedServiceRecords = new HashSet<>();
    private TreeMap<LocalDateTime, ServiceRecord> taskList = new TreeMap<>();

    /**
     * Constructor to initialize object of class Mechanic.
     *
     * @param name    String which contains the name of mechanic
     * @param surname String which contains the surname of mechanic
     * @param email   String variable which contains email of the mechanic
     */
    public Mechanic(String name, String surname, String email) {
        mechanicId = generateUniqueId();
        setName(name);
        setSurname(surname);
        setEmail(email);
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

    /**
     * Method sets service record to the list of performed service records by mechanic and set mark from UNDONE to DONE
     * for the service record. Firstly, it checks if service record is null and throws exception if it is, otherwise
     * it adds to the list of performed service records and set the status for service record as DONE.
     *
     * @param serviceRecord contains information about the certified service records
     */
    public void addPerformedServiceRecord(ServiceRecord serviceRecord) {
        if (!certifiedServiceRecords.contains(serviceRecord)) {
            throw new ServiceRecordException("Service record: " + serviceRecord.getUniqueId() + " not in certified " +
                    "service records");
        }

        performedServiceRecords.add(serviceRecord);
        serviceRecord.setServiceRecordStatus(ServiceRecordStatus.DONE);
    }

    /**
     * Method sets the mechanic to service center. Before the setting, method checks if mechanic is already assigned to
     * freelancer and throws exception, otherwise sets service center for mechanic. This method implement
     * XOR constraint (only one type of Mechanic could be assigned).
     *
     * @param serviceCenter contains the name of service center, for which mechanic should be assigned
     */
    public void assignToServiceCenter(ServiceCenter serviceCenter) {
        if (this.freelancer != null) {
            throw new CheckDataException("Mechanic " + email + " is already assigned as freelancer. " +
                    "Cannot join Service Center");
        }

        this.serviceCenter = serviceCenter;
    }

    /**
     * Method sets the mechanic as freelancer. Before the setting, method checks if mechanic is already assigned to
     * service center and throws exception, otherwise sets freelancer for the mechanic. This method implement
     * XOR constraint (only one type of Mechanic could be assigned).
     *
     * @param freelancer contains the region of freelance, for which mechanic should be assigned
     */
    public void assignAsFreelancer(Freelancer freelancer) {
        if (this.serviceCenter != null) {
            throw new CheckDataException("Mechanic " + name + " " + surname + " is already assigned in service center. " +
                    "Cannot join as Freelancer");
        }

        this.freelancer = freelancer;
    }

    /**
     * Method print mechanics assignment.
     */
    public void printAssignment() {
        if (serviceCenter != null) {
            System.out.println(name + " " + surname + " is working at " + serviceCenter.getName());
        } else if (freelancer != null) {
            System.out.println(name + " " + surname + " is working at " + freelancer.getRegion());
        } else {
            System.out.println(name + " " + surname + " no assignment");
        }
    }

    /**
     * Method generates the unique id to the Mechanic using pattern: "MECHANIC-x", where x - number which generated
     * randomly in range [1, 99999].
     *
     * @return String which contains unique ID for Mechanic
     */
    private String generateUniqueId() {
        return "MECHANIC-" + (int) (Math.random() * 99999 + 1);
    }

    /**
     * Method checks if name is null or name is empty and throws exception, otherwise set name.
     *
     * @param name contains information about the name
     */
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new CheckDataException("Name could not be null or empty");
        }

        this.name = name;
    }

    /**
     * Method checks if the surname is null or empty and throws exception, otherwise set the surname.
     *
     * @param surname contains information about the surname
     */
    public void setSurname(String surname) {
        if (surname == null || surname.isBlank()) {
            throw new CheckDataException("Surname could not be null or empty");
        }

        this.surname = surname;
    }

    /**
     * Method check the email and if it null or empty throws exception, otherwise set the email.
     *
     * @param email variable which contains email of Admin user
     */
    public void setEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new CheckDataException("Email could not be null or empty");
        }

        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public Set<ServiceRecord> getCertifiedServiceRecords() {
        return Set.copyOf(certifiedServiceRecords);
    }

    public Set<ServiceRecord> getPerformedServiceRecords() {
        return Set.copyOf(performedServiceRecords);
    }

    public TreeMap<LocalDateTime, ServiceRecord> getTaskList() {
        return taskList;
    }

    @Override
    public String toString() {
        return "Mechanic{" +
                "mechanicId='" + mechanicId + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

}
