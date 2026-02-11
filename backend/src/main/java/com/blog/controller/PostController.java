package com.blog.controller;

import com.blog.model.Post;
import com.blog.repository.PostRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostRepository repo;

    public PostController(PostRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Map<String, Object>> getAllPosts(@RequestParam(required = false) Boolean all) {
        List<Post> posts = Boolean.TRUE.equals(all)
                ? repo.findAll().stream().sorted(Comparator.comparing(Post::getDate).reversed()).toList()
                : repo.findByPublishedTrueOrderByDateDesc();

        return posts.stream().map(p -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", p.getId());
            map.put("title", p.getTitle());
            map.put("date", p.getDate().toString());
            map.put("published", p.isPublished());
            String text = p.getText();
            map.put("preview", text.length() > 200 ? text.substring(0, 200) + "..." : text);
            return map;
        }).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Post createPost(@RequestBody Post post) {
        return repo.save(post);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post post) {
        return repo.findById(id).map(existing -> {
            existing.setTitle(post.getTitle());
            existing.setText(post.getText());
            existing.setDate(post.getDate());
            existing.setPublished(post.isPublished());
            return ResponseEntity.ok(repo.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
