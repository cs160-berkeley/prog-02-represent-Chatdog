package com.example.chatdog.prog2;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

public class ShakeListener extends Service {
    public static float currAccel;
    public static float prevAccel;
    public static int shakeCount;
    public static SensorManager mSensorManager;

    @Override
    public void onCreate() {
        //code adapted from http://stackoverflow.com/questions/2317428/android-i-want-to-shake-it
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        currAccel = SensorManager.GRAVITY_EARTH;
        prevAccel = SensorManager.GRAVITY_EARTH;
        shakeCount = 0;
        Log.w("T", "Created mSensorManager");
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent se) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            prevAccel = currAccel;
            currAccel = (float) Math.sqrt((double) (x*x + y*y + z*z));

            float delta = Math.abs(currAccel - prevAccel);
            if (delta > 11) {
                shakeCount++;
                if(shakeCount > 2) {
                    shakeCount = 0;
                    Log.w("T", "Device is shaking. Previous Accel is " + Float.toString(prevAccel) + " and current accel is " + Float.toString(currAccel));
                    Intent sendIntent = new Intent(ShakeListener.this, WatchToPhoneService.class);
                    sendIntent.putExtra("TYPE", "Shake");
                    startService(sendIntent);
                }
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
