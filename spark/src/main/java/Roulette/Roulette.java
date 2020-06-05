package Roulette;

import DAO.PlayerDao;
import org.bson.types.ObjectId;

import java.util.*;

public class Roulette {

    private final long countDown = 30000L; //time between each roulette roll session in milliseconds, default 30s
    private Date rollTime = new Date(System.currentTimeMillis() + countDown); //server roll time
    private String result;
    private Integer resultInt;
    private String sessionId = UUID.randomUUID().toString();


    // Hashmap for recording betting on 3 different color;
    // records player id and how much they bet on the color;
    private HashMap<ObjectId, Bet> betMap = new HashMap<>();


    /*
     * method returns how many miliseconds before the server is rolling for results in long
     */
    public long getRollTime() {
        return rollTime.getTime();
    }

    /*
     * method returns roll result:
     * black ~47.36%
     * red ~47.36%
     * green ~5.26%
     */
    public String roll() {
        Random r = new Random();
        List<Integer> black = new ArrayList<>();
        for (int j = 1; j < 15; j++) {
            if (j % 2 != 0) {
                black.add(j);
            }
        }

        int i = r.nextInt(14);
        resultInt = i;
        if (i == 0) {
            result = "Green";
            return "Green";
        } else if (black.contains(i)) {
            result = "Black";
            return "Black";
        } else {
            result = "Red";
            return "Red";
        }
    }

    /*
     * method call for players to place a bet,
     * returns 0 if task performs successfully
     * returns 1 if player already placed a bet
     * returns -1 if not enough money
     */
    public int placeBet(ObjectId playerId, String color, int amount) {
        Bet bet = new Bet(color, amount, playerId);
        if (betMap.containsKey(playerId)) { //check if player has place a bet already, if so return error 1
            return 1;
        } else {
            if (PlayerDao.get(playerId).score > amount) { //then check if player has enough credit
                //if enough credit deducte the score then place the bet into map
                PlayerDao.updateScore(playerId, PlayerDao.get(playerId).score - amount);
                betMap.put(playerId, bet);
            } else {
                return -1; // otherwise return error -1
            }
        }
        return 0;
    }

    public void updateScore(String result) {
        this.betMap.entrySet().stream()
                .filter(bet -> bet.getValue().color.equals(result))
                .forEach(bet -> {
                    if (result.equals("Green")) {
                        System.out.println(PlayerDao.get(bet.getValue().playerId) + ": " + PlayerDao.get(bet.getValue().playerId).score + bet.getValue().amount * 14);
                        PlayerDao.updateScore(bet.getValue().playerId, PlayerDao.get(bet.getValue().playerId).score + bet.getValue().amount * 14);
                    } else {
                        System.out.println(PlayerDao.get(bet.getValue().playerId) + ": " + PlayerDao.get(bet.getValue().playerId).score + bet.getValue().amount * 2);
                        PlayerDao.updateScore(bet.getValue().playerId, PlayerDao.get(bet.getValue().playerId).score + bet.getValue().amount * 2);
                    }
                });
    }

    /*
    run this method when time is up, updates the score,
     */
    public void endOfRound() {
        this.result = roll();
        updateScore(this.result);
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Integer getResultInt() {
        return resultInt;
    }
}
