package DAO;

import DTO.NoteDto;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

public class NoteDao {
    private static NoteDao instance;
    private static MongoCollection<Document> noteCollection;

    public static void initInstance() {
        if (instance == null) {
            instance = new NoteDao();

            // Adding Roulette Note
            for (int i = 1; i <= 36; i++) {
                if ((i % 2 == 0 && i <= 10) || (i % 2 != 0 && i >= 11 && i <= 17) || (i % 2 == 0 && i >= 20 && i <= 28) || (i % 2 != 0 && i >= 29)) {
                    add(new Document().append("note", i).append("color", "black"));
                } else {
                    add(new Document().append("note", i).append("color", "red"));
                }
            }
        }
    }

    private NoteDao() {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase db = mongoClient.getDatabase("Roulette");
        noteCollection = db.getCollection("NoteCollection");
    }

    private static NoteDto add(Document req) {
        noteCollection.insertOne(req);
        return new NoteDto(req.get("note"), req.get("color"));
    }

    public static List<NoteDto> list() {
        MongoCursor<Document> cursor = noteCollection.find().iterator();
        List<NoteDto> response = new ArrayList<NoteDto>();
        try {
            while (cursor.hasNext()) {
                Document temp = cursor.next();
                response.add(new NoteDto(temp.get("note"), temp.get("color")));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            cursor.close();
        }
        return response;
    }

    public static NoteDto delete(ObjectId req) {
        Document temp = noteCollection.find(Filters.eq("_id", req)).first();
        noteCollection.deleteOne(temp);
        return new NoteDto(temp.get("note"), temp.get("color"));
    }

    public static NoteDto get(ObjectId req) {
        Document temp = noteCollection.find(Filters.eq("_id", req)).first();
        return new NoteDto(temp.get("note"), temp.get("color"));
    }
}
