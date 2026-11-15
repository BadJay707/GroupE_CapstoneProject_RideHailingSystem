public class Driver extends User{
    private String vehicleType;
    private String plateNumber;
    private boolean isAvailable;

    public Driver(String name, String email, String contactNum, String vehicleType,
                  String plateNumber) throws InvalidLocationException{
        super(name, email, contactNum);
        setVehicleType(vehicleType);
        setPlateNumber(plateNumber);
        this.isAvailable = true;
    }

    public void acceptRide(Ride ride){
        if(ride != null && this.isAvailable && ride.getDriver() == null){
            this.isAvailable = false;
            setActiveRide(ride);
            ride.setDriver(this);
        }
    }

    public String getVehicleType(){ return vehicleType; }
    public String getPlateNumber() { return plateNumber; }
    public boolean isAvailable() { return isAvailable; }

    public void setVehicleType(String vehicleType) throws InvalidLocationException{
        if(vehicleType == null || (!vehicleType.equalsIgnoreCase("Car") && !vehicleType.equalsIgnoreCase("Motorcycle"))){
            throw new InvalidLocationException("Vehicle type must be either a Car or a Motorcycle.");
        }
        this.vehicleType = vehicleType;
    }

    public void setPlateNumber(String plateNumber) throws InvalidLocationException{
        if(plateNumber == null || plateNumber.trim().isEmpty()){
            throw new InvalidLocationException("Plate number cannot be empty.");
        }
        this.plateNumber = plateNumber;
    }
    public void setAvailable(boolean available) { isAvailable = available; }
}
