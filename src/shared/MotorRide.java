package shared;

public class MotorRide extends Ride{
    private static final double BASE_FARE = 25.0; // Edit here to change the values
    private static final double RATE_PER_KM = 10.0;  // Edit here to change the values

    public MotorRide(User passenger, Location pickUpLoc, Location dropOffLoc) throws InvalidLocationException{
        super(passenger, pickUpLoc, dropOffLoc);
    }

    @Override
    public double calculateFare(){
        double total = BASE_FARE + (getDistanceKm() * RATE_PER_KM);
        setFareAmount(total);
        return total;
    }
}
