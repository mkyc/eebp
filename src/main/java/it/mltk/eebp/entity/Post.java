package it.mltk.eebp.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Created by mateusz on 09.07.2017.
 */
@Document
@CompoundIndexes({
        @CompoundIndex(def = "{'title':'text', 'content':'text'}")
})
public @Data class Post {
    @Id
    private String id;
    private String title;
    private String content;
    private ArrayList<Tag> tags;
    private String author;
    private int year;
    private int month;
    private int day;
    private LocalTime timestamp;
    private String urlTitle;

    public Post(String title, String content, ArrayList<Tag> tags, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.tags = tags;
        LocalDateTime ldt = LocalDateTime.now();
        LocalTime lt = ldt.toLocalTime();
        LocalDate ld = ldt.toLocalDate();
        this.timestamp = lt;
        this.year = ld.getYear();
        this.month = ld.getMonthValue();
        this.day = ld.getDayOfMonth();
        this.urlTitle = title.replaceAll("\\W", "");
    }
}
