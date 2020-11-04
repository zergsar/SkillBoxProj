package main.model.repository;

import main.model.GlobalSettings;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalSettingsRepository extends CrudRepository<GlobalSettings, Integer> {

  @Query("SELECT gs FROM GlobalSettings as gs WHERE gs.code = :code")
  GlobalSettings getSettingsByCode(@Param("code") String code);

}
