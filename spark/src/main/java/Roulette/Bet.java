package Roulette;

import org.bson.types.ObjectId;

public class Bet {
    String color;
    int amount;
    ObjectId playerId;

    public Bet(String color, int amount, ObjectId playerId) {
        this.color = color;
        this.amount = amount;
        this.playerId = playerId;
    }
}
