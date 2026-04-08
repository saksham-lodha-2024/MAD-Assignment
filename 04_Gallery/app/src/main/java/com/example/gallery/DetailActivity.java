package com.example.gallery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.documentfile.provider.DocumentFile;

public class DetailActivity extends AppCompatActivity {
    ImageView imageView;
    TextView detailsText;
    Button deleteBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        imageView = findViewById(R.id.imageView);
        detailsText = findViewById(R.id.detailsText);
        deleteBtn = findViewById(R.id.deleteBtn);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        Uri uri = getIntent().getData();

        try {
            getContentResolver().takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION |
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageView.setImageURI(uri);

        DocumentFile documentFile = DocumentFile.fromSingleUri(this, uri);
        String detailsToDisplay = "Details not available";

        if (documentFile != null && documentFile.exists()) {
            String name = documentFile.getName();
            long sizeBytes = documentFile.length();
            String sizeString;
            if (sizeBytes > 1024 * 1024) {
                // Show in MB if bigger than 1MB
                sizeString = String.format(
                        java.util.Locale.getDefault(),
                        "%.2f MB",
                        sizeBytes / (1024.0 * 1024.0)
                );
            } else {sizeString = String.format(
                    java.util.Locale.getDefault(),
                    "%.2f KB",
                    sizeBytes / 1024.0
            );
            }
            long lastModified = documentFile.lastModified();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                    "MMM dd, yyyy • hh:mm a",
                    java.util.Locale.getDefault()
            );
            String dateString = sdf.format(new java.util.Date(lastModified));
            detailsToDisplay =
                            "Name: " + name + "\n" +
                            "Path: " + uri.toString() + "\n" +
                            "Size: " + sizeString + "\n" +
                            "Date: " + dateString;
        }
        detailsText.setText(detailsToDisplay);

        deleteBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setMessage("Delete this image?")
                    .setPositiveButton("Yes", (d, w) -> {

                        DocumentFile fileToDelete =
                                DocumentFile.fromSingleUri(this, uri);

                        if (fileToDelete != null && fileToDelete.delete()) {
                            Toast.makeText(this,
                                    "Deleted",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this,
                                    "Delete failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }
}
