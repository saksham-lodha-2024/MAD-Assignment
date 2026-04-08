package com.example.gallery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_FOLDER = 200;
    static final int REQUEST_IMAGE = 100;

    Uri imageUri;
    String currentPhotoPath;
    Uri selectedFolderUri;

    TextView tvSelectedFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvSelectedFolder = findViewById(R.id.tvSelectedFolder);
        Button btnOpenFolder = findViewById(R.id.openFolderBtn);

        // Load previously saved folder URI from SharedPreferences
        // So if app restarts, user doesn't have to pick folder again
        SharedPreferences prefs = getSharedPreferences("gallery_prefs", MODE_PRIVATE);
        String savedUri = prefs.getString("folder_uri", null);
        if (savedUri != null) {
            selectedFolderUri = Uri.parse(savedUri);
            // Show folder name in UI
            updateFolderText();
        }

        btnOpenFolder.setOnClickListener(v -> {
            // Open Android's built-in folder picker
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            Uri rootUri = Uri.parse(
                    "content://com.android.externalstorage.documents/document/primary%3A"
            );
            intent.putExtra(android.provider.DocumentsContract.EXTRA_INITIAL_URI, rootUri);
            startActivityForResult(intent, REQUEST_FOLDER);
        });
    }

    //  TAKE PHOTO

    // Called automatically when Take Photo button is tapped
    // android:onClick="takePhoto" in XML links to this method
    public void takePhoto(View view) {

        // Check camera permission first
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 100);
            return;
        }

        // Must pick a folder before taking photo
        if (selectedFolderUri == null) {
            Toast.makeText(this,
                    "Please choose a folder first using Open Folder!",
                    Toast.LENGTH_LONG).show();
            return;
        }

        try {
            // Step 1: Create temp file in app's private Pictures folder
            // FileProvider needs a real file path — SAF URIs don't work with it
            File tempFile = createTempImageFile();

            // Step 2: Wrap temp file as content:// URI for camera
            imageUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider",
                    tempFile
            );

            // Step 3: Launch camera — tell it to save full photo to our URI
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, REQUEST_IMAGE);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
        }
    }

    // Creates temp .jpg file in app's private Pictures folder
    // This is needed because FileProvider can't work with SAF URIs directly
    private File createTempImageFile() throws IOException {
        String name = "IMG_" + System.currentTimeMillis();

        // getExternalFilesDir = app's own private folder
        // matches external-files-path in file_paths.xml
        // no storage permission needed to write here
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(name, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //  COPY PHOTO TO CHOSEN FOLDER

    private void copyPhotoToChosenFolder() {
        try {
            // Get the chosen folder as DocumentFile
            DocumentFile folder = DocumentFile.fromTreeUri(this, selectedFolderUri);

            if (folder == null || !folder.isDirectory()) {
                Toast.makeText(this,
                        "Folder not accessible. Please pick again.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Create new .jpg file inside chosen folder
            String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
            DocumentFile newFile = folder.createFile("image/jpeg", fileName);

            if (newFile == null) {
                Toast.makeText(this,
                        "Could not create file in chosen folder",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Open output stream to chosen folder file
            OutputStream outputStream =
                    getContentResolver().openOutputStream(newFile.getUri());

            // Open input stream from temp file
            FileInputStream inputStream = new FileInputStream(currentPhotoPath);

            // Copy bytes from temp file chosen folder file
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            // Delete temp file — no longer needed
            new File(currentPhotoPath).delete();

            Toast.makeText(this,
                    "Photo saved to chosen folder!",
                    Toast.LENGTH_SHORT).show();

            // Open GalleryActivity to show the chosen folder
            // User will immediately see their new photo in the grid
            Intent intent = new Intent(this, GalleryActivity.class);
            intent.putExtra("folderUri", selectedFolderUri.toString());
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving photo: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    //  ACTIVITY RESULTS

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            // Photo was taken — copy from temp file to chosen folder
            copyPhotoToChosenFolder();

        } else if (requestCode == REQUEST_FOLDER
                && resultCode == RESULT_OK
                && data != null) {

            Uri treeUri = data.getData();

            if (treeUri != null) {
                try {
                    // Make permission permanent — survives app restarts
                    int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
                    getContentResolver().takePersistableUriPermission(treeUri, takeFlags);

                    // Save chosen folder URI — used for both saving photos and viewing
                    selectedFolderUri = treeUri;

                    // Store in SharedPreferences so it persists after app restart
                    SharedPreferences prefs =
                            getSharedPreferences("gallery_prefs", MODE_PRIVATE);
                    prefs.edit().putString("folder_uri", treeUri.toString()).apply();

                    // Update folder name shown in UI
                    updateFolderText();

                    // Open GalleryActivity to view images in this folder
                    Intent intent = new Intent(this, GalleryActivity.class);
                    intent.putExtra("folderUri", treeUri.toString());
                    startActivity(intent);

                } catch (SecurityException e) {
                    e.printStackTrace();
                    Toast.makeText(this,
                            "Permission denied by system",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //  HELPERS

    // Updates the TextView showing which folder is currently selected
    private void updateFolderText() {
        if (selectedFolderUri != null) {
            DocumentFile folder = DocumentFile.fromTreeUri(this, selectedFolderUri);
            if (folder != null && folder.getName() != null) {
                tvSelectedFolder.setText("Save folder: " + folder.getName());
            } else {
                tvSelectedFolder.setText("Folder selected");
            }
        }
    }
}