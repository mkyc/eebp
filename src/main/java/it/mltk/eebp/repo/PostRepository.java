package it.mltk.eebp.repo;

import it.mltk.eebp.entity.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by mateusz on 09.07.2017.
 */
public interface PostRepository extends MongoRepository<Post, String> {

    public Post findByTitle(String title);
//    @Query(value = "{ 'tagss.name' : ?0 }", fields = "{ 'tags.name' : 0 }")
//    List<Post> findByTagsName(String name);

    List<Post> findAllByTagsName(String name);
}
