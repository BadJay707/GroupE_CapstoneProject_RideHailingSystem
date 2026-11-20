package shared;

public class CarRide extends Ride{
    private static final double BASE_FARE = 45.0; // Edit here to change the values
    private static final double RATE_PER_KM = 15.0;  // Edit here to change the values

    public CarRide(User passenger, Location pickUpLoc, Location dropOffLoc) throws InvalidLocationException{
        super(passenger, pickUpLoc, dropOffLoc);
    }

    //----------------Methods----------------//
    @Override
    public double calculateFare(){
        double total = BASE_FARE + (getDistanceKm() * RATE_PER_KM);
        setFareAmount(total);
        return total;
    }
}
