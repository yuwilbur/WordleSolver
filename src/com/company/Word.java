package com.company;

import java.util.ArrayList;

public class Word {
    public String word = "";
    public int weight = 0;
    public ArrayList<Integer> weights = new ArrayList<>();

    public Word() {
        weights.add(0);
        weights.add(0);
        weights.add(0);
        weights.add(0);
        weights.add(0);
    }

    public void calculateWeights() {
        this.weight = 0;
        for(Integer weight : weights) {
            this.weight += weight;
        }
    }
}
