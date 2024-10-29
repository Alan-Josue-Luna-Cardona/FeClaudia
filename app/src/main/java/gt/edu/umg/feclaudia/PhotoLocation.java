package gt.edu.umg.feclaudia;


public class PhotoLocation {
    private byte[] photo;
    private double latitude;
    private double longitude;
    private long timestamp;

    public PhotoLocation(byte[] photo, double latitude, double longitude, long timestamp) {
        this.photo = photo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    // Getters
    public byte[] getPhoto() { return photo; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public long getTimestamp() { return timestamp; }

    // Setters
    public void setPhoto(byte[] photo) { this.photo = photo; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
