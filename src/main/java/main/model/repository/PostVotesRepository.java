package main.model.repository;

import java.util.List;
import java.util.Optional;
import main.model.Post;
import main.model.PostVotes;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostVotesRepository extends CrudRepository<PostVotes, Integer> {

  @Query("SELECT COUNT(*) FROM PostVotes as pv WHERE pv.postId = :id AND pv.value = 1")
  int getCountLikePost(@Param("id") Post post);

  @Query("SELECT COUNT(*) FROM PostVotes as pv WHERE pv.postId = :id AND pv.value = -1")
  int getDislikePost(@Param("id") Post post);

  @Query(value = "SELECT count(*) FROM post_votes WHERE value = 1;", nativeQuery = true)
  Integer getTotalLikesCount();

  @Query(value = "SELECT count(*) FROM post_votes WHERE value = -1;", nativeQuery = true)
  Integer getTotalDislikesCount();

  @Query("SELECT count(*) FROM PostVotes as pv WHERE pv.postId in :ids AND pv.value = 1")
  Integer getLikesCountByPostIds(@Param("ids") List<Integer> ids);

  @Query("SELECT count(*) FROM PostVotes as pv WHERE pv.postId in :ids AND pv.value = -1")
  Integer getDislikesCountByPostIds(@Param("ids") List<Integer> ids);

  @Query(value = "SELECT * FROM post_votes WHERE post_id = :postId AND user_id = :userId AND value = :value"
      , nativeQuery = true)
  Optional<PostVotes> findVotePostByUserId(@Param("postId") int postId,
      @Param("userId") int userId, @Param("value") int value);




}
