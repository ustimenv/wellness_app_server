import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client
{
    final static int ServerPort = 1234;
    private final String clientName;
    private Socket socket;

    public Client(String name)
    {
        this.clientName = name;
        // getting localhost ip
        InetAddress ip = null;
        try {
            ip = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        // establish the connection
        try {
            if(null != ip)
            {
                socket = new Socket(ip, ServerPort);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void SendMessage(String message) throws IOException
    {
        // obtaining input and out streams
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        outputStream.writeUTF(clientName+"#"+message);
    }
    public void DoChat() throws IOException
    {
        Scanner inputScanner = new Scanner(System.in);


        // obtaining input and out streams
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

        // sendMessage thread
        Thread sendMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {
                while (true) {

                    // read the message to deliver.
                    String msg = inputScanner.nextLine();

                    try {
                        // write on the output stream
                        outputStream.writeUTF(clientName+"#"+msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // readMessage thread
        Thread readMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {

                while (true) {
                    try {
                        // read the message sent to this client
                        String msg = inputStream.readUTF();
                        System.out.println(msg);
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }
        });

        sendMessage.start();
        readMessage.start();

    }

    public static void main(String args[])
    {
        Client client1 = new Client(args[0]);
        try {
            client1.SendMessage(" #login");
            client1.DoChat();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}