package com.algaworks.algaposts.ems_post_service.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostSummaryOutput {

    private String postId;
    private String title;
    private String summary;
    private String author;
}
