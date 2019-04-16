package chat.my_chat_window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Курс 3
 * Доашнее задание н-6
 * создание графического интерфейса, отправка и получение сообщений от сервера
 *
 * решение в классе MyServer
 *
 * @author Ложкин Александр
 * @version 1.0
 */

public class MyWindow extends JFrame implements Runnable {

    private Thread t;

    private LocalHistoryRecording localHistoryRecording;

    private final String SERVER_ADDR = "localhost";
    private final int SERVER_PORT = 8189;

    private JTextField loginField;
    private JTextField passField;

    private JTextField textField;
    private JTextArea textArea;

    private Socket socket;

    private DataInputStream in;
    private DataOutputStream out;

    private String myNick;

    public MyWindow(LocalHistoryRecording localHistoryRecording) {

        this.localHistoryRecording = localHistoryRecording;

        t = new Thread(this);

        setBounds(600, 300, 500, 500);
        setTitle("Client");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel authPanel = new JPanel(new GridLayout());

        loginField = new JTextField();
        passField = new JTextField();

        loginField.setToolTipText("Enter Login");
        passField.setToolTipText("Enter Password");

        JButton jbAuth = new JButton("Auth me");

        authPanel.add(loginField);
        authPanel.add(passField);
        authPanel.add(jbAuth);

        add(authPanel, BorderLayout.NORTH);

        jbAuth.addActionListener(e -> {
            onAuthClick();
        });

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);

        JScrollPane jsp = new JScrollPane(textArea);
        add(jsp, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        add(bottomPanel, BorderLayout.SOUTH);

        JButton jbSend = new JButton("SEND");
        bottomPanel.add(jbSend, BorderLayout.EAST);

        textField = new JTextField();
        bottomPanel.add(textField, BorderLayout.CENTER);

        onAuthClick();

        jbSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!textField.getText().trim().isEmpty()) {
                    sendMsg();
                    textField.grabFocus();
                }
            }
        });
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMsg();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    out.writeUTF("/end");
                    out.flush();
                    socket.close();
                    out.close();
                    in.close();
                } catch (IOException exc) {
                }
            }
        });
        setVisible(true);
    }


    public void onAuthClick() {
        if (socket == null || socket.isClosed()) {
            t.start();
        } else {
            try {
                out.writeUTF("/auth " + loginField.getText() + " " + passField.getText());
                loginField.setText("");
                passField.setText("");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMsg() {
        try {
            out.writeUTF(textField.getText());
            textField.setText("");
        } catch (IOException e) {
            System.out.println("Ошибка отправки сообщения");
        }
    }

    @Override
    public void run() {
        try {
            socket = new Socket(SERVER_ADDR, SERVER_PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            try {
                while (true) {
                    String str = in.readUTF();
                    if (str.startsWith("/authok")) {

                        myNick = str.split("\\s")[1];
                        localHistoryRecording.setNameFile(myNick);
                        if(localHistoryRecording.getHistory() != null){
                            textArea.append(String.valueOf(localHistoryRecording.getHistory()));
                        }
                        break;
                    }
                    textArea.append(str + "\n");
                }
                while (true) {
                    String str = in.readUTF();
                    localHistoryRecording.setHistory(str);
                    if (str.equals("/end")) {
                        break;
                    }
                    textArea.append(str + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                try {

                    socket.close();
                    myNick = "";
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            textArea.append("Не удалось подключиться к серверу" + "\n");
            e.printStackTrace();
        }
    }

    public Thread getT() {
        return t;
    }
}
