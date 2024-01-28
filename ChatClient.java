import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) {
        final String serverHost = "localhost";
        final int serverPort = 9001;

        try (Socket socket = new Socket(serverHost, serverPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            // Menampilkan keterangan bahwa klien terhubung ke server
            System.out.println("Terhubung ke server.");

            // Meminta pengguna untuk memasukkan nama pengguna
            System.out.print("Masukkan nama Anda: ");
            String userName = scanner.nextLine();

            // Mengirim nama pengguna ke server
            out.println(userName);

            // Thread untuk membaca pesan dari server
            Thread readThread = new Thread(() -> {
                try {
                    String responseLine;
                    while ((responseLine = in.readLine()) != null) {
                        // Menampilkan pesan dari server ke pengguna klien
                        System.out.println(responseLine);
                    }
                } catch (IOException e) {
                    System.out.println("Error reading from server: " + e.getMessage());
                }
            });
            readThread.start();

            // Thread untuk menulis pesan dari klien ke server
            Thread writeThread = new Thread(() -> {
                try {
                    String userInput;
                    while (!(userInput = scanner.nextLine()).equalsIgnoreCase("exit")) {
                        // Mengirim pesan dari klien ke server
                        out.println(userInput);
                    }
                } catch (Exception e) {
                    System.out.println("Error writing to server: " + e.getMessage());
                }
            });
            writeThread.start();

            // Menunggu kedua thread selesai
            readThread.join();
            writeThread.join();
        } catch (IOException | InterruptedException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
        }
    }
}