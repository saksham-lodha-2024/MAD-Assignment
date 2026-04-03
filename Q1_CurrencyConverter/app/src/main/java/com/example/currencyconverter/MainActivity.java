package com.example.currencyconverter;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// HashMap is a data structure that stores key-value pairs
// like a dictionary: "USD" -> 1.0, "INR" -> 83.0
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private EditText etAmount;
    private Spinner spinnerFrom, spinnerTo;
    private Button btnConvert, btnSettings;
    private TextView tvResult;

    private String[] currencies = {"USD", "INR", "JPY", "EUR"};

    // HashMap to store exchange rates
    private HashMap<String, Double> rates = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // connect XML views to Java variables
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

        // fill both spinners with currency names
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                currencies
        );
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);
        spinnerFrom.setSelection(0); // USD
        spinnerTo.setSelection(1);   // INR

        // method call hoga ab yaha se click hone par
        btnConvert.setOnClickListener(v -> {
            convertCurrency();
        });

        btnSettings.setOnClickListener(v -> {
            Toast.makeText(this, "Settings coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    // the method  that do actual conversion
    private void convertCurrency() {

        // Step 1: get the text user typed
        // getText() returns an Editable object, toString() converts it to String
        String input = etAmount.getText().toString();

        // Step 2: check if user actually typed something
        // isEmpty() returns true if string has nothing in it
        if (input.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return; // stop here, don't continue
        }

        // Step 3: convert String to a number (double)
        // "100" is a String, 100.0 is a number — we need the number for math
        double amount;
        try {
            amount = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            // if input is not a valid number like "abc"
            Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Step 4: get selected currencies from spinners
        // getSelectedItem() returns whatever item is currently shown in the spinner
        String from = spinnerFrom.getSelectedItem().toString();
        String to = spinnerTo.getSelectedItem().toString();

        // Step 5: get the exchange rates from our HashMap
        // rates.get("USD") returns 1.0, rates.get("INR") returns 83.0
        double fromRate = rates.get(from);
        double toRate = rates.get(to);

        // Step 6: do the math and covert to usd frist then the requried to: thing
        double result = (amount / fromRate) * toRate;

        // Step 7: show the result
        // formatted th reuslts like 8300.0 becomes "8300.00"
        String output = String.format("%.2f %s = %.2f %s", amount, from, result, to);
        tvResult.setText(output);
    }
}