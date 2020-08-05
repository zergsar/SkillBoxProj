package main.model;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCommentsRepository extends CrudRepository<PostComments, Integer> {

  @Query("SELECT COUNT(*) FROM PostComments as pc WHERE pc.postId = :id")
  int getCommentCount(@Param("id") Post post);

  @Query("SELECT pc FROM PostComments as pc WHERE pc.postId = :id")
  List<PostComments> getCommentsById(@Param("id") Post post);


}
