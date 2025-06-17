package com.algaworks.algaposts.ems_post_service.domain.service;

import com.algaworks.algaposts.ems_post_service.api.model.PostInput;
import com.algaworks.algaposts.ems_post_service.api.model.PostMessage;
import com.algaworks.algaposts.ems_post_service.api.model.PostOutput;
import com.algaworks.algaposts.ems_post_service.api.model.PostSummaryOutput;
import com.algaworks.algaposts.ems_post_service.domain.exception.PostBodyTooLongException;
import com.algaworks.algaposts.ems_post_service.domain.exception.PostNotFoundException;
import com.algaworks.algaposts.ems_post_service.domain.model.Post;
import com.algaworks.algaposts.ems_post_service.domain.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.algaworks.algaposts.ems_post_service.infrastructure.rabbitmq.RabbitMQConfig.FANOUT_EXCHANGE_NAME;


@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    public static final String POSTID_NAO_ENCONTRADO = "Não temos post com o id %s. Verifique e tente novamente";
    public static final String BODY_MUITO_LONGO = "O campo 'body' pode ter no máximo 255 caracteres.";

    private final PostRepository postRepository;

    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public PostOutput createPost(PostInput postInput){

        if (postInput.getBody().length() > 255) {
            throw new PostBodyTooLongException(BODY_MUITO_LONGO);
        }


        Post post = Post.builder()
                .title(postInput.getTitle())
                .body(postInput.getBody())
                .author(postInput.getAuthor())
                .build();

        Post createdPost = postRepository.saveAndFlush(post);

        log.info("CreatedPostId = " + createdPost.getPostId());

        String exchange = FANOUT_EXCHANGE_NAME;
        String routingKey = "";
        PostMessage payload = PostMessage.builder()
                .id(createdPost.getPostId().toString())
                .body(createdPost.getBody())
                .build();

        log.info(String.valueOf(payload));

        rabbitTemplate.convertAndSend(exchange, routingKey, payload);

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
                .postId(post.getPostId())
                .title(post.getTitle())
                .body(post.getBody())
                .author(post.getAuthor())
                .build();
    }

    private PostSummaryOutput mapToSummaryOutput(Post post){
        return PostSummaryOutput.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .summary(post.getBody())
                .author(post.getAuthor())
                .build();
    }


}
