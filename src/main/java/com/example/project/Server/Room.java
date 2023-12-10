package com.example.project.Server;

import com.example.project.GameLogic.Game;
import com.example.project.GameLogic.Player;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class Room {
    private List<ClientHandler> clients;
    private Game game;
    private Timer timer;
    private AtomicInteger minutes;

    public Room(){
        this.clients = new ArrayList<>();
        this.game = new Game();
    }
    public void addClient(ClientHandler client){
        clients.add(client);
    }
    public boolean isReadyToStart(){
        if (clients.size() == 2){
            return true;
        }
        return false;
    }
    public void startTimer(){
        timer = new Timer();
        minutes = new AtomicInteger(15);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int remMin = minutes.getAndDecrement();
                if (remMin > 0){
                    for (ClientHandler client: clients){
                        client.sendMessage("Осталось " + remMin + " минут");
                    }
                }else{
                    timer.cancel();
                    endGame();
                }
            }
        },0,60000);
    }

    private String getOppName(ClientHandler client){
        for (ClientHandler c: clients){
            if (c != client)
            return c.getPlayer().getName();
        }
        return null;
    }
    public void startGame(){
        for (ClientHandler client: clients){
            client.sendMessage("Игра началась!");
            client.sendMessage("Ваш уровень: " + client.getPlayer().getCurrentLevel());
            client.sendMessage("Твой соперник: " + getOppName(client));
            client.sendMessage("Слово для отгадывания: " + game.getWordByLevel(client.getPlayer().getCurrentLevel()));
            client.sendMessage("Кол-во отгаданных слов: " + client.getPlayer().getGuessedWordCount() + "/" + getNecessaryCountOfWordsLevel(client));
        }
        startTimer();
    }
    public void handleClientInput(ClientHandler client,String guess){
        if (game.checkAnswer(client.getPlayer().getCurrentLevel(),guess)){
            client.sendMessage("Правильно!");
            client.getPlayer().incrementGuessedWordsCount();
            client.sendMessage("Слово для отгадывания: " + game.getWordByLevel(client.getPlayer().getCurrentLevel()));
            client.sendMessage("Кол-во отгаданных слов: " + client.getPlayer().getGuessedWordCount() + "/" + getNecessaryCountOfWordsLevel(client));
            for (ClientHandler c: clients){
                if (c != client){
                    c.sendMessage(" ");
                    if (c.getPlayer().getCurrentLevel() == client.getPlayer().getCurrentLevel()){
                        c.sendMessage(client.getPlayer().getName() + " отгадал слово: " + guess);
                    }

                }
            }
            if (isRequiredCountOfWords(client)){
                moveToNextLevel(client);
                if (getNowLevel(client) == 11){
                    endGame();
                }
            }
        }else{
            if(game.getGuessedWords(client.getPlayer().getCurrentLevel(),guess)){
                client.sendMessage("Это слово уже отгадано");
            }else {
                client.sendMessage(" ");
                client.sendMessage("Неправильно.Попробуйте снова");
                client.sendMessage("Слово для отгадывания: " + game.getWordByLevel(client.getPlayer().getCurrentLevel()));
                client.sendMessage("Кол-во отгаданных слов: " + client.getPlayer().getGuessedWordCount() + "/" + getNecessaryCountOfWordsLevel(client));
            }
        }
    }
    public boolean isRequiredCountOfWords(ClientHandler client){
        int necessaryWords = getNecessaryCountOfWordsLevel(client);
        if (client.getPlayer().getGuessedWordCount() >= necessaryWords){
            return true;
        }
        return false;
    }
    public void moveToNextLevel(ClientHandler client){
        client.sendMessage(" ");
        client.sendMessage("Вы переходите на следующий уровень");
        client.getPlayer().setCurrentLevel(client.getPlayer().getCurrentLevel() + 1);
        client.sendMessage("Ваш уровень: " + client.getPlayer().getCurrentLevel());
        client.sendMessage("Слово для отгадывания: " + game.getWordByLevel(client.getPlayer().getCurrentLevel()));
        client.sendMessage("Кол-во отгаданных слов: " + client.getPlayer().getGuessedWordCount() + "/" + getNecessaryCountOfWordsLevel(client));
        client.sendMessage("Кол-во очков: " + client.getPlayer().getPoints());
    }

    public void endGame(){
        String answer;
        Player p1 = clients.get(0).getPlayer();
        Player p2 = clients.get(1).getPlayer();
        if (p1.getPoints() > p2.getPoints()){
            answer = "Выиграл: " + p1.getName() + ", он набрал " + p1.getPoints() + " очков";
        }else if (p1.getPoints() < p2.getPoints()){
            answer = "Выиграл: " + p2.getName() + ", он набрал " + p2.getPoints() + " очков";
        }else{
            answer = "Ничья! Вы оба набрали по " + p1.getPoints() + " очков";
        }
        for (ClientHandler client: clients){
            client.sendMessage(answer);
        }
    }

    public boolean isJoinable(){
        if (clients.size() >= 2){
            return false;
        }
        return true;
    }
    public int getNecessaryCountOfWordsLevel(ClientHandler client){
        return game.getCountOfNecessaryWordsOnLevel(client.getPlayer().getCurrentLevel());
    }
    public int getNowLevel(ClientHandler client){
        return client.getPlayer().getCurrentLevel();
    }
}
