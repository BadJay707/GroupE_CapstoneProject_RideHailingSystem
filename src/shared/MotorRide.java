package shared;

public class MotorRide extends Ride{
    private static final double BASE_FARE = 50.0; // Edit here to change the values
    private static final double RATE_PER_KM = 14.0;  // Edit here to change the values

    public MotorRide(User passenger, Location pickUpLoc, Location dropOffLoc) throws InvalidLocationException{
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
    public int getTripDuration(){ return Math.max(3000, (int)(getDistanceKm() * 1500)); } // 3 seconds every 1500m

    @Override
    public int getVehicleStepMs(){ return 65; }
}
