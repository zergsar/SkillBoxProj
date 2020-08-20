package main.service;

import main.model.Post;
import main.model.PostComments;
import main.model.User;
import main.model.repository.PostCommentsRepository;
import main.model.repository.PostRepository;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

  private final PostCommentsRepository postCommentsRepository;
  private final PostRepository postRepository;

  public CommentService(PostCommentsRepository postCommentsRepository,
      PostRepository postRepository) {
    this.postCommentsRepository = postCommentsRepository;
    this.postRepository = postRepository;
  }

  public PostComments addingCommentToPost(Post post, Integer parentPostCommentsId, String textComment, User user){
    PostComments postComments;
    if(parentPostCommentsId == null){
      postComments = new PostComments(textComment, post, user);
    }
    else{
      PostComments parentPostComments = postCommentsRepository.findById(parentPostCommentsId).get();
      postComments = new PostComments(textComment, post, parentPostComments, user);
    }
    return postCommentsRepository.save(postComments);
  }


}
