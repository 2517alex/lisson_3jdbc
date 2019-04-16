package chat.my_chat_window;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

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

public class LocalHistoryRecording {

    private Queue<String> history;
    private String nameFile;

    public LocalHistoryRecording() {
        this.history = new LinkedList<>();
    }

    public void setHistory(String his) {
        if(his != null & !his.equals("")){
            if (history.size() == 100){
                history.poll();
            }
            history.add(his);
        }
    }

    public StringBuffer getHistory() {
        String line;
        StringBuffer strB = new StringBuffer();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader((nameFile + ".txt")))) {
            for (int i = 0; (line = bufferedReader.readLine()) != null & i < 100; i++) {
                strB.append(line + "\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strB;
    }

    public void setLocalHistory() {
        if (history.peek() != null){
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter((nameFile + ".txt"), true))) {
                String str;
                while ((str = history.poll()) != null){
                    bufferedWriter.write(str);
                    bufferedWriter.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }
}
