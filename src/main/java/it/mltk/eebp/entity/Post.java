package it.mltk.eebp.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Created by mateusz on 09.07.2017.
 */
@Document
@CompoundIndexes({
        @CompoundIndex(def = "{'title':'text', 'content':'text'}")
})
public class Post {
    @Id
    private String id;
    private String title;
    private String content;
    private ArrayList<Tag> tags;
    private String author;
    private int year;
    private int month;
    private int day;

    public Post(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
        LocalDate ld = LocalDate.now();
        this.year = ld.getYear();
        this.month = ld.getMonthValue();
        this.day = ld.getDayOfMonth();
    }

    public Post(String title, String content, ArrayList<Tag> tags, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.tags = tags;
        LocalDate ld = LocalDate.now();
        this.year = ld.getYear();
        this.month = ld.getMonthValue();
        this.day = ld.getDayOfMonth();
    }

    public Post() {}

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", author='" + author + '\'' +
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                '}';
    }
}
