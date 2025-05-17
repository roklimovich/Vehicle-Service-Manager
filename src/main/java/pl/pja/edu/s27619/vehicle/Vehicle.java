package pl.pja.edu.s27619.vehicle;

import pl.pja.edu.s27619.exceptions.CheckDataException;
import pl.pja.edu.s27619.service.VehicleManager;
import pl.pja.edu.s27619.vehicle.component.Engine;
import pl.pja.edu.s27619.vehicle.condition.VehicleCertificate;
import pl.pja.edu.s27619.vehicle.repair.ServiceRecord;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

public class Vehicle implements Serializable {

    private String uniqueId;
    private static int idCounter;
    private VehicleType vehicleType;
    private String name;
    private String model;
    private String color;
    private Engine engine;
    private Map<LocalDate, ServiceRecord> serviceRecords = new HashMap<>();
    private List<VehicleCertificate> vehicleCertificates = new ArrayList<>();

    /**
     * Constructor to initialize Vehicle object without a color.
     *
     * @param vehicleType the type of the vehicle
     * @param name        manufacturer name of the vehicle
     * @param model       model of the vehicle
     * @param engine      engine which used in vehicle
     */
    public Vehicle(VehicleType vehicleType, String name, String model, Engine engine) {
        uniqueId = generateUniqueId(); // derived attribute
        setVehicleType(vehicleType);
        setName(name);
        setModel(model);
        setEngine(engine);
        VehicleManager.getRegisteredVehicles().put(uniqueId, this);
    }

    /**
     * Overload constructor to initialize Vehicle object with a color.
     *
     * @param vehicleType the type of the vehicle
     * @param name        manufacturer name of the vehicle
     * @param model       model of the vehicle
     * @param engine      engine which used in vehicle
     * @param color       color of vehicle
     */
    public Vehicle(VehicleType vehicleType, String name, String model, Engine engine, String color) {
        uniqueId = generateUniqueId();
        setVehicleType(vehicleType);
        setName(name);
        setModel(model);
        setColor(color);
        setEngine(engine);
        VehicleManager.getRegisteredVehicles().put(uniqueId, this);
    }

    /**
     * Generates a unique ID for the vehicle using counter of the Vehicle objects.
     *
     * @return String with unique ID in special format "VEHICLE-<idCounter>"
     */
    public String generateUniqueId() {
        return "VEHICLE-" + (++idCounter);
    }

    /**
     * This method adds service record to the vehicle.
     *
     * @param repairLog contains information about service record for vehicle
     */
    public void addServiceRecord(ServiceRecord repairLog) {
        if (repairLog == null || repairLog.getServiceDate() == null) {
            throw new CheckDataException("Repair log and date could not be null");
        }

        if (serviceRecords.containsKey(repairLog.getServiceDate())) {
            throw new CheckDataException("Repair log already in service records for this date: "
                    + repairLog.getServiceDate());
        }

        serviceRecords.put(repairLog.getServiceDate(), repairLog);
        repairLog.linkVehicle(this);
    }

    /**
     * This method is used be ServiceRecord class to make sure that bidirectional link back to Vehicle during
     * composition.
     *
     * @param serviceRecord the service record which should be linked
     */
    public void linkServiceRecord(ServiceRecord serviceRecord) {
        serviceRecords.put(serviceRecord.getServiceDate(), serviceRecord);
    }

    /**
     * This method remove a service record from vehicle based on the given date, also ensures that the association is
     * unlinked in both directions (Vehicle -> ServiceRecord and ServiceRecord -> Vehicle).
     *
     * @param date date of the service record to remove
     */
    public void removeServiceRecord(LocalDate date) {
        if (serviceRecords.containsKey(date)) {
            ServiceRecord removedRecord = serviceRecords.remove(date);
            if (removedRecord != null) {
                removedRecord.unlinkVehicle();
            }

        }
    }

    /**
     * Find service record by the date from given service records for vehicle.
     *
     * @param date the date of the service
     * @return an Optional containing ServiceRecords if found, otherwise empty
     */
    public Optional<ServiceRecord> getServiceRecordByDate(LocalDate date) {
        return Optional.ofNullable(serviceRecords.get(date));
    }

    /**
     * Method to display history of service records and show them to the user, sort them in ascending order by date.
     */
    public void displayServiceHistory() {
        System.out.println("Service history for " + uniqueId + ":");
        if (serviceRecords.isEmpty()) {
            System.out.println("Service history is empty for this vehicle");
        } else {
            serviceRecords.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(System.out::println);
        }
    }

    /**
     * Iterate basic info about Vehicle and present it as String with special pattern.
     *
     * @return String containing the unique ID, name, model, and engine type of vehicle
     */
    public String getBasicVehicleInfo() {
        return "Unique ID: " + uniqueId + "; Name: " + name + "; Model: " + model +
                "; Engine: " + engine.getEngineType();
    }

    /**
     * Method to display detailed information about Vehicle in console.
     */
    public void displayInfo() {
        System.out.println("Unique ID: " + uniqueId);
        System.out.println("Vehicle type: " + vehicleType);
        System.out.println("Manufacturer name: " + name);
        System.out.println("Model:  " + model);
        System.out.println("Engine: " + engine);
        System.out.println("Color: " + (color != null ? color : "Not specified"));
        displayServiceHistory();
    }

    /**
     * Method to set the vehicle type, if it is not null, otherwise throw exception.
     *
     * @param vehicleType type of the vehicle
     * @throws CheckDataException if vehicleType is null
     */
    public void setVehicleType(VehicleType vehicleType) {
        if (vehicleType == null) {
            throw new CheckDataException("Vehicle type could not be null");
        }

        this.vehicleType = vehicleType;
    }

    /**
     * Method sets the name of the vehicle, if it is not null and not empty.
     *
     * @param name the manufacturer name of the vehicle
     * @throws CheckDataException if name is null or empty
     */
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new CheckDataException("Vehicle name could not be null or empty");
        }

        this.name = name;
    }

    /**
     * Method sets the model of the vehicle, if it is not null and empty.
     *
     * @param model model name of the vehicle
     * @throws CheckDataException if model is null or empty
     */
    public void setModel(String model) {
        if (model == null || model.isBlank()) {
            throw new CheckDataException("Model could not be null or empty");
        }

        this.model = model;
    }

    /**
     * Method sets the color of the vehicle, if it is not null and empty.
     *
     * @param color String variable contains color of the vehicle
     * @throws CheckDataException if color is null or empty
     */
    public void setColor(String color) {
        if (color == null || color.isBlank()) {
            throw new CheckDataException("Color could not be null or empty");
        }

        this.color = color;
    }


    /**
     * Method sets the engine for vehicle, if it is not null.
     *
     * @param engine engine which should be added to vehicle
     * @throws CheckDataException if engine is null
     */
    public void setEngine(Engine engine) {
        if (engine == null) {
            throw new CheckDataException("Engine could not be null");
        }

        this.engine = engine;
    }

    /**
     * Method sets the certificate to the vehicle. Certificate could not be duplicated, and should be Unique.
     *
     * @param certificate variable which contains information about the given ID
     */
    public void addCertificate(VehicleCertificate certificate) {
        if (!vehicleCertificates.contains(certificate)) {
            vehicleCertificates.add(certificate);
            certificate.setVehicle(this);
        }
    }

    /**
     * Method removes the certificate from the vehicle.
     *
     * @param certificate variable which contains certificate which should be deleted
     */
    public void removeCertificate(VehicleCertificate certificate) {
        if (vehicleCertificates.remove(certificate)) {
            certificate.removeVehicle();
        }
    }

    public Engine getEngine() {
        return engine;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public String getName() {
        return name;
    }

    public Optional<String> getColor() {
        return Optional.ofNullable(color);
    }

    public String getModel() {
        return model;
    }

    public Map<LocalDate, ServiceRecord> getServiceRecords() {
        return serviceRecords;
    }
}
