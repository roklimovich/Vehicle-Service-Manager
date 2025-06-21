package pl.pja.edu.s27619.vehicle.component;

import jakarta.persistence.*;
import pl.pja.edu.s27619.exceptions.CheckDataException;
import pl.pja.edu.s27619.vehicle.Vehicle;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "Engine")
public class Engine implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "engine_id")
    private Long id;  // Must be here

    @Column(name = "engine_name")
    private String engineName;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private EngineType engineType; // multi-aspect

    @Enumerated(EnumType.STRING)
    @Column(name = "emission_level")
    private EmissionLevel emissionLevel; // multi-aspect

    @Enumerated(EnumType.STRING)
    @Column(name = "engine_category")
    private EngineCategory engineCategory; //multi-aspect

    @Column(name = "power")
    private int power;

    @OneToMany(mappedBy = "engine")
    private List<Vehicle> vehicleList;

    // for Hibernate purpose
    public Engine() {}

    /**
     * Constructor to initialize Engine object.
     *
     * @param engineType enum which contains engine type
     * @param power      integer variable contains power of the engine
     */
    public Engine(String engineName, EngineType engineType, EmissionLevel emissionLevel,
                  EngineCategory engineCategory, int power) {
        setEngineName(engineName);
        setEngineType(engineType);
        setEmissionLevel(emissionLevel);
        setEngineCategory(engineCategory);
        setPower(power);

    }

    /**
     * Method sets engineType, if it is not null, otherwise throws exception.
     *
     * @param engineType enum which contains engine type
     * @throws CheckDataException if engineType is null
     */
    public void setEngineType(EngineType engineType) {
        if (engineType == null) {
            throw new CheckDataException("Engine could not be null");
        }
        this.engineType = engineType;
    }

    /**
     * Method sets power engine, if it is not negative value, otherwise throws exception.
     *
     * @param power integer variable contains power of the engine
     * @throws CheckDataException if power is negative
     */
    public void setPower(int power) {
        if (power <= 0) {
            throw new CheckDataException("Power could not be less than 0");
        }
        this.power = power;
    }

    /**
     * Method set emission level, checks if it is not null, otherwise throws exception.
     *
     * @param emissionLevel variable which contains enum of the emission level
     */
    public void setEmissionLevel(EmissionLevel emissionLevel) {
        if (emissionLevel == null) {
            throw new CheckDataException("Emission level could not be null");
        }

        this.emissionLevel = emissionLevel;
    }

    /**
     * Method set engine category to the engine, check if it is not null, otherwise throws exception.
     *
     * @param engineCategory variable which contains enum of the engine category
     */
    public void setEngineCategory(EngineCategory engineCategory) {
        if (engineCategory == null) {
            throw new CheckDataException("Engine category could not be null");
        }

        this.engineCategory = engineCategory;
    }

    /**
     * Method set engine name to the engine, check if it is not null, otherwise throws exception.
     *
     * @param engineName variable which contains name of the engine
     */
    public void setEngineName(String engineName) {
        if (engineName == null) {
            throw new CheckDataException("Engine category could not be null");
        }

        this.engineName = engineName;
    }

    public String getEngineName() {
        return engineName;
    }


    public EmissionLevel getEmissionLevel() {
        return emissionLevel;
    }

    public EngineCategory getEngineCategory() {
        return engineCategory;
    }

    public int getPower() {
        return power;
    }

    public EngineType getEngineType() {

        return engineType;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {

        return engineType.toString() + " (" + power + " HP) " + "Category: " + engineCategory.toString()
                + "; Emission level: " + emissionLevel.toString();
    }

}
