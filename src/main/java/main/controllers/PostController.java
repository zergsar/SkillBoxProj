package main.controllers;

import javax.servlet.http.HttpSession;
import main.api.request.PostCreateOrEditRequest;
import main.api.response.post.ResponseCreatingOrEditPost;
import main.api.response.post.ResponseImageUpload;
import main.api.response.post.ResponsePost;
import main.api.response.post.ResponsePostCalendar;
import main.api.response.post.ResponsePostDetails;
import main.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

  public PostController(PostService postService) {
    this.postService = postService;
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
  @Transactional(readOnly = true)
  public ResponseEntity<ResponsePost> moderationPost(@RequestParam("offset") int offset,
      @RequestParam("limit") int limit, @RequestParam("status") String status, HttpSession httpSession){
    String sessionId = httpSession.getId();
    return new ResponseEntity<>(postService.getModerationPost(offset, limit, status, sessionId), HttpStatus.OK);
  }

  @GetMapping("/api/post/my")
  @Transactional
  public ResponseEntity<ResponsePost> userPost(@RequestParam("offset") int offset,
      @RequestParam("limit") int limit, @RequestParam("status") String status, HttpSession httpSession){
    String sessionId = httpSession.getId();
    return new ResponseEntity<>(postService.getUserPost(offset, limit, status, sessionId), HttpStatus.OK);
  }

  @PostMapping("/api/post")
  @Transactional
  public ResponseEntity<ResponseCreatingOrEditPost> createPost(HttpSession httpSession,
      @RequestBody PostCreateOrEditRequest postCreateOrEditRequest) {
    String sessionId = httpSession.getId();
    return new ResponseEntity<>(postService.createNewPost(sessionId, postCreateOrEditRequest),
        HttpStatus.OK);
  }

  @PostMapping("/api/image")
  @Transactional
  public ResponseEntity uploadImage(@RequestParam("image") MultipartFile image) {
    ResponseImageUpload responseImageUpload = postService.uploadImageToPost(image);
    if (responseImageUpload.isResult()) {
      return new ResponseEntity(responseImageUpload.getPathToImage(), HttpStatus.OK);
    }
    return new ResponseEntity(responseImageUpload, HttpStatus.BAD_REQUEST);
  }


  @PutMapping("api/post/{id}")
  @Transactional
  public ResponseEntity<ResponseCreatingOrEditPost> editPost(@PathVariable int id,
      HttpSession httpSession, @RequestBody PostCreateOrEditRequest postCreateOrEditRequest) {
    String sessionId = httpSession.getId();
    return new ResponseEntity<>(postService.getEditPost(id, sessionId, postCreateOrEditRequest),
        HttpStatus.OK);
  }
}
