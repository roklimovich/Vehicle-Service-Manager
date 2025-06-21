package pl.pja.edu.s27619.clients;

import jakarta.persistence.*;
import pl.pja.edu.s27619.exceptions.CheckDataException;
import pl.pja.edu.s27619.vehicle.Vehicle;

import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "Client")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "client_type")
public abstract class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name", length = 60, nullable = false)
    private String name;

    @Column(name = "surname", length = 80, nullable = false)
    private String surname;

    @Column(name = "phone_number", length = 20, nullable = false)
    private String phoneNumber;

    @Column(name = "email", length = 50, nullable = false)
    private String email;

    @Column(name = "loyalty_points", nullable = false)
    private int loyaltyPoints;

    @Column(name = "discount", nullable = false)
    private double discount;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Vehicle> clientVehicles;

    public Client() {} // for hibernate purpose

    /**
     * Constructor to initialize Client object. Constructor automatically add client to the list of the registered
     * clients in the system and add client vehicles to the system using ClientManager.
     *
     * @param name        String variable which contains information about the client name
     * @param surname     String variable which contains information about the client surname
     * @param phoneNumber String variable which contains information about the client phone number
     * @param email       String variable which contains information about the client email
     */
    public Client(String name, String surname, String phoneNumber, String email) {
        setName(name);
        setSurname(surname);
        setPhoneNumber(phoneNumber);
        setEmail(email);
        loyaltyPoints = 0;
        discount = getDiscount();
        clientVehicles = new LinkedList<>();
    }

    /**
     * Method which return discount based on the type of client. Basic client by default has 0% as discount.
     */
    public abstract double getDiscount();

    /**
     * Method checks if name is null or name is empty and throws exception, otherwise set name.
     *
     * @param name String variable which contains information about the name
     */
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new CheckDataException("Name could not be null or empty");
        }

        this.name = name;
    }

    /**
     * Method checks if the surname is null or empty and throws exception, otherwise set the surname.
     *
     * @param surname contains information about the surname
     */
    public void setSurname(String surname) {
        if (surname == null || surname.isBlank()) {
            throw new CheckDataException("Surname could not be null or empty");
        }

        this.surname = surname;
    }

    /**
     * Method checks if the phone number is null or empty and throws exception, otherwise set the phone number.
     *
     * @param phoneNumber String variable which contains information about the surname
     */
    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new CheckDataException("Phone number could not be null or empty");
        }

        this.phoneNumber = phoneNumber;
    }

    /**
     * Method checks if the email is null or empty and throws exception, otherwise set the email to client.
     *
     * @param email String variable which contains information about the client email
     */
    public void setEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new CheckDataException("Email could not be null or empty");
        }

        this.email = email;
    }

    /**
     * Method to update dynamically loyalty point based on service records on the system.
     *
     * @param loyaltyPoints contains information about loyalty points to the current client
     */
    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }


    /**
     * Method sets client vehicles to the current client.
     *
     * @param clientVehicles is list which contains information about each Vehicle of the client
     */
    public void setClientVehicles(List<Vehicle> clientVehicles) {
        this.clientVehicles = clientVehicles;
    }


    /**
     * Method sets discount to the current client.
     *
     * @param discount double variable which contains discount rate
     */
    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public List<Vehicle> getClientVehicles() {
        return clientVehicles;
    }

    public double getDiscountInPercentage() {
        return discount;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public String getType() {
        if (this instanceof BasicClient) {
            return "BASIC";
        }

        if (this instanceof VIPClient) {
            return "VIP";
        }

        return "UNKNOWN";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", loyaltyPoint=" + loyaltyPoints + '\'' +
                ", discount=" + getDiscount() + "%" + '\'' +
                ", typeOfClient=" + getClass() +
                '}';
    }
}
