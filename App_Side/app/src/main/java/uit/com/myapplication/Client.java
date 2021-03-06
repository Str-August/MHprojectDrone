package uit.com.myapplication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    private Socket socket;
    private DataInputStream input = null;
    private DataOutputStream out = null;
    private String addressC;
    private int portNum;

    public Client(String addressC, int portNum) {
        this.addressC = addressC;
        this.portNum = portNum;
    }

    // constructor to put ip address and port
    public boolean UNit() {
        // establish a connection
        try {
            Socket sock = new Socket(addressC,portNum);
            System.out.println("Connected");
            socket = sock;

            // takes input from terminal
            input = new DataInputStream(socket.getInputStream());

            // sends output to the socket
            out = new DataOutputStream(socket.getOutputStream());
            return true;

        } catch (Exception u) {
            u.printStackTrace();
            return false;
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
            out.writeBytes("!"+line);
            //byte[] b = line.getBytes();
            //out.writeBytes(line);
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

    public Byte recievMessage() {
        //int length = input.readInt();
        Byte data;
        try {
            data = input.readByte();

        } catch (IOException e) {
            e.printStackTrace();
            data = '!';
        }
        return data;


    }


    public Socket getSocket() {
        return socket;
    }
}
