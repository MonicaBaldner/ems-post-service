package com.algaworks.algaposts.ems_post_service.domain.service;

import com.algaworks.algaposts.ems_post_service.api.model.*;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.algaworks.algaposts.ems_post_service.infrastructure.rabbitmq.RabbitMQConfig.FANOUT_EXCHANGE_NAME_POST;


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

        String exchange = FANOUT_EXCHANGE_NAME_POST;
        String routingKey = "";
        PostMessage payload = PostMessage.builder()
                .postId(createdPost.getPostId().toString())
                .body(createdPost.getBody())
                .build();

        log.info(String.valueOf(payload));

        rabbitTemplate.convertAndSend(exchange, routingKey, payload);

        return mapToOutput(createdPost);
    }

    @Transactional
    public void upddatePost(PostResult postResult){
        log.info("Está no updatePost de PostService com o resultado monetizado");

        //para testar dql
       /* postResult.setCalculatedValue(BigDecimal.ZERO);
        if (postResult.getCalculatedValue().equals(BigDecimal.ZERO)) {
            throw new RuntimeException("Simulando erro no serviço");
        }*/
        //fim para testar dql

        UUID postId = UUID.fromString(postResult.getPostId());

        log.info("O UUID de postResult é {}", postId );

        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new PostNotFoundException(
                        String.format(POSTID_NAO_ENCONTRADO, postResult.getPostId())));

    post.setWordCount(postResult.getWordCount());
        log.info("O WordCount de postResult é {}", post.getWordCount() );

        post.setCalculatedValue(postResult.getCalculatedValue());
        log.info("O calculatedValue de postResult é {}", post.getCalculatedValue() );

    postRepository.saveAndFlush(post);

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
                .postId(post.getPostId().toString())
                .title(post.getTitle())
                .body(post.getBody())
                .author(post.getAuthor())
                .wordCount(post.getWordCount())
                .calculatedValue(post.getCalculatedValue())
                .build();
    }

    private PostSummaryOutput mapToSummaryOutput(Post post){
        return PostSummaryOutput.builder()
                .postId(post.getPostId().toString())
                .title(post.getTitle())
                .summary(summarize(post.getBody()))
                .author(post.getAuthor())
                .build();
    }

    private String summarize(String body){
        return Optional.ofNullable(body)
                .map(b -> Arrays.stream(b.split("\\n"))
                        .limit(3)
                        .collect(Collectors.joining("\n")))
                .orElse("");
    }
}
