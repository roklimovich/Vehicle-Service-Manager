package pl.pja.edu.s27619.administration.interfaces;

import pl.pja.edu.s27619.service.mechanic.Mechanic;
import pl.pja.edu.s27619.vehicle.repair.ServiceRecord;

import java.time.LocalDateTime;

public interface Distributor {
    void assignScheduleToMechanic(LocalDateTime localDateTime, Mechanic mechanic, ServiceRecord serviceRecord);
}
