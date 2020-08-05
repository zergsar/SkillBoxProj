package main.controllers;

import java.util.Optional;
import javax.servlet.http.HttpSession;
import main.controllers.response.ResponsePost;
import main.controllers.response.ResponsePostDetails;
import main.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostController {

  private final PostService postService;

  public PostController(PostService postService) {
    this.postService = postService;
  }

  @GetMapping("/api/post")
  public ResponseEntity<ResponsePost> getPost(@RequestParam("offset") int offset,
      @RequestParam("limit") int limit, @RequestParam("mode") String mode) {
    return new ResponseEntity<>(postService.getAllPosts(offset, limit, mode), HttpStatus.OK);
  }

  @GetMapping("/api/post/search")
  public ResponseEntity<ResponsePost> search(@RequestParam("offset") int offset,
      @RequestParam("limit") int limit, @RequestParam("query") String query) {
    return new ResponseEntity<>(postService.getSearchPost(offset, limit, query), HttpStatus.OK);
  }

  @GetMapping("/api/post/byDate")
  public ResponseEntity<ResponsePost> postByDate(@RequestParam("offset") int offset,
      @RequestParam("limit") int limit, @RequestParam("date") String date) {
    return new ResponseEntity<>(postService.getPostByDate(offset, limit, date), HttpStatus.OK);
  }

  @GetMapping("/api/post/{id}")
  public ResponseEntity postDetails(@PathVariable int id, HttpSession httpSession) {
    ResponsePostDetails responsePostDetails = postService.getPostDetails(id);
    if(responsePostDetails == null)
    {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
    postService.postViewsCounter(id, httpSession);
    return new ResponseEntity(postService.getPostDetails(id), HttpStatus.OK);
  }


}
