package com.edu.csuft.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.MobileAnarchy.Android.Widgets.Joystick.DualJoystickView;
import com.MobileAnarchy.Android.Widgets.Joystick.JoystickMovedListener;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final int BTB_A = 4096;
    public static final int BTB_B = 8192;
    public static final int BTB_BLUE = 16384;
    public static final int BTB_C = 16384;
    public static final int BTB_CIRCLE = 8192;
    public static final int BTB_CROSS = 16384;
    public static final int BTB_D = 32768;
    public static final int BTB_GREEN = 4096;
    public static final int BTB_L1 = 1024;
    public static final int BTB_L2 = 256;
    public static final int BTB_L3 = 2;
    public static final int BTB_PAD_DOWN = 64;
    public static final int BTB_PAD_LEFT = 128;
    public static final int BTB_PAD_RIGHT = 32;
    public static final int BTB_PAD_UP = 16;
    public static final int BTB_PINK = 32768;
    public static final int BTB_R1 = 2048;
    public static final int BTB_R2 = 512;
    public static final int BTB_R3 = 4;
    public static final int BTB_RED = 8192;
    public static final int BTB_SELECT = 1;
    public static final int BTB_SQUARE = 32768;
    public static final int BTB_START = 8;
    public static final int BTB_TRIANGLE = 4096;
    public static final String DEVICE_NAME = "device_name";
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_WRITE = 3;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String TOAST = "toast";
    private int buttonsData;
    private Button mButtonA;
    private Button mButtonB;
    private Button mButtonC;
    private Button mButtonD;
    private Button mButtonDOWN;
    private Button mButtonLEFT;
    private Button mButtonRIGHT;
    private Button mButtonSET;
    private Button mButtonUP;
    private int mDataFormat;
    DualJoystickView mDualJoystick;
    private MenuItem mItemAbout;
    private MenuItem mItemConnect;
    private MenuItem mItemOptions;
    private int mMaxTimeoutCount;
    private String mStrA;
    private String mStrB;
    private String mStrC;
    private String mStrD;
    private TextView mTxtDataL;
    private TextView mTxtDataR;
    private TextView mTxtStatus;
    private long mUpdatePeriod;
    private Timer mUpdateTimer;
    private final boolean D = true;
    private String mConnectedDeviceName = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothRfcommClient mRfcommClient = null;
    private double mRadiusL = 0.0d;
    private double mRadiusR = 0.0d;
    private double mAngleL = 0.0d;
    private double mAngleR = 0.0d;
    private boolean mCenterL = true;
    private boolean mCenterR = true;
    private byte lX = 0;
    private byte lY = 0;
    private byte rX = 0;
    private byte rY = 0;
    private int mTimeoutCounter = 0;

    private JoystickMovedListener _listenerLeft = new JoystickMovedListener() { // from class: org.lien.btjoystick.MainActivity.14
        @Override // com.MobileAnarchy.Android.Widgets.Joystick.JoystickMovedListener
        public void OnMoved(int pan, int tilt) {
            MainActivity.this.mRadiusL = Math.sqrt((double) ((pan * pan) + (tilt * tilt)));
            MainActivity.this.mAngleL = Math.atan2((double) (-pan), (double) (-tilt));
            MainActivity.this.mTxtDataL.setText(String.format("( r%.0f, %.0f° )", Double.valueOf(Math.min(MainActivity.this.mRadiusL, 10.0d)), Double.valueOf((MainActivity.this.mAngleL * 180.0d) / 3.141592653589793d)));
            MainActivity.this.mCenterL = false;
        }

        @Override // com.MobileAnarchy.Android.Widgets.Joystick.JoystickMovedListener
        public void OnReleased() {
        }

        @Override // com.MobileAnarchy.Android.Widgets.Joystick.JoystickMovedListener
        public void OnReturnedToCenter() {
            MainActivity.this.mRadiusL = MainActivity.this.mAngleL = 0.0d;
            MainActivity.this.UpdateMethod();
            MainActivity.this.mCenterL = true;
        }
    };
    private JoystickMovedListener _listenerRight = new JoystickMovedListener() { // from class: org.lien.btjoystick.MainActivity.15
        @Override // com.MobileAnarchy.Android.Widgets.Joystick.JoystickMovedListener
        public void OnMoved(int pan, int tilt) {
            MainActivity.this.mRadiusR = Math.sqrt((double) ((pan * pan) + (tilt * tilt)));
            MainActivity.this.mAngleR = Math.atan2((double) (-pan), (double) (-tilt));
            MainActivity.this.mTxtDataR.setText(String.format("( r%.0f, %.0f° )", Double.valueOf(Math.min(MainActivity.this.mRadiusR, 10.0d)), Double.valueOf((MainActivity.this.mAngleR * 180.0d) / 3.141592653589793d)));
            MainActivity.this.mCenterR = false;
        }

        @Override // com.MobileAnarchy.Android.Widgets.Joystick.JoystickMovedListener
        public void OnReleased() {
        }

        @Override // com.MobileAnarchy.Android.Widgets.Joystick.JoystickMovedListener
        public void OnReturnedToCenter() {
            MainActivity.this.mRadiusR = MainActivity.this.mAngleR = 0.0d;
            MainActivity.this.UpdateMethod();
            MainActivity.this.mCenterR = true;
        }
    };
    private final Handler mHandler = new Handler() { // from class: org.lien.btjoystick.MainActivity.16
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    switch (msg.arg1) {
                        case 0:
                            MainActivity.this.mTxtStatus.setText(R.string.title_not_connected);
                            return;
                        case 1:
                        default:
                            return;
                        case 2:
                            MainActivity.this.mTxtStatus.setText(R.string.title_connecting);
                            return;
                        case 3:
                            MainActivity.this.mTxtStatus.setText(R.string.title_connected_to);
                            MainActivity.this.mTxtStatus.append(" " + MainActivity.this.mConnectedDeviceName);
                            return;
                    }
                case 2:
                case 3:
                default:
                    return;
                case 4:
                    MainActivity.this.mConnectedDeviceName = msg.getData().getString(MainActivity.DEVICE_NAME);
                    Toast.makeText(MainActivity.this.getApplicationContext(), "Connected to " + MainActivity.this.mConnectedDeviceName, 0).show();
                    return;
                case MainActivity.MESSAGE_TOAST /* 5 */:
                    Toast.makeText(MainActivity.this.getApplicationContext(), msg.getData().getString(MainActivity.TOAST), 0).show();
                    return;
            }
        }
    };

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.activity_main);
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (this.mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", 1).show();
            finish();
            return;
        }
        if (!this.mBluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 2);
        }
        this.mRfcommClient = new BluetoothRfcommClient(this, this.mHandler);
        this.mDualJoystick = (DualJoystickView) findViewById(R.id.dualjoystickView);
        this.mDualJoystick.setOnJostickMovedListener(this._listenerLeft, this._listenerRight);
        this.mTxtStatus = (TextView) findViewById(R.id.txt_status);
        this.mTxtDataL = (TextView) findViewById(R.id.txt_dataL);
        this.mTxtDataR = (TextView) findViewById(R.id.txt_dataR);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        this.mUpdatePeriod = Long.parseLong(prefs.getString("updates_interval", "200"));
        this.mMaxTimeoutCount = Integer.parseInt(prefs.getString("maxtimeout_count", "20"));
        this.mDataFormat = Integer.parseInt(prefs.getString("data_format", "5"));
        this.mStrA = prefs.getString("btnA_data", "A");
        this.mStrB = prefs.getString("btnB_data", "B");
        this.mStrC = prefs.getString("btnC_data", "C");
        this.mStrD = prefs.getString("btnD_data", "D");
        this.mButtonA = (Button) findViewById(R.id.button_A);
        this.mButtonA.setOnTouchListener(new View.OnTouchListener() { // from class: org.lien.btjoystick.MainActivity.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == 1) {
                    MainActivity.this.buttonsData &= -4097;
                    MainActivity.this.sendMessage5();
                    return false;
                } else if (event.getAction() != 0) {
                    return false;
                } else {
                    MainActivity.this.buttonsData |= 4096;
                    MainActivity.this.sendMessage5();
                    return false;
                }
            }
        });
        this.mButtonB = (Button) findViewById(R.id.button_B);
        this.mButtonB.setOnTouchListener(new View.OnTouchListener() { // from class: org.lien.btjoystick.MainActivity.2
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == 1) {
                    MainActivity.this.buttonsData &= -8193;
                    MainActivity.this.sendMessage5();
                    return false;
                } else if (event.getAction() != 0) {
                    return false;
                } else {
                    MainActivity.this.buttonsData |= 8192;
                    MainActivity.this.sendMessage5();
                    return false;
                }
            }
        });
        this.mButtonC = (Button) findViewById(R.id.button_C);
        this.mButtonC.setOnTouchListener(new View.OnTouchListener() { // from class: org.lien.btjoystick.MainActivity.3
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == 1) {
                    MainActivity.this.buttonsData &= -16385;
                    MainActivity.this.sendMessage5();
                    return false;
                } else if (event.getAction() != 0) {
                    return false;
                } else {
                    MainActivity.this.buttonsData |= 16384;
                    MainActivity.this.sendMessage5();
                    return false;
                }
            }
        });
        this.mButtonD = (Button) findViewById(R.id.button_D);
        this.mButtonD.setOnTouchListener(new View.OnTouchListener() { // from class: org.lien.btjoystick.MainActivity.4
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == 1) {
                    MainActivity.this.buttonsData &= -32769;
                    MainActivity.this.sendMessage5();
                    return false;
                } else if (event.getAction() != 0) {
                    return false;
                } else {
                    MainActivity.this.buttonsData |= 32768;
                    MainActivity.this.sendMessage5();
                    return false;
                }
            }
        });
        this.mButtonUP = (Button) findViewById(R.id.button_UP);
        this.mButtonUP.setOnTouchListener(new View.OnTouchListener() { // from class: org.lien.btjoystick.MainActivity.5
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == 1) {
                    MainActivity.this.buttonsData &= -17;
                    MainActivity.this.sendMessage5();
                    return false;
                } else if (event.getAction() != 0) {
                    return false;
                } else {
                    MainActivity.this.buttonsData |= 16;
                    MainActivity.this.sendMessage5();
                    return false;
                }
            }
        });
        this.mButtonDOWN = (Button) findViewById(R.id.button_DOWN);
        this.mButtonDOWN.setOnTouchListener(new View.OnTouchListener() { // from class: org.lien.btjoystick.MainActivity.6
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == 1) {
                    MainActivity.this.buttonsData &= -65;
                    MainActivity.this.sendMessage5();
                    return false;
                } else if (event.getAction() != 0) {
                    return false;
                } else {
                    MainActivity.this.buttonsData |= 64;
                    MainActivity.this.sendMessage5();
                    return false;
                }
            }
        });
        this.mButtonLEFT = (Button) findViewById(R.id.button_LEFT);
        this.mButtonLEFT.setOnTouchListener(new View.OnTouchListener() { // from class: org.lien.btjoystick.MainActivity.7
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == 1) {
                    MainActivity.this.buttonsData &= -129;
                    MainActivity.this.sendMessage5();
                    return false;
                } else if (event.getAction() != 0) {
                    return false;
                } else {
                    MainActivity.this.buttonsData |= MainActivity.BTB_PAD_LEFT;
                    MainActivity.this.sendMessage5();
                    return false;
                }
            }
        });
        this.mButtonRIGHT = (Button) findViewById(R.id.button_RIGHT);
        this.mButtonRIGHT.setOnTouchListener(new View.OnTouchListener() { // from class: org.lien.btjoystick.MainActivity.8
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == 1) {
                    MainActivity.this.buttonsData &= -33;
                    MainActivity.this.sendMessage5();
                    return false;
                } else if (event.getAction() != 0) {
                    return false;
                } else {
                    MainActivity.this.buttonsData |= 32;
                    MainActivity.this.sendMessage5();
                    return false;
                }
            }
        });
        this.mButtonSET = (Button) findViewById(R.id.button_SET);
        this.mButtonSET.setOnClickListener(new View.OnClickListener() { // from class: org.lien.btjoystick.MainActivity.9
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                MainActivity.this.showConnect();
            }
        });
        this.mUpdateTimer = new Timer();
        this.mUpdateTimer.schedule(new TimerTask() { // from class: org.lien.btjoystick.MainActivity.10
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                MainActivity.this.UpdateMethod();
            }
        }, 2000, this.mUpdatePeriod);
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        this.mItemConnect = menu.add("Connect");
        this.mItemAbout = menu.add("About");
        return super.onCreateOptionsMenu(menu);
    }

    /* access modifiers changed from: private */
    public void showConnect() {
        startActivityForResult(new Intent(this, DeviceListActivity.class), 1);
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item == this.mItemConnect) {
            startActivityForResult(new Intent(this, DeviceListActivity.class), 1);
        } else if (item == this.mItemOptions) {
            Toast.makeText(this, "此功能", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(this, OptionsActivity.class));
        } else if (item == this.mItemAbout) {
            AlertDialog about = new AlertDialog.Builder(this).create();
            about.setCancelable(false);
            about.setMessage("BTJoystick Controller\n' www.7gp.cn");
            about.setButton("OK", new DialogInterface.OnClickListener() { // from class: org.lien.btjoystick.MainActivity.11
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            about.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals("updates_interval")) {
            this.mUpdateTimer.cancel();
            this.mUpdateTimer.purge();
            this.mUpdatePeriod = Long.parseLong(prefs.getString("updates_interval", "200"));
            this.mUpdateTimer = new Timer();
            this.mUpdateTimer.schedule(new TimerTask() { // from class: org.lien.btjoystick.MainActivity.12
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    MainActivity.this.UpdateMethod();
                }
            }, this.mUpdatePeriod, this.mUpdatePeriod);
        } else if (key.equals("maxtimeout_count")) {
            this.mMaxTimeoutCount = Integer.parseInt(prefs.getString("maxtimeout_count", "20"));
        } else if (key.equals("data_format")) {
            this.mDataFormat = Integer.parseInt(prefs.getString("data_format", "5"));
        } else if (key.equals("btnA_data")) {
            this.mStrA = prefs.getString("btnA_data", "A");
        } else if (key.equals("btnB_data")) {
            this.mStrB = prefs.getString("btnB_data", "B");
        } else if (key.equals("btnC_data")) {
            this.mStrC = prefs.getString("btnC_data", "C");
        } else if (key.equals("btnD_data")) {
            this.mStrD = prefs.getString("btnD_data", "D");
        }
    }

    @Override // android.app.Activity
    public synchronized void onResume() {
        super.onResume();
        if (this.mRfcommClient != null && this.mRfcommClient.getState() == 0) {
            this.mRfcommClient.start();
        }
    }

    @Override // android.app.Activity
    public void onDestroy() {
        this.mUpdateTimer.cancel();
        if (this.mRfcommClient != null) {
            this.mRfcommClient.stop();
        }
        super.onDestroy();
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("Bluetooth Joystick V0.9").setMessage("Close this controller?").setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: org.lien.btjoystick.MainActivity.13
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.finish();
            }
        }).setNegativeButton("No", (DialogInterface.OnClickListener) null).show();
    }

    private void sendMessage(String message) {
        if (this.mRfcommClient.getState() == 3 && message.length() > 0) {
            this.mRfcommClient.write(message.getBytes());
        }
    }

    /* access modifiers changed from: private */
    public void sendMessage5() {
        byte[] data = new byte[12];
        data[0] = 36;
        data[1] = 77;
        data[2] = 61;
        data[3] = this.rX;
        data[4] = this.rY;
        data[5] = this.lX;
        data[6] = this.lY;
        data[7] = 0;
        data[8] = 0;
        data[9] = 0;
        data[10] = 0;
        data[11] = 42;
        data[7] = (byte) (this.buttonsData >> 24);
        data[8] = (byte) ((this.buttonsData >> 16) & 255);
        data[9] = (byte) ((this.buttonsData >> 8) & 255);
        data[10] = (byte) (this.buttonsData & 255);
        for (int i = 0; i < 11; i++) {
            System.out.print(((int) data[i]) + " ");
        }
        System.out.println();
        if (this.mRfcommClient.getState() == 3) {
            this.mRfcommClient.write(data);
        }
    }

    /* access modifiers changed from: private */
    public void UpdateMethod() {
        int leftPwm;
        int rightPwm;
        if (!this.mCenterL || !this.mCenterR || (this.mTimeoutCounter >= this.mMaxTimeoutCount && this.mMaxTimeoutCount > -1)) {
            byte radiusL = (byte) ((int) Math.min(this.mRadiusL, 10.0d));
            byte radiusR = (byte) ((int) Math.min(this.mRadiusR, 10.0d));
            byte angleL = (byte) ((int) (((this.mAngleL * 18.0d) / 3.141592653589793d) + 36.0d + 0.5d));
            byte angleR = (byte) ((int) (((this.mAngleR * 18.0d) / 3.141592653589793d) + 36.0d + 0.5d));
            if (angleL >= 36) {
                angleL = (byte) (angleL - 36);
            }
            if (angleR >= 36) {
                angleR = (byte) (angleR - 36);
            }
            this.lX = (byte) ((int) ((Math.sin(this.mAngleL) * ((double) radiusL) * -1.0d) + 10.0d));
            this.lY = (byte) ((int) ((Math.cos(this.mAngleL) * ((double) radiusL)) + 10.0d));
            this.rX = (byte) ((int) ((Math.sin(this.mAngleR) * ((double) radiusR) * -1.0d) + 10.0d));
            this.rY = (byte) ((int) ((Math.cos(this.mAngleR) * ((double) radiusR)) + 10.0d));
            Log.d(TAG, String.format("%d, %d, %d, %d", Byte.valueOf(radiusL), Byte.valueOf(angleL), Byte.valueOf(radiusR), Byte.valueOf(angleR)));
            Log.d(TAG, String.format("%d, %d, %d, %d", Integer.valueOf((int) (Math.sin(this.mAngleL) * ((double) radiusL))), Integer.valueOf((int) (Math.cos(this.mAngleL) * ((double) radiusL))), Byte.valueOf(this.rX), Byte.valueOf(this.rY)));
            if (this.mDataFormat == 4) {
                sendMessage(new String(new byte[]{radiusL, angleL, radiusR, angleR}));
            } else if (this.mDataFormat == 5) {
                sendMessage5();
            } else if (this.mDataFormat == 6) {
                sendMessage(new String(new byte[]{2, radiusL, angleL, radiusR, angleR, 3}));
            } else if (this.mDataFormat == 7) {
                int radius = (radiusL * 100) / 10;
                int angle = angleL * 10;
                double angleRadians = Math.toRadians((double) angle);
                if (angle <= 180) {
                    if (angle > 90) {
                        rightPwm = -radius;
                    } else {
                        rightPwm = radius;
                    }
                    leftPwm = (int) Math.round(Math.cos(angleRadians) * ((double) radius));
                } else {
                    if (angle < 270) {
                        leftPwm = -radius;
                    } else {
                        leftPwm = radius;
                    }
                    rightPwm = (int) Math.round(Math.cos(angleRadians) * ((double) radius));
                }
                sendMessage("$PWM=" + leftPwm + "," + rightPwm + "*");
            }
            this.mTimeoutCounter = 0;
        } else if (this.mMaxTimeoutCount > -1) {
            this.mTimeoutCounter++;
        }
    }

    @Override // android.app.Activity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == -1) {
                    this.mRfcommClient.connect(this.mBluetoothAdapter.getRemoteDevice(data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS)));
                    return;
                }
                return;
            case 2:
                if (resultCode != -1) {
                    Toast.makeText(this, (int) R.string.bt_not_enabled_leaving, 0).show();
                    finish();
                    return;
                }
                return;
            default:
                return;
        }
    }
}