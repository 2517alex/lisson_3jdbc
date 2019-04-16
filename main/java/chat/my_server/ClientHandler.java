package chat.my_server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

/**
 * Курс 3
 * Доашнее задание н-6
 * авторизация, обмен сообщениями между клиентами и сервером
 *
 * решение в классе MyServer
 *
 * @author Ложкин Александр
 * @version 1.0
 */

public class ClientHandler {
    private MyServer myServer;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String name;

    public String getName() {
        return name;
    }

    public ClientHandler(MyServer myServer, Socket socket) {
        try {
            this.myServer = myServer;
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.name = "";
                try {
                    while (true) { // цикл вторизации
                        String str = in.readUTF();
                        if (str.startsWith("/auth")) {
                            String[] parts = str.split("\\s");
                            String nick = null;
                            try {
                                nick = myServer.getAuthService().getNickByLoginPass(parts[1], parts[2]);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            if (nick != null) {
                                if (!myServer.isNickBusy(nick)) {
                                    sendMsg("/authok " + nick);
                                    name = nick;
                                    myServer.broadcastMsg(name + " зашел в чат");
                                    myServer.subscribe(this);
                                    break;
                                } else sendMsg("Учетная запись уже используется");
                            } else {
                                sendMsg("Неверные логин/пароль");
                            }
                        }
                    }
                    while (true) { // цикл получения сообщений
                        String str = in.readUTF();
                        if (str.startsWith("/n")) { //cмена ника
                            String[] parts = str.split("\\s");
                            String newNick = null;
                            try {
                                newNick = myServer.getAuthService().getNick(parts[1], name);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            myServer.broadcastMsg("Пользователь сником - " + name + ", сменил ник на: " + newNick);
                            myServer.getLOGGER().info("Пользователь сником - " + name + ", сменил ник на: " + newNick);
                            name = newNick;
                            continue;
                        }
                        myServer.getLOGGER().info("от " + name + ": " + str);
                        if (str.equals("/end")) break;
                        myServer.broadcastMsg(name + ": " + str);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    myServer.unsubscribe(this);
                    myServer.broadcastMsg(name + " вышел из чата");
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        } catch (IOException e) {
            myServer.getLOGGER().info("Проблемы при создании обработчика клиента");
        }
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}