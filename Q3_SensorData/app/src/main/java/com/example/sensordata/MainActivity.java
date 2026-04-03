package com.example.sensordata;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;

    private Sensor accelerometer;
    private Sensor lightSensor;
    private Sensor proximitySensor;

    private TextView tvAccelX, tvAccelY, tvAccelZ;
    private TextView tvLight;
    private TextView tvProximity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvAccelX = findViewById(R.id.tvAccelX);
        tvAccelY = findViewById(R.id.tvAccelY);
        tvAccelZ = findViewById(R.id.tvAccelZ);
        tvLight = findViewById(R.id.tvLight);
        tvProximity = findViewById(R.id.tvProximity);

        // getSystemService se Android ka sensor system milta hai
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // SENSOR_DELAY_NORMAL = kitni speed se data aayega
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (proximitySensor != null) {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    // jab app background mein jaaye, sensors band karo
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // event.sensor.getType batata hai kis sensor ne data bheja hai
        int sensorType = event.sensor.getType();

        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            // accelerometer 3 values deta hai  X, Y, Z
            tvAccelX.setText(String.format("X: %.2f", event.values[0]));
            tvAccelY.setText(String.format("Y: %.2f", event.values[1]));
            tvAccelZ.setText(String.format("Z: %.2f", event.values[2]));
        }
        else if (sensorType == Sensor.TYPE_LIGHT) {
            // light sensor ek hi value deta hai
            tvLight.setText(String.format("Light: %.2f lux", event.values[0]));
        }
        else if (sensorType == Sensor.TYPE_PROXIMITY) {
            // proximity sensor ek value deta hai - cm mein distance
            tvProximity.setText(String.format("Proximity: %.2f cm", event.values[0]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

        //  ye method likhna zaroori hai kyunki SensorEventListener interface mein ye defined hai
    }
}