import java.io.*;
import java.net.*;

public class MusicClient {
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 9876;

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(60000);

            byte[] startData = "ready".getBytes();
            InetAddress address = InetAddress.getByName(hostname);
            socket.send(new DatagramPacket(startData, startData.length, address, port));
            System.out.println("Messaggio 'READY' inviato alla console del server...");

            FileOutputStream outputStream = new FileOutputStream("canzone_ricevuta.mp3");
            byte[] buffer = new byte[1024];
            System.out.println("In attesa del file...");

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String check = new String(packet.getData(), 0, packet.getLength());
                if (check.equals("END")) break;

                outputStream.write(packet.getData(), 0, packet.getLength());
            }

            outputStream.close();
            System.out.println("Trasferimento terminato: file salvato correttamente.");

        } catch (IOException e) {
            System.err.println("Errore o Timeout: " + e.getMessage());
        }
    }
}
