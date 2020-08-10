package main.service;

import java.util.ArrayList;
import java.util.List;
import main.model.Post;
import main.model.Tag;
import main.model.Tag2Post;
import main.model.Tag2PostRepository;
import main.model.TagRepository;
import org.springframework.stereotype.Service;

@Service
public class TagService {

  private TagRepository tagRepository;
  private Tag2PostRepository tag2PostRepository;


  public TagService(TagRepository tagRepository, Tag2PostRepository tag2PostRepository) {
    this.tagRepository = tagRepository;
    this.tag2PostRepository = tag2PostRepository;
  }

  public List<Integer> getFoundByTagIdPosts(String tagName)
  {
    ArrayList<Integer> idPostList = new ArrayList<>();
    Tag tagId = tagRepository.findIdByName(tagName);
    List<Tag2Post> tag2Posts = tag2PostRepository.findAllPostIdByTagId(tagId);

    if (tag2Posts.isEmpty()) {
      return idPostList;
    }

    for (Tag2Post tag2Post : tag2Posts) {
      idPostList.add(tag2Post.getPostId().getId());
    }

    return idPostList;
  }

  public ArrayList<String> getPostTags(Post post) {

    ArrayList<String> tags = new ArrayList<>();
    List<Tag2Post> tagsList = post.getTag2Post();

    if (!tagsList.isEmpty()) {
      for (Tag2Post tags2Post : tagsList) {
        tags.add(tags2Post.getTagId().getName());
      }
    }
    return tags;
  }

  public int getTagIdByName(String tagName)
  {
    int tagId = tagRepository.findIdByName(tagName).getId();
    return tagId;
  }

}
