package pl.pja.edu.s27619.administration;

import jakarta.persistence.*;
import pl.pja.edu.s27619.administration.interfaces.Accountant;
import pl.pja.edu.s27619.administration.interfaces.Distributor;
import pl.pja.edu.s27619.administration.interfaces.SystemAdmin;
import pl.pja.edu.s27619.clients.Client;
import pl.pja.edu.s27619.exceptions.CheckDataException;
import pl.pja.edu.s27619.exceptions.ServiceRecordException;
import pl.pja.edu.s27619.service.Mechanic;
import pl.pja.edu.s27619.system.SystemSetup;
import pl.pja.edu.s27619.vehicle.component.Engine;
import pl.pja.edu.s27619.vehicle.repair.ServiceRecord;
import pl.pja.edu.s27619.warehouse.PartOrder;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User implements SystemAdmin, Accountant {
    @Transient
    private SystemSetup system = new SystemSetup();

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartOrder> partOrders;

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Client> clients = new LinkedList<>();

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mechanic> mechanics = new LinkedList<>();

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Engine> engines = new LinkedList<>();

    public Admin() {} // for hibernate purpose

    /**
     * Constructor to initialize Admin object.
     *
     * @param name    String variable which contains information about the admin name
     * @param surname String variable which contains information about the admin surname
     * @param email   String variable which contains information about the admin email
     */
    public Admin(String login, String password, String name, String surname, String email) {
        super(login, password, name, surname, email);
        partOrders = new LinkedList<>();

    }

    public void addClient(Client client) {
        clients.add(client);
        client.setAdmin(this);
    }

    public void removeClient(Client client) {
        clients.remove(client);
        client.setAdmin(null);
    }

    public void addMechanic(Mechanic mechanic) {
        mechanics.add(mechanic);
        mechanic.setAdmin(this);
    }

    public void removeMechanic(Mechanic mechanic) {
        mechanics.remove(mechanic);
        mechanic.setAdmin(null);
    }

    public void addEngine(Engine engine) {
        engines.add(engine);
        engine.setAdmin(this);
    }

    public void removeEngine(Engine engine) {
        engines.remove(engine);
        engine.setAdmin(null);
    }

    public void displayInfo() {
        System.out.println("Admin info:" + '\n' + "Name: " + getName() + " | " + "Surname: " + getSurname()
                + " | Email: " + getEmail() + ";");
    }

    /**
     * Method which implements interface Accountant and calculates the budget per month which takes assigned budget
     * for the year and divide it by 12 month, also save only 2 numbers after the dot, using pattern to format.
     *
     * @return double variable which contains information about budget assigned per month
     */
    @Override
    public double calculateBudgetPerMonth() {
        double budgetForMonth = system.getBUDGET_FOR_THE_YEAR() / 12;
        DecimalFormat df = new DecimalFormat("0.00");
        String formatted = df.format(budgetForMonth);
        budgetForMonth = Double.parseDouble(formatted);

        return budgetForMonth;
    }


    /**
     * Method which implements interface SystemAdmin and throws exception, if system working. By default, system works
     * and flag systemDropped is false, after that the flag systemDropped sets to true.
     */
    @Override
    public void breakTheSystem() {
        if (!system.isSystemDropped()) {
            system.setSystemDropped(true);
            throw new RuntimeException("Forced application shutdown");
        }
    }

    /**
     * Method ordering parts and add them to the list of all order parts by current supervisor.
     *
     * @param partName variable which contains necessary information about the ordered part
     * @param quantity int variable contains amount of the part, which should be ordered
     * @param cost     double variable which contains information about the cost for 1 part
     */
    public void orderPart(String partName, int quantity, double cost) {
        PartOrder order = new PartOrder(this, partName, quantity, cost);

        partOrders.add(order);
    }

    public List<ServiceRecord> getAllServiceRecords() {
        return clients.stream()
                .flatMap(client -> client.getClientVehicles().stream())
                .flatMap(vehicle -> vehicle.getServiceRecords().stream())
                .toList();
    }

    public List<PartOrder> getPartOrders() {
        return partOrders;
    }

    public List<Client> getClients() {
        return clients;
    }

    public List<Mechanic> getMechanics() {
        return mechanics;
    }

    public List<Engine> getEngines() {
        return engines;
    }
}
