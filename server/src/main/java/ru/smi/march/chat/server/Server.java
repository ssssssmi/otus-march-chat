package ru.smi.march.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static ru.smi.march.chat.server.JDBCUserService.roles;

public class Server {

    private int port;
    private List<ClientHandler> clients;
    private AuthenticationService authenticationService;
    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            this.authenticationService = new InMemoryAuthenticationService();
            JDBCUserService.userMapper();
            JDBCUserService.roleMapper();
            System.out.println("Сервис аутентификации запущен: " + authenticationService.getClass().getSimpleName());
            System.out.printf("Сервер запущен на порту: %d, ожидаем подключения клиентов\n", port);

            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    new ClientHandler(this, socket);
                } catch (Exception e) {
                    System.out.println("Возникла ошибка при обработке подключившегося клиента");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        broadcastMessage("К чату присоединился " + clientHandler.getNickname());
        clients.add(clientHandler);
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastMessage("Из чата вышел " + clientHandler.getNickname());
    }

    public synchronized int addRole(String nickname) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Регистрируется новый клиент " + nickname + ", какую роль ему присвоить? (введите id роли)");
        System.out.println(roles);
        while (true) {
            int roleID = scanner.nextInt();
            for (Role r : roles) {
                if (r.getId() == roleID) {
                    return roleID;
                }
            }
            System.out.println("Такой роли нет, только admin или user");
        }
    }

    public void broadcastMessage(String message) {
        for (ClientHandler c : clients) {
            c.sendMessage(message);
        }
    }

    public synchronized boolean isNicknameBusy(String nickname) {
        for (ClientHandler c : clients) {
            if (c.getNickname().equals(nickname)) {
                return true;
            }
        }
        return false;
    }

    public synchronized ClientHandler getClientByNickname(String nickname) {
        for (ClientHandler c : clients) {
            if (c.getNickname().equals(nickname)) {
                return c;
            }
        }
        return null;
    }
}