package main.controllers;

import javax.servlet.http.HttpSession;
import main.api.request.CommentToPostRequest;
import main.api.request.CreateUpdatePostRequest;
import main.api.request.DecisionToPostRequest;
import main.api.request.PostVoteRequest;
import main.api.response.ResponseResult;
import main.api.response.comment.ResponseCommentToPost;
import main.api.response.post.ResponseCreateUpdatePost;
import main.api.response.post.ResponseImageUpload;
import main.api.response.post.ResponsePost;
import main.api.response.post.ResponsePostCalendar;
import main.api.response.post.ResponsePostDetails;
import main.api.response.tag.ResponseTags;
import main.model.enums.VoteType;
import main.service.CommentService;
import main.service.PostService;
import main.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class PostController {

  private final PostService postService;
  private final TagService tagService;
  private final CommentService commentService;

  public PostController(PostService postService, TagService tagService,
      CommentService commentService) {
    this.postService = postService;
    this.tagService = tagService;
    this.commentService = commentService;
  }

  @GetMapping("/api/post")
  @Transactional(readOnly = true)
  public ResponseEntity<ResponsePost> getPost(@RequestParam("offset") int offset,
      @RequestParam("limit") int limit, @RequestParam("mode") String mode) {
    return new ResponseEntity<>(postService.getAllPosts(offset, limit, mode), HttpStatus.OK);
  }

  @GetMapping("/api/post/search")
  @Transactional(readOnly = true)
  public ResponseEntity<ResponsePost> search(@RequestParam("offset") int offset,
      @RequestParam("limit") int limit, @RequestParam("query") String query) {
    return new ResponseEntity<>(postService.getSearchPost(offset, limit, query), HttpStatus.OK);
  }

  @GetMapping("/api/post/byDate")
  @Transactional(readOnly = true)
  public ResponseEntity<ResponsePost> postByDate(@RequestParam("offset") int offset,
      @RequestParam("limit") int limit, @RequestParam("date") String date) {
    return new ResponseEntity<>(postService.getPostByDate(offset, limit, date), HttpStatus.OK);
  }

  @GetMapping("/api/post/{id}")
  @Transactional
  public ResponseEntity<ResponsePostDetails> postDetails(@PathVariable int id,
      HttpSession httpSession) {
    String sessionId = httpSession.getId();
    ResponsePostDetails responsePostDetails = postService.getPostDetails(id, sessionId);
    if (responsePostDetails == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    postService.postViewsCounter(id, sessionId);
    return new ResponseEntity<>(responsePostDetails, HttpStatus.OK);
  }

  @GetMapping("/api/post/byTag")
  @Transactional(readOnly = true)
  public ResponseEntity<ResponsePost> postByTag(@RequestParam("offset") int offset,
      @RequestParam("limit") int limit, @RequestParam("tag") String tag) {
    return new ResponseEntity<>(postService.getPostsByTag(offset, limit, tag), HttpStatus.OK);
  }

  @GetMapping("/api/calendar")
  @Transactional(readOnly = true)
  public ResponseEntity<ResponsePostCalendar> postCalendar(
      @RequestParam(name = "year", required = false) String year) {
    return new ResponseEntity<>(postService.getCalendarPosts(year), HttpStatus.OK);
  }

  @GetMapping("/api/post/moderation")
  @PreAuthorize("hasAuthority('user:moderate')")
  @Transactional(readOnly = true)
  public ResponseEntity<ResponsePost> moderationPost(@RequestParam("offset") int offset,
      @RequestParam("limit") int limit, @RequestParam("status") String status,
      HttpSession httpSession) {
    String sessionId = httpSession.getId();
    return new ResponseEntity<>(postService.getModerationPost(offset, limit, status, sessionId),
        HttpStatus.OK);
  }


  @PostMapping("/api/moderation")
  @PreAuthorize("hasAuthority('user:moderate')")
  @Transactional
  public ResponseEntity<ResponseResult> decisionToPost(
      @RequestBody DecisionToPostRequest decisionToPostRequest,
      HttpSession httpSession) {
    String sessionId = httpSession.getId();
    return new ResponseEntity<>(postService.getDecisionToPost(sessionId, decisionToPostRequest),
        HttpStatus.OK);
  }


  @GetMapping("/api/post/my")
  @PreAuthorize("hasAuthority('user:write')")
  @Transactional
  public ResponseEntity<ResponsePost> userPost(@RequestParam("offset") int offset,
      @RequestParam("limit") int limit, @RequestParam("status") String status,
      HttpSession httpSession) {
    String sessionId = httpSession.getId();
    return new ResponseEntity<>(postService.getUserPost(offset, limit, status, sessionId),
        HttpStatus.OK);
  }

  @PostMapping("/api/post")
  @PreAuthorize("hasAuthority('user:write')")
  @Transactional
  public ResponseEntity<ResponseCreateUpdatePost> createPost(HttpSession httpSession,
      @RequestBody CreateUpdatePostRequest createUpdatePostRequest) {
    String sessionId = httpSession.getId();
    return new ResponseEntity<>(postService.createNewPost(sessionId, createUpdatePostRequest),
        HttpStatus.OK);
  }

  @PostMapping("/api/image")
  @PreAuthorize("hasAuthority('user:write')")
  @Transactional
  public ResponseEntity uploadImage(@RequestParam("image") MultipartFile image) {
    ResponseImageUpload responseImageUpload = postService.uploadImageToPost(image);
    if (responseImageUpload.isResult()) {
      return new ResponseEntity(responseImageUpload.getPathToImage(), HttpStatus.OK);
    }
    return new ResponseEntity(responseImageUpload, HttpStatus.BAD_REQUEST);
  }

  @PutMapping("api/post/{id}")
  @PreAuthorize("hasAuthority('user:write')")
  @Transactional
  public ResponseEntity<ResponseCreateUpdatePost> editPost(@PathVariable int id,
      HttpSession httpSession, @RequestBody CreateUpdatePostRequest createUpdatePostRequest) {
    String sessionId = httpSession.getId();
    return new ResponseEntity<>(postService.getEditPost(id, sessionId, createUpdatePostRequest),
        HttpStatus.OK);
  }

  @PostMapping("/api/comment")
  @PreAuthorize("hasAuthority('user:write')")
  @Transactional
  public ResponseEntity<ResponseCommentToPost> commentPost(
      @RequestBody CommentToPostRequest commentToPostRequest, HttpSession httpSession) {
    String sessionId = httpSession.getId();
    ResponseCommentToPost responseCommentToPost = commentService
        .addCommentToPost(commentToPostRequest, sessionId);
    if (responseCommentToPost == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
    if (!responseCommentToPost.isResult() && responseCommentToPost.getErrors() == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
    return new ResponseEntity<>(responseCommentToPost, HttpStatus.OK);
  }

  @GetMapping("/api/tag")
  @Transactional(readOnly = true)
  public ResponseEntity<ResponseTags> tagsWeight(
      @RequestParam(name = "query", required = false, defaultValue = "") String query) {
    return new ResponseEntity<>(tagService.getTagsAndWeights(query), HttpStatus.OK);
  }

  @PostMapping("/api/post/like")
  @PreAuthorize("hasAuthority('user:write')")
  @Transactional
  public ResponseEntity<ResponseResult> likePost(HttpSession httpSession, @RequestBody
      PostVoteRequest postVoteRequest) {
    String sessionId = httpSession.getId();
    ResponseResult rr = postService.getVotesForPost(sessionId, postVoteRequest, VoteType.LIKE);
    if (rr == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
    return new ResponseEntity<>(rr, HttpStatus.OK);
  }


  @PostMapping("/api/post/dislike")
  @PreAuthorize("hasAuthority('user:write')")
  @Transactional
  public ResponseEntity<ResponseResult> dislikePost(HttpSession httpSession, @RequestBody
      PostVoteRequest postVoteRequest) {
    String sessionId = httpSession.getId();
    ResponseResult rr = postService.getVotesForPost(sessionId, postVoteRequest, VoteType.DISLIKE);
    if (rr == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
    return new ResponseEntity<>(rr, HttpStatus.OK);
  }


}
