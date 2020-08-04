package main.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import main.controllers.response.PostInfoResponse;
import main.controllers.response.PostUserInfoResponse;
import main.controllers.response.ResponsePost;
import main.model.Post;
import main.model.PostRepository;
import main.model.User;
import main.model.UserRepository;
import main.utils.DateHandler;
import org.springframework.stereotype.Service;

@Service
public class PostService {

  private final PostRepository postRepository;
  private final UserRepository userRepository;

  public PostService(PostRepository postRepository, UserRepository userRepository) {
    this.postRepository = postRepository;
    this.userRepository = userRepository;
  }


  public ResponsePost getAllPosts(int offset, int limit, String mode) {

    ResponsePost responsePost = new ResponsePost();
    List<Post> posts = new ArrayList<>();

    switch (mode) {
      case "recent":
        posts = postRepository.getRecentPosts(limit, offset);
        break;

      case "popular":
        posts = postRepository.getPopularPosts(limit, offset);
        break;

      case "best":
        posts = postRepository.getBestPosts(limit, offset);
        break;

      case "early":
        posts = postRepository.getEarlyPosts(limit, offset);
        break;
    }

    ArrayList<PostInfoResponse> postArrayList = collectInfoForResponse(posts);

    responsePost.setCount(postRepository.getCountVisiblePosts());
    responsePost.setPosts(postArrayList);

    return responsePost;
  }


  private ArrayList<PostInfoResponse> collectInfoForResponse(List<Post> posts) {
    PostInfoResponse postInfoResponse;
    PostUserInfoResponse postUserInfoResponse;
    ArrayList<PostInfoResponse> postsList = new ArrayList<>();

    if(posts.isEmpty())
    {
      return postsList;
    }

    for (Post post : posts) {

      Optional<User> user = userRepository.findById(post.getUserId().getId());
      postInfoResponse = new PostInfoResponse();
      postUserInfoResponse = new PostUserInfoResponse();

      postInfoResponse.setId(post.getId());
      postInfoResponse.setTimestamp(post.getTime().getTimeInMillis() / 1000);

      if (user.isPresent()) {
        postUserInfoResponse.setId(user.get().getId());
        postUserInfoResponse.setName(user.get().getName());
        postInfoResponse.setUser(postUserInfoResponse);
      }

      postInfoResponse.setTitle(post.getTitle());
      postInfoResponse.setAnnounce(post.getText());
      postInfoResponse.setLikeCount(postRepository.getLikePost(post.getId()));
      postInfoResponse.setDislikeCount(postRepository.getDislikePost(post.getId()));
      postInfoResponse.setViewCount(post.getViewCount());
      postInfoResponse.setCommentCount(postRepository.getCommentCount(post.getId()));

      postsList.add(postInfoResponse);

    }
    return postsList;
  }

  public ResponsePost getSearchPost(int offset, int limit, String query) {
    ResponsePost responsePost = new ResponsePost();
    ArrayList<Post> posts = postRepository.getFoundPosts(limit, offset, query);
    ArrayList<PostInfoResponse> postArrayList = collectInfoForResponse(posts);

    responsePost.setCount(postArrayList.size());
    responsePost.setPosts(postArrayList);

    return responsePost;
  }

  public ResponsePost getPostByDate(int offset, int limit, String dateString)
  {
    ResponsePost responsePost = new ResponsePost();
    Calendar date = DateHandler.getDateFromString(dateString);
    ArrayList<Post> posts = postRepository.getPostsOnDate(limit, offset, date);
    ArrayList<PostInfoResponse> postArrayList = collectInfoForResponse(posts);

    responsePost.setCount(postArrayList.size());
    responsePost.setPosts(postArrayList);

    return responsePost;

  }




}
