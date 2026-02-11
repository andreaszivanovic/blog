package com.blog.config;

import com.blog.model.AdminUser;
import com.blog.model.Post;
import com.blog.repository.AdminUserRepository;
import com.blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataSeeder implements CommandLineRunner {

    private final PostRepository postRepo;
    private final AdminUserRepository adminRepo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Value("${blog.admin.username:admin}")
    private String defaultUsername;

    @Value("${blog.admin.password:admin}")
    private String defaultPassword;

    public DataSeeder(PostRepository postRepo, AdminUserRepository adminRepo) {
        this.postRepo = postRepo;
        this.adminRepo = adminRepo;
    }

    @Override
    public void run(String... args) {
        if (adminRepo.count() == 0) {
            adminRepo.save(new AdminUser(defaultUsername, encoder.encode(defaultPassword)));
        }

        if (postRepo.count() > 0) return;

        postRepo.save(new Post(
            "Hello World",
            "# Hello World\n\nThis is my **first blog post**. Welcome to my minimal blog.\n\n## What this blog is about\n\nJust a place to write down thoughts and share ideas.\n\n- Simple\n- Clean\n- Minimal",
            LocalDate.of(2025, 1, 15),
            true
        ));

        postRepo.save(new Post(
            "On Simplicity",
            "# On Simplicity\n\nSimplicity is the ultimate sophistication.\n\n> Perfection is achieved, not when there is nothing more to add, but when there is nothing left to take away.\n> — Antoine de Saint-Exupery\n\nThis principle applies to:\n\n1. Software design\n2. Writing\n3. Life in general\n\n```java\n// Simple is better than complex\npublic String greet(String name) {\n    return \"Hello, \" + name;\n}\n```",
            LocalDate.of(2025, 2, 3),
            true
        ));

        postRepo.save(new Post(
            "Markdown is Great",
            "# Markdown is Great\n\nMarkdown lets you write **rich content** with *simple syntax*.\n\n## Features I love\n\n- **Bold** and *italic* text\n- [Links](https://example.com)\n- Code blocks with syntax highlighting\n- Lists and blockquotes\n- Headers for structure\n\n### A code example\n\n```javascript\nconst greeting = 'Hello from Markdown!';\nconsole.log(greeting);\n```\n\nThat's it. No complex editors needed.",
            LocalDate.of(2025, 3, 10),
            true
        ));

        postRepo.save(new Post(
            "Draft: Ideas for Later",
            "# Draft\n\nThis post is not published yet. Only visible in the admin panel.\n\n- Idea 1\n- Idea 2\n- Idea 3",
            LocalDate.of(2025, 3, 20),
            false
        ));
    }
}
