package nottoworry.clickaway.connections;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by sahil on 7/4/17.
 *
 */

public class myGPSProvider implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    static GoogleApiClient client;
    static Double lat = -1.0, lon =-1.0;
    Context context;

    public myGPSProvider(Context ctx){
        this.context = ctx;
        this.init(ctx);
    }

    private void init(Context ctx) {
        if (client == null) {
            client = new GoogleApiClient.Builder(ctx)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        client.connect();
    }

    public boolean refresh(Context context) {
        try {
            init(context);
            return true;
        } catch (Exception e) {
            e.getStackTrace();
            return false;
        }
    }

    public double getLat() {return lat;}

    public double getLon() {
        return lon;
    }

    public boolean isRunning() {
        return false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location l = LocationServices.FusedLocationApi.getLastLocation(client);
        lon = l.getLongitude();
        lat = l.getLatitude();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(context, "Location Suspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(context, "Location Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void finalize() throws Throwable {
        client.disconnect();
        super.finalize();
    }
}
