package com.example.stm32_viewer;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

import com.hoho.android.usbserial.driver.FtdiSerialDriver;
import com.hoho.android.usbserial.driver.ProbeTable;
import com.hoho.android.usbserial.driver.UsbSerialDriver;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.stm32_viewer.databinding.ActivityMainBinding;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    // Debug view
    private TextView logTextView;
    private Button refreshButton;

    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";

    protected void LOG(String text) {

        // Log to normal console
        System.out.println(text);

        // Log to on screen console
        logTextView.append(text + "\n");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Init debug view
        logTextView = findViewById(R.id.logTextView);
        logTextView.setMovementMethod(new ScrollingMovementMethod());

        // Init USB
        init();

        //for(int i = 0; i < 40; i++) {
        //    LOG("test row #" + i);
        //    try {
        //        Thread.sleep(1000);
        //    } catch (InterruptedException e) {
        //        throw new RuntimeException(e);
        //    }
        //}
        //new Thread(new SleepLoopRunnable()).start();
        refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Append text to the console TextView
                appendToConsole("Refreshing USB connection!");
                InitUsbSerialPort();
            }
        });
    }



    void init() {
        if(InitUsbSerialPort()) {
            //System.out.println("Serial port initialized!");
            LOG("Serial port initialized!");

        }
        else {
            //System.out.println("Failed to initialize serial port!");
            LOG("Failed to initialize serial port!");
        }
    }

    protected boolean InitUsbSerialPort() {
        // Find all available drivers from attached devices.
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (availableDrivers.isEmpty()) {
            LOG("No suitable USB drivers found! Try custom drivers");
            // Try to probe with custom drivers
            ProbeTable customTable = new ProbeTable();
            customTable.addProduct(0x1234, 0x0001, FtdiSerialDriver.class);
            customTable.addProduct(0x1234, 0x0002, FtdiSerialDriver.class);
            UsbSerialProber prober = new UsbSerialProber(customTable);
            availableDrivers = prober.findAllDrivers(manager);

            //System.out.println("No serial ports found!");
            //Log.i("TAG", "No serial ports found!");
            if (availableDrivers.isEmpty()) {
                LOG("No suitable USB drivers found!");
                return false;
            }
        }
        LOG(availableDrivers.size() + " Available USB drivers found");


        // Open a connection to the first available driver.
        UsbSerialDriver driver = availableDrivers.get(0);
        LOG("Found USB device with name: " + driver.getDevice().getDeviceName());

        UsbDeviceConnection connection = manager.openDevice(driver.getDevice());

        if (connection == null) {
            // add UsbManager.requestPermission(driver.getDevice(), ..) handling here
            //System.out.println("Failed to open connection");
            LOG("Failed to get USB connection");

            if (!manager.hasPermission(driver.getDevice())) {
                LOG("Requesting permissions for USB..");
                //Sleep(5000);
                PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);

                
                manager.requestPermission(driver.getDevice(), permissionIntent);
                //Sleep(5000);
            }

            if (!manager.hasPermission(driver.getDevice())) {
                LOG("Failed to get USB connection with permissions");
                return false;
            }
        }
        LOG("USB connection established!");

        if (driver.getPorts().isEmpty()) {
            //System.out.println("No serial ports found!");
            LOG("No serial ports found!");
            return false;
        }
        LOG("Found " + driver.getPorts().size() + " USB serial ports!");

        UsbSerialPort port = driver.getPorts().get(0); // Most devices have just one port (port 0)
        try {
            port.open(connection);
            port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            LOG("USB port opened!");
            // Create a SerialInputOutputManager to handle reading from and writing to the serial port
            SerialInputOutputManager serialIoManager = new SerialInputOutputManager(port, new SerialInputOutputManager.Listener() {
                @Override
                public void onNewData(byte[] data) {
                    // Handle new data received from the serial port
                    String receivedData = null; // Assuming UTF-8 encoding
                    try {
                        receivedData = new String(data, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                    //System.out.println("Received data: " + receivedData);
                    LOG("Received data: " + receivedData);
                }

                @Override
                public void onRunError(Exception e) {
                    // Handle errors that occur during data reading
                    //System.out.println("Error reading from serial port: " + e.getMessage());
                    LOG("Error reading from serial port: " + e.getMessage());
                    e.printStackTrace();
                }
            });

            serialIoManager.start();
            LOG("Serial manager started!");

        } catch (IOException e) {
            //System.out.println("Error opening serial port: " + e.getMessage());
            LOG("Error opening serial port: " + e.getMessage());
            //throw new RuntimeException(e);
            return false;
        }

        return true;
    }

    private void appendToConsole(String text) {
        LOG(text);
    }

    private class SleepLoopRunnable implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 40; i++) {
                String message = "test row #" + i;
                //LOG(message);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logTextView.append(message + "\n");
                    }
                });

            }
        }
    }

    private void Sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

