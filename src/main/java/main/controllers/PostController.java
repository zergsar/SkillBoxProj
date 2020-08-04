package main.controllers;

import java.util.Calendar;
import main.controllers.response.ResponsePost;
import main.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
      @RequestParam("limit") int limit, @RequestParam("date") String date){
    return new ResponseEntity<>(postService.getPostByDate(offset, limit, date), HttpStatus.OK);
  }


}
