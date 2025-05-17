package pl.pja.edu.s27619;

import pl.pja.edu.s27619.administration.Supervisor;
import pl.pja.edu.s27619.clients.BasicClient;
import pl.pja.edu.s27619.clients.Client;
import pl.pja.edu.s27619.exceptions.CheckDataException;
import pl.pja.edu.s27619.service.mechanic.Freelancer;
import pl.pja.edu.s27619.service.mechanic.Mechanic;
import pl.pja.edu.s27619.service.VehicleManager;
import pl.pja.edu.s27619.service.mechanic.ServiceCenter;
import pl.pja.edu.s27619.vehicle.Car;
import pl.pja.edu.s27619.vehicle.VehicleType;
import pl.pja.edu.s27619.vehicle.component.EmissionLevel;
import pl.pja.edu.s27619.vehicle.component.Engine;
import pl.pja.edu.s27619.vehicle.component.EngineCategory;
import pl.pja.edu.s27619.vehicle.component.EngineType;
import pl.pja.edu.s27619.vehicle.condition.VehicleCertificate;
import pl.pja.edu.s27619.vehicle.repair.ServiceRecord;
import pl.pja.edu.s27619.warehouse.Part;
import pl.pja.edu.s27619.warehouse.PartOrder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Main {
    public static void main(String[] args) {
        Engine engine = new Engine(EngineType.DIESEL, EmissionLevel.EURO_6,
                EngineCategory.SPORT, 760);
        engine.setEmissionLevel(EmissionLevel.EURO_5); // attributes
        engine.setEngineCategory(EngineCategory.ECONOMY); // attributes
        engine.setPower(240); // attributes

        Car bmwM550i = new Car(VehicleType.CAR, "BMW", "M550i", engine, 5);
        Car bmwX6M = new Car(VehicleType.CAR, "BMW", "X6M", engine, 5);
        Car renaultDuster = new Car(VehicleType.CAR, "Renault", "Duster", engine,
                5);

        // unique constraints
        VehicleCertificate vehicleCertificate = new VehicleCertificate(bmwM550i);
        System.out.println(VehicleManager.getCertificateIDs());
        VehicleManager.registerCertificate(vehicleCertificate);


        Client roman = new BasicClient("Roman", "Klimovich", "+48000000",
                "test@gmail.com");
        ServiceRecord bmwX6MServiceRecord = new ServiceRecord(roman,
                LocalDate.of(2024, 5, 6), "Engine repair", 9999,
                bmwX6M);
        ServiceRecord bmwM550iServiceRecord = new ServiceRecord(roman,
                LocalDate.of(2025, 5, 13), "Head-up display changed",
                7777, bmwM550i);
        ServiceRecord renaultDusterServiceRecord = new ServiceRecord(roman,
                LocalDate.of(2023, 4, 14), "Brake Service",
                450.0, renaultDuster);

        System.out.println(bmwX6MServiceRecord);
        System.out.println(bmwM550iServiceRecord);
        System.out.println(renaultDusterServiceRecord);


        Mechanic mechanic = new Mechanic("Piotr", "Slomczynski", "test@pjwstk.edu.pl");
        System.out.println(mechanic);

        // subset constraint
        //mechanic.addPerformedServiceRecord(bmwX6MServiceRecord); //could be uncommented, but it throws exception
        mechanic.addCertifiedServiceRecord(bmwX6MServiceRecord);
        mechanic.addCertifiedServiceRecord(bmwM550iServiceRecord);
        mechanic.addPerformedServiceRecord(bmwX6MServiceRecord);

        System.out.println(mechanic.getCertifiedServiceRecords());
        System.out.println(mechanic.getPerformedServiceRecords());

        Supervisor supervisor = new Supervisor("Ksenia", "Klimovich", "test@gmail.com");

        /* ordered constraint. Supervisor generate schedule for the Mechanic, automatically ordered by the time from
         * the morning to the evening
         */
        supervisor.assignScheduleToMechanic(LocalDateTime.of(LocalDate.now(),
                LocalTime.of(11, 0)), mechanic, bmwX6MServiceRecord);
        supervisor.assignScheduleToMechanic(LocalDateTime.of(LocalDate.now(),
                LocalTime.of(9, 0)), mechanic, bmwM550iServiceRecord);
        supervisor.assignScheduleToMechanic(LocalDateTime.of(LocalDate.now(),
                LocalTime.of(10, 0)), mechanic, renaultDusterServiceRecord);

        System.out.println(mechanic.getTaskList());

        // custom constraint
        bmwX6MServiceRecord.setCost(10000);

        // bag constraint
        Part oilFilter = new Part("Oil filter");
        supervisor.orderPart(oilFilter, 1, 55);
        supervisor.orderPart(oilFilter, 5, 95);
        System.out.println("Part orders for " + supervisor.getName() + " " + supervisor.getEmail() + ": ");
        for (PartOrder order : supervisor.getPartOrders()) {
            System.out.println(order);
        }

        // xor constraint
        ServiceCenter serviceCenter = new ServiceCenter("MAJORKA PARTS");
        mechanic.assignToServiceCenter(serviceCenter);
        mechanic.printAssignment();

        try {
            mechanic.assignAsFreelancer(new Freelancer("Bialystok"));
        } catch (CheckDataException e) {
            System.out.println("XOR violation " + e.getMessage());
        }

        Mechanic mechanic1 = new Mechanic("Jon", "Smith", "jon.smith@gmail.com");
        mechanic1.assignAsFreelancer(new Freelancer("Krakow"));
        mechanic1.printAssignment();

        //mechanic1.assignToServiceCenter(serviceCenter); //could be uncommented, but it throws exception, just to test
    }
}
