package web.chat.client;

import web.chat.network.TCPConnect;
import web.chat.network.TCPConnetionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class WebWindowClient extends JFrame implements ActionListener, TCPConnetionListener {

    private static final String IP_ADRESS = "89.222.249.131";
    private static final int PORT = 8189;
    //Размеры окна.
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new WebWindowClient();
            }
        });

    }

    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickname = new JTextField("Vitaliy");
    private final JTextField fieldInput = new JTextField();

    private TCPConnect connection;

    //Конструктор
    private WebWindowClient() {
        //Операция закрытия на нажатие крестика.
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //Размер окна.
        setSize(WIDTH,HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);

        log.setEditable(false);
        log.setLineWrap(true);
        add(log, BorderLayout.CENTER);

        //Добавляю строку в которую будут печатать на южную часть окна.
        fieldInput.addActionListener(this);
        add(fieldInput, BorderLayout.SOUTH);

        //Добавляю ник на северную часть окна.
        add(fieldNickname, BorderLayout.NORTH);

        setVisible(true);
        try {
            connection = new TCPConnect(this, IP_ADRESS, PORT);
        } catch (IOException e) {
            printMessage("Connection Exception: " + e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) { //Обработка клавиши Enter.
        String msg = fieldInput.getText();
        if(msg.equals("")) return; // Если случайно нажали ентер на пустой строчке, ничего не будет передаваться.
        fieldInput.setText(null); // Если строчка есть, передаем.
        connection.SendMessage(fieldNickname.getText() + ": " + msg); //Соединение присылает сообщение (Ник: текст)

    }

    @Override
    public void onConnectionReady(TCPConnect tcpConnect) {//Если подключились.
        printMessage("Connection Ready...");
    }

    @Override
    public void onReseiveString(TCPConnect tcpConnect, String value) {
        printMessage(value);
    }

    @Override
    public void onDisconnect(TCPConnect tcpConnect) { //Если отключаемся.
        printMessage("Connection Close...");
    }

    @Override
    public void onException(TCPConnect tcpConnect, Exception e) { //Если искл.
        printMessage("Connection Exception: " + e);
    }

    private synchronized void printMessage(String msg){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // новая строчка после ввода сообщения.
                log.append(msg + "\n");
                //гарантирует новую строчку.
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
}
