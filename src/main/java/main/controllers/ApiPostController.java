package main.controllers;

import main.model.Post;
import main.model.PostRepository;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiPostController {

    @Autowired
    private PostRepository postRepository;


    @GetMapping("/api/post")
    public Iterable<Post> getPost()
    {
        Iterable<Post> posts = postRepository.findAll();

        return posts;
    }



}
