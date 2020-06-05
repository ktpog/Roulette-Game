package DTO;

import org.bson.types.ObjectId;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PlayerDto {
    public final String name;
    public final String password;
    //public final  List<String> achievement;
    public final ObjectId _id;
    public final int score;

    public PlayerDto(Object name, Object password, /*List<Object> ach,*/ ObjectId _id, Object score) {
        this.name = name.toString();
        this.password = password.toString();
        /*achievement = ach.stream()
                .map(object -> Objects.toString(object, null))
                .collect(Collectors.toList());*/
        this._id = _id;
        this.score = (int) score;
    }

    public int getScore() {
        return score;
    }
}