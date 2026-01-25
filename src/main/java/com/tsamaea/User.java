import java.time.DateTimeException;
import java.time.LocalDate;

public class User {
    private final String name;
    //private final String passport;
    private final int id;
    private LocalDate dob; // Modern, immutable date object

    // Chains to the main constructor
    public User() {
        this("", 0);
    }

    public User(String name, int id) {
        this.name = (name != null) ? name : "";
        //this.passport = (passport != null) ? passport : "";
        this.id = id;
        this.dob = null;
    }

    /**
     * Sets DOB using 1-based months (1=Jan, 12=Dec)
     * Throws DateTimeException if the date is invalid (e.g., Feb 30)
     */
    public void setDOB(int year, int month, int day) {
        try {
            this.dob = LocalDate.of(year, month, day);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Invalid Date of Birth: " + e.getMessage());
        }
    }

    // Getters
    public String getName() { return name; }
    //public String getPassport() { return passport; }
    public int getId() { return id; }
    public LocalDate getDob() { return dob; }

    @Override
    public String toString() {
        return String.format("Name : %s\n", getName());
    }
    
}