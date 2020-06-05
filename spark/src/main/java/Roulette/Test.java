package Roulette;

import DTO.ResponseDto;
import org.bson.types.ObjectId;

import java.util.HashMap;

public class Test {
    public static void main(String[] args) {

        Roulette roulette = new Roulette();


//        green: 5.263518684200719
//        red: 47.36851763323346
//        black: 47.36796368256582

        // public void updateScore(String result, HashMap< ObjectId, Bet> betMap){
        ObjectId id1 = new ObjectId();
        ObjectId id2 = new ObjectId();
        ObjectId id3 = new ObjectId();

        HashMap<ObjectId, Bet> betMap = new HashMap<>();
        Bet bet1 = new Bet("Red", 100, id1);
        Bet bet2 = new Bet("Black", 100, id2);
        Bet bet3 = new Bet("Green", 100, id3);

        betMap.put(id1, bet1);
        betMap.put(id2, bet2);
        betMap.put(id3, bet3);

        System.out.println(new ResponseDto.ResponseBuilder());
    }
}
