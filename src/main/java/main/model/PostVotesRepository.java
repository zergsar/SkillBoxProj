package main.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostVotesRepository extends CrudRepository<PostVotes, Integer> {

  @Query("SELECT COUNT(*) FROM PostVotes as pv WHERE pv.postId = :id AND pv.value = 1")
  int getLikePost(@Param("id") Post post);

  @Query("SELECT COUNT(*) FROM PostVotes as pv WHERE pv.postId = :id AND pv.value = -1")
  int getDislikePost(@Param("id") Post post);

}
