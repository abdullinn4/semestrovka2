package com.example.project.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private ServerSocket serverSocket;
    private List<Room> rooms;

    public Server(){
        rooms = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(8888);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start(){
        System.out.println("Сервер запущен");
        while(true){
            try{
                Socket client = serverSocket.accept();
                System.out.println("Подключился пользователь");

                Room room = getOrCreateRoom();
                ClientHandler clientHandler = new ClientHandler(client,room);
                Thread thread = new Thread(clientHandler);
                thread.start();
                System.out.println(rooms.size());

            } catch (IOException e) {
                System.err.println("Ошибка при подключении клиента: " + e.getMessage());
            }
        }
    }
    private Room getOrCreateRoom(){
        for (Room r: rooms){
            if (r.isJoinable()){
                return r;
            }
        }
        Room room = new Room();
        rooms.add(room);
        return room;
    }
    public void stop(){
        try{
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

}
