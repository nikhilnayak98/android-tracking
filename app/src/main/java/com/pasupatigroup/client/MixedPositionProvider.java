/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Nikhil Nayak <nikhilnayak98@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.pasupatigroup.client;

import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

@SuppressWarnings("MissingPermission")
public class MixedPositionProvider extends PositionProvider implements LocationListener, GpsStatus.Listener {

    private static int FIX_TIMEOUT = 30 * 1000;

    private LocationListener backupListener;
    private long lastFixTime;

    public MixedPositionProvider(Context context, PositionListener listener) {
        super(context, listener);
    }

    public void startUpdates() {
        lastFixTime = System.currentTimeMillis();
        locationManager.addGpsStatusListener(this);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, period, 0, this);
        } catch (IllegalArgumentException e) {
            Log.w(TAG, e);
        }
    }

    public void stopUpdates() {
        locationManager.removeUpdates(this);
        locationManager.removeGpsStatusListener(this);
        stopBackupProvider();
    }

    private void startBackupProvider() {
        Log.i(TAG, "backup provider start");
        if (backupListener == null) {

            backupListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.i(TAG, "backup provider location");
                    updateLocation(location);
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {
                }

                @Override
                public void onProviderEnabled(String s) {
                }

                @Override
                public void onProviderDisabled(String s) {
                }
            };

            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, period, 0, backupListener);
        }
    }

    private void stopBackupProvider() {
        Log.i(TAG, "backup provider stop");
        if (backupListener != null) {
            locationManager.removeUpdates(backupListener);
            backupListener = null;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "provider location");
        stopBackupProvider();
        lastFixTime = System.currentTimeMillis();
        updateLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(TAG, "provider enabled");
        stopBackupProvider();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(TAG, "provider disabled");
        startBackupProvider();
    }

    @Override
    public void onGpsStatusChanged(int event) {
        if (backupListener == null && System.currentTimeMillis() - (lastFixTime + period) > FIX_TIMEOUT) {
            startBackupProvider();
        }
    }

}
