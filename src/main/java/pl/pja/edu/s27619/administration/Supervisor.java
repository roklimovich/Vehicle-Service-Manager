package pl.pja.edu.s27619.administration;

import pl.pja.edu.s27619.administration.interfaces.Accountant;
import pl.pja.edu.s27619.administration.interfaces.Distributor;
import pl.pja.edu.s27619.administration.interfaces.SystemAdmin;
import pl.pja.edu.s27619.exceptions.CheckDataException;
import pl.pja.edu.s27619.exceptions.ServiceRecordException;
import pl.pja.edu.s27619.service.mechanic.Mechanic;
import pl.pja.edu.s27619.system.SystemInformation;
import pl.pja.edu.s27619.vehicle.repair.ServiceRecord;
import pl.pja.edu.s27619.warehouse.Part;
import pl.pja.edu.s27619.warehouse.PartOrder;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class Supervisor extends Admin implements SystemAdmin, Accountant, Distributor {
    private SystemInformation system = new SystemInformation();
    private List<PartOrder> partOrders;

    public Supervisor(String name, String surname, String email) {
        super(name, surname, email);
        partOrders = new LinkedList<>();
    }

    /**
     * Method shows information about the admin.
     */
    @Override
    public void displayInfo() {
        System.out.println("Supervisor info:" + '\n' + "Name: " + getName() + " | " + "Surname: " + getSurname()
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
     * Method assigns schedule to the mechanic. Before the start of scheduling method checks if the mechanic and service
     * record are null, or not. If yes - method throws appropriate exceptions, otherwise we receive the list of all
     * tasks for given mechanic and assign new task, to his task list.
     *
     * @param localDateTime variable which contains information about time, for which service record should be assigned
     * @param mechanic      variable which contains mechanic for whom service record should be assigned
     * @param serviceRecord variable which contains information about the service record
     */
    @Override
    public void assignScheduleToMechanic(LocalDateTime localDateTime, Mechanic mechanic, ServiceRecord serviceRecord) {
        if (mechanic == null) {
            throw new CheckDataException("Mechanic could not be null");
        } else if (serviceRecord == null) {
            throw new ServiceRecordException("Service record could not be null");
        }

        mechanic.getTaskList().put(localDateTime, serviceRecord);
    }

    /**
     * Method ordering parts and add them to the list of all order parts by current supervisor.
     *
     * @param part     variable which contains necessary information about the ordered part
     * @param quantity int variable contains amount of the part, which should be ordered
     * @param cost     double variable which contains information about the cost for 1 part
     */
    public void orderPart(Part part, int quantity, double cost) {
        PartOrder order = new PartOrder(this, part, quantity, cost);

        partOrders.add(order);
    }

    public List<PartOrder> getPartOrders() {
        return partOrders;
    }
}
