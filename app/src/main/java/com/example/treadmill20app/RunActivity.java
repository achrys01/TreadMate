package com.example.treadmill20app;


import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.treadmill20app.BtServices.BleFtmsService;
import com.example.treadmill20app.BtServices.BleHeartRateService;
import com.example.treadmill20app.BtServices.GattActions;
import com.example.treadmill20app.models.WorkoutEntry;
import com.example.treadmill20app.utils.TypeConverter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Locale;

import de.siegmar.fastcsv.reader.CsvParser;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;

import static com.example.treadmill20app.BtServices.GattActions.ACTION_GATT_HEART_RATE_EVENTS;
import static com.example.treadmill20app.BtServices.GattActions.ACTION_GATT_FTMS_EVENTS;
import static com.example.treadmill20app.BtServices.GattActions.COMMAND_CHAR;
import static com.example.treadmill20app.BtServices.GattActions.CONTROL_TYPE;
import static com.example.treadmill20app.BtServices.GattActions.HR_EVENT;
import static com.example.treadmill20app.BtServices.GattActions.FTMS_EVENT;
import static com.example.treadmill20app.BtServices.GattActions.HEART_RATE_DATA;
import static com.example.treadmill20app.BtServices.GattActions.INCLINATION_DATA;
import static com.example.treadmill20app.BtServices.GattActions.INCL_INCREMENT;
import static com.example.treadmill20app.BtServices.GattActions.INSTANT_SPEED_DATA;
import static com.example.treadmill20app.BtServices.GattActions.MAX_INCL;
import static com.example.treadmill20app.BtServices.GattActions.MAX_SPEED;
import static com.example.treadmill20app.BtServices.GattActions.MIN_INCL;
import static com.example.treadmill20app.BtServices.GattActions.MIN_SPEED;
import static com.example.treadmill20app.BtServices.GattActions.SPEED_INCREMENT;
import static com.example.treadmill20app.BtServices.GattActions.STATUS_TYPE;
import static com.example.treadmill20app.BtServices.GattActions.STATUS_VALUE;
import static com.example.treadmill20app.BtServices.GattActions.TOTAL_DISTANCE_DATA;

/**
 * This activity is based on the Public API for the Bluetooth GATT Profile.
 * The BLE-GATT-Movesense-2.0 application provided by anderslm on github:
 * https://gits-15.sys.kth.se/anderslm/Ble-Gatt-Movesense-2.0 is used as a reference.
 * The activity subscribes to writeable, readable, indicatable and
 * notifiable characteristics of a fitness machine service following the
 * FTMS protocol for bluetooth fitness machines:
 * https://www.bluetooth.org/DocMan/handlers/DownloadDoc.ashx?doc_id=423422
 * CSV Reader and Writer are implemented from the FastCSV (v1.0.2) API  provided by Oliver Siegmar
 * https://github.com/osiegmar/FastCSV
 **/
public class RunActivity extends MenuActivity {

    private BluetoothDevice mSelectedFTMS = null;

    private Handler mHandler;

    //
    public static final String EXTRAS_HR_NAME = "HR_NAME";
    public static final String EXTRAS_HR_ADDRESS = "HR_ADDRESS";
    public static final String EXTRAS_FTMS_ADDRESS = "FTMS_ADDRESS";

    private TextView mHRView;

    private String mHrAddress;
    private String mFtmsAddress;

    private BleHeartRateService mBluetoothLeHrService;
    private BleFtmsService mBluetoothLeFtmsService;
    //

    private TextView mConnectionView;
    private TextView mTdView;
    private TextView mSpeedView;
    private TextView mInclView;
    private TextView mControlSpeedView;
    private TextView mControlInclView;
    private SeekBar mSpeedBar;
    private SeekBar mInclBar;
    private ImageButton mSpeedUp;
    private ImageButton mSpeedDown;
    private ImageButton mInclUp;
    private ImageButton mInclDown;
    private Button StartStopButton;

    private boolean isRunning;
    private boolean isConnected;

    private static int maxSpeed;
    private static int minSpeed;
    private static double speedIncrement;
    private static int maxIncl;
    private static int minIncl;
    private static double inclIncrement;

    private static final String LOG_TAG = "RunActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = findViewById(R.id.menu_frame);
        getLayoutInflater().inflate(R.layout.activity_run, contentFrameLayout);

        //Find views, buttons and seekbars
        TextView mDeviceView = findViewById(R.id.deviceView);
        mConnectionView = findViewById(R.id.connection_view);
        mTdView = findViewById(R.id.Td_view);
        mSpeedView = findViewById(R.id.speed_view);
        mInclView = findViewById(R.id.incl_view);
        mHRView = findViewById(R.id.heart_rate_view);
        mSpeedBar = findViewById(R.id.speed_bar);
        mInclBar = findViewById(R.id.incl_bar);
        mSpeedUp = findViewById(R.id.speed_up);
        mSpeedDown = findViewById(R.id.speed_down);
        mInclUp = findViewById(R.id.incl_up);
        mInclDown = findViewById(R.id.incl_down);
        mControlSpeedView = findViewById(R.id.speed);
        mControlInclView = findViewById(R.id.incl);
        StartStopButton = findViewById(R.id.startStopButton);
        //Toolbar myToolbar = findViewById(R.id.my_toolbar);
        //setSupportActionBar(myToolbar);
        //TODO: Find another way to do this. Is run every time you enter the activity.
        //isConnected = false;
        //Disable all buttons until connected
        //enable(false);

        // Get the selected device from the intent
        Intent intent = getIntent();
        mSelectedFTMS = intent.getParcelableExtra(StartTrainingActivity.SELECTED_FTMS);
        Log.i(LOG_TAG, "selected device" + mSelectedFTMS);
        if (mSelectedFTMS == null) {
            mDeviceView.setText(R.string.devices_info);
        } else {
            mDeviceView.setText(mSelectedFTMS.getName());
        }

        //The bluetooth callbacks are performed on a worker thread.
        //Use this handler to update ui.
        mHandler = new Handler();

        //SeekbarListeners. Update text views in accordance to changes
        mSpeedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                String controlSpeed = String.format(Locale.ENGLISH, "%.1f km/h", (float) (progress * speedIncrement));
                mControlSpeedView.setText(controlSpeed);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }


            //When seekbar released, let treadmill know.
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() == 0)
                    seekBar.setProgress(1);
                setSpeed((float) (seekBar.getProgress() * speedIncrement));
            }
        });

        mInclBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // Print current value (seekbar progress*increment) to text view
                String controlIncl = String.format(Locale.ENGLISH, "%.1f", (float) (progress * inclIncrement)) + " %";
                mControlInclView.setText(controlIncl);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Set inclination with seekbar change
                setIncl((float) (seekBar.getProgress() * inclIncrement));
            }
        });

        // the intent from BleHeartRateService, that started this activity
        mHrAddress = intent.getStringExtra(EXTRAS_HR_ADDRESS);
        if (mHrAddress == null) {
            mHRView.setText(R.string.no_data);
        } else {
            mHRView.setText(R.string.connecting_hr);
            // NB! bind to the BleHeartRateService
            // Use onResume or onStart to register a BroadcastReceiver.
            Intent gattHRServiceIntent = new Intent(this, BleHeartRateService.class);
            bindService(gattHRServiceIntent, mHrServiceConnection, BIND_AUTO_CREATE);
            //TODO: Temporary fix, find better way to do all below
            /*
            isConnected = true;
            //Disable all buttons until connected
            enable(true);
            mConnectionView.setText(R.string.connected);
            maxSpeed = intent.getIntExtra(MAX_SPEED,0);
            minSpeed = intent.getIntExtra(MIN_SPEED,0);
            speedIncrement = intent.getDoubleExtra(SPEED_INCREMENT,0);

            mSpeedBar.setMax((int) (maxSpeed * speedIncrement));
            maxIncl = intent.getIntExtra(MAX_INCL,0);
            minIncl = intent.getIntExtra(MIN_INCL,0);
            inclIncrement = intent.getDoubleExtra(INCL_INCREMENT,0);

            int inclIntervals = (int) (maxIncl / (10 * inclIncrement));
            mInclBar.setMax(inclIntervals);
             */
        }

        // the intent from BleHeartRateService, that started this activity
        mFtmsAddress = intent.getStringExtra(EXTRAS_FTMS_ADDRESS);


        // NB! bind to the BleFtmsService
        // Use onResume or onStart to register a BroadcastReceiver.
        Intent gattFtmsServiceIntent = new Intent(this, BleFtmsService.class);
        bindService(gattFtmsServiceIntent, mFtmsServiceConnection, BIND_AUTO_CREATE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeHrService != null) {
            final boolean resultHr = mBluetoothLeHrService.connect(mHrAddress);
        }
        if (mBluetoothLeFtmsService != null) {
            final boolean resultFtms = mBluetoothLeFtmsService.connect(mFtmsAddress);
        }
    }

//IS this needed?
/*
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO: Find other way to change the text color
        getMenuInflater().inflate(R.menu.menu_run, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.connect_hr_sensor) {
            Intent intentNew = new Intent(RunActivity.this, ScanHRActivity.class);
            intentNew.putExtra(StartTrainingActivity.SELECTED_FTMS,mSelectedFTMS);
            startActivity(intentNew);
        } else if (item.getItemId() == R.id.disconnect) {
            /* Old method to disconnect, doesn't work with a service.
            if (mBluetoothGatt != null) {
                mBluetoothGatt.disconnect();
                mConnectionView.setText(R.string.disconnected);
                enable(false);
                try {
                    mBluetoothGatt.close();
                } catch (Exception e) {
                    // Android BLE API bug handling
                }
            }
             */
        }else if (!isConnected)
            return false;
        else if (item.getItemId() == R.id.load_workout) {
            Intent intentLoad = new Intent(Intent.ACTION_GET_CONTENT);
            intentLoad.setType("*/*");
            startActivityForResult(intentLoad, requestCode);
        } else if (item.getItemId() == R.id.new_workout) {
            Intent intentNew = new Intent(RunActivity.this, WorkoutActivity.class);
            startActivity(intentNew);
        }

        return super.onOptionsItemSelected(item);
    }

    //Reading a pre-defined workout from a csv
    int requestCode = 1;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            final String[] split = uri.getPath().split(":"); //split the path
            String filePath = split[1]; //assign second part to a string
            File file = new File(filePath);
            CsvReader csvReader = new CsvReader();
            csvReader.setContainsHeader(true); //If csv contains headers
            ArrayList<WorkoutEntry> workout = new ArrayList<>();
            //CSV parsing
            try (CsvParser csvParser = csvReader.parse(file, StandardCharsets.UTF_8)) {
                CsvRow row;
                while ((row = csvParser.nextRow()) != null) {
                    WorkoutEntry newEntry = new WorkoutEntry();
                    newEntry.setDur(Float.parseFloat(row.getField("Duration")));
                    newEntry.setSpeed(Float.parseFloat(row.getField("Speed")));
                    newEntry.setIncl(Float.parseFloat(row.getField("Inclination")));
                    workout.add(newEntry);
                }
                Toast.makeText(this, "Loaded from" + filePath, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

            ListIterator<WorkoutEntry> workoutIterate = workout.listIterator();
            //This runnable is designed to prevent timeout errors. The speed is changed just before the inclination
            Runnable executeCsv = new Runnable() {
                int counter = 0;
                WorkoutEntry nextEntry;

                @Override
                public void run() {
                    if (workoutIterate.hasNext() && counter % 2 == 0) {
                        nextEntry = workoutIterate.next();
                        setSpeed(nextEntry.getSpeed());
                        mHandler.postDelayed(this, 500);
                        counter++;
                    } else if (counter % 2 == 1) {
                        setIncl(nextEntry.getIncl());
                        mHandler.postDelayed(this, (long) (nextEntry.getDur() * 60 * 1000 - 500));
                        counter++;
                    }
                }
            };
            mHandler.post(executeCsv);
        }
    }

    //Method linked to green speed arrow
    public void speed_up(View view) {
        int curr = mSpeedBar.getProgress();
        mSpeedBar.setProgress(curr + 1);
        setSpeed((float) (mSpeedBar.getProgress() * speedIncrement));
    }//Method linked to red speed arrow

    public void speed_down(View view) {
        int curr = mSpeedBar.getProgress();
        if (curr != 1) {
            mSpeedBar.setProgress(curr - 1);
            setSpeed((float) (mSpeedBar.getProgress() * speedIncrement));
        }
    }//Method linked to green inclination arrow.

    public void incl_up(View view) {
        int curr = mInclBar.getProgress();
        mInclBar.setProgress(curr + 1);
        setIncl((float) (mInclBar.getProgress() * inclIncrement));
    }//Method linked to red inclination arrow.

    public void incl_down(View view) {
        int curr = mInclBar.getProgress();
        mInclBar.setProgress(curr - 1);
        setIncl((float) (mInclBar.getProgress() * inclIncrement));
    }

    //Method to change speed of treadmill. Will have to be accepted before execution. Happens in onChangedCharacteristic
    public void setSpeed(float Speed) {
        byte[] a = new byte[3];
        a[0] = 2;
        byte[] b = TypeConverter.intToBytes((int) (Speed * 100), 2);
        System.arraycopy(b, 0, a, 1, 2);
        // Broadcast the control array to the service
        broadcastControl(a);
    }

    //Method to change inclination of treadmill. Will have to be accepted before execution. Happens in onChangedCharacteristic
    public void setIncl(float Inclination) {
        byte[] a = new byte[3];
        a[0] = 3;
        byte[] b = TypeConverter.intToBytes((int) (Inclination * 10), 2);
        System.arraycopy(b, 0, a, 1, 2);
        // Broadcast the control array to the service
        broadcastControl(a);
    }

    //Singletons used in setting spinner values
    public static double getSpeedIncrement() {
        return speedIncrement;
    }

    public static double getInclIncrement() {
        return inclIncrement;
    }

    public static int getMaxSpeed() {
        return maxSpeed;
    }

    public static int getMaxIncl() {
        return maxIncl;
    }

    //Method linked to the start/stop button
    public void start_stop(View view) {
        byte[] a = new byte[1];
        if (isRunning)
            a[0] = 8;
        else
            a[0] = 7;
        // Broadcast the control array to the service
        broadcastControl(a);
    }

    //Method to disable and enable buttons and seekbars on ui
    public void enable(Boolean enable) {
        mSpeedUp.setEnabled(enable);
        mSpeedDown.setEnabled(enable);
        mInclUp.setEnabled(enable);
        mInclDown.setEnabled(enable);
        mSpeedBar.setEnabled(enable);
        mInclBar.setEnabled(enable);
        StartStopButton.setEnabled(enable);

        float alpha;
        if (enable)
            alpha = 1.0f;
        else
            alpha = 0.1f;
        mSpeedUp.setAlpha(alpha);
        mSpeedDown.setAlpha(alpha);
        mInclUp.setAlpha(alpha);
        mInclDown.setAlpha(alpha);
    }

    /*
    Callback methods to manage the (BleHeartRate)Service lifecycle.
    */
    private final ServiceConnection mHrServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeHrService = ((BleHeartRateService.LocalBinder) service).getService();
            if (!mBluetoothLeHrService.initialize()) {
                Log.i(LOG_TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeHrService.connect(mHrAddress);
            Log.i(LOG_TAG, "Hr onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeHrService = null;
            Log.i(LOG_TAG, "onServiceDisconnected");
        }
    };

    /*
    Callback methods to manage the (Ftms)Service lifecycle.
    */
    private final ServiceConnection mFtmsServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeFtmsService = ((BleFtmsService.LocalBinder) service).getService();
            if (!mBluetoothLeFtmsService.initialize()) {
                Log.i(LOG_TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeFtmsService.connect(mFtmsAddress);
            Log.i(LOG_TAG, "Ftms onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeFtmsService = null;
            Log.i(LOG_TAG, "onServiceDisconnected");
        }
    };


    private void broadcastControl(byte[] command_char) {
        final Intent intent = new Intent(ACTION_GATT_FTMS_EVENTS);
        intent.putExtra(FTMS_EVENT, GattActions.FTMS_Event.FTMS_COMMAND);
        //Add control updates to intent
        intent.putExtra(COMMAND_CHAR, command_char);
        sendBroadcast(intent);
    }


    /*
    A BroadcastReceiver handling various events fired by the Service, see GattActions.Event.
    */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (ACTION_GATT_HEART_RATE_EVENTS.equals(action)) {
                GattActions.HR_Event hr_event = (GattActions.HR_Event) intent.getSerializableExtra(HR_EVENT);
                if (hr_event != null) {
                    switch (hr_event) {
                        case GATT_CONNECTED:
                        case GATT_DISCONNECTED:
                        case GATT_CONNECTING:
                            mHRView.setText("-");
                        case GATT_SERVICES_DISCOVERED:
                        case HEART_RATE_SERVICE_DISCOVERED:
                            mHRView.setText("-");
                            break;
                        case HR_DATA_AVAILABLE:
                            int heartRate = intent.getIntExtra(HEART_RATE_DATA, -1);
                            Log.i(LOG_TAG, "got hr data: " + heartRate);
                            if (heartRate < 0) {
                                mHRView.setText("?");
                            } else {
                                mHRView.setText(String.format(Locale.ENGLISH, "%d", heartRate));
                            }
                            break;
                        case HEART_RATE_SERVICE_NOT_AVAILABLE:
                            break;
                        default:
                            mHRView.setText("?");
                    }
                }

            }
            if (ACTION_GATT_FTMS_EVENTS.equals(action)) {
                GattActions.FTMS_Event ftms_event = (GattActions.FTMS_Event) intent.getSerializableExtra(FTMS_EVENT);
                if (ftms_event != null) {
                    switch (ftms_event) {
                        case GATT_CONNECTED:
                            mConnectionView.setText(R.string.connected);
                            break;
                        case GATT_DISCONNECTED:
                            mConnectionView.setText(R.string.disconnected);
                            break;
                        case GATT_CONNECTING:
                            mConnectionView.setText(R.string.connecting);
                            break;
                        case GATT_SERVICES_DISCOVERED:
                        case FTMS_DATA_AVAILABLE:
                            double instSpeed = intent.getDoubleExtra(INSTANT_SPEED_DATA, -1);
                            double incl = intent.getDoubleExtra(INCLINATION_DATA, -1);
                            int totDist = intent.getIntExtra(TOTAL_DISTANCE_DATA, -1);

                            if(instSpeed != -1 ) {
                                Log.i(LOG_TAG, "got ftms data: ");
                                Log.i(LOG_TAG, "Speed: " + instSpeed);
                                Log.i(LOG_TAG, "Inclination: " + incl);
                                Log.i(LOG_TAG, "Distance: " + totDist);

                                mTdView.setText(String.valueOf(totDist));
                                //The following two views are not displayed, this will not be needed!
                                mSpeedView.setText(String.valueOf(instSpeed));
                                mInclView.setText(String.valueOf(incl));
                            }

                            break;
                        case SUPPORTED_SPEED:
                            maxSpeed = intent.getIntExtra(MAX_SPEED,0);
                            minSpeed = intent.getIntExtra(MIN_SPEED,0);
                            speedIncrement = intent.getDoubleExtra(SPEED_INCREMENT,0);

                            mSpeedBar.setMax((int) (maxSpeed * speedIncrement));
                            break;
                        case SUPPORTED_INCLINATION:
                            maxIncl = intent.getIntExtra(MAX_INCL,0);
                            minIncl = intent.getIntExtra(MIN_INCL,0);
                            inclIncrement = intent.getDoubleExtra(INCL_INCREMENT,0);

                            int inclIntervals = (int) (maxIncl / (10 * inclIncrement));
                            mInclBar.setMax(inclIntervals);
                            break;
                        case FTMS_STATUS:
                            int type = intent.getIntExtra(STATUS_TYPE,-1);
                            int value = intent.getIntExtra(STATUS_VALUE,0);
                            if (type == 4) {
                                System.out.println("Treadmill started by user");
                                StartStopButton.setBackgroundColor(Color.RED);
                                StartStopButton.setText(R.string.stop);
                                isRunning = true;
                            } else if (type == 2 && value == 1) {
                                System.out.println("Treadmill stopped by user");
                                StartStopButton.setBackgroundColor(Color.GREEN);
                                StartStopButton.setText(R.string.start);
                                isRunning = false;
                            } else if (type == 5) {
                                String speedText = String.format(Locale.ENGLISH, "%.1f km/h", value / 100.0);
                                System.out.println("Speed changed to " + speedText);
                                mControlSpeedView.setText(speedText);
                                mSpeedBar.setProgress((int) (value * speedIncrement));
                            } else if (type == 6) {
                                String inclText = String.format(Locale.ENGLISH, "%.1f", value / 10.0) + " %";
                                System.out.println("Inclination changed to " + inclText);
                                mControlInclView.setText(inclText);
                                mInclBar.setProgress((int) (value / (inclIncrement * 10)));
                            }
                            break;
                        case FTMS_CONTROL:
                            String control_type = intent.getStringExtra(CONTROL_TYPE);
                            switch (control_type) {
                                case "Control granted":
                                    StartStopButton.setBackgroundColor(Color.RED);
                                    enable(true);
                                    break;
                                case "Start":
                                    StartStopButton.setBackgroundColor(Color.RED);
                                    StartStopButton.setText(R.string.stop);
                                    isRunning = true;
                                    break;
                                case "Stop":
                                    StartStopButton.setBackgroundColor(Color.GREEN);
                                    StartStopButton.setText(R.string.start);
                                    isRunning = false;
                                    break;
                            }


                    }
                }
            }
        }
    };

    // Intent filter for broadcast updates from BleHeartRateServices and BleFtmsServices
    private IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GATT_HEART_RATE_EVENTS);
        intentFilter.addAction(ACTION_GATT_FTMS_EVENTS);
        return intentFilter;
    }

}