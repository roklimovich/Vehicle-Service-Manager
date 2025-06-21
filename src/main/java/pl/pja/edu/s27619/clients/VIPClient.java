package pl.pja.edu.s27619.clients;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("VIP")
public class VIPClient extends Client {

    public VIPClient() {} // for hibernate purpose

    public VIPClient(String name, String surname, String phoneNumber, String email) {
        super(name, surname, phoneNumber, email);

    }

    /**
     * Method which return discount for VIP client and has 20% as discount.
     *
     * @return value which contains discount in percentage
     */
    @Override
    public double getDiscount() {
        return 0.2 * 100;
    }
}
