package pl.pja.edu.s27619.warehouse;

import pl.pja.edu.s27619.exceptions.CheckDataException;

public class Part {
    private String name;

    /**
     * Constructor which requires that name should be set to the object of this class.
     *
     * @param name String variable which contains information about the name of part, which should be ordered
     */
    public Part(String name) {
        setName(name);
    }

    /**
     * Method sets the name of the part, if it is not null and not empty.
     *
     * @param name the name of the part which should be ordered
     * @throws CheckDataException if name is null or empty
     */
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new CheckDataException("Name of the part could not be null or empty");
        }

        this.name = name;
    }

    public String getName() {
        return name;
    }
}
