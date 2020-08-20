package main.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import main.api.response.tag.ResponseTags;
import main.api.response.tag.TagsWeightResponse;
import main.model.Post;
import main.model.Tag;
import main.model.Tag2Post;
import main.model.repository.PostRepository;
import main.model.repository.Tag2PostRepository;
import main.model.repository.TagRepository;
import org.springframework.stereotype.Service;

@Service
public class TagService {

  private final TagRepository tagRepository;
  private final Tag2PostRepository tag2PostRepository;
  private final PostRepository postRepository;


  public TagService(TagRepository tagRepository, Tag2PostRepository tag2PostRepository,
      PostRepository postRepository) {
    this.tagRepository = tagRepository;
    this.tag2PostRepository = tag2PostRepository;
    this.postRepository = postRepository;
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
    return tagRepository.findIdByName(tagName);
  }

  public Tag createNewTag(String tagName) {
    return tagRepository.save(new Tag(tagName));
  }

  public void createTag2PostLink(Post post, Tag tag) {
    tag2PostRepository.save(new Tag2Post(post, tag));
  }

  public void deleteAllTag2PostLinks(List<Tag2Post> tagList) {
    tag2PostRepository.deleteAll(tagList);
  }

  public ResponseTags getTagsAndWeights(String query) {
    ResponseTags responseTags = new ResponseTags();
    int countVisiblePosts = postRepository.getCountAllVisiblePosts();
    HashSet<TagsWeightResponse> tagWeight = new HashSet<>();
    TagsWeightResponse twr;
    Iterable<Tag> tagIter = tagRepository.findAll();

    for (Tag tag : tagIter) {
      twr = new TagsWeightResponse();
      String nameTag = tag.getName();
      Integer countPublWithTag = tag2PostRepository.countPostIdByTagId(tag);
      if (countPublWithTag == null || countPublWithTag == 0) {
        continue;
      }
      float weight = (float) countPublWithTag / countVisiblePosts;
      twr.setName(nameTag);
      twr.setWeight(weight);
      tagWeight.add(twr);
    }

    try {
      float maxWeight = tagWeight.stream().max(Comparator.comparing(TagsWeightResponse::getWeight))
          .map(TagsWeightResponse::getWeight).orElseThrow(Exception::new);
      tagWeight.forEach(t -> t.setWeight(t.getWeight() / maxWeight));
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (!query.isBlank()) {
      Optional<TagsWeightResponse> tagsWeightResponseOptional = tagWeight.stream()
          .filter(t -> t.getName().equals(query)).findFirst();
      if (tagsWeightResponseOptional.isPresent()) {
        twr = tagsWeightResponseOptional.get();
        tagWeight.clear();
        tagWeight.add(twr);
      }
    }
    responseTags.setTags(tagWeight);
    return responseTags;
  }

}
