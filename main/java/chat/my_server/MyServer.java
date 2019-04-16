package chat.my_server;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Курс 3
 * Доашнее задание н-6
 * реализация сервера
 *
 * 2. Добавить на серверную сторону сетевого чата логирование событий
 * (сервер запущен, произошла ошибка, клиент подключился, клиент прислал сообщение/команду).
 *
 * @author Ложкин Александр
 * @version 1.0
 */
public class MyServer {

    //Создание логера
    private static final Logger LOGGER = LogManager.getLogger(MyServer.class);

    ExecutorService executorService;

    private ServerSocket server;
    private Vector<ClientHandler> clients;
    private BaseAuthService authService;

    public BaseAuthService getAuthService() {
        return authService;
    }

    private MyServer get() {
        return this;
    }

    private final int PORT = 8189;
    public MyServer() {
        try {
            server = new ServerSocket(PORT);
            Socket socket;
            authService = new BaseAuthService();
            clients = new Vector<>();
            while (true) {

                //вывести сообщение вконсоль
                LOGGER.info("Сервер ожидает подключения");
                socket = server.accept();
                //вывести сообщение вконсоль
                LOGGER.info("Клиент подключился");
                executorService = Executors.newCachedThreadPool();
                Socket finalSocket = socket;

                //управление потоками через ExecutorService
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        new ClientHandler(get(), finalSocket);
                    }
                });
            }
        } catch (IOException e) {
            //вывести сообщение об ошибки вконсоль
            LOGGER.error("Ошибка при работе сервера");
        } finally {
            try {
                executorService.shutdown();
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public synchronized boolean isNickBusy(String nick) {
        for (ClientHandler o : clients) {
            if (o.getName().equals(nick)) return true;
        }
        return false;
    }
    public synchronized void broadcastMsg(String msg) {
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }
    }
    public synchronized void unsubscribe(ClientHandler o) {
        clients.remove(o);
    }
    public synchronized void subscribe(ClientHandler o) {
        clients.add(o);
    }
    public static Logger getLOGGER() {
        return LOGGER;
    }
}


