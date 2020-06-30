package uit.com.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends Activity {

    private Client client;
    ImageButton inputButton;
    String ADDRESSIP;
    EditText portNum,addrConnect;
    int PORTNUMBER;
    VideoView streamView;
    MediaController mediaController;
    Switch PW;
    Thread clientThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ADDRESSIP  = "192.168.0.101";
        PORTNUMBER = 5533;


        streamView = findViewById(R.id.streamView);
        PW = findViewById(R.id.powerButton);
        inputButton = findViewById(R.id.buttonInput);

        showDialogInput();
        inputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogInput();
            }
        });




//        this.clientThread= new Thread(new ClientThread());
//        clientThread.start();


    }

    private void showDialogInput() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogView =inflater.inflate(R.layout.alert_dialog_view,null);
        builder.setView(dialogView)

                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                addrConnect=dialogView.findViewById(R.id.address_input);
                                portNum=dialogView.findViewById(R.id.port_num);
                                Toast.makeText(MainActivity.this, "this"+addrConnect.getText().toString(), Toast.LENGTH_LONG).show();
                                if(!addrConnect.getText().toString().isEmpty()&&!portNum.getText().toString().isEmpty()){
                                    if(isCheckedInput(addrConnect.getText().toString(),portNum.getText().toString())) {
                                        ADDRESSIP = addrConnect.getText().toString();
                                        PORTNUMBER = Integer.parseInt(portNum.getText().toString());
                                        Toast.makeText(MainActivity.this, "Enter successful!!", Toast.LENGTH_LONG).show();
                                    }else
                                    {
                                        Toast.makeText(MainActivity.this,"Please enter the IP and PORT!!",Toast.LENGTH_LONG).show();
                                    }
                                }
                                else
                                {
                                    Toast.makeText(MainActivity.this,"Please enter the IP and PORT!!",Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                )
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this,"Please enter the IP and PORT!!",Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

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
            Toast.makeText(this, ADDRESSIP, Toast.LENGTH_LONG).show();
            Toast.makeText(this, String.valueOf(PORTNUMBER), Toast.LENGTH_LONG).show();
            this.clientThread= new Thread(new ClientThread());
            clientThread.start();


            //playStream(addrConnect);

            //client.sendMessage("hello");


        }else
        {
            client.closeSocket();
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

    private boolean isCheckedInput(String scr, String port) {
        int count = 0, last_i = 0 ;  // count variable to count the value of point //
        for (int i = 0; i < scr.length() ; i++)
        {

            if(scr.charAt(i)=='.'&& i- last_i <=3 && i-last_i > 0) // if the distance of point (.) must >0 and < 4
            {
                count++;

                // check the num
                int num=0;
                for (int j =last_i ; j < i;j++)
                {
                    if('0'>scr.charAt(j)||scr.charAt(j)>'9') return false;
                    num = num*10+(scr.charAt(j)-'0');
                }
                if(num>255) return false; // must be < 255 //
                last_i =i+1;
            }

        }
        for (int i = 0; i < port.length() ; i++)
        {
            if('0'>port.charAt(i)||port.charAt(i)>'9') return false;

        }

        return (count==3);
    }



    class ClientThread implements Runnable {
        @Override
        public void run() {
            client = new Client();
            client.UNit(ADDRESSIP,PORTNUMBER);


        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        //client.closeSocket();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        streamView.stopPlayback();


    }
}

