import shared.*;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.geom.Point2D;
import java.io.*;
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
    private List<GeoPosition> routePoints = new ArrayList<>();
    private static final String ROUTES_FILE = "src/data/routes.txt";

    private JPanel pnlMain;
    private JLabel lblRequestARide;
    private JPanel pnlRequestARide;
    private JLabel lblPickUpLoc;
    private JTextField tfPickUpLoc;
    private JTextField tfDropOffLoc;
    private JLabel lblDropOffLoc;
    private JLabel lblVehicle;
    private JToggleButton rbtnCar;
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
    private JButton btnCancelRide;
    private JPanel pnlMotorcycle;
    private JPanel pnlCar;
    private JPanel pnlPaymentMethodBG;
    private JLabel lblBigText;
    private JLabel lblSmallText;
    private JPanel pnlText;
    private JPanel pnlStatus;
    private JLabel lblStatus;
    private JLabel lblLogo;

    public Main() throws InvalidLocationException{
        ensureDefaultLocations();
        setContentPane(pnlMain);
        setTitle("Ride Hailing System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);

        // Hard coding the logo, it won't be used in form
        URL logoUrl = Main.class.getClassLoader().getResource("resources/logo.png");
        lblLogo.setIcon(new ImageIcon(new ImageIcon(logoUrl).getImage().getScaledInstance(100, 50, Image.SCALE_SMOOTH)));

        // Make panels, text fields, and buttons round
        pnlRequestARide.putClientProperty(FlatClientProperties.STYLE_CLASS, "pnlRound");
        pnlRide.putClientProperty(FlatClientProperties.STYLE_CLASS, "pnlRound");
        pnlMap.putClientProperty(FlatClientProperties.STYLE_CLASS, "pnlMap");
        pnlStatus.putClientProperty(FlatClientProperties.STYLE_CLASS, "pnlStatus");
        pnlDistFare.putClientProperty("FlatLaf.style", "arc: 17");
        pnlCar.putClientProperty("FlatLaf.style", "arc: 17");
        pnlMotorcycle.putClientProperty("FlatLaf.style", "arc: 17");
        pnlPaymentMethodBG.putClientProperty("FlatLaf.style", "arc: 17");
        btnBook.putClientProperty("FlatLaf.style", "arc: 17; font: bold 16 'Inter'");
        btnCancelRide.putClientProperty("FlatLaf.style", "arc: 17; font: bold 16 'Inter'");
        tfDropOffLoc.putClientProperty("FlatLaf.style", "arc: 17");
        tfPickUpLoc.putClientProperty("FlatLaf.style", "arc: 17");
        pnlMap.setPreferredSize(new Dimension(600, 510));

        btnCancelRide.setVisible(false);
        btnCancelRide.disable();

        //----------------Start of the Code----------------//
        currentUser = new User("admin", "admin@example.com", "123456789"); // TODO CHANGE THIS LATER
        drivers = new ArrayList<>();
        // test
        drivers.add(new Driver("Speedy Gonzales", "speedingyourgonzales@gmail.com", "123456789", "Motorcycle", "RUN-5555"));
        drivers.add(new Driver("Hiroshi Odokawa", "oddtaxi@gmail.com", "123456789", "Car", "ODD-3939"));

        // Button Functionality
        btnBook.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                handleBookRide();
            }
        });
        btnCancelRide.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                // TODO ADD A POPUP FOR ARE YOU SURE?
                if(currentRide != null){
                    int choice = JOptionPane.showConfirmDialog(
                            Main.this,
                            "Are you sure you want to cancel your currrent ride?",
                            "Cancel Ride",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

                    if(choice == JOptionPane.NO_OPTION){
                        return;
                    }
                }

                tfPickUpLoc.setText("Enter pickup location");
                tfDropOffLoc.setText("Enter dropoff location");
                lblEstDistanceVal.setText("0.00 km");
                lblEstFareVal.setText("Php 0.00");

                rbtnCar.setSelected(true);
                rbtnCash.setSelected(true);
                pnlText.setVisible(true);
                pnlStatus.setVisible(true);
                btnCancelRide.setVisible(false);
                btnCancelRide.disable();

                currentRide = null;
                pickupLocation = null;
                dropoffLocation = null;

                routePoints.clear();

                if(mapViewer != null){
                    pnlMap.remove(mapViewer);
                    mapViewer = null;
                }
                //lblMapPlaceholder.setVisible(true);
                pnlMap.revalidate();
                pnlMap.repaint();

                btnBook.setEnabled(true);
                btnBook.setText("BOOK");
            }
        });

        // Remove text from TextFields when selected
        tfPickUpLoc.addFocusListener(new java.awt.event.FocusAdapter(){
            String text = "Enter pickup location";
            @Override
            public void focusGained(FocusEvent e){
                if(tfPickUpLoc.getText().equals(text)){
                    tfPickUpLoc.setText("");
                }
            }
            @Override
            public void focusLost(FocusEvent e){
                if(tfPickUpLoc.getText().isEmpty()){
                    tfPickUpLoc.setText(text);
                }
            }
        });
        tfDropOffLoc.addFocusListener(new java.awt.event.FocusAdapter(){
            String text = "Enter dropoff location";
            @Override
            public void focusGained(FocusEvent e){
                if(tfDropOffLoc.getText().equals(text)){
                    tfDropOffLoc.setText("");
                }
            }
            @Override
            public void focusLost(FocusEvent e){
                if(tfDropOffLoc.getText().isEmpty()){
                    tfDropOffLoc.setText(text);
                }
            }
        });
    }

    //----------------Functions----------------//

    // Checks and makes sure locations.txt and routes.txt exists
    private void ensureFilesExist(){
        File file1 = new File(LOCATIONS_FILE);
        File file2 = new File(ROUTES_FILE);
        if(file1.getParentFile() != null && file2.getParentFile() != null){
            file1.getParentFile().mkdirs();
            file2.getParentFile().mkdirs();
        }
    }

    // Checks and makes sure locations.txt exists, then adds in locations for offline use
    private void ensureDefaultLocations(){
        File file = new File(LOCATIONS_FILE);
        ensureFilesExist();

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
        btnCancelRide.setVisible(true);
        btnCancelRide.enable();

        System.out.println(currentUser.getName() + " has booked a ride");

        // DO NOT TOUCH, this stops the window from freezing when searching on OpenStreetMap
        SwingWorker<Location[], Exception> worker = new SwingWorker<Location[], Exception>(){
            @Override
            protected Location[] doInBackground() throws Exception{
                Location p = searchLocation(pickup);
                Location d = searchLocation(dropoff);
                routePoints = getRoadRoute(p, d);
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

                    double roadDistanceKm = calculateRouteDistanceKm(routePoints);
                    if(roadDistanceKm > 0){
                        currentRide.setDistanceKm(roadDistanceKm);
                    }

                    lblEstDistanceVal.setText(String.format("%.2f km", currentRide.getDistanceKm()));
                    lblEstFareVal.setText(String.format("Php %.2f", currentRide.getFareAmount()));

                    GeoPosition p1Geo = new GeoPosition(pickupLocation.getLatitude(), pickupLocation.getLongitude());
                    GeoPosition p2Geo = new GeoPosition(dropoffLocation.getLatitude(), dropoffLocation.getLongitude());

                    if(mapViewer == null){
                        initMap();
                        pnlText.setVisible(false);
                        pnlStatus.setVisible(false);
                    }

                    if(routePoints != null && !routePoints.isEmpty()){
                        mapViewer.zoomToBestFit(new HashSet<>(routePoints), 0.7);
                    }
                    mapViewer.repaint();

                    JOptionPane.showMessageDialog(
                            Main.this,
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
                    JOptionPane.showMessageDialog(
                            Main.this,
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
        Timer timer = new Timer(5000, e -> {
            if(currentRide == null || !currentRide.getStatus().equals("Pending")){
                return;
            }

            for(Driver d : drivers){
                if(d.isAvailable() && d.getVehicleType().equalsIgnoreCase(vehicle)){
                    d.acceptRide(currentRide);
                    lblActiveRideStatus.setText(
                            "Active Ride Status: " +
                            currentRide.getStatus() + " — " + d.getName() +
                            " (" + d.getPlateNumber() + ")"
                    );

                    // Pop-up saying that a Driver Accepted
                    JOptionPane.showMessageDialog(
                            this,
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
            if(pickupLocation == null || dropoffLocation == null){
                return;
            }

            Graphics2D g2 = (Graphics2D)g.create();
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
            if(routePoints != null && routePoints.size() > 1){
                java.awt.geom.Path2D roadPath = new java.awt.geom.Path2D.Double();
                for(int i = 0; i < routePoints.size(); i++){
                    Point2D pt = map.convertGeoPositionToPoint(routePoints.get(i));
                    if(i == 0){
                        roadPath.moveTo(pt.getX(), pt.getY());
                    }
                    else{
                        roadPath.lineTo(pt.getX(), pt.getY());
                    }
                }
                g2.draw(roadPath);
            }

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
        pnlMap.setPreferredSize(new Dimension(600, 510));
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
    private void saveLocationToFile(String name, double lat, double lon){
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

    // Make the route follow roads
    private List<GeoPosition> getRoadRoute(Location start, Location end) throws InvalidLocationException{
        String routeKey = String.format(
                "%.5f,%.5f->%.5f,%.5f",
                start.getLatitude(),
                start.getLongitude(),
                end.getLatitude(),
                end.getLongitude()
        );

        // Checks the file
        List<GeoPosition> cached = loadRouteFromFile(routeKey);
        if(!cached.isEmpty()){
            return cached;
        }

        // Uses OSRM
        List<GeoPosition> points = new ArrayList<>();
        try{
            String urlStr = String.format(
                    java.util.Locale.US,
                    "https://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=full&geometries=geojson",
                    start.getLongitude(),
                    start.getLatitude(),
                    end.getLongitude(),
                    end.getLatitude()
            );

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "RideHailingSystem/1.0");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            if(conn.getResponseCode() == 200){
                try(BufferedReader br = new BufferedReader((new InputStreamReader(conn.getInputStream())))){
                    StringBuilder response = new StringBuilder();
                    String line;

                    while((line = br.readLine()) != null){
                        response.append(line);
                    }
                    String json = response.toString();

                    String marker = "\"coordinates\":[[";
                    int startIdx = json.indexOf(marker);
                    if(startIdx != -1){
                        startIdx += marker.length();
                        int endIdx = json.indexOf("]]", startIdx);
                        String coords = json.substring(startIdx, endIdx);

                        for(String pair : coords.split("\\],\\[")){
                            String[] entries = pair.split(",");
                            double lon = Double.parseDouble(entries[0]);
                            double lat = Double.parseDouble(entries[1]);
                            points.add(new GeoPosition(lat, lon));
                        }
                    }
                }
            }
        }
        catch(Exception e){
            System.out.println("Unavailable to access OSRM.");
        }

        if(points.size() > 1){
            saveRouteToFile(routeKey, points);
            return points;
        }

        points.add(new GeoPosition(start.getLatitude(), start.getLongitude()));
        points.add(new GeoPosition(end.getLatitude(), end.getLongitude()));

        return points;
    }

    private List<GeoPosition> loadRouteFromFile(String routeKey){
        List<GeoPosition> points = new ArrayList<>();

        File file = new File(ROUTES_FILE);
        if(file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))){
                String line;
                while((line = br.readLine()) != null){
                    if(line.trim().isEmpty()){
                        continue;
                    }

                    String[] entries = line.split("\\|");
                    if(entries.length == 2 && entries[0].equals(routeKey)){
                       for(String coord : entries[1].split(";")){
                           String[] latLon = coord.split(",");
                           points.add(new GeoPosition(
                                   Double.parseDouble(latLon[0]),
                                   Double.parseDouble(latLon[1])
                           ));
                       }
                       break;
                    }
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        return points;
    }

    private void saveRouteToFile(String routeKey, List<GeoPosition> points){
        File file = new File(ROUTES_FILE);

        StringBuilder sb = new StringBuilder(routeKey).append("|");
        for(int i = 0; i < points.size(); i++){
            GeoPosition gp = points.get(i);
            sb.append(gp.getLatitude()).append(",").append(gp.getLongitude());
            if(i < points.size() - 1){
                sb.append(";");
            }
        }

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))){
            bw.write(sb.toString());
            bw.newLine();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    // Helper functions to calculate the distance using the road distance
    private double calculateRouteDistanceKm(List<GeoPosition> points) throws InvalidLocationException{
        double totalKm = 0.0;

        if(points == null || points.size() < 2){
            return totalKm;
        }

        for(int i = 0; i < points.size() - 1; i++){
            GeoPosition p1 = points.get(i);
            GeoPosition p2 = points.get(i+1);

            Location loc1 = new Location("waypoint", p1.getLongitude(), p1.getLatitude());
            Location loc2 = new Location("waypoint", p2.getLongitude(), p2.getLatitude());

            totalKm += loc1.getDistanceTo(loc2);
        }

        return totalKm;
    }

    public static void main(String[] args) throws InvalidLocationException {
        FlatDarkLaf.registerCustomDefaultsSource("resources");
        FlatDarkLaf.setup();
        new Main();
    }
}

