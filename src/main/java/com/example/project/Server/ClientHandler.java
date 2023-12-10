package com.example.project.Server;

import com.example.project.GameLogic.Game;
import com.example.project.GameLogic.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private Player player;
    private Room room;

    public ClientHandler(Socket clientSocket,Room room){
        this.clientSocket = clientSocket;
        this.room = room;
        try{
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(),true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendMessage(String message){
        out.println(message);
    }
    @Override
    public void run() {
        try{
            sendMessage("Добро пожаловать в игру ! Цель игры: составить слова из заданного слова");
            sendMessage("Введи свое имя");
            String name = in.readLine();
            player = new Player(name);
            room.addClient(this);

            if (room.isReadyToStart()){
                room.startGame();
            }else{
                sendMessage("Ожидаем подключения твоего соперника...");
            }

            String guess;
            while ((guess = in.readLine()) != null) {
                room.handleClientInput(this,guess);
            }
        } catch (IOException e) {
            System.err.println("Соединение с клиентом разорвано: " + e.getMessage());
        }finally {
            try {

                clientSocket.close(); // Закрытие сокета при завершении игры или разрыве соединения
            } catch (IOException e) {
                System.err.println("Ошибка при закрытии сокета: " + e.getMessage());
            }
        }
    }

    public Player getPlayer() {
        return player;
    }
}
