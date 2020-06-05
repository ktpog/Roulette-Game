package Server;

import DAO.PlayerDao;
import DTO.PlayerDto;
import DTO.ResponseDto;
import Roulette.RouletteInstance;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static spark.Spark.*;


public class SparkDemo {

    private static RouletteInstance rouletteInstance = new RouletteInstance();

    public static void main(String[] args) {
        port(1234);
        webSocket("/ws", WebSocketHandler.class);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        new Thread(rouletteDaemon).start();

        post("/createAccount", (req, res) -> {
            PlayerDao.initInstance();
            System.out.println("creataccount");
            if (PlayerDao.userExists(req.queryParams("newName"))) {
                return gson.toJson(new ResponseDto.ResponseBuilder().build());
            } else {
                Document doc = new Document().append("name", req.queryParams("newName"))
                        .append("password", req.queryParams("newPassword"))
                        .append("score", 10000);//, /*List<Object> ach,*/ ObjectId _id, Object score")
                PlayerDto note = PlayerDao.add(doc);
                List<PlayerDto> ls = new ArrayList<PlayerDto>();
                ls.add(note);
                System.out.println(req.body());
                return gson.toJson(new ResponseDto.ResponseBuilder()
                        .setResponseCode("OK")
                        .setPlayerList(ls)
                        .build());
            }
        });
        post("/login", (req, res) -> {
            PlayerDao.initInstance();
            String name = req.queryParams("name");
            String password = req.queryParams("password");
            System.out.println(name + password);
            if (PlayerDao.userExists(name, password)) {
                List<PlayerDto> ls = new ArrayList<PlayerDto>();
                ls.add(PlayerDao.get(PlayerDao.getId(name, password)));
                return gson.toJson(new ResponseDto.ResponseBuilder()
                        .setResponseCode("OK")
                        .setPlayerList(ls)
                        .build());
            } else {
                return gson.toJson(new ResponseDto.ResponseBuilder().build());
            }
        });

        get("/leaderboard", (req, res) -> {
            PlayerDao.initInstance();
            List<PlayerDto> ls = PlayerDao.getLeaders();
            return gson.toJson(new ResponseDto.ResponseBuilder()
                    .setResponseCode("OK")
                    .setPlayerList(ls)
                    .build());
        });

        get("/getPlayer", (req, res) -> {
            PlayerDao.initInstance();
            String name = req.queryParams("name");
            List<PlayerDto> ls = new ArrayList<PlayerDto>();
            ls.add(PlayerDao.get(PlayerDao.getId(name)));
            return gson.toJson(new ResponseDto.ResponseBuilder()
                    .setResponseCode("OK")
                    .setPlayerList(ls)
                    .build());
        });

        post("/placebet", (req, res) -> {
            String name = req.queryParams("name");
            String color = req.queryParams("color");
            Integer amount = Integer.parseInt(req.queryParams("amount"));

            //return PlayerDto after placing bet so score can be updated in the front end;
            //check responseCode for any Error;
            int status = RouletteInstance.getCurrent().placeBet(PlayerDao.getId(name), color, amount);
            if (status != 0) {
                return gson.toJson(new ResponseDto.ResponseBuilder()
                        .setResponseCode("Error")
                        .build());
            } else {
              List<PlayerDto> ls = new ArrayList<PlayerDto>();
              ls.add(PlayerDao.get(PlayerDao.getId(name)));
                return gson.toJson(new ResponseDto.ResponseBuilder()
                        .setResponseCode("OK")
                        .setPlayerList(ls)
                        .build());
            }
        });

        get("/getRollTime", (req, res) -> {
            return gson.toJson(new ResponseDto.ResponseBuilder()
                    .setResponseCode("OK")
                    .setSecondTillRoll((int) (RouletteInstance.getCurrent().getRollTime() - System.currentTimeMillis()) / 1000 + 2));
        });

        get("/resultInt", (req, res) -> {
            return gson.toJson(new ResponseDto.ResponseBuilder()
                    .setResponseCode("OK")
                    .setPreviousResultInt(RouletteInstance.getPrevious().getResultInt())
                    .build());
        });
    }

    private static Runnable rouletteDaemon = new Runnable() {
        public void run() {
            while (true) {
                rouletteInstance.update();
            }
        }
    };
}
