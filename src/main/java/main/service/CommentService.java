package main.service;

import java.util.Optional;
import main.api.request.CommentToPostRequest;
import main.api.response.comment.CommentErrorsResponse;
import main.api.response.comment.ResponseCommentToPost;
import main.model.Post;
import main.model.PostComments;
import main.model.User;
import main.model.cache.RedisCache;
import main.model.repository.PostCommentsRepository;
import main.model.repository.PostRepository;
import main.model.repository.UserRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

  private final PostCommentsRepository postCommentsRepository;
  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final RedisCache redisCache;

  public CommentService(PostCommentsRepository postCommentsRepository,
      PostRepository postRepository, UserRepository userRepository,
      RedisCache redisCache) {
    this.postCommentsRepository = postCommentsRepository;
    this.postRepository = postRepository;
    this.userRepository = userRepository;
    this.redisCache = redisCache;
  }

  public PostComments addingCommentToPost(Post post, Integer parentPostCommentsId,
      String textComment, User user) {
    PostComments postComments;
    if (parentPostCommentsId == null) {
      postComments = new PostComments(textComment, post, user);
    } else {
      PostComments parentPostComments = postCommentsRepository.findById(parentPostCommentsId).get();
      postComments = new PostComments(textComment, post, parentPostComments, user);
    }
    return postCommentsRepository.save(postComments);
  }

  public ResponseCommentToPost addCommentToPost(CommentToPostRequest commentToPostRequest,
      String sessionId) {

    User user = getUserFromSession(sessionId);
    if (user == null) {
      return null;
    }
    ResponseCommentToPost responseCommentToPost = validateComment(commentToPostRequest);

    if (responseCommentToPost.isResult()) {
      Integer postId = commentToPostRequest.getPostId() == null ? null
          : Integer.parseInt(commentToPostRequest.getPostId());
      Integer parentPostCommentId = commentToPostRequest.getParentId() == null ? null
          : Integer.parseInt(commentToPostRequest.getParentId());
      Post post = postRepository.findById(postId).get();
      String textComment = commentToPostRequest.getText();
      PostComments postComments = addingCommentToPost(post, parentPostCommentId, textComment, user);
      responseCommentToPost.setId(postComments.getId());
    }
    return responseCommentToPost;
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

  private ResponseCommentToPost validateComment(CommentToPostRequest commentToPostRequest) {
    ResponseCommentToPost responseCommentToPost = new ResponseCommentToPost();
    CommentErrorsResponse commentErrorsResponse = new CommentErrorsResponse();
    boolean isResult = true;
    String textCommentWithoutHtml = getTextWithoutHtml(commentToPostRequest.getText());
    Integer postId = commentToPostRequest.getPostId() == null ? null
        : Integer.parseInt(commentToPostRequest.getPostId());
    Integer parentPostCommentId = commentToPostRequest.getParentId() == null ? null
        : Integer.parseInt(commentToPostRequest.getParentId());

    if (postId == null) {
      isResult = false;
      responseCommentToPost.setResult(isResult);
      return responseCommentToPost;
    }
    if (postRepository.findById(postId).isEmpty()) {
      isResult = false;
      responseCommentToPost.setResult(isResult);
      return responseCommentToPost;
    }
    if (parentPostCommentId != null) {
      if (postCommentsRepository.findById(parentPostCommentId).isEmpty()) {
        isResult = false;
        responseCommentToPost.setResult(isResult);
        return responseCommentToPost;
      }
    }

    if (textCommentWithoutHtml.length() < 2) {
      isResult = false;
      commentErrorsResponse.setText("Текст комментария не задан или слишком короткий");
    }

    if (!isResult) {
      responseCommentToPost.setErrors(commentErrorsResponse);
    }
    responseCommentToPost.setResult(isResult);
    return responseCommentToPost;
  }

  private String getTextWithoutHtml(String text) {
    Document doc = Jsoup.parseBodyFragment(text);
    return doc.text();
  }


}
