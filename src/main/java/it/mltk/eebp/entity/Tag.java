package it.mltk.eebp.entity;

import lombok.Data;

/**
 * Created by mateusz on 09.07.2017.
 */
public @Data
class Tag {
    private String name;

    public Tag(String name) {
        this.name = name;
    }

}
