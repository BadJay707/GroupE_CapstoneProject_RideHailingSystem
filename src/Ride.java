public class Ride {
    private String rideID;
    private Location pickUpLoc;
    private Location dropOffLoc;
    private double fareAmount;
    private User passenger;
    private Driver driver;

    public Ride(String rideID, Location pickUpLoc, Location dropOffLoc, double fareAmount, User passenger, Driver driver){
        this.rideID = rideID;
        this.pickUpLoc = pickUpLoc;
        this.dropOffLoc = dropOffLoc;
        this.fareAmount = fareAmount;
        this.passenger = passenger;
        this.driver = driver;
    }

    public String status(){

    }

    public void calculateFare(){

    }
}
