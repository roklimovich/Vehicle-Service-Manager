package pl.pja.edu.s27619.service;

import pl.pja.edu.s27619.clients.BasicClient;
import pl.pja.edu.s27619.clients.Client;
import pl.pja.edu.s27619.clients.VIPClient;
import pl.pja.edu.s27619.exceptions.CheckDataException;
import pl.pja.edu.s27619.exceptions.ClientNotFoundException;
import pl.pja.edu.s27619.exceptions.VehicleNotFoundException;
import pl.pja.edu.s27619.service.interfaces.Manager;
import pl.pja.edu.s27619.service.interfaces.Validation;
import pl.pja.edu.s27619.vehicle.Vehicle;

import java.util.HashMap;
import java.util.List;

public class ClientManager implements Manager, Validation {
    private static final int POINTS_PROMOTE_TO_VIP = 15;
    private static HashMap<String, List<Vehicle>> registeredClientVehicles = new HashMap<>();
    private static HashMap<String, Client> registeredClients = new HashMap<>();
    private static HashMap<String, Client> registeredVIPClients = new HashMap<>();

    /**
     * Method to set client vehicles to registered ones.
     *
     * @param clientID    String variable which contains ID of the client
     * @param vehicleList list of the vehicle which should be connected to the client
     */
    public static void addClientVehiclesToRegistered(String clientID, List<Vehicle> vehicleList) {
        if (!registeredClientVehicles.containsKey(clientID)) {
            registeredClientVehicles.put(clientID, vehicleList);
        }
    }

    /**
     * Method sets client to HashMap which contains the registered ones clients. Checks if client and id are not null
     * or empty, otherwise throws exception.
     *
     * @param id     String variable which contains information about the client id
     * @param client contains information about the client
     */
    public static void addClientToRegistered(String id, Client client) {
        if (client == null) {
            throw new ClientNotFoundException("Client could no be null");
        } else if (id.isEmpty() || id.isBlank()) {
            throw new CheckDataException("Id could not be null or empty");
        }

        if (!registeredClients.containsKey(id)) {
            registeredClients.put(id, client);
        }
    }

    /**
     * Method add vehicle to the given client, firstly checks if client is not null or vehicle is not null, otherwise
     * throws exception.
     *
     * @param client  contains information about Client
     * @param vehicle contains information about the Vehicle
     */
    public static void addVehicleToClient(Client client, Vehicle vehicle) {
        if (client == null) {
            throw new ClientNotFoundException("Client could not be null");
        } else if (vehicle == null) {
            throw new VehicleNotFoundException("Vehicle could not be null");
        }

        if (registeredClientVehicles.containsKey(client.getId())) {
            registeredClientVehicles.get(client.getId()).add(vehicle);
        }
    }

    /**
     * Method remove vehicle based on given client, firstly checking if the client or vehicle is null, otherwise throws
     * exception.
     *
     * @param client  contains information about the Client
     * @param vehicle contains information about the Vehicle
     */
    public static void removeVehicleBasedOnClient(Client client, Vehicle vehicle) {
        if (client == null) {
            throw new ClientNotFoundException("Client could not be null");
        } else if (vehicle == null) {
            throw new VehicleNotFoundException("Vehicle could not be null");
        }

        if (registeredClientVehicles.containsKey(client.getId())) {
            registeredClientVehicles.get(client.getId()).remove(vehicle);
        }
    }

    /**
     * Method shows vehicle for given client, where system iterate each of the vehicle and print info to the console.
     * First of all checks, if given client is null or not, otherwise throws exception.
     *
     * @param client contains information about the Client
     */
    public static void showVehiclesForGivenClient(Client client) {
        if (client == null) {
            throw new ClientNotFoundException("Client could not be null");
        }

        List<Vehicle> vehicleList = registeredClientVehicles.get(client.getId())
                .stream()
                .toList();

        System.out.println("Vehicles for client with ID: " + client.getId());

        if (vehicleList.isEmpty()) {
            System.out.println("Vehicle list is empty");
        } else {
            for (Vehicle vehicle : vehicleList) {
                vehicle.displayInfo();
            }
        }
    }

    /**
     * Method which dynamically updates the type of the client based on his loyaltyPoints. To be promoted user should
     * have more than POINTS_PROMOTE_TO_VIP, otherwise user will not be able to promote.
     *
     * @param id String variable which contains information about the client ID
     */
    public static void promoteToVIP(String id) {
        String givenId = id.toUpperCase();
        Client client;

        if (registeredClients.containsKey(givenId)) {
            client = registeredClients.get(givenId);
        } else {
            throw new ClientNotFoundException("Client is not found");
        }

        if (client instanceof BasicClient && client.getLoyaltyPoints() >= POINTS_PROMOTE_TO_VIP) {
            VIPClient vipClient = new VIPClient(client.getName(), client.getSurname(), client.getPhoneNumber(),
                    client.getEmail());
            vipClient.setId(client.getId());
            vipClient.setLoyaltyPoints(client.getLoyaltyPoints());
            vipClient.setClientVehicles(client.getClientVehicles());
            client.setDiscount(vipClient.getDiscount());
            client.setLoyaltyPoints(client.getLoyaltyPoints());

            registeredVIPClients.put(vipClient.getId(), vipClient);
            registeredClients.remove(client.getId());
        } else {
            System.out.println("There are no enough points to promote. ");
        }

    }

    /**
     * Method shows all registered clients in the system.
     */
    public static void printDetailsAboutAllClients() {
        System.out.println(registeredClients.values());
    }

    /**
     * Method print service history for the given vehicle and given client. Firstly, it checks if the client and
     * vehicle are null and throws exception, otherwise display the history.
     *
     * @param client  contains information about the Client
     * @param vehicle contains information about the Vehicle
     */
    public static void getListOfServiceRecordsForGivenClientAndVehicle(Client client, Vehicle vehicle) {
        if (client == null) {
            throw new ClientNotFoundException("Client could not be null");
        } else if (vehicle == null) {
            throw new VehicleNotFoundException("Vehicle could not be null");
        }

        if (registeredClients.containsKey(client.getId())) {
            List<Vehicle> vehicles = registeredClientVehicles.get(client.getId());

            if (vehicles.contains(vehicle)) {
                vehicle.displayServiceHistory();
            } else {
                System.out.println("There is no vehicle " + vehicle.getUniqueId() + " for client: " + client.getId());
            }
        }
    }

    /**
     * Method displays service history for all vehicles based on the given client. Firstly, method checks if client is
     * null or not, if yes, throws exception, otherwise continue the logic.
     *
     * @param client variable which contains information about the Client
     */
    public void getListOfAllServiceRecordsByGivenClient(Client client) {
        if (client == null) {
            throw new ClientNotFoundException("Client could not be null");
        }

        List<Vehicle> vehicleList = registeredClientVehicles.get(client.getId());

        for (Vehicle vehicle : vehicleList) {
            vehicle.displayServiceHistory();
        }

    }

    /**
     * Method checks if the client is already in registered VIP clients or not.
     *
     * @param id String variable which contains info about the Client id who should be checked
     * @return true if already in, otherwise false
     */
    public static boolean getInfoAboutVIPClients(String id) {
        String givenId = id.toUpperCase();

        return registeredVIPClients.containsKey(givenId);
    }

    /**
     * Method checks if the client is basic or not.
     *
     * @param client contains information about the Client
     * @return true if basic, otherwise false
     */
    public boolean isBasic(Client client) {
        return !registeredVIPClients.containsKey(client.getId().toUpperCase());
    }
}
