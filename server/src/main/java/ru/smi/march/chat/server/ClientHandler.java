package ru.smi.march.chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;

    private static int usersCounter = 0;

    public String getUsername() {
        return username;
    }

    private void generateUsername() {
        usersCounter++;
        this.username = "user" + usersCounter;
    }

    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.generateUsername();
        new Thread(() -> {
            try {
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                System.out.println("Подключился новый клиент");
                sendMessage("Ваш никнейм: " + username);
                while (true) {
                    String msg = in.readUTF();
                    if (msg.startsWith("/")) {
                        if (msg.startsWith("/exit")) {
                            System.out.println("Всего доброго");
                            break;
                        }
                        if (msg.startsWith("/w")) {
                            sendPrivateMessage(msg);
                        }
                        continue;
                    }
                    server.broadcastMessage(username + ": " + msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnect();
            }
        }).start();
    }

    public void sendMessage(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPrivateMessage(String message) {
        String[] splitMsg = message.split(" ", 3);
        String nickname = splitMsg[1];
        String msgForUser = "Сообщение от " + username + ": " + splitMsg[2];
        if (server.getUserByUsername(nickname) == null) {
            sendMessage("Клиент с никнеймом не найден");
        } else {
            server.getUserByUsername(nickname).sendMessage(msgForUser);
            sendMessage("Сообщение отправлено клиенту с никнеймом " + nickname);
        }
    }

    public void disconnect() {
        server.unsubscribe(this);
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}