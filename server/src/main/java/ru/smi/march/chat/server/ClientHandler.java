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
    private String nickname;
    private UserRole role;

    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        new Thread(() -> {
            try {
                System.out.println("Подключился новый клиент");
                if (tryToAuthenticate()) {
                    communicate();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnect();
            }
        }).start();
    }

    public String getNickname() {
        return nickname;
    }

    private void communicate() throws IOException {
        while (true) {
            String msg = in.readUTF();
            if (msg.startsWith("/")) {
                if (msg.startsWith("/exit")) {
                    break;
                } else if (msg.startsWith("/w ")) {
                    sendPrivateMessage(msg);
                } else if (msg.startsWith("/kick ")) {
                    // /kick nickname
                    String[] tokens = msg.split(" ");
                    if (tokens.length != 2) {
                        sendMessage("Некорректный формат запроса");
                        continue;
                    }
                    if (role == UserRole.ADMIN) {
                        if (server.getClientByNickname(tokens[1]) == null) {
                            sendMessage("Клиент с таким никнеймом не найден");
                        } else {
                            ClientHandler kickedClient = server.getClientByNickname(tokens[1]);
                            kickedClient.disconnect();
                            sendMessage("Пользователь " + kickedClient.getNickname() + " был выкинут из чата");
                        }
                    } else {
                        sendMessage("Только пользователи с ролью admin могут тут кого-то кикать");
                    }
                }
                continue;
            } else if (msg.startsWith("/w ")) {
                sendPrivateMessage(msg);
            }
            server.broadcastMessage(nickname + ": " + msg);
        }
    }

    private boolean tryToAuthenticate() throws IOException {
        while (true) {
            String msg = in.readUTF();
            if (msg.startsWith("/auth ")) {
                // /auth login pass
                String[] tokens = msg.split(" ");
                if (tokens.length != 3) {
                    sendMessage("Некорректный формат запроса");
                    continue;
                }
                String login = tokens[1];
                String password = tokens[2];
                //запрос в базе данных юзера по логпасу
                String nickname = server.getJDBCService().getNicknameIfUserInBase(login, password);
                this.nickname = nickname;
                if (nickname == null) {
                    sendMessage("Неправильный логин/пароль");
                    continue;
                }
                if (server.isNicknameBusy(nickname)) {
                    sendMessage("Указанная учетная запись уже занята. Попробуйте зайти позднее");
                    continue;
                }
                server.subscribe(this);
                sendMessage(nickname + ", добро пожаловать в чат!");
                return true;
            } else if (msg.startsWith("/register ")) {
                // /register nickname login pass
                String[] tokens = msg.split(" ");
                if (tokens.length != 4) {
                    sendMessage("Некорректный формат запроса");
                    continue;
                }
                String nickname = tokens[1];
                String login = tokens[2];
                String password = tokens[3];

                if (server.getJDBCService().isLoginAlreadyExist(login)) {
                    sendMessage("Указанный логин уже занят");
                    continue;
                }
                if (server.getJDBCService().isNicknameAlreadyExist(nickname)) {
                    sendMessage("Указанный никнейм уже занят");
                    continue;
                }
                try {
                    this.role = server.addRole(nickname);
                    server.getJDBCService().addUserToBase(nickname, login, password, role);
                    this.nickname = nickname;
                    server.subscribe(this);
                    sendMessage("Вы успешно зарегистрировались! " + nickname + ", добро пожаловать в чат!");
                    return true;
                } catch (Exception e) {
                    sendMessage("Не удалось пройти регистрацию");
                    e.printStackTrace();
                }
            } else if (msg.equals("/exit")) {
                return false;
            } else {
                sendMessage("Вам необходимо авторизоваться");
            }
        }
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
        String msgForUser = "Сообщение от " + nickname + ": " + splitMsg[2];
        if (server.getClientByNickname(nickname) == null) {
            sendMessage("Клиент с никнеймом не найден");
        } else {
            server.getClientByNickname(nickname).sendMessage(msgForUser);
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