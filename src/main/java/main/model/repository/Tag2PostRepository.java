package main.model.repository;

import java.util.List;
import main.model.Tag;
import main.model.Tag2Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Tag2PostRepository extends CrudRepository<Tag2Post, Integer> {

  List<Tag2Post> findAllPostIdByTagId(Tag tagId);

}
