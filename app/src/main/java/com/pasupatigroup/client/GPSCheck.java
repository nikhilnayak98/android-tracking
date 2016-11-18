package com.pasupatigroup.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

/**
 * Created by Nikhil on 18-11-2016.
 */

public class GPSCheck extends BroadcastReceiver {

    private AudioManager myAudioManager;
    public Ringtone ringtone;

    @Override
    public void onReceive(Context context, Intent intent) {
        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

        myAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);;
        myAudioManager.setStreamVolume(4, myAudioManager.getStreamMaxVolume(4), 0);
        Uri path = Uri.parse("android.resource://" + context.getPackageName() + "/raw/alert");
        ringtone = RingtoneManager.getRingtone(context, path);
        ringtone.setStreamType(4);

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(context, "Please Switch On The GPS", Toast.LENGTH_LONG).show();
        }
        while (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            ringtone.play();
        }
        ringtone.stop();
    }
}