package com.algaworks.algaposts.ems_post_service.api.model;

import lombok.*;

@Builder
@Data
public class PostMessage {

    private String postId;
    private String body;

}
