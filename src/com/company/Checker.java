package com.company;

import java.util.*;
import java.io.*;

class Checker {
    Solver solver = new Solver(false);

    public int total_runs = 0;
    public int total_successes = 0;
    Map<Integer, Integer> guesses = new HashMap<>();

    public Checker() {
        guesses.put(-1, 0);
        guesses.put(1, 0);
        guesses.put(2, 0);
        guesses.put(3, 0);
        guesses.put(4, 0);
        guesses.put(5, 0);
        guesses.put(6, 0);
    }

    public ArrayList<String> readFile(char prefix) {
        String[] raw_words = new String[]{};
        try {
            Scanner reader = new Scanner(new File("src/com/company/words.txt"));
            if (reader.hasNextLine()) {
                raw_words = reader.nextLine().split(" ");
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ArrayList<String> words = new ArrayList<>();
        for (String raw_word : raw_words) {
            if (raw_word.charAt(0) != prefix) {
                continue;
            }
            words.add(raw_word);
        }
        return words;
    }

    public void solveMultiple(char prefix) {
        solver.enablePrint(false);
        ArrayList<String> words = readFile(prefix);
        int total = words.size();
        int success = 0;

        for (String word : words) {
            int guess = solve(word, false);
            if (guess != -1) {
                success++;
            }
            guesses.put(guess, guesses.get(guess) + 1);
        }
        total_successes += success;
        total_runs += total;
        System.out.println(prefix + ": " + success + "/" + total + "(" + (int) ((double) success / (double) total * 100) + "%)");
    }

    public int solve(String word) {
        return solve(word, true);
    }

    public int solve(String word, boolean enable_print) {
        solver.enablePrint(enable_print);
        solver.reset();
        int guesses = 0;
        String guess = "";
        do {
            guesses++;
            guess = solver.guess();
            if (guess.equals(word)) {
                return guesses;
            }
            if (guess.equals("")) {
                return -1;
            }
            ArrayList<Solver.LetterResult> result = new ArrayList<>();
            for (int i = 0; i < word.length(); ++i) {
                if (word.charAt(i) == guess.charAt(i)) {
                    result.add(Solver.LetterResult.CORRECT_SPOT);
                } else if (word.indexOf(guess.charAt(i)) != -1) {
                    result.add(Solver.LetterResult.WRONG_SPOT);
                } else {
                    result.add(Solver.LetterResult.NOT_FOUND);
                }
            }
            solver.validateGuess(result);
        } while (guesses < 6);
        return -1;
    }
}