package main.model.repository;

import java.util.List;
import main.model.Tag;
import main.model.Tag2Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface Tag2PostRepository extends CrudRepository<Tag2Post, Integer> {

  List<Tag2Post> findAllPostIdByTagId(Tag tagId);

  @Query(value = "SELECT sum(if(tag2post.tag_id is null, 0, 1))"
      + " FROM tag2post"
      + " LEFT JOIN posts ON posts.id = tag2post.post_id"
      + " WHERE posts.is_active = 1 AND posts.moderation_status = 'ACCEPTED' AND posts.time <= NOW()"
      + " AND tag2post.tag_id = :tagId"
      + " GROUP BY tag2post.tag_id", nativeQuery = true)
  Integer countPostIdByTagId(@Param("tagId") Tag tagId);

}
