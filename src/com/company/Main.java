package com.company;

import java.util.*;
import java.io.*;

class Main {
    final static char[] alphabets = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    public static void main(String[] args) throws IOException {
//        run();
        check();
    }

    static void run() {
        Solver solver = new Solver();

        System.out.println("");
        System.out.println("Welcome to Wilbur's Wordle solver!!! <3");
        System.out.println("");
        System.out.println("When you are prompted about the guess, input the following:");
        System.out.println("0: This letter is not in the word. Please try again.");
        System.out.println("1: This letter is in the word but wrong spot. So close.");
        System.out.println("2: This letter is in the word and correct spot! DING DING DING!");
        System.out.println("");
        System.out.println(
                "For example, if the answer is WORDS and the guess is ROPES, then the response will be 12002 because R is in WORDS but wrong spot, and O and S are both in the word and correct spot. Everything else is not in the word.");

        Scanner reader = new Scanner(System.in);
        String input = "";
        do {
            System.out.println("");
            System.out.println("What about now (Can I have your number)?");
            System.out.println(solver.guess());
            input = reader.nextLine();
            // TODO: Check if input is number.
            ArrayList<Solver.LetterResult> result = new ArrayList<>();
            for (char character : input.toCharArray()) {
                if (character == '0') {
                    result.add(Solver.LetterResult.NOT_FOUND);
                } else if (character == '1') {
                    result.add(Solver.LetterResult.WRONG_SPOT);
                } else if (character == '2') {
                    result.add(Solver.LetterResult.CORRECT_SPOT);
                } else {
                    // uh oh
                }
            }
            solver.validateGuess(result);
        } while (!input.equals("END"));
        reader.close();
    }

    static void check() {
        Checker checker = new Checker();
//        checker.solve("TANGY");
        for (char alphabet : alphabets) {
            checker.solveMultiple(alphabet);
        }
        System.out.println("Summary");
        System.out.println(checker.total_successes + "/" + checker.total_runs + " (" + (int) ((double) checker.total_successes / (double) checker.total_runs * 100) + "%)");
        Map<Integer, Integer> guesses = checker.guesses;
        for(Integer key : guesses.keySet()) {
            if (key != -1) {
                System.out.println("Solved " + guesses.get(key) + " words with " + key + " guess(es).");
            } else {
                System.out.println("Could not solve " + guesses.get(key) + " words.");
            }
        }
    }
}