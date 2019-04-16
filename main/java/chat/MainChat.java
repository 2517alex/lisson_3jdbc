package chat;

import chat.my_chat_window.LocalHistoryRecording;
import chat.my_chat_window.MyWindow;
/**
 * Курс 3
 * Доашнее задание н-6
 * запуск чата
 *
 * решение в классе MyServer
 *
 * @author Ложкин Александр
 * @version 1.0
 */
public class MainChat {
    public static void main(String[] args) {
        LocalHistoryRecording localHistoryRecording = new LocalHistoryRecording();
        try {
            new  MyWindow(localHistoryRecording).getT().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        localHistoryRecording.setLocalHistory();
    }
}
