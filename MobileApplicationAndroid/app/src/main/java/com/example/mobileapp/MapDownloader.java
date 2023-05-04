package com.example.mobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.datatransport.runtime.dagger.internal.MapFactory;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.tileprovider.modules.TileDownloader;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayManager;
import org.osmdroid.views.overlay.Polygon;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MapDownloader extends AppCompatActivity implements MapEventsReceiver, Marker.OnMarkerDragListener {

    private MapView mapView;
    private EditText minZoom;
    private EditText maxZoom;
    private Button downloadButton;
    private GeoPoint startPoint;
    private GPSTracker gpsTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_downloader);
        intialise();


        String osmdroidDirPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File osmdroidDir = new File(osmdroidDirPath);
        if (!osmdroidDir.exists()) {
            System.out.println("osmdroid directory not found: " + osmdroidDirPath);
        }


        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (minZoom.getText().toString().isEmpty() || maxZoom.getText().toString().isEmpty()) {
                    Toast.makeText(MapDownloader.this, "Please enter min and max zoom levels", Toast.LENGTH_SHORT).show();
                    return;
                }
                int minZoomLevel = Integer.parseInt(minZoom.getText().toString());
                int maxZoomLevel = Integer.parseInt(maxZoom.getText().toString());
                if (minZoomLevel > maxZoomLevel) {
                    Toast.makeText(MapDownloader.this, "Min zoom level cannot be greater than max zoom level", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (minZoomLevel < 4 || maxZoomLevel > 20) {
                    Toast.makeText(MapDownloader.this, "Min zoom level should be greater than 4 and max zoom level should be less than 20", Toast.LENGTH_SHORT).show();
                    return;
                }
                BoundingBox boundingBox = mapView.getBoundingBox();
                System.out.println("______________________________________________________");
                System.out.println("______________________________________________________");
                System.out.println("______________________________________________________");
                System.out.println("Bounding box: " + boundingBox);
                download(boundingBox, minZoomLevel, maxZoomLevel);
                System.out.println("______________________________________________________");
                System.out.println("______________________________________________________");
                System.out.println("______________________________________________________");
            }
        });

    }

    private void intialise() {
        mapView = findViewById(R.id.mapView);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        ITileSource tileSource = TileSourceFactory.OpenTopo;



        mapView.setTileSource(tileSource); // Set the tile source to OpenStreetMap
        mapView.setBuiltInZoomControls(true); // Enable built-in zoom controls
        mapView.setMultiTouchControls(true); // Enable multi-touch controls
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this, this);
        mapView.getOverlays().add(0, mapEventsOverlay);
        mapView.setMaxZoomLevel(20.0);
        mapView.setMinZoomLevel(4.0);
        gpsTracker = new GPSTracker(this);
        startPoint = new GeoPoint(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        Marker marker = new Marker(mapView);
        marker.setPosition(startPoint);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), Utils.createCircleBitmap(Color.RED, Color.BLACK, 20));

        marker.setIcon(bitmapDrawable);
        ArrayList<Marker> markers = new ArrayList<>();
        markers.add(marker);

        mapView.getOverlays().addAll(markers);
        mapView.getController().setCenter(startPoint);
        mapView.getController().setZoom(7.5);
        minZoom = findViewById(R.id.minZoom);
        maxZoom = findViewById(R.id.maxZoom);
        downloadButton = findViewById(R.id.downloadMap);
    }

    private void drawRectangle(){



        List<Overlay> overlays = mapView.getOverlays();
        List<Overlay> markerOverlays = new ArrayList<>();
        OverlayManager overlayManager = mapView.getOverlayManager();
        for (Overlay overlay : overlayManager) {
            if (overlay instanceof Polygon) {
                overlayManager.remove(overlay);
                break;
            }
        }



        for (Overlay overlay : overlays) {
            if (overlay instanceof Marker) {
                System.out.println("Marker found");
                System.out.println(overlay);
                markerOverlays.add(overlay);
            }
        }


        List<GeoPoint> geoPoints = new ArrayList<>();
        for (Overlay overlay : markerOverlays) {
            Marker marker = (Marker) overlay;
            if (overlay instanceof Marker) {
                if (!marker.getPosition().equals(startPoint)) {
                    geoPoints.add(marker.getPosition());
                }

            }
        }
        GeoPoint topLeft = geoPoints.get(0);
        GeoPoint bottomRight = geoPoints.get(1);
        GeoPoint topRight = new GeoPoint(geoPoints.get(0).getLatitude(), geoPoints.get(1).getLongitude());
        GeoPoint bottomLeft = new GeoPoint(geoPoints.get(1).getLatitude(), geoPoints.get(0).getLongitude());

        geoPoints.clear();

        geoPoints.add(bottomRight);
        geoPoints.add(topRight);

        geoPoints.add(topLeft);
        geoPoints.add(bottomLeft);

        Polygon polygon = new Polygon();
        polygon.setPoints(geoPoints);

        // Set the fill color and stroke color of the Polygon

        polygon.getFillPaint().setColor(Color.argb(50, 255, 0, 0));
        polygon.setStrokeColor(Color.argb(100, 255, 0, 0));
        mapView.getOverlayManager().add(polygon);
        mapView.invalidate();
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        List<Overlay> overlays = mapView.getOverlays();
        List<Overlay> markerOverlays = new ArrayList<>();
        for (Overlay overlay : overlays) {
            if (overlay instanceof Marker) {
                System.out.println("Marker found");
                System.out.println(overlay);
                markerOverlays.add(overlay);
            }
        }

        if (markerOverlays.size() > 2) {

            return false;
        }

        Marker marker = new Marker(mapView);
        marker.setPosition(p);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setDraggable(true);
        marker.setOnMarkerDragListener(this);

        mapView.getOverlays().add(marker);
        if (markerOverlays.size() == 2) {
            drawRectangle();
            return false;
        }
        mapView.invalidate();
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        return false;
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        drawRectangle();
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        drawRectangle();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }


    public void download(BoundingBox boundingBox, int minZoomLevel,int maxZoomLevel){
        CacheManager cacheManager = new CacheManager(mapView);
        cacheManager.downloadAreaAsync(this, boundingBox, minZoomLevel, maxZoomLevel); // Specify the min and max zoom levels for caching
    }
}