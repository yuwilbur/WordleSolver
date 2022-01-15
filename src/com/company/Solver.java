package com.company;

import java.util.*;
import java.io.*;

class Solver {
    RawData[] raw_data = new RawData[]{};
    final char[] alphabets = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
            'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    final ArrayList<String> starter_words = new ArrayList<>(Arrays.asList(new String[]{"FIBER", "MADLY", "PUNKS", "GOTCH"}));
    final Map<String, ArrayList<Word>> sorted_words = new HashMap<>();
    final List<Map<Character, Integer>> letters_weights = new ArrayList<>();

    ArrayList<ArrayList<Character>> possible_letters = new ArrayList<>();
    ArrayList<Character> found_letters = new ArrayList<>();
    ArrayList<Character> remaining_letters = new ArrayList<>();

    int starter_words_index = 0;
    String guess = "";

    public boolean enable_print = true;

    public static enum LetterResult {
        NOT_FOUND, WRONG_SPOT, CORRECT_SPOT
    }

    public Solver() {
        this(true);
    }

    public Solver(boolean enable_print) {
        this.enable_print = enable_print;
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

        for (int i = 0; i < 5; ++i) {
            Map<Character, Integer> letter_weights = new HashMap<>();
            for (char alphabet : alphabets) {
                letter_weights.put(alphabet, 0);
            }
            letters_weights.add(letter_weights);
        }
        for (String word : raw_words) {
            for (int i = 0; i < word.length(); ++i) {
                char letter = word.charAt(i);
                letters_weights.get(i).put(letter, letters_weights.get(i).get(letter) + 1);
            }
        }
        raw_data = new RawData[raw_words.length];
        for (int i = 0; i < raw_words.length; ++i) {
            raw_data[i] = new RawData();
            raw_data[i].word = raw_words[i];
            ArrayList<Character> visited_letters = new ArrayList<>();
            for (int j = 0; j < raw_data[i].word.length(); ++j) {
                char letter = raw_data[i].word.charAt(j);
                if (visited_letters.indexOf(letter) != -1) {
                    continue;
                }
                visited_letters.add(letter);
                raw_data[i].weight += letters_weights.get(j).get(letter);
            }
        }

        reset();
    }

    public void reset() {
        possible_letters.clear();
        for (int i = 0; i < 5; ++i) {
            possible_letters.add(new ArrayList<>());
            for (char alphabet : alphabets) {
                possible_letters.get(possible_letters.size() - 1).add(alphabet);
            }
        }

        remaining_letters.clear();
        for (char alphabet : alphabets) {
            remaining_letters.add(alphabet);
        }

        found_letters.clear();

        sorted_words.clear();
        for (int i = 0; i < raw_data.length; ++i) {
            char charArray[] = raw_data[i].word.toCharArray();
            Arrays.sort(charArray);
            String sorted_word = new String(charArray);
            if (!sorted_words.containsKey(sorted_word)) {
                sorted_words.put(sorted_word, new ArrayList<>());
            }
            Word word = new Word();
            word.word = raw_data[i].word;
            for (int j = 0; j < word.word.length(); ++j) {
                word.weights.set(j, letters_weights.get(j).get(word.word.charAt(j)));
            }
            word.calculateWeights();
            sorted_words.get(sorted_word).add(word);
        }

        starter_words_index = 0;
        guess = "";
    }

    public String guess() {
        guess = dumbGuess();
        print("Guess: " + guess);
        return guess;
    }

    public String averageGuess() {
        int current_weight = -1;
        String current_word = "";
        for (String sorted_word : sorted_words.keySet()) {
            for (Word word : sorted_words.get(sorted_word)) {
                if (word.weight > current_weight) {
                    current_weight = word.weight;
                    current_word = word.word;
                }
            }
        }
        return current_word;
    }

    public String dumbGuess() {
        if (starter_words_index < starter_words.size() && getSize() != 1) {
            return starter_words.get(starter_words_index++);
        } else {
            return averageGuess();
        }
    }

    public void validateGuess(ArrayList<LetterResult> result) {
        for (int i = 0; i < result.size(); ++i) {
            char current_letter = guess.charAt(i);
            switch (result.get(i)) {
                case CORRECT_SPOT:
                    found_letters.add(current_letter);
                    possible_letters.get(i).clear();
                    possible_letters.get(i).add(current_letter);
                    removeWordsWithoutLetter(current_letter);
                    recalculateWeightsWithLetter(current_letter, result.get(i));
                    break;
                case WRONG_SPOT:
                    found_letters.add(current_letter);
                    if (possible_letters.get(i).indexOf(current_letter) != -1) {
                        possible_letters.get(i).remove(possible_letters.get(i).indexOf(current_letter));
                    }
                    removeWordsWithoutLetter(current_letter);
                    recalculateWeightsWithLetter(current_letter, result.get(i));
                    break;
                case NOT_FOUND:
                    removeWordsWithLetter(current_letter);
                default:
                    break;
            }
            if (remaining_letters.indexOf(current_letter) != -1) {
                remaining_letters.remove(remaining_letters.indexOf(current_letter));
            }
        }
        processWords();
        print("Possible words left: " + getSize());
    }

    private void removeWordsWithLetter(char letter) {
        if (remaining_letters.indexOf(letter) == -1) {
            return;
        }
        sorted_words.entrySet().removeIf(entry -> (entry.getKey().indexOf(letter) != -1));
    }

    private void removeWordsWithoutLetter(char letter) {
        if (remaining_letters.indexOf(letter) == -1) {
            return;
        }
        sorted_words.entrySet().removeIf(entry -> (entry.getKey().indexOf(letter) == -1));
    }

    private void recalculateWeightsWithLetter(char letter, LetterResult result) {
        for (String sorted_word : sorted_words.keySet()) {
            if (sorted_word.indexOf(letter) == -1) {
                continue;
            }
            for (Word word : sorted_words.get(sorted_word)) {
                for (int i = 0; i < word.word.length(); ++i) {
                    char current_letter = word.word.charAt(i);
                    if (current_letter == letter) {
                        if (result == LetterResult.WRONG_SPOT) {
                            word.weights.set(i, letters_weights.get(i).get(current_letter) * 4);
                        } else if (result == LetterResult.CORRECT_SPOT) {
                            word.weights.set(i, 0);
                        }
                        word.calculateWeights();
                    }
                }
            }
        }
    }

    private void processWords() {
        for (String sorted_word : sorted_words.keySet()) {
            ArrayList<Word> words = sorted_words.get(sorted_word);
            words.removeIf(word -> {
                for (int i = 0; i < this.possible_letters.size(); ++i) {
                    // If we cannot find the word's character in one of the possible_letters characters,
                    // delete it.
                    if (possible_letters.get(i).indexOf(word.word.charAt(i)) == -1) {
                        return true;
                    }
                }
                return false;
            });
        }
        sorted_words.entrySet().removeIf(entry -> (entry.getValue().size() == 0));
    }

    private int getSize() {
        return this.getSize(false);
    }

    private int getSize(boolean showAll) {
        int size = 0;
        for (String sorted_word : sorted_words.keySet()) {
            size += sorted_words.get(sorted_word).size();
            if (showAll) {
                for (Word word : sorted_words.get(sorted_word)) {
                    System.out.println(word.word);
                }
            }
        }
        return size;
    }

    public void enablePrint(boolean enable) {
        enable_print = enable;
    }

    private void print(String message) {
        if (!enable_print) {
            return;
        }
        System.out.println(message);
    }
}