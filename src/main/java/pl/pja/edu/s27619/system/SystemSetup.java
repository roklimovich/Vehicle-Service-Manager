package pl.pja.edu.s27619.system;

public class SystemSetup {
    private final double BUDGET_FOR_THE_YEAR = 2000000;
    private boolean systemDropped = false;

    /**
     * Method check if system dropped or not, by default systemDropped flag is set to false.
     *
     * @return boolean variable which contains information is system dropped or not
     */
    public boolean isSystemDropped() {
        return systemDropped;
    }

    /**
     * Method set the system is dropped.
     *
     * @param systemDropped contains information about system dropped or not
     */
    public void setSystemDropped(boolean systemDropped) {
        this.systemDropped = systemDropped;
    }

    public double getBUDGET_FOR_THE_YEAR() {
        return BUDGET_FOR_THE_YEAR;
    }


}
