package pl.pja.edu.s27619;

import pl.pja.edu.s27619.administration.Supervisor;
import pl.pja.edu.s27619.clients.BasicClient;
import pl.pja.edu.s27619.clients.Client;
import pl.pja.edu.s27619.service.ClientManager;
import pl.pja.edu.s27619.vehicle.Car;
import pl.pja.edu.s27619.vehicle.VehicleType;
import pl.pja.edu.s27619.vehicle.component.EmissionLevel;
import pl.pja.edu.s27619.vehicle.component.Engine;
import pl.pja.edu.s27619.vehicle.component.EngineCategory;
import pl.pja.edu.s27619.vehicle.component.EngineType;
import pl.pja.edu.s27619.vehicle.repair.ServiceRecord;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        ClientManager clientManager = new ClientManager();
        // example of overlapping
        Supervisor supervisor = new Supervisor("Roman", "Klimovich", "test@pjwstk.edu.pl");
        // example of usage methods which are implemented in interfaces
        System.out.println("Calculated budget per month: " + supervisor.calculateBudgetPerMonth());;
        supervisor.displayInfo();
        // could be uncommented, but by the logic it breaks the application
        //supervisor.breakTheSystem();

        // objects with abstract and disjoint class client
        Client roman = new BasicClient("Roman", "Klimovich", "+48000000",
                "test@gmail.com");
        Client ksenia = new BasicClient("Ksenia", "Klimovich", "+375000000",
                "test1@gmail.com");

        // example of usage multi-aspect based in EmissionLevel, EngineCategory, EngineType
        Engine bmwX6Engine = new Engine(EngineType.DIESEL, EmissionLevel.EURO_6,
                EngineCategory.SPORT, 625);
        Car bmwX6M = new Car(VehicleType.CAR, "BMW", "X6M", bmwX6Engine,5);
        ClientManager.addVehicleToClient(ksenia, bmwX6M);


        Engine bmwM550iEngine = new Engine(EngineType.PETROL, EmissionLevel.EURO_6,
                EngineCategory.SPORT,530);
        Car bmwM550i = new Car(VehicleType.CAR, "BMW", "M550i", bmwM550iEngine, 5);
        ClientManager.addVehicleToClient(roman, bmwM550i);

        Engine reanultDusterEngine = new Engine(EngineType.DIESEL, EmissionLevel.EURO_4,
                EngineCategory.ECONOMY, 190);
        Car renaultDuster = new Car(VehicleType.CAR, "Renault", "Duster", reanultDusterEngine,
                5);
        ClientManager.addVehicleToClient(roman, renaultDuster);

        Engine audiEngine = new Engine(EngineType.DIESEL, EmissionLevel.EURO_6,
                EngineCategory.STANDARD, 450);
        Car audi = new Car(VehicleType.CAR, "Audi", "RS7", audiEngine, 5);
        ClientManager.addVehicleToClient(ksenia, audi);

        ClientManager.showVehiclesForGivenClient(roman);
        ClientManager.showVehiclesForGivenClient(ksenia);

        ClientManager.removeVehicleBasedOnClient(ksenia, audi);

        System.out.println("Get vehicles after removing for client: ");
        ClientManager.showVehiclesForGivenClient(ksenia);

        System.out.println("Checking if client is Basic: " + clientManager.isBasic(roman) + "\n");

        System.out.println("Get loyalty point before adding manually to the user with id: " + roman.getId()
                + "; Loyalty points: " + roman.getLoyaltyPoints());

        roman.setLoyaltyPoints(14);

        System.out.println("Get loyalty point after adding to the user with id: " + roman.getId()
                + "; Loyalty points: " + roman.getLoyaltyPoints() + "\n");

        ServiceRecord renaultServiceRecord = new ServiceRecord(roman,
                LocalDate.of(2025, 5, 1), "Engine repair", 9990,
                renaultDuster);
        ServiceRecord renaultServiceRecord1 = new ServiceRecord(roman,
                LocalDate.of(2025, 3, 2), "Oil replacement", 2000,
                renaultDuster);
        ServiceRecord bmwX6MServiceRecord = new ServiceRecord(ksenia,
                LocalDate.of(2025, 3, 2), "Engine repair", 7777,
                bmwX6M);

        System.out.println("Loyalty points after adding service records, which is adding automatically: "
                + roman.getLoyaltyPoints() + "\n");

        // polymorphic method call getDiscount()
        System.out.println("Get discount for client: " + roman.getId() + " " + roman.getDiscount() + "%.");
        System.out.println("Get discount for client: " + ksenia.getId() + " " + ksenia.getDiscount() + "%." + "\n");

        System.out.println("Service history for given client with ID: " + roman.getId()
                + ", vehicle with ID: " + renaultDuster.getUniqueId() + " " + renaultDuster.getName()
                + " " + renaultDuster.getModel() + "\n");
        ClientManager.getListOfServiceRecordsForGivenClientAndVehicle(roman, renaultDuster);

        // dynamically change the type of the client (dynamic inheritance)
        ClientManager.promoteToVIP(roman.getId());

        System.out.println("Print details about client after promoting to VIP:");
        ClientManager.printDetailsAboutAllClients();

        System.out.println("\n" + "Get discount for basic client: " + ksenia.getId() + " "
                + ksenia.getDiscountInPercentage() + "%.");
        System.out.println("Get discount after promote client to VIP: " + roman.getId() + " "
                + roman.getDiscountInPercentage() + "%." + "\n");


        ServiceRecord bmwM550iServiceRecord = new ServiceRecord(roman,
                LocalDate.of(2025, 5, 3), "Head-up display changed",
                9990, bmwM550i);

        System.out.println("Show all service records for promoted client: " + roman.getId() + " " + roman.getName());
        // example of multi-inheritance
        clientManager.getListOfAllServiceRecordsByGivenClient(roman);
        System.out.println(clientManager.isBasic(roman));

    }
}
