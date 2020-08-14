package main.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import main.model.Post;
import main.model.Tag;
import main.model.Tag2Post;
import main.model.repository.Tag2PostRepository;
import main.model.repository.TagRepository;
import org.springframework.stereotype.Service;

@Service
public class TagService {

  private TagRepository tagRepository;
  private Tag2PostRepository tag2PostRepository;


  public TagService(TagRepository tagRepository, Tag2PostRepository tag2PostRepository) {
    this.tagRepository = tagRepository;
    this.tag2PostRepository = tag2PostRepository;
  }

  public List<Integer> getFoundByTagIdPosts(String tagName) {
    ArrayList<Integer> idPostList = new ArrayList<>();
    Optional<Tag> tagId = tagRepository.findIdByName(tagName);
    if (tagId.isPresent()) {
      List<Tag2Post> tag2Posts = tag2PostRepository.findAllPostIdByTagId(tagId.get());

      if (tag2Posts.isEmpty()) {
        return idPostList;
      }

      for (Tag2Post tag2Post : tag2Posts) {
        idPostList.add(tag2Post.getPostId().getId());
      }
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

  public Optional<Tag> getTagByName(String tagName) {
    Optional<Tag> tagId = tagRepository.findIdByName(tagName);
    return tagId;
  }

  public Tag createNewTag(String tagName) {
    return tagRepository.save(new Tag(tagName));
  }

  public void createTag2PostLink(Post post, Tag tag) {
    tag2PostRepository.save(new Tag2Post(post, tag));
  }

}
