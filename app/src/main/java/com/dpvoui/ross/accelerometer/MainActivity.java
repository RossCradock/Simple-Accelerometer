package com.dpvoui.ross.accelerometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ToggleButton;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Properties;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
// MainActivity class requires an onCreate method to initialise the app screen and record its current state if paused
// When MainActivity implements SensorEventListener the methods onAccuracyChanged and onSensorChanged must be created

    private SensorManager sensorManger;
    private Sensor accelerometer;
    //  SensorManager and Sensor are the API classes used to receive raw data from android accelerometer

    private Boolean accPresent;
    private Calendar now = Calendar.getInstance();
    private String errorMessage;
    public long startTime;

    // The above float variables are initilialised  and will represent acceleration in the x, y and z axes

    private Properties propertiesX = new Properties();
    private Properties propertiesY = new Properties();
    private Properties propertiesZ = new Properties();
	// these are the key-value paired dictionary arrays where the time and acceleration properties are stored

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //set layout
        final ToggleButton onoff = (ToggleButton)findViewById(R.id.toggleButton);
        final EditText editText = (EditText)findViewById(R.id.editText);
        sensorManger = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        if(sensorManger.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){ // Check to see if the phone has a operational accelerometer
            // Accelerometer found
            accelerometer = sensorManger.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // Initialise the accelerometer
            accPresent = true;
            errorMessage = "\nThe app found an operational accelerometer. - " + now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE);
            editText.append(errorMessage);
        } else{
            // Accelerometer not found
            accPresent = false;
            errorMessage = "\nThe app failed to find an operational accelerometer. - " + now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE);
            editText.append(errorMessage);
        }

        onoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //toggle button clicked
                if(accPresent) {
                    if (onoff.isChecked()) { //app started
                        // Toggle button is turn on
                        startTime = System.currentTimeMillis();
                        registerList();
                    }
                    if (!onoff.isChecked()) { //app stopped
                        // Toggle button is turn off
                        errorMessage = "\nListener is turned Off - " + now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE);
                        editText.append(errorMessage);
                        unregisterList();
                        try {
                            File root = new File(Environment.getExternalStorageDirectory(), "Notes"); //try and find file "Notes" in the phone
                            if(!root.exists()){
                                //noinspection ResultOfMethodCallIgnored
                                root.mkdirs // create the directory if it is not found
                            }
                            File filepath = new File(root,"X_Axis_Acceleration.txt");
                            FileWriter writer = new FileWriter(filepath);
                            String xaxis = getPropertyAsString(propertiesX);
                            writer.append(xaxis);
                            writer.flush(); //write to the file the strings as a properties
                            writer.close();
                            errorMessage = "\nWritten to X_Axis_Acceleration.txt - " + now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE);
                            editText.append(errorMessage);

                            File Yfilepath = new File(root,"Y_Axis_Acceleration.txt");
                            FileWriter Ywriter = new FileWriter(Yfilepath);
                            String yaxis = getPropertyAsString(propertiesY);
                            Ywriter.append(yaxis);
                            Ywriter.flush();
                            Ywriter.close();
                            errorMessage = "\nWritten to Y_Axis_Acceleration.txt - " + now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE);
                            editText.append(errorMessage);

                            File Zfilepath = new File(root,"Z_Axis_Acceleration.txt");
                            FileWriter Zwriter = new FileWriter(Zfilepath);
                            String zaxis = getPropertyAsString(propertiesZ);
                            Zwriter.append(zaxis);
                            Zwriter.flush();
                            Zwriter.close();
                            errorMessage = "\nWritten to X_Axis_Acceleration.txt - " + now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE);
                            editText.append(errorMessage);

                        } catch (Exception e) {
                            //an issue occured throughout the write process
                            errorMessage = "\nNot Written to X_Axis_Acceleration.txt " + e + " - " + now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE);
                            editText.append(errorMessage);
                        }
                    }
                }else{
                    errorMessage = "\nNo accelerometer present - " + now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE);
                    editText.append(errorMessage);
                    onoff.setChecked(false);
                }
            }
        });
    }

    public void onAccuracyChanged(Sensor sensor, int i){
        // onAccuracyChanged method is mandatory when implementing SensorEventListener, but is not required
    }

    public void onSensorChanged(SensorEvent event){

        float xacceleration = event.values[0];
        float yacceleration = event.values[1];
        float zacceleration = event.values[2];
		//get the raw data values of acceleration in float
		
        String elaspsedTime = String.valueOf((System.currentTimeMillis() - startTime));
        String XaccString = String.valueOf(xacceleration) + "//"; // the string seen here of "//" is a seperator for the acceleration value and the time 
        String YaccString = String.valueOf(yacceleration) + "//";
        String ZaccString = String.valueOf(zacceleration) + "//";
		//floats must be changed to string
		
        if((System.currentTimeMillis() - startTime) < 1200000.0){ 
		// if app has been running for less than 20 mins then the string values of elapsed time and the acceleration values are put into the properties array
            propertiesX.put(elaspsedTime, XaccString);
            propertiesY.put(elaspsedTime, YaccString);
            propertiesZ.put(elaspsedTime, ZaccString);
        }
    }

    public String getPropertyAsString(Properties prop) {
		//this subroutine changes the entire properties array into a string so it can be written to a file
        StringWriter writer = new StringWriter();
        try {
            prop.store(writer, "");
        } catch (IOException e) {
            final EditText editText = (EditText)findViewById(R.id.editText);
            errorMessage = "\nProperties wasn't converted to a string -" + now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE);
            editText.append(errorMessage);
        }
        return writer.getBuffer().toString();
    }

    public void registerList(){
        sensorManger.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL); // Register a listener for accelerometer changes with a 200ms delay(SENSOR_DELAY_NORMAL)
    }

    public void unregisterList(){
        sensorManger.unregisterListener(this);
    }

    public void onPause(){
        super.onPause();
    }

    public void onResume(){
        super.onResume();
        registerList();
    }

}
