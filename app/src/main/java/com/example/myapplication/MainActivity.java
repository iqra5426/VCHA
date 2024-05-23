package com.example.myapplication;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;

    private SpeechRecognizer speechRecognizer;
    private TextView tvCommand;

    // Bluetooth variables
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnVoiceInput = findViewById(R.id.btnVoiceInput);
        tvCommand = findViewById(R.id.tvCommand);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        // Initialize Bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show();
        }

        // Set click listener for the button to initiate voice recognition
        btnVoiceInput.setOnClickListener(v -> {
            // Start speech recognition
            startSpeechRecognition();
        });

        // Connect to the robot
        connectToRobot();
    }

    // Method to start speech recognition
    private void startSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now");
        startActivityForResult(intent, 1);
    }

    // Method to connect to the robot
    private void connectToRobot() {
        // Check if Bluetooth is enabled
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            // Get a set of paired devices
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Handle permission request
                return;
            }
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

            // Search for the robot's device among the paired devices
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("YourRobotName")) { // Replace "YourRobotName" with your actual robot's Bluetooth name
                    // Attempt to create a BluetoothSocket to connect to the robot
                    try {
                        bluetoothSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                        bluetoothSocket.connect();

                        // If connection is successful, open OutputStream to send data to the robot
                        outputStream = bluetoothSocket.getOutputStream();
                        return; // Exit the method as the connection is established
                    } catch (IOException e) {
                        // Error occurred while connecting or opening OutputStream
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to connect to the robot", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else {
            // Bluetooth is not enabled, request user to enable Bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    // Handle speech recognition result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String recognizedText = result.get(0);
            tvCommand.setText("Recognized Command: " + recognizedText);

            // Send the recognizedText to the robot via Bluetooth
            if (bluetoothAdapter.isEnabled() && bluetoothSocket != null) {
                try {
                    outputStream = bluetoothSocket.getOutputStream();
                    outputStream.write(recognizedText.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to send data via Bluetooth", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Bluetooth is not enabled or not connected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Handle permissions request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
            }
        }
    }
}