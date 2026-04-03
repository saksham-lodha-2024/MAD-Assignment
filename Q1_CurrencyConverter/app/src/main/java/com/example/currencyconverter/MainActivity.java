package com.example.currencyconverter;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etAmount;//all variables so we can access then and us it in
    private Spinner spinnerFrom, spinnerTo;
    private Button btnConvert, btnSettings;
    private TextView tvResult;

    private String[] currencies = {"USD", "INR", "JPY", "EUR"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//app crashh hojayhega without his
        setContentView(R.layout.activity_main);
//xml se saari files lee aao
        etAmount = findViewById(R.id.etAmount);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        btnConvert = findViewById(R.id.btnConvert);
        btnSettings = findViewById(R.id.btnSettings);
        tvResult = findViewById(R.id.tvResult);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                currencies
        );
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);
        //shows by default as o is usd and 1 is inr so accordignly it shows
        spinnerFrom.setSelection(0);
        spinnerTo.setSelection(1);

        btnConvert.setOnClickListener(v -> {
            Toast.makeText(this, "Convert btn clicked!", Toast.LENGTH_SHORT).show();
        });

        btnSettings.setOnClickListener(v -> {
            Toast.makeText(this, "Settings coming soon!", Toast.LENGTH_SHORT).show();
        });
    }
}