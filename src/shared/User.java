package shared;

public class User {
    private String name;
    private String email;
    private String contactNum;
    private Ride activeRide;

    public User(String name, String email, String contactNum){
        this.name = name;
        this.email = email;
        this.contactNum = contactNum;
        this.activeRide = null;
    }

    //----------------Methods----------------//
    public Ride bookRide(Location pickup, Location dropoff, boolean isCar) throws InvalidLocationException{
        return (isCar ? new CarRide(this, pickup, dropoff) : new MotorRide(this, pickup, dropoff)); // Abstract factory???
    }

    public void cancelRide(Ride ride){
        if(ride != null){
            ride.setStatus("Cancelled");
            activeRide = null;
        }
    }

    // Getters and Setters
    public String getName(){ return name; }
    public String getContactNum() { return contactNum; }
    public String getEmail() { return email; }
    public Ride getActiveRide() { return activeRide; }

    public void setEmail(String email) throws InvalidLocationException{
        if(email == null || !email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")){
            throw new InvalidLocationException("Invalid email format");
        }
        this.email = email;
    }
    public void setContactNum(String contactNum) throws InvalidLocationException{
        if(contactNum == null || !email.matches("^09\\\\d{9}$")){
            throw new InvalidLocationException("Contact number must be 11 digits starting with 09");
        }
        this.contactNum = contactNum;
    }
    public void setActiveRide(Ride activeRide) { this.activeRide = activeRide; }
}
