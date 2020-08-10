package main.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.servlet.http.HttpSession;
import main.controllers.response.PostCommentsResponse;
import main.controllers.response.PostInfoResponse;
import main.controllers.response.PostUserInfoResponse;
import main.controllers.response.ResponseCalendar;
import main.controllers.response.ResponsePost;
import main.controllers.response.ResponsePostDetails;
import main.model.Post;
import main.model.PostComments;
import main.model.PostCommentsRepository;
import main.model.PostRepository;
import main.model.PostVotesRepository;
import main.model.User;
import main.model.UserRepository;
import main.model.cache.RedisCache;
import main.model.enums.UserInfoWithPhoto;
import main.utils.DateHandler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PostService {

  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final PostVotesRepository postVotesRepository;
  private final PostCommentsRepository postCommentsRepository;
  private final TagService tagService;
  private final RedisCache redisCache;

  public PostService(PostRepository postRepository, UserRepository userRepository,
      PostVotesRepository postVotesRepository,
      PostCommentsRepository postCommentsRepository, TagService tagService,
      RedisCache redisCache) {
    this.postRepository = postRepository;
    this.userRepository = userRepository;
    this.postVotesRepository = postVotesRepository;
    this.postCommentsRepository = postCommentsRepository;
    this.tagService = tagService;
    this.redisCache = redisCache;
  }


  public ResponsePost getAllPosts(int offset, int limit, String mode) {

    ResponsePost responsePost = new ResponsePost();
    ArrayList<PostInfoResponse> postInfoResponseList = new ArrayList<>();
    Page<Post> posts = Page.empty();

    switch (mode) {
      case "recent":
        posts = postRepository.getRecentPosts(getPagination(offset, limit));
        break;

      case "popular":
        posts = postRepository.getPopularPosts(getPagination(offset, limit));
        break;

      case "best":
        posts = postRepository.getBestPosts(getPagination(offset, limit));
        break;

      case "early":
        posts = postRepository.getEarlyPosts(getPagination(offset, limit));
        break;

      default:
        return responsePost;
    }

    if (posts.isEmpty()) {
      responsePost.setCount(0);
      responsePost.setPosts(postInfoResponseList);
      return responsePost;
    }

    return makePostResponse(posts);
  }

  public ResponsePost getPostsByTag(int offset, int limit, String tagName) {
    ResponsePost responsePost = new ResponsePost();
    ArrayList<PostInfoResponse> postInfoResponseList = new ArrayList<>();

    List<Integer> postIdsList = tagService.getFoundByTagIdPosts(tagName);

    if (postIdsList.size() == 0) {
      responsePost.setCount(0);
      responsePost.setPosts(postInfoResponseList);
      return responsePost;
    }

    Page<Post> posts = postRepository
        .getAllAllowedPostsByIds(getPagination(offset, limit), postIdsList);

    return makePostResponse(posts);
  }

  public ResponsePostDetails getPostDetails(int id) {
    Optional<Post> postOptional = postRepository.getAllowedPostById(id);

    if (postOptional.isEmpty()) {
      return null;
    }

    Post post = postOptional.get();
    int userId = post.getUserId().getId();
    ResponsePostDetails responsePostDetails = new ResponsePostDetails();

    responsePostDetails.setId(id);
    responsePostDetails.setActive(post.getIsActive() == 1);
    responsePostDetails.setTimestamp(post.getTime().getTimeInMillis() / 1000);
    responsePostDetails.setUser(getUserInfoResponse(userId, UserInfoWithPhoto.NO));
    responsePostDetails.setTitle(post.getTitle());
    responsePostDetails.setText(post.getText());
    responsePostDetails.setLikeCount(postVotesRepository.getLikePost(post));
    responsePostDetails.setDislikeCount(postVotesRepository.getDislikePost(post));
    responsePostDetails.setViewCount(post.getViewCount());
    responsePostDetails.setComments(getPostComments(post));
    responsePostDetails.setTags(tagService.getPostTags(post));

    return responsePostDetails;
  }

  public void postViewsCounter(int id, HttpSession httpSession) {
    String sessionId = httpSession.getId();
    boolean isCacheSession = redisCache.isCacheSession(sessionId);
    Post post;

    Optional<Post> postOptional = postRepository.findById(id);

    if (isCacheSession && postOptional.isPresent()) {
      Integer userId = redisCache.findUserIdBySessionId(sessionId);
      Optional<User> userOptional = userRepository.findById(userId);

      if (userOptional.isPresent()) {
        post = postOptional.get();
        User user = userOptional.get();

        boolean isModerator = user.isModerator() == 1;
        boolean isUserOwnerPost = post.getUserId().getId() == user.getId();
        if (!isModerator && !isUserOwnerPost) {
          post.setViewCount(post.getViewCount() + 1);
          postRepository.save(post);
        }
      }
    } else if (postOptional.isPresent()) {
      post = postOptional.get();
      post.setViewCount(post.getViewCount() + 1);
      postRepository.save(post);
    }
  }

  public ResponseCalendar getCalendarPosts(String yearStr) {
    ResponseCalendar responseCalendar = new ResponseCalendar();
    HashMap<String, Integer> postDateCount = new HashMap<>();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    int year =
        yearStr.isEmpty() ? Calendar.getInstance().get(Calendar.YEAR) : Integer.parseInt(yearStr);

    HashSet<Integer> yearsSet = new HashSet<>(postRepository.getAllYearsCreatingPosts());
    List<Post> posts = postRepository.getAllVisiblePostsOnYear(year);

    for (Post post : posts) {
      Calendar date = post.getTime();
      String dateStr = sdf.format(date.getTime());
      postDateCount.computeIfPresent(dateStr, (key, value) -> value + 1);
      postDateCount.putIfAbsent(dateStr, 1);
    }

    responseCalendar.setYears(yearsSet);
    responseCalendar.setPosts(postDateCount);
    return responseCalendar;
  }


  public ResponsePost getSearchPost(int offset, int limit, String query) {
    Page<Post> posts = postRepository.getFoundPosts(getPagination(offset, limit), query);
    return makePostResponse(posts);
  }

  public ResponsePost getPostByDate(int offset, int limit, String dateString) {
    Page<Post> posts = postRepository.getPostsOnDate(getPagination(offset, limit), dateString);
    return makePostResponse(posts);
  }

  private ResponsePost makePostResponse(Page<Post> posts) {
    int totalPostsCount = (int) posts.getTotalElements();
    ArrayList<PostInfoResponse> postArrayList = collectPostsInfoForResponse(posts);

    ResponsePost responsePost = new ResponsePost();
    responsePost.setCount(totalPostsCount);
    responsePost.setPosts(postArrayList);
    return responsePost;
  }

  private Pageable getPagination(int offset, int limit) {
    return PageRequest.of(offset / limit, limit);
  }


  private ArrayList<PostInfoResponse> collectPostsInfoForResponse(Iterable<Post> posts) {
    PostInfoResponse postInfoResponse;
    ArrayList<PostInfoResponse> postsList = new ArrayList<>();

    for (Post post : posts) {

      int userId = post.getUserId().getId();
      postInfoResponse = new PostInfoResponse();

      postInfoResponse.setId(post.getId());
      postInfoResponse.setTimestamp(post.getTime().getTimeInMillis() / 1000);
      postInfoResponse.setUser(getUserInfoResponse(userId, UserInfoWithPhoto.NO));
      postInfoResponse.setTitle(post.getTitle());
      postInfoResponse.setAnnounce(post.getText());
      postInfoResponse.setLikeCount(postVotesRepository.getLikePost(post));
      postInfoResponse.setDislikeCount(postVotesRepository.getDislikePost(post));
      postInfoResponse.setViewCount(post.getViewCount());
      postInfoResponse.setCommentCount(postCommentsRepository.getCommentCount(post));

      postsList.add(postInfoResponse);

    }
    return postsList;
  }

  private PostUserInfoResponse getUserInfoResponse(int userId, UserInfoWithPhoto withPhoto) {
    PostUserInfoResponse postUserInfoResponse = new PostUserInfoResponse();
    Optional<User> userOptional = userRepository.findById(userId);

    if (userOptional.isPresent()) {
      User user = userOptional.get();
      postUserInfoResponse.setId(user.getId());
      postUserInfoResponse.setName(user.getName());
      if (withPhoto.name().equals("YES")) {
        postUserInfoResponse.setPhoto(user.getPhoto());
      }
    }

    return postUserInfoResponse;
  }

  private Set<PostCommentsResponse> getPostComments(Post post) {
    Set<PostCommentsResponse> listPostComments = new HashSet<>();
    List<PostComments> postComments = postCommentsRepository.getCommentsById(post);

    if (postComments.isEmpty()) {
      return listPostComments;
    }

    for (PostComments pc : postComments) {
      PostCommentsResponse postCommentsResponse = new PostCommentsResponse();
      int userId = pc.getUserId().getId();
      postCommentsResponse.setId(pc.getId());
      postCommentsResponse.setText(pc.getText());
      postCommentsResponse.setTimestamp(pc.getTime().getTimeInMillis() / 1000);
      postCommentsResponse.setUser(getUserInfoResponse(userId, UserInfoWithPhoto.YES));
      listPostComments.add(postCommentsResponse);
    }

    return listPostComments;
  }

}
