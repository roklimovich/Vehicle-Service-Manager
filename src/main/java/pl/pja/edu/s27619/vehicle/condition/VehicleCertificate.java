package pl.pja.edu.s27619.vehicle.condition;

import pl.pja.edu.s27619.exceptions.CheckDataException;
import pl.pja.edu.s27619.service.VehicleManager;
import pl.pja.edu.s27619.vehicle.Vehicle;

public class VehicleCertificate {
    private String certificateId;
    private Vehicle vehicle;


    public VehicleCertificate(Vehicle vehicle) {
        certificateId = generatesUniqueId();
        setVehicle(vehicle);
        VehicleManager.registerCertificate(this);
    }

    /**
     * Method generates the unique id to the certificate using pattern: "CERT-x", where x - number which generated
     * randomly in range [1, 99999].
     *
     * @return String which contains unique ID for certificate
     */
    public String generatesUniqueId() {
        return "CERT-" + (int) (Math.random() * 99999 + 1);
    }

    /**
     * Method sets the vehicle automatically to the given certificate
     *
     * @param vehicle variable which contains information about the vehicle
     */
    public void setVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            throw new CheckDataException("Vehicle cannot be null for assigning to Certificate");
        }

        if (this.vehicle != vehicle) {
            this.vehicle = vehicle;
            vehicle.addCertificate(this);
        }
    }

    /**
     * Method removes certificate from the vehicle.
     */
    public void removeVehicle() {
        if (this.vehicle != null) {
            Vehicle temp = this.vehicle;
            this.vehicle = null;
            temp.removeCertificate(this);
        }
    }

    public String getCertificateId() {
        return certificateId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

}
