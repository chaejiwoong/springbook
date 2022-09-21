package com.example.springbook.web.dto;

import com.example.springbook.domain.posts.Posts;
import lombok.Getter;

import java.time.LocalDateTime;


// 전체 게시글 목록 dto
@Getter
public class PostsListResponseDto {

    private Long id;
    private String title;
    private String author;
    private LocalDateTime modifiedDate;

    public PostsListResponseDto(Posts entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.author = entity.getAuthor();
        this.modifiedDate = entity.getModifiedDate();
    }
}
