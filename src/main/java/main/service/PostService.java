package main.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import main.api.request.DecisionToPostRequest;
import main.api.request.CreateUpdatePostRequest;
import main.api.request.PostVoteRequest;
import main.api.response.post.PostCommentsResponse;
import main.api.response.post.PostCreatingErrorsResponse;
import main.api.response.post.PostImageErrorsResponse;
import main.api.response.post.PostInfoResponse;
import main.api.response.post.PostUserInfoResponse;
import main.api.response.post.ResponseCreateUpdatePost;
import main.api.response.post.ResponseImageUpload;
import main.api.response.post.ResponsePost;
import main.api.response.post.ResponsePostCalendar;
import main.api.response.post.ResponsePostDetails;
import main.api.response.ResponseResult;
import main.model.Post;
import main.model.PostComments;
import main.model.PostVotes;
import main.model.Tag;
import main.model.Tag2Post;
import main.model.User;
import main.model.cache.RedisCache;
import main.model.enums.ModerationStatus;
import main.model.enums.UserInfoWithPhoto;
import main.model.enums.VoteType;
import main.model.repository.PostCommentsRepository;
import main.model.repository.PostRepository;
import main.model.repository.PostVotesRepository;
import main.model.repository.UserRepository;
import main.utils.FileUtils;
import main.utils.Generator;
import main.utils.TextHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PostService {

  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final PostVotesRepository postVotesRepository;
  private final PostCommentsRepository postCommentsRepository;
  private final TagService tagService;
  private final RedisCache redisCache;

  @Value("${subdir.name.length}")
  private int subdirNameLength;
  @Value("${subdir.depth}")
  private int subdirDepth;
  @Value("${default.upload.dir}")
  private String defaultUploadDir;
  @Value("${image.upload.max.size}")
  private int sizeImageMb;


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
    Page<Post> posts;

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

  public ResponsePostDetails getPostDetails(int id, String sessionId) {
    Optional<Post> postOptional = postRepository.findById(id);
    ResponsePostDetails responsePostDetails = new ResponsePostDetails();

    if (postOptional.isEmpty()) {
      return null;
    }

    Post post = postOptional.get();
    boolean isCacheSession = redisCache.isCacheSession(sessionId);
    boolean isPostVisible = post.getIsActive() == 1;
    boolean isPostTimeBeforeCurrentTime = post.getTime().before(Calendar.getInstance());
    boolean isAllowedStatus = post.getModerationStatus().equals(ModerationStatus.ACCEPTED);

    if (isPostTimeBeforeCurrentTime && isAllowedStatus && isPostVisible) {
      responsePostDetails = collectPostDetailsInfo(post);
    } else if (isCacheSession) {
      User user = getUserFromSession(sessionId);
      if (user == null) {
        return null;
      }
      boolean isUserAuthorOrModerator =
          user.isModerator() == 1 || post.getUserId().getId() == user.getId();

      if (isUserAuthorOrModerator) {
        responsePostDetails = collectPostDetailsInfo(post);
      }
    }

    return responsePostDetails;
  }

  public void postViewsCounter(int id, String sessionId) {
    boolean isCacheSession = redisCache.isCacheSession(sessionId);
    Post post;

    Optional<Post> postOptional = postRepository.findById(id);
    if (isCacheSession && postOptional.isPresent()) {
      User user = getUserFromSession(sessionId);
      if (user != null) {
        post = postOptional.get();
        boolean isModerator = user.isModerator() == 1;
        boolean isAuthorPost = post.getUserId().getId() == user.getId();
        if (!isModerator && !isAuthorPost) {
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

  public ResponsePostCalendar getCalendarPosts(String yearStr) {
    ResponsePostCalendar responsePostCalendar = new ResponsePostCalendar();
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

    responsePostCalendar.setYears(yearsSet);
    responsePostCalendar.setPosts(postDateCount);
    return responsePostCalendar;
  }


  public ResponsePost getSearchPost(int offset, int limit, String query) {
    Page<Post> posts = postRepository.getFoundPosts(getPagination(offset, limit), query);
    return makePostResponse(posts);
  }

  public ResponsePost getPostByDate(int offset, int limit, String dateString) {
    Page<Post> posts = postRepository.getPostsOnDate(getPagination(offset, limit), dateString);
    return makePostResponse(posts);
  }

  public ResponseCreateUpdatePost createNewPost(String sessionId,
      CreateUpdatePostRequest createUpdatePostRequest) {

    ResponseCreateUpdatePost responseCreateUpdatePost = validateCreateUpdatePost(
        createUpdatePostRequest, sessionId);
    if (responseCreateUpdatePost.isResult()) {
      saveNewPostToBase(sessionId, createUpdatePostRequest);
    }

    return responseCreateUpdatePost;
  }


  public ResponsePost getModerationPost(int offset, int limit, String status, String sessionId) {

    Optional<Integer> userId = redisCache.findUserIdBySessionId(sessionId);
    if (userId.isEmpty()) {
      System.out.println("User not found");
      return new ResponsePost();
    }
    Page<Post> posts = Page.empty();
    if (userRepository.isModerator(userId.get()) == 1) {
      posts = status.equals("new") ?
          postRepository.getPostForModeration(getPagination(offset, limit), status)
          : postRepository.getModeratedPost(getPagination(offset, limit), userId.get(), status);
    }

    return makePostResponse(posts);
  }

  public ResponseResult getDecisionToPost(String sessionId,
      DecisionToPostRequest decisionToPostRequest) {
    ResponseResult responseResult = new ResponseResult();
    boolean isResult = false;
    int postId = decisionToPostRequest.getPostId();
    Optional<Post> postOptional = postRepository.findById(postId);
    User user = getUserFromSession(sessionId);
    if (user != null && user.isModerator() == 1) {
      if (postOptional.isEmpty()) {
        responseResult.setResult(isResult);
        return responseResult;
      }
      Post post = postOptional.get();
      if (decisionToPostRequest.getDecision().equals("accept")) {
        isResult = true;
        post.setModerationStatus(ModerationStatus.ACCEPTED);
      }
      if (decisionToPostRequest.getDecision().equals("decline")) {
        isResult = true;
        post.setModerationStatus(ModerationStatus.DECLINED);
      }
      post.setModeratorId(user);
    }

    responseResult.setResult(isResult);
    return responseResult;
  }

  public ResponsePost getUserPost(int offset, int limit, String status, String sessionId) {
    ResponsePost responsePost = new ResponsePost();
    User user = getUserFromSession(sessionId);
    if (user == null) {
      System.out.println("User not found");
      return responsePost;
    }

    Page<Post> posts;

    switch (status) {
      case "inactive":
        posts = postRepository.getInactivePost(getPagination(offset, limit), user.getId());
        break;

      case "pending":
        posts = postRepository.getUserPostByStatus(getPagination(offset, limit), user.getId(),
            ModerationStatus.NEW.toString());
        break;

      case "declined":
        posts = postRepository.getUserPostByStatus(getPagination(offset, limit), user.getId(),
            ModerationStatus.DECLINED.toString());
        break;

      case "published":
        posts = postRepository.getUserPostByStatus(getPagination(offset, limit), user.getId(),
            ModerationStatus.ACCEPTED.toString());
        break;

      default:
        return responsePost;
    }
    return makePostResponse(posts);

  }

  public ResponseImageUpload uploadImageToPost(MultipartFile image) {
    ResponseImageUpload responseImageUpload = validateImage(image);
    if (!responseImageUpload.isResult()) {
      return responseImageUpload;
    }

    String subDirNames = Generator.getRandomPathToImage(subdirNameLength, subdirDepth);
    String dir =
        (defaultUploadDir.endsWith("/") ? defaultUploadDir : defaultUploadDir + "/") + subDirNames;

    responseImageUpload.setPathToImage(FileUtils.uploadFileToSubDir(dir, image).replace("\\", "/"));

    String pathToRes = FileUtils.uploadFileToSubDir(dir, image).replace("\\", "/")
        .replace(defaultUploadDir, "");
    pathToRes = pathToRes.startsWith("/") ? "/upload" + pathToRes : "/upload/" + pathToRes;
    responseImageUpload.setPathToImage(pathToRes);
    return responseImageUpload;
  }

  public ResponseCreateUpdatePost getEditPost(int id, String sessionId,
      CreateUpdatePostRequest createUpdatePostRequest) {
    ResponseCreateUpdatePost responseCreateUpdatePost = new ResponseCreateUpdatePost();
    Optional<Post> post = postRepository.findById(id);

    if (post.isEmpty()) {
      return responseCreateUpdatePost;
    }

    responseCreateUpdatePost = validateCreateUpdatePost(createUpdatePostRequest, sessionId);

    if (responseCreateUpdatePost.isResult()) {

      updatePostInBase(sessionId, createUpdatePostRequest, post.get());
    }

    return responseCreateUpdatePost;
  }

  public ResponseResult getVotesForPost(String sessionId, PostVoteRequest postVoteRequest,
      VoteType voteType) {
    ResponseResult responseResult = new ResponseResult();
    int postId = postVoteRequest.getPostId();

    User user = getUserFromSession(sessionId);
    if (user == null) {
      return null;
    }

    int userId = user.getId();

    Optional<PostVotes> revotePvOptional = VoteType.LIKE.equals(voteType) ? postVotesRepository
        .findVotePostByUserId(postId, userId, VoteType.LIKE.getValue())
        : postVotesRepository.findVotePostByUserId(postId, userId, VoteType.DISLIKE.getValue());

    Optional<PostVotes> oppositePvOptional = VoteType.LIKE.equals(voteType) ? postVotesRepository
        .findVotePostByUserId(postId, userId, VoteType.DISLIKE.getValue()) : postVotesRepository
        .findVotePostByUserId(postId, userId, VoteType.LIKE.getValue());
    Optional<Post> post = postRepository.findById(postId);

    if (post.isPresent()) {
      if (revotePvOptional.isPresent()) {
        responseResult.setResult(false);
        return responseResult;
      }
      if (oppositePvOptional.isPresent()) {
        PostVotes oppositePv = oppositePvOptional.get();
        oppositePv.setValue((byte) voteType.getValue());
        oppositePv.setTime(Calendar.getInstance());
        postVotesRepository.save(oppositePv);
        responseResult.setResult(true);
        return responseResult;
      }
      postVotesRepository.save(new PostVotes(user, post.get(), (byte) voteType.getValue()));
      responseResult.setResult(true);
      return responseResult;
    }
    return responseResult;
  }

  private ResponseCreateUpdatePost validateCreateUpdatePost(
      CreateUpdatePostRequest createUpdatePostRequest,
      String sessionId) {

    ResponseCreateUpdatePost responseCreateUpdatePost = new ResponseCreateUpdatePost();
    PostCreatingErrorsResponse postCreatingErrorsResponse = new PostCreatingErrorsResponse();
    boolean isInputInfoRight = true;

    if (createUpdatePostRequest.getTitle().length() < 3) {
      postCreatingErrorsResponse.setTitle("Заголовок не установлен");
      isInputInfoRight = false;
    }
    if (TextHandler.getTextWithoutHtml(createUpdatePostRequest.getText()).length() < 50) {
      postCreatingErrorsResponse.setText("Текст публикации слишком короткий");
      isInputInfoRight = false;
    }
    if (redisCache.findUserIdBySessionId(sessionId).isEmpty()) {
      System.out.println("Session expired, or user not loginned");
      isInputInfoRight = false;
    }

    responseCreateUpdatePost.setResult(isInputInfoRight);

    if (!isInputInfoRight) {
      responseCreateUpdatePost.setErrors(postCreatingErrorsResponse);
    }

    return responseCreateUpdatePost;
  }


  private void updatePostInBase(String sessionId,
      CreateUpdatePostRequest createUpdatePostRequest, Post post) {
    Calendar timePost = createUpdatePostRequest.getTimestamp();
    Calendar currentTime = Calendar.getInstance();

    String title = createUpdatePostRequest.getTitle();
    String text = createUpdatePostRequest.getText();
    byte isActive = createUpdatePostRequest.getActive();
    ArrayList<String> tags = createUpdatePostRequest.getTags();

    User user = getUserFromSession(sessionId);

    if (user != null) {

      if (user.isModerator() != 1) {
        post.setModerationStatus(ModerationStatus.NEW);
      }

      if (timePost.before(currentTime)) {
        timePost = currentTime;
      }

      post.setTitle(title);
      post.setText(text);
      post.setIsActive(isActive);
      post.setTime(timePost);

      postRepository.save(post);

      List<Tag2Post> tag2PostList = post.getTag2Post();
      if (tags.isEmpty()) {
        deleteTags2Post(post, tag2PostList);
      } else {
        ArrayList<String> postTagsList = tagService.getPostTags(post);
        List<String> duplicateTags = postTagsList.stream()
            .distinct()
            .filter(x -> tags.stream().anyMatch(y -> y.equals(x)))
            .collect(Collectors.toList());
        tags.removeAll(duplicateTags);
        postTagsList.clear();
        postTagsList.addAll(tags);
        postTagsList.addAll(duplicateTags);
        deleteTags2Post(post, tag2PostList);
        linkPostAndTags(postTagsList, post);
      }
    }
  }

  private void saveNewPostToBase(String sessionId,
      CreateUpdatePostRequest createUpdatePostRequest) {
    Calendar timePost = createUpdatePostRequest.getTimestamp();
    Calendar currentTime = Calendar.getInstance();

    String title = createUpdatePostRequest.getTitle();
    String text = createUpdatePostRequest.getText();
    byte isActive = createUpdatePostRequest.getActive();
    ArrayList<String> tags = createUpdatePostRequest.getTags();

    User user = getUserFromSession(sessionId);

    if (user != null) {

      if (timePost.before(currentTime)) {
        timePost = currentTime;
      }

      Post post = postRepository
          .save(new Post(timePost, isActive, title, text, user, null, ModerationStatus.NEW));

      if (!tags.isEmpty()) {
        linkPostAndTags(tags, post);
      }
    } else {
      System.out.println("user not found");
    }
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
      postInfoResponse.setAnnounce(TextHandler.getTextWithoutHtml(post.getText()));
      postInfoResponse.setLikeCount(postVotesRepository.getCountLikePost(post));
      postInfoResponse.setDislikeCount(postVotesRepository.getDislikePost(post));
      postInfoResponse.setViewCount(post.getViewCount());
      postInfoResponse.setCommentCount(postCommentsRepository.getCommentCount(post));

      postsList.add(postInfoResponse);

    }
    return postsList;
  }

  private ResponsePostDetails collectPostDetailsInfo(Post post) {
    ResponsePostDetails responsePostDetails = new ResponsePostDetails();
    responsePostDetails.setId(post.getId());
    responsePostDetails.setActive(post.getIsActive() == 1);
    responsePostDetails.setTimestamp(post.getTime().getTimeInMillis() / 1000);
    responsePostDetails
        .setUser(getUserInfoResponse(post.getUserId().getId(), UserInfoWithPhoto.NO));
    responsePostDetails.setTitle(post.getTitle());
    responsePostDetails.setText(post.getText());
    responsePostDetails.setLikeCount(postVotesRepository.getCountLikePost(post));
    responsePostDetails.setDislikeCount(postVotesRepository.getDislikePost(post));
    responsePostDetails.setViewCount(post.getViewCount());
    responsePostDetails.setComments(getPostComments(post));
    responsePostDetails.setTags(tagService.getPostTags(post));

    return responsePostDetails;
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

  private void linkPostAndTags(ArrayList<String> tags, Post post) {
    for (String tagName : tags) {
      Optional<Tag> tag = tagService.getTagByName(tagName);
      if (tag.isPresent()) {
        tagService.createTag2PostLink(post, tag.get());
      } else {
        tagService.createTag2PostLink(post, tagService.createNewTag(tagName));
      }
    }
  }

  private ResponseImageUpload validateImage(MultipartFile image) {

    ResponseImageUpload responseImageUpload = new ResponseImageUpload();
    PostImageErrorsResponse postImageErrorsResponse = new PostImageErrorsResponse();
    boolean isValidImage = true;

    if (!(image.isEmpty() || image.getSize() == 0)) {
      String contentType = image.getContentType();
      String jpgType = "image/jpeg";
      String pngType = "image/png";
      boolean isAllowedImageFormat =
          Objects.requireNonNull(contentType).equals(jpgType) || contentType.equals(pngType);

      if (image.getSize() / 1000000 > sizeImageMb) {
        isValidImage = false;
        postImageErrorsResponse.setImage("Размер файла превышает допустимый размер");
      }
      if (!isAllowedImageFormat) {
        isValidImage = false;
        postImageErrorsResponse.setImage("Недопустимый формат изображения");
      }
    } else {
      isValidImage = false;
      postImageErrorsResponse.setImage("Файл пуст");
    }

    responseImageUpload.setResult(isValidImage);
    responseImageUpload.setErrors(postImageErrorsResponse);

    return responseImageUpload;
  }

  private User getUserFromSession(String sessionId) {

    Optional<Integer> userId = redisCache.findUserIdBySessionId(sessionId);
    if (userId.isEmpty()) {
      return null;
    }
    Optional<User> userOptional = userRepository.findById(userId.get());
    if (userOptional.isEmpty()) {
      return null;
    }
    return userOptional.get();
  }

  private void deleteTags2Post(Post post, List<Tag2Post> tag2PostList) {
    post.setTag2Post(new ArrayList<>());
    tagService.deleteAllTag2PostLinks(tag2PostList);
  }

  public int getCountPostsForModeration() {
    return postRepository.getCountNewPostsForModeration();
  }

}
