package it.mltk.eebp.entity;

import it.mltk.eebp.utils.StyleHelper;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.security.NoSuchAlgorithmException;

/**
 * Created by mateusz on 09.07.2017.
 */
@Document
public @Data class Tag {



    private String id;
    private String name;
    private String style;

    public Tag(String name) {

        this.name = name;
        try {
            this.style = StyleHelper.getRandomPostCategoryStyle(name);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

}
