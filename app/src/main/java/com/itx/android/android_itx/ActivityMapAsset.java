package com.itx.android.android_itx;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.itx.android.android_itx.Entity.ImageHolder;
import com.itx.android.android_itx.Service.APIService;
import com.itx.android.android_itx.Utils.ApiUtils;
import com.itx.android.android_itx.Utils.ImageUtils;
import com.itx.android.android_itx.Utils.SessionManager;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityMapAsset extends AppCompatActivity implements OnMapReadyCallback, EasyPermissions.PermissionCallbacks, View.OnClickListener, Validator.ValidationListener {

    private static final int LOCATION_REQUEST = 201;
    private final static int LOCATION_SETTING_RC = 122;
    private APIService mApiSevice;
    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap;
    private Marker mAssetMarker;
    private MarkerOptions mAssetMarkerOptions;
    Validator validator;
    private LatLng assetLocation = new LatLng(-6.1880158, 106.8309161);
    private String[] locationPerm = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private String mAddress = "";


    ProgressDialog progressDialog;

    @BindView(R.id.btn_submit_location)
    Button mBtnSubmitLocation;

    @NotEmpty
    @BindView(R.id.et_add_static_address)
    EditText mEtAssetAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_asset);

        ButterKnife.bind(this);
        validator = new Validator(this);
        validator.setValidationListener(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mApiSevice = ApiUtils.getAPIService(new SessionManager(this).getToken());

        mBtnSubmitLocation.setOnClickListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.asset_map);
        mapFragment.getMapAsync(this);

        // ask user to on the GPS when the activity is created
        if (EasyPermissions.hasPermissions(this, locationPerm)) {
            askUserToTurnOnGPS();
        } else {
            EasyPermissions.requestPermissions(this, "Izinkan aplikasi untuk mengakses lokasi anda", LOCATION_REQUEST, locationPerm);
        }

        // prepare maps
        double lang = getIntent().getDoubleExtra("lang", 0);
        double lat = getIntent().getDoubleExtra("lat", 0);

        assetLocation = new LatLng(lat, lang);

    }


    private void askUserToTurnOnGPS() {
        try {
            GoogleApiClient
                    googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .build();

            googleApiClient.connect();

            LocationRequest request = new LocationRequest();
            request.setInterval(1000000)
                    .setFastestInterval(500000)
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(request);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can initialize location
                            // requests here.
                            if (state.isGpsUsable() && state.isLocationUsable() && state.isNetworkLocationUsable()) {
                                return;
                            }
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(
                                        ActivityMapAsset.this, LOCATION_SETTING_RC);
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        } catch (Exception exception) {
            // Log exception
        }
    }


    private void getCurrentLocation() {

        try {
            Task<Location> result = mFusedLocationClient.getLastLocation();
            result.addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        Log.d("currlocation", "berhasil :" + location.getLongitude());
                        assetLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        getAddressByLocation(location.getLatitude(), location.getLongitude());
                    }
                }
            });
        } catch (SecurityException e) {

            e.printStackTrace();
        }
    }

    private void getAddressByLocation(double lat, double lng) {
        String formattedLangLat = lat + "," + lng;
        Call<JsonObject> geoRequest = mApiSevice.reverseGeocode(formattedLangLat, getString(R.string.geocode_key));
        geoRequest.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonArray results = response.body().getAsJsonArray("results");
                JsonObject firstAddress = results.get(0).getAsJsonObject();
                mAddress = firstAddress.get("formatted_address").getAsString();
                updateMapUI();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void updateMapUI() {
        if (mMap != null && mAssetMarker != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(assetLocation, 16.0f));
            mAssetMarker.setPosition(assetLocation);
            mEtAssetAddress.setText(mAddress);
        }
    }

    @AfterPermissionGranted(12)
    private void getCurrentLocationWithPermission() {

        String[] locPerms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(this, locPerms)) {
            getCurrentLocation();

        } else {
            EasyPermissions.requestPermissions(this, "Izinkan aplikasi untuk mengakses lokasi anda", 12, locPerms);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setIndoorEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(assetLocation, 16.0f));

        if (EasyPermissions.hasPermissions(this, locationPerm)) {
            mMap.setMyLocationEnabled(true);
        } else {
            EasyPermissions.requestPermissions(this, "Izinkan aplikasi untuk mengakses lokasi anda", LOCATION_REQUEST, locationPerm);
        }


        mAssetMarkerOptions = new MarkerOptions();
        mAssetMarkerOptions.position(assetLocation);
        mAssetMarkerOptions.draggable(true);
        mAssetMarkerOptions.title("Lokasi Asset");
        mAssetMarker = mMap.addMarker(mAssetMarkerOptions);
        mAssetMarker.showInfoWindow();


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {


                mMap.getUiSettings().setMapToolbarEnabled(true);
                // return true will prevent any further map action from happening
                return false;

            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                assetLocation = point;
                getAddressByLocation(assetLocation.latitude, assetLocation.longitude);
//                Toast.makeText(ActivityMapAsset.this, assetLocation.toString(), Toast.LENGTH_LONG).show();
            }
        });

        getCurrentLocationWithPermission();
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

        if (requestCode == 12) {
            getCurrentLocation();
        } else if (requestCode == LOCATION_REQUEST) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_SETTING_RC && resultCode == RESULT_OK) {
            LocationSettingsStates settingsStates = LocationSettingsStates.fromIntent(data);
            if (settingsStates.isNetworkLocationUsable() && settingsStates.isGpsUsable() && settingsStates.isLocationUsable()) {
                return;
            }
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_submit_location:
                validator.validate();
                break;
            default:
                break;
        }

    }

    @Override
    public void onValidationSucceeded() {

        Log.d("checklocation", assetLocation.toString());

        Intent i = new Intent();
        i.setAction("sendMapCoordinates");
        i.putExtra("address", mEtAssetAddress.getText().toString());
        i.putExtra("lang", assetLocation.longitude);
        i.putExtra("lat", assetLocation.latitude);
        ActivityMapAsset.this.sendBroadcast(i);

        finish();


    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {

        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }


    }
}
