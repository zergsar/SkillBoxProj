package main.model;

import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CaptchaCodesRepository extends CrudRepository<CaptchaCodes, Integer> {

  Optional<CaptchaCodes> findByCode(String code);

  @Modifying
  @Transactional
  @Query(value = "DELETE FROM captcha_codes WHERE "
      + "time_to_sec(timediff(now(), captcha_codes.time))/60 > :timeout",
      nativeQuery = true)
  void deleteAllOldCaptcha(@Param("timeout") int timeout);



}
