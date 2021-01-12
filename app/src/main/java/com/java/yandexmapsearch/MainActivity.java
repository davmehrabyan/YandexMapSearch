package com.java.yandexmapsearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import com.java.yandexmapsearch.databinding.ActivityMainBinding;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Geometry;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateReason;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.search.Response;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchManagerType;
import com.yandex.mapkit.search.SearchOptions;
import com.yandex.mapkit.search.Session;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

public class MainActivity extends AppCompatActivity implements CameraListener, InputListener, Session.SearchListener {

    private ActivityMainBinding binding;
    private SearchManager searchManager;
    private Session searchSession;
    private SearchOptions searchOptions;
    private MapObjectCollection mapObjectCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapKitFactory.setApiKey("your api key");
        MapKitFactory.initialize(this);
        searchOptions = new SearchOptions();
        searchOptions.setGeometry(true);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.mapview.getMap().setNightModeEnabled((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES);

        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);
        binding.mapview.getMap().addCameraListener(this);
        binding.mapview.getMap().addInputListener(this);
        mapObjectCollection = binding.mapview.getMap().getMapObjects();

        binding.mapview.getMap().move(
                new CameraPosition(new Point(55.751574, 37.573856), 11.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        binding.mapview.onStop();
        MapKitFactory.getInstance().onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.mapview.onStart();
        MapKitFactory.getInstance().onStart();
    }

    public void submitQueryByName(String query) {
        searchSession = searchManager.submit(
                query,
                Geometry.fromPoint(new Point(40.177200, 44.503490)),
                searchOptions,
                this);
    }

    public void submitQueryByPoint(Point point) {
        searchSession = searchManager.submit(
                point,
                11,
                searchOptions,
                this);
    }

    @Override
    public void onCameraPositionChanged(@NonNull Map map, @NonNull CameraPosition cameraPosition, @NonNull CameraUpdateReason cameraUpdateReason, boolean finished) {
//        Log.e("onCameraPositionChanged"," cameraPosition: "+cameraPosition+" cameraUpdateReason: "+cameraUpdateReason+" finished: "+finished);
    }

    @Override
    public void onMapTap(@NonNull Map map, @NonNull Point point) {
        MapObjectCollection mapObjects = binding.mapview.getMap().getMapObjects();
        mapObjects.clear();

        PlacemarkMapObject placemarkMapObject = mapObjectCollection.addPlacemark(new Point(point.getLatitude(), point.getLongitude()),
                ImageProvider.fromResource(this, R.mipmap.marker_flag));
//        submitQueryByPoint(point);
        submitQueryByName("Smolenskiy bulvar");
        Log.e("onMapTap", "point lat - lang: " + point.getLatitude() + " : " + point.getLongitude());
    }

    @Override
    public void onMapLongTap(@NonNull Map map, @NonNull Point point) {
//        Log.e("onMapLongTap","onMapLongTap");
    }

    @Override
    public void onSearchResponse(@NonNull Response response) {
        try {
            Log.e("Search", "Response: " + response);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSearchError(@NonNull Error error) {
        String errorMessage = "unknown_error_message";
        if (error instanceof RemoteError) {
            errorMessage = "remote_error_message";
        } else if (error instanceof NetworkError) {
            errorMessage = "network_error_message";
        }
        Log.e("Response error", " error: " + errorMessage);
    }
}