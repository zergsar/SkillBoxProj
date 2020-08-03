package main.controllers;

import main.model.Post;
import main.model.PostRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostController {

  private final PostRepository postRepository;

  public PostController(PostRepository postRepository) {
    this.postRepository = postRepository;
  }


  @GetMapping("/api/post")
  public Iterable<Post> getPost() {
    Iterable<Post> posts = postRepository.findAll();

    return posts;
  }


}
