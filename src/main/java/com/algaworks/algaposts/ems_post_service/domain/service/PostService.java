package com.algaworks.algaposts.ems_post_service.domain.service;

import com.algaworks.algaposts.ems_post_service.api.model.PostInput;
import com.algaworks.algaposts.ems_post_service.api.model.PostOutput;
import com.algaworks.algaposts.ems_post_service.api.model.PostSummaryOutput;
import com.algaworks.algaposts.ems_post_service.domain.exception.PostNotFoundException;
import com.algaworks.algaposts.ems_post_service.domain.model.Post;
import com.algaworks.algaposts.ems_post_service.domain.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    public static final String POSTID_NAO_ENCONTRADO = "Não temos post com o id %s.Verifique e tente novamente";

    private final PostRepository postRepository;

    @Transactional
    public PostOutput createPost(PostInput postInput){

        Post post = Post.builder()
                .title(postInput.getTitle())
                .body(postInput.getBody())
                .author(postInput.getAuthor())
                .build();

        Post createdPost = postRepository.saveAndFlush(post);
       return mapToOutput(createdPost);
    }

    public PostOutput getPostById(UUID postId){
            Post post = postRepository.findById(postId)
                    .orElseThrow(()-> new PostNotFoundException(
         String.format(POSTID_NAO_ENCONTRADO, postId)));
            return mapToOutput(post);
    }

    public Page<PostSummaryOutput> getAllPosts(Pageable pageable){

        return postRepository.findAll(pageable)
                .map(this::mapToSummaryOutput);
    }

    private PostOutput mapToOutput(Post post){
        return PostOutput.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .body(post.getBody())
                .author(post.getAuthor())
                .build();
    }

    private PostSummaryOutput mapToSummaryOutput(Post post){
        return PostSummaryOutput.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .summary(post.getBody())
                .author(post.getAuthor())
                .build();
    }


}
