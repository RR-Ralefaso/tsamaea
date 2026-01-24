class Driver {

    public static class Car {
        private String model;
        private String numberplate;

        public Car() {
            this.model = "";
            this.numberplate = "";
        }

        public Car(String model, String numberplate) {
            this.model = model;
            this.numberplate = numberplate;
        }

        public String getModel() { return this.model; }
        public String getNumberplate() { return this.numberplate; }

        @Override
        public String toString() {
            return String.format("Model of vehicle: %s\nNumber Plate: %s", model, numberplate);
        }
    }

    private final Car car;  
    private String name;
    private String passport;
    private int rating;
    private final int id;

    // Default constructor
    public Driver() {
        this.car = new Car();
        this.name = "";
        this.passport = "";
        this.rating = 0;
        this.id = 0;
    }
    

    // Parameterized constructor
    public Driver(int id, Car car, String name, String passport, int rating) {
        this.id = id; 
        this.car = (car != null) ? car : new Car(); // Null safety check
        this.name = name;
        this.passport = passport;
        this.rating = rating;
    }

    // Setters
    public void setCar(String model, String Numberplate) {
        this.car.model = model;
        this.car.numberplate = Numberplate;
    }
    public void setName(String name) { this.name = name; }
    public void setPassport(String passport) { this.passport = passport; }
    public void setRating(int rating) { this.rating = rating; }

    // Getters
    public Car getCar() { return car; }
    public String getName() { return name; }
    public String getPassport() { return passport; }
    public int getRating() { return rating; }
    public int getId() { return id; }

    @Override
    public String toString() {
        return String.format("Driver name : %s \nVehicle : %s \nID : %s", getName(), getCar(), getId());
    } 
}