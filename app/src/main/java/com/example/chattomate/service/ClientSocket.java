package com.example.chattomate.service;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class ClientSocket {
    Socket mSocket;

    {
        try {
            mSocket = IO.socket("http://localhost:8888");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


}
