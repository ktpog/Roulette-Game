package DTO;

public class NoteDto {
    public final String note;
    public final String color;

    public NoteDto(Object note, Object color) {
        this.note = note.toString();
        this.color = color.toString();
    }
}
