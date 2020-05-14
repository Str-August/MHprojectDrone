package uit.com.myapplication;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.MediaController;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    private Client client;

    String addrConnect;
    VideoView streamView;
    MediaController mediaController;
    Switch PW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addrConnect = "192.168.0.1";


        streamView = findViewById(R.id.streamView);
        PW = findViewById(R.id.powerButton);
        PW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    //Toast.makeText(MainActivity.this, "Turn on", Toast.LENGTH_LONG).show();
                    //playStream(addrConnect);
                    new Thread(new ClientThread()).start();



                } else {
                    Toast.makeText(MainActivity.this, "Turn off", Toast.LENGTH_LONG).show();
                }

            }


        });


    }

    private void playStream(String src) {
        Uri UriSrc = Uri.parse(src);
        if (UriSrc == null) {
            Toast.makeText(MainActivity.this, "Please enter the Uri.", Toast.LENGTH_LONG).show();
        } else {
            streamView.setVideoURI(UriSrc);
            mediaController = new MediaController(this);
            streamView.setMediaController(mediaController);
            streamView.start();

            Toast.makeText(this, "Connect" + src, Toast.LENGTH_LONG).show();
        }
    }

    class ClientThread implements Runnable {
        @Override
        public void run() {
            client = new Client();
            client.UNit();

            client.sendMessage("Hello000"); // only send 5 characters (hardcode here!! at server)
            client.closeSocket();

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        streamView.stopPlayback();

    }
}

