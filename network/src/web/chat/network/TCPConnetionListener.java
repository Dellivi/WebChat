package web.chat.network;

public interface TCPConnetionListener {

    void onConnectionReady(TCPConnect tcpConnect);
    void onReseiveString(TCPConnect tcpConnect, String value);
    void onDisconnect(TCPConnect tcpConnect);
    void onException(TCPConnect tcpConnect, Exception e);
}
