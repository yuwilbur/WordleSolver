package com.company;

import java.util.*;
import java.io.*;

class Solver {
    final String[] raw_words;
    final char[] alphabets = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
            'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
    final ArrayList<String> starter_words = new ArrayList<>(Arrays.asList(new String[] { "FIBER", "MADLY", "PUNKS", "GOTCH"}));
    final Map<String, ArrayList<String>> sorted_words = new HashMap<>();

    ArrayList<ArrayList<Character>> possible_letters = new ArrayList<>();
    ArrayList<Character> found_letters = new ArrayList<>();
    ArrayList<Character> remaining_letters = new ArrayList<>();

    int starter_words_index = 0;
    String guess = "";

    // Map<Character, Integer> char_frequency = new LinkedHashMap<>();

    boolean enable_print = true;

    public static enum Status {
        NOT_FOUND, WRONG_SPOT, CORRECT_SPOT
    }

    public Solver() {
        this(true);
    }

    public Solver(boolean enable_print) {
        this.enable_print = enable_print;
        String[] read_raw_words = new String[]{};
        try {
            Scanner reader = new Scanner(new File("src/com/company/words.txt"));
            if (reader.hasNextLine()) {
                read_raw_words = reader.nextLine().split(" ");
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        raw_words = read_raw_words;
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
        for (String raw_word : raw_words) {
            char charArray[] = raw_word.toCharArray();
            Arrays.sort(charArray);
            String sorted_word = new String(charArray);
            if (!this.sorted_words.containsKey(sorted_word)) {
                this.sorted_words.put(sorted_word, new ArrayList<>());
            }
            this.sorted_words.get(sorted_word).add(raw_word);
        }

        starter_words_index = 0;
        guess = "";

        // for (char alphabet : alphabets) {
        //   this.char_frequency.put(alphabet, 0);
        // }
    }

    public String guess() {
        if (starter_words_index < starter_words.size() && getSize() != 1) {
            guess = starter_words.get(starter_words_index);
            starter_words_index++;
        } else {
            guess = processNextGuess();
        }
        print("Guess: " + guess);
        return guess;
    }

    public void validateGuess(ArrayList<Status> result) {
        for (int i = 0; i < result.size(); ++i) {
            char current_letter = guess.charAt(i);
            switch (result.get(i)) {
                case CORRECT_SPOT:
                    found_letters.add(current_letter);
                    possible_letters.get(i).clear();
                    possible_letters.get(i).add(current_letter);
                    removeWordsWithoutLetter(current_letter);
                    break;
                case WRONG_SPOT:
                    found_letters.add(current_letter);
                    if (possible_letters.get(i).indexOf(current_letter) != -1) {
                        possible_letters.get(i).remove(possible_letters.get(i).indexOf(current_letter));
                    }
                    removeWordsWithoutLetter(current_letter);
                    break;
                case NOT_FOUND:
                    removeWordsWithLetter(current_letter);
                default:
                    break;
            }
            remaining_letters.remove(remaining_letters.indexOf(current_letter));
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

    private String processNextGuess() {
        if (getSize() == 1) {
            for (String sorted_word : sorted_words.keySet()) {
                for (String word : sorted_words.get(sorted_word)) {
                    return word;
                }
            }
        } else {
            for (String sorted_word : sorted_words.keySet()) {
                for (String word : sorted_words.get(sorted_word)) {
                    // System.out.println(word);
                }
            }
        }

        return "";
    }

    private void processWords() {
        for (String sorted_word : sorted_words.keySet()) {
            ArrayList<String> words = sorted_words.get(sorted_word);
            words.removeIf(word -> {
                for (int i = 0; i < this.possible_letters.size(); ++i) {
                    // If we cannot find the word's character in one of the possible_letters characters,
                    // delete it.
                    if (possible_letters.get(i).indexOf(word.charAt(i)) == -1) {
                        return true;
                    }
                }
                return false;
            });
        }
        sorted_words.entrySet().removeIf(entry -> (entry.getValue().size() == 0));
    }

    // private void calculateFrequency() {
    //   for (char alphabet : char_frequency.keySet()) {
    //     char_frequency.put(alphabet, 0);
    //   }
    //   for (String key : sorted_words.keySet()) {
    //     for (char letter : key.toCharArray()) {
    //       char_frequency.put(letter, char_frequency.get(letter) + sorted_words.get(key).size());
    //     }
    //   }
    //   char_frequency = char_frequency.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
    //       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue,
    //           LinkedHashMap::new));
    //   // DEBUG
    //   // for (char alphabet : char_frequency.keySet()) {
    //   // System.out.println(alphabet + ": " + char_frequency.get(alphabet));
    //   // }
    // }

    private int getSize() {
        int size = 0;
        for (String sorted_word : sorted_words.keySet()) {
            size += sorted_words.get(sorted_word).size();
        }
        return size;
    }

    private void print(String message) {
        if (!enable_print) {
            return;
        }
        System.out.println(message);
    }
}