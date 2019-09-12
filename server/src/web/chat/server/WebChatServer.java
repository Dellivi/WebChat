package web.chat.server;

import web.chat.network.TCPConnect;
import web.chat.network.TCPConnetionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class WebChatServer implements TCPConnetionListener {

    public static void main(String[] args){
        new WebChatServer();

    }

    private final List<TCPConnect> connections = new ArrayList<>();

    private WebChatServer(){
        System.out.println("Server running...");
        try(ServerSocket serverSocket = new ServerSocket(8189)){
            while(true){
                try{
                    new TCPConnect(this, serverSocket.accept());
                }catch (IOException e){
                    System.out.println("TCPConnection exception: " + e);
                }
            }

        }catch (IOException e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public synchronized void onConnectionReady(TCPConnect tcpConnect) {
        connections.add(tcpConnect);
    }

    @Override
    public synchronized void onReseiveString(TCPConnect tcpConnect, String value) {
        sendToAllConnections(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnect tcpConnect) {
        connections.remove(tcpConnect);
        sendToAllConnections("Client disconnect: " + tcpConnect);
    }

    @Override
    public synchronized void onException(TCPConnect tcpConnect, Exception e) {
        System.out.println("TCPConnection: " + e);
    }

    public void sendToAllConnections(String value){
        System.out.println(value);
        for(int i = 0; i < connections.size(); i++) connections.get(i).SendMessage(value);
    }
}
