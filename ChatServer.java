import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 9001;
    private static List<PrintWriter> clientWriters = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server sedang berjalan");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);
                Thread clientThread = new Thread(() -> handleClient(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    // Method untuk menangani koneksi dari client
    private static void handleClient(Socket clientSocket) {
        try {
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            clientWriters.add(writer);

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String userName = in.readLine(); // Membaca nama pengguna dari client

            broadcastMessage(userName + " has joined the chat"); // Mengirim pesan bergabung ke semua client

            // Thread untuk membaca pesan dari client
            Thread readThread = new Thread(() -> {
                try {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        System.out.println("Message from " + userName + ": " + inputLine); // Menampilkan pesan dari client
                        broadcastMessage(userName + ": " + inputLine); // Mengirim pesan dari client ke semua client
                    }
                } catch (IOException e) {
                    System.out.println("Error reading from client: " + e.getMessage());
                }
            });
            readThread.start(); // Memulai thread untuk membaca pesan dari client

        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        }
    }

    // Method untuk mengirim pesan ke semua client yang terhubung
    private static void broadcastMessage(String message) {
        for (PrintWriter writer : clientWriters) {
            writer.println(message); // Mengirim pesan ke klien
            writer.flush(); // Memastikan pesan terkirim segera
        }
    }

    // Method untuk mengirim pesan dari server ke semua client
    private static void sendServerMessage(String message) {
        broadcastMessage("[Server]: " + message); // Menambahkan tag [Server] pada pesan sebelum mengirimnya ke semua client
    }

    // Method untuk menerima input dari server console dan mengirimkannya ke semua client
    private static void serverConsoleInput() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            sendServerMessage(input); // Mengirim pesan dari server console ke semua client
        }
    }

    // Thread untuk menerima input dari server console
    static {
        Thread consoleInputThread = new Thread(ChatServer::serverConsoleInput);
        consoleInputThread.setDaemon(true); // Agar thread berhenti saat program utama berhenti
        consoleInputThread.start(); // Memulai thread untuk menerima input dari server console
    }
}