package pl.pja.edu.s27619.service;

import pl.pja.edu.s27619.exceptions.CheckDataException;
import pl.pja.edu.s27619.vehicle.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VehicleManager {
    private static final String FILE_NAME = "./Vehicle.ser";
    private static HashMap<String, Vehicle> registeredVehicles = new HashMap<>();
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

}
