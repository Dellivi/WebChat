package web.chat.network;

import java.io.*;
import java.net.Socket;

public class TCPConnect {

    private final Socket socket;
    private final Thread rxThread;
    private final TCPConnetionListener eventListener;
    private final BufferedReader in;
    private final BufferedWriter out;

    public TCPConnect(TCPConnetionListener eventListener, String ipAddr, int port) throws IOException{
        this(eventListener, new Socket(ipAddr,port));
    }

    public TCPConnect(TCPConnetionListener eventListener,Socket socket) throws IOException {
        this.eventListener = eventListener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    eventListener.onConnectionReady(TCPConnect.this);
                    while(!rxThread.isInterrupted()){
                        eventListener.onReseiveString(TCPConnect.this, in.readLine());
                    }
                }catch (IOException e){
                    eventListener.onException(TCPConnect.this, e);
                }finally {
                    eventListener.onDisconnect(TCPConnect.this);
                }
            }
        });
        rxThread.start();
    }

    public synchronized void SendMessage(String value){
        try{
            out.write(value + "\r\n");
            out.flush();
        }catch (IOException e){
            eventListener.onException(TCPConnect.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect(){
        rxThread.interrupt();
        try{
            socket.close();
        }catch (IOException e){
            eventListener.onException(TCPConnect.this, e);
        }
    }

    @Override
    public String toString(){
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
