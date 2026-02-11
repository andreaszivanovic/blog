package com.blog.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private boolean published;

    public Post() {}

    public Post(String title, String text, LocalDate date, boolean published) {
        this.title = title;
        this.text = text;
        this.date = date;
        this.published = published;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
}
