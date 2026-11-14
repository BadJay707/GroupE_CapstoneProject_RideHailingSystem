import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.util.*;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

public class Main extends JFrame{
    private JXMapViewer mapViewer;
    private Location pickupLocation;
    private Location dropoffLocation;

    private JPanel pnlMain;
    private JLabel lblRequestARide;
    private JPanel pnlRequestARide;
    private JLabel lblPickUpLoc;
    private JTextField tfPickUpLoc;
    private JTextField tfDropOffLocation;
    private JLabel lblDropOffLoc;
    private JLabel lblVehicle;
    private JRadioButton rbtnCar;
    private JRadioButton rbtnMotorcycle;
    private JTextField tfEstDistance;
    private JTextField tfEstFare;
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

    public Main(){
        setContentPane(pnlMain);
        setTitle("Ride Hailing System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initMap();
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    // Initializes the map
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
        });

        pnlMap.setLayout(new BorderLayout());
        pnlMap.setPreferredSize(new Dimension(300, 400));
        pnlMap.add(mapViewer, BorderLayout.CENTER);
        pnlMap.revalidate();
        pnlMap.repaint();
    }

    static void main(String[] args) {
        new Main();
    }
}

