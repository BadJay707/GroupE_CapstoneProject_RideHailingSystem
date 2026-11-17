import shared.*;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.HttpURLConnection;
import java.net.URL;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatClientProperties;

public class Main extends JFrame{
    private JXMapViewer mapViewer;
    private User currentUser;
    private Ride currentRide;
    private Location pickupLocation;
    private Location dropoffLocation;
    private List<Driver> drivers;
    private static final String LOCATIONS_FILE = "src/data/locations.txt";

    private JPanel pnlMain;
    private JLabel lblRequestARide;
    private JPanel pnlRequestARide;
    private JLabel lblPickUpLoc;
    private JTextField tfPickUpLoc;
    private JTextField tfDropOffLoc;
    private JLabel lblDropOffLoc;
    private JLabel lblVehicle;
    private JRadioButton rbtnCar;
    private JRadioButton rbtnMotorcycle;
    private JLabel lblEstDistance;
    private JLabel lblEstFare;
    private JButton btnBook;
    private JPanel pnlRide;
    private JLabel lblActiveRideStatus;
    private JPanel pnlDistFare;
    private JLabel lblPayment;
    private JRadioButton rbtnCash;
    private JRadioButton rbtnCard;
    private JRadioButton rbtnEwallet;
    private JPanel pnlMap;
    private JSeparator underline2;
    private JSeparator underline1;
    private JPanel pnlVehicles;
    private JPanel pnlPickUpLocation;
    private JPanel pnlDropOffLocation;
    private JPanel pnlPaymentMethod;
    private JLabel lblEstDistanceVal;
    private JLabel lblEstFareVal;

    public Main() throws InvalidLocationException{
        ensureLocationsFileExists();
        setContentPane(pnlMain);
        setTitle("Ride Hailing System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initMap();
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);

        // Make panels, text fields, and buttons round
        pnlRequestARide.putClientProperty(FlatClientProperties.STYLE_CLASS, "roundPanel");
        pnlRide.putClientProperty(FlatClientProperties.STYLE_CLASS, "roundPanel");
        pnlDistFare.putClientProperty("FlatLaf.style", "arc: 17");
        btnBook.putClientProperty("FlatLaf.style", "arc: 17");
        tfDropOffLoc.putClientProperty("FlatLaf.style", "arc: 17");
        tfPickUpLoc.putClientProperty("FlatLaf.style", "arc: 17");

        //----------------Start of the Code----------------//
        currentUser = new User("admin", "admin@example.com", "123456789"); // TODO CHANGE THIS LATER
        drivers = new ArrayList<>();
        // test
        drivers.add(new Driver("Speedy Gonzales", "speedingyourgonzales@gmail.com", "123456789", "Motorcycle", "RUN-5555"));
        drivers.add(new Driver("Hiroshi Odokawa", "oddtaxi@gmail.com", "123456789", "Car", "ODD-3939"));

        btnBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleBookRide();
            }
        });

        // Remove text from TextFields when selected
        tfPickUpLoc.addFocusListener(new java.awt.event.FocusAdapter(){
            String text = "Enter pickup location";
            @Override
            public void focusGained(FocusEvent e) {
                if(tfPickUpLoc.getText().equals(text)){
                    tfPickUpLoc.setText("");
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if(tfPickUpLoc.getText().isEmpty()){
                    tfPickUpLoc.setText(text);
                }
            }
        });
        tfDropOffLoc.addFocusListener(new java.awt.event.FocusAdapter(){
            String text = "Enter dropoff location";
            @Override
            public void focusGained(FocusEvent e) {
                if(tfDropOffLoc.getText().equals(text)){
                    tfDropOffLoc.setText("");
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if(tfDropOffLoc.getText().isEmpty()){
                    tfDropOffLoc.setText(text);
                }
            }
        });
    }

    // Checks and makes sure Locations.txt exists
    private void ensureLocationsFileExists(){
        File file = new File(LOCATIONS_FILE);
        if(file.getParentFile() != null){
            file.getParentFile().mkdirs();
        }
        if(!file.exists()){
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
                writer.write("cit-u,10.29578,123.88044");
                writer.newLine();
                writer.write("ayala,10.31778,123.90500");
                writer.newLine();
                writer.write("it park,10.3304,123.9074");
                writer.newLine();
                writer.write("sm seaside,10.280389,123.881833");
                writer.newLine();
                writer.write("sm city,10.3111,123.9181");
                writer.newLine();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    // Where the magic happens
    public void handleBookRide(){
        String pickup = tfPickUpLoc.getText().trim();
        String dropoff = tfDropOffLoc.getText().trim();

        if(pickup.isEmpty() || pickup.equals("Enter pickup location") || dropoff.isEmpty() ||
                dropoff.equals("Enter dropoff location")){
            JOptionPane.showMessageDialog(this, "Please enter valid pickup and dropoff locations");
            return;
        }

        boolean isCar = rbtnCar.isSelected();
        btnBook.setEnabled(false);
        btnBook.setText("LOCATING...");

        System.out.println(currentUser.getName() + " has booked a ride");
        // DO NOT TOUCH, this stops the window from freezing when searching on OpenStreetMap
        SwingWorker<Location[], Exception> worker = new SwingWorker<Location[], Exception>() {
            @Override
            protected Location[] doInBackground() throws Exception {
                Location p = searchLocation(pickup);
                Location d = searchLocation(dropoff);
                return new Location[]{p, d};
            }

            @Override
            protected void done(){
                btnBook.setText("BOOK");
                try{
                    Location[] results = get();
                    pickupLocation = results[0];
                    dropoffLocation = results[1];

                    currentRide = currentUser.bookRide(pickupLocation, dropoffLocation, isCar);

                    lblEstDistanceVal.setText(String.format("%.2f km", currentRide.getDistanceKm()));
                    lblEstFareVal.setText(String.format("Php %.2f", currentRide.getFareAmount()));

                    GeoPosition p1Geo = new GeoPosition(pickupLocation.getLatitude(), pickupLocation.getLongitude());
                    GeoPosition p2Geo = new GeoPosition(dropoffLocation.getLatitude(), dropoffLocation.getLongitude());
                    mapViewer.zoomToBestFit(new HashSet<>(Arrays.asList(p1Geo, p2Geo)), 0.7);
                    mapViewer.repaint();

                    JOptionPane.showMessageDialog(Main.this,
                            "Ride Booked!\n" +
                            "Distance: " + String.format("%.2f", currentRide.getDistanceKm()) + "\n" +
                            "Estimated Fare: " + String.format("%.2f km", currentRide.getFareAmount()) + "\n" +
                            "Status: " + currentRide.getStatus(),
                            "Book Ride",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    simulateDriverAccept(isCar ? "Car" : "Motorcycle");
                }
                catch(Exception e){
                    btnBook.setEnabled(true);
                    e.printStackTrace();
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    JOptionPane.showMessageDialog(Main.this,
                            "Booking failed: " + cause.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        worker.execute();
    }

    // Driver accepts after 3 seconds (FOR TESTING PURPOSES)
    private void simulateDriverAccept(String vehicle){
        Timer timer = new Timer(3000, e -> {
            if(currentRide == null || !currentRide.getStatus().equals("Pending")){ return; }

            for(Driver d : drivers){
                if(d.isAvailable() && d.getVehicleType().equalsIgnoreCase(vehicle)){
                    d.acceptRide(currentRide);
                    lblActiveRideStatus.setText("Active Ride Status: " + currentRide.getStatus() +
                                                " — " + d.getName() + " (" + d.getPlateNumber() + ")");

                    // Pop-up saying that a Driver Accepted
                    JOptionPane.showMessageDialog(this,
                            "Driver Accepted!\n" +
                            "Driver: " + d.getName() + "\n" +
                            "Vehicle: " + d.getVehicleType() + " (" + d.getPlateNumber() + ")\n" +
                            "Status: " + currentRide.getStatus(),
                            "Driver Found",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    btnBook.setEnabled(true);
                    return;
                }
            }
            lblActiveRideStatus.setText("Active Ride Status: No available " + vehicle + " driver found.");
            btnBook.setEnabled(true);
        });
        timer.setRepeats(false);
        timer.start();
    }

    // TODO Make the map actually follow roads instead of a straight line
    // Initializes and draws the map
    public void initMap(){
        TileFactoryInfo info = new OSMTileFactoryInfo("OpenStreetMap", "https://tile.openstreetmap.org");
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        tileFactory.setUserAgent("RideHailingSystem/1.0");

        mapViewer = new JXMapViewer();
        mapViewer.setTileFactory(tileFactory);
        mapViewer.setZoom(5);
        mapViewer.setAddressLocation(new GeoPosition(10.3157,123.8854));

        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));

        mapViewer.setOverlayPainter((g, map, w, h) -> {
            if(pickupLocation == null || dropoffLocation == null){ return; }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GeoPosition p1Geo = new GeoPosition(pickupLocation.getLatitude(), pickupLocation.getLongitude());
            GeoPosition p2Geo = new GeoPosition(dropoffLocation.getLatitude(), dropoffLocation.getLongitude());

            Point2D p1 = map.convertGeoPositionToPoint(p1Geo);
            Point2D p2 = map.convertGeoPositionToPoint(p2Geo);

            int x1 = (int) p1.getX();
            int y1 = (int) p1.getY();
            int x2 = (int) p2.getX();
            int y2 = (int) p2.getY();

            // Route
            g2.setColor(new Color(0, 200, 255)); // Blue
            g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(x1, y1, x2, y2);

            // Pickup Point
            g2.setColor(new Color(0, 255, 130)); // Green
            g2.fillOval(x1 - 8, y1 - 8, 16, 16);
            g2.setColor(Color.WHITE);
            g2.drawOval(x1 - 8, y1 - 8, 16, 16);
            drawPinLabel(g2, "Pickup: " + pickupLocation.getAddress(), x1 + 12, y1 + 4);

            // Dropoff Point
            g2.setColor(new Color(255, 50, 0)); // Red
            g2.fillOval(x2 - 8, y2 - 8, 16, 16);
            g2.setColor(Color.WHITE);
            g2.drawOval(x2 - 8, y2 - 8, 16, 16);
            drawPinLabel(g2, "Dropoff: " + dropoffLocation.getAddress(), x2 + 12, y2 + 4);

            g2.dispose();
        });

        pnlMap.setLayout(new BorderLayout());
        pnlMap.setPreferredSize(new Dimension(300, 510));
        pnlMap.add(mapViewer, BorderLayout.CENTER);
        pnlMap.revalidate();
        pnlMap.repaint();
    }

    // Draws the pins for Pickup and Dropoff
    private void drawPinLabel(Graphics2D g2, String text, int x, int y){
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();
        int pad = 4;
        g2.setColor(new Color(30, 34, 40, 210)); // RGBA
        g2.fillRoundRect(x - pad, y - fm.getAscent() - pad + 2, fm.stringWidth(text) + pad * 2, fm.getHeight() + pad, 6, 6);
        g2.setColor(Color.WHITE);
        g2.drawString(text, x, y);
    }

    // Checks locations.txt, if not there searches on OpenStreetMap and adds result to locations.txt
    private Location searchLocation(String query) throws InvalidLocationException{
        String cleanQuery = query.trim();
        String lowerQuery = cleanQuery.toLowerCase();

        File file = new File(LOCATIONS_FILE);
        if(file.exists()) {
            try(BufferedReader br = new BufferedReader(new FileReader(file))){
                String line;
                while((line = br.readLine()) != null){
                    if(line.trim().isEmpty()){
                        continue;
                    }

                    String[] entries = line.split(",");
                    if(entries.length == 3) {
                        String savedName = entries[0].trim().toLowerCase();
                        if(lowerQuery.equals(savedName) || lowerQuery.contains(savedName)){
                            double lat = Double.parseDouble(entries[1].trim());
                            double lon = Double.parseDouble(entries[2].trim());
                            return new Location(cleanQuery, lon, lat);
                        }
                    }
                }
            }
            catch(IOException | NumberFormatException e){
                e.printStackTrace();
            }
        }

        Location fetched = geocodeFromNominatim(cleanQuery);
        saveLocationToFile(cleanQuery, fetched.getLatitude(), fetched.getLongitude());
        return fetched;
    }

    // Adds the location in Locations.txt
    private void saveLocationToFile(String name, double lat, double lon) {
        String safeName = name.replace(",", " ").trim().toLowerCase();

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(LOCATIONS_FILE, true))){
            bw.write(safeName + "," + lat + "," + lon);
            bw.newLine();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    // Searches on OpenStreetMap (needs internet)
    private Location geocodeFromNominatim(String query) throws InvalidLocationException{
        try{
            String searchQuery = (query.toLowerCase().contains("cebu") ? query : query + ", Cebu, Philippines");
            String encodedQuery = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8);
            String urlStr = "https://nominatim.openstreetmap.org/search?q=" + encodedQuery + "&format=json&limit=1";

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "RideHailingSystem/1.0");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if(conn.getResponseCode() == 200){
                try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))){
                    StringBuilder response = new StringBuilder();
                    String line;

                    while((line = br.readLine()) != null) {
                        response.append(line);
                    }

                    String json = response.toString();
                    double lat = extractJsonDouble(json, "\"lat\":\"");
                    double lon = extractJsonDouble(json, "\"lon\":\"");
                    return new Location(query, lon, lat);
                }
            }
        }
        catch(InvalidLocationException e){
            throw e;
        }
        catch(Exception e){
            throw new InvalidLocationException("Could not find coordinates for \"" + query + "\".");
        }
        throw new InvalidLocationException("Location \"" + query + "\" was not found on the map.");
    }

    // Extracts information from the json created by searching from OpenStreetMap
    private double extractJsonDouble(String json, String key) throws InvalidLocationException{
        int startIndex = json.indexOf(key);

        if(startIndex == -1){
            throw new InvalidLocationException("Coordinates not found in map response.");
        }

        startIndex += key.length();
        int endIndex = json.indexOf("\"", startIndex);

        try{
            return Double.parseDouble(json.substring(startIndex, endIndex));
        }
        catch(NumberFormatException e){
            throw new InvalidLocationException("Malformed coordinate data.");
        }
    }

    static void main(String[] args) throws InvalidLocationException {
        FlatDarkLaf.registerCustomDefaultsSource("resources");
        FlatDarkLaf.setup();
        new Main();
    }
}

