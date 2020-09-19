package main.model.repository;

import java.util.Optional;
import main.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

  Optional<User> findByEmail(String email);

  Optional<User> findByName(String name);

  @Query("SELECT u.isModerator FROM User as u WHERE u.id = :id")
  int isModerator(@Param("id") int id);

  @Query("SELECT u FROM User as u WHERE u.code = :code")
  Optional<User> findByCode(@Param("code") String code);

}
