package com.example.currencyconverter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

// HashMap = dictionary jaisi cheez, key-value pairs store karta hai
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private EditText etAmount;
    private Spinner spinnerFrom, spinnerTo;
    private Button btnConvert, btnSettings;
    private TextView tvResult;

    private String[] currencies = {"USD", "INR", "JPY", "EUR"};

    // HashMap = exchange rates store karne ke liye
    private HashMap<String, Double> rates = new HashMap<>();

    // onCreate = sabse pehle ye chalta hai jab screen khulti hai
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("dark_mode", false);
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        etAmount = findViewById(R.id.etAmount);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        btnConvert = findViewById(R.id.btnConvert);
        btnSettings = findViewById(R.id.btnSettings);
        tvResult = findViewById(R.id.tvResult);

        rates.put("USD", 1.0);    // 1 dollar mein kitne USD? 1 (khud hi)
        rates.put("INR", 83.0);   // 1 dollar mein kitne INR? 83
        rates.put("JPY", 155.0);  // 1 dollar mein kitne JPY? 155
        rates.put("EUR", 0.93);   // 1 dollar mein kitne EUR? 0.93

        // ArrayAdapter = data (array) ko dropdown items mein convert karta hai
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                currencies
        );

        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);
        spinnerFrom.setSelection(0); // default USD
        spinnerTo.setSelection(1);   // default INR

        // jab Convert click ho tab convertCurrency method call hoga
        btnConvert.setOnClickListener(v -> {
            convertCurrency();
        });

        // Settings button click = SettingsActivity kholo
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    // ye method actual conversion karta hai
    private void convertCurrency() {

        // Step 1: user ne kya type kiya wo lo
        // getText() = Editable object deta hai, toString() = String mein convert
        String input = etAmount.getText().toString();

        // Step 2: check kro ki kuch type kiya ya nahi
        if (input.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        // Step 3: String ko number mein convert kro
        double amount;
        try {
            amount = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Step 4: spinner se selected currency lo
        // getSelectedItem() = jo abhi dropdown mein dikha raha hai wo do
        String from = spinnerFrom.getSelectedItem().toString();
        String to = spinnerTo.getSelectedItem().toString();

        // Step 5: HashMap se rates lo
        // rates.get("INR") = 83.0 return karega
        double fromRate = rates.get(from);
        double toRate = rates.get(to);

        // Step 6: math kro x curr to usd then usd to to: waali currecncy mai jaao
        double result = (amount / fromRate) * toRate;

        // Step 7: result
        String output = String.format("%.2f %s = %.2f %s", amount, from, result, to);
        tvResult.setText(output);
    }
}