package pl.pja.edu.s27619.warehouse;

import pl.pja.edu.s27619.administration.Supervisor;
import pl.pja.edu.s27619.exceptions.CheckDataException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PartOrder {
    private Supervisor supervisor;
    private Part part;
    private int quantity;
    private double cost;
    private LocalDateTime orderTime;

    /**
     * Constructor which generated object of class PartOrder and requires information, which should be added for sure,
     * to proceed the correct work of the system.
     *
     * @param supervisor variable which contains information about supervisor, which order the part
     * @param part       variable with information about part, which should be ordered
     * @param quantity   contains the number of parts, which should be ordered
     * @param cost       contains how much the one part which should be ordered costs
     */
    public PartOrder(Supervisor supervisor, Part part, int quantity, double cost) {
        setSupervisor(supervisor);
        setPart(part);
        setQuantity(quantity);
        setCost(cost);
        setOrderTime();
    }

    /**
     * Method sets the supervisor to the PartOrder. Firstly, checks if supervisor is null or not, if yes - throws
     * exception, otherwise set the supervisor.
     *
     * @param supervisor contains information about the supervisor, who order the parts
     */
    public void setSupervisor(Supervisor supervisor) {
        if (supervisor == null) {
            throw new CheckDataException("Supervisor could not be null");
        }

        this.supervisor = supervisor;
    }

    /**
     * Method sets the part to the PartOrder. Firstly, checks if part is null or not, if yes - throws
     * exception, otherwise set the part.
     *
     * @param part contains information about the part which should be ordered
     */
    public void setPart(Part part) {
        if (part == null) {
            throw new CheckDataException("Part could not be null");
        }

        this.part = part;
    }

    /**
     * Method sets the quantity to the PartOrder. Firstly, checks if part is not negative, if yes - throws
     * exception, otherwise set the quantity.
     *
     * @param quantity int variable which contains amount of the product, which should be ordered
     */
    public void setQuantity(int quantity) {
        if (quantity < 1) {
            throw new CheckDataException("Quantity could be negative or zero");
        }

        this.quantity = quantity;
    }

    /**
     * Method sets the cost to the PartOrder. Firstly, checks if part is not negative, if yes - throws
     * exception, otherwise set the cost.
     *
     * @param cost variable of type double which contains cost of the product for 1 item, which should be ordered
     */
    public void setCost(double cost) {
        if (cost < 0) {
            throw new CheckDataException("Cost could not be null");
        }

        this.cost = cost;
    }

    /**
     * Method sets the order time. By the default it sets the local time, which was, when the order is created.
     */
    public void setOrderTime() {
        orderTime = LocalDateTime.now();
    }

    /**
     * Method formats the order time to readable pattern "yyyy-MM-dd HH:mm:ss".
     *
     * @param localDateTime contains the time when order was created
     * @return String which contains formatted time
     */
    public String getFormattedOrderTime(LocalDateTime localDateTime) {

        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public String toString() {
        return part.getName() + " quantity: " + quantity + ", Costs: " + cost
                + ", Time: " + getFormattedOrderTime(orderTime);
    }
}
