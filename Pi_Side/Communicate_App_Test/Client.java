import java.net.*;
import java.io.*;
import java.lang.*;
// import java.net.ConnectException;
import java.util.Scanner;
import java.util.Arrays;

public class Client {
    // initialize socket and input output streams
    private Socket socket            = null;
    private DataInputStream  input   = null;
    private DataOutputStream out     = null;

    // constructor to put ip address and port
    public Client(String address, int port) {
        // establish a connection
        boolean scanning = true;
        while (scanning) {
            try {
                socket = new Socket(address, port);
                System.out.println("Connected");
                scanning = false;

                // takes input from terminal
                input  = new DataInputStream(socket.getInputStream());

                // sends output to the socket
                out    = new DataOutputStream(socket.getOutputStream());
            } catch (UnknownHostException u) {
                System.out.println("Connect failed, waiting and trying again");
                try {
                    Thread.sleep(2000);//2 seconds
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
                //System.out.println(u);
            } catch (IOException i) {
                System.out.println(i);
            }
        }
    }
    public void sendMessage(String line) {

        // string to read message from input
        try {
            //Scanner scanner = new Scanner(System.in);
            //line = scanner.nextLine();
            //out.writeUTF(line);
            byte[] b = line.getBytes();
            //out.writeInt(b.length);
            out.write(b);
        } catch (IOException i) {
            System.out.println(i);
        }

    }
    public void closeSocket() {
        // close the connection
        try {
            input.close();
            out.close();
            socket.close();
        } catch (IOException i) {
            System.out.println(i);
        }
    }

    public String recievMessage() {
        //int length = input.readInt();
        String s="";
        try 
        {
            byte[] message = new byte[5];
            input.readFully(message, 0, message.length); // read the message
            s = new String(message);
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
            s="Error";
        }
        return s;   

    }

    public static void main(String args[]) {
        Client client = new Client("127.0.0.1", 5533); // adress and port to listen server
        client.sendMessage("Hello"); // only send 5 characters (hardcode here!! at server)
        client.closeSocket();
    }
}
// Sender:
// byte[] message = ...
// Socket socket = ...
// DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());

// dOut.writeInt(message.length); // write length of the message
// dOut.write(message);           // write the message


// Reciever:
// Socket socket = ...
// DataInputStream dIn = new DataInputStream(socket.getInputStream());

// int length = dIn.readInt();                    // read length of incoming message
// if(length>0) {
//     byte[] message = new byte[length];
//     dIn.readFully(message, 0, message.length); // read the message
// }

// final class MyResult {
//     private final int first;
//     private final String second;

//     public MyResult(int first, int second) {
//         this.first = first;
//         this.second = second;
//     }

//     public int getNotification() {
//         return first;
//     }
//     // public void setResult(int temp_i , String temp _s )
//     // {
//     //     this.first = temp_i;
//     //     this.second = temp_s;
//     // }

//     public String getString() {
//         return second;
//     }
// }
