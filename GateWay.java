import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class GateWay {

    static DatagramSocket socket;

    public static void main(String[] args) {

        InetAddress ipAddress = null;
        ArrayList<String> monitorIdArray = new ArrayList<>();

        try {
            // Keep a socket open to listen to all the UDP trafic that is destined for this
            // port
            int BROADCAST_PORT = 8000;
            // Get the IP address of the running computer
            ipAddress = getIPAddressOfComputer();

            socket = new DatagramSocket(BROADCAST_PORT, ipAddress);
            socket.setBroadcast(true);

            while (true) {
                // Receive a packet
                byte[] recvBuf = new byte[15000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);

                Monitor monitor = (Monitor) deserialize(packet.getData());

                if (!monitorIdArray.contains(monitor.getMonitorID())) {

                    monitorIdArray.add(monitor.getMonitorID());
                    System.out.println("Discovery packet received from: " + packet.getAddress());

                    ClientHandler clientHandler = new ClientHandler(monitor);
                    clientHandler.start();

                    System.out.println(monitor.getMonitorID() + " is detected");

                }

            }
        } catch (Exception e) {
            System.out.println("Gateway creating exception");
        }

    }

    static public Object deserialize(byte[] blob) throws IOException,
            ClassNotFoundException {

        if (blob == null) {
            throw new IllegalArgumentException("null blob to deserialize");

        }

        ByteArrayInputStream bstream = new ByteArrayInputStream(blob);
        ObjectInputStream istream = new ObjectInputStream(bstream);

        return istream.readObject();
    }

    private static InetAddress getIPAddressOfComputer() {
        InetAddress ip_address = null;
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.connect(InetAddress.getByName("8.8.8.8"), 80); // Send request to name server
            ip_address = InetAddress.getByName(socket.getLocalAddress().getHostAddress());
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ip_address;
    }

}

class ClientHandler extends Thread {

    private final Monitor monitor;

    public static Socket socket;

    public ClientHandler(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void run() {

        try {
            socket = new Socket(monitor.getIp(), monitor.getPort());
            System.out.println("monitor = " + monitor.getMonitorID());

            // takes input from the client socket
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (true) {
                System.out.println(in.readLine());
            }

        } catch (IOException e) {

            e.printStackTrace();
            System.out.println(monitor.getMonitorID() + "is disconnected");
        }

    }

    static public Object deserialize(byte[] blob) throws IOException,
            ClassNotFoundException {

        if (blob == null) {
            throw new IllegalArgumentException("null blob to deserialize");

        }

        ByteArrayInputStream bstream = new ByteArrayInputStream(blob);
        ObjectInputStream istream = new ObjectInputStream(bstream);

        return istream.readObject();
    }

}
