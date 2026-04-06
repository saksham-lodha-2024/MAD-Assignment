package com.example.gallery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {


    static final int REQUEST_FOLDER = 200;
    static final int REQUEST_IMAGE = 100;
    Uri imageUri;
    String currentPath;

//    REQUEST_FOLDER = 200 → the code we'll use for folder picker
//    REQUEST_IMAGE = 100 → the code we'll use for camera

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = findViewById(R.id.openFolderBtn);

        btn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            Uri rootUri = Uri.parse("content://com.android.externalstorage.documents/document/primary%3A");
            intent.putExtra(android.provider.DocumentsContract.EXTRA_INITIAL_URI, rootUri);
            startActivityForResult(intent, REQUEST_FOLDER);
        });
    }
    public void takePhoto(View view) throws IOException {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 100);
            return;
        }

        try {
            File file = createImageFile();

            imageUri = FileProvider.getUriForFile(this,
                    getPackageName() + ".provider", file);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, REQUEST_IMAGE);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
        }
    }
    private File createImageFile() throws IOException {

        String name = "IMG_" + System.currentTimeMillis();

        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM
        );

        File image = File.createTempFile(name, ".jpg", storageDir);

        currentPath = image.getAbsolutePath();

        return image;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {

            Toast.makeText(this, "Image Saved", Toast.LENGTH_SHORT).show();

        } else if (requestCode == REQUEST_FOLDER && resultCode == RESULT_OK && data != null) {

            Uri treeUri = data.getData();

            if (treeUri != null) {
                try {
                    int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

                    getContentResolver().takePersistableUriPermission(treeUri, takeFlags);

                    Intent intent = new Intent(this, GalleryActivity.class);
                    intent.putExtra("folderUri", treeUri.toString());
                    startActivity(intent);

                } catch (SecurityException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Permission denied by system", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

//flow
//package + imports
//class declaration + variables
//onCreate sets up folder button
//takePhoto handles camera button
//createImageFile() creates empty file for photo
//onActivityResult()  handles returns from camera/folder picker