package main.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.servlet.http.HttpSession;
import main.api.request.PostCreateRequest;
import main.api.response.post.PostCommentsResponse;
import main.api.response.post.PostCreatingErrorsResponse;
import main.api.response.post.PostImageErrorsResponse;
import main.api.response.post.PostInfoResponse;
import main.api.response.post.PostUserInfoResponse;
import main.api.response.post.ResponseCreatingPost;
import main.api.response.post.ResponseImageUpload;
import main.api.response.post.ResponsePost;
import main.api.response.post.ResponsePostCalendar;
import main.api.response.post.ResponsePostDetails;
import main.model.Post;
import main.model.PostComments;
import main.model.Tag;
import main.model.User;
import main.model.cache.RedisCache;
import main.model.enums.ModerationStatus;
import main.model.enums.UserInfoWithPhoto;
import main.model.repository.PostCommentsRepository;
import main.model.repository.PostRepository;
import main.model.repository.PostVotesRepository;
import main.model.repository.UserRepository;
import main.utils.FileUtils;
import main.utils.Generator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
      Integer userId = redisCache.findUserIdBySessionId(sessionId).get();
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

  public ResponseCreatingPost createNewPost(String sessionId, PostCreateRequest postCreateRequest) {

    ResponseCreatingPost responseCreatingPost = validateCreatingPost(postCreateRequest, sessionId);
    if (responseCreatingPost.isResult()) {
      saveNewPostToBase(sessionId, postCreateRequest);
    }

    return responseCreatingPost;
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

    System.out.println(responseImageUpload.getPathToImage());
    return responseImageUpload;
  }

  private ResponseCreatingPost validateCreatingPost(PostCreateRequest postCreateRequest,
      String sessionId) {

    ResponseCreatingPost responseCreatingPost = new ResponseCreatingPost();
    PostCreatingErrorsResponse postCreatingErrorsResponse = new PostCreatingErrorsResponse();
    boolean isInputInfoRight = true;

    Document doc = Jsoup.parseBodyFragment(postCreateRequest.getText());
    String text = doc.text();

    if (postCreateRequest.getTitle().length() < 3) {
      postCreatingErrorsResponse.setTitle("Заголовок не установлен");
      isInputInfoRight = false;
    }
    if (text.length() < 50) {
      postCreatingErrorsResponse.setText("Текст публикации слишком короткий");
      isInputInfoRight = false;
    }
    if (redisCache.findUserIdBySessionId(sessionId).isEmpty()) {
      System.out.println("Session expired, or user not loginned");
      isInputInfoRight = false;
    }

    responseCreatingPost.setResult(isInputInfoRight);

    if (!isInputInfoRight) {
      responseCreatingPost.setErrors(postCreatingErrorsResponse);
    }

    return responseCreatingPost;
  }

  private void saveNewPostToBase(String sessionId, PostCreateRequest postCreateRequest) {
    Calendar timePost = postCreateRequest.getTimestamp();
    Calendar currentTime = Calendar.getInstance();

    String title = postCreateRequest.getTitle();
    String text = postCreateRequest.getText();
    byte isActive = postCreateRequest.getActive();
    ArrayList<String> tags = postCreateRequest.getTags();

    Optional<Integer> userId = redisCache.findUserIdBySessionId(sessionId);

    if (userId.isPresent()) {

      Optional<User> user = userRepository.findById(userId.get());

      if (timePost.before(currentTime)) {
        timePost = currentTime;
      }

      if (user.isEmpty()) {
        throw new UsernameNotFoundException("User not found");
      }

      Post post = postRepository
          .save(new Post(timePost, isActive, title, text, user.get(), null, ModerationStatus.NEW));

      if (!tags.isEmpty()) {
        linkPostAndTags(tags, post);
      }
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
      boolean isAllowedImageFormat = contentType.equals(jpgType) || contentType.equals(pngType);

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

}
