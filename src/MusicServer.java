import java.io.*;
import java.net.*;
import java.util.Scanner;

public class MusicServer {

    public static void main(String[] args) {
        int port = 9876;
        Scanner input = new Scanner(System.in);
        String filePath = ""; // Percorso canzone

        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("--- MusicServer UDP Attivo ---");
            System.out.println("In ascolto di segnale READY sulla porta " + port + "...");

            byte[] receiveBuffer = new byte[1024];

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);

                String clientMsg = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                if (clientMsg.equalsIgnoreCase("READY")) {
                    System.out.println("Canzone richiesta da: " + clientAddress + ":" + clientPort);
                    System.out.println("Inserisci il percorso assoluto del file .mp3 da inviare: ");
                    filePath = input.nextLine();
                    File musicFile = new File(filePath);

                    if (musicFile.exists()) {
                        System.out.println("File trovato. Inizio invio automatico...");
                        inviaCanzone(socket, musicFile, clientAddress, clientPort);
                    } else {
                        System.err.println("Errore: Il file al percorso " + filePath + " non esiste.");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void inviaCanzone(DatagramSocket socket, File file, InetAddress address, int port) {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            int pacchettiInviati = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                DatagramPacket sendPacket = new DatagramPacket(buffer, bytesRead, address, port);
                socket.send(sendPacket);
                pacchettiInviati++;

                Thread.sleep(2); // pausa per non sovraccaricare il server
            }

            byte[] endMsg = "END".getBytes();
            socket.send(new DatagramPacket(endMsg, endMsg.length, address, port));
            System.out.println("Invio completato. Totale pacchetti: " + pacchettiInviati);

        } catch (Exception e) {
            System.err.println("Errore durante l'invio: " + e.getMessage());
        }
    }
}
