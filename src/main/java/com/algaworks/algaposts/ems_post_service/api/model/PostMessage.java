package com.algaworks.algaposts.ems_post_service.api.model;

import lombok.*;

import java.util.UUID;

@Builder
@Data
public class PostMessage {

    private String id;
    private String body;

}
