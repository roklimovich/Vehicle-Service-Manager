package pl.pja.edu.s27619.service;

import pl.pja.edu.s27619.exceptions.CheckDataException;
import pl.pja.edu.s27619.vehicle.*;
import pl.pja.edu.s27619.vehicle.condition.VehicleCertificate;

import java.io.*;
import java.util.*;

public class VehicleManager {
    private static final String FILE_NAME = "./Vehicle.ser";
    private static HashMap<String, Vehicle> registeredVehicles = new HashMap<>();
    private static Set<String> certificateIDs = new HashSet<>();


    public static HashMap<String, Vehicle> getRegisteredVehicles() {
        return registeredVehicles;
    }

    /**
     * Save registered vehicles to file.
     */
    public static void saveRegisteredVehiclesToFile() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(FILE_NAME);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(registeredVehicles);
        } catch (IOException e) {
            System.out.println("No possibility to save registered vehicles to file " + e.getMessage());
        }
    }

    /**
     * Load registered vehicles from file.
     */
    public static void loadRegisteredVehiclesFromFile() {
        try {
            FileInputStream fileInputStream = new FileInputStream(FILE_NAME);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            registeredVehicles = (HashMap<String, Vehicle>) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No possibility to load registered vehicle from file " + e.getMessage());
        }
    }

    /**
     * Method deletes the vehicle from registered vehicles using given unique ID if exists, otherwise trows exception.
     *
     * @param uniqueId given id of vehicle should be deleted
     */
    public static void deleteVehicle(String uniqueId) {
        String givenId = uniqueId.toUpperCase(); // to avoid incorrect register of id entered from the user

        if (!givenId.isBlank() && !registeredVehicles.containsKey(givenId)) {
            throw new CheckDataException("No such id in registered vehicles");
        }

        registeredVehicles.remove(givenId);

    }

    /**
     * Method finds the vehicle from registered vehicles using given unique ID if exists, otherwise trows exception.
     *
     * @param id given id of vehicle should be returned
     */
    public static Vehicle getVehicleById(String id) {
        String givenId = id.toUpperCase(); // to avoid incorrect register of id entered from the user

        if (!givenId.isBlank() && !registeredVehicles.containsKey(givenId)) {
            throw new CheckDataException("No such id in registered vehicles");
        }

        return registeredVehicles.get(givenId);
    }

    /**
     * Method register certificate in the base of all given certificates in the system, avoid duplicates. If just one
     * certificate will be founded with the same ID, which should be added to the system, system will provide for the
     * user message with declining to do this action.
     *
     * @param vehicleCertificate variable which contains certificate which should be registered
     */
    public static void registerCertificate(VehicleCertificate vehicleCertificate) {
        if (!certificateIDs.add(vehicleCertificate.getCertificateId())) {
            System.out.println("Duplicated certificate id: " + vehicleCertificate.getCertificateId());
        }
    }

    public static Set<String> getCertificateIDs() {
        return certificateIDs;
    }
}
