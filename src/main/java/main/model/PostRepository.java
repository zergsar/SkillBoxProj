package main.model;

import java.util.ArrayList;
import java.util.Calendar;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends CrudRepository<Post, Integer> {

  @Query(value = "SELECT * FROM posts WHERE is_active = 1"
      + " AND moderation_status = 'ACCEPTED' AND time <= now() ORDER BY time DESC"
      + " LIMIT :limit OFFSET :offset",
      nativeQuery = true)
  ArrayList<Post> getRecentPosts(@Param("limit") int limit, @Param("offset") int offset);

  @Query(value = "SELECT * FROM posts WHERE is_active = 1"
      + " AND moderation_status = 'ACCEPTED' AND time <= now() ORDER BY time ASC"
      + " LIMIT :limit OFFSET :offset",
      nativeQuery = true)
  ArrayList<Post> getEarlyPosts(@Param("limit") int limit, @Param("offset") int offset);

  @Query(value = "SELECT * FROM posts WHERE is_active = 1"
      + " AND moderation_status = 'ACCEPTED' AND time <= now()"
      + " ORDER BY (SELECT count(*) FROM post_comments WHERE post_id = posts.id) DESC"
      + " LIMIT :limit OFFSET :offset",
      nativeQuery = true)
  ArrayList<Post> getPopularPosts(@Param("limit") int limit, @Param("offset") int offset);

  @Query(value = "SELECT * FROM posts WHERE is_active = 1"
      + " AND moderation_status = 'ACCEPTED' AND time <= now()"
      + " ORDER BY (SELECT count(*) FROM post_votes WHERE post_id = posts.id AND value = 1) DESC"
      + " LIMIT :limit OFFSET :offset",
      nativeQuery = true)
  ArrayList<Post> getBestPosts(@Param("limit") int limit, @Param("offset") int offset);

  @Query(value = "SELECT COUNT(*) FROM blogdb.posts LEFT JOIN post_votes "
      + "ON posts.id = post_votes.post_id WHERE posts.id = :id AND post_votes.value = 1",
      nativeQuery = true)
  int getLikePost(int id);

  @Query(value = "SELECT COUNT(*) FROM blogdb.posts LEFT JOIN post_votes "
      + "ON posts.id = post_votes.post_id WHERE posts.id = :id AND post_votes.value = -1",
      nativeQuery = true)
  int getDislikePost(int id);

  @Query(value = "SELECT COUNT(*) FROM posts WHERE is_active = 1 "
      + "AND moderation_status = 'ACCEPTED' AND time <= now()",
      nativeQuery = true)
  int getCountVisiblePosts();

  @Query(value = "SELECT COUNT(*) FROM blogdb.posts LEFT JOIN post_comments "
      + "ON posts.id = post_comments.post_id WHERE post_comments.post_id = :id",
      nativeQuery = true)
  int getCommentCount(int id);

  @Query(value = "SELECT * FROM posts WHERE is_active = 1"
      + " AND moderation_status = 'ACCEPTED' AND time <= now()"
      + " AND posts.title LIKE CONCAT ('%', :query, '%')"
      + " OR posts.text LIKE CONCAT ('%', :query, '%')"
      + " LIMIT :limit OFFSET :offset",
      nativeQuery = true)
  ArrayList<Post> getFoundPosts(@Param("limit") int limit, @Param("offset") int offset,
      @Param("query") String query);

  @Query(value = "SELECT * FROM posts WHERE is_active = 1"
      + " AND moderation_status = 'ACCEPTED' AND time <= now() AND time = :date"
      + " LIMIT :limit OFFSET :offset",
      nativeQuery = true)
  ArrayList<Post> getPostsOnDate(@Param("limit") int limit, @Param("offset") int offset,
      @Param("date") Calendar date);

}
