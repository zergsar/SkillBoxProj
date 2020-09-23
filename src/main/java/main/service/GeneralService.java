package main.service;

import java.util.List;
import java.util.Optional;
import main.api.request.PutGlobalSettingsRequest;
import main.api.response.settings.ResponseGeneralSettings;
import main.api.response.statistics.ResponseAllBlogStatistics;
import main.model.GlobalSettings;
import main.model.User;
import main.model.cache.RedisCache;
import main.model.repository.GlobalSettingsRepository;
import main.model.repository.PostRepository;
import main.model.repository.PostVotesRepository;
import main.model.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class GeneralService {

  private final GlobalSettingsRepository globalSettingsRepository;
  private final PostRepository postRepository;
  private final PostVotesRepository postVotesRepository;
  private final UserRepository userRepository;
  private final RedisCache redisCache;

  public GeneralService(GlobalSettingsRepository globalSettingsRepository,
      PostRepository postRepository, PostVotesRepository postVotesRepository,
      UserRepository userRepository, RedisCache redisCache) {
    this.globalSettingsRepository = globalSettingsRepository;
    this.postRepository = postRepository;
    this.postVotesRepository = postVotesRepository;
    this.userRepository = userRepository;
    this.redisCache = redisCache;
  }

  public ResponseGeneralSettings getSettingsFromBase() {
    ResponseGeneralSettings response = new ResponseGeneralSettings();

    globalSettingsRepository.findAll().forEach(GlobalSettings ->
    {
      boolean value = false;
      if (GlobalSettings.getValue().equals("YES")) {
        value = true;
      }
      switch (GlobalSettings.getCode()) {
        case "MULTIUSER_MODE":
          response.setMultiuserMode(value);
          break;

        case "POST_PREMODERATION":
          response.setPostPremoderation(value);
          break;

        case "STATISTICS_IS_PUBLIC":
          response.setStatisticsIsPublic(value);
          break;
      }
    });

    return response;
  }

  public ResponseAllBlogStatistics getAllBlogStatistics() {
    ResponseAllBlogStatistics rbs = new ResponseAllBlogStatistics();
    rbs.setPostsCount(postRepository.getTotalCountVisiblePosts());
    rbs.setLikesCount(postVotesRepository.getTotalLikesCount());
    rbs.setDislikesCount(postVotesRepository.getTotalDislikesCount());
    rbs.setViewsCount(postRepository.getTotalViewCount());
    Long firstPostTimestamp = postRepository.getFirstPostTimestamp();
    rbs.setFirstPublication(firstPostTimestamp == null ? 0 : firstPostTimestamp);
    return rbs;
  }

  public ResponseAllBlogStatistics getMyBlogStatistics(int id) {
    ResponseAllBlogStatistics rbs = new ResponseAllBlogStatistics();

    rbs.setPostsCount(postRepository.getCountVisiblePostsByAuthorId(id));
    List<Integer> allPostIds = postRepository.getAllIdVisiblePostsByAuthorId(id);
    rbs.setLikesCount(postVotesRepository.getLikesCountByPostIds(allPostIds));
    rbs.setDislikesCount(postVotesRepository.getDislikesCountByPostIds(allPostIds));
    rbs.setViewsCount(postRepository.getViewCountPostsByAuthorId(id));
    Long firstPostTimestamp = postRepository.getFirstPostTimestampByAuthorId(id);
    rbs.setFirstPublication(firstPostTimestamp == null ? 0 : firstPostTimestamp);
    return rbs;
  }

  public HttpStatus putSettingsInBase(String sessionId, PutGlobalSettingsRequest pgsr) {
    HttpStatus httpStatus = HttpStatus.OK;
    Optional<Integer> userIdOpt = redisCache.findUserIdBySessionId(sessionId);
    if (userIdOpt.isEmpty()) {
      return HttpStatus.UNAUTHORIZED;
    }
    Optional<User> userOpt = userRepository.findById(userIdOpt.get());
    if (userOpt.isEmpty()) {
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }
    User user = userOpt.get();
    if(user.isModerator() == 0){
      return HttpStatus.BAD_REQUEST;
    }

    GlobalSettings multiuserMode = globalSettingsRepository.getSettingsByCode("MULTIUSER_MODE");
    GlobalSettings postPremod = globalSettingsRepository.getSettingsByCode("POST_PREMODERATION");
    GlobalSettings statIsPublic = globalSettingsRepository.getSettingsByCode("STATISTICS_IS_PUBLIC");

    boolean multiuserModeBool = multiuserMode.getValue().equals("YES");
    boolean postPremodBool = postPremod.getValue().equals("YES");
    boolean statIsPublicBool = statIsPublic.getValue().equals("YES");

    if(pgsr.isMultiuserMode() != multiuserModeBool){
      multiuserMode.setValue(pgsr.isMultiuserMode() ? "YES" : "NO");
      globalSettingsRepository.save(multiuserMode);
    }
    if(pgsr.isPostPremoderation() != postPremodBool){
      postPremod.setValue(pgsr.isPostPremoderation() ? "YES" : "NO");
      globalSettingsRepository.save(postPremod);
    }
    if(pgsr.isStatisticsIsPublic() != statIsPublicBool){
      statIsPublic.setValue(pgsr.isStatisticsIsPublic() ? "YES" : "NO");
      globalSettingsRepository.save(statIsPublic);
    }


    return httpStatus;
  }

}
