package pl.pja.edu.s27619.warehouse;

import jakarta.persistence.*;
import pl.pja.edu.s27619.administration.Admin;
import pl.pja.edu.s27619.exceptions.CheckDataException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "part_order")
public class PartOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @Column(name = "part_name", nullable = false)
    private String partName;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "cost", nullable = false)
    private double cost;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Column(name = "admin_email", nullable = false)
    private String adminEmail;

    public PartOrder(){} // for hibernate purpose

    /**
     * Constructor which generated object of class PartOrder and requires information, which should be added for sure,
     * to proceed the correct work of the system.
     *
     * @param admin variable which contains information about supervisor, which order the part
     * @param partName   variable with the name about part, which should be ordered
     * @param quantity   contains the number of parts, which should be ordered
     * @param cost       contains how much the one part which should be ordered costs
     */
    public PartOrder(Admin admin, String partName, int quantity, double cost) {
        setAdmin(admin);
        setPart(partName);
        setQuantity(quantity);
        setCost(cost);
        setOrderTime();
        setAdminEmail(admin.getEmail());
    }

    /**
     * Method sets the admin to the PartOrder. Firstly, checks if supervisor is null or not, if yes - throws
     * exception, otherwise set the supervisor.
     *
     * @param admin contains information about the admin, who order the parts
     */
    public void setAdmin(Admin admin) {
        if (admin == null) {
            throw new CheckDataException("Supervisor could not be null");
        }

        this.admin = admin;
    }

    /**
     * Method sets the part to the PartOrder. Firstly, checks if part is null or not, if yes - throws
     * exception, otherwise set the part.
     *
     * @param partName contains information about the part which should be ordered
     */
    public void setPart(String partName) {
        if (partName == null || partName.isBlank()) {
            throw new CheckDataException("Part could not be null");
        }

        this.partName = partName;
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
        orderDate = LocalDate.now();
    }

    /**
     * Method sets the email of admin, which buy the part in the shop.
     *
     * @param adminEmail variable which contains admin email
     */
    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    /**
     * Method formats the order time to readable pattern "yyyy-MM-dd".
     *
     * @param localDate contains the time when order was created
     * @return String which contains formatted time
     */
    public String getFormattedOrderTime(LocalDate localDate) {

        return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    @Override
    public String toString() {
        return partName + " quantity: " + quantity + ", Costs: " + cost
                + ", Time: " + getFormattedOrderTime(orderDate);
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public Long getId() {
        return Id;
    }

    public Admin getAdmin() {
        return admin;
    }

    public String getPartName() {
        return partName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getCost() {
        return cost;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }
}
