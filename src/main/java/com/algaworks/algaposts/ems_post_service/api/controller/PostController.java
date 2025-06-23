package com.algaworks.algaposts.ems_post_service.api.controller;

import com.algaworks.algaposts.ems_post_service.api.model.PostInput;
import com.algaworks.algaposts.ems_post_service.api.model.PostOutput;
import com.algaworks.algaposts.ems_post_service.api.model.PostSummaryOutput;
import com.algaworks.algaposts.ems_post_service.domain.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostOutput createPost(@Valid @RequestBody PostInput postInput){

        return postService.createPost(postInput);
    }

    @GetMapping("/{postId}")
    public PostOutput getCommentById(@PathVariable UUID postId){
        return postService.getPostById(postId);
    }

    @GetMapping
    public Page<PostSummaryOutput> getAllComments(@PageableDefault Pageable pageable) {
        return postService.getAllPosts(pageable);
    }

}
