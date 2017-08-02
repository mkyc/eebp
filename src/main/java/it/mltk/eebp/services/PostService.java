package it.mltk.eebp.services;

import it.mltk.eebp.repo.PostRepository;
import it.mltk.eebp.repo.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by mateusz on 10.07.2017.
 */
@Service
public class PostService {


    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TagRepository tagRepository;

    /**
     * Testing purposes only
     */
    public void clean() {
        postRepository.deleteAll();
        tagRepository.deleteAll();
    }
}
