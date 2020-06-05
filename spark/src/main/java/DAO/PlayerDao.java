package DAO;

import DTO.PlayerDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

public class PlayerDao {
    private static PlayerDao instance;
    private static MongoCollection<Document> playerCollection;

    public static void initInstance() {
        if (instance == null) {
            instance = new PlayerDao();
        }
    }

    private PlayerDao() {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase db = mongoClient.getDatabase("Roulette");
        playerCollection = db.getCollection("PlayerCollection");
    }


    /* add new player return its PlayerDto */
    public static PlayerDto add(Document req) {
        playerCollection.insertOne(req);
        return new PlayerDto(req.get("name"), req.get("password"),/* req.get("achievement"),*/(ObjectId) req.get("_id"), req.get("score"));
    }

    /* return a List<PlayerDto> of all player */
    public static List<PlayerDto> list() {
        MongoCursor<Document> cursor = playerCollection.find().iterator();
        List<PlayerDto> response = new ArrayList<PlayerDto>();
        try {
            while (cursor.hasNext()) {
                Document temp = cursor.next();
                response.add(new PlayerDto(temp.get("name"), temp.get("password"), /*Achievement[] achievement,*/(ObjectId) temp.get("_id"), temp.get("score")));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            cursor.close();
        }
        return response;
    }

    /* just in case */
    public static PlayerDto delete(ObjectId req) {
        Document temp = playerCollection.find(Filters.eq("_id", req)).first();
        playerCollection.deleteOne(temp);
        return new PlayerDto(temp.get("name"), temp.get("password"), /*Achievement[] achievement,*/ (ObjectId) temp.get("_id"), temp.get("score"));
    }

    /* get the PlayerDto by ObjectId */
    public static PlayerDto get(ObjectId req) {
        Document temp = playerCollection.find(Filters.eq("_id", req)).first();
        return new PlayerDto(temp.get("name"), temp.get("password"), /*Achievement[] achievement,*/ (ObjectId) temp.get("_id"), temp.get("score"));
    }

    /* updateScore and return its refreshed playerDto */
    public static PlayerDto updateScore(ObjectId req, Object score) {
        playerCollection.updateOne(Filters.eq("_id", req), new Document("$set", new Document("score", score)));
        return get(req);
    }

    /* return List<PlayerDto> of top ten score */
    public static List<PlayerDto> getLeaders() {
        List<PlayerDto> temp = list();
        List<PlayerDto> topTen = temp.stream().sorted(Comparator.comparingInt(PlayerDto::getScore).reversed()).limit(10).collect(Collectors.toList());
        return topTen;
    }

    /* return ObjectId  by name*/
    public static ObjectId getId(String name) {
        List<PlayerDto> temp = list();
        temp = temp.stream()
                .filter(player -> player.name.equals(name))
                .collect(Collectors.toList());
        return temp.get(0)._id;
    }

    /* return ObjectId  by name and password*/
    public static ObjectId getId(String name, String password) {
        List<PlayerDto> temp = list();
        temp = temp.stream()
                .filter(player -> player.name.equals(name))
                .filter(player -> player.password.equals(password))
                .collect(Collectors.toList());
        return temp.get(0)._id;
    }

    public static boolean userExists(String name) {
        List<PlayerDto> temp = list();
        temp = temp.stream()
                .filter(player -> player.name.equals(name))
                .collect(Collectors.toList());
        return temp.size() == 1;
    }

    public static boolean userExists(String name, String password) {
        List<PlayerDto> temp = list();
        temp = temp.stream()
                .filter(player -> player.name.equals(name))
                .filter(player -> player.password.equals(password))
                .collect(Collectors.toList());
        return temp.size() == 1;
    }

    /* Under development */
    public static String getAchievement(int score) {
        switch (score) {
            case -1000000:
                return "'A small loan of a million dollar'\n\n" +
                        "Donald Trump\n" +
                        "45th U.S. President";
            case -100000:
                return "'I stole to gamble,'\n\n" +
                        "Brian Molon\n" +
                        "Misappropriated company funds of over $10 million to fund his frequent casinos trips to Atlantic City\n" +
                        "losing the majority of the money very quickly";
            case -10000:
                return "'Whenever a man does\n" +
                        "a thoroughly stupid thing,\n" +
                        "it is always from the noblest motives.'\n\n" +
                        "Oscar Wilde\n" +
                        "Irish Writer and Poet";
            case 0:
                return "'The only sure thing about luck is\n" +
                        "that it will change.'\n\n" +
                        "Bret Harte\n" +
                        "American Author and Poet";
            case 1000:
                return "Fresh";
            case 10000:
                return "'Remember this:\n" +
                        "The house doesn’t beat the player.\n" +
                        "It just gives him the opportunity\n" +
                        "to beat himself.'\n\n" +
                        "Nicholas Dandolos, a.k.a. “Nick the Greek”\n" +
                        "Greek-born Professional Gambler and High Roller)";
            case 100000:
                return "'Luck is what happens\n" +
                        "when preparation meets opportunity.'\n\n" +
                        "Seneca\n" +
                        "Roman Philosopher, Dramatist, and Writer";
            case 1000000:
                return "'In gambling the many must lose\n" +
                        "in order\n" +
                        "that the few may win.'\n\n" +
                        "George Bernard Shaw\n" +
                        "Irish Playwright and Co-founder of the London School of Economics)";
            default:
                return null;
        }
    }
}
