package edu.rosehulman.fieldsensors;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import edu.rosehulman.fieldsensors.edu.rosehulman.me435.FieldOrientation;
import edu.rosehulman.fieldsensors.edu.rosehulman.me435.FieldOrientationListener;


public class FieldSensorActivity extends Activity implements FieldOrientationListener {

    /** Field Orientation instance that gives field theta values from the sensor heading. */
    private FieldOrientation mFieldOrientation;

    /** Text views that will be updated. */
    private TextView mHeadingTextView, mRawDataTextView, mCounterTextView;

    /** Counter for the number of updates received. */
    private long mUpdatesCounter = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_sensor);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mHeadingTextView = (TextView) findViewById(R.id.heading_textview);
        mRawDataTextView = (TextView) findViewById(R.id.raw_data_textview);
        mCounterTextView = (TextView) findViewById(R.id.counter_textview);

        mFieldOrientation = new FieldOrientation(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFieldOrientation.registerListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFieldOrientation.unregisterListener();
    }

    @Override
    public void onSensorChanged(double fieldHeading, float[] orientationValues) {
        mUpdatesCounter++;
        mCounterTextView.setText("" + mUpdatesCounter);
        mHeadingTextView.setText(String.format("%.1f°", fieldHeading));
        mRawDataTextView.setText(String.format("%.1f°  %.1f°  %.1f°", orientationValues[0], orientationValues[1], orientationValues[2]));
    }

    public void handleSetAsNeg30(View view) {
        mFieldOrientation.setCurrentFieldHeading(-30);
    }

    public void handleSetAs0(View view) {
        mFieldOrientation.setCurrentFieldHeading(0);
    }

    public void handleSetAs30(View view) {
        mFieldOrientation.setCurrentFieldHeading(30);
    }
}