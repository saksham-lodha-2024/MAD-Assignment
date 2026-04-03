package com.example.mediaplayer;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ImageButton btnPlay, btnPause, btnStop;
    private Button btnPickAudio;
    private TextView tvAudioName;
    private MediaPlayer mediaPlayer;
    private Uri audioUri;

    // video ke liye variables
    private EditText etVideoUrl;
    private Button btnPlayVideo;
    private VideoView videoView;

    // file picker  ye phone ka file browser kholega
    private ActivityResultLauncher<String> audioPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPickAudio = findViewById(R.id.btnPickAudio);
        tvAudioName = findViewById(R.id.tvAudioName);
        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnStop = findViewById(R.id.btnStop);
        etVideoUrl = findViewById(R.id.etVideoUrl);
        btnPlayVideo = findViewById(R.id.btnPlayVideo);
        videoView = findViewById(R.id.videoView);

        // jab user file choose karega, ye code chalega
        audioPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    // uri is selected file ka address
                    if (uri != null) {
                        audioUri = uri;
                        tvAudioName.setText("File selected");

                        // agar pehle se koi audio chal rahi thi, band karo
                        if (mediaPlayer != null) {
                            mediaPlayer.release();
                        }

                        // naya MediaPlayer banao selected file ke liye
                        mediaPlayer = MediaPlayer.create(this, audioUri);
                    }
                }
        );

        // pick button file browser kholega
        btnPickAudio.setOnClickListener(v -> {
            audioPickerLauncher.launch("audio/*");
        });

        // play button  audio chalao
        btnPlay.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.start();
                Toast.makeText(this, "Playing", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Pick a file first!", Toast.LENGTH_SHORT).show();
            }
        });

        // pause button audio roko
        btnPause.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                Toast.makeText(this, "Paused", Toast.LENGTH_SHORT).show();
            }
        });

// stop button sab kuch reset karo nd file bhi hatao
        btnStop.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            //  reset
            audioUri = null;
            tvAudioName.setText("No file selected");
            Toast.makeText(this, "Stopped", Toast.LENGTH_SHORT).show();
        });

        // MediaController = built-in play/pause/seekbar for video
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // play video button - URL se video stream karo
        btnPlayVideo.setOnClickListener(v -> {
            String url = etVideoUrl.getText().toString().trim();
            if (url.isEmpty()) {
                Toast.makeText(this, "Enter a URL first!", Toast.LENGTH_SHORT).show();
                return;
            }
            Uri videoUri = Uri.parse(url);
            videoView.setVideoURI(videoUri);
            videoView.start();
        });
    }

    // jab user app band kare, MediaPlayer release karo
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}