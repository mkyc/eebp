package it.mltk.eebp.repo;

import it.mltk.eebp.entity.Tag;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by mateusz on 10.07.2017.
 */
public interface TagRepository extends MongoRepository<Tag, String> {

    Tag findByName(String name);
}
