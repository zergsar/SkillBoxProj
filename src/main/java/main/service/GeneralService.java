package main.service;

import java.util.List;
import main.api.response.settings.ResponseGeneralSettings;
import main.api.response.statistics.ResponseAllBlogStatistics;
import main.model.repository.GlobalSettingsRepository;
import main.model.repository.PostRepository;
import main.model.repository.PostVotesRepository;
import org.springframework.stereotype.Service;

@Service
public class GeneralService {

  private final GlobalSettingsRepository globalSettingsRepository;
  private final PostRepository postRepository;
  private final PostVotesRepository postVotesRepository;

  public GeneralService(GlobalSettingsRepository globalSettingsRepository,
      PostRepository postRepository, PostVotesRepository postVotesRepository) {
    this.globalSettingsRepository = globalSettingsRepository;
    this.postRepository = postRepository;
    this.postVotesRepository = postVotesRepository;
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

}
