package shared;

public abstract class Ride {
    private User passenger;
    private Driver driver;
    private Location pickUpLoc;
    private Location dropOffLoc;
    private double distanceKm;
    private double fareAmount;
    private String status;
    private boolean paid = false;

    public Ride(User passenger, Location pickUpLoc, Location dropOffLoc) throws InvalidLocationException{
        if(pickUpLoc == null || dropOffLoc == null) {
            throw new InvalidLocationException("Both locations are required");
        }
        this.driver = null;
        this.passenger = passenger;
        this.pickUpLoc = pickUpLoc;
        this.dropOffLoc = dropOffLoc;
        this.status = "Pending";
        this.distanceKm = pickUpLoc.getDistanceTo(dropOffLoc);
        this.fareAmount = calculateFare();
    }

    //----------------Methods----------------//
    public abstract double calculateFare();
    public abstract int getTripDuration(); // Uses miliseconds
    public abstract int getVehicleStepMs(); // Uses miliseconds

    public void startRide(){
        if("Accepted".equals(status)){
            this.status = "In Progress";
        }
    }
    public void endRide(){
        if("In Progress".equals(status)){
            this.status = "Complete";
        }
    }

    // Getters and Setters
    public Driver getDriver() { return driver; }
    public User getPassenger() { return passenger; }
    public Location getPickUpLoc() { return pickUpLoc; }
    public Location getDropOffLoc() { return dropOffLoc; }
    public double getDistanceKm() { return distanceKm; }
    public double getFareAmount() { return fareAmount; }
    public String getStatus() { return status; }
    public boolean isPaid(){ return paid; }

    public void setDriver(Driver driver) { this.driver = driver; }
    public void setPassenger(User passenger) { this.passenger = passenger; }
    public void setPickUpLoc(Location pickUpLoc) { this.pickUpLoc = pickUpLoc; }
    public void setDropOffLoc(Location dropOffLoc) { this.dropOffLoc = dropOffLoc; }
    public void setDistanceKm(double distanceKm) {
        if(distanceKm > 0){
            this.distanceKm = distanceKm;
            calculateFare();
        }
    }
    public void setFareAmount(double fareAmount) { this.fareAmount = fareAmount; }
    public void setStatus(String status) { this.status = status; }
    public void setPaid(boolean paid) { this.paid = paid; }
}
