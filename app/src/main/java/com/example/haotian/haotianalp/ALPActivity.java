package com.example.haotian.haotianalp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ALPActivity extends Activity implements SensorEventListener {
    protected LockPatternView mPatternView;
    protected PatternGenerator mGenerator;
    protected Button mGenerateButton;
    protected Button mDesigner;
    protected ToggleButton mPracticeToggle;
    private List<Point> mEasterEggPattern;
    protected SharedPreferences mPreferences;
    protected int mGridLength=0;
    protected int mPatternMin=0;
    protected int mPatternMax=0;
    protected String mHighlightMode;
    protected boolean mTactileFeedback;

    private static final String TAG = "SensorActivity";
    private static final String TAGmotion = "motionEvent";
    private SensorManager mSensorManager = null;

    public List<Sensor> deviceSensors;
    private  Sensor mAccelerometer, mMagnetometer, mGyroscope, mRotation, mGravity, myLinearAcc;

    private File file;
    protected File path;
    protected File hw2data;
    private File hw3data;
    protected File hw4data;
    public static String[] mLine;
    public BufferedWriter bufferedWriter;
    protected FileOutputStream outputStream = null;
    private VelocityTracker mVelocityTracker = null;
    private int control = 0;
    DateFormat mDateFormat;
    String mTimestamp;
    private int counter=0;
    private String myStr = "";

    String hw2DataFile;

    // Fields for storing sensor data
    float[] accel; // Accelerometer
    float[] mag; // Magnetometer
    float[] gyro; // Gyroscope
    float[] rotation; // Rotation
    float[] linear; // Linear accleration
    float[] gravity; // Gravity

    StringBuilder data;
    private int dataCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mGenerator = new PatternGenerator();

        setContentView(R.layout.activity_alp);
        mPatternView = (LockPatternView) findViewById(R.id.pattern_view);

        // HW1
        mGenerateButton = (Button) findViewById(R.id.generate_button);
            mGenerateButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View V) {
                    mPatternView.setPattern(mGenerator.getPattern()); // Generate random pattern
                    mPatternView.invalidate(); // Invalidate view and redraw
                }
            });

        mPracticeToggle = (ToggleButton) findViewById(R.id.practice_toggle);


        mPracticeToggle.setOnCheckedChangeListener(
                new ToggleButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mPatternView.setPracticeMode(isChecked); // Toggle practice mode state
                        mPatternView.invalidate(); // Invalidate view and redraw
                    }
                });
        // End HW1

        // HW2
        data = new StringBuilder();
        dataCount = 0;

        hw2DataFile = "hw2DataFile.csv";
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        path.mkdirs();
        hw2data = new File(path, "hw2Data.csv");
        hw3data = new File(path, "hw3Data.csv");
        hw4data = new File(path, "mattPattern4.csv");
        headerDataString();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        myLinearAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mRotation, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, myLinearAcc, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_NORMAL);

        accel = new float[3]; // Accelerometer
        mag = new float[3]; // Magnetometer
        gyro = new float[3]; // Gyroscope
        rotation = new float[3]; // Rotation
        linear = new float[3]; // Linear accleration
        gravity = new float[3]; // Gravity
    }

    @Override
    protected void onResume()
    {
        super.onResume();


        updateFromPrefs();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_al, menu);
        return true;
    }

    @Override
    protected void onPause() {

        super.onPause();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateFromPrefs()
    {
        int gridLength =
                mPreferences.getInt("grid_length", Defaults.GRID_LENGTH);
        int patternMin =
                mPreferences.getInt("pattern_min", Defaults.PATTERN_MIN);
        int patternMax =
                mPreferences.getInt("pattern_max", Defaults.PATTERN_MAX);
        String highlightMode =
                mPreferences.getString("highlight_mode", Defaults.HIGHLIGHT_MODE);
        boolean tactileFeedback = mPreferences.getBoolean("tactile_feedback",
                Defaults.TACTILE_FEEDBACK);

        // sanity checking
        if(gridLength < 1)
        {
            gridLength = 1;
        }
        if(patternMin < 1)
        {
            patternMin = 1;
        }
        if(patternMax < 1)
        {
            patternMax = 1;
        }
        int nodeCount = (int) Math.pow(gridLength, 2);
        if(patternMin > nodeCount)
        {
            patternMin = nodeCount;
        }
        if(patternMax > nodeCount)
        {
            patternMax = nodeCount;
        }
        if(patternMin > patternMax)
        {
            patternMin = patternMax;
        }

        // only update values that differ
        if(gridLength != mGridLength)
        {
            setGridLength(gridLength);
        }
        if(patternMax != mPatternMax)
        {
            setPatternMax(patternMax);
        }
        if(patternMin != mPatternMin)
        {
            setPatternMin(patternMin);
        }
        if(!highlightMode.equals(mHighlightMode))
        {
            setHighlightMode(highlightMode);
        }
        if(tactileFeedback ^ mTactileFeedback)
        {
            setTactileFeedback(tactileFeedback);
        }
    }

    private void setGridLength(int length)
    {
        mGridLength = length;
        mGenerator.setGridLength(length);
        mPatternView.setGridLength(length);
    }
    private void setPatternMin(int nodes)
    {
        mPatternMin = nodes;
        mGenerator.setMinNodes(nodes);
    }
    private void setPatternMax(int nodes)
    {
        mPatternMax = nodes;
        mGenerator.setMaxNodes(nodes);
    }
    private void setHighlightMode(String mode)
    {
        if("no".equals(mode))
        {
            mPatternView.setHighlightMode(new LockPatternView.NoHighlight());
        }
        else if("first".equals(mode))
        {
            mPatternView.setHighlightMode(new LockPatternView.FirstHighlight());
        }
        else if("rainbow".equals(mode))
        {
            mPatternView.setHighlightMode(
                    new LockPatternView.RainbowHighlight());
        }

        mHighlightMode = mode;
    }
    private void setTactileFeedback(boolean enabled)
    {
        mTactileFeedback = enabled;
        mPatternView.setTactileFeedbackEnabled(enabled);
    }

    public void writeToFile (File file) {
        if (isExternalStorageWriteable()) {
            try {
                bufferedWriter = new BufferedWriter(new FileWriter(file, true));
                bufferedWriter.write(data.toString());
                bufferedWriter.newLine();
                bufferedWriter.close();
                clearDataBuffer();
                dataCount++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            Log.e("Write error", "Failed isExternalStorageWriteable");
        }
    }

    public void clearDataBuffer() {
        data.setLength(0);
    }
    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Leaving empty for this assignment
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                getAccelerometer(event);
            case Sensor.TYPE_MAGNETIC_FIELD:
                getMagnetometer(event);
            case Sensor.TYPE_GYROSCOPE:
                getGyroscope(event);
            case Sensor.TYPE_ROTATION_VECTOR:
                getRotation(event);
            case Sensor.TYPE_LINEAR_ACCELERATION:
                getAcceleration(event);
            case Sensor.TYPE_GRAVITY:
                getGravity(event);
        }

        //String data = createSensorString(event);
        //writeToFile(hw3data, data);
    }

    public void headerDataString(){
        data.append("TimeStamp");
        data.append(", ");
        data.append("TYPE_ACC0");
        data.append(", ");
        data.append("TYPE_ACC1");
        data.append(", ");
        data.append("TYPE_ACC2");
        data.append(", ");
        data.append("TYPE_MAG0");
        data.append(", ");
        data.append("TYPE_MAG1");
        data.append(", ");
        data.append("TYPE_MAG2");
        data.append(", ");
        data.append("TYPE_GYRO0");
        data.append(", ");
        data.append("TYPE_GYRO1");
        data.append(", ");
        data.append("TYPE_GYRO2");
        data.append(", ");
        data.append("TYPE_ROTATION0");
        data.append(", ");
        data.append("TYPE_ROTATION1");
        data.append(", ");
        data.append("TYPE_ROTATION2");
        data.append(", ");
        data.append("TYPE_LINEAR0");
        data.append(", ");
        data.append("TYPE_LINEAR1");
        data.append(", ");
        data.append("TYPE_LINEAR2");
        data.append(", ");
        data.append("TYPE_GRAVITY0");
        data.append(", ");
        data.append("TYPE_GRAVITY1");
        data.append(", ");
        data.append("TYPE_GRAVITY2");
        data.append(", ");
        data.append("position_x");
        data.append(", ");
        data.append("position_y");
        data.append(", ");
        data.append("velocity_x");
        data.append(", ");
        data.append("velocity_y");
        data.append(", ");
        data.append("pressure");
        data.append(", ");
        data.append("size");
        data.append(", ");
        data.append("mCurrentPattern");
        data.append(", ");
        data.append("Counter");
        data.append(System.lineSeparator());
    }

    public void createDataString(MotionEvent event, float xVelocity, float yVelocity) {

        data.append(event.getEventTime());
        data.append(", ");
        data.append(accel[0]);
        data.append(", ");
        data.append(accel[1]);
        data.append(", ");
        data.append(accel[2]);
        data.append(", ");
        data.append(mag[0]);
        data.append(", ");
        data.append(mag[1]);
        data.append(", ");
        data.append(mag[2]);
        data.append(", ");
        data.append(gyro[0]);
        data.append(", ");
        data.append(gyro[1]);
        data.append(", ");
        data.append(gyro[2]);
        data.append(", ");
        data.append(rotation[0]);
        data.append(", ");
        data.append(rotation[1]);
        data.append(", ");
        data.append(rotation[2]);
        data.append(", ");
        data.append(linear[0]);
        data.append(", ");
        data.append(linear[1]);
        data.append(", ");
        data.append(linear[2]);
        data.append(", ");
        data.append(gravity[0]);
        data.append(", ");
        data.append(gravity[1]);
        data.append(", ");
        data.append(gravity[2]);
        data.append(", ");
        data.append(event.getX());
        data.append(", ");
        data.append(event.getY());
        data.append(", ");
        data.append(xVelocity);
        data.append(", ");
        data.append(yVelocity);
        data.append(", ");
        data.append(event.getPressure());
        data.append(", ");
        data.append(event.getSize());
        data.append(", ");
        data.append("\"" +mPatternView.mCurrentPattern.toString() +"\"");
        data.append(", ");
        data.append(dataCount);
        data.append(System.lineSeparator());
    }

    private void getAccelerometer(SensorEvent event) {
        accel = event.values;
    }

    private void getMagnetometer(SensorEvent event) {
        mag = event.values;
    }

    private void getGyroscope(SensorEvent event) {
        gyro = event.values;
    }

    private void getRotation(SensorEvent event) {
        rotation = event.values;
    }

    private void getAcceleration(SensorEvent event) {
        linear = event.values;
    }

    private void getGravity(SensorEvent event) {
        gravity = event.values;
    }

    private boolean isExternalStorageWriteable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
