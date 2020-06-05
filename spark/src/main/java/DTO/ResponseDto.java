package DTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ResponseDto {
    public final String date;
    public final String responseCode;
    public final List<PlayerDto> playerList;
    public final List<NoteDto> noteList;
    public final Integer secondTillRoll;
    public final Integer previousResultInt;

    private ResponseDto(ResponseBuilder builder) {
        this.date = builder.date;
        this.responseCode = builder.responseCode;
        this.playerList = builder.playerList;
        this.noteList = builder.noteList;
        this.secondTillRoll = builder.secondTillRoll;
        this.previousResultInt = builder.previousResultInt;
    }

    public static class ResponseBuilder {
        private String date;
        private String responseCode;
        private List<PlayerDto> playerList;
        private List<NoteDto> noteList;
        private Integer secondTillRoll;
        private Integer previousResultInt;

        public ResponseBuilder() {
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
            date = currentDateTime.format(formatter);
            this.responseCode = "Error";
            this.playerList = null;
            this.noteList = null;
        }

        public ResponseBuilder setPlayerList(List<PlayerDto> obj) {
            this.playerList = obj;
            return this;
        }

        public ResponseBuilder setNoteList(List<NoteDto> obj) {
            this.noteList = obj;
            return this;
        }

        public ResponseBuilder setResponseCode(String code) {
            this.responseCode = code;
            return this;
        }

        public ResponseBuilder setSecondTillRoll(Integer time){
            this.secondTillRoll = time;
            return this;
        }

        public ResponseBuilder setPreviousResultInt(Integer previousResultInt) {
            this.previousResultInt = previousResultInt;
            return this;
        }

        public ResponseDto build() {
            return new ResponseDto(this);
        }
    }
}
