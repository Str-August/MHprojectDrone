package uit.com.myapplication;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.MediaController;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends Activity {

    private Client client;

    String addrConnect;
    int portNum;
    VideoView streamView;
    MediaController mediaController;
    Switch PW;
    Thread clientThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addrConnect = "192.168.0.101";
        portNum=5533;


        streamView = findViewById(R.id.streamView);
        PW = findViewById(R.id.powerButton);
        this.clientThread= new Thread(new ClientThread());
        clientThread.start();





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

    public void switchClick(View view) {
        if(PW.isChecked()) {
            //playStream(addrConnect);
            client.sendMessage("hello");

        }else
        {
            Toast.makeText(MainActivity.this, "Turn off", Toast.LENGTH_LONG).show();
        }
//        PW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//                if (isChecked) {
//                    Toast.makeText(MainActivity.this, "Turn on", Toast.LENGTH_LONG).show();
//                    //playStream(addrConnect);
//
//                    client.sendMessage("hello");
//
//
//
//
//
//                } else {
//                    Toast.makeText(MainActivity.this, "Turn off", Toast.LENGTH_LONG).show();
//                    //client.closeSocket();
//                }
//
//            }


//        });

    }

    public void up1Click(View view) {
    }

    public void right1Click(View view) {
    }

    public void bottom1Click(View view) {
    }

    public void left1Click(View view) {
    }

    public void right2Click(View view) {
    }

    public void bottom2Click(View view) {
    }

    public void left2Click(View view) {
    }

    public void up2Click(View view) {
    }

    class ClientThread implements Runnable {
        @Override
        public void run() {
            client = new Client();
            client.UNit(addrConnect,portNum);


        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        client.closeSocket();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        streamView.stopPlayback();


    }
}

