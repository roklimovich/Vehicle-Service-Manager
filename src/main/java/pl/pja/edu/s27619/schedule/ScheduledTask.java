package pl.pja.edu.s27619.schedule;

import jakarta.persistence.*;
import pl.pja.edu.s27619.service.Mechanic;
import pl.pja.edu.s27619.vehicle.repair.ServiceRecord;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "Scheduled_Task")
public class ScheduledTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mechanic_id")
    private Mechanic mechanic;

    @ManyToOne(optional = false)
    @JoinColumn(name = "service_record_id")
    private ServiceRecord serviceRecord;

    @Column(name = "date")
    private LocalDate scheduledDate;

    @Column(name = "time")
    private LocalTime scheduledTime;

    public ScheduledTask() {} // for hibernate purpose

    public ScheduledTask(Mechanic mechanic, ServiceRecord serviceRecord, LocalDate scheduledDate,
                         LocalTime scheduledTime) {
        this.mechanic = mechanic;
        this.serviceRecord = serviceRecord;
        this.scheduledDate = scheduledDate;
        this.scheduledTime = scheduledTime;
    }

    public Mechanic getMechanic() {
        return mechanic;
    }

    public ServiceRecord getServiceRecord() {
        return serviceRecord;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public LocalTime getScheduledTime() {
        return scheduledTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMechanic(Mechanic mechanic) {
        this.mechanic = mechanic;
    }

    public void setServiceRecord(ServiceRecord serviceRecord) {
        this.serviceRecord = serviceRecord;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public void setScheduledTime(LocalTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    @Override
    public String toString() {
        return "Date: " + scheduledDate + " Time: " + scheduledTime + " Description: " + serviceRecord.getDescription();
    }
}
