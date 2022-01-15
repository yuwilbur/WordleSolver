package com.company;
import java.util.*;
import java.io.*;

class Checker {
    Solver solver = new Solver(false);

    public int total_runs = 0;
    public int total_successes = 0;

    public Checker() {
    }

    public ArrayList<String> readFile(char prefix) {
        String[] raw_words = new String[] {};
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
        ArrayList<String> words = readFile(prefix);
        int total = words.size();
        int success = 0;
        for (String word : words) {
            if (solve(word)) {
                success++;
            }
        }
        total_successes += success;
        total_runs += total;
        System.out.println(prefix + ": " + success + "/" + total + "(" + (double)success/(double)total*100 + "%)");
    }

    public boolean solve(String word) {
        solver.reset();
        String guess = "";
        do {
            guess = solver.guess();
            if (guess.equals(word)) {
                return true;
            }
            if (guess.equals("")) {
                return false;
            }
            ArrayList<Solver.Status> result = new ArrayList<>();
            for (int i = 0; i < word.length(); ++i) {
                if (word.charAt(i) == guess.charAt(i)) {
                    result.add(Solver.Status.CORRECT_SPOT);
                } else if (word.indexOf(guess.charAt(i)) != -1) {
                    result.add(Solver.Status.WRONG_SPOT);
                } else {
                    result.add(Solver.Status.NOT_FOUND);
                }
            }
            solver.validateGuess(result);
        } while (!guess.equals(""));
        return false;
    }
}