package it.mltk.eebp.repo;

import it.mltk.eebp.entity.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * Created by mateusz on 09.07.2017.
 */
public interface PostRepository extends MongoRepository<Post, String> {

    public Post findByTitle(String title);

    @Query("{$text: {$search: ?0}}, {score: {$meta: 'textScore'}}).sort({score:{$meta:'textScore'}}")
    List<Post> findInTitleAndContent(String value);

    List<Post> findAllByTags_Name(String name);
}
