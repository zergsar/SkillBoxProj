package main.model.repository;

import java.util.List;
import java.util.Optional;
import main.model.Post;
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
      + " AND moderation_status = 'ACCEPTED' AND time <= now() AND DATE(time) = :date",
      nativeQuery = true)
  Page<Post> getPostsOnDate(Pageable pageable, @Param("date") String date);

  @Query("SELECT p FROM Post as p WHERE p.id = :id AND p.isActive = 1"
      + " AND p.moderationStatus = 'ACCEPTED'"
      + " AND p.time <= now()")
  Optional<Post> getAllowedPostById(@Param("id") int id);

  @Query("SELECT p FROM Post as p WHERE p.id in :ids AND p.isActive = 1"
      + " AND p.moderationStatus = 'ACCEPTED'"
      + " AND p.time <= now()")
  Page<Post> getAllAllowedPostsByIds(Pageable pageable, @Param("ids") List<Integer> ids);

  @Query(value = "SELECT * FROM posts WHERE is_active = 1"
      + " AND moderation_status = 'ACCEPTED' AND time <= now() AND YEAR(time) = :year",
      nativeQuery = true)
  List<Post> getAllVisiblePostsOnYear(@Param("year") int year);

  @Query(value = "SELECT DISTINCT(YEAR(time)) FROM posts WHERE time <= now()", nativeQuery = true)
  List<Integer> getAllYearsCreatingPosts();

  @Query(value = "SELECT * FROM posts WHERE is_active = 1"
      + " AND moderation_status = :status", nativeQuery = true)
  Page<Post> getPostForModeration(Pageable pageable, @Param("status") String status);

  @Query(value = "SELECT * FROM posts WHERE is_active = 1 AND moderation_status = :status"
      + " AND moderator_id = :id", nativeQuery = true)
  Page<Post> getModeratedPost(Pageable pageable, @Param("id") int id,
      @Param("status") String status);

  @Query(value = "SELECT * FROM posts WHERE is_active = 0 AND user_id = :id", nativeQuery = true)
  Page<Post> getInactivePost(Pageable pageable, @Param("id") int id);

  @Query(value = "SELECT * FROM posts WHERE is_active = 1 AND moderation_status = :status"
      + " AND user_id = :id", nativeQuery = true)
  Page<Post> getUserPostByStatus(Pageable pageable, @Param("id") int id,
      @Param("status") String status);

  @Query(value = "SELECT count(*) FROM posts WHERE is_active = 1 AND moderation_status = 'NEW'"
      + " AND moderator_id is null", nativeQuery = true)
  Integer getCountNewPostsForModeration();

  @Query(value = "SELECT count(*) FROM posts WHERE is_active = 1 AND moderation_status = 'ACCEPTED'"
      + " AND time <= now()", nativeQuery = true)
  Integer getCountAllVisiblePosts();




}
