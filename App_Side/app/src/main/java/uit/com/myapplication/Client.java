package uit.com.myapplication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    private Socket socket;
    private DataInputStream input = null;
    private DataOutputStream out = null;

    // constructor to put ip address and port
    public void UNit() {
        // establish a connection
        try {
            Socket sock = new Socket("192.168.0.11", 5533);
            System.out.println("Connected");
            socket = sock;

            // takes input from terminal
            input = new DataInputStream(socket.getInputStream());

            // sends output to the socket
            out = new DataOutputStream(socket.getOutputStream());

        } catch (Exception u) {
            u.printStackTrace();
//                try {
//                    Thread.sleep(2000);//2 seconds
//                } catch (InterruptedException ie) {
//                    ie.printStackTrace();
//                }
            //System.out.println(u);
        }

    }

    public void sendMessage(String line) {
        try {

            byte[] b = line.getBytes();
            out.writeBytes(line);
            //out.write(b);
        } catch (Exception i) {
            i.printStackTrace();
        }

    }

    public void closeSocket() {
        // close the connection
        try {
            input.close();
            out.close();
            socket.close();
        } catch (Exception i) {
            i.printStackTrace();
        }
    }

    public String recievMessage() {
        //int length = input.readInt();
        String s = "";
        try {
            byte[] message = new byte[5];
            input.readFully(message, 0, message.length); // read the message
            s = new String(message);
        } catch (IOException e) {
            e.printStackTrace();
            s = "Error";
        }
        return s;

    }
}
