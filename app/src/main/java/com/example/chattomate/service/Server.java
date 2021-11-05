package com.example.chattomate.service;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {

        ServerSocket ss = new ServerSocket(3333);
        Socket s = ss.accept();/// Khởi tạo Socket và chấp nhận kết nối từ đối tượng Socket Server.
        DataInputStream din = new DataInputStream(s.getInputStream());/// Tạo luồng đọc dữ liệu vào từ client
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());// Tạo luồng in dữ liệu ra

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));// Tạo bộ đệm đọc đọc dữ liệu
        String str = "", str2 = "";
        while (!str.equals("stop")) {
            str = din.readUTF();
            System.out.println("client says: " + str);
            str2 = br.readLine();
            dout.writeUTF(str2);
            dout.flush();
        }
        din.close();
        s.close();
        ss.close();
    }

}

