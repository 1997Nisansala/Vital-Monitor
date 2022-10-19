import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Gate {
    static DatagramSocket socket;

    public static void main(String[] args)throws Exception {
        try {
            System.out.println("Waiting for clients requests");
            ServerSocket ss = new ServerSocket(7000);           //create a socket
            Socket soc = ss.accept(); //blocking call, wait for a request
            System.out.println("Connection is established");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
