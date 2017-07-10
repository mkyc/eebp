package it.mltk.eebp.services;

import it.mltk.eebp.entity.Post;
import it.mltk.eebp.entity.Tag;
import it.mltk.eebp.repo.PostRepository;
import it.mltk.eebp.repo.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Created by mateusz on 10.07.2017.
 */
@Service
public class PostService {


    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TagRepository tagRepository;

    public Post createPost(String title, String content, String author, String... tagsList) {
        ArrayList<Tag> tags = new ArrayList<>();
        for(String tag : tagsList) {
            Tag t = tagRepository.findByName(tag);
            if(t == null) {
                t = new Tag(tag);
                t = tagRepository.save(t);
            }
            tags.add(t);
        }
        return postRepository.save(new Post(title, content, tags, author));
    }

    /**
     * Testing purposes only
     */
    public void clean() {
        postRepository.deleteAll();
        tagRepository.deleteAll();
    }
}
