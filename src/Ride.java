public abstract class Ride {
    private Location pickUpLoc;
    private Location dropOffLoc;
    private double distanceKm;
    private double fareAmount;
    private String status
    private User passenger;
    private Driver driver;

    public Ride(Location pickUpLoc, Location dropOffLoc, double distanceKm, double fareAmount, String status){
        this.pickUpLoc = pickUpLoc;
        this.dropOffLoc = dropOffLoc;
        this.fareAmount = fareAmount;
        this.passenger = passenger;
        this.driver = driver;
    }

    // Getters and Setters
    public Location getPickUpLoc() { pickUpLoc; }
    public void setPickUpLoc(Location pickUpLoc) { this.pickUpLoc = pickUpLoc; }
    public Location getDropOffLoc() { dropOffLoc; }
    public void setDropOffLoc(Location dropOffLoc) { this.dropOffLoc = dropOffLoc; }
}
