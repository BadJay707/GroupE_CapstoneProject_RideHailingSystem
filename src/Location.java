public class Location {
    private String address;
    private double longitude;
    private double latitude;

    public Location(String address, double longitude, double latitude){
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    // Getters and Setters
    public String getAddress() { return address; }
    public double getLongitude() { return longitude; }
    public double getLatitude() { return latitude; }

    public void setAddress(String address) throws InvalidLocationException{
        if(address == null || address.trim().isEmpty()){
            throw new InvalidLocationException("Location address cannot be empty.");
        }
        this.address = address.trim();
    }

    public void setLongitude(double longitude) throws InvalidLocationException{
        if(longitude < -180.0 || longitude > 180.0 ){
            throw new InvalidLocationException("Invalid longitude value: " + longitude);
        }
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) throws InvalidLocationException{
        if(longitude < -90.0 || longitude > 90.0 ){
            throw new InvalidLocationException("Invalid longitude value: " + longitude);
        }
        this.latitude = latitude;
    }

    // Calculates distance in kilometers using the Haversine formula
    public double getDistanceTo(Location other){
        final double R = 6371.0;
        double dLat = Math.toRadians(other.latitude - this.latitude);
        double dLon = Math.toRadians(other.longitude - this.longitude);

        double a = Math.sin(dLat/2)*Math.sin(dLat/2) + Math.cos(Math.toRadians(this.latitude)) *
                   Math.cos(Math.toRadians(other.latitude)) * Math.sin(dLon/2)*Math.sin(dLon/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}
