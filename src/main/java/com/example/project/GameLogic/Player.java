package com.example.project.GameLogic;

public class Player {
    private String name;
    private int currentLevel;
    private int guessedWordCount;
    private int points;

    public Player(String name){
        this.name = name;
        this.currentLevel = 1;
        this.guessedWordCount = 0;
        this.points = 0;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public String getName() {
        return name;
    }

    public int getGuessedWordCount() {
        return guessedWordCount;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
        this.guessedWordCount = 0;
    }
    public void incrementGuessedWordsCount() {
        this.guessedWordCount++;
        this.points++;
    }

    public int getPoints() {
        return points;
    }
}

