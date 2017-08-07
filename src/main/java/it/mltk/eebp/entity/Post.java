package it.mltk.eebp.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Created by mateusz on 09.07.2017.
 */
@Document
@NoArgsConstructor
public @Data class Post {
    @Id
    private String id;
    private String title;
    private String content;
    private ArrayList<Tag> tags;
    private String author;
    private String authorUrl;
    private String avatarUrl;
    private String sha;
    private String path;
    private int year;
    private int month;
    private int day;
    private LocalTime created;
    private LocalTime updated;
    private String urlTitle;
}
