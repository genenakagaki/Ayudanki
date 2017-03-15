package com.genenakagaki.ayudanki.data.model;

/**
 * Created by gene on 3/15/17.
 */

public class Card {

    private long id;
    private String term;
    private String definition;
    private int points;

    public Card() {}

    public Card(long id, String term, String definition, int points) {
        this.id = id;
        this.term = term;
        this.definition = definition;
        this.points = points;
    }

    public boolean equals(Card card) {
        return card.getId() == id;
    }

    public long getId() {
        return id;
    }

    public String getTerm() {
        return term;
    }

    public String getDefinition() {
        return definition;
    }

    public int getPoints() {
        return points;
    }

}
