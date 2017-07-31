package it.mltk.eebp.repo;

import it.mltk.eebp.entity.Post;
import it.mltk.eebp.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by mateusz on 09.07.2017.
 */
public interface PostRepository extends MongoRepository<Post, String> {
    Post findOneByYearAndMonthAndDayAndUrlTitle(int year, int month, int day, String urlTitle);
    Page<Post> findAllByOrderByTimestampDesc(Pageable pageable);
    Page<Post> findAllByTagsOrderByTimestampDesc(Tag tag, Pageable pageable);
}
