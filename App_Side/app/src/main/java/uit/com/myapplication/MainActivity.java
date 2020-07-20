package uit.com.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Switch;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    private Client client;
    static private boolean showNotice = false;
    static private boolean isReady = false;
    String ADDRESSIP;
    EditText portNum,addrConnect,portVideo;
    int PORTNUMBER,PORTVIDEO;
    ImageView streamView;
    MediaController mediaController;
    Switch PW,cutSwitch,cutSwitchP;
    Thread clientThread = null,streamThread = null;
    JoystickView joystickRight;
    Joystick joystickLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ADDRESSIP  = "192.168.0.15";
        PORTNUMBER = 5533;
        PORTVIDEO =5555;

        PW = findViewById(R.id.powerButton);
        cutSwitch = findViewById(R.id.motorCutSwitch);
        cutSwitchP = findViewById(R.id.motorCutSwitchP);
        joystickLeft = findViewById(R.id.joystick1);
        joystickRight = findViewById(R.id.joystick2);
        streamView =findViewById(R.id.streamView);
        showDialogInput();
    }

    private void showDialogInput() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogView =inflater.inflate(R.layout.alert_dialog_view,null);
        addrConnect=dialogView.findViewById(R.id.address_input);
        portNum=dialogView.findViewById(R.id.port_num);
        portVideo =dialogView.findViewById(R.id.port_video);
        addrConnect.setText("192.168.12.1");
        portNum.setText("5533");
        portVideo.setText("5555");
        builder.setView(dialogView)
                .setCancelable(false)

                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if(!addrConnect.getText().toString().isEmpty()&&!portNum.getText().toString().isEmpty()){
                                    if(isCheckedInput(addrConnect.getText().toString(),portNum.getText().toString())) {
                                        ADDRESSIP = addrConnect.getText().toString();
                                        PORTNUMBER = Integer.parseInt(portNum.getText().toString());
//                                        clientThread = new Thread(new ClientThread());
//                                        clientThread.start();
//                                        while(!isReady);
//                                        if(!showNotice) {
//                                            isReady = false;
//                                            showDialogInput();
//                                        }

                                        //Toast.makeText(MainActivity.this, "Enter successful!!", Toast.LENGTH_LONG).show();
                                    }else
                                    {
                                        Toast.makeText(MainActivity.this,"Please enter the IP and PORT!!",Toast.LENGTH_LONG).show();
                                        showDialogInput();
                                    }
                                }
                                else
                                {
                                    Toast.makeText(MainActivity.this,"Please enter the IP and PORT!!",Toast.LENGTH_LONG).show();
                                    showDialogInput();
                                }
                            }
                        }
                )
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Toast.makeText(MainActivity.this,"Please enter the IP and PORT!!",Toast.LENGTH_SHORT).show();
                        finish();
                        System.exit(0);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();


    }



    private void playStream(final String src) {
        final Timer timer =new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                LoadImage loadImage = new LoadImage(streamView);
                loadImage.execute(src);
                //your method
                if(!PW.isChecked()) timer.cancel();
            }
        }, 0, 100);//put here time 1000 milliseconds=1 second

//        Uri UriSrc = Uri.parse(src);
//        if (UriSrc == null) {
//            Toast.makeText(MainActivity.this, "Please enter the Uri.", Toast.LENGTH_LONG).show();
//        } else {
//            streamView.setBackgroundResource(0);
//            //streamView.setVideoPath(src);
//            //streamView.setVideoURI(UriSrc);
//            mediaController = new MediaController(this);
//            streamView.setMediaController(mediaController);
//            streamView.start();
//
//            Toast.makeText(this, "Connect" + src, Toast.LENGTH_LONG).show();
//        }
    }

    public void switchClick(View view) {
        switchClickAction();
    }

    private void switchClickAction() {
        if(PW.isChecked()) {
            //client.sendMessage(instructionSend("power","on"));
            //sendSpeedData();
            cutSwitch.setChecked(false);
            //controlMotorCut();
            playStream("http://"+ADDRESSIP+":"+PORTVIDEO+"/html/cam_pic.php");
        }else
        {
            //client.sendMessage(instructionSend("power","off"));
            Toast.makeText(MainActivity.this, "Turn off", Toast.LENGTH_LONG).show();
        }
    }

    private void sendSpeedData() {
        joystickLeft.setOnMoveListener(new Joystick.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                if(angle<0) angle = 1500-5*Math.abs(angle); else  angle = 1500+5*Math.abs(angle);
                strength = 1000+strength/100;
                client.sendMessage(instructionSend("JoyR","yaw",Integer.toString(angle)));
                client.sendMessage(instructionSend("JoyR","throttle",Integer.toString(strength)));

            }
        });
        joystickRight.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int pitch, int roll) {
                if(pitch<0) pitch = 1500-5*Math.abs(pitch); else  pitch = 1500+5*Math.abs(pitch);
                if(roll>0) roll = 1500-5*Math.abs(roll); else  roll = 1500+5*Math.abs(roll);
                //System.out.println("Test:"+ pitch + " : "+ roll);
                client.sendMessage(instructionSend("JoyR","pitch",Integer.toString(pitch)));
                client.sendMessage(instructionSend("JoyR","roll",Integer.toString(roll)));
            }
        });
    }

    private String instructionSend(String device, String valueName, String value)
    {
        return (device.length()+"/"+device+valueName.length()+"/"+valueName+value.length()+"/"+value);
    }
    private String instructionSend(String device, String valueName)
    {
        return (device.length()+"/"+device+valueName.length()+"/"+valueName);
    }

    private void controlMotorCut() {
        cutSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    client.sendMessage(instructionSend("motor","cw"));
                }
                else
                {
                    client.sendMessage(instructionSend("motor","ccw"));
                }
            }
        });
        cutSwitchP.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    client.sendMessage((instructionSend("motor","on")));
                } else
                {
                    client.sendMessage(instructionSend("motor","off"));
                }
            }
        });
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
        public ClientThread() {
            client = new Client(ADDRESSIP,PORTNUMBER);
        }

        @Override
        public void run() {

            if(client.UNit()) {
                showNotice = true;
                System.out.println("Test");
            }
            else showNotice = false;
            isReady = true;
        }


    }
    public Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            System.out.println(input.toString());
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }




    @Override
    protected void onRestart() {
        super.onRestart();
        switchClickAction();
//        if(PW.isChecked()) {
//            this.clientThread = new Thread(new ClientThread());
//            clientThread.start();
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //switchClickAction();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        client.sendMessage(instructionSend("power","off"));
//        try {
//            client.closeSocket();
//        }catch (Exception u)
//        {
//            System.out.println(u);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //streamView.stopPlayback();


    }

    private class LoadImage extends AsyncTask<String,Void,Bitmap>{
        ImageView imageView;
        public LoadImage(ImageView streamView) {
            this.imageView= streamView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String urlLink= strings[0];
            Bitmap bitmap = null;
            try{
                InputStream inputStream = new java.net.URL(urlLink).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            streamView.setImageBitmap(bitmap);
        }

    }
}

