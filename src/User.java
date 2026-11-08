public class User {
    private String userID;
    private String name;
    private String email;
    private String contactNum;

    public User(String userID, String name, String email, String contactNum){
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.contactNum = contactNum;
    }

    public Ride bookRide(Location pickup, Location dropoff){

    }

    public void cancelRide(Ride ride){

    }

    public void rateDriver(Driver driver, double rating){

    }

    public String getName(){
        return name;
    }

    public String getContactNum() {
        return contactNum;
    }

    public String getEmail() {
        return email;
    }
}
