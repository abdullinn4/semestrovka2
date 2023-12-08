package com.example.project.GameLogic;

import java.util.*;

public class Game {
    private Map<Integer,String> levelWords;
    private Map<Integer, List<String>> levelAnswers;
    private Map<Integer,Integer> countOfNecessaryWordsOnLevels;
    private Map<Integer,List<String>> guessedWordsOnLevel;

    public Game(){
        levelWords = new HashMap<>();
        levelAnswers = new HashMap<>();
        countOfNecessaryWordsOnLevels = new HashMap<>();
        guessedWordsOnLevel = new HashMap<>();
        createGame();
    }
    public void createGame(){
        Words words = new Words();
        Map<String,String[]> wordsMap = words.getWordsMap();
        int level = 0;
        for (String key : wordsMap.keySet()){
            level++;
            String [] wordName = key.split(" ");
            levelWords.put(level,wordName[0]);
            countOfNecessaryWordsOnLevels.put(level, Integer.parseInt(wordName[1]));
            levelAnswers.put(level, List.of(wordsMap.get(key)));
        }
    }

    public String getWordByLevel(int level){
        return levelWords.get(level);
    }
    public List<String> getAnswersByLevel(int level){
        return levelAnswers.get(level);
    }
    public Integer getCountOfNecessaryWordsOnLevel(int level){
        return countOfNecessaryWordsOnLevels.get(level);
    }
    public boolean checkAnswer(int level,String guess){
        List<String> answers = getAnswersByLevel(level);
        List<String> guessedAnswers = guessedWordsOnLevel.getOrDefault(level,new ArrayList<>());
        for (String answer: answers){
            if (answer.equals(guess) && !guessedAnswers.contains(answer)){
                guessedAnswers.add(guess);
                guessedWordsOnLevel.put(level,guessedAnswers);
                return true;
            }
        }
        return false;
    }
    public boolean getGuessedWords(int level,String guess){
        List<String> guessedWords = guessedWordsOnLevel.get(level);
        for(String word: guessedWords){
            if(word.equals(guess)){
                return true;
            }
        }
        return false;
    }

}
