package main.model;

import java.util.Calendar;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends CrudRepository<Post, Integer> {

  @Query(value = "SELECT * FROM posts WHERE is_active = 1"
      + " AND moderation_status = 'ACCEPTED' AND time <= now() ORDER BY time DESC",
      nativeQuery = true)
  Page<Post> getRecentPosts(Pageable pageable);

  @Query(value = "SELECT * FROM posts WHERE is_active = 1"
      + " AND moderation_status = 'ACCEPTED' AND time <= now() ORDER BY time ASC",
      nativeQuery = true)
  Page<Post> getEarlyPosts(Pageable pageable);

  @Query(value = "SELECT * FROM posts WHERE is_active = 1"
      + " AND moderation_status = 'ACCEPTED' AND time <= now()"
      + " ORDER BY (SELECT count(*) FROM post_comments WHERE post_id = posts.id) DESC",
      nativeQuery = true)
  Page<Post> getPopularPosts(Pageable pageable);

  @Query(value = "SELECT * FROM posts WHERE is_active = 1"
      + " AND moderation_status = 'ACCEPTED' AND time <= now()"
      + " ORDER BY (SELECT count(*) FROM post_votes WHERE post_id = posts.id AND value = 1) DESC",
      nativeQuery = true)
  Page<Post> getBestPosts(Pageable pageable);

  @Query(value = "SELECT * FROM posts WHERE is_active = 1"
      + " AND moderation_status = 'ACCEPTED' AND time <= now()"
      + " AND posts.title LIKE CONCAT ('%', :query, '%')"
      + " OR posts.text LIKE CONCAT ('%', :query, '%')",
      nativeQuery = true)
  Page<Post> getFoundPosts(Pageable pageable, @Param("query") String query);

  @Query(value = "SELECT * FROM posts WHERE is_active = 1"
      + " AND moderation_status = 'ACCEPTED' AND time <= now() AND time = :date",
      nativeQuery = true)
  Page<Post> getPostsOnDate(Pageable pageable, @Param("date") Calendar date);

  @Query("SELECT p FROM Post as p WHERE p.id = :id AND p.isActive = 1"
      + " AND p.moderationStatus = 'ACCEPTED'"
      + " AND p.time <= now()")
  Optional<Post> getAllowedPostById(@Param("id") int id);

}
