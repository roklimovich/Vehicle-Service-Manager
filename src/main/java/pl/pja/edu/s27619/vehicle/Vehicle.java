package pl.pja.edu.s27619.vehicle;

import jakarta.persistence.*;
import pl.pja.edu.s27619.clients.Client;
import pl.pja.edu.s27619.exceptions.CheckDataException;
import pl.pja.edu.s27619.vehicle.component.Engine;
import pl.pja.edu.s27619.vehicle.repair.ServiceRecord;

import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "Vehicle")
@Inheritance(strategy = InheritanceType.JOINED)
public class Vehicle implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id", nullable = false, updatable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    @Column(name = "name", length = 60, nullable = false)
    private String name;

    @Column(name = "model", length = 60, nullable = false)
    private String model;

    @Column(name = "color", length = 60)
    private String color;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    private Client owner;

    @Transient
    private String ownerEmail;

    @ManyToOne
    @JoinColumn(name = "engine_id")
    private Engine engine;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ServiceRecord> serviceRecords = new LinkedList<>();


    public Vehicle() {} // for hibernate purpose

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
        setVehicleType(vehicleType);
        setName(name);
        setModel(model);
        setColor(color);
        setEngine(engine);
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

        if (serviceRecords.contains(repairLog)) {
            throw new CheckDataException("Repair log already in service records for this date: "
                    + repairLog.getServiceDate());
        }

        serviceRecords.add(repairLog);
        repairLog.linkVehicle(this);
    }

    /**
     * This method is used be ServiceRecord class to make sure that bidirectional link back to Vehicle during
     * composition.
     *
     * @param serviceRecord the service record which should be linked
     */
    public void linkServiceRecord(ServiceRecord serviceRecord) {
        serviceRecords.add(serviceRecord);
    }

    /**
     * This method remove a service record from vehicle based on the given date, also ensures that the association is
     * unlinked in both directions (Vehicle -> ServiceRecord and ServiceRecord -> Vehicle).
     *
     * @param serviceRecordToDelete the service record to remove
     */
    public void removeServiceRecord(ServiceRecord serviceRecordToDelete) {
        ServiceRecord removedRecord = null;
        if (serviceRecords.contains(serviceRecordToDelete)) {
            for (ServiceRecord serviceRecord : serviceRecords) {
                if (serviceRecord == serviceRecordToDelete) {
                    removedRecord = serviceRecord;
                    break;
                }
            }

            if (removedRecord != null) {
                removedRecord.unlinkVehicle();
            }

        }
    }

    /**
     * Iterate basic info about Vehicle and present it as String with special pattern.
     *
     * @return String containing the unique ID, name, model, and engine type of vehicle
     */
    public String getBasicVehicleInfo() {
        return "Unique ID: " + id + "; Name: " + name + "; Model: " + model +
                "; Engine: " + engine.getEngineType();
    }

    /**
     * Method to display detailed information about Vehicle in console.
     */
    public void displayInfo() {
        System.out.println("Unique ID: " + id);
        System.out.println("Vehicle type: " + vehicleType);
        System.out.println("Manufacturer name: " + name);
        System.out.println("Model:  " + model);
        System.out.println("Engine: " + engine);
        System.out.println("Color: " + (color != null ? color : "Not specified"));
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

        this.name = name.toUpperCase();
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

        this.model = model.toUpperCase();
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
     * Method to set the owner for the vehicle.
     *
     * @param owner variable of type Client, which has information about the vehicle owner
     */
    public void setOwner(Client owner) {
        if (owner == null) {
            throw new CheckDataException("Owner must be assigned for vehicle and could not be null");
        }

        this.owner = owner;
    }

    public Engine getEngine() {
        return engine;
    }

    public Long getId() {
        return id;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        if (color == null) {
            return null;
        } else {
            return color;
        }
    }

    public String getModel() {
        return model;
    }

    public List<ServiceRecord> getServiceRecords() {
        return serviceRecords;
    }

    public Client getOwner() {
        return owner;
    }

    public String getOwnerEmail() {
        return owner.getEmail();
    }

    public String getFullName() {
        return getName() + " " + getModel();
    }

    @Override
    public String toString() {
        return "VEHICLE: " + getFullName().toUpperCase() + ", Vehicle type: "
                + getVehicleType() + ", Engine: " + engine.getEngineName().toUpperCase() + " " + engine.getEngineType().toString()
                + " " + engine.getPower();
    }
}
