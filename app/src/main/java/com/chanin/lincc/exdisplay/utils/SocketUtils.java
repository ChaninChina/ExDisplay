package com.chanin.lincc.exdisplay.utils;

import java.io.IOException;
import java.net.Socket;

public class SocketUtils {


    public static boolean isConnected(Socket socket) {
        return socket != null && socket.isConnected() && !socket.isClosed() && !socket.isInputShutdown() && !socket.isOutputShutdown();
    }

    public static boolean isInputConnected(Socket socket) {
        return socket != null && socket.isConnected() && !socket.isClosed() && !socket.isInputShutdown();
    }


    public static boolean isOutputConnected(Socket socket) {
        return socket != null && socket.isConnected() && !socket.isClosed() && !socket.isOutputShutdown();
    }

    public static void closeSocket(Socket socket) {
        if(socket!=null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
