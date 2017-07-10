package it.mltk.eebp.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by mateusz on 10.07.2017.
 */
public class StyleHelper {

    private final static List<String> list = Arrays.asList("post-category-design", "post-category-pure", "post-category-yui", "post-category-js");

    public static String getRandomPostCategoryStyle() {
        Random r = new Random();
        return list.get(r.nextInt(4));
    }
}
