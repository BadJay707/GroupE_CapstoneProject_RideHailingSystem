package shared;

public class CarRide extends Ride{
    private static final double BASE_FARE = 85.0; // Edit here to change the values
    private static final double RATE_PER_KM = 22.0;  // Edit here to change the values

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

    @Override
    public int getTripDuration(){
        return Math.max(5000, (int)(getDistanceKm() * 2500)); // 5 seconds every 2500m
    }

    @Override
    public int getVehicleStepMs(){ return 120; }
}
